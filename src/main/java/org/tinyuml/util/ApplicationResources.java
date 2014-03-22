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

import java.util.ResourceBundle;

/**
 * This class holds the application resource strings. UI labels are never
 * defined directly in the source code, but defined in locale-sensitive
 * property files. Even though other languages than English are not supported
 * here, a german localization would definitely be feasible.
 *
 * @author Wei-ju Wu
 * @version 1.0
 */
public final class ApplicationResources {

  // The resource bundle this is based on.
  private ResourceBundle resources =
    ResourceBundle.getBundle("org.tinyuml.ui.tinyuml-captions");

  private static ApplicationResources instance = new ApplicationResources();

  /**
   * Private constructor to enforce Singleton.
   */
  private ApplicationResources() { }

  /**
   * Returns the singleton instance.
   * @return the singleton instance
   */
  public static ApplicationResources getInstance() { return instance; }

  /**
   * Returns the value of the specified property.
   * @param property the property name
   * @return the value
   */
  public String getString(String property) {
    try {
      return resources.getString(property);
    } catch (Exception ex) {
      return null;
    }
  }

  /**
   * Returns the first character of a resource property.
   * @param property the property to retrieve
   * @return the first character as an int
   */
  public int getChar(String property) {
    String str = getString(property);
    return str == null ? 0 : str.charAt(0);
  }
}
