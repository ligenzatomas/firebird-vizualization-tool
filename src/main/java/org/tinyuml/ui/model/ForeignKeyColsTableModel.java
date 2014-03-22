/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tinyuml.ui.model;

import java.util.LinkedList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author cml
 */
public class ForeignKeyColsTableModel extends AbstractTableModel {
	
	private String[] columnNames = { "", "Sloupec"};
	
	private List<ForeignKeyCol> entries = new LinkedList<ForeignKeyCol>();
	
	public String getColumnName(int index) {
		
		return columnNames[index];
	}
	
	public void clearEntries() {
		
		entries.clear();
		fireTableDataChanged();
	}
	
	public void addEntry(Boolean check, String col) {
		
		int size = entries.size();
		
		entries.add(new ForeignKeyCol(check, col));
		fireTableRowsInserted(size, size);
	}
	
	public List<ForeignKeyCol> getEntries() {
		
		return entries;
	}
	
	public boolean isForeignKeyColByName(String name) {
		
		for(ForeignKeyCol col : entries) {
			
			if(col.getName() == name)
				return true;
		}
		
		return false;
	}
	
	public void moveUpEntry(int index) {
		
		if(index < getRowCount()) {
			
			ForeignKeyCol entryDown = entries.remove(index);
			ForeignKeyCol entryUp = entries.remove(index - 1);

			entries.add(index - 1, entryDown);
			entries.add(index, entryUp);

			fireTableDataChanged();
		}
	}
	
	public void moveDownEntry(int index) {
		
		if(index < (getRowCount() - 1)) {
			
			ForeignKeyCol entryDown = entries.remove(index);
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
		}
		
		fireTableCellUpdated(rowIndex, columnIndex);
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) { 
		
		if(columnIndex == 0)
			return true; 
		else
			return false;
	}
	
	@Override
	public Class getColumnClass(int c) {
		
		return getValueAt(0, c).getClass();
	}
}
