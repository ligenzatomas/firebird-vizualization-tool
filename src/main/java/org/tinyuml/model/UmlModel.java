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

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * The UmlModel holds the logical entities of a model. It is the repository that
 * diagrams retrieve their data objects from. The UmlModel objects are
 * shareable, so they are can be used from every diagram.
 *
 * @author Wei-ju Wu
 * @version 1.0
 */
public interface UmlModel extends Serializable {

  /**
   * Adds the specified element.
   * @param anElement the element to add
   */
  void addElement(UmlModelElement anElement);

  /**
   * Determines if the specified model element is in the model.
   * @param anElement the model element to look up
   * @return true if the element exists, false otherwise
   */
  boolean contains(UmlModelElement anElement);

  /**
   * Returns all elements.
   * @return the list of all elements
   */
  Set<? extends NamedElement> getElements();

  /**
   * Adds the specified diagram.
   * @param diagram the diagram to add
   */
  void addDiagram(UmlDiagram diagram);

  /**
   * Removes the specified diagram.
   * @param diagram the diagram to remove
   */
  void removeDiagram(UmlDiagram diagram);

  /**
   * Returns all diagrams.
   * @return the diagrams
   */
  List<? extends UmlDiagram> getDiagrams();

  /**
   * Adds the specified model listener.
   * @param l the model listener
   */
  void addModelListener(UmlModelListener l);

  /**
   * Removes the specified model listener.
   * @param l the model listener
   */
  void removeModelListener(UmlModelListener l);
}
