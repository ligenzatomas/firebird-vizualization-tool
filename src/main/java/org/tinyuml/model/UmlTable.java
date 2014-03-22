/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tinyuml.model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author cml
 */
public class UmlTable extends AbstractUmlModelElement {
	
	private static final long serialVersionUID = -758612882514026520L;
	
	private List<UmlTableCol> cols = new ArrayList<UmlTableCol>();
	private List<UmlTableIndex> indexes = new ArrayList<UmlTableIndex>();
	private List<UmlTableForeignKey> foreignKeys = new ArrayList<UmlTableForeignKey>();
	private List<UmlTableTrigger> triggers = new ArrayList<UmlTableTrigger>();
	
	private static UmlTable prototype;
	
	public static UmlTable getPrototype() {
	
		if (prototype == null) prototype = new UmlTable();
	
		return prototype;
	}
	
	private UmlTable() { }
	
	public Object clone() {
		
		UmlTable cloned = (UmlTable) super.clone();
		
		cloned.cols = new ArrayList<UmlTableCol>();
		for (UmlTableCol attribute : cols) {
			cloned.cols.add(attribute);
		}
		
		cloned.indexes = new ArrayList<UmlTableIndex>();
		for (UmlTableIndex method : indexes) {
			cloned.indexes.add(method);
		}
		
		cloned.foreignKeys = new ArrayList<UmlTableForeignKey>();
		for (UmlTableForeignKey stereotype : foreignKeys) {
			cloned.foreignKeys.add(stereotype);
		}
		
		cloned.triggers = new ArrayList<UmlTableTrigger>();
		for (UmlTableTrigger stereotype : triggers) {
			cloned.triggers.add(stereotype);
		}
		
		return cloned;
	}

	public List<UmlTableCol> getCols() { return cols; }
	
	public UmlTableCol getColByName(String name) {
		
		UmlTableCol tableCol = null;
		
		for(UmlTableCol col : getCols()) {
			
			if(col.getName().equals(name))
				tableCol = col;
		}
		
		return tableCol;
	}
	
	public void setCols(List<UmlTableCol> colsList) {
		cols = colsList;
		notifyElementChanged();
	}
	
	public void addCol(UmlTableCol col) {
		
		if(col.checkValid()) {
			cols.add(col);
			notifyElementChanged();
		}
	}

	public List<UmlTableIndex> getIndexes() { return indexes; }
	
	public void setIndexes(List<UmlTableIndex> indexesList) {
		indexes = indexesList;
		notifyElementChanged();
	}
	
	public void addIndex(UmlTableIndex index) {
		
		if(index.checkValid()
			&& (index.getiIndexType().equals(UmlTableIndex.indexType.PRIMARY)
				&& !isPrimaryKey())) {
			
			indexes.add(index);
			notifyElementChanged();
		}
	}

	public List<UmlTableForeignKey> getForeignKeys() { return foreignKeys; }

	public void setForeignKeys(List<UmlTableForeignKey> foreignKeysList) {
		foreignKeys = foreignKeysList;
		notifyElementChanged();
	}
	
	public void addForeignKey(UmlTableForeignKey foreignKey) {
		
		System.out.println("addForeignKey - " + foreignKey.getName());
		
		if(foreignKey.checkValid()) {
			
			System.out.println("addForeignKey - isValid true");
			
			foreignKeys.add(foreignKey);
			notifyElementChanged();
		}
	}
	
	public List<UmlTableTrigger> getTriggers() { return triggers; }

	public void setTriggers(List<UmlTableTrigger> triggersList) {
		triggers = triggersList;
		notifyElementChanged();
	}
	
	public UmlTableIndex getPrimaryKey() {
		
		UmlTableIndex primaryKey = null;
		
		for(UmlTableIndex index : getIndexes()) {
			
			if(index.getiIndexType() == UmlTableIndex.indexType.PRIMARY) {
				
				primaryKey = index;
			}
		}
		
		return primaryKey;
	}
	
	public boolean isPrimaryKey() {
		
		for(UmlTableIndex index : getIndexes()) {
			
			if(index.getiIndexType() == UmlTableIndex.indexType.PRIMARY) {
				
				return true;
			}
		}
		
		return false;
	}
}
