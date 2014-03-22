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
package test.tinyuml.draw;

import java.awt.geom.Point2D;
import java.util.List;
import junit.framework.*;
import org.tinyuml.draw.CompositeNode;
import org.tinyuml.draw.GeometryUtil.Orientation;
import org.tinyuml.draw.Node;
import org.tinyuml.draw.RectilinearLineBuilder;
import org.tinyuml.draw.RectilinearLineBuilder.NodeDirection;
import org.tinyuml.draw.RectilinearLineBuilder.Direction;

/**
 * A test class for the RectilinearLineBuilder.
 * @author Wei-ju Wu
 * @version 1.0
 */
public class RectilinearLineBuilderTest extends TestCase {
  private RectilinearLineBuilder builder = RectilinearLineBuilder.getInstance();
  private double x = 150.0, y = 125.0;
  private double inx = 150.0, iny = 125.0, wx = 50.0, wy = 125.0,
    sx = 150.0, sy = 180.0, ex = 350.0, ey = 125.0, nx = 150.0, ny = 35.0;
  private double nwx = 70.0, nwy = 70.0, nex = 380.0, ney = 10.0;
  private double swx = 60.0, swy = 170.0, sex = 400.0, sey = 180.0;
  private CompositeNode parent = new MyAbsoluteParent();
  private Node node1 = new MyCompositeNode();
  private Node node2 = new MyCompositeNode();
    
  /**
   * {@inheritDoc}
   */
  @Override
  protected void setUp() {
    node1.setParent(parent);
    node2.setParent(parent);    
  }
  
  /**
   * Tests the getDirection() method.
   */
  public void testGetDirection() {
    assertEquals(Direction.INSIDE, builder.getDirection(x, y, inx, iny));
    assertEquals(Direction.STRAIGHT, builder.getDirection(x, y, wx, wy));
    assertEquals(Direction.STRAIGHT, builder.getDirection(x, y, sx, sy));
    assertEquals(Direction.STRAIGHT, builder.getDirection(x, y, ex, ey));
    assertEquals(Direction.STRAIGHT, builder.getDirection(x, y, nx, ny));
    assertEquals(Direction.DIAGONAL, builder.getDirection(x, y, nwx, nwy));
    assertEquals(Direction.DIAGONAL, builder.getDirection(x, y, nex, ney));
    assertEquals(Direction.DIAGONAL, builder.getDirection(x, y, swx, swy));
    assertEquals(Direction.DIAGONAL, builder.getDirection(x, y, sex, sey));
  }
  
  /**
   * The fixed point is inside the Rectangle.
   */
  public void testFixedPointInsideRectangle() {
    assertEquals(0, builder.calculateLineSegments(new Point2D.Double(x, y),
      new Point2D.Double(inx, iny), Orientation.HORIZONTAL).size());
  }
  
  /**
   * Generate a line segment on the western side.
   */
  public void testGenerateOnWestSide() {
    List<Point2D> segments = builder.calculateLineSegments(
      new Point2D.Double(x, y), new Point2D.Double(wx, wy),
      Orientation.HORIZONTAL);
    assertEquals(2, segments.size());
    assertEquals(x, segments.get(0).getX());
    assertEquals(y, segments.get(0).getY());
    assertEquals(wx, segments.get(1).getX());
    assertEquals(wy, segments.get(1).getY());
  }
  
  /**
   * Generate a line segment on the southern side.
   */
  public void testGenerateOnSouthSide() {
    List<Point2D> segments = builder.calculateLineSegments(
      new Point2D.Double(x, y), new Point2D.Double(sx, sy),
      Orientation.VERTICAL);
    assertEquals(2, segments.size());
    assertEquals(x, segments.get(0).getX());
    assertEquals(y, segments.get(0).getY());
    assertEquals(sx, segments.get(1).getX());
    assertEquals(sy, segments.get(1).getY());
  }
  
  /**
   * Generate a line segment on the eastern side.
   */
  public void testGenerateOnEastSide() {
    List<Point2D> segments = builder.calculateLineSegments(
      new Point2D.Double(x, y), new Point2D.Double(ex, ey),
      Orientation.HORIZONTAL);
    assertEquals(2, segments.size());
    assertEquals(x, segments.get(0).getX());
    assertEquals(y, segments.get(0).getY());
    assertEquals(ex, segments.get(1).getX());
    assertEquals(ey, segments.get(1).getY());
  }
  
  /**
   * Generate a line segment on the northern side.
   */
  public void testGenerateOnNorthSide() {
    List<Point2D> segments = builder.calculateLineSegments(
      new Point2D.Double(x, y), new Point2D.Double(nx, ny),
      Orientation.VERTICAL);
    assertEquals(2, segments.size());
    assertEquals(x, segments.get(0).getX());
    assertEquals(y, segments.get(0).getY());
    assertEquals(nx, segments.get(1).getX());
    assertEquals(ny, segments.get(1).getY());
  }
  
  // ************************************************************************
  // ***** Two-segmented lines
  // *********************************
  
  /**
   * Generate a line segment on the north western side, horizontal orientation.
   */
  public void testGenerateNWHorizontal() {
    List<Point2D> segments = builder.calculateLineSegments(
      new Point2D.Double(x, y), new Point2D.Double(nwx, nwy),
      Orientation.HORIZONTAL);
    assertEquals(3, segments.size());
    assertEquals(x, segments.get(0).getX());
    assertEquals(y, segments.get(0).getY());
    assertEquals(nwx, segments.get(1).getX());
    assertEquals(y, segments.get(1).getY());
    assertEquals(nwx, segments.get(2).getX());
    assertEquals(nwy, segments.get(2).getY());
  }

  /**
   * Generate a line segment on the north western side, vertical orientation.
   */
  public void testGenerateNWVertical() {
    List<Point2D> segments = builder.calculateLineSegments(
      new Point2D.Double(x, y), new Point2D.Double(nwx, nwy),
      Orientation.VERTICAL);
    assertEquals(3, segments.size());
    assertEquals(x, segments.get(0).getX());
    assertEquals(y, segments.get(0).getY());
    assertEquals(x, segments.get(1).getX());
    assertEquals(nwy, segments.get(1).getY());
    assertEquals(nwx, segments.get(2).getX());
    assertEquals(nwy, segments.get(2).getY());
  }

  // ************************************************************************
  // ***** Generation of segments given two nodes. There are four
  // ***** orientations, N-S (vertical), E-W (horizontal), NW-SE and NE-SW
  // ***** (the main diagonals). The other orientations  are retrieved by
  // ***** reversing.
  // **************************************************
  
  /**
   * Tests the getNodeDirection() method.
   */
  public void testGetNodeDirection() {
    // WEST-EAST
    node1.setOrigin(10, 10);
    node1.setSize(120, 80);
    node2.setOrigin(200, 50);
    node2.setSize(20, 50);
    assertEquals(NodeDirection.WEST_EAST, builder.getNodeDirection(node1, node2));
    // WEST-EAST (reverse node order)
    assertEquals(NodeDirection.EAST_WEST, builder.getNodeDirection(node2, node1));

    // NORTH-SOUTH
    node1.setOrigin(30, 50);
    node1.setSize(50, 30);
    node2.setOrigin(25, 100);
    node2.setSize(40, 30);
    assertEquals(NodeDirection.NORTH_SOUTH, builder.getNodeDirection(node1, node2));
    // SOUTH-NORTH (reverse node order)
    assertEquals(NodeDirection.SOUTH_NORTH, builder.getNodeDirection(node2, node1));
    
    // NW-SE
    node1.setOrigin(30, 50);
    node1.setSize(50, 30);
    node2.setOrigin(100, 100);
    node2.setSize(40, 30);
    assertEquals(NodeDirection.NW_SE, builder.getNodeDirection(node1, node2));
    // SE-NW (reverse node order)
    assertEquals(NodeDirection.SE_NW, builder.getNodeDirection(node2, node1));
    
    // SW-NE
    node1.setOrigin(30, 150);
    node1.setSize(50, 30);
    node2.setOrigin(100, 40);
    node2.setSize(40, 30);
    assertEquals(NodeDirection.SW_NE, builder.getNodeDirection(node1, node2));    
    // NE-SW (reverse node order)
    assertEquals(NodeDirection.NE_SW, builder.getNodeDirection(node2, node1));
  }
  
  /**
   * Tests generation between two nodes that can be aligned in WEST-EAST
   * direction.
   */
  public void testCalculateLineSegmentsWithNodesWestEast() {
    node1.setOrigin(10, 10);
    node1.setSize(120, 80); // y from 10 - 90
    node2.setOrigin(200, 50);
    node2.setSize(80, 30); // y from 50 - 80 -> overlap is from 50 - 80
    List<Point2D> points = builder.calculateLineSegments(node1, node2);
    assertEquals(2, points.size());
    assertEquals(node1.getAbsoluteX2(), points.get(0).getX());
    assertEquals(node2.getAbsoluteX1(), points.get(1).getX());
    assertEquals(points.get(0).getY(), points.get(1).getY());
    assertTrue(points.get(0).getY() >= 50);
    assertTrue(points.get(0).getY() <= 80);
    
    // reverse direction EAST-WEST
    points = builder.calculateLineSegments(node2, node1);
    assertEquals(2, points.size());
    assertEquals(node2.getAbsoluteX1(), points.get(0).getX());
    assertEquals(node1.getAbsoluteX2(), points.get(1).getX());
    assertEquals(points.get(0).getY(), points.get(1).getY());
    assertTrue(points.get(0).getY() >= 50);
    assertTrue(points.get(0).getY() <= 80);
  }

  /**
   * Tests generation between two nodes that can be aligned in NORTH-SOUTH
   * direction.
   */
  public void testCalculateLineSegmentsWithNodesNorthSouth() {
    node1.setOrigin(10, 10);
    node1.setSize(120, 80); // x from 10 to 130
    node2.setOrigin(5, 150);
    node2.setSize(80, 30); // x from 5 to 85 -> (10, 85)
    List<Point2D> points = builder.calculateLineSegments(node1, node2);
    assertEquals(2, points.size());
    assertEquals(node1.getAbsoluteY2(), points.get(0).getY());
    assertEquals(node2.getAbsoluteY1(), points.get(1).getY());
    assertEquals(points.get(0).getX(), points.get(1).getX());
    assertTrue(points.get(0).getX() >= 10);
    assertTrue(points.get(0).getX() <= 130);
    
    // reverse direction EAST-WEST
    points = builder.calculateLineSegments(node2, node1);
    assertEquals(2, points.size());
    assertEquals(node2.getAbsoluteY1(), points.get(0).getY());
    assertEquals(node1.getAbsoluteY2(), points.get(1).getY());
    assertEquals(points.get(0).getX(), points.get(1).getX());
    assertTrue(points.get(0).getX() >= 10);
    assertTrue(points.get(0).getX() <= 130);
  }
  
  /**
   * Tests generation between two nodes that can be aligned in SE-NW (\)
   * direction.
   */
  public void testCalculateLineSegmentsWithNodesSeNw() {
    node1.setOrigin(10, 10);
    node1.setSize(120, 80);
    node2.setOrigin(150, 120);
    node2.setSize(80, 30);
    // goes from top left to bottom right
    List<Point2D> points = builder.calculateLineSegments(node1, node2);
    assertEquals(3, points.size());
    assertEquals(node1.getAbsoluteX2(), points.get(0).getX());
    assertEquals(node1.getAbsCenterY(), points.get(0).getY());
    assertEquals(node2.getAbsCenterX(), points.get(2).getX());
    assertEquals(node2.getAbsoluteY1(), points.get(2).getY());
    
    // reverse directions
    points = builder.calculateLineSegments(node2, node1);
    assertEquals(3, points.size());
    assertEquals(node2.getAbsoluteX1(), points.get(0).getX());
    assertEquals(node2.getAbsCenterY(), points.get(0).getY());
    assertEquals(node1.getAbsCenterX(), points.get(2).getX());
    assertEquals(node1.getAbsoluteY2(), points.get(2).getY());
  }

  /**
   * Tests generation between two nodes that can be aligned in NE-SW (/)
   * direction.
   */
  public void testCalculateLineSegmentsWithNodesSwNe() {
    node1.setOrigin(10, 200);
    node1.setSize(120, 80);
    node2.setOrigin(150, 50);
    node2.setSize(80, 30);
    List<Point2D> points = builder.calculateLineSegments(node1, node2);
    assertEquals(3, points.size());
    assertEquals(node1.getAbsoluteX2(), points.get(0).getX());
    assertEquals(node1.getAbsCenterY(), points.get(0).getY());
    assertEquals(node2.getAbsCenterX(), points.get(2).getX());
    assertEquals(node2.getAbsoluteY2(), points.get(2).getY());

    // reverse directions
    points = builder.calculateLineSegments(node2, node1);
    assertEquals(3, points.size());
    assertEquals(node2.getAbsoluteX1(), points.get(0).getX());
    assertEquals(node2.getAbsCenterY(), points.get(0).getY());
    assertEquals(node1.getAbsCenterX(), points.get(2).getX());
    assertEquals(node1.getAbsoluteY1(), points.get(2).getY());
  }
}
