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

import java.util.HashMap;
import java.util.Map;
import org.tinyuml.util.AppCommandListener;
import org.tinyuml.ui.diagram.DiagramEditor;
import org.tinyuml.util.MethodCall;

/**
 * This class receives Editor related AppCommands and dispatches them to
 * the right places. This offloads editor related commands from the
 * AppFrame object, while AppFrame handles commands on a global level,
 * EditorCommandDispatcher handles it on the level of the current editor.
 *
 * @author Wei-ju Wu
 * @version 1.0
 */
public class EditorCommandDispatcher implements AppCommandListener {

  private ApplicationShell shell;
  private Map<String, MethodCall> selectorMap =
    new HashMap<String, MethodCall>();

  /**
   * Constructor.
   * @param aShell the application shell
   */
  public EditorCommandDispatcher(ApplicationShell aShell) {
    this.shell = aShell;
    initSelectorMap();
  }

  /**
   * Initializes the selector map.
   */
  private void initSelectorMap() {
    try {
      // Self-calls
      selectorMap.put("SHOW_GRID", new MethodCall(
        getClass().getMethod("showGrid")));
      selectorMap.put("SNAP_TO_GRID", new MethodCall(
        getClass().getMethod("snapToGrid")));
    } catch (NoSuchMethodException ex) {
      ex.printStackTrace();
    }
  }

  /**
   * Displays the grid depending on the selection state of the menu item.
   */
  public void showGrid() {
    getCurrentEditor().showGrid(
      getMenuManager().isSelected("SHOW_GRID"));
    getCurrentEditor().redraw();
  }

  /**
   * Activates snapping depending on the selection state of the menu item.
   */
  public void snapToGrid() {
    getCurrentEditor().snapToGrid(getMenuManager().isSelected("SNAP_TO_GRID"));
  }

  /**
   * {@inheritDoc}
   */
  public void handleCommand(String command) {
    MethodCall methodcall = selectorMap.get(command);
    if (methodcall != null) {
      methodcall.call(this);
    } else {
      DiagramEditor editor = getCurrentEditor();
      if (editor != null) editor.handleCommand(command);
    }
  }

  /**
   * Returns the currently selected editor.
   * @return the current editor
   */
  private DiagramEditor getCurrentEditor() {
    return shell.getCurrentEditor();
  }

  /**
   * Returns the application's menu manager.
   * @return the menu manager
   */
  private MenuManager getMenuManager() {
    return shell.getMenuManager();
  }
}
