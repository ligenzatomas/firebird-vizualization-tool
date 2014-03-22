/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tinyuml.ui.model;

import java.util.LinkedList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.tinyuml.model.Sequence;

/**
 *
 * @author cml
 */
public class SequenceModel extends AbstractTableModel {
	
	private static final long serialVersionUID = 6671735259027235792L;

	private String[] columnNames = {"Název sekvence", "Inicializační hodnota"};
	
	private List<Sequence> entries = new LinkedList<Sequence>();
	
	@Override
	public String getColumnName(int index) {
		
		return columnNames[index];
	}
	
	public void addEntry(Sequence col) {
		
		int size = entries.size();
		
		entries.add(col);
		fireTableRowsInserted(size, size);
	}
	
	public List<Sequence> getEntries() {
		
		return entries;
	}
	
	public boolean isColByName(String name) {
		
		for (Sequence col : entries) {
			
			if(col.getName() == name)
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
				ret = entries.get(rowIndex).getInitValue();
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
				entries.get(rowIndex).setInitValue((Long) value);
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