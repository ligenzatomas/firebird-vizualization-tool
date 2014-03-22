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

import java.util.Collection;

/**
 * Super class for model elements. Every element in the system has an unique
 * identifier. Since every element can have one or more shapes referencing it,
 * we hold the reference count in order to determine whether operations can be
 * performed on it.
 * The Cloneable interface is implemented here in order to support the Prototype
 * pattern in derived classes, these should make their constructors private
 * and provide a static method to return the prototype instances.
 *
 * @author Wei-ju Wu
 * @version 1.0
 */
public interface UmlModelElement extends NamedElement {

  /**
   * Makes the clone() method public.
   * @return the cloned object
   */
  Object clone();

  /**
   * Adds the specified UmlModelElementListener to the list of this element's
   * listeners.
   * @param l the listener to add
   */
  void addModelElementListener(UmlModelElementListener l);

  /**
   * Removes the specified UmlModelElementListener from the list of this
   * element's listeners.
   * @param l the listener to remove
   */
  void removeModelElementListener(UmlModelElementListener l);

  /**
   * Returns the UmlModelElementListeners for testing.
   * @return the UmlModelElementListeners
   */
  Collection<UmlModelElementListener> getModelElementListeners();
}
