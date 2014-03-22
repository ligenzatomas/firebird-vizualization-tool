/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tinyuml.ui.model;

import java.util.LinkedList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.tinyuml.model.UmlTableForeignKey;

/**
 *
 * @author cml
 */
public class ForeignKeyTableModel extends AbstractTableModel {
	
	private String[] columnNames = {"Název cizího klíče", "Reference na tabulku", "Update", "Delete"};
	
	private List<UmlTableForeignKey> entries = new LinkedList<UmlTableForeignKey>();
	
	public String getColumnName(int index) {
		
		return columnNames[index];
	}
	
	public void addEntry(UmlTableForeignKey col) {
		
		int size = entries.size();
		
		entries.add(col);
		fireTableRowsInserted(size, size);
	}
	
	public UmlTableForeignKey getEntry(int index) {
		
		if(index < 0 || index >= getRowCount())
			throw new ArrayIndexOutOfBoundsException("Špatně zadán index pole (" + index + ").");
		else
			return entries.get(index);
			
	}
	
	public List<UmlTableForeignKey> getEntries() {
		
		return entries;
	}
	
	public void moveUpEntry(int index) {
		
		if(index < getRowCount()) {

			UmlTableForeignKey entryDown = entries.remove(index);
			UmlTableForeignKey entryUp = entries.remove(index - 1);

			entries.add(index - 1, entryDown);
			entries.add(index, entryUp);

			fireTableDataChanged();
		}
	}
	
	public void moveDownEntry(int index) {
		
		if(index < (getRowCount() - 1)) {
		
			UmlTableForeignKey entryDown = entries.remove(index);
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
				entries.get(rowIndex).setOnUpdateAction((UmlTableForeignKey.action) value);
			break;
				
			case 3:
				entries.get(rowIndex).setOnDeleteAction((UmlTableForeignKey.action) value);
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
