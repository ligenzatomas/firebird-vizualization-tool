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

import java.awt.Component;
import java.awt.Window;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import org.firebirdvisualizationtool.database.firebird.ForeignKeyActions;
import org.firebirdvisualizationtool.database.firebird.IndexOrders;
import org.firebirdvisualizationtool.database.firebird.IndexTypes;
import org.tinyuml.draw.DiagramElement;
import org.tinyuml.draw.RectilinearConnection;
import org.tinyuml.draw.RectilinearLineBuilder;
import org.tinyuml.draw.RectilinearLineConnectMethod;
import org.tinyuml.draw.SimpleConnection;
import org.tinyuml.model.Column;
import org.tinyuml.model.DatabaseModel;
import org.tinyuml.model.Domain;
import org.tinyuml.model.ElementType;
import org.tinyuml.model.ForeignKey;
import org.tinyuml.model.ForeignKeyCol;
import org.tinyuml.model.Index;
import org.tinyuml.model.IndexColumn;
import org.tinyuml.model.Multiplicity;
import org.tinyuml.model.Procedure;
import org.tinyuml.model.Relation;
import org.tinyuml.model.RelationEndType;
import org.tinyuml.model.RelationType;
import org.tinyuml.model.Sequence;
import org.tinyuml.model.Table;
import org.tinyuml.model.UmlProcedure;
import org.tinyuml.model.UmlRelation;
import org.tinyuml.model.UmlTable;
import org.tinyuml.model.UmlView;
import org.tinyuml.model.View;
import org.tinyuml.ui.diagram.commands.ConvertConnectionTypeCommand;
import org.tinyuml.ui.diagram.commands.SetConnectionNavigabilityCommand;
import org.tinyuml.umldraw.eer.EERDiagram;
import org.tinyuml.umldraw.eer.ProcedureElement;
import org.tinyuml.umldraw.eer.Relationship;
import org.tinyuml.umldraw.eer.TableElement;
import org.tinyuml.umldraw.eer.ViewElement;
import org.tinyuml.umldraw.shared.GeneralDiagram;
import org.tinyuml.umldraw.shared.UmlConnection;
import org.tinyuml.umldraw.shared.UmlNode;
import org.tinyuml.util.MethodCall;

/**
 *
 * @author Tomáš Ligenza
 */
public class EERDiagramEditor extends DiagramEditor {
	
	private static Map<String, MethodCall> selectorMap = 
		new HashMap<String, MethodCall>();
	private static final long serialVersionUID = -6365796888401720887L;
	
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
			
			DatabaseModel databaseModel = ((EERDiagram) getDiagram()).getDatabaseModel();
			
			EditTableDialog dialog = new EditTableDialog(window, tableElement, databaseModel);
			
			dialog.setLocationRelativeTo(mainWindow);
			dialog.setVisible(true);
			
			if (dialog.isOk()) {
				
				umltable.setName(dialog.getName());
				umltable.setCols(dialog.getCols());
				umltable.setIndexes(dialog.getIndexes());
				umltable.setForeignKeys(dialog.getForeignKeys());
				umltable.setTriggers(dialog.getTriggers());
				
				tableElement.setLabelText(dialog.getName());
				tableElement.elementChanged(umltable);
				
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
			
			EditRelationshipDialog dialog = new EditRelationshipDialog(window,
				relationship);
			
			dialog.setLocationRelativeTo(mainWindow);
			dialog.setVisible(true);
			
			if (dialog.isOk()) {
				
				if(dialog.isReferencingTableMandatory() && !relationship.getRelation().isElement1Mandatory()) {
					
					relationship.getRelation().setElement1Mandatory();
				} else if(!dialog.isReferencingTableMandatory() && relationship.getRelation().isElement1Mandatory()) {
					
					relationship.getRelation().setElement1NonMandatory();
				}
				
				if(dialog.isReferencedTableMandatory() && !relationship.getRelation().isElement2Mandatory()) {
					
					relationship.getRelation().setElement2Mandatory();
				} else if(!dialog.isReferencedTableMandatory() && relationship.getRelation().isElement2Mandatory()) {
					
					relationship.getRelation().setElement2NonMandatory();
				}
				
				if(dialog.isOneToOne() && !relationship.getRelation().isOneOnOne()) {
					
					relationship.getRelation().setOneOnOne();
				}
				
				if(dialog.isOneToMany() && !relationship.getRelation().isOneOnMany()) {
					
					relationship.getRelation().setOneOnMany();
				}
				
				if(dialog.isIdentifying() && !relationship.getRelation().isIdentifying()) {
					
					relationship.getRelation().setIdentifying();
				} else if (!dialog.isIdentifying() && relationship.getRelation().isIdentifying()) {
					
					relationship.getRelation().setNonIdentifying();
				}
				
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
	
	public void createTable(Table aTable) {
		
		TableElement elementTable = (TableElement) super.createElement(ElementType.TABLE);

		((UmlTable) elementTable.getModelElement()).getTableModel().copy(aTable);
		UmlTable umlTable = (UmlTable) elementTable.getModelElement();
		
		elementTable.setModelElement(umlTable);
		elementTable.elementChanged(umlTable);

		Point2D position = getDiagram().getFreeSpaceForElement(elementTable);
		elementTable.setAbsolutePos(position.getX(), position.getY());
		
		super.notifyElementAdded(elementTable);

		redraw();
	}
	
	public void createView(View aView) {
		
		ViewElement elementView = (ViewElement) super.createElement(ElementType.VIEW);

		((UmlView) elementView.getModelElement()).getViewModel().copy(aView);
		UmlView umlView = (UmlView) elementView.getModelElement();
		
		elementView.setModelElement(umlView);
		elementView.elementChanged(umlView);

		Point2D position = getDiagram().getFreeSpaceForElement(elementView);
		elementView.setAbsolutePos(position.getX(), position.getY());
		
		super.notifyElementAdded(elementView);

		redraw();
	}
	
	public void createProcedure(Procedure aProcedure) {
		
		ProcedureElement elementProcedure = (ProcedureElement) super.createElement(ElementType.PROCEDURE);

		((UmlProcedure) elementProcedure.getModelElement()).getProcedureModel().copy(aProcedure);
		UmlProcedure umlProcedure = (UmlProcedure) elementProcedure.getModelElement();
		
		elementProcedure.setModelElement(umlProcedure);
		elementProcedure.elementChanged(umlProcedure);

		Point2D position = getDiagram().getFreeSpaceForElement(elementProcedure);
		elementProcedure.setAbsolutePos(position.getX(), position.getY());
		
		super.notifyElementAdded(elementProcedure);

		redraw();
	}
	
	public void createAllDomains(TreeSet<Domain> aDomains) {
		
		((EERDiagram) getDiagram()).getDatabaseModel().setDomains(aDomains);
	}
	
	public void createAllSequences(TreeSet<Sequence> aSequences) {
		
		((EERDiagram) getDiagram()).getDatabaseModel().setSequences(aSequences);
	}
	
	public void createAllConnections() {
		
		DatabaseModel databaseModel = ((EERDiagram) getDiagram()).getDatabaseModel();
		
		for(Table table : databaseModel.getTables()) {
			
			for(ForeignKey key : table.getForeignKeys()) {
				
				if(key.checkValid()) {
					
					Boolean notNull = true;
					Boolean pk		= true;
					
					for(ForeignKeyCol keyCol : key.getKeyCols()) {
						
						Column col = table.getColumnByName(keyCol.getName());
						
						if(col == null) {
							
							notNull = false;
							pk = false;
						}
						
						if(col != null && !col.getNn()) {
							notNull = false;
						}
						
						if(col != null && (!table.isPrimaryKey()
							|| !table.isPrimaryKeyColByName(col.getName()))) {
							pk = false;
						}
					}
					
					RelationType type;
					Multiplicity multi;
					
					if(pk) {
						
						type = RelationType.ONE_N_IDE;
						
						if(notNull) {
							multi = Multiplicity.ONE;
						} else {
							multi = Multiplicity.ZERO_TO_ONE;
						}
					} else {
						
						type = RelationType.ONE_N_NON_IDE;
						
						if(notNull) {
							multi = Multiplicity.ONE;
						} else {
							multi = Multiplicity.ZERO_TO_ONE;
						}
					}

					Table referencedTable = databaseModel.getTableByName(key.getReferencedTable());
					
					if(referencedTable == null
						|| !referencedTable.isPrimaryKey())
						break;

					DiagramElement referencingElement = null;
					DiagramElement referencedElement = null;
					
					for(DiagramElement el : getDiagram().getChildren()) {
						
						if(el instanceof TableElement) {
							
							TableElement tEl = (TableElement) el;
							
							if(((UmlTable) tEl.getModelElement()).getName().equals(table.getName()))
								referencingElement = el;
							
							if(((UmlTable) tEl.getModelElement()).getName().equals(referencedTable.getName()))
								referencedElement = el;
						}
					}
					
					if(referencedElement == null || referencingElement == null)
						break;

					UmlConnection relationshipAssociation = getDiagram().createConnection(
						type, (UmlNode) referencingElement, (UmlNode) referencedElement);
					setCreateConnectionMode(type);
					
					((Relation) relationshipAssociation.getModelElement()).setElement1Multiplicity(Multiplicity.ONE_TO_N);
					((Relation) relationshipAssociation.getModelElement()).setElement2Multiplicity(multi);
					
					((RectilinearConnection) relationshipAssociation.getConnection()).setPoints(
						RectilinearLineBuilder.getInstance().calculateLineSegments(
							relationshipAssociation.getNode1()
							, relationshipAssociation.getNode2()));
					
					relationshipAssociation.getConnectMethod().generateAndSetPointsToConnection(
						relationshipAssociation
						, relationshipAssociation.getNode1()
						, relationshipAssociation.getNode2()
						, relationshipAssociation.getEndPoint1()
						, relationshipAssociation.getEndPoint2());
						
					getDiagram().addChild(relationshipAssociation);
					
					super.notifyElementAdded(relationshipAssociation);
					relationshipAssociation.recalculateSize(getDrawingContext());
					
					redraw();
				}
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
		
		if (element instanceof TableElement) {
			
			DatabaseModel databaseModel = ((EERDiagram) getDiagram()).getDatabaseModel();
			Table table = ((UmlTable) ((TableElement) element).getModelElement()).getTableModel();
			
			int i = 1;
			String tableName = table.getName();
			
			while(databaseModel.containsTableByName(table.getName())) {
				
				table.setName(tableName + i);
				i++;
			}
			
			databaseModel.addTable(table);
			
		} else if (element instanceof ViewElement) {
			
			DatabaseModel databaseModel = ((EERDiagram) getDiagram()).getDatabaseModel();
			View view = ((UmlView) ((ViewElement) element).getModelElement()).getViewModel();
			
			int i = 1;
			String viewName = view.getName();
			
			while(databaseModel.containsViewByName(viewName)) {
				
				view.setName(viewName + i);
				i++;
			}
			
			databaseModel.addView(view);
			
		} else if (element instanceof ProcedureElement) {
			
			DatabaseModel databaseModel = ((EERDiagram) getDiagram()).getDatabaseModel();
			Procedure procedure = ((UmlProcedure) ((ProcedureElement) element).getModelElement()).getProcedureModel();
			
			int i = 1;
			String procedureName = procedure.getName();
			
			while(databaseModel.containsProcedureByName(procedureName)) {
				
				procedure.setName(procedureName + i);
				i++;
			}
			
			databaseModel.addProcedure(procedure);
			
		} else if (element instanceof Relationship) {
			
			Relationship relationship = (Relationship) element;
			UmlRelation umlRelation = (UmlRelation) relationship.getModelElement();
			
			UmlTable referencedTable = (UmlTable) umlRelation.getElement1();
			
			UmlTable referencingTable = (UmlTable) umlRelation.getElement2();
			
			// referencing table must have primary key
			if(referencingTable.isPrimaryKey()) {
				
				Index primaryKeyReferencingTable = referencingTable.getPrimaryKey();
				Index primaryKeyReferencedTable = referencedTable.getPrimaryKey();
				ForeignKey foreignKey = (ForeignKey) ForeignKey.getPrototype().clone();
				
				List<IndexColumn> indexCols;
				
				switch(relationship.getRelationshipType()) {

					case ONE_ONE_NON_IDE :
						
						// adding foreign key created from added cols
						
						foreignKey.setName(
							"fk" + referencedTable.getName() + referencingTable.getName() + (referencedTable.getForeignKeys().size() + 1));
						foreignKey.setReferencedTable(referencingTable.getName());
						foreignKey.setOnDeleteAction(ForeignKeyActions.NO_ACTION);
						foreignKey.setOnUpdateAction(ForeignKeyActions.NO_ACTION);
						
						// adding cols to make foreign key in referenced table
						// cols are taken from primary key in referencing table
						for(IndexColumn indexCol : primaryKeyReferencingTable.getCols()) {
							
							// primary key col
							Column col = referencingTable.getColByName(indexCol.getName());
							
							String colName = referencingTable.getName() + "_" + indexCol.getName();
							Column newCol = (Column) col.clone();
							
							newCol.setName(colName);
							newCol.setCheck("");
							newCol.setDefaultValue("");
							
							referencedTable.addCol(newCol);
							
							foreignKey.addKeyCol(Boolean.TRUE, colName);
							foreignKey.addReferencedCol(Boolean.TRUE, indexCol.getName());
						}
						
						referencedTable.addForeignKey(foreignKey);
						
						((UmlRelation) relationship.getRelation()).setForeignKey(foreignKey);
						((Relation) relationship.getModelElement()).setElement1Multiplicity(Multiplicity.ONE);
						((Relation) relationship.getModelElement()).setElement2Multiplicity(Multiplicity.ONE);
						
					break;
						
					case ONE_ONE_IDE :
						
						// adding foreign key created from added cols
						
						foreignKey.setName(
							"fk" + referencedTable.getName() + referencingTable.getName() + (referencedTable.getForeignKeys().size() + 1));
						foreignKey.setReferencedTable(referencingTable.getName());
						foreignKey.setOnDeleteAction(ForeignKeyActions.NO_ACTION);
						foreignKey.setOnUpdateAction(ForeignKeyActions.NO_ACTION);
						
						indexCols = new LinkedList<IndexColumn>();
						
						// adding cols to make foreign key in referenced table
						// cols are taken from primary key in referencing table
						for(IndexColumn indexCol : primaryKeyReferencingTable.getCols()) {
							
							// primary key col
							Column col = referencingTable.getColByName(indexCol.getName());
							
							String colName = referencingTable.getName() + "_" + indexCol.getName();
							Column newCol = (Column) col.clone();
							
							newCol.setName(colName);
							newCol.setNn(Boolean.TRUE);
							newCol.setCheck("");
							newCol.setDefaultValue("");
							
							referencedTable.addCol(newCol);
							
							foreignKey.addKeyCol(Boolean.TRUE, colName);
							foreignKey.addReferencedCol(Boolean.TRUE, indexCol.getName());
							
							indexCols.add(new IndexColumn(Boolean.TRUE, colName, IndexOrders.ASC));
						}
						
						referencedTable.addForeignKey(foreignKey);
						
						((UmlRelation) relationship.getRelation()).setForeignKey(foreignKey);
						((Relation) relationship.getModelElement()).setElement1Multiplicity(Multiplicity.ONE);
						((Relation) relationship.getModelElement()).setElement2Multiplicity(Multiplicity.ONE);
						
						Index primaryIndex = Index.getPrototype().create(
							"PRIMARY", IndexTypes.PRIMARY);
						primaryIndex.setCols(indexCols);
						
						referencedTable.addIndex(primaryIndex);
					break;
						
					case ONE_N_NON_IDE :
						
						// adding foreign key created from added cols
						
						foreignKey.setName(
							"fk" + referencedTable.getName() + referencingTable.getName() + (referencedTable.getForeignKeys().size() + 1));
						foreignKey.setReferencedTable(referencingTable.getName());
						foreignKey.setOnDeleteAction(ForeignKeyActions.NO_ACTION);
						foreignKey.setOnUpdateAction(ForeignKeyActions.NO_ACTION);
						
						// adding cols to make foreign key in referenced table
						// cols are taken from primary key in referencing table
						for(IndexColumn indexCol : primaryKeyReferencingTable.getCols()) {
							
							// primary key col
							Column col = referencingTable.getColByName(indexCol.getName());
							
							String colName = referencingTable.getName() + "_" + indexCol.getName();
							Column newCol = (Column) col.clone();
							
							newCol.setName(colName);
							newCol.setCheck("");
							newCol.setDefaultValue("");
							
							referencedTable.addCol(newCol);
							
							foreignKey.addKeyCol(Boolean.TRUE, colName);
							foreignKey.addReferencedCol(Boolean.TRUE, indexCol.getName());
						}
						
						referencedTable.addForeignKey(foreignKey);
						
						((UmlRelation) relationship.getRelation()).setForeignKey(foreignKey);
						((Relation) relationship.getModelElement()).setElement1Multiplicity(Multiplicity.ONE_TO_N);
						((Relation) relationship.getModelElement()).setElement2Multiplicity(Multiplicity.ONE);
						
					break;
						
					case ONE_N_IDE :
						
						// adding foreign key created from added cols
						
						foreignKey.setName(
							"fk" + referencedTable.getName() + referencingTable.getName() + (referencedTable.getForeignKeys().size() + 1));
						foreignKey.setReferencedTable(referencingTable.getName());
						foreignKey.setOnDeleteAction(ForeignKeyActions.NO_ACTION);
						foreignKey.setOnUpdateAction(ForeignKeyActions.NO_ACTION);
						
						indexCols = new LinkedList<IndexColumn>();
						
						// adding cols to make foreign key in referenced table
						// cols are taken from primary key in referencing table
						for(IndexColumn indexCol : primaryKeyReferencingTable.getCols()) {
							
							// primary key col
							Column col = referencingTable.getColByName(indexCol.getName());
							
							String colName = referencingTable.getName() + "_" + indexCol.getName();
							Column newCol = (Column) col.clone();
							
							newCol.setName(colName);
							newCol.setNn(Boolean.TRUE);
							newCol.setCheck("");
							newCol.setDefaultValue("");
							
							referencedTable.addCol(newCol);
							
							foreignKey.addKeyCol(Boolean.TRUE, colName);
							foreignKey.addReferencedCol(Boolean.TRUE, indexCol.getName());
							
							indexCols.add(new IndexColumn(Boolean.TRUE, colName, IndexOrders.ASC));
						}		
						
						referencedTable.addForeignKey(foreignKey);
						
						((UmlRelation) relationship.getRelation()).setForeignKey(foreignKey);
						((Relation) relationship.getModelElement()).setElement1Multiplicity(Multiplicity.ONE_TO_N);
						((Relation) relationship.getModelElement()).setElement2Multiplicity(Multiplicity.ONE);
						
						if(referencedTable.isPrimaryKey()) {
							
							Index primaryKey = referencedTable.getPrimaryKey();
							
							for(IndexColumn col : indexCols) {
								
								primaryKey.addCol(true, col.getName(), col.getoOrder());
							}
						} else {
							
							Index primaryIndexN = Index.getPrototype().create(
								"PRIMARY", IndexTypes.PRIMARY);
							primaryIndexN.setCols(indexCols);
						
							referencedTable.addIndex(primaryIndexN);
						}
					break;
						
					case N_N_IDE :
						
						// both tables must have primary keys
						if(!referencedTable.isPrimaryKey()
							|| !referencingTable.isPrimaryKey())
							break;
						
						// we have to create an association table
						// and two identifying connection 1:N
						// between association table and referencing table
						// and association table and referenced table
						
						// create new table element, table uml model and set its name
						TableElement elementTable = (TableElement) getDiagram().getElementFactory().createNode(ElementType.TABLE);
						UmlTable umlTable = UmlTable.getPrototype().create(referencedTable.getName() + "Has" + referencingTable.getName());
						
						elementTable.setModelElement(umlTable);
						elementTable.setParent(getDiagram());
						elementTable.addNodeChangeListener(getDiagram());
						
						
						// relationship from association table to referenced table
						Relationship relationshipAssociation = (Relationship) getDiagram().createConnection(
								RelationType.ONE_N_IDE, (UmlNode) elementTable, (UmlNode) relationship.getNode2());
						setCreateConnectionMode(RelationType.ONE_N_IDE);
						
						RectilinearLineConnectMethod.getInstance().generateAndSetPointsToConnection(
							relationshipAssociation, elementTable, relationshipAssociation.getNode2(), elementTable.getOrigin(), relationshipAssociation.getNode2().getOrigin());
						
						relationship.setRelationshipType(RelationType.ONE_N_IDE);
						relationship.setNode2(relationship.getNode1());
						relationship.setNode1(elementTable);
						
						relationship.getNode1().addConnection(relationship);
						relationship.getNode2().addConnection(relationship);
						
						((Relation) relationship.getModelElement()).setElement1(umlTable);
						((Relation) relationship.getModelElement()).setElement2(referencedTable);
						
						getDiagram().addChild(relationshipAssociation);
						notifyElementAdded(relationshipAssociation);
						notifyElementAdded(relationship);
						
						elementTable.draw(getDrawingContext());
						
						Point2D position = getDiagram().getFreeSpaceForElement(elementTable);

						elementTable.setAbsolutePos(position.getX(), position.getY());
						
						getDiagram().addChild(elementTable);
						notifyElementAdded(elementTable);
						
						redraw();
						
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
		
		if (element instanceof TableElement) {
			
			DatabaseModel databaseModel = ((EERDiagram) getDiagram()).getDatabaseModel();
			Table table = ((UmlTable) ((TableElement) element).getModelElement()).getTableModel();
			
			databaseModel.getTables().remove(table);
			
		} else if (element instanceof ViewElement) {
			
			DatabaseModel databaseModel = ((EERDiagram) getDiagram()).getDatabaseModel();
			View view = ((UmlView) ((ViewElement) element).getModelElement()).getViewModel();
			
			databaseModel.getViews().remove(view);
			
		} else if (element instanceof ProcedureElement) {
			
			DatabaseModel databaseModel = ((EERDiagram) getDiagram()).getDatabaseModel();
			Procedure procedure = ((UmlProcedure) ((ProcedureElement) element).getModelElement()).getProcedureModel();
			
			databaseModel.getProcedures().remove(procedure);
			
		} else if (element instanceof Relationship) {
			
			Relationship relationship = (Relationship) element;
			UmlRelation umlRelation = (UmlRelation) relationship.getModelElement();
			
			UmlTable referencedTable = (UmlTable) umlRelation.getElement1();
			UmlTable referencingTable = (UmlTable) umlRelation.getElement2();
			
			ForeignKey fk = ((UmlRelation) relationship.getRelation()).getForeignKey();
				
			switch(relationship.getRelationshipType()) {

				case ONE_ONE_NON_IDE :

					// remove foreign key and cols
					referencedTable.getForeignKeys().remove(fk);

					for(ForeignKeyCol indexCol : fk.getKeyCols()) {

						Column col = referencedTable.getColByName(indexCol.getName());

						if(col != null) {
							referencedTable.getCols().remove(col);
						}
					}
				break;

				case ONE_ONE_IDE :

					// remove foreign key, cols and cols from primary key
					referencedTable.getForeignKeys().remove(fk);

					for(ForeignKeyCol indexCol : fk.getKeyCols()) {

						Column col = referencedTable.getColByName(indexCol.getName());

						if(col != null) {

							if(referencedTable.isPrimaryKey()) {

								for(IndexColumn primaryCol : referencedTable.getPrimaryKey().getCols()) {

									if(primaryCol.getName().equals(indexCol.getName()))
										referencedTable.getPrimaryKey().getCols().remove(primaryCol);
								}
								
								if(referencedTable.getPrimaryKey().getCols().size() == 0)
									referencedTable.getIndexes().remove(referencedTable.getPrimaryKey());
							}

							referencedTable.getCols().remove(col);
						}
					}
				break;

				case ONE_N_NON_IDE :

					// remove foreign key and cols
					referencedTable.getForeignKeys().remove(fk);

					for(ForeignKeyCol indexCol : fk.getKeyCols()) {

						Column col = referencedTable.getColByName(indexCol.getName());

						if(col != null) {
							referencedTable.getCols().remove(col);
						}
					}

				break;

				case ONE_N_IDE :

					// remove foreign key, cols and cols from primary key
					referencedTable.getForeignKeys().remove(fk);

					for(ForeignKeyCol indexCol : fk.getKeyCols()) {

						Column col = referencedTable.getColByName(indexCol.getName());

						if(col != null) {

							if(referencedTable.isPrimaryKey()) {

								for(IndexColumn primaryCol : referencedTable.getPrimaryKey().getCols()) {

									if(primaryCol.getName().equals(indexCol.getName()))
										referencedTable.getPrimaryKey().getCols().remove(primaryCol);
								}
								
								if(referencedTable.getPrimaryKey().getCols().size() == 0)
									referencedTable.getIndexes().remove(referencedTable.getPrimaryKey());
							}

							referencedTable.getCols().remove(col);
						}
					}
				break;

				case N_N_IDE :

					throw new RuntimeException("Relationship N to M identifying is not supported now.");
			}
			
			relationship.getNode1().removeConnection(relationship);
			relationship.getNode2().removeConnection(relationship);
			
			TableElement table1 = (TableElement) relationship.getNode1();
			table1.elementChanged(table1.getModelElement());
			
			TableElement table2 = (TableElement) relationship.getNode2();
			table2.elementChanged(table2.getModelElement());
		}
		
		super.notifyElementRemoved(element);
		
		redraw();
	}
}
