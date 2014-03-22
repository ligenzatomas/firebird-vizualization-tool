/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tinyuml.ui.model;

/**
 *
 * @author cml
 */
public class ForeignKeyCol {
	
	private Boolean checked;
	
	private String name;

	public ForeignKeyCol(Boolean check, String name) {
		
		this.checked = check;
		this.name = name;
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
}
