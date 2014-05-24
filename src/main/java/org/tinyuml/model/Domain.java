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
 * @author Tomáš Ligenza
 */
public class Domain extends DefaultNamedElement {
	
	public static final String NULL_DOMAIN = "-";
	private static final long serialVersionUID = 774906248953603996L;
	
	private DataType dataType = new DataType();
	
	private static Domain prototype;
	
	public static Domain getPrototype() {
		
		if (prototype == null) prototype = new Domain();
		
		return prototype;
	}

	protected Domain() { }
	
	@Override
	public Object clone() {
		
		Domain cloned = null;
		cloned = (Domain) super.clone();
		
		cloned.dataType = dataType;
		
		return cloned;
	}
	
	public Domain create(String sName, ColumnTypes cColType, boolean notNull) {
		
		Domain cloned = null;
		cloned = (Domain) clone();
		
		cloned.dataType = new DataType();
		
		cloned.setName(sName);
		cloned.getDataType().setColType(cColType);
		cloned.getDataType().setNn(notNull);
		
		return cloned;
	}

	public DataType getDataType() {
		return dataType;
	}

	public void setDataType(DataType dataType) {
		this.dataType = dataType;
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
		String colType = "";
		
		if(getDataType().getNn())
			notNull = "NOT NULL";
		
		if(getColType() != null)
			colType = getColType().name();
		
		return getName() + " " + colType + " " + notNull;
	}
	
	public String getDropSql() {
		
		return "DROP DOMAIN " + getName() + ";";
	}
	
	public String getCreateSql() {
		
		String aDataType	= "";
		String character	= "";
		String collate		= "";
		String defaultValue = "";
		String check		= "";
		String notNull		= "";
		
		String sql = "CREATE DOMAIN " + getName() + " AS ";
		
		switch(getColType()) {
			
			case CHAR: case VARCHAR:
				aDataType = " " + getColType().name() + "(" + getSize() + ")";
				
				if(getCharacterSet() != null && !getCharacterSet().isEmpty())
					character = " CHARACTER SET " + getCharacterSet();
				if(getCollate() != null && !getCollate().isEmpty())
					collate = " COLLATE " + getCollate();
			break;
				
			case INTEGER: case SMALLINT: case BIGINT: 
			case FLOAT: case DOUBLE_PRECISION: 
			case DATE: case TIME: case TIMESTAMP:
			case ARRAY: case BLOB:
				aDataType = " " + getColType().name();
			break;
				
			case NUMERIC: case DECIMAL:
				aDataType = " " + getColType().name() + "(" + getSize() + "," + getScale() + ")";
			break;
		}
		
		if(!getDefaultValue().isEmpty())
			defaultValue = " DEFAULT " + getDefaultValue();
		
		if(!getCheck().isEmpty())
			check = " CHECK (" + getCheck() + ")";
		
		if(getNn())
			notNull = " NOT NULL";
		
		sql = sql.concat(aDataType + character + defaultValue + check + notNull + collate + ";");
		
		return sql;
	}
}
