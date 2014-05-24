/**
 * Copyright 2014 Tomáš Ligenza
 *
 * This file is part of Firebird Visualization Tool.
 *
 * Firebird Visualization Tool is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * TinyUML is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Firebird Visualization Tool; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.tinyuml.ui.diagram;

import java.awt.Dialog;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Iterator;
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
import javax.swing.text.PlainDocument;
import org.firebirdvisualizationtool.database.firebird.DataType;
import org.firebirdvisualizationtool.database.firebird.ForeignKeyActions;
import org.firebirdvisualizationtool.database.firebird.IndexOrders;
import org.firebirdvisualizationtool.database.firebird.IndexTypes;
import org.firebirdvisualizationtool.database.firebird.TriggerFiringTime;
import org.tinyuml.model.Column;
import org.tinyuml.model.DatabaseModel;
import org.tinyuml.model.ForeignKey;
import org.tinyuml.model.ForeignKeyCol;
import org.tinyuml.model.Index;
import org.tinyuml.model.IndexColumn;
import org.tinyuml.model.Table;
import org.tinyuml.model.Trigger;
import org.tinyuml.model.UmlTable;
import org.tinyuml.ui.EditColDialog;
import org.tinyuml.ui.model.ColTableModel;
import org.tinyuml.ui.model.ForeignKeyColsTableModel;
import org.tinyuml.ui.model.ForeignKeyTableModel;
import org.tinyuml.ui.model.IndexColsTableModel;
import org.tinyuml.ui.model.IndexTableModel;
import org.tinyuml.ui.model.TriggerTableModel;
import org.tinyuml.umldraw.eer.TableElement;
import org.tinyuml.util.ApplicationResources;
import org.tinyuml.util.DocumentFilterFactory;
import org.tinyuml.util.TableAdjuster;

/**
 *
 * @author Tomáš Ligenza
 */
public class EditTableDialog extends javax.swing.JDialog {
	
	private final TableElement objTableElement;
	
	private final DatabaseModel databaseModel;
	private Table tableModel;
	
	private final ColTableModel objColTableModel;
	
	private final IndexTableModel objIndexTableModel			= new IndexTableModel();
	private final IndexColsTableModel objIndexColsTableModel	= new IndexColsTableModel();
	
	private final ForeignKeyTableModel objForeignKeyTableModel	= new ForeignKeyTableModel();
	private final ForeignKeyColsTableModel objForeignKeyColsTableModel = new ForeignKeyColsTableModel();
	private final ForeignKeyColsTableModel objForeignKeyReferenceColsTableModel = new ForeignKeyColsTableModel();
	
	private final TriggerTableModel objTriggerTableModel		= new TriggerTableModel();
	
	private final TableAdjuster adjuster						= new TableAdjuster();
	
	private boolean isOk;
	
	private int lastModelIndexSelect					= -1;
	private int lastModelForeignKeySelect				= -1;
	private int lastModelTriggerSelect					= -1;
	
	private boolean indexColsLoading					= false;
	
	private boolean foreignKeysColsLoading				= false;
	private boolean foreignKeysReferencedColsLoading	= false;
	
	private boolean triggerPropertyLoading				= false;
	
	/**
	 * Creates new form EditTableDialog
	 * @param parent
	 * @param tableElement
	 * @param databaseModel
	 */
	public EditTableDialog(java.awt.Window parent, TableElement tableElement, DatabaseModel databaseModel) {
		super(parent, Dialog.ModalityType.APPLICATION_MODAL);
		
		this.objColTableModel = new ColTableModel();
		
		objTableElement = tableElement;
		this.databaseModel = databaseModel;
		
		initModels();
		initComponents();
		myPostInit();
	}
	
	private void initModels() {
		
		tableModel = databaseModel.getTableByName(((UmlTable) objTableElement.getModelElement()).getTableModel().getName());
		
		if(tableModel == null) {
			
			throw new RuntimeException("Trying to edit non-existing table.");
		}
		
		for (Column col : tableModel.getCols()) {
			objColTableModel.addEntry(col);
			objIndexColsTableModel.addEntry(false, col.getName(), IndexOrders.ASC);
		}
		
		for (Index index : tableModel.getIndexes()) {
			objIndexTableModel.addEntry(index);
		}
		
		for (ForeignKey foreign : tableModel.getForeignKeys()) {
			objForeignKeyTableModel.addEntry(foreign);
		}
		
		for (Trigger trigger : tableModel.getTriggers()) {
			objTriggerTableModel.addEntry(trigger);
		}
	}
	
	private void myPostInit() {
		
		tableName.setText(tableModel.getName());
		
		((PlainDocument) tableName.getDocument()).setDocumentFilter(
			DocumentFilterFactory.buildFilter(DocumentFilterFactory.DocumentFilterType.DATABASE_COLUMN_NAME_LENGTH, false));
		
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
				
				if(databaseModel.containsTableByName(tableName.getText())) {
					
					SwingUtilities.invokeLater(new Runnable() {

						@Override
						public void run() {
							tableName.setText("");
						}
					});
				} else {
					
					tableModel.setName(tableName.getText());
				}
				
				foreignKeyTable.getColumnModel().getColumn(1).setCellEditor(
					new DefaultCellEditor(new JComboBox(databaseModel.getTablesComboBoxModel())));
			}
		});
		
		
		colTable.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(new JComboBox(DataType.getTableColTypes())));
		setColsTableListeners();
		
		indexTable.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(new JComboBox(Index.getTableIndexTypes())));
		setIndexTableListeners();
		
		foreignKeyTable.getColumnModel().getColumn(1).setCellEditor(createFkTableTablesCellEditor());
		foreignKeyTable.getColumnModel().getColumn(2).setCellEditor(
				new DefaultCellEditor(new JComboBox(ForeignKey.getForeignKeyActions())));
		foreignKeyTable.getColumnModel().getColumn(3).setCellEditor(
				new DefaultCellEditor(new JComboBox(ForeignKey.getForeignKeyActions())));
		setForeignKeyTableListeners();
		
		triggerTable.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(new JComboBox(Trigger.getFiringTimeComboBoxModel())));
		setTriggerTableListeners();
	}
	
	private DefaultCellEditor createFkTableTablesCellEditor() {
		
		final DefaultCellEditor editor = new DefaultCellEditor(new JComboBox(databaseModel.getTablesComboBoxModel()));
		
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
	
	public List<Column> getCols() {
		
		List<Column> cols =
			((ColTableModel) colTable.getModel()).getEntries();
		
		List<Column> result = new ArrayList<Column>();
		
		for (Column col : cols) {
			
			if(!col.getName().isEmpty()
				&& col.getDataType().checkValid()) {
				
				result.add(col);
			}
		}
		
		return result;
	}
	
	public List<Index> getIndexes() {
		
		List<Index> indexes =
			((IndexTableModel) indexTable.getModel()).getEntries();
		
		List<Index> result = new ArrayList<Index>();
		
		for (Index index : indexes) {
			
			if(index.checkValid()) {
				
				result.add(index);
			}
		}
		
		return result;
	}
	
	public List<ForeignKey> getForeignKeys() {
		
		List<ForeignKey> foreignKeys =
			((ForeignKeyTableModel) foreignKeyTable.getModel()).getEntries();
		
		List<ForeignKey> result = new ArrayList<ForeignKey>();
		
		for (ForeignKey foreignKey : foreignKeys) {
			
			if(foreignKey.checkValid()) {
				
				result.add(foreignKey);
			}
		}
		
		return result;
	}
	
	public List<Trigger> getTriggers() {
		
		List<Trigger> triggers =
			((TriggerTableModel) triggerTable.getModel()).getEntries();
		
		List<Trigger> result = new ArrayList<Trigger>();
		
		for (Trigger trigger : triggers) {
			
			if(trigger.checkValid()) {
				
				result.add(trigger);
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
		
		List<IndexColumn> lCols = new LinkedList<IndexColumn>();
		
		for (IndexColumn col : objIndexColsTableModel.getEntries()) {
			
			if(col.getChecked()) {
				lCols.add(new IndexColumn(true, col.getName(), col.getoOrder()));
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
		
		for (Column tableCol : colTableModel.getEntries()) {
			
			if(!tableCol.getName().isEmpty()) {
				
				if(indexTableModel.getEntry(index).isColByName(tableCol.getName())) {
					
					IndexColumn col = indexTableModel.getEntry(index).getColByName(tableCol.getName());
					indexColsTableModel.addEntry(col.getChecked(), tableCol.getName(), col.getoOrder());
					
				} else {
					
					indexColsTableModel.addEntry(false, tableCol.getName(), IndexOrders.ASC);
					
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
			
			for(Column tableCol : colTableModel.getEntries()) {
				
				foreignKeyRefColsTableModel.addEntry(false, tableCol.getName());
			}
		
		} else {
		
			Iterator<Table> iterator = databaseModel.getTables().iterator();
			
			while(iterator.hasNext()) {
				
				Table table = iterator.next();
				
				if(table.getName().equals(refTableName)) {

					for(Column tableCol : table.getCols()) {

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
		
		for (Column tableCol : colTableModel.getEntries()) {
			
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
			
			for(Column tableCol : colTableModel.getEntries()) {
				
				if(foreignKeyModel.getEntry(index).isReferencedColByName(tableCol.getName())) {
						
					ForeignKeyCol fkCol = foreignKeyModel.getEntry(index).getReferencedColByName(tableCol.getName());
					foreignKeyRefColsTableModel.addEntry(fkCol.getChecked(), fkCol.getName());

				} else {

					foreignKeyRefColsTableModel.addEntry(false, tableCol.getName());
				}
			}
		} else {
			
			Iterator<Table> iterator = databaseModel.getTables().iterator();
			
			while(iterator.hasNext()) {
				
				Table table = iterator.next();
				
				if(foreignKeyModel.getEntry(index).getReferencedTable().equals(table.getName())) {

					for(Column tableCol : table.getCols()) {

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
        colEditButton = new javax.swing.JButton();
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

        colAddButton.setText(ApplicationResources.getInstance().getString("database.column.add"));
        colAddButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                colAddButtonActionPerformed(evt);
            }
        });

        colRemoveButton.setText(ApplicationResources.getInstance().getString("database.column.remove"));
        colRemoveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                colRemoveButtonActionPerformed(evt);
            }
        });

        colUpButton.setText(ApplicationResources.getInstance().getString("database.column.up"));
        colUpButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                colUpButtonActionPerformed(evt);
            }
        });

        colDownButton.setText(ApplicationResources.getInstance().getString("database.column.down"));
        colDownButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                colDownButtonActionPerformed(evt);
            }
        });

        colEditButton.setText(ApplicationResources.getInstance().getString("database.column.edit"));
        colEditButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                colEditButtonActionPerformed(evt);
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
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(colAddButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(colUpButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(colDownButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(colEditButton, javax.swing.GroupLayout.DEFAULT_SIZE, 152, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(colRemoveButton)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 423, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 11, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(colAddButton)
                    .addComponent(colEditButton)
                    .addComponent(colRemoveButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
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
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(infoPanelLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tableName, javax.swing.GroupLayout.DEFAULT_SIZE, 487, Short.MAX_VALUE)))
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
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab(ApplicationResources.getInstance().getString("dialog.tableEditInfo.tab"), infoPanel);

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        indexTable.setModel(objIndexTableModel);
        jScrollPane2.setViewportView(indexTable);

        indexColsTable.setModel(objIndexColsTableModel);
        jScrollPane3.setViewportView(indexColsTable);

        indexAddButton.setText(ApplicationResources.getInstance().getString("database.index.add"));
        indexAddButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                indexAddButtonActionPerformed(evt);
            }
        });

        indexRemoveButton.setText(ApplicationResources.getInstance().getString("database.index.remove"));
        indexRemoveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                indexRemoveButtonActionPerformed(evt);
            }
        });

        indexDownButton.setText(ApplicationResources.getInstance().getString("database.index.down"));
        indexDownButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                indexDownButtonActionPerformed(evt);
            }
        });

        indexUpButton.setText(ApplicationResources.getInstance().getString("database.index.up"));
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
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING)
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

        jTabbedPane1.addTab(ApplicationResources.getInstance().getString("database.indexes"), indexPanel);

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        foreignKeyTable.setModel(objForeignKeyTableModel);
        jScrollPane4.setViewportView(foreignKeyTable);

        fkAddButton.setText(ApplicationResources.getInstance().getString("database.foreignkey.add"));
        fkAddButton.setActionCommand(ApplicationResources.getInstance().getString("database.foreignkey.add"));
        fkAddButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fkAddButtonActionPerformed(evt);
            }
        });

        fkRemoveButton.setText(ApplicationResources.getInstance().getString("database.foreignkey.remove"));
        fkRemoveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fkRemoveButtonActionPerformed(evt);
            }
        });

        fkUpButton.setText(ApplicationResources.getInstance().getString("database.foreignkey.up"));
        fkUpButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fkUpButtonActionPerformed(evt);
            }
        });

        fkDownButton.setText(ApplicationResources.getInstance().getString("database.foreignkey.down"));
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
                    .addComponent(jScrollPane4)
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
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 236, Short.MAX_VALUE)
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

        jTabbedPane1.addTab(ApplicationResources.getInstance().getString("database.foreignkeys"), foreignPanel);

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        triggerTable.setModel(objTriggerTableModel);
        jScrollPane7.setViewportView(triggerTable);

        triggerCommandTextArea.setColumns(20);
        triggerCommandTextArea.setRows(5);
        jScrollPane8.setViewportView(triggerCommandTextArea);

        triggerAddButton.setText(ApplicationResources.getInstance().getString("database.trigger.add"));
        triggerAddButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                triggerAddButtonActionPerformed(evt);
            }
        });

        triggerRemoveButton.setText(ApplicationResources.getInstance().getString("database.trigger.remove"));
        triggerRemoveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                triggerRemoveButtonActionPerformed(evt);
            }
        });

        triggerDownButton.setText(ApplicationResources.getInstance().getString("database.trigger.down"));
        triggerDownButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                triggerDownButtonActionPerformed(evt);
            }
        });

        triggerUpButton.setText(ApplicationResources.getInstance().getString("database.trigger.up"));
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
                    .addComponent(jScrollPane7)
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 49, Short.MAX_VALUE)
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

        jTabbedPane1.addTab(ApplicationResources.getInstance().getString("database.triggers"), triggerPanel);

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
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 607, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(okButtonTable)
                    .addComponent(cancellButtonTable))
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
        
		EditColDialog dialog = new EditColDialog(this, null, databaseModel);
		
		dialog.setLocationRelativeTo(this);
		dialog.setVisible(true);
		
		if(dialog.isOk()) {

			Column column = dialog.getColumn();
			
			if(column.getDataType().checkValid()) {
				
				objColTableModel.addEntry(column);
				
			}
		
			int row = objColTableModel.getRowCount() - 1;

			colTable.changeSelection(colTable.convertRowIndexToView(row), 0, false, false);
			colTable.requestFocus();
		}
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
        
		objIndexTableModel.addEntry(Index.getPrototype().create("", IndexTypes.INDEX));
		
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
        
		objForeignKeyTableModel.addEntry(ForeignKey.getPrototype().create(
			"fk"
			, tableName.getText()
			, ForeignKeyActions.CASCADE
			, ForeignKeyActions.CASCADE));
		
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
		
		objTriggerTableModel.addEntry(Trigger.getPrototype().create(
			"trigger"
			, TriggerFiringTime.AFTER
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

    private void colEditButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_colEditButtonActionPerformed

		int row = colTable.convertRowIndexToModel(colTable.getSelectedRow());
		
		if(row >= 0) {
			
			EditColDialog dialog = new EditColDialog(this, objColTableModel.getEntries().get(row), databaseModel);
		
			dialog.setLocationRelativeTo(this);
			dialog.setVisible(true);

			if(dialog.isOk()) {

				Column column = dialog.getColumn();

				objColTableModel.getEntries().set(row, column);
				objColTableModel.fireTableDataChanged();

				colTable.requestFocus();
			}
		}
    }//GEN-LAST:event_colEditButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancellButtonTable;
    private javax.swing.JButton colAddButton;
    private javax.swing.JButton colDownButton;
    private javax.swing.JButton colEditButton;
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
