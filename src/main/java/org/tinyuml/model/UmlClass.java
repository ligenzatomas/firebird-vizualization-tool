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

import java.util.ArrayList;
import java.util.List;

/**
 * This is the UML model class for a Class.
 * @author Wei-ju Wu
 * @version 1.0
 */
public final class UmlClass extends AbstractUmlModelElement {

  private static final long serialVersionUID = -4204388361763579938L;
  private boolean isAbstract;
  private List<UmlProperty> attributes = new ArrayList<UmlProperty>();
  private List<UmlProperty> methods = new ArrayList<UmlProperty>();
  private List<UmlStereotype> stereotypes = new ArrayList<UmlStereotype>();
  private static UmlClass prototype;

  /**
   * Returns the Prototype instance of the UmlClass.
   * @return the Prototype instance
   */
  public static UmlClass getPrototype() {
    if (prototype == null) prototype = new UmlClass();
    return prototype;
  }

  /**
   * Constructor.
   */
  private UmlClass() { }

  /**
   * {@inheritDoc}
   */
  @Override
  public Object clone() {
    UmlClass cloned = (UmlClass) super.clone();
    cloned.attributes = new ArrayList<UmlProperty>();
    for (UmlProperty attribute : attributes) {
      cloned.attributes.add(attribute);
    }
    cloned.methods = new ArrayList<UmlProperty>();
    for (UmlProperty method : methods) {
      cloned.methods.add(method);
    }
    cloned.stereotypes = new ArrayList<UmlStereotype>();
    for (UmlStereotype stereotype : stereotypes) {
      cloned.stereotypes.add(stereotype);
    }
    return cloned;
  }

  /**
   * Returns the isAbstract attribute.
   * @return the isAbstract attribute
   */
  public boolean isAbstract() {
    return isAbstract;
  }

  /**
   * Sets the isAbstract attribute.
   * @param flag the isAbstract attribute
   */
  public void setAbstract(boolean flag) {
    isAbstract = flag;
    notifyElementChanged();
  }

  /**
   * Returns the methods.
   * @return the methods
   */
  public List<UmlProperty> getMethods() { return methods; }

  /**
   * Sets the method list.
   * @param methodList the method list
   */
  public void setMethods(List<UmlProperty> methodList) {
    methods = methodList;
    notifyElementChanged();
  }

  /**
   * Returns the attributes.
   * @return the attributes
   */
  public List<UmlProperty> getAttributes() { return attributes; }

  /**
   * Sets the attribute list.
   * @param attributeList the attribute list
   */
  public void setAttributes(List<UmlProperty> attributeList) {
    attributes = attributeList;
    notifyElementChanged();
  }

  /**
   * Returns the stereotypes.
   * @return the stereotypes
   */
  public List<UmlStereotype> getStereotypes() { return stereotypes; }

  /**
   * Sets the stereotypes.
   * @param stereotypeList the stereotypes
   */
  public void setStereotypes(List<UmlStereotype> stereotypeList) {
    stereotypes = stereotypeList;
    notifyElementChanged();
  }
}
