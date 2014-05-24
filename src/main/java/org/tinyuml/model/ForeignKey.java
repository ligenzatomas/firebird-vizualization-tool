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

package org.tinyuml.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.MissingResourceException;
import javax.swing.DefaultComboBoxModel;
import org.firebirdvisualizationtool.database.firebird.ForeignKeyActions;

/**
 *
 * @author Tomáš Ligenza
 */
public class ForeignKey extends DefaultNamedElement {
	
	private static final long serialVersionUID = 7051553472855657448L;
	
	private ForeignKeyActions onUpdateAction;
	private ForeignKeyActions onDeleteAction;
	
	private String referencedTable;
	
	private List<ForeignKeyCol> keyCols			= new LinkedList<ForeignKeyCol>();
	private List<ForeignKeyCol> referencedCols	= new LinkedList<ForeignKeyCol>();
	
	private static ForeignKey prototype;
	
	public static ForeignKey getPrototype() {
		
		if (prototype == null) prototype = new ForeignKey();
		
		return prototype;
	}

	private ForeignKey() { }
	
	@Override
	public Object clone() {
		
		ForeignKey cloned = null;
		cloned = (ForeignKey) super.clone();
		
		keyCols			= new LinkedList<ForeignKeyCol>();
		referencedCols	= new LinkedList<ForeignKeyCol>();
		
		return cloned;
	}
	
	public ForeignKey create(String sName, String sTableName, ForeignKeyActions onUpdate, ForeignKeyActions onDelete) {
		
		ForeignKey cloned = null;
		cloned = (ForeignKey) super.clone();
		
		cloned.keyCols = new LinkedList<ForeignKeyCol>();
		cloned.referencedCols = new LinkedList<ForeignKeyCol>();
		
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
			
			if(col.getName().equals(name))
				return true;
		}
		
		return false;
	}
	
	public ForeignKeyCol getKeyColByName(String name) {
		
		for (ForeignKeyCol col : this.keyCols) {
			
			if(col.getName().equals(name))
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
			
			if(col.getName().equals(name))
				return true;
		}
		
		return false;
	}
	
	public ForeignKeyCol getReferencedColByName(String name) {
		
		for (ForeignKeyCol col : this.referencedCols) {
			
			if(col.getName().equals(name))
				return col;
		}
		
		throw new MissingResourceException("Nebyl nalezen požadovaný objekt.", "ForeignKeyCol", name);
	}
	
	public static DefaultComboBoxModel getForeignKeyActions() {
		
		final DefaultComboBoxModel model = new DefaultComboBoxModel();
		
		for(ForeignKeyActions type : ForeignKeyActions.values()) {
			
			model.addElement(type);
		}
		
		return model;
	}

	public ForeignKeyActions getOnUpdateAction() {
		
		return onUpdateAction;
	}
	
	public String getOnActionSql(ForeignKeyActions action) {
		
		String sql;
		
		switch (action) {
			
			case NO_ACTION:
				sql = "NO ACTION";
			break;
			
			case SET_DEFAULT:
				sql = "SET DEFAULT";
			break;
				
			case SET_NULL:
				sql = "SET NULL";
			break;
				
			default:
				sql = action.name();
			break;
		}
		
		return sql;
	}

	public void setOnUpdateAction(ForeignKeyActions updateAction) {
		
		this.onUpdateAction = updateAction;
	}
	
	public ForeignKeyActions getOnDeleteAction() {
		
		return this.onDeleteAction;
	}

	public void setOnDeleteAction(ForeignKeyActions deleteAction) {
		
		this.onDeleteAction = deleteAction;
	}
	
	public String getReferencedTable() {
		
		return this.referencedTable;
	}

	public void setReferencedTable(String refTable) {
		
		this.referencedTable = refTable;
	}
	
	public String getDropSql(String tableName) {
		
		return "ALTER TABLE " + tableName + " DROP CONSTRAINT " + getName() + ";";
	}
	
	public String getCreateSql(String tableName) {
		
		String script = "ALTER TABLE " + tableName + " ADD CONSTRAINT " + getName() + "\n"
			+ " FOREIGN KEY ";
		
		List<String> referencingScript = new ArrayList<String>();

		for(ForeignKeyCol col : getKeyCols()) {

			if(col.getChecked())
				referencingScript.add(col.getName());
		}

		StringBuilder referencingBuilder = new StringBuilder();
		referencingBuilder.append( referencingScript.remove(0));

		for( String s : referencingScript) {
			referencingBuilder.append( ",");
			referencingBuilder.append( s);
		}

		script = script + "(" + referencingBuilder.toString() + ") REFERENCES "
			+ getReferencedTable();
		
		List<String> referencedScript = new ArrayList<String>();

		for(ForeignKeyCol col : getReferencedCols()) {

			if(col.getChecked())
				referencedScript.add(col.getName());
		}

		StringBuilder referencedBuilder = new StringBuilder();
		referencedBuilder.append( referencedScript.remove(0));

		for( String s : referencedScript) {
			referencedBuilder.append( ",");
			referencedBuilder.append( s);
		}

		script = script + " (" + referencedBuilder.toString() + ")\n"
			+ " ON UPDATE " + getOnActionSql(getOnUpdateAction())
			+ " ON DELETE " + getOnActionSql(getOnDeleteAction()) + ";";
		
		return script;
	}
}
