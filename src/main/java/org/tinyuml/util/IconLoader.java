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
package org.tinyuml.util;

import java.util.HashMap;
import java.util.Map;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * This class accesses images for icons from the class path.
 *
 * @author Wei-ju Wu
 * @version 1.0
 */
public final class IconLoader {

  /**
   * This enum type lists the available icon types.
   */
  public enum IconType {
    CLOSE_TAB,
    NEW, OPEN, SAVE, CUT, COPY, PASTE, DELETE, UNDO, REDO,
    ABOUT,
    MOUSE_POINTER,
    CLASS, PACKAGE, COMPONENT, DEPENDENCY, ASSOCIATION, AGGREGATION,
    COMPOSITION, INHERITANCE, INTERFACE_REALIZATION, NOTE, NOTE_CONNECTOR,
    LIFELINE, MESSAGE
	, ERASER, EER
  }

  private Map<IconType, String> urlMap = new HashMap<IconType, String>();
  private Map<IconType, Icon> iconMap = new HashMap<IconType, Icon>();
  private Map<String, IconType> iconTypeMap = new HashMap<String, IconType>();

  private static IconLoader instance = new IconLoader();

  /**
   * Private constructor.
   */
  private IconLoader() {
    for (IconType iconType : IconType.values()) {
      iconTypeMap.put(iconType.toString(), iconType);
    }
    urlMap.put(IconType.MOUSE_POINTER, "org/tinyuml/ui/mousepointer.png");
    urlMap.put(IconType.CLASS, "org/tinyuml/ui/class.png");
    urlMap.put(IconType.CLOSE_TAB, "org/tinyuml/ui/close-tab.png");
    urlMap.put(IconType.PACKAGE, "org/tinyuml/ui/package.png");
    urlMap.put(IconType.COMPONENT, "org/tinyuml/ui/component.png");
    urlMap.put(IconType.DEPENDENCY, "org/tinyuml/ui/dependency.png");
    urlMap.put(IconType.ASSOCIATION, "org/tinyuml/ui/association.png");
    urlMap.put(IconType.NOTE, "org/tinyuml/ui/note.png");
    urlMap.put(IconType.NOTE_CONNECTOR, "org/tinyuml/ui/note-connector.png");
    urlMap.put(IconType.AGGREGATION, "org/tinyuml/ui/aggregation.png");
    urlMap.put(IconType.COMPOSITION, "org/tinyuml/ui/composition.png");
    urlMap.put(IconType.INHERITANCE, "org/tinyuml/ui/inheritance.png");
    urlMap.put(IconType.INTERFACE_REALIZATION,
      "org/tinyuml/ui/interface-realization.png");
    urlMap.put(IconType.LIFELINE, "org/tinyuml/ui/lifeline.png");
    urlMap.put(IconType.MESSAGE, "org/tinyuml/ui/message.png");

    urlMap.put(IconType.NEW, "org/fife/plaf/Office2003/new.gif");
    urlMap.put(IconType.OPEN, "org/fife/plaf/Office2003/open.gif");
    urlMap.put(IconType.SAVE, "org/fife/plaf/Office2003/save.gif");
    urlMap.put(IconType.CUT, "org/fife/plaf/Office2003/cut.gif");
    urlMap.put(IconType.COPY, "org/fife/plaf/Office2003/copy.gif");
    urlMap.put(IconType.PASTE, "org/fife/plaf/Office2003/paste.gif");
    urlMap.put(IconType.DELETE, "org/fife/plaf/Office2003/delete.gif");
    urlMap.put(IconType.UNDO, "org/fife/plaf/Office2003/undo.gif");
    urlMap.put(IconType.REDO, "org/fife/plaf/Office2003/redo.gif");
    urlMap.put(IconType.ABOUT, "org/fife/plaf/Office2003/about.gif");
	
	urlMap.put(IconType.ERASER, "org/tinyuml/ui/eraser.png");
	urlMap.put(IconType.EER, "org/tinyuml/ui/EER.png");
  }

  /**
   * Returns the singleton instance.
   * @return the singleton instance
   */
  public static IconLoader getInstance() { return instance; }

  /**
   * Returns the icon for the specified icon type.
   * @param type the icon type
   * @return the icon
   */
  public Icon getIcon(IconType type) {
    if (!iconMap.containsKey(type)) {
      String urlstr = urlMap.get(type);
      if (urlstr != null) {
        iconMap.put(type,
          new ImageIcon(getClass().getClassLoader().getResource(urlstr)));
      }
    }
    return iconMap.get(type);
  }

  /**
   * Returns the icon for the specified icon type name.
   * @param typeName the icon type name
   * @return the icon
   */
  public Icon getIcon(String typeName) {
    return getIcon(iconTypeMap.get(typeName));
  }
}
