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

import java.util.List;
import org.firebirdvisualizationtool.database.firebird.IndexTypes;

/**
 *
 * @author Tomáš Ligenza
 */
public class UmlTable extends AbstractUmlModelElement {
	
	private static final long serialVersionUID = -6810853836079792202L;
	
	private Table tableModel = new Table();
	
	private static UmlTable prototype;
	
	public static UmlTable getPrototype() {
	
		if (prototype == null) prototype = new UmlTable();
	
		return prototype;
	}
	
	private UmlTable() { }
	
	@Override
	public Object clone() {
		
		UmlTable cloned = (UmlTable) super.clone();
		
		if (tableModel != null) {
			cloned.tableModel = (Table) tableModel.clone();
		}
		
		return cloned;
	}
	
	public UmlTable create(String name) {
		
		UmlTable cloned = (UmlTable) clone();
		
		cloned.setTableModel(new Table());
		cloned.setName(name);
		
		return cloned;
	}
	
	public UmlTable create(Table table) {
		
		UmlTable cloned = (UmlTable) clone();
		
		cloned.setTableModel(table);
		
		return cloned;
	}
	
	public void setTableModel(Table table) {
		
		this.tableModel = table;
	}
	
	public Table getTableModel() { return tableModel; }
	
	@Override
	public void setName(String name) {
		
		getTableModel().setName(name);
	}
	
	@Override
	public String getName() {
		
		return getTableModel().getName();
	}

	public List<Column> getCols() { return getTableModel().getCols(); }
	
	public Column getColByName(String name) {
		
		Column tableCol = null;
		
		for(Column col : getCols()) {
			
			if(col.getName().equals(name))
				tableCol = col;
		}
		
		return tableCol;
	}
	
	public void setCols(List<Column> colsList) {
		
		getTableModel().setCols(colsList);
		notifyElementChanged();
	}
	
	public void addCol(Column col) {
		
		if(col.getDataType().checkValid()) {
			
			getTableModel().getCols().add(col);
			notifyElementChanged();
		}
	}

	public List<Index> getIndexes() { return getTableModel().getIndexes(); }
	
	public void setIndexes(List<Index> indexesList) {
		
		getTableModel().setIndexes(indexesList);
		notifyElementChanged();
	}
	
	public void addIndex(Index index) {
		
		if(index.checkValid()) {
			
			getTableModel().getIndexes().add(index);
			notifyElementChanged();
		}
	}

	public List<ForeignKey> getForeignKeys() { return getTableModel().getForeignKeys(); }

	public void setForeignKeys(List<ForeignKey> foreignKeysList) {
		
		getTableModel().setForeignKeys(foreignKeysList);
		notifyElementChanged();
	}
	
	public void addForeignKey(ForeignKey foreignKey) {
		
		if(foreignKey.checkValid()) {
			
			getTableModel().getForeignKeys().add(foreignKey);
			notifyElementChanged();
		}
	}
	
	public List<Trigger> getTriggers() { return getTableModel().getTriggers(); }
	
	public void setTriggers(List<Trigger> triggersList) {
		
		getTableModel().setTriggers(triggersList);
		notifyElementChanged();
	}
	
	public void addTrigger(Trigger trigger) {
		
		if(trigger.checkValid()) {
			
			getTableModel().getTriggers().add(trigger);
			notifyElementChanged();
		}
	}
	
	public Index getPrimaryKey() {
		
		Index primaryKey = null;
		
		for(Index index : getIndexes()) {
			
			if(index.getiIndexType() == IndexTypes.PRIMARY) {
				
				primaryKey = index;
			}
		}
		
		return primaryKey;
	}
	
	public boolean isPrimaryKey() {
		
		for(Index index : getIndexes()) {
			
			if(index.getiIndexType() == IndexTypes.PRIMARY) {
				
				return true;
			}
		}
		
		return false;
	}
}
