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

/**
 *
 * @author Tomáš Ligenza
 */
public class View extends DefaultNamedElement {
	
	private static View prototype;
	private static final long serialVersionUID = -2045800942336579861L;
	
	private String source = 
		"CREATE VIEW name ( view_column, ...)\n" +
		"AS\n" +
		"/* write select statement here */\n" +
		"WITH CHECK OPTION;";
	
	public static View getPrototype() {
		
		if (prototype == null) prototype = new View();
		
		return prototype;
	}

	protected View() { }
	
	@Override
	public Object clone() {
		
		View cloned = null;
		cloned = (View) super.clone();
		
		return cloned;
	}
	
	public View create(String sName) {
		
		View cloned = null;
		cloned = (View) super.clone();
		
		cloned.setName(sName);
		
		return cloned;
	}
	
	public void copy(View aView) {
		
		this.source = aView.getSource();
		
		this.setName(aView.getName());
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}
	
	public String getCreateSql() {
		
		return getSource();
	}
	
	public String getDropSql() {
		
		return "DROP VIEW " + getName() + ";";
	}
}
