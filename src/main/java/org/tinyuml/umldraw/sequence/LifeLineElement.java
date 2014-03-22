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
package org.tinyuml.umldraw.sequence;

import java.awt.geom.Dimension2D;
import org.tinyuml.draw.AbstractCompositeNode;
import org.tinyuml.draw.Compartment;
import org.tinyuml.draw.DrawingContext;
import org.tinyuml.draw.DrawingContext.FontType;
import org.tinyuml.draw.Label;
import org.tinyuml.draw.LabelSource;
import org.tinyuml.draw.SimpleLabel;
import org.tinyuml.model.RelationEndType;
import org.tinyuml.model.RelationType;
import org.tinyuml.model.UmlLifeLine;
import org.tinyuml.model.UmlModelElement;
import org.tinyuml.umldraw.shared.UmlNode;

/**
 * This class implements a LifeLine.
 *
 * @author Wei-ju Wu
 * @version 1.0
 */
public final class LifeLineElement extends AbstractCompositeNode
implements LabelSource, UmlNode {

  private static final long serialVersionUID = 8132765736777401595L;
  private static LifeLineElement prototype;
  private Compartment mainCompartment;
  private UmlLifeLine lifeline;

  /**
   * Returns the prototype instance.
   * @return the prototype instance
   */
  public static LifeLineElement getPrototype() {
    if (prototype == null) prototype = new LifeLineElement();
    return prototype;
  }

  /**
   * Private constructor.
   */
  private LifeLineElement() {
    mainCompartment = new Compartment();
    mainCompartment.setParent(this);
    Label mainLabel = new SimpleLabel();
    mainLabel.setSource(this);
    mainLabel.setFontType(FontType.ELEMENT_NAME);
    mainCompartment.addLabel(mainLabel);
  }

  /**
   * Sets the model element.
   * @param aLifeLine an UmlLifeLine object
   */
  public void setModelElement(UmlLifeLine aLifeLine) {
    lifeline = aLifeLine;
  }

  /**
   * {@inheritDoc}
   */
  public Label getLabelAt(double mx, double my) {
    return mainCompartment.getLabelAt(mx, my);
  }

  /**
   * {@inheritDoc}
   */
  public String getLabelText() {
    return lifeline.getName();
  }

  /**
   * {@inheritDoc}
   */
  public void setLabelText(String aName) {
    lifeline.setName(aName);
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
  public UmlModelElement getModelElement() { return lifeline; }

  /**
   * {@inheritDoc}
   */
  public Compartment getMainCompartment() { return mainCompartment; }

  /**
   * Set the main compartment externally for testing.
   * @param aCompartment the compartment
   */
  public void setMainCompartment(Compartment aCompartment) {
    mainCompartment = aCompartment;
  }

   /**
   * {@inheritDoc}
   */
  @Override
  public void invalidate() { mainCompartment.invalidate(); }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isValid() { return mainCompartment.isValid(); }

  /**
   * {@inheritDoc}
   */
  @Override
  public void recalculateSize(DrawingContext drawingContext) {
    mainCompartment.recalculateSize(drawingContext);
    notifyNodeResized();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Dimension2D getMinimumSize() {
    return mainCompartment.getMinimumSize();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setMinimumSize(double width, double height) {
    mainCompartment.setMinimumSize(width, height);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Dimension2D getSize() {
    return mainCompartment.getSize();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setSize(double width, double height) {
    mainCompartment.setSize(width, height);
  }

 /**
   * {@inheritDoc}
   */
  @Override
  public Object clone() {
    LifeLineElement cloned = (LifeLineElement) super.clone();
    cloned.mainCompartment = (Compartment) mainCompartment.clone();
    cloned.mainCompartment.setParent(cloned);
    if (cloned.getModelElement() != null) {
      cloned.setModelElement((UmlLifeLine) getModelElement().clone());
    }
    Label mainLabel = new SimpleLabel();
    mainLabel.setSource(cloned);
    mainLabel.setFontType(FontType.ELEMENT_NAME);
    cloned.mainCompartment.getLabels().clear();
    cloned.mainCompartment.addLabel(mainLabel);
    return cloned;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void draw(DrawingContext drawingContext) {
    if (!isValid()) {
      recalculateSize(drawingContext);
    }
    mainCompartment.draw(drawingContext);
    // draw a funny dashed line
    double x = mainCompartment.getAbsCenterX();
    drawingContext.drawDashedLine(x, mainCompartment.getAbsoluteY2(), x,
      mainCompartment.getAbsoluteY2() + 100);
  }
}
