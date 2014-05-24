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

package org.tinyuml.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.MissingResourceException;
import javax.swing.DefaultComboBoxModel;
import org.firebirdvisualizationtool.database.firebird.IndexOrders;
import org.firebirdvisualizationtool.database.firebird.IndexTypes;

/**
 *
 * @author Tomáš Ligenza
 */
public class Index extends DefaultNamedElement {
	
	private static final long serialVersionUID = -5337919279209408819L;
	
	private IndexTypes iIndexType;
	private IndexOrders iIndexOrder;
	
	private List<IndexColumn> cols = new LinkedList<IndexColumn>();
	
	private static Index prototype;
	
	public static Index getPrototype() {
		
		if (prototype == null) prototype = new Index();
		
		return prototype;
	}

	private Index() { }
	
	@Override
	public Object clone() {
		
		Index cloned = null;
		cloned = (Index) super.clone();
	
		cloned.cols = cols;
		cloned.iIndexOrder = iIndexOrder;
		cloned.iIndexType = iIndexType;
		
		return cloned;
	}
	
	public Index create(String sName, IndexTypes iIndexType) {
		
		Index cloned = null;
		cloned = (Index) super.clone();
		
		cloned.cols = new LinkedList<IndexColumn>();
		cloned.iIndexOrder = null;
		
		cloned.setName(sName);
		cloned.iIndexType = iIndexType;
		
		return cloned;
	}
	
	public void setCols(List<IndexColumn> indexCols) {
		
		cols = indexCols;
	}
	
	public void addCol(Boolean check, String name, IndexOrders oOrder) {
		
		cols.add(new IndexColumn(check, name, oOrder));
	}
	
	public List<IndexColumn> getCols() {
		
		return cols;
	}
	
	public boolean isColByName(String name) {
		
		for (IndexColumn col : cols) {
			
			if(col.getName().equals(name))
				return true;
		}
		
		return false;
	}
	
	public IndexColumn getColByName(String name) {
		
		for (IndexColumn col : cols) {
			
			if(col.getName().equals(name))
				return col;
		}
		
		throw new MissingResourceException("Nebyl nalezen požadovaný objekt.", "IndexCol", name);
	}
	
	public boolean checkValid() {
		
		return !getName().isEmpty() && !getCols().isEmpty();
	}
	
	public static DefaultComboBoxModel getTableIndexTypes() {
		
		DefaultComboBoxModel model = new DefaultComboBoxModel();
		
		for(IndexTypes type : IndexTypes.values()) {
			
			model.addElement(type);
		}
		
		return model;
	}
	
	public static DefaultComboBoxModel getTableIndexColOrders() {
		
		DefaultComboBoxModel model = new DefaultComboBoxModel();
		
		for(IndexOrders type : IndexOrders.values()) {
			
			model.addElement(type);
		}
		
		return model;
	}

	public IndexTypes getiIndexType() {
		return iIndexType;
	}

	public void setiIndexType(IndexTypes iIndexType) {
		this.iIndexType = iIndexType;
	}

	public IndexOrders getiIndexOrder() {
		return iIndexOrder;
	}

	public void setiIndexOrder(IndexOrders iIndexOrder) {
		this.iIndexOrder = iIndexOrder;
	}
	
	@Override
	public String toString() {
		
		return getName() + ' ' + getiIndexType().name();
	}
	
	public String getCreateSql(String tableName) {
		
		String script = "";
		IndexOrders order = getiIndexOrder();
		
		if(order != null && order.equals(IndexOrders.DESC))
			script = "CREATE DESCENDING INDEX " + getName() + " ON " + tableName;
		else
			script = "CREATE INDEX " + getName() + " ON " + tableName;
			
			
		List<String> cols = new ArrayList<String>();
		
		for(IndexColumn col : getCols()) {
			
			if(col.getChecked())
				cols.add(col.getName());
		}
		
		StringBuilder builder = new StringBuilder();
		builder.append( cols.remove(0));

		for( String s : cols) {
			builder.append( ",");
			builder.append( s);
		}
		
		script = script + "(" + builder.toString() + ");";
		
		return script;
	}
}
