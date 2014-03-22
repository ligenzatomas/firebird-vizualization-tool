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
package org.tinyuml.umldraw.shared;

import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;
import org.tinyuml.draw.CompositeNode;
import org.tinyuml.draw.Connection;
import org.tinyuml.draw.DiagramElement;
import org.tinyuml.draw.DiagramOperations;
import org.tinyuml.draw.DrawingContext;
import org.tinyuml.draw.Label;
import org.tinyuml.draw.LineConnectMethod;
import org.tinyuml.draw.Node;
import org.tinyuml.draw.Selection;
import org.tinyuml.model.Relation;
import org.tinyuml.model.UmlModelElement;

/**
 * UML specific connections wrap the connections defined in the draw package
 * and specify how they should be rendered, possibly with additions of
 * decorations like arrow heads.
 *
 * @author Wei-ju Wu
 * @version 1.0
 */
public class BaseConnection implements UmlConnection {

  private static final long serialVersionUID = 4796693000723361980L;
  private Relation relation;
  private Connection connection;

  /**
   * Makes the Constructor protected to prevent direct instantiation.
   */
  protected BaseConnection() { }

  /**
   * {@inheritDoc}
   */
  public boolean isRectilinear() { return connection.isRectilinear(); }

  /**
   * {@inheritDoc}
   */
  public boolean isDashed() { return connection.isDashed(); }

  /**
   * {@inheritDoc}
   */
  public LineConnectMethod getConnectMethod() {
    return connection.getConnectMethod();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Object clone() {
    BaseConnection cloned = null;
    try {
      cloned = (BaseConnection) super.clone();
      if (relation != null) {
        cloned.relation = (Relation) relation.clone();
      }
      if (connection != null) {
        cloned.connection = (Connection) connection.clone();
      }
    } catch (CloneNotSupportedException ignore) {
      ignore.printStackTrace();
    }
    return cloned;
  }

  /**
   * {@inheritDoc}
   */
  public void setRelation(Relation aRelation) { relation = aRelation; }

  /**
   * Sets the Connection object.
   * @param aConnection the Connection object
   */
  public void setConnection(Connection aConnection) {
    connection = aConnection;
  }

  /**
   * Returns the wrapped connection object.
   * @return the connection
   */
  public Connection getConnection() { return connection; }

  /**
   * {@inheritDoc}
   */
  public UmlModelElement getModelElement() { return relation; }

  /**
   * Returns the Node 0 element.
   * @return node 0
   */
  public Node getNode1() { return connection.getNode1(); }

  /**
   * Sets the node 0 element.
   * @param aNode the node 0 element
   */
  public void setNode1(Node aNode) { connection.setNode1(aNode); }

  /**
   * Returns the Node 1 element.
   * @return node 1
   */
  public Node getNode2() { return connection.getNode2(); }

  /**
   * Sets the node 1 element of the connection.
   * @param aNode the node 1 element
   */
  public void setNode2(Node aNode) { connection.setNode2(aNode); }

  /**
   * Draws the connection.
   * @param drawingContext the DrawingContext
   */
  public void draw(DrawingContext drawingContext) {
    connection.draw(drawingContext);
  }

  /**
   * {@inheritDoc}
   */
  public List<Point2D> getPoints() { return connection.getPoints(); }

  /**
   * {@inheritDoc}
   */
  public void resetPoints() { connection.resetPoints(); }

  /**
   * {@inheritDoc}
   */
  public List<Line2D> getSegments() { return connection.getSegments(); }

  /**
   * {@inheritDoc}
   */
  public Line2D getSegmentAtPoint(double xcoord, double ycoord) {
    return connection.getSegmentAtPoint(xcoord, ycoord);
  }

  /**
   * {@inheritDoc}
   */
  public boolean contains(double x, double y) {
    return connection.contains(x, y);
  }

  /**
   * {@inheritDoc}
   */
  public boolean intersects(Rectangle2D bounds) {
    return connection.intersects(bounds);
  }

  /**
   * {@inheritDoc}
   */
  public Rectangle2D getAbsoluteBounds() {
    return connection.getAbsoluteBounds();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return relation == null ? super.toString() : relation.toString();
  }

  /**
   * {@inheritDoc}
   */
  public boolean isVisible(Rectangle2D bounds) {
    return connection.isVisible(bounds);
  }

  /**
   * {@inheritDoc}
   */
  public CompositeNode getParent() { return connection.getParent(); }

  /**
   * {@inheritDoc}
   */
  public boolean isAncestor(DiagramElement element) {
    return connection.isAncestor(element);
  }

  /**
   * {@inheritDoc}
   */
  public void setParent(CompositeNode aParent) {
    connection.setParent(aParent);
  }

  /**
   * {@inheritDoc}
   */
  public void recalculateSize(DrawingContext drawingContext) {
    connection.recalculateSize(drawingContext);
  }

  /**
   * {@inheritDoc}
   */
  public Selection getSelection(DiagramOperations operations) {
    return new UmlConnectionSelection(this,
      connection.getSelection(operations));
  }

  /**
   * {@inheritDoc}
   */
  public Label getLabelAt(double mx, double my) { return null; }


  /**
   * {@inheritDoc}
   */
  public boolean isValid() { return connection.isValid(); }

  /**
   * {@inheritDoc}
   */
  public void invalidate() { connection.invalidate(); }

  /**
   * {@inheritDoc}
   */
  public void setIsDashed(boolean flag) { connection.setIsDashed(flag); }

  /**
   * {@inheritDoc}
   */
  public void setPoints(List<Point2D> thePoints) {
    connection.setPoints(thePoints);
  }

  /**
   * {@inheritDoc}
   */
  public AffineTransform calculateRotationInEndPoint1() {
    return connection.calculateRotationInEndPoint1();
  }

  /**
   * {@inheritDoc}
   */
  public AffineTransform calculateRotationInEndPoint2() {
    return connection.calculateRotationInEndPoint2();
  }

  /**
   * {@inheritDoc}
   */
  public Point2D getEndPoint1() { return connection.getEndPoint1(); }

  /**
   * {@inheritDoc}
   */
  public Point2D getEndPoint2() { return connection.getEndPoint2(); }

  /**
   * {@inheritDoc}
   */
  public void copyData(Connection conn) { connection.copyData(conn); }

  // *************************************************************************
  // ***** Nesting
  // ********************

  /**
   * {@inheritDoc}
   */
  public boolean isNestable() { return false; }

  /**
   * {@inheritDoc}
   */
  public boolean canNestElements() { return false; }
}
