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

import java.awt.Color;
import java.awt.geom.Dimension2D;
import java.util.List;
import org.tinyuml.draw.AbstractCompositeNode;
import org.tinyuml.draw.Compartment;
import org.tinyuml.draw.DiagramElement;
import org.tinyuml.draw.DrawingContext.FontType;
import org.tinyuml.draw.DrawingContext;
import org.tinyuml.draw.Label;
import org.tinyuml.draw.LabelSource;
import org.tinyuml.model.RelationEndType;
import org.tinyuml.model.UmlModelElement;
import org.tinyuml.model.UmlPackage;
import org.tinyuml.draw.DoubleDimension;
import org.tinyuml.draw.SimpleLabel;
import org.tinyuml.model.RelationType;
import org.tinyuml.umldraw.shared.UmlNode;

/**
 * This class implements a representation for the UmlPackage class.
 *
 * @author Wei-ju Wu
 * @version 1.0
 */
public final class PackageElement extends AbstractCompositeNode
implements LabelSource, UmlNode {

  private static final long serialVersionUID = -601178725325007248L;
  private static final double TAB_MIN_WIDTH = 30;
  private static final double TAB_MIN_HEIGHT = 12;
  // This is a nice background color for a package
  private static final Color BACKGROUND = new Color(220, 230, 240);

  private UmlPackage pkg;
  private Compartment tabCompartment, mainCompartment;
  private Label mainLabel = new SimpleLabel();

  private static PackageElement prototype;

  /**
   * Returns the prototype instance.
   * @return the prototype instance
   */
  public static PackageElement getPrototype() {
    if (prototype == null) prototype = new PackageElement();
    return prototype;
  }

  /**
   * Constructor.
   */
  private PackageElement() {
    tabCompartment = new Compartment();
    tabCompartment.setParent(this);
    tabCompartment.setBackground(BACKGROUND);
    tabCompartment.setAspectToParentWidth(0.33);
    mainCompartment = new Compartment();
    mainCompartment.setParent(this);
    mainCompartment.setBackground(BACKGROUND);
    mainLabel.setParent(this);
    mainLabel.setFontType(FontType.ELEMENT_NAME);
    mainLabel.setSource(this);
    mainCompartment.addLabel(mainLabel);
    tabCompartment.setOrigin(0, 0);
    tabCompartment.setMinimumSize(TAB_MIN_WIDTH, TAB_MIN_HEIGHT);
    tabCompartment.setSize(TAB_MIN_WIDTH, TAB_MIN_HEIGHT);
    mainCompartment.setOrigin(0, TAB_MIN_HEIGHT);
    mainCompartment.setSize(120, 42); // size = 120, 60
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Object clone() {
    // cloning has to be done very carefully: the parent and label source
    // relationships have to be reconstructed for the new element
    PackageElement cloned = (PackageElement) super.clone();
    cloned.mainLabel = (Label) mainLabel.clone();
    cloned.mainLabel.setSource(cloned);
    cloned.tabCompartment = (Compartment) tabCompartment.clone();
    cloned.tabCompartment.setParent(cloned);
    cloned.mainCompartment = (Compartment) mainCompartment.clone();
    cloned.mainCompartment.setParent(cloned);
    cloned.mainCompartment.removeAllLabels();
    cloned.mainCompartment.addLabel(cloned.mainLabel);
    if (pkg != null) {
      cloned.pkg = (UmlPackage) pkg.clone();
    }
    return cloned;
  }

  /**
   * Returns the main label, exposed for testing only.
   * @return the main label
   */
  public Label getMainLabel() { return mainLabel; }

  /**
   * Returns the compartment that displays the tab. For testing purposes only.
   * @return the tab compartment
   */
  public Compartment getTabCompartment() { return tabCompartment; }

  /**
   * Returns the main compartment. For testing purposes only.
   * @return the main compartment
   */
  public Compartment getMainCompartment() { return mainCompartment; }

  /**
   * Sets the model element.
   * @param aPackage the model package element
   */
  public void setUmlPackage(UmlPackage aPackage) { pkg = aPackage; }

  /**
   * {@inheritDoc}
   */
  public UmlModelElement getModelElement() { return pkg; }

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
  public void recalculateSize(DrawingContext drawingContext) {
    tabCompartment.recalculateSize(drawingContext);
    mainCompartment.recalculateSize(drawingContext);
    notifyNodeResized();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void draw(DrawingContext drawingContext) {
    if (!isValid()) {
      recalculateSize(drawingContext);
    }
    tabCompartment.draw(drawingContext);
    mainCompartment.draw(drawingContext);
  }

  /**
   * {@inheritDoc}
   */
  public Label getLabelAt(double mx, double my) {
    Label labelAt = tabCompartment.getLabelAt(mx, my);
    if (labelAt == null) labelAt = mainCompartment.getLabelAt(mx, my);
    return labelAt;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Dimension2D getMinimumSize() {
    Dimension2D minTabSize = tabCompartment.getMinimumSize();
    Dimension2D minMainSize = mainCompartment.getMinimumSize();
    return new DoubleDimension(
      Math.max(minTabSize.getWidth(), minMainSize.getWidth()),
      minTabSize.getHeight() + minMainSize.getHeight());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Dimension2D getSize() {
    Dimension2D tabSize = tabCompartment.getSize();
    Dimension2D mainSize = mainCompartment.getSize();
    return new DoubleDimension(
      Math.max(tabSize.getWidth(), mainSize.getWidth()),
      tabSize.getHeight() + mainSize.getHeight());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setMinimumSize(double width, double height) {
    throw new UnsupportedOperationException("setMinimumSize() not supported");
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setSize(double width, double height) {
    double mainHeight = height - TAB_MIN_HEIGHT;
    mainCompartment.setSize(width, mainHeight);
    invalidate();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isValid() {
    return mainCompartment.isValid() && tabCompartment.isValid();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void invalidate() {
    mainCompartment.invalidate();
    tabCompartment.invalidate();
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
  @Override
  public void addChild(DiagramElement element) {
    mainCompartment.addChild(element);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void removeChild(DiagramElement element) {
    mainCompartment.removeChild(element);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public DiagramElement getChildAt(double xcoord, double ycoord) {
    return mainCompartment.getChildAt(xcoord, ycoord);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<DiagramElement> getChildren() {
    return mainCompartment.getChildren();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean canNestElements() { return true; }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isNestable() { return true; }
}
