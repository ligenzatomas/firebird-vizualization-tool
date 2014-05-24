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
public final class Sequence extends DefaultNamedElement {
	
	private static final long serialVersionUID = -7504821765834417624L;
	
	private long initValue = 0;
	
	private static Sequence prototype;
	
	public static Sequence getPrototype() {
		
		if (prototype == null) prototype = new Sequence();
		
		return prototype;
	}

	private Sequence() { }
	
	@Override
	public Object clone() {
		
		Sequence cloned = null;
		cloned = (Sequence) super.clone();
		
		return cloned;
	}
	
	public Sequence create(String sName) {
		
		Sequence cloned = null;
		cloned = (Sequence) super.clone();
		
		cloned.setName(sName);
		
		return cloned;
	}

	public long getInitValue() {
		
		return initValue;
	}

	public void setInitValue(long value) {
		
		this.initValue = value;
	}
	
	public boolean checkValid() {
		
		return !getName().isEmpty();
	}
	
	public String getCreateSql() {
		
		String script = "CREATE GENERATOR " + getName() + ";";
		
		if(getInitValue() != 0)
			script = script + "\n SET GENERATOR " + getName() + " TO " + getInitValue() + ";";
		
		return script;
	}
	
	public String getDropSql() {
		
		return "DROP GENERATOR " + getName() + ";";
	}
}