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
import java.util.List;

/**
 * This class implements a visual binary connection between to Node elements.
 * The current implementation tries to implement any kind of line connection,
 * the visual representation is dependent on the embedded model element and a
 * number of view parameters.
 *
 * @author Wei-ju Wu
 * @version 1.0
 */
public interface Connection extends DiagramElement {

  /**
   * Copies the underlying data from the specified connection. This is a
   * shallow copy, rather than a clone, since only the references to the nodes
   * of the source connection will be copied.
   * @param conn the source connection
   */
  void copyData(Connection conn);

  /**
   * Returns the Node 1 element.
   * @return node 1
   */
  Node getNode1();

  /**
   * Sets the node 1 element.
   * @param aNode the node 1 element
   */
  void setNode1(Node aNode);

  /**
   * Returns the Node 2 element.
   * @return node 2
   */
  Node getNode2();

  /**
   * Sets the node 2 element of the connection.
   * @param aNode the node 2 element
   */
  void setNode2(Node aNode);

  /**
   * Returns the connection points. The first and last point of the connection
   * represent the start and the end points.
   * @return the points of the connection
   */
  List<Point2D> getPoints();

  /**
   * Returns the segments of this connection.
   * @return the segments
   */
  List<Line2D> getSegments();

  /**
   * Resets the connection points.
   */
  void resetPoints();

  /**
   * Returns the segment of this connection that is closest to the specified
   * point.
   * @param xcoord the x coordinate
   * @param ycoord the y coordinate
   * @return the segment nearest to the point or null if not within DELTA
   */
  Line2D getSegmentAtPoint(double xcoord, double ycoord);

  /**
   * Sets the points in the rectilinear connection.
   * @param thePoints the list of points
   */
  void setPoints(List<Point2D> thePoints);

  /**
   * Sets the line style. True means a dashed line, false a solid line.
   * Default is solid.
   * @param flag true for dashed, false for solid
   */
  void setIsDashed(boolean flag);

  /**
   * Determines whether this connection is drawn dashed or not.
   * @return true for dashed, false oherwise
   */
  boolean isDashed();

  /**
   * Determines whether the connection is managed in rectilinear fashion.
   * @return true if managed in rectilinear fashion
   */
  boolean isRectilinear();

  /**
   * Calculates the rotation of the last segment to node 1.
   * @return the rotation of the last segment to node 1
   */
  AffineTransform calculateRotationInEndPoint1();

  /**
   * Calculates the rotation of the last segment to node 2.
   * @return the rotation of the last segment to node 2.
   */
  AffineTransform calculateRotationInEndPoint2();

  /**
   * Returns the point that connects to node 1.
   * @return the point that connects to node 1
   */
  Point2D getEndPoint1();

  /**
   * Returns the point that connects to node 2.
   * @return the point that connects to node 2
   */
  Point2D getEndPoint2();

  /**
   * Returns the LineConnectMethod that belongs to this Connection type.
   * @return the LineConnectMethod
   */
  LineConnectMethod getConnectMethod();
}
