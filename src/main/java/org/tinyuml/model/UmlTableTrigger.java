/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tinyuml.model;

import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;

/**
 *
 * @author cml
 */
public class UmlTableTrigger extends DefaultNamedElement {
	
	public enum firing	{ BEFORE, AFTER };
	
	private firing firingTime;
	private boolean insert, update, delete;
	
	private boolean active;
	
	private String command;
	
	private static UmlTableTrigger prototype;
	
	public static UmlTableTrigger getPrototype() {
		
		if (prototype == null) prototype = new UmlTableTrigger();
		
		return prototype;
	}

	private UmlTableTrigger() { }
	
	@Override
	public Object clone() {
		
		UmlTableTrigger cloned = null;
		cloned = (UmlTableTrigger) super.clone();
		
		return cloned;
	}
	
	public UmlTableTrigger create(String sName, firing firingTime, boolean active, boolean insert, boolean update, boolean delete, String command) {
		
		UmlTableTrigger cloned = null;
		cloned = (UmlTableTrigger) super.clone();
		
		cloned.setName(sName);
		cloned.setFiringTime(firingTime);
		cloned.setActive(active);
		cloned.setInsert(insert);
		cloned.setUpdate(update);
		cloned.setDelete(delete);
		cloned.setCommand(command);
		
		return cloned;
	}

	public firing getFiringTime() {
		return firingTime;
	}

	public void setFiringTime(firing firingTime) {
		this.firingTime = firingTime;
	}

	public boolean getInsert() {
		return insert;
	}

	public void setInsert(boolean insert) {
		this.insert = insert;
	}

	public boolean getUpdate() {
		return update;
	}

	public void setUpdate(boolean update) {
		this.update = update;
	}

	public boolean getDelete() {
		return delete;
	}

	public void setDelete(boolean delete) {
		this.delete = delete;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}
	
	public boolean checkValid() {
		
		return !getName().isEmpty()
			&& (getDelete() == true || getUpdate() == true || getInsert() == true)
			&& !getCommand().isEmpty();
	}
	
	public static DefaultComboBoxModel getFiringTimeComboBoxModel() {
		
		final DefaultComboBoxModel model = new DefaultComboBoxModel();
		
		for(firing type : firing.values()) {
			
			model.addElement(type);
		}
		
		return model;
	}
}
