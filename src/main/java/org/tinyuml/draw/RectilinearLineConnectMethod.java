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
import org.tinyuml.draw.GeometryUtil.Orientation;

/**
 * This class implements a rectilinear connection method to connect UmlNodes.
 *
 * @author Wei-ju Wu
 * @version 1.0
 */
public final class RectilinearLineConnectMethod implements LineConnectMethod {

  private static RectilinearLineConnectMethod instance =
    new RectilinearLineConnectMethod();

  /**
   * Returns the singleton instance.
   * @return the singleton instance
   */
  public static RectilinearLineConnectMethod getInstance() { return instance; }

  /**
   * Private constructor.
   */
  private RectilinearLineConnectMethod() { }

  /**
   * {@inheritDoc}
   */
  public void generateAndSetPointsToConnection(Connection conn,
    Node sourceNode, Node targetNode, Point2D source, Point2D dest) {
    RectilinearLineBuilder linebuilder = RectilinearLineBuilder.getInstance();
    List<Point2D> points = linebuilder.calculateLineSegments(source, dest,
      Orientation.HORIZONTAL);
    List<Point2D> linepoints = new LinkedList<Point2D>();
    for (Point2D point : points) {
      linepoints.add(point);
    }
    // calculate intersections with the nodes
    Line2D line = new Line2D.Double();
    // first
    // check if we could start at the second segment
    if (points.size() > 2) {
      line.setLine(points.get(1), points.get(2));
    }

    // if not, start at the first segment
    if (points.size() > 2 && sourceNode.intersects(line)) {
      linepoints.remove(0);
    } else {
      line.setLine(points.get(0), points.get(1));
    }
    sourceNode.calculateIntersection(line, linepoints.get(0));

    // last
    // check if we could end at the segment before the last one if yes,
    // remove the last control point
    if (points.size() > 2) {
      line.setLine(points.get(points.size() - 3),
                 points.get(points.size() - 2));
      if (targetNode.intersects(line)) {
        linepoints.remove(linepoints.size() - 1);
      } else {
        line.setLine(points.get(points.size() - 2),
                   points.get(points.size() - 1));
      }
    }
    targetNode.calculateIntersection(line,
      linepoints.get(linepoints.size() - 1));
    conn.setPoints(linepoints);
  }

  /**
   * {@inheritDoc}
   */
  public void drawLineSegments(DrawingContext drawingContext,
    Point2D source, Point2D dest) {
    // draw in a right angle
    RectilinearLineBuilder linebuilder = RectilinearLineBuilder.getInstance();
    List<Point2D> points = linebuilder.calculateLineSegments(source, dest,
      Orientation.HORIZONTAL);
    if (points.size() > 0) {
      Point2D lastPoint = points.get(0);
      Point2D nextPoint;
      for (int i = 1; i < points.size(); i++) {
        nextPoint = points.get(i);
        drawingContext.drawLine(lastPoint.getX(), lastPoint.getY(),
          nextPoint.getX(), nextPoint.getY());
        lastPoint = nextPoint;
      }
    }
  }
}
