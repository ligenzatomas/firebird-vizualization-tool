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
public class Procedure extends DefaultNamedElement {
	
	private static Procedure prototype;
	private static final long serialVersionUID = -3173267165926273949L;
	
	private String source = 
		"SET TERM ^ ;\n" +
		"\n" +
		"CREATE PROCEDURE name \n" +
		" ( input_parameter_name < datatype>, ... ) \n" +
		"RETURNS \n" +
		" ( output_parameter_name < datatype>, ... )\n" +
		"AS \n" +
		"DECLARE VARIABLE variable_name < datatype>; \n" +
		"BEGIN\n" +
		"  /* write your code here */ \n" +
		"END^\n" +
		"\n" +
		"SET TERM ; ^";
	
	public static Procedure getPrototype() {
		
		if (prototype == null) prototype = new Procedure();
		
		return prototype;
	}

	protected Procedure() { }
	
	@Override
	public Object clone() {
		
		Procedure cloned = null;
		cloned = (Procedure) super.clone();
		
		return cloned;
	}
	
	public Procedure create(String sName) {
		
		Procedure cloned = null;
		cloned = (Procedure) super.clone();
		
		cloned.setName(sName);
		
		return cloned;
	}

	public void copy(Procedure aProcedure) {
		
		this.source = aProcedure.getSource();
		
		this.setName(aProcedure.getName());
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
		return "DROP PROCEDURE " + getName() + ";";
	}
}
