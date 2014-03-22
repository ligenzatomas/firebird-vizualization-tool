/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tinyuml.model;

import javax.swing.DefaultComboBoxModel;
import org.firebirdvisualizationtool.database.firebird.DatabaseColumnTypes;
import static org.firebirdvisualizationtool.database.firebird.DatabaseColumnTypes.ARRAY;
import static org.firebirdvisualizationtool.database.firebird.DatabaseColumnTypes.BIGINT;
import static org.firebirdvisualizationtool.database.firebird.DatabaseColumnTypes.BLOB;
import static org.firebirdvisualizationtool.database.firebird.DatabaseColumnTypes.CHAR;
import static org.firebirdvisualizationtool.database.firebird.DatabaseColumnTypes.DATE;
import static org.firebirdvisualizationtool.database.firebird.DatabaseColumnTypes.DECIMAL;
import static org.firebirdvisualizationtool.database.firebird.DatabaseColumnTypes.DOUBLE_PRECISION;
import static org.firebirdvisualizationtool.database.firebird.DatabaseColumnTypes.FLOAT;
import static org.firebirdvisualizationtool.database.firebird.DatabaseColumnTypes.INTEGER;
import static org.firebirdvisualizationtool.database.firebird.DatabaseColumnTypes.NUMERIC;
import static org.firebirdvisualizationtool.database.firebird.DatabaseColumnTypes.SMALLINT;
import static org.firebirdvisualizationtool.database.firebird.DatabaseColumnTypes.TIME;
import static org.firebirdvisualizationtool.database.firebird.DatabaseColumnTypes.TIMESTAMP;
import static org.firebirdvisualizationtool.database.firebird.DatabaseColumnTypes.VARCHAR;

/**
 *
 * @author cml
 */
public class Domain extends DefaultNamedElement {
	
	private static final long serialVersionUID = -1125283840459663013L;
	
	private DatabaseColumnTypes colType;
	private Boolean nn;
	
	private int size = 0;
	private int scale = 0;
	
	private String characterSet = "";
	private String collate = "";
	
	private String defaultValue = "";
	private String check = "";
	
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
		
		return cloned;
	}
	
	public Domain create(String sName, DatabaseColumnTypes cColType, boolean notNull) {
		
		Domain cloned = null;
		cloned = (Domain) super.clone();
		
		cloned.setName(sName);
		cloned.setColType(cColType);
		cloned.setNn(notNull);
		
		return cloned;
	}
	
	public void repairInput() {
		
		DatabaseColumnTypes type = getColType();
		
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
		
		if(getName().isEmpty()
			|| getColType() == null)
			
			ret = false;
		
		switch(getColType()) {
			
			case CHAR: case VARCHAR:
				if(getSize() < 1
					|| getCharacterSet().isEmpty()
					|| getCollate().isEmpty())
					
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
	
	public DatabaseColumnTypes getColType() {
		return colType;
	}

	public void setColType(DatabaseColumnTypes colType) {
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
		
		int size = getSize();
		
		if(scale > size)
			scale = size;
		
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
	
	public static DefaultComboBoxModel<DatabaseColumnTypes> getTableColTypes() {
		
		DefaultComboBoxModel model = new DefaultComboBoxModel();
		
		for (DatabaseColumnTypes type : DatabaseColumnTypes.values()) {
			model.addElement(type);
		}
		
		return model;
	}
	
	@Override
	public String toString() {
		
		String notNull = "";
		
		if(getNn())
			notNull = "NOT NULL";
		
		return getName() + " " + getColType().name() + " " + notNull;
	}
}
