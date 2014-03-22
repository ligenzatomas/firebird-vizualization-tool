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
package org.tinyuml.ui.diagram;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import org.tinyuml.draw.Connection;
import org.tinyuml.draw.Selection;
import org.tinyuml.model.Relation;
import org.tinyuml.umldraw.shared.UmlConnection;
import org.tinyuml.util.IconLoader;
import org.tinyuml.util.AppCommandListener;
import org.tinyuml.umldraw.shared.UmlDiagramElement;
import org.tinyuml.umldraw.structure.Association;
import org.tinyuml.umldraw.structure.ClassElement;
import org.tinyuml.util.ApplicationResources;

/**
 * This class creates context menus, depending on the specified parameters.
 *
 * @author Wei-ju Wu
 * @version 1.0
 */
public class ContextMenuBuilder implements ActionListener {

  private Set<AppCommandListener> commandListeners =
    new HashSet<AppCommandListener>();

  /**
   * Adds the specified AppCommandListener.
   * @param l the AppCommandListener to add
   */
  public void addAppCommandListener(AppCommandListener l) {
    commandListeners.add(l);
  }

  /**
   * Created a popup menu for the specified selection.
   * @param selection the selection
   * @return the popup menu
   */
  public JPopupMenu createContextMenu(Selection selection) {
    if (selection.getElements().size() > 1) {
      return createMultipleSelectionContextMenu();
    } else {
      UmlDiagramElement elem = (UmlDiagramElement) selection.getElement();
      if (elem instanceof Connection) {
        return createSingleConnectionContextMenu((Connection) elem);
      }
      return createSingleNodeContextMenu(elem);
    }
  }

  /**
   * Creates a context menu for multiple selections.
   * @return the context menu
   */
  private JPopupMenu createMultipleSelectionContextMenu() {
    JPopupMenu menu = new JPopupMenu();
    createMenuItem(menu, "delete");
    return menu;
  }

  /**
   * Creates a context menu for single node selections.
   * @param element the diagram element to show the menu for
   * @return the context menu
   */
  private JPopupMenu createSingleNodeContextMenu(UmlDiagramElement element) {
    JPopupMenu menu = new JPopupMenu();
    if (element instanceof ClassElement) {
      createMenuItem(menu, "editproperties");
    }
    JMenu drawOrderMenu = new JMenu(
      ApplicationResources.getInstance().getString("submenu.draworder.name"));
    menu.add(drawOrderMenu);
    createMenuItem(drawOrderMenu, "draworder.tofront");
    createMenuItem(drawOrderMenu, "draworder.toback");
    menu.addSeparator();
    createMenuItem(menu, "delete");
    return menu;
  }

  /**
   * Creates a context menu for single connection selections.
   * @param conn the connection to create the menu for
   * @return the context menu
   */
  private JPopupMenu createSingleConnectionContextMenu(Connection conn) {
    JPopupMenu menu = new JPopupMenu();
    createMenuItem(menu, "resetpoints");
    if (conn.isRectilinear()) {
      createMenuItem(menu, "recttodirect");
    } else {
      createMenuItem(menu, "directtorect");
    }
    addNavigabilityMenu(menu, (UmlConnection) conn);
    menu.addSeparator();
    createMenuItem(menu, "delete");
    addEditConnectionPropertiesMenu(menu, (UmlConnection) conn);
    return menu;
  }

  /**
   * Adds the navigability setting menu.
   * @param menu the parent menu
   * @param conn the connection
   */
  private void addNavigabilityMenu(JPopupMenu menu, UmlConnection conn) {
    Relation relation = (Relation) conn.getModelElement();
    if (relation == null) return; // e.g. NoteConnection has no relation
    JMenu submenu = null;
    if (relation.canSetElement1Navigability() ||
        relation.canSetElement2Navigability()) {
      submenu = new JMenu(ApplicationResources.getInstance().getString(
        "submenu.navigableto.name"));
      menu.add(submenu);
    }
    if (relation.canSetElement1Navigability()) {
      JCheckBoxMenuItem nav2Elem1 = createCheckBoxMenuItem(submenu,
        "navigabletosource");
      nav2Elem1.setSelected(relation.isNavigableToElement1());
    }
    if (relation.canSetElement2Navigability()) {
      JCheckBoxMenuItem nav2Elem2 = createCheckBoxMenuItem(submenu,
        "navigabletotarget");
      nav2Elem2.setSelected(relation.isNavigableToElement2());
    }
  }

  /**
   * Creates a property menu if possible.
   * @param menu the popup menu
   * @param conn the connection
   */
  private void addEditConnectionPropertiesMenu(JPopupMenu menu,
    UmlConnection conn) {
    if (conn instanceof Association) {
      menu.addSeparator();
      createMenuItem(menu, "editproperties");
    }
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
   * {@inheritDoc}
   */
  public void actionPerformed(ActionEvent e) {
    for (AppCommandListener l : commandListeners) {
      l.handleCommand(e.getActionCommand());
    }
  }

  /**
   * Generic helper method to construct a menu according to the resource
   * strings.
   * @param menu the menu to create the item in
   * @param name the menu item name
   * @return the JMenuItem
   */
  private JMenuItem createMenuItem(JComponent menu, String name) {
    String prefix = "menuitem." + name;
    JMenuItem menuitem = new JMenuItem(getResourceString(prefix + ".name"));

    // Command
    String actionCommand = getResourceString(prefix + ".command");
    menuitem.setActionCommand(actionCommand);
    menuitem.addActionListener(this);

    // icon
    String iconType = getResourceString(prefix + ".icon");
    if (iconType != null) {
      menuitem.setIcon(IconLoader.getInstance().getIcon(iconType));
    }
    menu.add(menuitem);
    return menuitem;
  }

  /**
   * Helper method to construct a check box menu item according to resource
   * strings.
   * @param menu the parent menu
   * @param name the menuitem name
   * @return the menu item
   */
  private JCheckBoxMenuItem createCheckBoxMenuItem(JComponent menu,
    String name) {
    String prefix = "menuitem." + name;
    JCheckBoxMenuItem menuitem = new JCheckBoxMenuItem(
      getResourceString(prefix + ".name"));

    // Command
    String actionCommand = getResourceString(prefix + ".command");
    menuitem.setActionCommand(actionCommand);
    menuitem.addActionListener(this);
    menu.add(menuitem);
    return menuitem;
  }
}
