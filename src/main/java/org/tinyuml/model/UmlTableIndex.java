/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tinyuml.model;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import javax.swing.DefaultComboBoxModel;
import org.tinyuml.ui.model.IndexCol;

/**
 *
 * @author cml
 */
public class UmlTableIndex extends DefaultNamedElement {
	
	public enum indexType	{ INDEX, PRIMARY, UNIQUE };
	public enum order		{ ASC, DESC };
	
	private indexType iIndexType;
	private List<IndexCol> lCols = new LinkedList<IndexCol>();
	
	private static UmlTableIndex prototype;
	
	public static UmlTableIndex getPrototype() {
		
		if (prototype == null) prototype = new UmlTableIndex();
		
		return prototype;
	}

	private UmlTableIndex() { }
	
	@Override
	public Object clone() {
		
		UmlTableIndex cloned = null;
		cloned = (UmlTableIndex) super.clone();
		
		return cloned;
	}
	
	public UmlTableIndex create(String sName, indexType iIndexType) {
		
		UmlTableIndex cloned = null;
		cloned = (UmlTableIndex) super.clone();
		
		cloned.setName(sName);
		cloned.iIndexType = iIndexType;
		
		return cloned;
	}
	
	public void setCols(List<IndexCol> indexCols) {
		
		lCols = indexCols;
	}
	
	public void addCol(Boolean check, String name, order oOrder) {
		
		lCols.add(new IndexCol(check, name, oOrder));
	}
	
	public List<IndexCol> getCols() {
		
		return lCols;
	}
	
	public boolean isColByName(String name) {
		
		for (IndexCol col : lCols) {
			
			if(col.getName() == name)
				return true;
		}
		
		return false;
	}
	
	public IndexCol getColByName(String name) {
		
		for (IndexCol col : lCols) {
			
			if(col.getName() == name)
				return col;
		}
		
		throw new MissingResourceException("Nebyl nalezen požadovaný objekt.", "IndexCol", name);
	}
	
	public boolean checkValid() {
		
		return !getName().isEmpty() && !getCols().isEmpty();
	}
	
	public static DefaultComboBoxModel getTableIndexTypes() {
		
		DefaultComboBoxModel model = new DefaultComboBoxModel();
		
		for(indexType type : indexType.values()) {
			
			model.addElement(type);
		}
		
		return model;
	}
	
	public static DefaultComboBoxModel getTableIndexColOrders() {
		
		DefaultComboBoxModel model = new DefaultComboBoxModel();
		
		for(order type : order.values()) {
			
			model.addElement(type);
		}
		
		return model;
	}

	public indexType getiIndexType() {
		return iIndexType;
	}

	public void setiIndexType(indexType iIndexType) {
		this.iIndexType = iIndexType;
	}
	
	@Override
	public String toString() {
		
		return getName() + ' ' + getiIndexType().name();
	}
}
