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

package org.tinyuml.ui;

import java.awt.Dimension;
import javax.swing.ButtonGroup;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

/**
 * @author Tomáš Ligenza
 */
public class EEREditorToolbarManager
	extends AbstractToolbarManager {

	private ButtonGroup buttongroup;

	/**
	 * Constructor.
	 */
	public EEREditorToolbarManager() {
	  buttongroup = new ButtonGroup();
	  JToolBar toolbar = getToolbar();
	  toolbar.add(createToggleButtonWithName(buttongroup, "select"));
	 // toolbar.add(createToggleButtonWithName(buttongroup, "delete"));
	  
	  toolbar.addSeparator(new Dimension(10, 10));
	  
	 /* toolbar.add(createToggleButtonWithName(buttongroup, "layer"));
	  toolbar.add(createToggleButtonWithName(buttongroup, "note"));
	  toolbar.add(createToggleButtonWithName(buttongroup, "noteconnector"));
	  
	  toolbar.addSeparator(new Dimension(10, 10));*/
	  
	  toolbar.add(createToggleButtonWithName(buttongroup, "table"));
	  toolbar.add(createToggleButtonWithName(buttongroup, "view"));
	  toolbar.add(createToggleButtonWithName(buttongroup, "procedure"));
	  
	  toolbar.addSeparator(new Dimension(10, 10));
	  
	  toolbar.add(createToggleButtonWithName(buttongroup, "one_one_non_identifying"));
	  toolbar.add(createToggleButtonWithName(buttongroup, "one_n_non_identifying"));
	  toolbar.add(createToggleButtonWithName(buttongroup, "one_one_identifying"));
	  toolbar.add(createToggleButtonWithName(buttongroup, "one_n_identifying"));
	  toolbar.add(createToggleButtonWithName(buttongroup, "n_n_identifying"));
	  
	  toolbar.addSeparator(new Dimension(10, 10));
	  
	  doClick("SELECT_MODE");
	}

	private JToggleButton createToggleButtonWithName(ButtonGroup aButtonGroup,
	  String name) {
	  return createToggleButton(aButtonGroup, "eertoolbar." + name);
	}
}
