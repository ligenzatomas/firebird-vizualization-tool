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

import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import org.firebirdvisualizationtool.database.firebird.TriggerFiringTime;

/**
 *
 * @author Tomáš Ligenza
 */
public class Trigger extends DefaultNamedElement {
	
	private static final long serialVersionUID = 6957713241179332897L;
	
	private TriggerFiringTime firingTime;
	private boolean insert, update, delete;
	
	private boolean active;
	
	private String command;
	
	private static Trigger prototype;
	
	public static Trigger getPrototype() {
		
		if (prototype == null) prototype = new Trigger();
		
		return prototype;
	}

	private Trigger() { }
	
	@Override
	public Object clone() {
		
		Trigger cloned = null;
		cloned = (Trigger) super.clone();
		
		cloned.firingTime = firingTime;
		cloned.insert = insert;
		cloned.update = update;
		cloned.delete = delete;
		cloned.active = active;
		cloned.command = command;
		
		return cloned;
	}
	
	public Trigger create(String sName, TriggerFiringTime firingTime, boolean active, boolean insert, boolean update, boolean delete, String command) {
		
		Trigger cloned = null;
		cloned = (Trigger) super.clone();
		
		cloned.setName(sName);
		cloned.setFiringTime(firingTime);
		cloned.setActive(active);
		cloned.setInsert(insert);
		cloned.setUpdate(update);
		cloned.setDelete(delete);
		cloned.setCommand(command);
		
		return cloned;
	}
	
	public Trigger create(String sName) {
		
		Trigger cloned = null;
		cloned = (Trigger) super.clone();
		
		cloned.setName(sName);
		
		return cloned;
	}

	public TriggerFiringTime getFiringTime() {
		return firingTime;
	}

	public void setFiringTime(TriggerFiringTime firingTime) {
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
		
		for(TriggerFiringTime type : TriggerFiringTime.values()) {
			
			model.addElement(type);
		}
		
		return model;
	}
	
	public String getDropSql() {
		
		return "DROP TRIGGER " + getName() + ";";
	}
	
	public String getCreateSql(String tableName) {
		
		List<String> firingTime = new ArrayList<String>();
		String firing = "";
		
		if(getInsert())
			firingTime.add("INSERT");
		if(getUpdate())
			firingTime.add("UPDATE");
		if(getDelete())
			firingTime.add("DELETE");
		
		if(!firingTime.isEmpty()) {
			
			StringBuilder builder = new StringBuilder();
			builder.append( firingTime.remove(0));

			for( String s : firingTime) {
				builder.append( " OR ");
				builder.append( s);
			}
			
			firing = builder.toString();
		}
		
		String script = 
			"CREATE TRIGGER " + getName() + " FOR " + tableName + " \n" +
			getFiringTime().name() + " " + firing + " \n" + 
			"AS\n" +
			getCommand();
		
		return "SET TERM ^ ;\n" + script + "SET TERM ; ^";
	}
}
