/**
 * Copyright 2007 Wei-ju Wu
 *
 * This file is part of TinyUML.
 *
 * TinyUML is free software; you can redistribute it and/or modify
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
 * along with TinyUML; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.tinyuml.ui;

import javax.swing.JToolBar;
import org.tinyuml.util.AppCommandListener;

/**
 * A generic interface that defines a toolbar manager.
 * @author Wei-ju Wu
 * @version 1.0
 */
public interface ToolbarManager {

  /**
   * Returns the toolbar component.
   * @return the toolbar component
   */
  JToolBar getToolbar();

  /**
   * Selects the specified button.
   * @param actionCommand the action command string that is tied to the button
   */
  void doClick(String actionCommand);

  /**
   * Adds an AppCommandListener.
   * @param l the AppCommandListener to add
   */
  void addCommandListener(AppCommandListener l);

  /**
   * Removes an AppCommandListener.
   * @param l the AppCommandListener to remove
   */
  void removeCommandListener(AppCommandListener l);

  /**
   * Enables the specified button.
   * @param actionCommand the action command string that is tied to the button
   * @param flag true for enabling, false for disabling
   */
  void setEnabled(String actionCommand, boolean flag);
}
