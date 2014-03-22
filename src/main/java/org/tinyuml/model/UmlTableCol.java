/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tinyuml.model;

import org.firebirdvisualizationtool.database.firebird.DatabaseColumnTypes;

/**
 *
 * @author cml
 * 
 * TODO dodelat domeny a jejich editace
 * TODO dodelat generatory
 */
public final class UmlTableCol extends Domain {
	
	private static final long serialVersionUID = -6014439005086141927L;
	
	private String domain;
	
	private static UmlTableCol prototype;
	
	public static UmlTableCol getPrototype() {
		if (prototype == null) prototype = new UmlTableCol();
		
		return prototype;
	}

	private UmlTableCol() { }
	
	@Override
	public Object clone() {
		
		UmlTableCol cloned = null;
		cloned = (UmlTableCol) super.clone();
		
		return cloned;
	}
	
	public UmlTableCol create(String sName, DatabaseColumnTypes cColType) {
		
		UmlTableCol cloned = null;
		cloned = (UmlTableCol) super.clone();
		
		cloned.setName(sName);
		cloned.setColType(cColType);
		
		return cloned;
	}
	
	public void repairInput() {
		
		super.repairInput();
	}
	
	public boolean checkValid() {
		
		return !getName().isEmpty() && getColType() != null;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}
	
	@Override
	public String toString() {
		
		return getName() + ' ' + getColType().name();
	}
}
