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
import org.jmock.Mock;
import org.jmock.cglib.MockObjectTestCase;
import org.tinyuml.draw.CompositeNode;
import org.tinyuml.draw.Connection;
import org.tinyuml.draw.DrawingContext;
import org.tinyuml.draw.Node;
import org.tinyuml.draw.RectilinearConnection;
import org.tinyuml.draw.RectilinearLineConnectMethod;

/**
 * Tests the RectilinearLineConnectMethod class.
 * @author Wei-ju Wu
 * @version 1.0
 */
public class RectilinearLineConnectMethodTest extends MockObjectTestCase {

  private RectilinearLineConnectMethod method =
    RectilinearLineConnectMethod.getInstance();
  private CompositeNode parent = new MyAbsoluteParent();
  private Node sourceNode = new MyCompositeNode();
  private Node targetNode = new MyCompositeNode();
  private Connection conn = new RectilinearConnection();
  
  /**
   * {@inheritDoc}
   */
  protected void setUp() {
    sourceNode.setParent(parent);
    targetNode.setParent(parent);
    conn.setNode1(sourceNode);
    conn.setNode2(targetNode);
    sourceNode.addConnection(conn);
    targetNode.addConnection(conn);
    sourceNode.setSize(80, 40);
    targetNode.setSize(80, 40);
  }

  /**
   * Creates a horizontal straight line. This is a case that reveiled an error
   * during integration testing. There was an IndexOutOfBoundsException on
   * straight lines. Test for this.
   */
  public void testCreateStraightHorizontalLine() {
    sourceNode.setOrigin(10, 10);
    targetNode.setOrigin(100, 10);
    Point2D sourcePoint = new Point2D.Double(70, 30);
    Point2D targetPoint = new Point2D.Double(110, 30);
    method.generateAndSetPointsToConnection(conn, sourceNode, targetNode,
      sourcePoint, targetPoint);
    assertEquals(2, conn.getPoints().size());
    assertTrue("line should be horizontal",
      equals(conn.getPoints().get(0).getY(), conn.getPoints().get(1).getY()));
    assertTrue(equals(sourceNode.getAbsoluteX2(),
      conn.getPoints().get(0).getX()));
    assertTrue(equals(targetNode.getAbsoluteX1(),
      conn.getPoints().get(1).getX()));
  }

  /**
   * Creates a vertical straight line. This is a case that reveiled an error
   * during integration testing. There was an IndexOutOfBoundsException on
   * straight lines. Test for this.
   */
  public void testCreateStraightVerticalLine() {
    sourceNode.setOrigin(10, 10);
    targetNode.setOrigin(10, 100);
    Point2D sourcePoint = new Point2D.Double(50, 30);
    Point2D targetPoint = new Point2D.Double(50, 120);
    method.generateAndSetPointsToConnection(conn, sourceNode, targetNode,
      sourcePoint, targetPoint);
    assertEquals(2, conn.getPoints().size());
    assertTrue("line should be vertical",
      equals(conn.getPoints().get(0).getX(), conn.getPoints().get(1).getX()));
    assertTrue(equals(sourceNode.getAbsoluteY2(),
      conn.getPoints().get(0).getY()));
    assertTrue(equals(targetNode.getAbsoluteY1(),
      conn.getPoints().get(1).getY()));
  }
  
  /**
   * Tests creating a rectilinear line.
   */
  public void testCreateRectilinearLine() {
    sourceNode.setOrigin(10, 10);
    targetNode.setOrigin(100, 100);
    Point2D sourcePoint = new Point2D.Double(50, 30);
    Point2D targetPoint = new Point2D.Double(140, 120);
    method.generateAndSetPointsToConnection(conn, sourceNode, targetNode,
      sourcePoint, targetPoint);
    assertEquals(3, conn.getPoints().size());
    // test rectilinearity
    assertTrue(equals(conn.getPoints().get(0).getY(),
                      conn.getPoints().get(1).getY()));
    assertTrue(equals(conn.getPoints().get(1).getX(),
                      conn.getPoints().get(2).getX()));
  }
  
  /**
   * Tests creating a rectilinear line, eliminating the first segment.
   * This happens if the target node is overlapping with the source node in
   * x direction.
   */
  public void testCreateRectilinearLineEliminateFirstSegment() {
    sourceNode.setOrigin(10, 10);
    targetNode.setOrigin(20, 100);
    Point2D sourcePoint = new Point2D.Double(50, 30);
    Point2D targetPoint = new Point2D.Double(60, 120);
    method.generateAndSetPointsToConnection(conn, sourceNode, targetNode,
      sourcePoint, targetPoint);
    assertEquals(2, conn.getPoints().size());
    // test linearity
    assertTrue(equals(conn.getPoints().get(0).getX(),
                      conn.getPoints().get(1).getX()));
  }

  /**
   * Tests creating a rectilinear line, eliminating the last segment.
   * This happens if the target node is overlapping with the source node in
   * y direction.
   */
  public void testCreateRectilinearLineEliminateLastSegment() {
    sourceNode.setOrigin(10, 10);
    targetNode.setOrigin(100, 20);
    Point2D sourcePoint = new Point2D.Double(50, 30);
    Point2D targetPoint = new Point2D.Double(140, 40);
    method.generateAndSetPointsToConnection(conn, sourceNode, targetNode,
      sourcePoint, targetPoint);
    assertEquals(2, conn.getPoints().size());
    // test linearity
    assertTrue(equals(conn.getPoints().get(0).getY(),
                      conn.getPoints().get(1).getY()));
  }

  /**
   * Tests the draw() method.
   */
  public void testDraw() {
    Mock mockDrawingContext = mock(DrawingContext.class);
    
    // straight line
    mockDrawingContext.expects(once()).method("drawLine");
    method.drawLineSegments((DrawingContext) mockDrawingContext.proxy(),
      new Point2D.Double(10, 10), new Point2D.Double(100, 10));
    
    // rectilinear line
    mockDrawingContext.expects(exactly(2)).method("drawLine");
    method.drawLineSegments((DrawingContext) mockDrawingContext.proxy(),
      new Point2D.Double(50, 30), new Point2D.Double(140, 120));
  }

  /**
   * Double comparison.
   * @param value1 value 1
   * @param value2 value 2
   * @return true if near enough, false otherwise
   */
  private boolean equals(double value1, double value2) {
    return Math.abs(value1 - value2) < 0.001;
  }
}
