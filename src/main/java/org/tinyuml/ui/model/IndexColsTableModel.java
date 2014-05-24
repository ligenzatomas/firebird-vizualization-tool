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

import org.tinyuml.model.IndexColumn;
import java.util.LinkedList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.firebirdvisualizationtool.database.firebird.IndexOrders;
import org.tinyuml.util.ApplicationResources;

/**
 *
 * @author Tomáš Ligenza
 */
public class IndexColsTableModel extends AbstractTableModel {
	
	private static final long serialVersionUID = -3694142998063548137L;

	private final String[] columnNames = { 
		""
		, ApplicationResources.getInstance().getString("database.model.indexcol.column")
		, ApplicationResources.getInstance().getString("database.model.indexcol.way")};
	
	private final List<IndexColumn> entries = new LinkedList<IndexColumn>();
	
	@Override
	public String getColumnName(int index) {
		
		return columnNames[index];
	}
	
	public void clearEntries() {
		
		entries.clear();
		fireTableDataChanged();
	}
	
	public void addEntry(Boolean check, String col, IndexOrders order) {
		
		int size = entries.size();
		
		entries.add(new IndexColumn(check, col, order));
		fireTableRowsInserted(size, size);
	}
	
	public List<IndexColumn> getEntries() {
		
		return entries;
	}
	
	public boolean isIndexColByName(String name) {
		
		for(IndexColumn col : entries) {
			
			if(col.getName() == name)
				return true;
		}
		
		return false;
	}
	
	public void moveUpEntry(int index) {
		
		if(index < getRowCount()) {

			IndexColumn entryDown = entries.remove(index);
			IndexColumn entryUp = entries.remove(index - 1);

			entries.add(index - 1, entryDown);
			entries.add(index, entryUp);

			fireTableDataChanged();
		}
	}
	
	public void moveDownEntry(int index) {
		
		if(index < (getRowCount() - 1)) {
		
			IndexColumn entryDown = entries.remove(index);
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
				ret = entries.get(rowIndex).getChecked();
			break;
			
			case 1:
				ret = entries.get(rowIndex).getName();
			break;
			
			case 2:
				ret = entries.get(rowIndex).getoOrder();
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
				entries.get(rowIndex).setChecked((Boolean) value);
			break;
			
			case 1:
				entries.get(rowIndex).setName((String) value);
			break;
			
			case 2:
				entries.get(rowIndex).setoOrder((IndexOrders) value);
			break;
		}
		
		fireTableCellUpdated(rowIndex, columnIndex);
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) { 
		
		if(columnIndex == 0 || columnIndex == 2)
			return true; 
		else
			return false;
	}
	
	@Override
	public Class getColumnClass(int c) {
		
		return getValueAt(0, c).getClass();
	}
	
}
