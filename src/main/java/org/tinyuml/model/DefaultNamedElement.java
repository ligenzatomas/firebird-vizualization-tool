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
package org.tinyuml.model;

/**
 * This class provides a default implementation of the NamedElement interface
 * that can be either subclassed or embedded.
 *
 * @author Wei-ju Wu
 * @version 1.0
 */
public class DefaultNamedElement implements NamedElement {

  private static final long serialVersionUID = 6141569974727644801L;
  private String name;

  /**
   * {@inheritDoc}
   */
  @Override
  public Object clone() {
    DefaultNamedElement cloned = null;
    try {
      cloned = (DefaultNamedElement) super.clone();
    } catch (CloneNotSupportedException ignore) {
      ignore.printStackTrace();
    }
    return cloned;
  }

  /**
   * {@inheritDoc}
   */
  public String getName() { return name; }

  /**
   * {@inheritDoc}
   */
  public void setName(String aName) { name = aName; }

  /**
   * {@inheritDoc}
   */
  public String toString() { return getName(); }
}
