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
package org.tinyuml.draw;

import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * This class represents the abstract super class that contains the common
 * implements of connection implementations.
 *
 * @author Wei-ju Wu
 * @version 1.0
 */
public abstract class AbstractConnection
implements Connection, NodeChangeListener {
	
	private static final long serialVersionUID = -5958109339344544721L;
  private Node node1, node2;
  protected static final double DELTA = 3.0;
  private CompositeNode parent;
  private boolean isValid;
  private boolean isDashed;
  private List<Point2D> points;

  /**
   * {@inheritDoc}
   */
  @Override
  public Object clone() {
    AbstractConnection cloned = null;
    try {
      cloned = (AbstractConnection) super.clone();
      if (points != null) {
        cloned.points = new LinkedList<Point2D>();
        for (Point2D point : points) cloned.points.add((Point2D) point.clone());
      }
    } catch (CloneNotSupportedException ignore) {
      ignore.printStackTrace();
    }
    return cloned;
  }

  /**
   * Returns the Node 0 element.
   * @return node 0
   */
  public Node getNode1() { return node1; }

  /**
   * {@inheritDoc}
   */
  public void setNode1(Node aNode) {
    if (node1 != null && aNode != node1) {
      node1.removeNodeChangeListener(this);
    }
    if (aNode != null) {
      aNode.addNodeChangeListener(this);
    }
    node1 = aNode;
  }

  /**
   * Returns the Node 1 element.
   * @return node 1
   */
  public Node getNode2() { return node2; }

  /**
   * Sets the node 1 element of the connection.
   * @param aNode the node 1 element
   */
  public void setNode2(Node aNode) {
    if (node2 != null && aNode != node2) {
      node2.removeNodeChangeListener(this);
    }
    if (aNode != null) {
      aNode.addNodeChangeListener(this);
    }
    node2 = aNode;
  }

  /**
   * {@inheritDoc}
   */
  public CompositeNode getParent() { return parent; }

  /**
   * {@inheritDoc}
   */
  public void setParent(CompositeNode aParent) { parent = aParent; }

  /**
   * {@inheritDoc}
   */
  public boolean isAncestor(DiagramElement element) {
    // connections only have one direct ancestor, so this is sufficient
    return element == getParent();
  }

  /**
   * {@inheritDoc}
   */
  public void recalculateSize(DrawingContext drawingContext) { }

  /**
   * {@inheritDoc}
   */
  public Rectangle2D getAbsoluteBounds() {
    double minx = Double.MAX_VALUE, miny = Double.MAX_VALUE,
      maxx = Double.MIN_VALUE, maxy = Double.MIN_VALUE;
    for (Point2D point : getPoints()) {
      minx = Math.min(minx, point.getX());
      miny = Math.min(miny, point.getY());
      maxx = Math.max(maxx, point.getX());
      maxy = Math.max(maxy, point.getY());
    }
    return new Rectangle2D.Double(minx, miny, maxx - minx, maxy - miny);
  }

  /**
   * {@inheritDoc}
   */
  public boolean intersects(Rectangle2D bounds) {
    List<Line2D> segments = getSegments();
    for (Line2D segment : segments) {
      if (segment.intersects(bounds)) return true;
    }
    return false;
  }

  /**
   * {@inheritDoc}
   */
  public void invalidate() { setValid(false); }

  /**
   * {@inheritDoc}
   */
  public boolean isValid() { return isValid; }

  /**
   * Sets the validity property.
   * @param flag the value of the validity property
   */
  public void setValid(boolean flag) { isValid = flag; }

  /**
   * {@inheritDoc}
   */
  public void setIsDashed(boolean flag) { isDashed = flag; }

  /**
   * Returns the status of the isDashed property.
   * @return the isDashed property
   */
  public boolean isDashed() { return isDashed; }

  /**
   * {@inheritDoc}
   */
  public void nodeMoved(Node node) { invalidate(); }

  /**
   * {@inheritDoc}
   */
  public void nodeResized(Node node) { invalidate(); }

  /**
   * {@inheritDoc}
   */
  public boolean isVisible(Rectangle2D bounds) { return true; }

  /**
   * {@inheritDoc}
   */
  public Label getLabelAt(double mx, double my) { return null; }

  /**
   * Given a line, derive an AffineTransform object from it to rotate arrow
   * heads.
   * @param aLine the line that specifies the direction
   * @param anchorPoint the rotation point
   * @return the AffineTransform object
   */
  private AffineTransform calculateRotationTransform(Line2D aLine,
    Point2D anchorPoint) {
    // Calculate the rotation vector by setting it to the origin
    double rvx = aLine.getX2() - aLine.getX1();
    double rvy = aLine.getY2() - aLine.getY1();
    AffineTransform affineTransform = new AffineTransform();
    affineTransform.setToIdentity();
    affineTransform.rotate(rvx, rvy, anchorPoint.getX(), anchorPoint.getY());
    return affineTransform;
  }

  /**
   * {@inheritDoc}
   */
  public AffineTransform calculateRotationInEndPoint1() {
    return calculateRotationTransform(getFirstSegmentToNode1(), getEndPoint1());
  }

  /**
   * {@inheritDoc}
   */
  public AffineTransform calculateRotationInEndPoint2() {
    return calculateRotationTransform(getLastSegmentToNode2(), getEndPoint2());
  }

  /**
   * Returns the line segment connecting to Node 1. The points in the line
   * segment are ordered in the direction of the node.
   * @return the first segment connecting to node 1
   */
  private Line2D getFirstSegmentToNode1() {
    Line2D firstSegment = new Line2D.Double();
    firstSegment.setLine(points.get(1), points.get(0));
    return firstSegment;
  }

  /**
   * Returns the line segment connecting to Node 2.
   * @return the line segment connecting to Node 2
   */
  private Line2D getLastSegmentToNode2() {
    Line2D lastSegment = new Line2D.Double();
    lastSegment.setLine(points.get(points.size() - 2),
                        points.get(points.size() - 1));
    return lastSegment;
  }

  /**
   * {@inheritDoc}
   */
  public Point2D getEndPoint1() { return getPoints().get(0); }

  /**
   * {@inheritDoc}
   */
  public Point2D getEndPoint2() {
    return getPoints().get(getPoints().size() - 1);
  }

  /**
   * {@inheritDoc}
   */
  public boolean contains(double xcoord, double ycoord) {
    for (Line2D segment : getSegments()) {
      if (segment.ptSegDist(xcoord, ycoord) < DELTA) return true;
    }
    return false;
  }

  /**
   * {@inheritDoc}
   */
  public List<Point2D> getPoints() { return points; }

  /**
   * Sets the connection points.
   * @param thePoints the points
   */
  public void setPoints(List<Point2D> thePoints) {
    points = thePoints;
    setValid(true);
  }

  /**
   * {@inheritDoc}
   */
  public List<Line2D> getSegments() {
    List<Line2D> result = new ArrayList<Line2D>();
    Point2D firstpoint = getPoints().get(0);
    Point2D nextpoint;
    for (int i = 1; i < getPoints().size(); i++) {
      nextpoint = getPoints().get(i);
      result.add(new Line2D.Double(firstpoint, nextpoint));
      firstpoint = nextpoint;
    }
    return result;
  }

  /**
   * {@inheritDoc}
   */
  public Line2D getSegmentAtPoint(double xcoord, double ycoord) {
    for (Line2D segment : getSegments()) {
      if (segment.ptSegDist(xcoord, ycoord) < 3.0) {
        return segment;
      }
    }
    return null;
  }

  /**
   * {@inheritDoc}
   */
  public void draw(DrawingContext drawingContext) {
    for (Line2D segment : getSegments()) {
      drawSegment(drawingContext, segment);
    }
  }

  /**
   * Draws a line segment using the current line style.
   * @param drawingContext the DrawingContext
   * @param segment the segment to draw
   */
  private void drawSegment(DrawingContext drawingContext, Line2D segment) {
    if (isDashed()) {
      drawingContext.drawDashedLine(segment.getX1(), segment.getY1(),
        segment.getX2(), segment.getY2());
    } else {
      drawingContext.drawLine(segment.getX1(), segment.getY1(),
        segment.getX2(), segment.getY2());
    }
  }

  /**
   * {@inheritDoc}
   */
  public void copyData(Connection conn) {
    setParent(conn.getParent());
    setIsDashed(conn.isDashed());
    setNode1(conn.getNode1());
    setNode2(conn.getNode2());
  }

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
