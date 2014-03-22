/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tinyuml.umldraw.eer;

import java.util.LinkedList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import org.tinyuml.model.UmlTable;

/**
 *
 * @author cml
 */
public class TablesContainer {
	
	private List<UmlTable> tables = new LinkedList<UmlTable>();
	
	private int counter = 1;
	
	public void addTable(UmlTable table) {
		
		tables.add(table);
	}
	
	public int getCounterValue() {
		
		return counter++;
	}
	
	public void removeTable(UmlTable table) {
		
		tables.remove(table);
	}
	
	public void removeTableByName(String name) {
		
		for(UmlTable table : tables) {
			
			if(table.getName().equals(name))
				tables.remove(table);
		}
	}
	
	public int getSize() {
		
		return tables.size();
	}
	
	public void changeTable(UmlTable table) {
		
		int index = tables.indexOf(indexTableByName(table.getName()));
		
		if(index >= 0 && index < tables.size()) {
			
			tables.remove(index);
			tables.add(index, table);
		}
	}
	
	public boolean existsTableByName(String name) {
		
		return (indexTableByName(name) != -1);
	}
	
	public int indexTableByName(String name) {
		
		int index = -1;
		
		for(UmlTable table : tables) {
			
			if(table.getName().equals(name)) {
				
				index = tables.indexOf(table);
				break;
			}
		}
		
		return index;
	}
	
	public void changeTableName(String oldName, String newName) {
		
		int index = indexTableByName(oldName);
		
		if(index >= 0 && index < tables.size())
			tables.get(index).setName(newName);
	}
	
	public List<UmlTable> getTables() {
		
		return tables;
	}
	
	public DefaultComboBoxModel getTablesComboBoxModel() {
		
		DefaultComboBoxModel model = new DefaultComboBoxModel();
		
		for(UmlTable table : tables) {
			
			model.addElement(table.getName());
		}
		
		return model;
	}
}
