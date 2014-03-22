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
import java.awt.geom.GeneralPath;
import org.tinyuml.draw.AbstractCompositeNode;
import org.tinyuml.draw.DrawingContext;
import org.tinyuml.draw.Label;
import org.tinyuml.draw.LabelSource;
import org.tinyuml.draw.SimpleLabel;
import org.tinyuml.model.Relation.ReadingDirection;
import org.tinyuml.model.UmlRelation;

/**
 * This class implements a name label for an association. Associations have
 * special name labels which can display an arrow for reading directions.
 *
 * @author Wei-ju Wu
 * @version 1.0
 */
public class AssociationNameLabel extends AbstractCompositeNode
implements Label, LabelSource {

  private Label label;
  private Association association;

  /**
   * Constructor.
   */
  public AssociationNameLabel() {
    setLabel(new SimpleLabel());
  }

  /**
   * Returns the wrapped label.
   * @return the wrapped label
   */
  public Label getLabel() { return label; }

  /**
   * Sets a Label. This method is exposed for unit testing.
   * @param aLabel the label
   */
  public void setLabel(Label aLabel) {
    label = aLabel;
    label.setSource(this);
    label.setParent(this);
  }

  /**
   * Sets the association.
   * @param assoc the Association
   */
  public void setAssociation(Association assoc) {
    association = assoc;
  }

  /**
   * {@inheritDoc}
   */
  public Label getLabelAt(double mx, double my) {
    if (contains(mx, my)) return this;
    return null;
  }

  /**
   * {@inheritDoc}
   */
  public void setSource(LabelSource aSource) { }

  /**
   * {@inheritDoc}
   */
  public LabelSource getSource() { return this; }

  /**
   * {@inheritDoc}
   */
  public String getText() { return label.getText(); }

  /**
   * {@inheritDoc}
   */
  public void setText(String text) { label.setText(text); }

  /**
   * {@inheritDoc}
   */
  public void setFontType(DrawingContext.FontType aFontType) {
    label.setFontType(aFontType);
  }

  /**
   * {@inheritDoc}
   */
  public void centerHorizontally() { label.centerHorizontally(); }

  /**
   * {@inheritDoc}
   */
  @Override
  public void draw(DrawingContext drawingContext) {
    if (getLabelText() != null) {
      label.draw(drawingContext);
      drawDirection(drawingContext);
    }
  }

  /**
   * Draws the direction triangle.
   * @param drawingContext the drawing context
   */
  private void drawDirection(DrawingContext drawingContext) {
    ReadingDirection readingDirection =
      ((UmlRelation)association.getModelElement()).getNameReadingDirection();
    if (readingDirection == ReadingDirection.LEFT_RIGHT) {
      drawTriangleLeftRight(drawingContext);
    } else if (readingDirection == ReadingDirection.RIGHT_LEFT) {
      drawTriangleRightLeft(drawingContext);
    }
  }

  /**
   * Draws the triangle facing to the right.
   * @param drawingContext the drawing context
   */
  private void drawTriangleLeftRight(DrawingContext drawingContext) {
    GeneralPath trianglePath = new GeneralPath();
    double height = label.getSize().getHeight() - 6;
    double x = label.getAbsoluteX2() + 3, y = label.getAbsoluteY1() + 3;
    trianglePath.moveTo(x, y);
    trianglePath.lineTo(x + 5, y + height / 2);
    trianglePath.lineTo(x, y + height);
    trianglePath.closePath();
    drawingContext.draw(trianglePath, Color.BLACK);
  }

  /**
   * Draws the triangle facing to the left.
   * @param drawingContext the drawing context
   */
  private void drawTriangleRightLeft(DrawingContext drawingContext) {
    GeneralPath trianglePath = new GeneralPath();
    double height = label.getSize().getHeight() - 6;
    double x = label.getAbsoluteX1() - 3, y = label.getAbsoluteY1() + 3;
    trianglePath.moveTo(x, y);
    trianglePath.lineTo(x - 5, y + height / 2);
    trianglePath.lineTo(x, y + height);
    trianglePath.closePath();
    drawingContext.draw(trianglePath, Color.BLACK);
  }

  /**
   * {@inheritDoc}
   */
  public String getLabelText() {
    if (association.getModelElement() == null) return "";
    return association.getModelElement().getName();
  }

  /**
   * {@inheritDoc}
   */
  public void setLabelText(String aText) {
    if (association.getModelElement() != null) {
      association.getModelElement().setName(aText);
    }
  }
}
