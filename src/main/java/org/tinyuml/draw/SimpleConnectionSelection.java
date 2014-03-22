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

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.List;

/**
 * This class implements a ConnectionSelection that allows the user to edit
 * connection points in a simple fashion.
 *
 * @author Wei-ju Wu
 * @version 1.0
 */
public class SimpleConnectionSelection extends ConnectionSelection {

  private Line2D editedSegment;
  private Point2D draggedControlPoint, dragPoint;
  private List<Point2D> editpoints = new LinkedList<Point2D>();
  private static final double EPS = 5.0;

  /**
   * Constructor.
   * @param operations the DiagramOperations object
   * @param conn the Connection object
   */
  public SimpleConnectionSelection(DiagramOperations operations,
    Connection conn) {
    super(operations, conn);
  }

  /**
   * {@inheritDoc}
   */
  public List<Point2D> getEditPoints() { return editpoints; }

  /**
   * {@inheritDoc}
   */
  protected void startDragControlPoint(double xcoord, double ycoord) {
    copyConnectionPointsToEditPoints();
    draggedControlPoint = getControlPoint(xcoord, ycoord);
    List<Point2D> connectionPoints = getConnection().getPoints();
    int dragPointIndex = connectionPoints.indexOf(draggedControlPoint);
    // Only allow inner control points
    if (dragPointIndex > 0 &&
        dragPointIndex < connectionPoints.size() - 1) {
      dragPoint = editpoints.get(dragPointIndex);
      setIsDragControlPoint(true);
    }
  }

  /**
   * {@inheritDoc}
   */
  protected void startDragSegment(double xcoord, double ycoord) {
    copyConnectionPointsToEditPoints();
    dragPoint = new Point2D.Double(xcoord, ycoord);
    editedSegment = getConnection().getSegmentAtPoint(xcoord, ycoord);
    // copy the segment points and an additional point into the edit list
    if (editedSegment != null) {
      editpoints.add(getConnection().getPoints().indexOf(
        editedSegment.getP2()), dragPoint);
      setIsDragSegment(true);
    }
  }

  /**
   * Copies the control points to the edit point list.
   */
  private void copyConnectionPointsToEditPoints() {
    editpoints = new LinkedList<Point2D>();
    for (Point2D point : getConnection().getPoints()) {
      editpoints.add((Point2D) point.clone());
    }
    // Set the first and last point to the node centers
    editpoints.get(0).setLocation(getConnection().getNode1().getAbsCenterX(),
      getConnection().getNode1().getAbsCenterY());
    editpoints.get(editpoints.size() - 1).setLocation(
      getConnection().getNode2().getAbsCenterX(),
      getConnection().getNode2().getAbsCenterY());
  }

  /**
   * {@inheritDoc}
   */
  protected void finishDragSegment(double xcoord, double ycoord) {
    // only do the change if the point was dragged out enough
    if (editedSegment.ptSegDist(xcoord, ycoord) < EPS) {
      editpoints.remove(dragPoint);
    }
    setOuterEditPointsToNodeIntersections();
  }

  /**
   * {@inheritDoc}
   */
  protected void finishDragControlPoint(double xcoord, double ycoord) {
    // check if the point can be eliminated, which is the case if the point
    // is near enough to the segment formed by its next neighbors
    int index = editpoints.indexOf(dragPoint);
    if (index > 0 && index < editpoints.size() - 1) {
      Line2D outersegment = new Line2D.Double(editpoints.get(index - 1),
        editpoints.get(index + 1));
      if (outersegment.ptLineDist(dragPoint) <= EPS) {
        editpoints.remove(dragPoint);
      }
    }
    setOuterEditPointsToNodeIntersections();
  }

  /**
   * Trims the outer edit segments to the node edges.
   */
  private void setOuterEditPointsToNodeIntersections() {
    // compute intersections and set them
    Line2D firstSegment = new Line2D.Double(editpoints.get(0),
      editpoints.get(1));
    getConnection().getNode1().calculateIntersection(firstSegment,
      editpoints.get(0));
    Line2D lastSegment = new Line2D.Double(editpoints.get(
      editpoints.size() - 2), editpoints.get(editpoints.size() - 1));
    getConnection().getNode2().calculateIntersection(lastSegment,
      editpoints.get(editpoints.size() - 1));
  }

  /**
   * {@inheritDoc}
   */
  public void updatePosition(double xcoord, double ycoord) {
    // move freely
    if (isDragging()) {
      dragPoint.setLocation(xcoord, ycoord);
    }
  }
}
