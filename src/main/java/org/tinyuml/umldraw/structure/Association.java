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
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.List;
import org.tinyuml.draw.CompositeNode;
import org.tinyuml.draw.DrawingContext;
import org.tinyuml.draw.Label;
import org.tinyuml.draw.LabelSource;
import org.tinyuml.draw.Node;
import org.tinyuml.draw.RectilinearConnection;
import org.tinyuml.model.Relation;
import org.tinyuml.umldraw.shared.BaseConnection;
import org.tinyuml.draw.SimpleArrowTip;
import org.tinyuml.draw.SimpleLabel;

/**
 * This class implements an association connection. The association connection
 * can be configured to display either one of an unspecified association,
 * an aggregation or a composition, they mainly differ by the displayed
 * decorations.
 *
 * @author Wei-ju Wu
 * @version 1.0
 */
public final class Association extends BaseConnection {

  private static final long serialVersionUID = 1866495594812659939L;
  private static Association prototype;

  /**
   * The possible association types.
   */
  public enum AssociationType { COMPOSITION, AGGREGATION, UNSPECIFIED };
  private AssociationType associationType = AssociationType.UNSPECIFIED;
  private Label multiplicity1Label;
  private Label multiplicity2Label;
  private AssociationNameLabel nameLabel;
  private boolean showMultiplicities, showName;

  /**
   * Returns the prototype instance.
   * @return the prototype instance
   */
  public static Association getPrototype() {
    if (prototype == null) prototype = new Association();
    return prototype;
  }

  /**
   * Constructor.
   */
  private Association() {
    setConnection(new RectilinearConnection());
    setupMultiplicityLabels();
    setupNameLabel();
  }

  /**
   * Returns the value of the showName property.
   * @return the value of the showName property
   */
  public boolean showName() { return showName; }

  /**
   * Sets the showName property.
   * @param flag the value of the showName property
   */
  public void setShowName(boolean flag) { showName = flag; }

  /**
   * Returns the value of the showMultiplicities property.
   * @return the value of the showMultiplicities property
   */
  public boolean showMultiplicities() { return showMultiplicities; }

  /**
   * Sets the showMultiplicities property.
   * @param flag the value of the showMultiplicities property
   */
  public void setShowMultiplicities(boolean flag) { showMultiplicities = flag; }

  /**
   * {@inheritDoc}
   */
  @Override
  public Object clone() {
    Association cloned = (Association) super.clone();
    // readjust the multiplicity labels, they do not point to the correct
    // label sources after a clone()
    cloned.setupMultiplicityLabels();
    cloned.setupNameLabel();
    cloned.multiplicity1Label.setParent(multiplicity1Label.getParent());
    cloned.multiplicity2Label.setParent(multiplicity2Label.getParent());
    cloned.nameLabel.setParent(nameLabel.getParent());
    return cloned;
  }

  /**
   * Sets the name label.
   */
  public void setupNameLabel() {
    nameLabel = new AssociationNameLabel();
    nameLabel.setAssociation(this);
  }

  /**
   * Sets the multiplicity label sources.
   */
  private void setupMultiplicityLabels() {
    multiplicity1Label = new SimpleLabel();
    multiplicity1Label.setSource(new LabelSource() {
      /**
       * {@inheritDoc}
       */
      public String getLabelText() {
        return getRelation().getElement1Multiplicity().toString();
      }

      /**
       * {@inheritDoc}
       */
      public void setLabelText(String aText) { }
    });

    multiplicity2Label = new SimpleLabel();
    multiplicity2Label.setSource(new LabelSource() {
      /**
       * {@inheritDoc}
       */
      public String getLabelText() {
        return getRelation().getElement2Multiplicity().toString();
      }

      /**
       * {@inheritDoc}
       */
      public void setLabelText(String aText) { }
    });
  }

  /**
   * Returns the name label.
   * @return the name label
   */
  public Label getNameLabel() { return nameLabel; }

  /**
   * Returns the multiplicity label for element 1.
   * @return the multiplicity label for element 1
   */
  public Label getMultiplicity1Label() { return multiplicity1Label; }

  /**
   * Returns the multiplicity label for element 2.
   * @return the multiplicity label for element 2
   */
  public Label getMultiplicity2Label() { return multiplicity2Label; }

  /**
   * Returns the model element, which is always an instance of Relation.
   * @return the model element
   */
  private Relation getRelation() { return (Relation) getModelElement(); }

  /**
   * Returns the AssociationType.
   * @return the AssociationType
   */
  public AssociationType getAssociationType() { return associationType; }

  /**
   * Sets the AssociationType.
   * @param anAssociationType the AssociationType
   */
  public void setAssociationType(AssociationType anAssociationType) {
    associationType = anAssociationType;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setParent(CompositeNode parent) {
    super.setParent(parent);
    multiplicity1Label.setParent(parent);
    multiplicity2Label.setParent(parent);
    nameLabel.setParent(parent);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void draw(DrawingContext drawingContext) {
    super.draw(drawingContext);
    if (associationType == AssociationType.COMPOSITION) {
      drawRombus(drawingContext, calculateRotationInEndPoint1(), true);
    } else if (associationType == AssociationType.AGGREGATION) {
      drawRombus(drawingContext, calculateRotationInEndPoint1(), false);
    }
    drawNavigabilityArrows(drawingContext);
    drawLabels(drawingContext);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Label getLabelAt(double xcoord, double ycoord) {
    return null;
  }

  /**
   * {@inheritDoc}
   */
  private void drawRombus(DrawingContext drawingContext,
    AffineTransform rotationTransform, boolean filled) {
    Point2D endpoint = getEndPoint1();
    double x = endpoint.getX(), y = endpoint.getY();
    GeneralPath rombus = new GeneralPath();
    rombus.moveTo(x - 14, y);
    rombus.lineTo(x - 7, y - 5);
    rombus.lineTo(x, y);
    rombus.lineTo(x - 7, y + 5);
    rombus.closePath();
    rombus.transform(rotationTransform);
    Color fillColor = filled ? Color.BLACK : Color.WHITE;
    drawingContext.draw(rombus, fillColor);
  }

  /**
   * Draws the navigability arrows.
   * @param drawingContext the DrawingContext
   */
  private void drawNavigabilityArrows(DrawingContext drawingContext) {
    if (getRelation().isNavigableToElement1()) {
      drawArrow(drawingContext, getEndPoint1(), calculateRotationInEndPoint1());
    }
    if (getRelation().isNavigableToElement2()) {
      drawArrow(drawingContext, getEndPoint2(), calculateRotationInEndPoint2());
    }
  }

  /**
   * Draws the arrow.
   * @param drawingContext the drawing context
   * @param endpoint the end point to draw the arrow at
   * @param rotationTransform the rotation
   */
  private void drawArrow(DrawingContext drawingContext, Point2D endpoint,
    AffineTransform rotationTransform) {
    new SimpleArrowTip().draw(drawingContext, endpoint, rotationTransform);
  }

  /**
   * Draws the connection labels.
   * @param drawingContext the DrawingContext
   */
  private void drawLabels(DrawingContext drawingContext) {
    if (showMultiplicities) {
      positionLabel(multiplicity1Label, getNode1(), getEndPoint1());
      positionLabel(multiplicity2Label, getNode2(), getEndPoint2());
      multiplicity1Label.draw(drawingContext);
      multiplicity2Label.draw(drawingContext);
    }
    if (showName) {
      positionNameLabel();
      nameLabel.draw(drawingContext);
    }
  }

  /**
   * Positions a label relative to an endpoint.
   * @param label the label
   * @param node the node
   * @param endpoint the end point
   */
  private void positionLabel(Label label, Node node, Point2D endpoint) {
    Direction direction = getPointDirection(node, endpoint);
    double x, y;
    switch (direction) {
      case NORTH:
        x = endpoint.getX() + 5;
        y = endpoint.getY() - label.getSize().getHeight();
        break;
      case SOUTH:
        x = endpoint.getX() + 5;
        y = endpoint.getY() + 5;
        break;
      case EAST:
        x = endpoint.getX() + 5;
        y = endpoint.getY() + 5;
        break;
      case WEST:
      default:
        x = endpoint.getX() - label.getSize().getWidth();
        y = endpoint.getY() + 5;
        break;
    }
    label.setAbsolutePos(x, y);
  }

  /**
   * A direction of an end point relative to its connected node.
   */
  private enum Direction  { NORTH, SOUTH, EAST, WEST }

  /**
   * Determines the direction the point is relative to the node.
   * @param node the node
   * @param point the point
   * @return the direction
   */
  private Direction getPointDirection(Node node, Point2D point) {
    if (point.getX() >= node.getAbsoluteX2()) {
      return Direction.EAST;
    }
    if (point.getX() <= node.getAbsoluteX1()) {
      return Direction.WEST;
    }
    if (point.getY() <= node.getAbsoluteY1()) {
      return Direction.NORTH;
    }
    return Direction.SOUTH;
  }

  /**
   * Sets the position for the name label.
   */
  private void positionNameLabel() {
    // medium segment
    List<Line2D> segments = getSegments();
    Line2D middlesegment = segments.get(segments.size() / 2);
    int x = (int) (middlesegment.getX2() + middlesegment.getX1()) / 2;
    int y = (int) (middlesegment.getY2() + middlesegment.getY1()) / 2;
    nameLabel.setAbsolutePos(x, y);
  }
}
