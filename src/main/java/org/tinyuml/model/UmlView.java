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
public class UmlView extends AbstractUmlModelElement {
	
	private static final long serialVersionUID = -5323094691483096714L;
	
	private View viewModel = new View();
	
	private static UmlView prototype;
	
	public static UmlView getPrototype() {
	
		if (prototype == null) prototype = new UmlView();
	
		return prototype;
	}
	
	private UmlView() { }
	
	public Object clone() {
		
		UmlView cloned = (UmlView) super.clone();
		
		if (viewModel != null) {
			cloned.viewModel = (View) viewModel.clone();
		}
		
		return cloned;
	}
	
	public UmlView create(String name) {
		
		UmlView cloned = (UmlView) clone();
		
		cloned.setViewModel(new View());
		cloned.setName(name);
		
		return cloned;
	}
	
	@Override
	public void setName(String name) {
		
		getViewModel().setName(name);
	}
	
	@Override
	public String getName() {
		
		return getViewModel().getName();
	}

	public String getSource() {
		return viewModel.getSource();
	}

	public void setSource(String source) {
		this.viewModel.setSource(source);
	}

	public View getViewModel() {
		return viewModel;
	}

	public void setViewModel(View viewModel) {
		this.viewModel = viewModel;
	}
}
