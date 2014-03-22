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
 * The super interface to all UML model relations.
 * @author Wei-ju Wu
 * @version 1.0
 */
public interface Relation extends UmlModelElement {

  /**
   * The direction to read the name.
   */
  enum ReadingDirection { UNDEFINED, LEFT_RIGHT, RIGHT_LEFT };

  /**
   * Returns the name reading direction. The default is UNDEFINED.
   * @return the name reading direction
   */
  ReadingDirection getNameReadingDirection();

  /**
   * Sets the name reading direction.
   * @param dir the reading direction
   */
  void setNameReadingDirection(ReadingDirection dir);

  /**
   * Returns the element 1 of the relation.
   * @return the element 1 of the relation
   */
  UmlModelElement getElement1();

  /**
   * Sets element 1 of the relation.
   * @param element the element 1 of the relation
   */
  void setElement1(UmlModelElement element);

  /**
   * Returns the element 2 of the relation.
   * @return the element 2 of the relation
   */
  UmlModelElement getElement2();

  /**
   * Sets the element 2 of the relation.
   * @param element the element 2 of the relation
   */
  void setElement2(UmlModelElement element);

  /**
   * Determines whether this relation's navigability to element 1 can be set.
   * @return true if navigability can be set, false otherwise
   */
  boolean canSetElement1Navigability();

  /**
   * Determines whether this relation's navigability to element 2 can be set.
   * @return true if navigability can be set, false otherwise
   */
  boolean canSetElement2Navigability();

  /**
   * Determines the navigability to element 1.
   * @return true if navigability is set, false otherwise
   */
  boolean isNavigableToElement1();

  /**
   * Sets the navigability to element 1.
   * @param flag true to set navigability, false otherwise
   */
  void setNavigableToElement1(boolean flag);

  /**
   * Determines the navigability to element 2.
   * @return true if navigability is set, false otherwise
   */
  boolean isNavigableToElement2();

  /**
   * Sets the navigability to element 2.
   * @param flag true to set navigability, false otherwise
   */
  void setNavigableToElement2(boolean flag);

  /**
   * Returns the element 2 multiplicity
   * @return the multiplicity
   */
  Multiplicity getElement1Multiplicity();

  /**
   * Sets the element 1 multiplicity.
   * @param multiplicity the Multiplicity
   */
  void setElement1Multiplicity(Multiplicity multiplicity);

  /**
   * Returns the element 1 multiplicity
   * @return the multiplicity
   */
  Multiplicity getElement2Multiplicity();

  /**
   * Sets the element 2 multiplicity.
   * @param multiplicity the Multiplicity
   */
  void setElement2Multiplicity(Multiplicity multiplicity);
}
