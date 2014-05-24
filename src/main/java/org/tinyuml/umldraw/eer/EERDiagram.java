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

package org.tinyuml.umldraw.eer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import org.tinyuml.model.DatabaseModel;
import org.tinyuml.model.ElementType;
import org.tinyuml.model.Relation;
import org.tinyuml.model.RelationType;
import org.tinyuml.model.UmlModel;
import org.tinyuml.model.UmlProcedure;
import org.tinyuml.model.UmlRelation;
import org.tinyuml.model.UmlTable;
import org.tinyuml.model.UmlView;
import org.tinyuml.umldraw.shared.GeneralDiagram;
import org.tinyuml.umldraw.shared.UmlConnection;
import org.tinyuml.umldraw.shared.UmlDiagramElement;

/**
 *
 * @author Tomáš Ligenza
 */
public class EERDiagram extends GeneralDiagram {
	
	private static final long serialVersionUID = 6930979972716330858L;
	
	private DatabaseModel databaseModel;
	
	public EERDiagram(UmlModel umlModel, DatabaseModel databaseModel) {
		
		super(umlModel);
		this.databaseModel = databaseModel;
	}
	
	private void writeObject(ObjectOutputStream stream) throws IOException {
		
		stream.writeObject(databaseModel);
	}

	private void readObject(ObjectInputStream stream)
		throws IOException, ClassNotFoundException {
		
		databaseModel = (DatabaseModel) stream.readObject();
	}
	
	protected Map<ElementType, UmlDiagramElement> setupElementPrototypeMap() {
		Map<ElementType, UmlDiagramElement> elementPrototypes =
			new HashMap<ElementType, UmlDiagramElement>();

		UmlTable table = (UmlTable) UmlTable.getPrototype().clone();
		table.setName("MyTable");
		TableElement tableElem = (TableElement) TableElement.getPrototype().clone();
		tableElem.setModelElement(table);
		tableElem.addNodeChangeListener(this);
		elementPrototypes.put(ElementType.TABLE, tableElem);
		
		UmlView view = (UmlView) UmlView.getPrototype().clone();
		view.setName("MyView1");
		ViewElement viewElem = (ViewElement) ViewElement.getPrototype().clone();
		viewElem.setModelElement(view);
		viewElem.addNodeChangeListener(this);
		elementPrototypes.put(ElementType.VIEW, viewElem);
		
		UmlProcedure procedure = (UmlProcedure) UmlProcedure.getPrototype().clone();
		procedure.setName("MyProcedure1");
		ProcedureElement procedureElem = (ProcedureElement) ProcedureElement.getPrototype().clone();
		procedureElem.setModelElement(procedure);
		procedureElem.addNodeChangeListener(this);
		elementPrototypes.put(ElementType.PROCEDURE, procedureElem);

		return elementPrototypes;
	}

	protected Map<RelationType, UmlConnection> setupConnectionPrototypeMap() {
		Map<RelationType, UmlConnection> connectionPrototypes =
			new HashMap<RelationType, UmlConnection>();

		UmlRelation notnavigable = new UmlRelation();
		notnavigable.setCanSetElement1Navigability(false);
		notnavigable.setCanSetElement2Navigability(false);
		
		Relationship oneOneNonIdePrototype = (Relationship) Relationship.getPrototype().clone();
		oneOneNonIdePrototype.setRelation((Relation) notnavigable.clone());
		oneOneNonIdePrototype.setRelationshipType(RelationType.ONE_ONE_NON_IDE);
		connectionPrototypes.put(RelationType.ONE_ONE_NON_IDE, oneOneNonIdePrototype);
		
		Relationship oneOneIdePrototype = (Relationship) Relationship.getPrototype().clone();
		oneOneIdePrototype.setRelation((Relation) notnavigable.clone());
		oneOneIdePrototype.setRelationshipType(RelationType.ONE_ONE_IDE);
		connectionPrototypes.put(RelationType.ONE_ONE_IDE, oneOneIdePrototype);
		
		Relationship oneNNonIdePrototype = (Relationship) Relationship.getPrototype().clone();
		oneNNonIdePrototype.setRelation((Relation) notnavigable.clone());
		oneNNonIdePrototype.setRelationshipType(RelationType.ONE_N_NON_IDE);
		connectionPrototypes.put(RelationType.ONE_N_NON_IDE, oneNNonIdePrototype);
		
		Relationship oneNIdePrototype = (Relationship) Relationship.getPrototype().clone();
		oneNIdePrototype.setRelation((Relation) notnavigable.clone());
		oneNIdePrototype.setRelationshipType(RelationType.ONE_N_IDE);
		connectionPrototypes.put(RelationType.ONE_N_IDE, oneNIdePrototype);
		
		Relationship nNIdePrototype = (Relationship) Relationship.getPrototype().clone();
		nNIdePrototype.setRelation((Relation) notnavigable.clone());
		nNIdePrototype.setRelationshipType(RelationType.N_N_IDE);
		connectionPrototypes.put(RelationType.N_N_IDE, nNIdePrototype);

		return connectionPrototypes;
	}
	
	public DatabaseModel getDatabaseModel() {
		return databaseModel;
	}
}
