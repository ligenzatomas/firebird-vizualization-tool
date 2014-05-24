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

package org.firebirdvisualizationtool.database.firebird;

import java.io.Serializable;
import javax.swing.DefaultComboBoxModel;

/**
 *
 * @author Tomáš Ligenza
 */
public class DataType implements Serializable {
	
	private static final long serialVersionUID = 9018048308971539164L;
	
	private ColumnTypes colType = ColumnTypes.INTEGER;
	private Boolean nn = false;
	
	private int size = 0;
	private int scale = 0;
	
	private String characterSet = "";
	private String collate = "";
	
	private String defaultValue = "";
	private String check = "";
	
	public DataType() {}
	
	public void repairInput() {
		
		ColumnTypes type = getColType();
		
		switch(type) {
			
			case CHAR: case VARCHAR:
				setScale(0);
			break;
				
			case INTEGER: case SMALLINT: case BIGINT: 
			case FLOAT: case DOUBLE_PRECISION: 
			case DATE: case TIME: case TIMESTAMP:
			case ARRAY: case BLOB:
				setSize(0);
				setScale(0);
				setCharacterSet("");
				setCollate("");
			break;
				
			case NUMERIC: case DECIMAL:
				repairSize(size);
				repairScale(scale);
				setCharacterSet("");
				setCollate("");
			break;
		}
	}
	
	public boolean checkValid() {
		
		boolean ret = true;
		
		if(getColType() == null)
			
			ret = false;
		
		switch(getColType()) {
			
			case CHAR: case VARCHAR:
				if(getSize() < 1)
					
					ret = false;
			break;
				
			case NUMERIC: case DECIMAL:
				if(getSize() < 1
					|| getScale() < 1
					|| getSize() > 18
					|| getScale() > 18
					|| getSize() < getScale())
				
					ret = false;
			break;
		}
		
		return ret;
	}
	
	public ColumnTypes getColType() {
		return colType;
	}

	public void setColType(ColumnTypes colType) {
		this.colType = colType;
	}

	public Boolean getNn() {
		return nn;
	}

	public void setNn(Boolean nn) {
		this.nn = nn;
	}
	
	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		
		repairSize(size);
		
		this.size = size;
	}
	
	public int repairSize(int size) {
		
		if(size < 1)
			size = 1;
		else if (size > 18)
			size = 18;
		
		return size;
	}

	public int getScale() {
		return scale;
	}

	public void setScale(int scale) {
		
		repairScale(scale);
		
		this.scale = scale;
	}
	
	public int repairScale(int scale) {
		
		if(scale < 0)
			scale = 0;
		
		int aSize = getSize();
		
		if(scale > aSize)
			scale = aSize;
		
		return scale;
	}

	public String getCharacterSet() {
		return characterSet;
	}

	public void setCharacterSet(String characterSet) {
		this.characterSet = characterSet;
	}

	public String getCollate() {
		return collate;
	}

	public void setCollate(String collate) {
		this.collate = collate;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getCheck() {
		return check;
	}

	public void setCheck(String check) {
		this.check = check;
	}
	
	public static DefaultComboBoxModel<ColumnTypes> getTableColTypes() {
		
		DefaultComboBoxModel model = new DefaultComboBoxModel();
		
		for (ColumnTypes type : ColumnTypes.values()) {
			model.addElement(type);
		}
		
		return model;
	}
}
