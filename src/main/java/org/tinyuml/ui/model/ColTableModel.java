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

package org.tinyuml.ui.model;

import java.util.LinkedList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.firebirdvisualizationtool.database.firebird.ColumnTypes;
import org.tinyuml.model.Column;
import org.tinyuml.util.ApplicationResources;

/**
 *
 * @author Tomáš Ligenza
 */
public class ColTableModel extends AbstractTableModel {
	
	private static final long serialVersionUID = 6446003111846560429L;

	private final String[] columnNames = { 
		ApplicationResources.getInstance().getString("database.model.coltable.name")
		, ApplicationResources.getInstance().getString("database.model.coltable.type")
		, ApplicationResources.getInstance().getString("database.model.coltable.nn")};
	
	private final List<Column> entries = new LinkedList<Column>();
	
	@Override
	public String getColumnName(int index) {
		
		return columnNames[index];
	}
	
	@Override
	public void fireTableDataChanged() {
		
		super.fireTableDataChanged();
	}
	
	public void addEntry(Column col) {
		
		int size = entries.size();
		
		entries.add(col);
		fireTableRowsInserted(size, size);
	}
	
	public List<Column> getEntries() {
		
		return entries;
	}
	
	public boolean isColByName(String name) {
		
		for (Column col : entries) {
			
			if(col.getName().equals(name))
				return true;
		}
		
		return false;
	}
	
	public void moveUpEntry(int index) {
		
		if(index < getRowCount()) {

			Column entryDown = entries.remove(index);
			Column entryUp = entries.remove(index - 1);

			entries.add(index - 1, entryDown);
			entries.add(index, entryUp);

			fireTableDataChanged();
		}
	}
	
	public void moveDownEntry(int index) {
		
		if(index < (getRowCount() - 1)) {
		
			Column entryDown = entries.remove(index);
			entries.add(index + 1, entryDown);

			fireTableDataChanged();
		}
	}

	public void removeEntryAt(int index) {
		
		entries.remove(index);
		fireTableRowsDeleted(index, index);
	}
	
	@Override
	public int getRowCount() {
		
		return entries.size();
	}

	@Override
	public int getColumnCount() {
		
		return columnNames.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		
		Object ret;
		
		switch(columnIndex) {
			
			case 0:
				ret = entries.get(rowIndex).getName();
			break;
			
			case 1:
				ret = entries.get(rowIndex).getDataType().getColType();
			break;
				
			case 2:
				ret = entries.get(rowIndex).getDataType().getNn();
			break;
				
			default:
				ret = new Object();
			break;
		}
		
		return ret;
	}

	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		
		switch(columnIndex) {
			
			case 0:
				entries.get(rowIndex).setName((String) value);
			break;
			
			case 1:
				entries.get(rowIndex).getDataType().setColType((ColumnTypes) value);
			break;
				
			case 2:
				entries.get(rowIndex).getDataType().setNn((Boolean) value);
			break;
		}
		
		fireTableCellUpdated(rowIndex, columnIndex);
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) { 
		
		//if(columnIndex == 1)
		return false;
		
		//return true; 
	}
	
	@Override
	public Class getColumnClass(int c) {
		
		return getValueAt(0, c).getClass();
	}
	
}
