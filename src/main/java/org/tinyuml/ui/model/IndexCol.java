/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tinyuml.ui.model;

import org.tinyuml.model.UmlTableIndex;

/**
 *
 * @author cml
 */
public class IndexCol {
		
	private Boolean checked;
	
	private String name;
	private UmlTableIndex.order oOrder;

	public IndexCol(Boolean check, String name, UmlTableIndex.order oOrder) {
		this.checked = check;
		this.name = name;
		this.oOrder = oOrder;
	}
	
	public Boolean getChecked() {
		return checked;
	}

	public void setChecked(Boolean checked) {
		this.checked = checked;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public UmlTableIndex.order getoOrder() {
		return oOrder;
	}

	public void setoOrder(UmlTableIndex.order oOrder) {
		this.oOrder = oOrder;
	}

}
