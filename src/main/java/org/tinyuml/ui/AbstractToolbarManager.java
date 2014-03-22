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

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import org.tinyuml.util.AppCommandListener;
import org.tinyuml.util.ApplicationResources;
import org.tinyuml.util.IconLoader;

/**
 * An abstract super class that provides a generic implementation of a toolbar
 * manager.
 *
 * @author Wei-ju Wu
 * @version 1.0
 */
public abstract class AbstractToolbarManager
implements ToolbarManager, ActionListener {
  private JToolBar toolbar = new JToolBar();
  private List<AppCommandListener> listeners =
    new ArrayList<AppCommandListener>();
  private Map<String, AbstractButton> buttonMap =
    new HashMap<String, AbstractButton>();

  /**
   * {@inheritDoc}
   */
  public void addCommandListener(AppCommandListener l) {
    listeners.add(l);
  }

  /**
   * {@inheritDoc}
   */
  public void removeCommandListener(AppCommandListener l) {
    listeners.remove(l);
  }

  /**
   * {@inheritDoc}
   */
  public JToolBar getToolbar() { return toolbar; }

  /**
   * {@inheritDoc}
   */
  public void actionPerformed(ActionEvent e) {
    for (AppCommandListener l : listeners) {
      l.handleCommand(e.getActionCommand());
    }
  }

  /**
   * {@inheritDoc}
   */
  public void setEnabled(String actionCommand, boolean flag) {
    buttonMap.get(actionCommand).setEnabled(flag);
  }

  /**
   * {@inheritDoc}
   */
  public void doClick(String actionCommand) {
    buttonMap.get(actionCommand).requestFocusInWindow();
    buttonMap.get(actionCommand).doClick();
  }

  /**
   * Creates the specified toggle button.
   * @param aButtonGroup an optional ButtonGroup to add to
   * @param prefix the toggle button resource prefix
   * @return the toggle button
   */
  protected JToggleButton createToggleButton(ButtonGroup aButtonGroup,
    String prefix) {
    JToggleButton button = new JToggleButton(
      IconLoader.getInstance().getIcon(getResourceString(prefix + ".icon")));
    button.setMargin(new Insets(5, 5, 5, 5));
    button.setFocusable(false);
    String actionCommand = getResourceString(prefix + ".command");
    button.setActionCommand(actionCommand);
    button.addActionListener(this);
    toolbar.add(button);
    button.setToolTipText(getResourceString(prefix + ".tooltip"));
    buttonMap.put(actionCommand, button);
    if (aButtonGroup != null) {
      aButtonGroup.add(button);
    }
    return button;
  }

  /**
   * Creates the button with the specified name.
   * @param prefix the resource prefix
   * @return the button
   */
  protected JButton createButton(String prefix) {
    JButton button = new JButton(
      IconLoader.getInstance().getIcon(getResourceString(prefix + ".icon")));
    button.setFocusable(false);
    button.setMargin(new Insets(1, 1, 1, 1));
    String command = getResourceString(prefix + ".command");
    button.setActionCommand(command);
    button.addActionListener(this);
    buttonMap.put(command, button);
    toolbar.add(button);
    button.setToolTipText(getResourceString(prefix + ".tooltip"));
    return button;
  }

  /**
   * Returns the specified resource as a String object.
   * @param property the property name
   * @return the property value or null if not found
   */
  private String getResourceString(String property) {
    return ApplicationResources.getInstance().getString(property);
  }
}
