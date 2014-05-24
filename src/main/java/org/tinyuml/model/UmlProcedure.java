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
public class UmlProcedure extends AbstractUmlModelElement {
	
	private static final long serialVersionUID = -1017568125715449607L;
	
	private Procedure procedureModel = new Procedure();
	
	private static UmlProcedure prototype;
	
	public static UmlProcedure getPrototype() {
	
		if (prototype == null) prototype = new UmlProcedure();
	
		return prototype;
	}
	
	private UmlProcedure() { }
	
	public Object clone() {
		
		UmlProcedure cloned = (UmlProcedure) super.clone();
		
		if (procedureModel != null) {
			cloned.procedureModel = (Procedure) procedureModel.clone();
		}
		
		return cloned;
	}
	
	public UmlProcedure create(String name) {
		
		UmlProcedure cloned = (UmlProcedure) clone();
		
		cloned.setProcedureModel(new Procedure());
		cloned.setName(name);
		
		return cloned;
	}
	
	@Override
	public void setName(String name) {
		
		getProcedureModel().setName(name);
	}
	
	@Override
	public String getName() {
		
		return getProcedureModel().getName();
	}

	public String getSource() {
		return procedureModel.getSource();
	}

	public void setSource(String source) {
		this.procedureModel.setSource(source);
	}

	public Procedure getProcedureModel() {
		return procedureModel;
	}

	public void setProcedureModel(Procedure procedureModel) {
		this.procedureModel = procedureModel;
	}
}
