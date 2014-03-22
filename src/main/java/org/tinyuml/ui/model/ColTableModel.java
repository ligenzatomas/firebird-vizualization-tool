/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tinyuml.ui.model;

import java.util.LinkedList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.table.AbstractTableModel;
import org.firebirdvisualizationtool.database.firebird.DatabaseColumnTypes;
import org.tinyuml.model.UmlTableCol;

/**
 *
 * @author cml
 */
public class ColTableModel extends AbstractTableModel {

	private String[] columnNames = {"Název sloupce", "Datový typ", "NN", "PK", "UQ"};
	
	private List<UmlTableCol> entries = new LinkedList<UmlTableCol>();
	
	public String getColumnName(int index) {
		
		return columnNames[index];
	}
	
	public void addEntry(UmlTableCol col) {
		
		int size = entries.size();
		
		entries.add(col);
		fireTableRowsInserted(size, size);
	}
	
	public List<UmlTableCol> getEntries() {
		
		return entries;
	}
	
	public boolean isColByName(String name) {
		
		for (UmlTableCol col : entries) {
			
			if(col.getName() == name)
				return true;
		}
		
		return false;
	}
	
	public void moveUpEntry(int index) {
		
		if(index < getRowCount()) {

			UmlTableCol entryDown = entries.remove(index);
			UmlTableCol entryUp = entries.remove(index - 1);

			entries.add(index - 1, entryDown);
			entries.add(index, entryUp);

			fireTableDataChanged();
		}
	}
	
	public void moveDownEntry(int index) {
		
		if(index < (getRowCount() - 1)) {
		
			UmlTableCol entryDown = entries.remove(index);
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
				entries.get(rowIndex).setColType((DatabaseColumnTypes) value);
			break;
				
			case 2:
				entries.get(rowIndex).setNn((Boolean) value);
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
