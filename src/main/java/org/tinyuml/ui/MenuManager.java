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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;
import org.tinyuml.util.AppCommandListener;
import org.tinyuml.util.ApplicationResources;
import org.tinyuml.util.IconLoader;

/**
 * This class manages the pulldown menu of the application.
 *
 * @author Wei-ju Wu
 * @version 1.0
 */
public class MenuManager implements ActionListener {

  private JMenuBar menubar;
  private List<AppCommandListener> listeners =
    new ArrayList<AppCommandListener>();
  private Map<String, JMenuItem> itemMap = new HashMap<String, JMenuItem>();

  /**
   * Creates a new instance of MenuManager.
   */
  public MenuManager() {
    menubar = new JMenuBar();
    createFileMenu();
    createEditMenu();
    createViewMenu();
	createDatabaseMenu();
    createHelpMenu();
  }

  /**
   * Creates the File menu.
   */
  private void createFileMenu() {
    JMenu fileMenu = createMenu("file");
    menubar.add(fileMenu);

    createMenuItem(fileMenu, "new");
    JMenu newDiagramMenu = createMenu("new.newdiagram");
    fileMenu.add(newDiagramMenu);
    createMenuItem(newDiagramMenu, "newstructurediagram");
    createMenuItem(newDiagramMenu, "newsequencediagram");
	createMenuItem(newDiagramMenu, "neweerdiagram");
    createMenuItem(fileMenu, "open");
    createMenuItem(fileMenu, "saveas");
    createMenuItem(fileMenu, "save");
    createMenuItem(fileMenu, "exportgfx");
    fileMenu.addSeparator();
    createMenuItem(fileMenu, "quit");
  }

  /**
   * Creates the Edit mnu.
   */
  private void createEditMenu() {
    JMenu editMenu = createMenu("edit");
    menubar.add(editMenu);
    createMenuItem(editMenu, "undo");
    createMenuItem(editMenu, "redo");
    editMenu.addSeparator();
    /*
    createMenuItem(editMenu, "cut");
    createMenuItem(editMenu, "copy");
    createMenuItem(editMenu, "paste");
     */
    createMenuItem(editMenu, "delete");

    //editMenu.addSeparator();
    //createMenuItem(editMenu, "settings");

    enableMenuItem("UNDO", false);
    enableMenuItem("REDO", false);
    //enableMenuItem("CUT", false);
    //enableMenuItem("COPY", false);
    //enableMenuItem("PASTE", false);
    enableMenuItem("DELETE", false);
  }

  /**
   * Creates the View menu.
   */
  private void createViewMenu() {
    JMenu viewMenu = createMenu("view");
    menubar.add(viewMenu);
    JRadioButtonMenuItem zoom50 = createRadioMenuItem(viewMenu, "zoom_50");
    JRadioButtonMenuItem zoom75 = createRadioMenuItem(viewMenu, "zoom_75");
    JRadioButtonMenuItem zoom100 = createRadioMenuItem(viewMenu, "zoom_100");
    JRadioButtonMenuItem zoom150 = createRadioMenuItem(viewMenu, "zoom_150");
    viewMenu.add(zoom150);
    ButtonGroup group = new ButtonGroup();
    group.add(zoom50);
    group.add(zoom75);
    group.add(zoom100);
    group.add(zoom150);
    zoom100.setSelected(true);
    viewMenu.addSeparator();
    JMenuItem showGrid = createCheckBoxMenuItem(viewMenu, "showgrid");
    showGrid.setSelected(true);
    JMenuItem snapToGrid = createCheckBoxMenuItem(viewMenu, "snaptogrid");
    snapToGrid.setSelected(true);
    viewMenu.addSeparator();
    createMenuItem(viewMenu, "redraw");
  }
  
  private void createDatabaseMenu() {
	  
	  JMenu databaseMenu = createMenu("database");
	  menubar.add(databaseMenu);
	  createMenuItem(databaseMenu, "manageConnections");
	  
	  databaseMenu.addSeparator();
	  
	  createMenuItem(databaseMenu, "manageSequences");
	  createMenuItem(databaseMenu, "manageDomains");
	  
	  databaseMenu.addSeparator();
	  
	  createMenuItem(databaseMenu, "forwardEngineer");
	  createMenuItem(databaseMenu, "reverseEngineer");
  }

  /**
   * Creates the Help menu.
   */
  private void createHelpMenu() {
    JMenu helpMenu = createMenu("help");
    menubar.add(helpMenu);
    createMenuItem(helpMenu, "about");
    createMenuItem(helpMenu, "helpcontents");
  }

  /**
   * Generic helper method to construct a menu according to the resource
   * strings.
   * @param name the menu name
   * @return the JMenu
   */
  private JMenu createMenu(String name) {
    String prefix = "menu." + name;
    JMenu menu = new JMenu(getResourceString(prefix + ".name"));
    menu.setMnemonic(getResourceChar(prefix + ".mnemonic"));
    return menu;
  }

  /**
   * Generic helper method to construct a menu according to the resource
   * strings.
   * @param menu the menu to create the item in
   * @param name the menu item name
   * @return the JMenuItem
   */
  private JMenuItem createMenuItem(JMenu menu, String name) {
    String prefix = "menuitem." + name;
    JMenuItem menuitem = new JMenuItem(getResourceString(prefix + ".name"));
    addMenuItemInformation(menuitem, prefix);
    menu.add(menuitem);
    return menuitem;
  }

  /**
   * Creates a radio button menu item.
   * @param menu the menu to create this item under
   * @param name the resource name
   * @return the menu item
   */
  private JRadioButtonMenuItem createRadioMenuItem(JMenu menu, String name) {
    String prefix = "menuitem." + name;
    JRadioButtonMenuItem menuitem =
      new JRadioButtonMenuItem(getResourceString(prefix + ".name"));
    addMenuItemInformation(menuitem, prefix);
    menu.add(menuitem);
    return menuitem;
  }

  /**
   * Creates a checkbox menu item.
   * @param menu the menu to create this item under
   * @param name the resource name
   * @return the menu item
   */
  private JCheckBoxMenuItem createCheckBoxMenuItem(JMenu menu, String name) {
    String prefix = "menuitem." + name;
    JCheckBoxMenuItem menuitem =
      new JCheckBoxMenuItem(getResourceString(prefix + ".name"));
    addMenuItemInformation(menuitem, prefix);
    menu.add(menuitem);
    return menuitem;
  }

  /**
   * Adds the general menu item information to the specified menu item.
   * @param menuitem the menu item
   * @param prefix the resource prefix
   */
  private void addMenuItemInformation(JMenuItem menuitem, String prefix) {
    int mnemonic = getResourceChar(prefix + ".mnemonic");
    if (mnemonic > 0) {
      menuitem.setMnemonic(mnemonic);
    }
    String accel = getResourceString(prefix + ".accelerator");
    if (accel != null) {
      menuitem.setAccelerator(KeyStroke.getKeyStroke(accel));
    }
    String actionCommand = getResourceString(prefix + ".command");
    menuitem.setActionCommand(actionCommand);
    itemMap.put(actionCommand, menuitem);
    menuitem.addActionListener(this);

    // icon
    String iconType = getResourceString(prefix + ".icon");
    if (iconType != null) {
      menuitem.setIcon(IconLoader.getInstance().getIcon(iconType));
    }
  }

  /**
   * Adds a CommandListener.
   * @param l the CommandListener to add
   */
  public void addCommandListener(AppCommandListener l) {
    listeners.add(l);
  }

  /**
   * {@inheritDoc}
   */
  public void actionPerformed(ActionEvent e) {
    for (AppCommandListener l : listeners) {
      l.handleCommand(e.getActionCommand());
    }
  }

  /**
   * Retrieves the menubar instance.
   * @return the managed menubar instance
   */
  public JMenuBar getMenuBar() {
    return menubar;
  }

  /**
   * Returns the specified resource as a String object.
   * @param property the property name
   * @return the property value or null if not found
   */
  private String getResourceString(String property) {
    return ApplicationResources.getInstance().getString(property);
  }

  /**
   * Returns the first character of a resource property.
   * @param property the property to retrieve
   * @return the first character as an int
   */
  private int getResourceChar(String property) {
    return ApplicationResources.getInstance().getChar(property);
  }

  /**
   * Sets the enabled state for the menu item that corresponds to the specified
   * command.
   * @param actionCommand the action command string
   * @param flag the enable state
   */
  public void enableMenuItem(String actionCommand, boolean flag) {
    itemMap.get(actionCommand).setEnabled(flag);
  }

  /**
   * Enables or disables all items in the view menu.
   * @param flag the value to indicate whether to enable or disable the items
   */
  public void enableViewMenuItems(boolean flag) {
    enableMenuItem("ZOOM_50", flag);
    enableMenuItem("ZOOM_75", flag);
    enableMenuItem("ZOOM_100", flag);
    enableMenuItem("ZOOM_150", flag);
    enableMenuItem("SHOW_GRID", flag);
    enableMenuItem("SNAP_TO_GRID", flag);
    enableMenuItem("REDRAW", flag);
  }

  /**
   * Returns the selection state of the specified menu item.
   * @param actionCommand the action command string
   * @return the selection state
   */
  public boolean isSelected(String actionCommand) {
    return itemMap.get(actionCommand).isSelected();
  }
}
