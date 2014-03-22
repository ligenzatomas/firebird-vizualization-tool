/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tinyuml.ui.diagram;

import org.tinyuml.umldraw.eer.TableElement;
import org.tinyuml.util.ApplicationResources;
import java.awt.Dialog;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import org.firebirdvisualizationtool.database.firebird.DatabaseColumnTypes;
import org.tinyuml.model.Domain;
import org.tinyuml.model.UmlTable;
import org.tinyuml.model.UmlTableCol;
import org.tinyuml.model.UmlTableForeignKey;
import org.tinyuml.model.UmlTableIndex;
import org.tinyuml.model.UmlTableTrigger;
import org.tinyuml.ui.model.ColTableModel;
import org.tinyuml.ui.model.ForeignKeyCol;
import org.tinyuml.ui.model.ForeignKeyColsTableModel;
import org.tinyuml.ui.model.ForeignKeyTableModel;
import org.tinyuml.ui.model.IndexCol;
import org.tinyuml.ui.model.IndexColsTableModel;
import org.tinyuml.ui.model.IndexTableModel;
import org.tinyuml.ui.model.TriggerTableModel;
import org.tinyuml.umldraw.eer.TablesContainer;
import org.tinyuml.util.TableAdjuster;

/**
 *
 * @author cml
 */
public class EditTableDialog extends javax.swing.JDialog {
	
	private TableElement objTableElement;
	
	private TablesContainer tables;
	
	private ColTableModel objColTableModel				= new ColTableModel();
	
	private IndexTableModel objIndexTableModel			= new IndexTableModel();
	private IndexColsTableModel objIndexColsTableModel	= new IndexColsTableModel();
	
	private ForeignKeyTableModel objForeignKeyTableModel= new ForeignKeyTableModel();
	private ForeignKeyColsTableModel objForeignKeyColsTableModel = new ForeignKeyColsTableModel();
	private ForeignKeyColsTableModel objForeignKeyReferenceColsTableModel = new ForeignKeyColsTableModel();
	
	private TriggerTableModel objTriggerTableModel		= new TriggerTableModel();
	
	private TableAdjuster adjuster = new TableAdjuster();
	
	private boolean isOk;
	
	private int lastModelIndexSelect = -1;
	private int lastModelForeignKeySelect = -1;
	private int lastModelTriggerSelect = -1;
	
	private boolean indexColsLoading = false;
	
	private boolean foreignKeysColsLoading = false;
	private boolean foreignKeysReferencedColsLoading = false;
	
	private boolean triggerPropertyLoading = false;
	
	/**
	 * Creates new form EditTableDialog
	 */
	public EditTableDialog(java.awt.Window parent, TableElement tableElement, TablesContainer tables) {
		super(parent, Dialog.ModalityType.APPLICATION_MODAL);
		
		objTableElement = tableElement;
		this.tables = tables;
		
		initModels();
		initComponents();
		myPostInit();
	}
	
	public void initModels() {
		
		UmlTable umlTable = (UmlTable) objTableElement.getModelElement();
		
		for (UmlTableCol col : umlTable.getCols()) {
			objColTableModel.addEntry((UmlTableCol) col.clone());
			objIndexColsTableModel.addEntry(false, col.getName(), UmlTableIndex.order.ASC);
		}
		
		for (UmlTableIndex index : umlTable.getIndexes()) {
			objIndexTableModel.addEntry((UmlTableIndex) index.clone());
		}
		
		for (UmlTableForeignKey foreign : umlTable.getForeignKeys()) {
			objForeignKeyTableModel.addEntry((UmlTableForeignKey) foreign.clone());
		}
		
		for (UmlTableTrigger trigger : umlTable.getTriggers()) {
			objTriggerTableModel.addEntry((UmlTableTrigger) trigger.clone());
		}
	}
	
	private void myPostInit() {
		
		UmlTable umlTable = (UmlTable) objTableElement.getModelElement();
		
		tableName.setText(umlTable.getName());
		
		tableName.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void insertUpdate(DocumentEvent e) {
				textChanged();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				textChanged();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				textChanged();
			}
			
			public void textChanged() {
				
				if(tables.existsTableByName(tableName.getText())) {
					
					SwingUtilities.invokeLater(new Runnable() {

						@Override
						public void run() {
							tableName.setText("");
							objTableElement.setLabelText("");
						}
					});
				} else {
					
					objTableElement.setLabelText(tableName.getText());
				}
				
				foreignKeyTable.getColumnModel().getColumn(1).setCellEditor(
					new DefaultCellEditor(new JComboBox(tables.getTablesComboBoxModel())));
			}
		});
		
		
		colTable.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(new JComboBox(Domain.getTableColTypes())));
		setColsTableListeners();
		
		indexTable.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(new JComboBox(UmlTableIndex.getTableIndexTypes())));
		setIndexTableListeners();
		
		foreignKeyTable.getColumnModel().getColumn(1).setCellEditor(createFkTableTablesCellEditor());
		foreignKeyTable.getColumnModel().getColumn(2).setCellEditor(
				new DefaultCellEditor(new JComboBox(UmlTableForeignKey.getForeignKeyActions())));
		foreignKeyTable.getColumnModel().getColumn(3).setCellEditor(
				new DefaultCellEditor(new JComboBox(UmlTableForeignKey.getForeignKeyActions())));
		setForeignKeyTableListeners();
		
		triggerTable.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(new JComboBox(UmlTableTrigger.getFiringTimeComboBoxModel())));
		setTriggerTableListeners();
	}
	
	private DefaultCellEditor createFkTableTablesCellEditor() {
		
		final DefaultCellEditor editor = new DefaultCellEditor(new JComboBox(tables.getTablesComboBoxModel()));
		
		editor.addCellEditorListener(new CellEditorListener() {

			@Override
			public void editingStopped(ChangeEvent e) {
				
				loadForeignKeyReferencedColsTable((String) editor.getCellEditorValue());
			}

			@Override
			public void editingCanceled(ChangeEvent e) {
				throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
			}
		});
		
		return editor;
	}
	
	/**
	 * Returns true if ok was clicked.
	 * @return true if ok was clicked
	 */
	public boolean isOk() { return isOk; }
	
	/**
	* Returns the value in the name text field.
	* @return the name
	*/
	@Override
	public String getName() { return tableName.getText(); }
	
	public List<UmlTableCol> getCols() {
		List<UmlTableCol> cols =
			((ColTableModel) colTable.getModel()).getEntries();
		List<UmlTableCol> result = new ArrayList<UmlTableCol>();
		for (UmlTableCol col : cols) {
			
			if(!col.getName().isEmpty()) {
				
				UmlTableCol tableCol = (UmlTableCol) UmlTableCol.getPrototype().clone();
				
				tableCol.setName(col.getName());
				tableCol.setColType(col.getColType());
				tableCol.setNn(col.getNn());
				
				result.add(tableCol);
			}
		}
		return result;
	}
	
	public List<UmlTableIndex> getIndexes() {
		
		List<UmlTableIndex> indexes =
			((IndexTableModel) indexTable.getModel()).getEntries();
		
		List<UmlTableIndex> result = new ArrayList<UmlTableIndex>();
		
		for (UmlTableIndex index : indexes) {
			
			if(index.checkValid()) {
				
				UmlTableIndex tableIndex = (UmlTableIndex) UmlTableIndex.getPrototype().clone();
				
				tableIndex.setName(index.getName());
				tableIndex.setiIndexType(index.getiIndexType());
				tableIndex.setCols(index.getCols());
				
				result.add(tableIndex);
			}
		}
		
		return result;
	}
	
	public List<UmlTableForeignKey> getForeignKeys() {
		
		List<UmlTableForeignKey> foreignKeys =
			((ForeignKeyTableModel) foreignKeyTable.getModel()).getEntries();
		
		List<UmlTableForeignKey> result = new ArrayList<UmlTableForeignKey>();
		
		for (UmlTableForeignKey foreignKey : foreignKeys) {
			
			if(foreignKey.checkValid()) {
				
				UmlTableForeignKey tableFk = (UmlTableForeignKey) UmlTableForeignKey.getPrototype().clone();
				
				tableFk.setName(foreignKey.getName());
				tableFk.setReferencedTable(foreignKey.getReferencedTable());
				tableFk.setOnDeleteAction(foreignKey.getOnDeleteAction());
				tableFk.setOnUpdateAction(foreignKey.getOnUpdateAction());
				tableFk.setKeyCols(foreignKey.getKeyCols());
				tableFk.setReferencedCols(foreignKey.getReferencedCols());
				
				result.add(tableFk);
			}
		}
		
		return result;
	}
	
	public List<UmlTableTrigger> getTriggers() {
		
		List<UmlTableTrigger> triggers =
			((TriggerTableModel) triggerTable.getModel()).getEntries();
		
		List<UmlTableTrigger> result = new ArrayList<UmlTableTrigger>();
		
		for (UmlTableTrigger trigger : triggers) {
			
			if(trigger.checkValid()) {
				
				UmlTableTrigger tableTrigger = (UmlTableTrigger) UmlTableTrigger.getPrototype().clone();
				
				tableTrigger.setName(trigger.getName());
				tableTrigger.setActive(trigger.isActive());
				tableTrigger.setFiringTime(trigger.getFiringTime());
				tableTrigger.setInsert(trigger.getInsert());
				tableTrigger.setUpdate(trigger.getUpdate());
				tableTrigger.setDelete(trigger.getDelete());
				tableTrigger.setCommand(trigger.getCommand());
				
				result.add(tableTrigger);
			}
		}
		
		return result;
	}
	
	public void setColsTableListeners() {
		
		colTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		colTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		adjuster.adjustColumns(colTable);
		
		colTable.addPropertyChangeListener( new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent e) {
				
				adjuster.adjustColumns(colTable);
			}
		});
		
		colTable.getModel().addTableModelListener( new TableModelListener() {

			@Override
			public void tableChanged(TableModelEvent e) {
				
				saveIndexColsTable(lastModelIndexSelect);
				loadIndexColsTable(lastModelIndexSelect);

				if (e.getType() == TableModelEvent.UPDATE
						&& e.getColumn() > 0) {

					int column = colTable.convertColumnIndexToView(e.getColumn());
					
					adjuster.adjustColumn( column, colTable );

				} else {

					adjuster.adjustColumns(colTable);
				}
			}
		} );
	}
	
	public void setIndexTableListeners() {
		
		indexTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		indexTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		adjuster.adjustColumns(indexTable);
		
		indexTable.addPropertyChangeListener( new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent e) {
				
				adjuster.adjustColumns(indexTable);
			}
		});
		
		indexTable.getModel().addTableModelListener( new TableModelListener() {

			@Override
			public void tableChanged(TableModelEvent e) {
				
				if (e.getType() == TableModelEvent.UPDATE
						&& e.getColumn() > 0) {

					int column = indexTable.convertColumnIndexToView(e.getColumn());
			
					adjuster.adjustColumn( column, indexTable );

				} else {

					adjuster.adjustColumns(indexTable);
				}
			}
		});
		
		indexTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				
				int index = indexTable.convertRowIndexToModel(indexTable.getSelectedRow());
				
				if(e.getValueIsAdjusting() || index < 0)
					return;
				
				saveIndexColsTable(lastModelIndexSelect);
				
				lastModelIndexSelect = index;
				
				loadIndexColsTable(lastModelIndexSelect);
			}
		});
		
		indexColsTable.getModel().addTableModelListener(new TableModelListener() {

			@Override
			public void tableChanged(TableModelEvent e) {
				
				int index = indexTable.convertRowIndexToModel(indexTable.getSelectedRow());
				
				if(index >= 0
					&& indexColsLoading == false) {
					
					saveIndexColsTable(index);
				}
			}
		});
	}
	
	public void setForeignKeyTableListeners() {
		
		foreignKeyTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		foreignKeyTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		adjuster.adjustColumns(foreignKeyTable);
		
		foreignKeyTable.addPropertyChangeListener( new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent e) {
				
				adjuster.adjustColumns(foreignKeyTable);
			}
		});
		
		foreignKeyTable.getModel().addTableModelListener( new TableModelListener() {

			@Override
			public void tableChanged(TableModelEvent e) {
				
				if (e.getType() == TableModelEvent.UPDATE
						&& e.getColumn() > 0) {

					int column = foreignKeyTable.convertColumnIndexToView(e.getColumn());
					
					adjuster.adjustColumn( column, foreignKeyTable );

				} else {

					adjuster.adjustColumns(foreignKeyTable);
				}
			}
		});
		
		foreignKeyTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				
				int index = foreignKeyTable.convertRowIndexToModel(foreignKeyTable.getSelectedRow());
				
				if(e.getValueIsAdjusting() || index < 0)
					return;
				
				saveForeignKeyColsTables(lastModelForeignKeySelect);
				saveForeignKeyReferencedColsTable(lastModelForeignKeySelect);
				
				lastModelForeignKeySelect = index;
				
				loadForeignKeyColsTables(lastModelForeignKeySelect);
			}
		});
		
		fkColsTable.getModel().addTableModelListener(new TableModelListener() {

			@Override
			public void tableChanged(TableModelEvent e) {
				
				int index = foreignKeyTable.convertRowIndexToModel(foreignKeyTable.getSelectedRow());
				
				if(index >= 0
					&& foreignKeysColsLoading == false) {
					
					saveForeignKeyColsTables(index);
				}
			}
		});
		
		fkRefColsTable.getModel().addTableModelListener(new TableModelListener() {

			@Override
			public void tableChanged(TableModelEvent e) {
				
				int index = foreignKeyTable.convertRowIndexToModel(foreignKeyTable.getSelectedRow());
				
				if(index >= 0
					&& foreignKeysReferencedColsLoading == false) {
					
					saveForeignKeyReferencedColsTable(index);
				}
			}
		});
	}
	
	public void setTriggerTableListeners() {
		
		triggerTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		triggerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		adjuster.adjustColumns(triggerTable);
		
		triggerTable.addPropertyChangeListener( new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent e) {
				
				adjuster.adjustColumns(triggerTable);
			}
		});
		
		triggerTable.getModel().addTableModelListener( new TableModelListener() {

			@Override
			public void tableChanged(TableModelEvent e) {
				
				if (e.getType() == TableModelEvent.UPDATE
						&& e.getColumn() > 0) {

					int column = triggerTable.convertColumnIndexToView(e.getColumn());
					
					adjuster.adjustColumn( column, triggerTable );

				} else {

					adjuster.adjustColumns(triggerTable);
				}
			}
		});
		
		triggerTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				
				int index = triggerTable.convertRowIndexToModel(triggerTable.getSelectedRow());
				
				if(e.getValueIsAdjusting() || index < 0)
					return;
				
				saveTriggerProperty(lastModelTriggerSelect);
				
				lastModelTriggerSelect = index;
				
				loadTriggerProperty(lastModelTriggerSelect);
			}
		});
		
		triggerCommandTextArea.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void insertUpdate(DocumentEvent e) {
				textChange();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				textChange();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				textChange();
			}
		});
		
		triggerInsertCheckBox.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				textChange();
			}
		});
		
		triggerUpdateCheckBox.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				textChange();
			}
		});
		
		triggerDeleteCheckBox.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				textChange();
			}
		});
		
	}
	
	private void textChange() {
				
		int index = triggerTable.convertRowIndexToModel(triggerTable.getSelectedRow());

		if(index >= 0
			&& triggerPropertyLoading == false) {

			saveTriggerProperty(index);
		}
	}
	
	private void saveIndexColsTable(int index) {
		
		IndexTableModel indexModel = ((IndexTableModel) indexTable.getModel());
		
		if(indexModel.isEmpty()
			|| index < 0)
			return;
		
		List<IndexCol> lCols = new LinkedList<IndexCol>();
		
		for (IndexCol col : objIndexColsTableModel.getEntries()) {
			
			if(col.getChecked()) {
				lCols.add(new IndexCol(true, col.getName(), col.getoOrder()));
			}
		}
		
		indexModel.getEntry(index).setCols(lCols);
	}
	
	private void loadIndexColsTable(int index) {
		
		indexColsLoading = true;
		
		IndexTableModel indexTableModel = ((IndexTableModel) indexTable.getModel());
		
		if(indexTableModel.isEmpty()
			|| index < 0)
			return;
		
		ColTableModel colTableModel =
			((ColTableModel) colTable.getModel());
		
		IndexColsTableModel indexColsTableModel = ((IndexColsTableModel) indexColsTable.getModel());
		indexColsTableModel.clearEntries();
		
		for (UmlTableCol tableCol : colTableModel.getEntries()) {
			
			if(!tableCol.getName().isEmpty()) {
				
				if(indexTableModel.getEntry(index).isColByName(tableCol.getName())) {
					
					IndexCol col = indexTableModel.getEntry(index).getColByName(tableCol.getName());
					indexColsTableModel.addEntry(col.getChecked(), tableCol.getName(), col.getoOrder());
					
				} else {
					
					indexColsTableModel.addEntry(false, tableCol.getName(), UmlTableIndex.order.ASC);
					
				}
			}
		}
		
		indexColsLoading = false;
	}
	
	private void saveForeignKeyColsTables(int index) {
		
		ForeignKeyTableModel foreignKeyModel = ((ForeignKeyTableModel) foreignKeyTable.getModel());
		
		if(foreignKeyModel.isEmpty()
			|| index < 0)
			return;
		
		List<ForeignKeyCol> lCols = new LinkedList<ForeignKeyCol>();
		
		for (ForeignKeyCol col : objForeignKeyColsTableModel.getEntries()) {
			
			if(col.getChecked()) {
				
				lCols.add(new ForeignKeyCol(true, col.getName()));
			}
		}
		
		foreignKeyModel.getEntry(index).setKeyCols(lCols);
	}
	
	private void saveForeignKeyReferencedColsTable(int index) {
		
		ForeignKeyTableModel foreignKeyModel = ((ForeignKeyTableModel) foreignKeyTable.getModel());
		
		if(foreignKeyModel.isEmpty()
			|| index < 0)
			return;
		
		List<ForeignKeyCol> referencedCols = new LinkedList<ForeignKeyCol>();
		
		for (ForeignKeyCol col : objForeignKeyReferenceColsTableModel.getEntries()) {
			
			if(col.getChecked()) {
				
				referencedCols.add(new ForeignKeyCol(true, col.getName()));
			}
		}
		
		foreignKeyModel.getEntry(index).setReferencedCols(referencedCols);
	}
	
	private void loadForeignKeyReferencedColsTable(String refTableName) {
		
		foreignKeysReferencedColsLoading = true;
		
		ForeignKeyColsTableModel foreignKeyRefColsTableModel = ((ForeignKeyColsTableModel) fkRefColsTable.getModel());
		foreignKeyRefColsTableModel.clearEntries();
		
		if(refTableName.equals(tableName.getText())) {
			
			ColTableModel colTableModel =
				((ColTableModel) colTable.getModel());
			
			for(UmlTableCol tableCol : colTableModel.getEntries()) {
				
				foreignKeyRefColsTableModel.addEntry(false, tableCol.getName());
			}
		
		} else {
		
			for (UmlTable table : tables.getTables()) {

				if(table.getName().equals(refTableName)) {

					for(UmlTableCol tableCol : table.getCols()) {

						foreignKeyRefColsTableModel.addEntry(false, tableCol.getName());
					}
				}
			}
		}
		
		foreignKeysReferencedColsLoading = false;
	}
	
	private void loadForeignKeyColsTables(int index) {
		
		foreignKeysColsLoading = true;
		foreignKeysReferencedColsLoading = true;
		
		ForeignKeyTableModel foreignKeyModel = ((ForeignKeyTableModel) foreignKeyTable.getModel());
		
		if(foreignKeyModel.isEmpty()
			|| index < 0)
			return;
		
		ColTableModel colTableModel =
			((ColTableModel) colTable.getModel());
		
		ForeignKeyColsTableModel foreignKeyColsTableModel = ((ForeignKeyColsTableModel) fkColsTable.getModel());
		foreignKeyColsTableModel.clearEntries();
		
		for (UmlTableCol tableCol : colTableModel.getEntries()) {
			
			if(!tableCol.getName().isEmpty()) {
				
				if(foreignKeyModel.getEntry(index).isKeyColByName(tableCol.getName())) {
					
					ForeignKeyCol col = foreignKeyModel.getEntry(index).getKeyColByName(tableCol.getName());
					foreignKeyColsTableModel.addEntry(col.getChecked(), tableCol.getName());
					
				} else {
					
					foreignKeyColsTableModel.addEntry(false, tableCol.getName());
					
				}
			}
		}
		
		ForeignKeyColsTableModel foreignKeyRefColsTableModel = ((ForeignKeyColsTableModel) fkRefColsTable.getModel());
		foreignKeyRefColsTableModel.clearEntries();
		
		if(foreignKeyModel.getEntry(index).getReferencedTable().equals(tableName.getText())) {
			
			for(UmlTableCol tableCol : colTableModel.getEntries()) {
				
				if(foreignKeyModel.getEntry(index).isReferencedColByName(tableCol.getName())) {
						
					ForeignKeyCol fkCol = foreignKeyModel.getEntry(index).getReferencedColByName(tableCol.getName());
					foreignKeyRefColsTableModel.addEntry(fkCol.getChecked(), fkCol.getName());

				} else {

					foreignKeyRefColsTableModel.addEntry(false, tableCol.getName());
				}
			}
		} else {
			
			for (UmlTable table : tables.getTables()) {

				if(foreignKeyModel.getEntry(index).getReferencedTable().equals(table.getName())) {

					for(UmlTableCol tableCol : table.getCols()) {

						if(foreignKeyModel.getEntry(index).isReferencedColByName(tableCol.getName())) {

							ForeignKeyCol fkCol = foreignKeyModel.getEntry(index).getReferencedColByName(tableCol.getName());
							foreignKeyRefColsTableModel.addEntry(fkCol.getChecked(), fkCol.getName());

						} else {

							foreignKeyRefColsTableModel.addEntry(false, tableCol.getName());
						}
					}
				}
			}
		}
		
		foreignKeysColsLoading = false;
		foreignKeysReferencedColsLoading = false;
	}
	
	private void saveTriggerProperty(int index) {
		
		TriggerTableModel triggerModel = ((TriggerTableModel) triggerTable.getModel());
		
		if(triggerModel.isEmpty()
			|| index < 0)
			return;
		
		triggerModel.getEntry(index).setCommand(triggerCommandTextArea.getText());
		triggerModel.getEntry(index).setInsert(triggerInsertCheckBox.isSelected());
		triggerModel.getEntry(index).setUpdate(triggerUpdateCheckBox.isSelected());
		triggerModel.getEntry(index).setDelete(triggerDeleteCheckBox.isSelected());
	}
	
	private void loadTriggerProperty(int index) {
		
		triggerPropertyLoading = true;
		
		TriggerTableModel triggerModel = ((TriggerTableModel) triggerTable.getModel());
		
		if(triggerModel.isEmpty()
			|| index < 0)
			return;
		
		triggerCommandTextArea.setText(triggerModel.getEntry(index).getCommand());
		triggerInsertCheckBox.setSelected(triggerModel.getEntry(index).getInsert());
		triggerUpdateCheckBox.setSelected(triggerModel.getEntry(index).getUpdate());
		triggerDeleteCheckBox.setSelected(triggerModel.getEntry(index).getDelete());
		
		triggerPropertyLoading = false;
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        infoPanel = new javax.swing.JPanel();
        tableName = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        colTable = new javax.swing.JTable();
        colAddButton = new javax.swing.JButton();
        colRemoveButton = new javax.swing.JButton();
        colUpButton = new javax.swing.JButton();
        colDownButton = new javax.swing.JButton();
        indexPanel = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        indexTable = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        indexColsTable = new javax.swing.JTable();
        indexAddButton = new javax.swing.JButton();
        indexRemoveButton = new javax.swing.JButton();
        indexDownButton = new javax.swing.JButton();
        indexUpButton = new javax.swing.JButton();
        foreignPanel = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        foreignKeyTable = new javax.swing.JTable();
        fkAddButton = new javax.swing.JButton();
        fkRemoveButton = new javax.swing.JButton();
        fkUpButton = new javax.swing.JButton();
        fkDownButton = new javax.swing.JButton();
        jScrollPane5 = new javax.swing.JScrollPane();
        fkColsTable = new javax.swing.JTable();
        jScrollPane6 = new javax.swing.JScrollPane();
        fkRefColsTable = new javax.swing.JTable();
        triggerPanel = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane7 = new javax.swing.JScrollPane();
        triggerTable = new javax.swing.JTable();
        jScrollPane8 = new javax.swing.JScrollPane();
        triggerCommandTextArea = new javax.swing.JTextArea();
        triggerAddButton = new javax.swing.JButton();
        triggerRemoveButton = new javax.swing.JButton();
        triggerDownButton = new javax.swing.JButton();
        triggerUpButton = new javax.swing.JButton();
        triggerInsertCheckBox = new javax.swing.JCheckBox();
        triggerUpdateCheckBox = new javax.swing.JCheckBox();
        triggerDeleteCheckBox = new javax.swing.JCheckBox();
        okButtonTable = new javax.swing.JButton();
        cancellButtonTable = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(ApplicationResources.getInstance().getString("dialog.tableEdit.title"));

        jTabbedPane1.setName(ApplicationResources.getInstance().getString("dialog.tableEditInfo.tab"));

        infoPanel.setPreferredSize(new java.awt.Dimension(500, 500));

        jLabel1.setText(ApplicationResources.getInstance().getString("dialog.tableEditInfo.table"));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jScrollPane1.setBorder(null);

        colTable.setModel(objColTableModel);
        jScrollPane1.setViewportView(colTable);

        colAddButton.setText("Přidat sloupec");
        colAddButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                colAddButtonActionPerformed(evt);
            }
        });

        colRemoveButton.setText("Odebrat sloupec");
        colRemoveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                colRemoveButtonActionPerformed(evt);
            }
        });

        colUpButton.setText("Posunout výše");
        colUpButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                colUpButtonActionPerformed(evt);
            }
        });

        colDownButton.setText("Posunout níže");
        colDownButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                colDownButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(colAddButton, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(colRemoveButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 28, Short.MAX_VALUE)
                        .addComponent(colUpButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(colDownButton)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 421, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(colAddButton)
                    .addComponent(colRemoveButton)
                    .addComponent(colUpButton)
                    .addComponent(colDownButton))
                .addContainerGap())
        );

        javax.swing.GroupLayout infoPanelLayout = new javax.swing.GroupLayout(infoPanel);
        infoPanel.setLayout(infoPanelLayout);
        infoPanelLayout.setHorizontalGroup(
            infoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(infoPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(infoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(infoPanelLayout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(infoPanelLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tableName)))
                .addContainerGap())
        );
        infoPanelLayout.setVerticalGroup(
            infoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(infoPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(infoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(tableName))
                .addGap(18, 18, 18)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(16, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab(ApplicationResources.getInstance().getString("dialog.tableEditInfo.tab"), infoPanel);

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        indexTable.setModel(objIndexTableModel);
        jScrollPane2.setViewportView(indexTable);

        indexColsTable.setModel(objIndexColsTableModel);
        jScrollPane3.setViewportView(indexColsTable);

        indexAddButton.setText("Přidat index");
        indexAddButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                indexAddButtonActionPerformed(evt);
            }
        });

        indexRemoveButton.setText("Odebrat index");
        indexRemoveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                indexRemoveButtonActionPerformed(evt);
            }
        });

        indexDownButton.setText("Posunout níže");
        indexDownButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                indexDownButtonActionPerformed(evt);
            }
        });

        indexUpButton.setText("Posunout výše");
        indexUpButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                indexUpButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 482, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(indexAddButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(indexRemoveButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(indexUpButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(indexDownButton)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 295, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(indexAddButton)
                    .addComponent(indexRemoveButton)
                    .addComponent(indexDownButton)
                    .addComponent(indexUpButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout indexPanelLayout = new javax.swing.GroupLayout(indexPanel);
        indexPanel.setLayout(indexPanelLayout);
        indexPanelLayout.setHorizontalGroup(
            indexPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(indexPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        indexPanelLayout.setVerticalGroup(
            indexPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(indexPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Indexy", indexPanel);

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        foreignKeyTable.setModel(objForeignKeyTableModel);
        jScrollPane4.setViewportView(foreignKeyTable);

        fkAddButton.setText("Přidat index");
        fkAddButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fkAddButtonActionPerformed(evt);
            }
        });

        fkRemoveButton.setText("Odebrat index");
        fkRemoveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fkRemoveButtonActionPerformed(evt);
            }
        });

        fkUpButton.setText("Posunout výše");
        fkUpButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fkUpButtonActionPerformed(evt);
            }
        });

        fkDownButton.setText("Posunout níže");
        fkDownButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fkDownButtonActionPerformed(evt);
            }
        });

        fkColsTable.setModel(objForeignKeyColsTableModel);
        jScrollPane5.setViewportView(fkColsTable);

        fkRefColsTable.setModel(objForeignKeyReferenceColsTableModel);
        jScrollPane6.setViewportView(fkRefColsTable);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 482, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(fkAddButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(fkRemoveButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(fkUpButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(fkDownButton)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 251, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 211, Short.MAX_VALUE)
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(fkAddButton)
                    .addComponent(fkRemoveButton)
                    .addComponent(fkUpButton)
                    .addComponent(fkDownButton))
                .addContainerGap())
        );

        javax.swing.GroupLayout foreignPanelLayout = new javax.swing.GroupLayout(foreignPanel);
        foreignPanel.setLayout(foreignPanelLayout);
        foreignPanelLayout.setHorizontalGroup(
            foreignPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(foreignPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        foreignPanelLayout.setVerticalGroup(
            foreignPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(foreignPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Cizí klíče", foreignPanel);

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        triggerTable.setModel(objTriggerTableModel);
        jScrollPane7.setViewportView(triggerTable);

        triggerCommandTextArea.setColumns(20);
        triggerCommandTextArea.setRows(5);
        jScrollPane8.setViewportView(triggerCommandTextArea);

        triggerAddButton.setText("Přidat index");
        triggerAddButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                triggerAddButtonActionPerformed(evt);
            }
        });

        triggerRemoveButton.setText("Odebrat index");
        triggerRemoveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                triggerRemoveButtonActionPerformed(evt);
            }
        });

        triggerDownButton.setText("Posunout níže");
        triggerDownButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                triggerDownButtonActionPerformed(evt);
            }
        });

        triggerUpButton.setText("Posunout výše");
        triggerUpButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                triggerUpButtonActionPerformed(evt);
            }
        });

        triggerInsertCheckBox.setText("after INSERT");

        triggerUpdateCheckBox.setText("after UPDATE");

        triggerDeleteCheckBox.setText("after DELETE");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 482, Short.MAX_VALUE)
                    .addComponent(jScrollPane8)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(triggerAddButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(triggerRemoveButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(triggerUpButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(triggerDownButton))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(triggerInsertCheckBox)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(triggerUpdateCheckBox)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(triggerDeleteCheckBox)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 212, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(triggerInsertCheckBox)
                    .addComponent(triggerUpdateCheckBox)
                    .addComponent(triggerDeleteCheckBox))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 19, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(triggerAddButton)
                    .addComponent(triggerRemoveButton)
                    .addComponent(triggerDownButton)
                    .addComponent(triggerUpButton))
                .addContainerGap())
        );

        javax.swing.GroupLayout triggerPanelLayout = new javax.swing.GroupLayout(triggerPanel);
        triggerPanel.setLayout(triggerPanelLayout);
        triggerPanelLayout.setHorizontalGroup(
            triggerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(triggerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        triggerPanelLayout.setVerticalGroup(
            triggerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(triggerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Triggery", triggerPanel);

        okButtonTable.setText(ApplicationResources.getInstance().getString("stdcaption.ok"));
        okButtonTable.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        okButtonTable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonTableActionPerformed(evt);
            }
        });

        cancellButtonTable.setText(ApplicationResources.getInstance().getString("stdcaption.cancel"));
        cancellButtonTable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancellButtonTableActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(okButtonTable)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cancellButtonTable)
                .addGap(18, 18, 18))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jTabbedPane1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancellButtonTable)
                    .addComponent(okButtonTable))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void okButtonTableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonTableActionPerformed
        isOk = true;
		dispose();
    }//GEN-LAST:event_okButtonTableActionPerformed

    private void cancellButtonTableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancellButtonTableActionPerformed
        dispose();
    }//GEN-LAST:event_cancellButtonTableActionPerformed

    private void colDownButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_colDownButtonActionPerformed
        
		ColTableModel tableModel = (ColTableModel) colTable.getModel();
		
		int row = colTable.convertRowIndexToModel(colTable.getSelectedRow());
		
		if(row >= 0 && row < tableModel.getRowCount() - 1) {
			
			tableModel.moveDownEntry(row++);
			
			colTable.addRowSelectionInterval(colTable.convertRowIndexToView(row), colTable.convertRowIndexToView(row));
		}
    }//GEN-LAST:event_colDownButtonActionPerformed

    private void colAddButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_colAddButtonActionPerformed
        
		objColTableModel.addEntry(UmlTableCol.getPrototype().create("", DatabaseColumnTypes.INTEGER));
		
		int row = objColTableModel.getRowCount() - 1;
		
		saveIndexColsTable(indexTable.convertRowIndexToModel(lastModelIndexSelect));
		loadIndexColsTable(indexTable.convertRowIndexToModel(lastModelIndexSelect));
		
		colTable.changeSelection(colTable.convertRowIndexToView(row), 0, false, false);
		colTable.requestFocus();
    }//GEN-LAST:event_colAddButtonActionPerformed

    private void colRemoveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_colRemoveButtonActionPerformed
        
		ColTableModel tableModel = (ColTableModel) colTable.getModel();
		
		int row = colTable.convertRowIndexToModel(colTable.getSelectedRow());
		
		if(row >= 0) {
			
			tableModel.removeEntryAt(row--);
			
			saveIndexColsTable(indexTable.convertRowIndexToModel(lastModelIndexSelect));
			loadIndexColsTable(indexTable.convertRowIndexToModel(lastModelIndexSelect));
			
			if(row >= 0)
				colTable.changeSelection(colTable.convertRowIndexToView(row), 0, false, false);
		}
    }//GEN-LAST:event_colRemoveButtonActionPerformed

    private void colUpButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_colUpButtonActionPerformed
        
		ColTableModel tableModel = (ColTableModel) colTable.getModel();
		
		int row = colTable.convertRowIndexToModel(colTable.getSelectedRow());
		
		if(row > 0) {
			
			tableModel.moveUpEntry(row--);
			
			colTable.addRowSelectionInterval(colTable.convertRowIndexToView(row), colTable.convertRowIndexToView(row));
		}
    }//GEN-LAST:event_colUpButtonActionPerformed

    private void indexAddButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_indexAddButtonActionPerformed
        
		objIndexTableModel.addEntry(UmlTableIndex.getPrototype().create("", UmlTableIndex.indexType.INDEX));
		
		int row = objIndexTableModel.getRowCount() - 1;
		
		indexTable.changeSelection(indexTable.convertRowIndexToView(row), 0, false, false);
		indexTable.requestFocus();
    }//GEN-LAST:event_indexAddButtonActionPerformed

    private void indexRemoveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_indexRemoveButtonActionPerformed
        
		IndexTableModel tableModel = (IndexTableModel) indexTable.getModel();
		
		int row = indexTable.convertRowIndexToModel(indexTable.getSelectedRow());
		
		if(row >= 0) {
			
			tableModel.removeEntryAt(row--);
			
			if(row >= 0)
				indexTable.changeSelection(indexTable.convertRowIndexToView(row), 0, false, false);
		}
    }//GEN-LAST:event_indexRemoveButtonActionPerformed

    private void indexUpButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_indexUpButtonActionPerformed
        
		IndexTableModel tableModel = (IndexTableModel) indexTable.getModel();
		
		int row = indexTable.convertRowIndexToModel(indexTable.getSelectedRow());
		
		if(row > 0) {
			
			saveIndexColsTable(lastModelIndexSelect);
			
			tableModel.moveUpEntry(row--);
			
			lastModelIndexSelect = row;
			loadIndexColsTable(lastModelIndexSelect);
			
			indexTable.addRowSelectionInterval(indexTable.convertRowIndexToView(row), indexTable.convertRowIndexToView(row));
		}
    }//GEN-LAST:event_indexUpButtonActionPerformed

    private void indexDownButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_indexDownButtonActionPerformed
        
		IndexTableModel tableModel = (IndexTableModel) indexTable.getModel();
		
		int row = indexTable.convertRowIndexToModel(indexTable.getSelectedRow());
		
		if(row >= 0 && row < tableModel.getRowCount() - 1) {
			
			saveIndexColsTable(lastModelIndexSelect);
			
			tableModel.moveDownEntry(row++);
			
			lastModelIndexSelect = row;
			loadIndexColsTable(lastModelIndexSelect);
			
			indexTable.addRowSelectionInterval(indexTable.convertRowIndexToView(row), indexTable.convertRowIndexToView(row));
		}
    }//GEN-LAST:event_indexDownButtonActionPerformed

    private void fkAddButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fkAddButtonActionPerformed
        
		objForeignKeyTableModel.addEntry(UmlTableForeignKey.getPrototype().create(
			"fk"
			, tableName.getText()
			, UmlTableForeignKey.action.CASCADE
			, UmlTableForeignKey.action.CASCADE));
		
		int row = objForeignKeyTableModel.getRowCount() - 1;
		
		foreignKeyTable.changeSelection(foreignKeyTable.convertRowIndexToView(row), 0, false, false);
		foreignKeyTable.requestFocus();
    }//GEN-LAST:event_fkAddButtonActionPerformed

    private void fkRemoveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fkRemoveButtonActionPerformed
        
		ForeignKeyTableModel tableModel = (ForeignKeyTableModel) foreignKeyTable.getModel();
		
		int row = foreignKeyTable.convertRowIndexToModel(foreignKeyTable.getSelectedRow());
		
		if(row >= 0) {
			
			tableModel.removeEntryAt(row--);
			
			if(row >= 0) {
				
				foreignKeyTable.changeSelection(foreignKeyTable.convertRowIndexToView(row), 0, false, false);
				
				loadForeignKeyColsTables(row);
			}
		}
    }//GEN-LAST:event_fkRemoveButtonActionPerformed

    private void fkUpButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fkUpButtonActionPerformed
        
		ForeignKeyTableModel tableModel = (ForeignKeyTableModel) foreignKeyTable.getModel();
		
		int row = foreignKeyTable.convertRowIndexToModel(foreignKeyTable.getSelectedRow());
		
		if(row > 0) {

			saveForeignKeyColsTables(lastModelForeignKeySelect);
			
			tableModel.moveUpEntry(row--);
			
			lastModelForeignKeySelect = row;
			loadForeignKeyColsTables(lastModelForeignKeySelect);
			
			foreignKeyTable.addRowSelectionInterval(foreignKeyTable.convertRowIndexToView(row), foreignKeyTable.convertRowIndexToView(row));
		}
    }//GEN-LAST:event_fkUpButtonActionPerformed

    private void fkDownButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fkDownButtonActionPerformed
        
		ForeignKeyTableModel tableModel = (ForeignKeyTableModel) foreignKeyTable.getModel();
		
		int row = foreignKeyTable.convertRowIndexToModel(foreignKeyTable.getSelectedRow());
		
		if(row >= 0 && row < tableModel.getRowCount() - 1) {

			saveForeignKeyColsTables(lastModelForeignKeySelect);
			
			tableModel.moveDownEntry(row++);
			
			lastModelForeignKeySelect = row;
			loadForeignKeyColsTables(lastModelForeignKeySelect);
			
			foreignKeyTable.addRowSelectionInterval(foreignKeyTable.convertRowIndexToView(row), foreignKeyTable.convertRowIndexToView(row));
		}
    }//GEN-LAST:event_fkDownButtonActionPerformed

    private void triggerAddButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_triggerAddButtonActionPerformed
		
		objTriggerTableModel.addEntry(UmlTableTrigger.getPrototype().create(
			"trigger"
			, UmlTableTrigger.firing.AFTER
			, true
			, false
			, false
			, false
			, ""));
		
		int row = objTriggerTableModel.getRowCount() - 1;
		
		triggerTable.changeSelection(triggerTable.convertRowIndexToView(row), 0, false, false);
		triggerTable.requestFocus();
    }//GEN-LAST:event_triggerAddButtonActionPerformed

    private void triggerRemoveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_triggerRemoveButtonActionPerformed

		TriggerTableModel tableModel = (TriggerTableModel) triggerTable.getModel();
		
		int row = triggerTable.convertRowIndexToModel(triggerTable.getSelectedRow());
		
		if(row >= 0) {
			
			tableModel.removeEntryAt(row--);
			
			if(row >= 0) {
				
				triggerTable.changeSelection(triggerTable.convertRowIndexToView(row), 0, false, false);
				
				loadTriggerProperty(row);
			}
		}
    }//GEN-LAST:event_triggerRemoveButtonActionPerformed

    private void triggerUpButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_triggerUpButtonActionPerformed

		TriggerTableModel tableModel = (TriggerTableModel) triggerTable.getModel();
		
		int row = triggerTable.convertRowIndexToModel(triggerTable.getSelectedRow());
		
		if(row > 0) {

			saveTriggerProperty(lastModelTriggerSelect);
			
			tableModel.moveUpEntry(row--);
			
			lastModelTriggerSelect = row;
			loadTriggerProperty(lastModelTriggerSelect);
			
			triggerTable.addRowSelectionInterval(triggerTable.convertRowIndexToView(row), triggerTable.convertRowIndexToView(row));
		}
    }//GEN-LAST:event_triggerUpButtonActionPerformed

    private void triggerDownButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_triggerDownButtonActionPerformed

		TriggerTableModel tableModel = (TriggerTableModel) triggerTable.getModel();
		
		int row = triggerTable.convertRowIndexToModel(triggerTable.getSelectedRow());
		
		if(row >= 0 && row < tableModel.getRowCount() - 1) {

			saveTriggerProperty(lastModelTriggerSelect);
			
			tableModel.moveDownEntry(row++);
			
			lastModelTriggerSelect = row;
			loadTriggerProperty(lastModelTriggerSelect);
			
			triggerTable.addRowSelectionInterval(triggerTable.convertRowIndexToView(row), triggerTable.convertRowIndexToView(row));
		}
    }//GEN-LAST:event_triggerDownButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancellButtonTable;
    private javax.swing.JButton colAddButton;
    private javax.swing.JButton colDownButton;
    private javax.swing.JButton colRemoveButton;
    private javax.swing.JTable colTable;
    private javax.swing.JButton colUpButton;
    private javax.swing.JButton fkAddButton;
    private javax.swing.JTable fkColsTable;
    private javax.swing.JButton fkDownButton;
    private javax.swing.JTable fkRefColsTable;
    private javax.swing.JButton fkRemoveButton;
    private javax.swing.JButton fkUpButton;
    private javax.swing.JTable foreignKeyTable;
    private javax.swing.JPanel foreignPanel;
    private javax.swing.JButton indexAddButton;
    private javax.swing.JTable indexColsTable;
    private javax.swing.JButton indexDownButton;
    private javax.swing.JPanel indexPanel;
    private javax.swing.JButton indexRemoveButton;
    private javax.swing.JTable indexTable;
    private javax.swing.JButton indexUpButton;
    private javax.swing.JPanel infoPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JButton okButtonTable;
    private javax.swing.JTextField tableName;
    private javax.swing.JButton triggerAddButton;
    private javax.swing.JTextArea triggerCommandTextArea;
    private javax.swing.JCheckBox triggerDeleteCheckBox;
    private javax.swing.JButton triggerDownButton;
    private javax.swing.JCheckBox triggerInsertCheckBox;
    private javax.swing.JPanel triggerPanel;
    private javax.swing.JButton triggerRemoveButton;
    private javax.swing.JTable triggerTable;
    private javax.swing.JButton triggerUpButton;
    private javax.swing.JCheckBox triggerUpdateCheckBox;
    // End of variables declaration//GEN-END:variables
}
