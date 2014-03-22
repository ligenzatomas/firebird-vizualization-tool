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
package org.tinyuml.umldraw.structure;

import java.awt.geom.Dimension2D;
import org.tinyuml.draw.AbstractCompositeNode;
import org.tinyuml.draw.Compartment;
import org.tinyuml.draw.Compartment.Alignment;
import org.tinyuml.draw.DoubleDimension;
import org.tinyuml.draw.DrawingContext.FontType;
import org.tinyuml.draw.DrawingContext;
import org.tinyuml.draw.Label;
import org.tinyuml.draw.LabelSource;
import org.tinyuml.draw.SimpleLabel;
import org.tinyuml.model.RelationEndType;
import org.tinyuml.model.RelationType;
import org.tinyuml.model.UmlClass;
import org.tinyuml.model.UmlModelElement;
import org.tinyuml.model.UmlModelElementListener;
import org.tinyuml.model.UmlProperty;
import org.tinyuml.model.UmlStereotype;
import org.tinyuml.umldraw.shared.UmlModelElementLabelSource;
import org.tinyuml.umldraw.shared.UmlNode;

/**
 * This class represents a Class element in the editor. It is responsible for
 * rendering the information in the editor.
 *
 * @author Wei-ju Wu
 * @version 1.0
 */
public final class ClassElement extends AbstractCompositeNode
implements LabelSource, UmlNode, UmlModelElementListener {

  private static final long serialVersionUID = 8767029215902619069L;
  private UmlClass classData;
  private Compartment mainCompartment;
  private Compartment attributesCompartment;
  private Compartment operationsCompartment;
  private Label mainLabel;
  private boolean showOperations = true, showAttributes = false,
    showStereotypes = true;

  private static ClassElement prototype;

  /**
   * Returns the prototype instance.
   * @return the prototype instance
   */
  public static ClassElement getPrototype() {
    if (prototype == null) prototype = new ClassElement();
    return prototype;
  }

  /**
   * Private constructor.
   */
  private ClassElement() {
    mainCompartment = new Compartment();
    attributesCompartment = new Compartment();
    operationsCompartment = new Compartment();
    mainLabel = new SimpleLabel();
    mainLabel.setSource(this);
    mainCompartment.addLabel(mainLabel);
    mainCompartment.setParent(this);
    attributesCompartment.setParent(this);
    attributesCompartment.setAlignment(Alignment.LEFT);
    operationsCompartment.setParent(this);
    operationsCompartment.setAlignment(Alignment.LEFT);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Object clone() {
    ClassElement cloned = (ClassElement) super.clone();
    if (classData != null) {
      cloned.classData = (UmlClass) classData.clone();
      cloned.classData.addModelElementListener(cloned);
    }
    cloned.mainLabel = (Label) mainLabel.clone();
    cloned.mainLabel.setSource(cloned);
    cloned.mainCompartment = (Compartment) mainCompartment.clone();
    cloned.mainCompartment.setParent(cloned);
    cloned.mainCompartment.removeAllLabels();
    cloned.mainCompartment.addLabel(cloned.mainLabel);
    cloned.attributesCompartment = (Compartment) attributesCompartment.clone();
    cloned.attributesCompartment.setParent(cloned);
    cloned.operationsCompartment = (Compartment) operationsCompartment.clone();
    cloned.operationsCompartment.setParent(cloned);
    return cloned;
  }

  /**
   * Returns the main label for testing purposes.
   * @return the main label
   */
  public Label getMainLabel() { return mainLabel; }

  /**
   * Returns the main compartment for testing purposes.
   * @return the main compartment
   */
  public Compartment getMainCompartment() { return mainCompartment; }

  /**
   * Sets the main compartment for testing purposes.
   * @param aCompartment the compartment to set
   */
  public void setMainCompartment(Compartment aCompartment) {
    mainCompartment = aCompartment;
  }

  /**
   * Returns the attributes compartment for testing purposes.
   * @return the attributes compartment
   */
  public Compartment getAttributesCompartment() {
    return attributesCompartment;
  }

  /**
   * Sets the attributes compartment for testing purposes.
   * @param aCompartment the compartment to set
   */
  public void setAttributesCompartment(Compartment aCompartment) {
    attributesCompartment = aCompartment;
  }

  /**
   * Returns the operations compartment for testing purposes.
   * @return the operations compartment
   */
  public Compartment getOperationsCompartment() {
    return operationsCompartment;
  }

  /**
   * Sets the operations compartment for testing purposes.
   * @param aCompartment the compartment to set
   */
  public void setOperationsCompartment(Compartment aCompartment) {
    operationsCompartment = aCompartment;
  }

  /**
   * Sets the model element.
   * @param aModelElement the model element
   */
  public void setModelElement(UmlClass aModelElement) {
    classData = aModelElement;
    if (classData != null) {
      classData.addModelElementListener(this);
    }
  }

  /**
   * {@inheritDoc}
   */
  public UmlModelElement getModelElement() { return classData; }

  /**
   * Sets the showAttributes flag.
   * @param flag true to show the attributes compartment, false otherwise
   */
  public void setShowAttributes(boolean flag) {
    if (showAttributes && !flag) {
      setHeight(getSize().getHeight() -
                attributesCompartment.getSize().getHeight());
    }
    showAttributes = flag;
    invalidate();
  }

  /**
   * Returns the value of the showAttributes attribute.
   * @return the value of the showAttributes attribute
   */
  public boolean showAttributes() { return showAttributes; }

  /**
   * Sets the showOperations flag.
   * @param flag true to show the operations compartment, false otherwise
   */
  public void setShowOperations(boolean flag) {
    if (showOperations && !flag) {
      setHeight(getSize().getHeight() -
                attributesCompartment.getSize().getHeight());
    }
    showOperations = flag;
    invalidate();
  }

  /**
   * Returns the value of the showOperations attribute.
   * @return the value of the showOperations attribute
   */
  public boolean showOperations() { return showOperations; }

  /**
   * Sets the showStereotypes flag.
   * @param flag the value
   */
  public void setShowStereotypes(boolean flag) {
    if (showStereotypes && !flag) {
      setHeight(getSize().getHeight() -
                mainCompartment.getSize().getHeight());
    }
    showStereotypes = flag;
    reinitMainCompartment();
    mainCompartment.addLabel(mainLabel);
    invalidate();
  }

  /**
   * Returns the value of the showStereotypes attribute.
   * @return the status of the showStereotypes attribute
   */
  public boolean showStereotypes() { return showStereotypes; }

  /**
   * {@inheritDoc}
   */
  public String getLabelText() { return getModelElement().getName(); }

  /**
   * {@inheritDoc}
   */
  public void setLabelText(String aText) { getModelElement().setName(aText); }

  /**
   * {@inheritDoc}
   */
  @Override
  public void draw(DrawingContext drawingContext) {
    if (!isValid()) {
      mainLabel.setFontType(getMainLabelFontType());
      recalculateSize(drawingContext);
    }
    mainCompartment.draw(drawingContext);
    if (showAttributes) attributesCompartment.draw(drawingContext);
    if (showOperations) operationsCompartment.draw(drawingContext);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void invalidate() {
    mainCompartment.invalidate();
    attributesCompartment.invalidate();
    operationsCompartment.invalidate();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isValid() {
    boolean result = mainCompartment.isValid();
    if (showAttributes) {
      result &= attributesCompartment.isValid();
    }
    if (showOperations) {
      result &= operationsCompartment.isValid();
    }
    return result;
  }

  /**
   * Returns the main label font type. Dependent whether the class is abstract
   * or not.
   * @return the FontType for the main label
   */
  private FontType getMainLabelFontType() {
    return (classData.isAbstract()) ? FontType.ABSTRACT_ELEMENT :
      FontType.ELEMENT_NAME;
  }

  /**
   * {@inheritDoc}
   */
  public Label getLabelAt(double mx, double my) {
    Label label = mainCompartment.getLabelAt(mx, my);
    if (label == null && showAttributes) {
      label = attributesCompartment.getLabelAt(mx, my);
    }
    if (label == null && showOperations) {
      label = operationsCompartment.getLabelAt(mx, my);
    }
    return label;
  }

  /**
   * {@inheritDoc}
   */
  public boolean acceptsConnection(RelationType associationType,
    RelationEndType as, UmlNode with) {
    return true;
  }

  /**
   * {@inheritDoc}
   */
  public void elementChanged(UmlModelElement element) {
    attributesCompartment.removeAllLabels();
    for (UmlProperty property : ((UmlClass) element).getAttributes()) {
      Label label = new SimpleLabel();
      label.setSource(new UmlModelElementLabelSource(property));
      attributesCompartment.addLabel(label);
    }
    operationsCompartment.removeAllLabels();
    for (UmlProperty property : ((UmlClass) element).getMethods()) {
      Label label = new SimpleLabel();
      label.setSource(new UmlModelElementLabelSource(property));
      operationsCompartment.addLabel(label);
    }
    reinitMainCompartment();
    invalidate();
  }

  /**
   * Reinitializes the main compartment with the contained labels.
   */
  private void reinitMainCompartment() {
    UmlClass umlclass = (UmlClass) getModelElement();
    mainCompartment.removeAllLabels();
    if (showStereotypes) {
      for (UmlStereotype stereotype : umlclass.getStereotypes()) {
        Label label = new SimpleLabel();
        label.setSource(new UmlModelElementLabelSource(stereotype));
        mainCompartment.addLabel(label);
      }
    }
    mainCompartment.addLabel(mainLabel);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return getModelElement().getName();
  }

  // ************************************************************************
  // ****** Size calculation
  // *********************************

  /**
   * {@inheritDoc}
   */
  @Override
  public Dimension2D getMinimumSize() {
    return new DoubleDimension(calculateMinimumWidth(),
                               calculateMinimumHeight());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void recalculateSize(DrawingContext drawingContext) {
    recalculateMainCompartment(drawingContext);
    recalculateAttributesCompartment(drawingContext);
    recalculateOperationsCompartment(drawingContext);
    double totalWidth = calculateTotalWidth();
    // do not invalidate the sub elements
    setSizePlain(totalWidth, calculateTotalHeight());
    // adjust main compartment, in case the other compartments made this
    // element wider
    mainCompartment.setWidth(totalWidth);
    attributesCompartment.setWidth(totalWidth);
    resizeLastCompartmentToFit();
    notifyNodeResized();
  }

  /**
   * Recalculates the main compartment.
   * @param drawingContext the DrawingContext
   */
  private void recalculateMainCompartment(DrawingContext drawingContext) {
    mainLabel.setFontType(getMainLabelFontType());
    mainCompartment.recalculateSize(drawingContext);
  }

  /**
   * Recalculates the attributes compartment.
   * @param drawingContext the DrawingContext
   */
  private void recalculateAttributesCompartment(DrawingContext drawingContext) {
    attributesCompartment.recalculateSize(drawingContext);
    attributesCompartment.setOrigin(0, getMainCompartmentHeight());
  }

  /**
   * Recalculates the operations compartment.
   * @param drawingContext the DrawingContext
   */
  private void recalculateOperationsCompartment(DrawingContext drawingContext) {
    operationsCompartment.recalculateSize(drawingContext);
    operationsCompartment.setOrigin(0, getOperationsY());
  }

  /**
   * Returns the y position for the operations compartment.
   * @return the y position for the operations compartment
   */
  private double getOperationsY() {
    double result = getMainCompartmentHeight();
    if (showAttributes) result += attributesCompartment.getSize().getHeight();
    return result;
  }

  /**
   * Determines the total height of this element.
   * @return the total height
   */
  private double calculateTotalHeight() {
    double compartmentHeightSum = getMainCompartmentHeight();
    if (showAttributes) {
      compartmentHeightSum +=
        attributesCompartment.getMinimumSize().getHeight();
    }
    if (showOperations) {
      compartmentHeightSum +=
        operationsCompartment.getMinimumSize().getHeight();
    }
    return Math.max(compartmentHeightSum, getSize().getHeight());
  }

  /**
   * Determines the total width of this element.
   * @return the total width
   */
  private double calculateTotalWidth() {
    double maxwidth = Math.max(mainCompartment.getSize().getWidth(),
      getSize().getWidth());
    if (showAttributes) {
      maxwidth = Math.max(maxwidth, attributesCompartment.getSize().getWidth());
    }
    if (showOperations) {
      maxwidth = Math.max(maxwidth, operationsCompartment.getSize().getWidth());
    }
    return maxwidth;
  }

  /**
   * Calculates the minimum width of this element.
   * @return the minimum width
   */
  private double calculateMinimumWidth() {
    double minimumWidth = mainCompartment.getMinimumSize().getWidth();
    if (showAttributes) {
      minimumWidth = Math.max(minimumWidth,
        attributesCompartment.getMinimumSize().getWidth());
    }
    if (showOperations) {
      minimumWidth = Math.max(minimumWidth,
        operationsCompartment.getMinimumSize().getWidth());
    }
    return minimumWidth;
  }

  /**
   * Calculates the minimum height of this element.
   * @return the minimum height
   */
  private double calculateMinimumHeight() {
    double minimumHeight = mainCompartment.getMinimumSize().getHeight();
    if (showAttributes) {
      minimumHeight += attributesCompartment.getMinimumSize().getHeight();
    }
    if (showOperations) {
      minimumHeight += operationsCompartment.getMinimumSize().getHeight();
    }
    return minimumHeight;
  }

  /**
   * Resizes the last visible compartment to fit within the total height.
   */
  private void resizeLastCompartmentToFit() {
    Compartment lastCompartment = getLastVisibleCompartment();
    double diffHeight = getSize().getHeight() - getCompartmentHeightSum();
    lastCompartment.setHeight(lastCompartment.getSize().getHeight() +
                              diffHeight);
  }

  /**
   * Returns the last visible compartment.
   * @return the last visible compartment
   */
  private Compartment getLastVisibleCompartment() {
    Compartment lastCompartment = mainCompartment;
    if (showAttributes) lastCompartment = attributesCompartment;
    if (showOperations) lastCompartment = operationsCompartment;
    return lastCompartment;
  }

  /**
   * Returns the sum of compartment heights.
   * @return the sum of compartment heights
   */
  private double getCompartmentHeightSum() {
    double result = mainCompartment.getSize().getHeight();
    if (showAttributes) {
      result += attributesCompartment.getSize().getHeight();
    }
    if (showOperations) {
      result += operationsCompartment.getSize().getHeight();
    }
    return result;
  }

  /**
   * Returns the height of the compartment.
   * @return the height of the compartment
   */
  private double getMainCompartmentHeight() {
    return mainCompartment.getSize().getHeight();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isNestable() { return true; }
}
