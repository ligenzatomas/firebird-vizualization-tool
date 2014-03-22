/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tinyuml.ui.diagram;

import org.tinyuml.draw.DiagramElement;
import org.tinyuml.draw.RectilinearConnection;
import org.tinyuml.draw.SimpleConnection;
import org.tinyuml.ui.diagram.commands.ConvertConnectionTypeCommand;
import org.tinyuml.ui.diagram.commands.SetConnectionNavigabilityCommand;
import org.tinyuml.model.ElementType;
import org.tinyuml.model.Relation;
import org.tinyuml.model.RelationEndType;
import org.tinyuml.model.RelationType;
import org.tinyuml.model.UmlTable;
import org.tinyuml.umldraw.shared.GeneralDiagram;
import org.tinyuml.umldraw.shared.UmlConnection;
import org.tinyuml.umldraw.structure.Association;
import org.tinyuml.umldraw.eer.TableElement;
import org.tinyuml.util.MethodCall;
import java.awt.Component;
import java.awt.Window;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.tinyuml.model.ElementNameChangeListener;
import org.tinyuml.model.NameChangeListener;
import org.tinyuml.model.NamedElement;
import org.tinyuml.model.UmlProcedure;
import org.tinyuml.model.UmlRelation;
import org.tinyuml.model.UmlTableCol;
import org.tinyuml.model.UmlTableForeignKey;
import org.tinyuml.model.UmlTableIndex;
import org.tinyuml.model.UmlView;
import org.tinyuml.ui.model.IndexCol;
import org.tinyuml.umldraw.eer.ProcedureElement;
import org.tinyuml.umldraw.eer.Relationship;
import org.tinyuml.umldraw.eer.TablesContainer;
import org.tinyuml.umldraw.eer.ViewElement;

/**
 *
 * @author cml
 */
public class EERDiagramEditor extends DiagramEditor {
	
	private static Map<String, MethodCall> selectorMap = 
		new HashMap<String, MethodCall>();
	
	private TablesContainer tables = new TablesContainer();
	
	static {
		initSelectorMap();
	}
	
	public EERDiagramEditor(Component aWindow, GeneralDiagram aDiagram) {
		super(aWindow, aDiagram);
	}
	
	private static void initSelectorMap() {
		
		try {
			selectorMap.put("NEW_LAYER", new MethodCall(
				EERDiagramEditor.class.getMethod("setCreationMode", ElementType.class)
				, ElementType.LAYER));
			selectorMap.put("NEW_NOTE", new MethodCall(
				EERDiagramEditor.class.getMethod("setCreationMode", ElementType.class)
				, ElementType.NOTE));
			selectorMap.put("NEW_TABLE", new MethodCall(
				EERDiagramEditor.class.getMethod("setCreationMode", ElementType.class)
				, ElementType.TABLE));
			selectorMap.put("NEW_VIEW", new MethodCall(
				EERDiagramEditor.class.getMethod("setCreationMode", ElementType.class)
				, ElementType.VIEW));
			selectorMap.put("NEW_PROCEDURE", new MethodCall(
				EERDiagramEditor.class.getMethod("setCreationMode", ElementType.class)
				, ElementType.PROCEDURE));
			
			selectorMap.put("ONE_ONE_NON_IDE", new MethodCall(
				EERDiagramEditor.class.getMethod("setCreateConnectionMode", RelationType.class)
				, RelationType.ONE_ONE_NON_IDE));
			selectorMap.put("ONE_N_NON_IDE", new MethodCall(
				EERDiagramEditor.class.getMethod("setCreateConnectionMode", RelationType.class)
				, RelationType.ONE_N_NON_IDE));
			selectorMap.put("ONE_ONE_IDE", new MethodCall(
				EERDiagramEditor.class.getMethod("setCreateConnectionMode", RelationType.class)
				, RelationType.ONE_ONE_IDE));
			selectorMap.put("ONE_N_IDE", new MethodCall(
				EERDiagramEditor.class.getMethod("setCreateConnectionMode", RelationType.class)
				, RelationType.ONE_N_IDE));
			selectorMap.put("N_N_IDE", new MethodCall(
				EERDiagramEditor.class.getMethod("setCreateConnectionMode", RelationType.class)
				, RelationType.N_N_IDE));
			
		} catch (NoSuchMethodException ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	* {@inheritDoc}
	*/
	public void editProperties(DiagramElement element) {
		
		Window window = (mainWindow instanceof Window) ? ((Window) mainWindow) : null;
		
		if (element instanceof TableElement) {
			
			TableElement tableElement = (TableElement) element;
			UmlTable umltable = (UmlTable) tableElement.getModelElement();
			
			EditTableDialog dialog = new EditTableDialog(window, tableElement, tables);
			
			dialog.setLocationRelativeTo(mainWindow);
			dialog.setVisible(true);
			
			if (dialog.isOk()) {
				
				umltable.setCols(dialog.getCols());
				umltable.setIndexes(dialog.getIndexes());
				umltable.setForeignKeys(dialog.getForeignKeys());
				umltable.setTriggers(dialog.getTriggers());
				
				tables.changeTable(umltable);
				
				redraw();
			}
		} else if (element instanceof ViewElement) {
			
			ViewElement viewElement = (ViewElement) element;
			UmlView umlView = (UmlView) viewElement.getModelElement();
			
			EditViewDialog dialog = new EditViewDialog(window, viewElement);
			
			dialog.setLocationRelativeTo(mainWindow);
			dialog.setVisible(true);
			
			if (dialog.isOk()) {
				
				umlView.setName(dialog.getName());
				umlView.setSource(dialog.getSource());
				
				viewElement.elementChanged(umlView);
				
				redraw();
			}

		} else if (element instanceof ProcedureElement) {
			
			ProcedureElement procedureElement = (ProcedureElement) element;
			UmlProcedure umlProcedure = (UmlProcedure) procedureElement.getModelElement();
			
			EditProcedureDialog dialog = new EditProcedureDialog(window, procedureElement);
			
			dialog.setLocationRelativeTo(mainWindow);
			dialog.setVisible(true);
			
			if (dialog.isOk()) {
				
				umlProcedure.setName(dialog.getName());
				umlProcedure.setSource(dialog.getSource());
				
				procedureElement.elementChanged(umlProcedure);
				
				redraw();
			}

		} else if (element instanceof Relationship) {
			
			Relationship relationship = (Relationship) element;
			
			System.out.println("EditRelationshipDialog - " + relationship.getNode1().toString());
			System.out.println("EditRelationshipDialog - " + relationship.getNode2().toString());
			
			EditRelationshipDialog dialog = new EditRelationshipDialog(window,
				relationship);
			
			
			
			dialog.setLocationRelativeTo(mainWindow);
			dialog.setVisible(true);
			
			if (dialog.isOk()) {
				
				redraw();
			}
		}
	}
	
	/**
	* Switches a rectilinear connection to a direct one.
	*/
	public void rectilinearToDirect() {
		if (getSelectedElements().size() > 0 &&
			getSelectedElements().get(0) instanceof UmlConnection) {
			
			UmlConnection conn = (UmlConnection) getSelectedElements().get(0);
			execute(new ConvertConnectionTypeCommand(this, conn,
				new SimpleConnection()));
			 // we can only tell the selection handler to forget about the selection
			selectionHandler.deselectAll();
		}
	}

	/**
	* Switches a direct connection into a rectilinear one.
	*/
	public void directToRectilinear() {
		if (getSelectedElements().size() > 0 &&
			getSelectedElements().get(0) instanceof UmlConnection) {
			
			UmlConnection conn = (UmlConnection) getSelectedElements().get(0);
			execute(new ConvertConnectionTypeCommand(this, conn,
				new RectilinearConnection()));
			 // we can only tell the selection handler to forget about the selection
			selectionHandler.deselectAll();
		}
	}

	/**
	* Sets the end type navigability of the current selected connection.
	* @param endType the RelationEndType
	*/
	public void setNavigability(RelationEndType endType) {
		if (getSelectedElements().size() > 0 &&
			getSelectedElements().get(0) instanceof UmlConnection) {
			
			UmlConnection conn = (UmlConnection) getSelectedElements().get(0);
			Relation relation = (Relation) conn.getModelElement();
			// Setup a toggle
			if (endType == RelationEndType.SOURCE) {
				execute(new SetConnectionNavigabilityCommand(this, conn, endType,
					!relation.isNavigableToElement1()));
			}
			
			if (endType == RelationEndType.TARGET) {
				execute(new SetConnectionNavigabilityCommand(this, conn, endType,
					!relation.isNavigableToElement2()));
			}
		}
	}

	/**
	* {@inheritDoc}
	*/
	@Override
	public void handleCommand(String command) {
		
		MethodCall methodcall = selectorMap.get(command);
		if (methodcall != null) methodcall.call(this);
		else super.handleCommand(command);
	}
	
	/**
	* {@inheritDoc}
	*/
	public void notifyElementAdded(DiagramElement element) {
		
		if(element instanceof TableElement) {
			
			((UmlTable) ((TableElement) element).getModelElement()).setName("Table" + tables.getCounterValue());
			
			((TableElement) element).addNameChangeListener(new ElementNameChangeListener() {

				@Override
				public boolean nameChanged(String oldName, String newName) {
					
					if(oldName.equals(newName))
						return true;
					else if(tables.existsTableByName(newName))
						return false;
					else
						tables.changeTableName(oldName, newName);
					
					return true;
				}
			});
			
			tables.addTable((UmlTable) ((TableElement) element).getModelElement());
		} else if (element instanceof Relationship) {
			
			Relationship relationship = (Relationship) element;
			UmlRelation umlRelation = (UmlRelation) relationship.getModelElement();
			
			UmlTable referencedTable = (UmlTable) umlRelation.getElement1();
			
			System.out.println("notifyElementAdded - Relationship - element1 - " + referencedTable.getName());
			
			UmlTable referencingTable = (UmlTable) umlRelation.getElement2();
			
			System.out.println("notifyElementAdded - Relationship - element2 - " + referencingTable.getName());
			
			// referencing table must have primary key
			if(referencingTable.isPrimaryKey()) {
				
				UmlTableIndex primaryKey = referencingTable.getPrimaryKey();
				UmlTableForeignKey foreignKey = (UmlTableForeignKey) UmlTableForeignKey.getPrototype().clone();
				
				switch(relationship.getRelationshipType()) {

					case ONE_ONE_NON_IDE :
						
						// adding foreign key created from added cols
						
						foreignKey.setName(
							"fk_" + referencedTable.getName() + "_" + referencingTable.getName() + referencedTable.getForeignKeys().size() + 1);
						foreignKey.setReferencedTable(referencingTable.getName());
						foreignKey.setOnDeleteAction(UmlTableForeignKey.action.NO_ACTION);
						foreignKey.setOnUpdateAction(UmlTableForeignKey.action.NO_ACTION);
						
						// adding cols to make foreign key in referenced table
						// cols are taken from primary key in referencing table
						for(IndexCol indexCol : primaryKey.getCols()) {
							
							// primary key col
							UmlTableCol col = referencingTable.getColByName(indexCol.getName());
							
							String colName = referencingTable.getName() + "_" + indexCol.getName();
							
							referencedTable.addCol(
								UmlTableCol.getPrototype().create(
									colName
									, col.getColType()));
							
							foreignKey.addKeyCol(Boolean.TRUE, colName);
							foreignKey.addReferencedCol(Boolean.TRUE, indexCol.getName());
						}
						
						referencedTable.addForeignKey(foreignKey);
						
					break;
						
					case ONE_ONE_IDE :
						
						// adding foreign key created from added cols
						
						foreignKey.setName(
							"fk_" + referencedTable.getName() + "_" + referencingTable.getName() + referencedTable.getForeignKeys().size() + 1);
						foreignKey.setReferencedTable(referencingTable.getName());
						foreignKey.setOnDeleteAction(UmlTableForeignKey.action.NO_ACTION);
						foreignKey.setOnUpdateAction(UmlTableForeignKey.action.NO_ACTION);
						
						List<IndexCol> indexCols = new LinkedList<IndexCol>();
						
						// adding cols to make foreign key in referenced table
						// cols are taken from primary key in referencing table
						for(IndexCol indexCol : primaryKey.getCols()) {
							
							// primary key col
							UmlTableCol col = referencingTable.getColByName(indexCol.getName());
							
							String colName = referencingTable.getName() + "_" + indexCol.getName();
							
							referencedTable.addCol(
								UmlTableCol.getPrototype().create(
									colName
									, col.getColType()));
							
							foreignKey.addKeyCol(Boolean.TRUE, colName);
							foreignKey.addReferencedCol(Boolean.TRUE, indexCol.getName());
							
							indexCols.add(new IndexCol(Boolean.TRUE, colName, UmlTableIndex.order.ASC));
						}
						
						referencedTable.addForeignKey(foreignKey);
						
						UmlTableIndex primaryIndex = UmlTableIndex.getPrototype().create(
							"PRIMARY", UmlTableIndex.indexType.PRIMARY);
						primaryIndex.setCols(indexCols);
						
						referencedTable.addIndex(primaryIndex);
					break;
				}
			}
		}
		
		super.notifyElementAdded(element);
	}

	/**
	* {@inheritDoc}
	*/
	public void notifyElementRemoved(DiagramElement element) {
		
		super.notifyElementRemoved(element);
		
		if(element instanceof TableElement) {
			
			tables.removeTable((UmlTable) ((TableElement) element).getModelElement());
		}
	}
}
