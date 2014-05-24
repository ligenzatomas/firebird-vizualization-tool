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
import org.tinyuml.model.Domain;
import org.tinyuml.util.ApplicationResources;

/**
 *
 * @author Tomáš Ligenza
 */
public class DomainModel extends AbstractTableModel {
	
	private static final long serialVersionUID = -2790925726057560399L;
	
	private final String[] columnNames = {
		ApplicationResources.getInstance().getString("database.model.domain.name")
		, ApplicationResources.getInstance().getString("database.model.coltable.type")
		, ApplicationResources.getInstance().getString("database.model.coltable.nn")};
	
	private final List<Domain> entries = new LinkedList<Domain>();
	
	@Override
	public String getColumnName(int index) {
		
		return columnNames[index];
	}
	
	public void addEntry(Domain dom) {
		
		int size = entries.size();
		
		entries.add(dom);
		fireTableRowsInserted(size, size);
	}
	
	public List<Domain> getEntries() {
		
		return entries;
	}
	
	public boolean isColByName(String name) {
		
		for (Domain col : entries) {
			
			if(col.getName().equals(name))
				return true;
		}
		
		return false;
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
				ret = entries.get(rowIndex).getColType();
			break;
				
			case 2:
				ret = entries.get(rowIndex).getNn();
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
				entries.get(rowIndex).setColType((ColumnTypes) value);
			break;
				
			case 2:
				entries.get(rowIndex).setNn((Boolean) value);
			break;
		}
		
		fireTableCellUpdated(rowIndex, columnIndex);
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) { return false; }
	
	@Override
	public Class getColumnClass(int c) {
		
		return getValueAt(0, c).getClass();
	}	
}
