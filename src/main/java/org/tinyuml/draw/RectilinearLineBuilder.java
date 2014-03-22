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

import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.List;
import org.tinyuml.draw.GeometryUtil.Orientation;

/**
 * A class that generates a set of line segments that conform to the following
 * requirements:
 * Given two points and a priority direction, generated a set of connected line
 * segments such that:
 * - the number of line segments is either zero, one or two
 * - if the points are equal, return zero segments
 * - if the two points can be connected to a line that is parallel to either
 *   the x or the y axis, create one single segment
 * - otherwise create an additional point that is first drawn in the priority
 *   direction (horizontal or vertical) and then connects to the second point
 *
 * @author Wei-ju Wu
 * @version 1.0
 */
public final class RectilinearLineBuilder {

  /**
   * The direction of the fixed point from the rectangle.
   */
  public enum Direction {
    STRAIGHT, DIAGONAL, INSIDE
  };

  /**
   * The orientation that nodes take relative to each other.
   */
  public enum NodeDirection {
    EAST_WEST, WEST_EAST, NORTH_SOUTH, SOUTH_NORTH, NE_SW, SW_NE, NW_SE, SE_NW
  }

  /**
   * Static instance.
   */
  private static RectilinearLineBuilder instance =
    new RectilinearLineBuilder();

  /**
   * Returns the singleton instance.
   * @return the instance
   */
  public static RectilinearLineBuilder getInstance() {
    return instance;
  }

  /**
   * Private constructor.
   */
  private RectilinearLineBuilder() { }

  /**
   * Calculates a number of line segments, given two fixed points and an
   * orientation for the first segment.
   * @param p1 the first point
   * @param p2 the second point
   * @param firstSegmentOrientation the orientation for the first segment
   * @return the calculated line segments
   */
  public List<Point2D> calculateLineSegments(Point2D p1, Point2D p2,
    Orientation firstSegmentOrientation) {
    return calculateLineSegments(p1.getX(), p1.getY(), p2.getX(), p2.getY(),
      firstSegmentOrientation);
  }

  /**
   * Calculates a number of line segments, given two fixed points.
   * @param x1 the x1 coordinate
   * @param y1 the y1 coordinate
   * @param x2 the x2 coordinate
   * @param y2 the y2 coordinate
   * @param firstSegmentOrientation the orientation for the first segment
   * @return the calculated line segments
   */
  private List<Point2D> calculateLineSegments(double x1, double y1, double x2,
    double y2, Orientation firstSegmentOrientation) {
    Direction direction = getDirection(x1, y1, x2, y2);
    List<Point2D> result = new LinkedList<Point2D>();
    switch (direction) {
      case INSIDE:
        break;
      case STRAIGHT:
        result.add(new Point2D.Double(x1, y1));
        result.add(new Point2D.Double(x2, y2));
        break;
      case DIAGONAL:
        createTwoSegments(result, x1, y1, x2, y2, firstSegmentOrientation);
        break;
      default:
        break;
    }
    return result;
  }

  /**
   * Returns the line segments between two nodes. The orientations in relation
   * to each other and for each node, a side is chosen heuristically.
   * @param node1 the first Node
   * @param node2 the second Node
   * @return the line segments
   */
  public List<Point2D> calculateLineSegments(Node node1, Node node2) {
    NodeDirection direction = getNodeDirection(node1, node2);
    switch (direction) {
      case WEST_EAST:
        return createHorizontalLineSegment(node1, node2, true);
      case EAST_WEST:
        return createHorizontalLineSegment(node1, node2, false);
      case NORTH_SOUTH:
        return createVerticalLineSegment(node1, node2, true);
      case SOUTH_NORTH:
        return createVerticalLineSegment(node1, node2, false);
      case SE_NW:
        return calculateLineSegments(node1.getAbsoluteX1(),
          node1.getAbsCenterY(), node2.getAbsCenterX(), node2.getAbsoluteY2(),
          Orientation.HORIZONTAL);
      case SW_NE:
        return calculateLineSegments(node1.getAbsoluteX2(),
          node1.getAbsCenterY(), node2.getAbsCenterX(), node2.getAbsoluteY2(),
          Orientation.HORIZONTAL);
      case NW_SE:
        return calculateLineSegments(node1.getAbsoluteX2(),
          node1.getAbsCenterY(), node2.getAbsCenterX(), node2.getAbsoluteY1(),
          Orientation.HORIZONTAL);
      case NE_SW:
      default:
        return calculateLineSegments(node1.getAbsoluteX1(),
          node1.getAbsCenterY(), node2.getAbsCenterX(), node2.getAbsoluteY1(),
          Orientation.HORIZONTAL);
    }
  }

  /**
   * Creates a line segment that connects node1 and node2 with the
   * following properties:
   * - the line segment is parallel to the x-axis
   * - it lies within the overlapping range of the vertical edges
   * @param node1 the first Node
   * @param node2 the second Node
   * @param leftToRight true if the line is drawn from left to right, false
   * if from right to left
   * @return the line segment as a list of Point2D
   */
  private List<Point2D> createHorizontalLineSegment(Node node1, Node node2,
    boolean leftToRight) {
    List<Point2D> result = new LinkedList<Point2D>();
    double y = calculateOverlapMiddlePoint(node1.getAbsoluteY1(),
      node1.getAbsoluteY2(), node2.getAbsoluteY1(), node2.getAbsoluteY2());
    if (leftToRight) {
      result.add(new Point2D.Double(node1.getAbsoluteX2(), y));
      result.add(new Point2D.Double(node2.getAbsoluteX1(), y));
    } else {
      result.add(new Point2D.Double(node1.getAbsoluteX1(), y));
      result.add(new Point2D.Double(node2.getAbsoluteX2(), y));
    }
    return result;
  }

  /**
   * Creates a line segment that connects node1 and node2 with the
   * following properties:
   * - the line segment is parallel to the y-axis
   * - it lies within the overlapping range of the horizontal edges
   * @param node1 the first Node
   * @param node2 the second Node
   * @param topToBottom true if the line is drawn from top to bottom, false
   * if from bottom to top
   * @return the line segment as a list of Point2D
   */
  private List<Point2D> createVerticalLineSegment(Node node1, Node node2,
    boolean topToBottom) {
    List<Point2D> result = new LinkedList<Point2D>();
    double x = calculateOverlapMiddlePoint(node1.getAbsoluteX1(),
      node1.getAbsoluteX2(), node2.getAbsoluteX1(), node2.getAbsoluteX2());
    if (topToBottom) {
      result.add(new Point2D.Double(x, node1.getAbsoluteY2()));
      result.add(new Point2D.Double(x, node2.getAbsoluteY1()));
    } else {
      result.add(new Point2D.Double(x, node1.getAbsoluteY1()));
      result.add(new Point2D.Double(x, node2.getAbsoluteY2()));
    }
    return result;
  }

  /**
   * Creates line segments where the target point is in NW or SW direction with
   * the specified first segment orientation.
   * @param points the list of points to add to
   * @param x1 the x1 coordinate
   * @param y1 the y1 coordinate
   * @param x2 the x2 coordinate
   * @param y2 the y2 coordinate
   * @param firstSegmentOrientation the orientation of the first segment
   */
  private void createTwoSegments(List<Point2D> points, double x1, double y1,
    double x2, double y2, Orientation firstSegmentOrientation) {
    if (firstSegmentOrientation == Orientation.HORIZONTAL) {
      points.add(new Point2D.Double(x1, y1));
      points.add(new Point2D.Double(x2, y1));
      points.add(new Point2D.Double(x2, y2));
    } else {
      points.add(new Point2D.Double(x1, y1));
      points.add(new Point2D.Double(x1, y2));
      points.add(new Point2D.Double(x2, y2));
    }
  }

  /**
   * Determines the direction where point 2 lies relative to point 1.
   * @param x1 the x1 coordinate
   * @param y1 the y1 coordinate
   * @param x2 the x2 coordinate
   * @param y2 the y2 coordinate
   * @return the direction
   */
  public Direction getDirection(double x1, double y1, double x2, double y2) {
    if (x1 == x2 && y1 == y2) return Direction.INSIDE;
    if (x1 == x2 || y1 == y2) return Direction.STRAIGHT;
    return Direction.DIAGONAL;
  }

  /**
   * Determines the axis orientation of two nodes to each other.
   * @param node1 the first node
   * @param node2 the second node
   * @return the Axis that indicates the orientation of the nodes
   */
  public NodeDirection getNodeDirection(Node node1, Node node2) {
    double node1x1 = node1.getAbsoluteX1(), node1y1 = node1.getAbsoluteY1();
    double node1x2 = node1.getAbsoluteX2(), node1y2 = node1.getAbsoluteY2();
    double node2x1 = node2.getAbsoluteX1(), node2y1 = node2.getAbsoluteY1();
    double node2x2 = node2.getAbsoluteX2(), node2y2 = node2.getAbsoluteY2();

    if (node2.getAbsCenterX() >= node1.getAbsCenterX() &&
        rangesOverlap(node1y1, node1y2, node2y1, node2y2)) {
      return NodeDirection.WEST_EAST;
    }
    if (node1.getAbsCenterX() > node2.getAbsCenterX() &&
        rangesOverlap(node1y1, node1y2, node2y1, node2y2)) {
      return NodeDirection.EAST_WEST;
    }
    if (node2.getAbsCenterY() >= node1.getAbsCenterY() &&
        rangesOverlap(node1x1, node1x2, node2x1, node2x2)) {
      return NodeDirection.NORTH_SOUTH;
    }
    if (node1.getAbsCenterY() > node2.getAbsCenterY() &&
        rangesOverlap(node1x1, node1x2, node2x1, node2x2)) {
      return NodeDirection.SOUTH_NORTH;
    }

    // the diagonal directions
    if (node2.getAbsCenterX() > node1.getAbsCenterX() &&
        node2.getAbsCenterY() > node1.getAbsCenterY()) {
      return NodeDirection.NW_SE;
    }
    if (node1.getAbsCenterX() > node2.getAbsCenterX() &&
        node1.getAbsCenterY() > node2.getAbsCenterY()) {
      return NodeDirection.SE_NW;
    }
    if (node2.getAbsCenterX() > node1.getAbsCenterX() &&
        node2.getAbsCenterY() < node1.getAbsCenterY()) {
      return NodeDirection.SW_NE;
    }
    return NodeDirection.NE_SW;
  }

  /**
   * Returns true if the ranges r1 and r2 overlap.
   * Precondition: r1start <= r1end and r2start <= r2end
   * @param r1start the start of the first range
   * @param r1end the end of the first range
   * @param r2start the start of the second range
   * @param r2end the end of the second range
   * @return true if r1 and r2 overlap, false otherwise
   */
  private boolean rangesOverlap(double r1start, double r1end, double r2start,
    double r2end) {
    return GeometryUtil.getInstance().rangesOverlap(r1start, r1end, r2start,
      r2end);
  }

  /**
   * Returns the middle point of the overlapping region between the two
   * specified ranges.
   * Preconditions:
   * - The ranges r1 and r2 overlap
   * - r1start <= r1end and r2start <= r2end
   * @param r1start the start of the first range
   * @param r1end the end of the first range
   * @param r2start the start of the second range
   * @param r2end the end of the second range
   * @return true if r1 and r2 overlap, false otherwise
   */
  private double calculateOverlapMiddlePoint(double r1start, double r1end,
    double r2start, double r2end) {
    double regionStart = Math.max(r1start, r2start);
    double regionEnd = Math.min(r1end, r2end);
    return (regionStart + regionEnd) / 2;
  }
}
