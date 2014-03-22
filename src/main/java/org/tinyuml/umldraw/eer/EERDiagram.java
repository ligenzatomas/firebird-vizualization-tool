/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tinyuml.umldraw.eer;

import org.tinyuml.model.ElementType;
import org.tinyuml.model.Relation;
import org.tinyuml.model.RelationType;
import org.tinyuml.model.UmlModel;
import org.tinyuml.model.UmlRelation;
import org.tinyuml.model.UmlTable;
import org.tinyuml.umldraw.shared.GeneralDiagram;
import org.tinyuml.umldraw.shared.NoteConnection;
import org.tinyuml.umldraw.shared.NoteElement;
import org.tinyuml.umldraw.shared.UmlConnection;
import org.tinyuml.umldraw.shared.UmlDiagramElement;
import java.util.HashMap;
import java.util.Map;
import org.tinyuml.model.UmlProcedure;
import org.tinyuml.model.UmlView;
import org.tinyuml.umldraw.structure.Association;
import org.tinyuml.umldraw.structure.Dependency;
import org.tinyuml.umldraw.structure.Inheritance;

/**
 *
 * @author cml
 */
public class EERDiagram extends GeneralDiagram {
	
	public EERDiagram(UmlModel umlModel) {
		
		super(umlModel);
	}
	
	protected Map<ElementType, UmlDiagramElement> setupElementPrototypeMap() {
		Map<ElementType, UmlDiagramElement> elementPrototypes =
			new HashMap<ElementType, UmlDiagramElement>();

		UmlTable table = (UmlTable) UmlTable.getPrototype().clone();
		table.setName("Table1");
		TableElement tableElem = (TableElement) TableElement.getPrototype().clone();
		tableElem.setModelElement(table);
		tableElem.addNodeChangeListener(this);
		elementPrototypes.put(ElementType.TABLE, tableElem);
		
		UmlView view = (UmlView) UmlView.getPrototype().clone();
		view.setName("View1");
		ViewElement viewElem = (ViewElement) ViewElement.getPrototype().clone();
		viewElem.setModelElement(view);
		viewElem.addNodeChangeListener(this);
		elementPrototypes.put(ElementType.VIEW, viewElem);
		
		UmlProcedure procedure = (UmlProcedure) UmlProcedure.getPrototype().clone();
		procedure.setName("Procedure1");
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
}
