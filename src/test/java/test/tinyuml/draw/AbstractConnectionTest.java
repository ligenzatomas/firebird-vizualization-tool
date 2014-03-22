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

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import org.tinyuml.draw.AbstractConnection;
import org.tinyuml.draw.Connection;
import org.tinyuml.draw.DiagramOperations;
import org.tinyuml.draw.DrawingContext;
import org.tinyuml.draw.LineConnectMethod;
import org.tinyuml.draw.Selection;

/**
 * Tests the AbstractConnection class.
 * @author Wei-ju Wu
 * @version 1.0
 */
public class AbstractConnectionTest extends MockObjectTestCase {
  class MyConnection extends AbstractConnection {
    private List<Point2D> points = new ArrayList<Point2D>();
    public boolean isRectilinear() { return false; }
    public Selection getSelection(DiagramOperations op) { return null; }
    public void resetPoints() { }
    public LineConnectMethod getConnectMethod() { return null; }
  }
  private MyConnection connection = new MyConnection();
  private MyAbsoluteParent parent = new MyAbsoluteParent();

  /**
   * {@inheritDoc}
   */
  protected void setUp() {
    connection.setParent(parent);
  }

  /**
   * Tests the initial state.
   */
  public void testInitial() {
    MyAbsoluteParent otherParent = new MyAbsoluteParent();
    assertFalse(connection.isNestable());
    assertFalse(connection.canNestElements());
    assertTrue(connection.isAncestor(parent));
    assertFalse(connection.isAncestor(otherParent));
  }

  /**
   * Tests the intersects() method.
   */
  public void testIntersects() {
    final List<Point2D> points = new ArrayList<Point2D>();
    points.add(new Point2D.Double(10.0, 10.0));
    points.add(new Point2D.Double(50.0, 50.0));
    connection.setPoints(points);
    Rectangle2D bounds = new Rectangle2D.Double();
    bounds.setRect(5, 5, 50, 30);
    assertTrue(connection.intersects(bounds));
    bounds.setRect(200, 200, 50, 30);
    assertFalse(connection.intersects(bounds));
  }
  
  /**
   * Tests the getAbsoluteBounds() method.
   */
  public void testGetAbsoluteBounds() {
    final List<Point2D> points = new ArrayList<Point2D>();
    points.add(new Point2D.Double(10.0, 5.0));
    points.add(new Point2D.Double(50.0, 40.0));
    connection.setPoints(points);
    assertEquals(10.0, connection.getAbsoluteBounds().getX());
    assertEquals(5.0, connection.getAbsoluteBounds().getY());
    assertEquals(40.0, connection.getAbsoluteBounds().getWidth());
    assertEquals(35.0, connection.getAbsoluteBounds().getHeight());
  }
  
  /**
   * Tests the copyData() method.
   */
  public void testCopyData() {
    MyConnection original = new MyConnection();
    MyConnection copy = new MyConnection();
    MyCompositeNode node1 = new MyCompositeNode();
    MyCompositeNode node2 = new MyCompositeNode();
    MyAbsoluteParent parent = new MyAbsoluteParent();
    original.setIsDashed(true);
    original.setNode1(node1);
    original.setNode2(node2);
    original.setParent(parent);
    
    copy.copyData(original);
    assertTrue(copy.isDashed());
    assertTrue(node1 == copy.getNode1());
    assertTrue(node2 == copy.getNode2());
    assertTrue(parent == copy.getParent());
  }
  
  /**
   * Tests the characteristics of a multi segment connection.
   */
  public void testGetMultiSegmentLineCharacteristics() {
    Connection connection = createMultiSegmentConnection();
    Point2D p1 = new Point2D.Double(10.0, 5.0);
    Point2D p2 = new Point2D.Double(20.0, 5.0);
    Point2D p3 = new Point2D.Double(30.0, 5.0);
    
    // end points
    assertEquals(p1, connection.getEndPoint1());
    assertEquals(p3, connection.getEndPoint2());
    
    // getSegmentAtPoint()
    assertNull("no segment should be found here",
      connection.getSegmentAtPoint(100.0, 6.0));

    Line2D segment1 = connection.getSegmentAtPoint(13.0, 5.0);
    assertEquals(p1, segment1.getP1());
    assertEquals(p2, segment1.getP2());
    
    Line2D segment2 = connection.getSegmentAtPoint(26.0, 5.0);
    assertEquals(p2, segment2.getP1());
    assertEquals(p3, segment2.getP2());
    System.out.println("dist: " + segment2.ptSegDist(26, 5));
    
    // contains
    assertFalse(connection.contains(100, 6));
    assertTrue(connection.contains(26, 5));
  }
  
  /**
   * Tests rotations methods.
   */
  public void testCalculateRotations() {
    Connection conn = createMultiSegmentConnection();
    assertNotNull(conn.calculateRotationInEndPoint1());
    assertNotNull(conn.calculateRotationInEndPoint2());
  }
  
  /**
   * Tests drawing.
   */
  public void testDrawMultiSegment() {
    Connection conn = createMultiSegmentConnection();
    Mock mockDrawingContext = mock(DrawingContext.class);
    // undashed
    mockDrawingContext.expects(once()).method("drawLine")
      .with(eq(10.0), eq(5.0), eq(20.0), eq(5.0));
    mockDrawingContext.expects(once()).method("drawLine")
      .with(eq(20.0), eq(5.0), eq(30.0), eq(5.0));
    conn.draw((DrawingContext) mockDrawingContext.proxy());
    
    // dashed
    conn.setIsDashed(true);
    mockDrawingContext.expects(once()).method("drawDashedLine")
      .with(eq(10.0), eq(5.0), eq(20.0), eq(5.0));
    mockDrawingContext.expects(once()).method("drawDashedLine")
      .with(eq(20.0), eq(5.0), eq(30.0), eq(5.0));
    conn.draw((DrawingContext) mockDrawingContext.proxy());
  }
  
  /**
   * Sets up and creates multi segment connection.
   * @return the connection
   */
  private Connection createMultiSegmentConnection() {
    // Define two segments
    List<Point2D> points = new ArrayList<Point2D>();
    Point2D p1 = new Point2D.Double(10.0, 5.0);
    Point2D p2 = new Point2D.Double(20.0, 5.0);
    Point2D p3 = new Point2D.Double(30.0, 5.0);
    points.add(p1);
    points.add(p2);
    points.add(p3);
    connection.setPoints(points);    
    return connection;
  }
}
