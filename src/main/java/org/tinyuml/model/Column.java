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

import org.firebirdvisualizationtool.database.firebird.ColumnTypes;
import org.firebirdvisualizationtool.database.firebird.DataType;

/**
 *
 * @author cml
 */
public class Column extends DefaultNamedElement {
	
	private static final long serialVersionUID = 8295271256791079222L;
	
	private DataType dataType = new DataType();
	private Domain domain;
	
	private static Column prototype;
	
	public static Column getPrototype() {
		
		if (prototype == null) prototype = new Column();
		
		return prototype;
	}

	protected Column() { }
	
	@Override
	public Object clone() {
		
		Column cloned = null;
		cloned = (Column) super.clone();
		
		cloned.dataType = dataType;
		cloned.domain = domain;
		
		return cloned;
	}
	
	public Column create(String sName, ColumnTypes cColType, boolean notNull) {
		
		Column cloned = null;
		cloned = (Column) clone();
		
		cloned.dataType = new DataType();
		cloned.domain = null;
		
		cloned.setName(sName);
		cloned.getDataType().setColType(cColType);
		cloned.getDataType().setNn(notNull);
		
		return cloned;
	}
	
	public DataType getDataType() {
		
		if(domain == null)
			return dataType;
		else
			return domain.getDataType();
	}

	public void setDataType(DataType dataType) {
		
		this.dataType = dataType;
	}

	public Domain getDomain() {
		return domain;
	}

	public void setDomain(Domain domain) {
		this.domain = domain;
	}
	
	public void repairInput() {
		
		getDataType().repairInput();
	}
	
	public boolean checkValid() {
		
		return getName() != null && !getName().isEmpty() && getDataType().checkValid();
	}
	
	public ColumnTypes getColType() {
		return getDataType().getColType();
	}

	public void setColType(ColumnTypes colType) {
		getDataType().setColType(colType);
	}

	public Boolean getNn() {
		return getDataType().getNn();
	}

	public void setNn(Boolean nn) {
		getDataType().setNn(nn);
	}
	
	public int getSize() {
		return getDataType().getSize();
	}

	public void setSize(int size) {
		
		repairSize(size);
		
		getDataType().setSize(size);
	}
	
	public int repairSize(int size) {
		
		return getDataType().repairSize(size);
	}

	public int getScale() {
		return getDataType().getScale();
	}

	public void setScale(int scale) {
		
		repairScale(scale);
		
		getDataType().setScale(scale);
	}
	
	public int repairScale(int scale) {
		
		return getDataType().repairScale(scale);
	}

	public String getCharacterSet() {
		return getDataType().getCharacterSet();
	}

	public void setCharacterSet(String characterSet) {
		getDataType().setCharacterSet(characterSet);
	}

	public String getCollate() {
		return getDataType().getCollate();
	}

	public void setCollate(String collate) {
		getDataType().setCollate(collate);
	}

	public String getDefaultValue() {
		return getDataType().getDefaultValue();
	}

	public void setDefaultValue(String defaultValue) {
		getDataType().setDefaultValue(defaultValue);
	}

	public String getCheck() {
		return getDataType().getCheck();
	}

	public void setCheck(String check) {
		getDataType().setCheck(check);
	}
	
	@Override
	public String toString() {
		
		String notNull = "";
		
		if(getDataType().getNn())
			notNull = "NOT NULL";
		
		return getName() + " " + getDataType().getColType().name() + " " + notNull;
	}
}
