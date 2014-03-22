/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tinyuml.model;

import java.util.LinkedList;
import java.util.List;
import java.util.MissingResourceException;
import javax.swing.DefaultComboBoxModel;
import org.tinyuml.ui.model.ForeignKeyCol;
import org.tinyuml.ui.model.IndexCol;

/**
 *
 * @author cml
 */
public class UmlTableForeignKey extends DefaultNamedElement {
	
	public enum action	{ RESTRICT, NO_ACTION, CASCADE, SET_DEFAULT, SET_NULL };
	
	private action onUpdateAction;
	private action onDeleteAction;
	
	private String referencedTable;
	
	private List<ForeignKeyCol> keyCols			= new LinkedList<ForeignKeyCol>();
	private List<ForeignKeyCol> referencedCols	= new LinkedList<ForeignKeyCol>();
	
	private static UmlTableForeignKey prototype;
	
	public static UmlTableForeignKey getPrototype() {
		
		if (prototype == null) prototype = new UmlTableForeignKey();
		
		return prototype;
	}

	private UmlTableForeignKey() { }
	
	@Override
	public Object clone() {
		
		UmlTableForeignKey cloned = null;
		cloned = (UmlTableForeignKey) super.clone();
		
		return cloned;
	}
	
	public UmlTableForeignKey create(String sName, String sTableName, action onUpdate, action onDelete) {
		
		UmlTableForeignKey cloned = null;
		cloned = (UmlTableForeignKey) super.clone();
		
		cloned.setName(sName);
		cloned.setReferencedTable(sTableName);
		cloned.setOnUpdateAction(onUpdate);
		cloned.setOnDeleteAction(onDelete);
		
		return cloned;
	}
	
	public void setKeyCols(List<ForeignKeyCol> keyCols) {
		
		this.keyCols = keyCols;
	}
	
	public void addKeyCol(Boolean check, String name) {
		
		this.keyCols.add(new ForeignKeyCol(check, name));
	}
	
	public List<ForeignKeyCol> getKeyCols() {
		
		return this.keyCols;
	}
	
	public boolean isKeyColByName(String name) {
		
		for (ForeignKeyCol col : this.keyCols) {
			
			if(col.getName() == name)
				return true;
		}
		
		return false;
	}
	
	public ForeignKeyCol getKeyColByName(String name) {
		
		for (ForeignKeyCol col : this.keyCols) {
			
			if(col.getName() == name)
				return col;
		}
		
		throw new MissingResourceException("Nebyl nalezen požadovaný objekt.", "ForeignKeyCol", name);
	}
	
	public boolean checkValid() {
		
		return !getName().isEmpty()
			&& !getReferencedTable().isEmpty()
			&& !getKeyCols().isEmpty()
			&& !getReferencedCols().isEmpty();
	}
	
	public void setReferencedCols(List<ForeignKeyCol> keyCols) {
		
		this.referencedCols = keyCols;
	}
	
	public void addReferencedCol(Boolean check, String name) {
		
		this.referencedCols.add(new ForeignKeyCol(check, name));
	}
	
	public List<ForeignKeyCol> getReferencedCols() {
		
		return this.referencedCols;
	}
	
	public boolean isReferencedColByName(String name) {
		
		for (ForeignKeyCol col : this.referencedCols) {
			
			if(col.getName() == name)
				return true;
		}
		
		return false;
	}
	
	public ForeignKeyCol getReferencedColByName(String name) {
		
		for (ForeignKeyCol col : this.referencedCols) {
			
			if(col.getName() == name)
				return col;
		}
		
		throw new MissingResourceException("Nebyl nalezen požadovaný objekt.", "ForeignKeyCol", name);
	}
	
	public static DefaultComboBoxModel getForeignKeyActions() {
		
		final DefaultComboBoxModel model = new DefaultComboBoxModel();
		
		for(action type : action.values()) {
			
			model.addElement(type);
		}
		
		return model;
	}

	public action getOnUpdateAction() {
		
		return onUpdateAction;
	}

	public void setOnUpdateAction(action updateAction) {
		
		this.onUpdateAction = updateAction;
	}
	
	public action getOnDeleteAction() {
		
		return this.onDeleteAction;
	}

	public void setOnDeleteAction(action deleteAction) {
		
		this.onDeleteAction = deleteAction;
	}
	
	public String getReferencedTable() {
		
		return this.referencedTable;
	}

	public void setReferencedTable(String refTable) {
		
		this.referencedTable = refTable;
	}
}
