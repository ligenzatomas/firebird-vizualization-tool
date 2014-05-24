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
import org.firebirdvisualizationtool.database.firebird.ForeignKeyActions;
import org.tinyuml.model.ForeignKey;
import org.tinyuml.util.ApplicationResources;

/**
 *
 * @author Tomáš Ligenza
 */
public class ForeignKeyTableModel extends AbstractTableModel {
	
	private static final long serialVersionUID = 7790111380855684907L;
	
	private final String[] columnNames = {
		ApplicationResources.getInstance().getString("database.model.foreignkey.name")
		, ApplicationResources.getInstance().getString("database.model.foreignkey.reftable")
		, ApplicationResources.getInstance().getString("database.model.foreignkey.update")
		, ApplicationResources.getInstance().getString("database.model.foreignkey.delete")};
	
	private final List<ForeignKey> entries = new LinkedList<ForeignKey>();
	
	@Override
	public String getColumnName(int index) {
		
		return columnNames[index];
	}
	
	public void addEntry(ForeignKey col) {
		
		int size = entries.size();
		
		entries.add(col);
		fireTableRowsInserted(size, size);
	}
	
	public ForeignKey getEntry(int index) {
		
		if(index < 0 || index >= getRowCount())
			throw new ArrayIndexOutOfBoundsException(ApplicationResources.getInstance().getString("error.bad.index.message"));
		else
			return entries.get(index);
			
	}
	
	public List<ForeignKey> getEntries() {
		
		return entries;
	}
	
	public void moveUpEntry(int index) {
		
		if(index < getRowCount()) {

			ForeignKey entryDown = entries.remove(index);
			ForeignKey entryUp = entries.remove(index - 1);

			entries.add(index - 1, entryDown);
			entries.add(index, entryUp);

			fireTableDataChanged();
		}
	}
	
	public void moveDownEntry(int index) {
		
		if(index < (getRowCount() - 1)) {
		
			ForeignKey entryDown = entries.remove(index);
			entries.add(index + 1, entryDown);

			fireTableDataChanged();
		}
	}

	public void removeEntryAt(int index) {
		
		entries.remove(index);
		fireTableRowsDeleted(index, index);
	}
	
	public boolean isEmpty() {
		
		return entries.isEmpty();
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
				ret = entries.get(rowIndex).getReferencedTable();
			break;
				
			case 2:
				ret = entries.get(rowIndex).getOnUpdateAction();
			break;
				
			case 3:
				ret = entries.get(rowIndex).getOnDeleteAction();
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
				entries.get(rowIndex).setReferencedTable((String) value);
			break;
				
			case 2:
				entries.get(rowIndex).setOnUpdateAction((ForeignKeyActions) value);
			break;
				
			case 3:
				entries.get(rowIndex).setOnDeleteAction((ForeignKeyActions) value);
			break;
		}
		
		fireTableCellUpdated(rowIndex, columnIndex);
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) { return true; }
	
	@Override
	public Class getColumnClass(int c) {
		
		return getValueAt(0, c).getClass();
	}
}
