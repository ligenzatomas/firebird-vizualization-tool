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
import java.util.ArrayList;
import java.util.List;
import org.jmock.Mock;
import org.jmock.cglib.MockObjectTestCase;
import org.tinyuml.draw.CompositeNode;
import org.tinyuml.draw.DrawingContext;
import org.tinyuml.draw.Node;
import org.tinyuml.draw.RectilinearConnection;
import org.tinyuml.draw.RectilinearLineConnectMethod;

/**
 * A test class for RectilinearConnection.
 * @author Wei-ju Wu
 * @version 1.0
 */
public class RectilinearConnectionTest extends MockObjectTestCase {

  private CompositeNode parent = new MyAbsoluteParent();
  private Node rectNode1 = new MyCompositeNode();
  private Node rectNode2 = new MyCompositeNode();
  private RectilinearConnection conn = new RectilinearConnection();
  private Mock mockDrawingContext = mock(DrawingContext.class);
  
  /**
   * {@inheritDoc}
   */
  @Override
  protected void setUp() {
    rectNode1.setParent(parent);
    rectNode2.setParent(parent);
    conn.setNode1(rectNode1);
    conn.setNode2(rectNode2);
    rectNode1.addConnection(conn);
    rectNode2.addConnection(conn);
    rectNode1.setSize(80, 40);
    rectNode2.setSize(80, 40);
  }
  
  /**
   * Tests the configuration.
   */
  public void testConfiguration() {
    assertTrue(conn.isRectilinear());
    assertTrue(conn.getConnectMethod() instanceof RectilinearLineConnectMethod);
  }
  
  /**
   * Tests the clone() method.
   */
  public void testClone() {
    RectilinearConnection cloned = (RectilinearConnection) conn.clone();
    assertNull(cloned.getPoints());
    
    List<Point2D> points = new ArrayList<Point2D>();
    points.add(new Point2D.Double(1, 2));
    points.add(new Point2D.Double(1, 3));
    conn.setPoints(points);
    RectilinearConnection cloned2 = (RectilinearConnection) conn.clone();
    assertEquals(cloned2.getPoints(), conn.getPoints());
    assertFalse(cloned2.getPoints() == conn.getPoints());
    for (int i = 0; i < cloned2.getPoints().size(); i++) {
      assertTrue(cloned2.getPoints().get(i) != conn.getPoints().get(i));
    }
  }
  
  /**
   * Test the copyData() method.
   */
  public void testCopyData() {
    List<Point2D> points = new ArrayList<Point2D>();
    rectNode1.setOrigin(10, 10);
    rectNode2.setOrigin(150, 100);
    points.add(new Point2D.Double(rectNode1.getAbsoluteX2(), 30));
    points.add(new Point2D.Double(190, 30));
    points.add(new Point2D.Double(190, rectNode2.getAbsoluteY1()));
    conn.setPoints(points);
    
    RectilinearConnection copy = new RectilinearConnection();
    copy.copyData(conn);
    assertEquals(3, copy.getPoints().size());
    assertEquals(rectNode1.getAbsoluteX2(), copy.getPoints().get(0).getX());
    assertEquals(copy.getPoints().get(0).getY(),
                 copy.getPoints().get(1).getY());
    assertEquals(copy.getPoints().get(1).getX(),
                 copy.getPoints().get(2).getX());
    assertEquals(rectNode2.getAbsoluteY1(), copy.getPoints().get(2).getY());
  }
  
  /**
   * Tests restoring invalid connections on a draw(). Nodes are left and
   * right.
   */
  public void testDrawRestoreNodesLeftRight() {
    List<Point2D> points = new ArrayList<Point2D>();
    rectNode1.setOrigin(10, 10);
    rectNode2.setOrigin(100, 10);
    points.add(new Point2D.Double(rectNode1.getAbsoluteX2(), 30));
    points.add(new Point2D.Double(rectNode2.getAbsoluteX1(), 30));
    conn.setPoints(points);
    ignoreDrawingOperations();
    conn.draw((DrawingContext) mockDrawingContext.proxy());
    
    // move the node1 to the left
    rectNode1.setOrigin(0, 10);
    conn.invalidate();
    conn.draw((DrawingContext) mockDrawingContext.proxy());
    assertEquals(rectNode1.getAbsoluteX2(), conn.getPoints().get(0).getX());
    assertEquals(30.0, conn.getPoints().get(0).getY());
    
    // move the node2 to the right
    rectNode2.setOrigin(130, 10);
    conn.invalidate();
    conn.draw((DrawingContext) mockDrawingContext.proxy());
    assertEquals(rectNode2.getAbsoluteX1(), conn.getPoints().get(1).getX());
    assertEquals(30.0, conn.getPoints().get(1).getY());
  }
  
  /**
   * Tests restoring invalid connections on a draw(). Nodes are top and
   * bottom.
   */
  public void testDrawRestoreNodesTopBottom() {
    List<Point2D> points = new ArrayList<Point2D>();
    rectNode1.setOrigin(10, 10);
    rectNode2.setOrigin(10, 100);
    points.add(new Point2D.Double(50, rectNode1.getAbsoluteY2()));
    points.add(new Point2D.Double(50, rectNode2.getAbsoluteY1()));
    conn.setPoints(points);
    ignoreDrawingOperations();
    conn.draw((DrawingContext) mockDrawingContext.proxy());

    // move the node1 up
    rectNode1.setOrigin(10, -5);
    conn.invalidate();
    conn.draw((DrawingContext) mockDrawingContext.proxy());
    assertEquals(rectNode1.getAbsoluteY2(), conn.getPoints().get(0).getY());
    assertEquals(50.0, conn.getPoints().get(0).getX());
    
    // move the node2 down
    rectNode2.setOrigin(10, 150);
    conn.invalidate();
    conn.draw((DrawingContext) mockDrawingContext.proxy());
    assertEquals(rectNode2.getAbsoluteY1(), conn.getPoints().get(1).getY());
    assertEquals(50.0, conn.getPoints().get(1).getX());
  }

  /**
   * Tests restoring invalid connections on a draw(). Nodes form stairs, thus
   * forming a three-point connection.
   */
  public void testDrawRestoreNodesStairShaped() {
    List<Point2D> points = new ArrayList<Point2D>();
    rectNode1.setOrigin(10, 10);
    rectNode2.setOrigin(150, 100);
    points.add(new Point2D.Double(rectNode1.getAbsoluteX2(), 30));
    points.add(new Point2D.Double(190, 30));
    points.add(new Point2D.Double(190, rectNode2.getAbsoluteY1()));
    conn.setPoints(points);
    ignoreDrawingOperations();
    conn.draw((DrawingContext) mockDrawingContext.proxy());

    // move the node1 to the right
    rectNode1.setOrigin(20, 10);
    conn.invalidate();
    conn.draw((DrawingContext) mockDrawingContext.proxy());
    assertEquals(rectNode1.getAbsoluteX2(), conn.getPoints().get(0).getX());
    assertEquals(30.0, conn.getPoints().get(0).getY());
    assertEquals(3, conn.getPoints().size());

    // move the node2 up
    rectNode2.setOrigin(150, 90);
    conn.invalidate();
    conn.draw((DrawingContext) mockDrawingContext.proxy());
    assertEquals(rectNode2.getAbsoluteY1(), conn.getPoints().get(2).getY());
    assertEquals(190.0, conn.getPoints().get(2).getX());
    assertEquals(3, conn.getPoints().size());
  }

  /**
   * It is not that important what is drawn.
   */
  private void ignoreDrawingOperations() {
    mockDrawingContext.expects(atLeastOnce()).method("drawLine");
  }
  
  /**
   * Tests the reset method.
   */
  public void testResetPoints() {
    List<Point2D> points = new ArrayList<Point2D>();
    rectNode1.setOrigin(10, 10);
    rectNode2.setOrigin(150, 100);
    points.add(new Point2D.Double(rectNode1.getAbsoluteX2(), 30));
    points.add(new Point2D.Double(190, 30));
    points.add(new Point2D.Double(190, 50));
    points.add(new Point2D.Double(190, rectNode2.getAbsoluteY1()));
    conn.setPoints(points);
    
    conn.resetPoints();
    assertEquals(3, conn.getPoints().size());
    assertEquals(rectNode1.getAbsoluteX2(), conn.getPoints().get(0).getX());
    assertEquals(conn.getPoints().get(0).getY(),
                 conn.getPoints().get(1).getY());
    assertEquals(conn.getPoints().get(1).getX(),
                 conn.getPoints().get(2).getX());
    assertEquals(rectNode2.getAbsoluteY1(), conn.getPoints().get(2).getY());
  }
  
  /**
   * Tests the draw method and reconnect.
   */
  public void testReconnectPulledOffPointsWithMoreThanThreePoints() {
    List<Point2D> points = new ArrayList<Point2D>();
    rectNode1.setOrigin(10, 10); // 10, 10 - 90, 50
    rectNode2.setOrigin(10, 120); // 10, 120 - 90, 160
    points.add(new Point2D.Double(rectNode1.getAbsoluteX2(), 50));
    points.add(new Point2D.Double(140, 50));
    points.add(new Point2D.Double(140, 130));
    points.add(new Point2D.Double(rectNode2.getAbsoluteX2(), 140));
    conn.setPoints(points);
    
    // now pull the second node off left and invoke draw
    rectNode2.setOrigin(0, 120);
    conn.invalidate();
    ignoreDrawingOperations();
    conn.draw((DrawingContext) mockDrawingContext.proxy());
    assertEquals(4, conn.getPoints().size());
    assertEquals(rectNode2.getAbsoluteX2(), conn.getPoints().get(3).getX());
    
  
    // now pull the first node off right and invoke draw
    rectNode1.setOrigin(20, 10);
    conn.invalidate();
    ignoreDrawingOperations();
    conn.draw((DrawingContext) mockDrawingContext.proxy());
    assertEquals(4, conn.getPoints().size());
    assertEquals(rectNode1.getAbsoluteX2(), conn.getPoints().get(0).getX());
    
    // now pull the first node down and invoke draw
    rectNode1.setOrigin(20, 40);
    conn.invalidate();
    ignoreDrawingOperations();
    conn.draw((DrawingContext) mockDrawingContext.proxy());
    assertEquals(4, conn.getPoints().size());
    assertEquals(rectNode1.getAbsoluteY2(), conn.getPoints().get(0).getY());
  }

  /**
   * Tests the draw method and reconnect u-configuration.
   */
  public void testReconnectPulledOffPointsWithMoreThanThreePointsUConfig() {
    List<Point2D> points = new ArrayList<Point2D>();
    rectNode1.setOrigin(10, 10); // 10, 10 - 90, 50
    rectNode2.setOrigin(150, 10); // 150, 10 - 230, 50
    points.add(new Point2D.Double(rectNode1.getAbsoluteX2(),
      rectNode1.getAbsoluteY2()));
    points.add(new Point2D.Double(rectNode1.getAbsoluteX2(), 100));
    points.add(new Point2D.Double(rectNode2.getAbsoluteX1(), 100));
    points.add(new Point2D.Double(rectNode2.getAbsoluteX1(),
      rectNode2.getAbsoluteY2()));
    conn.setPoints(points);
    
    // now pull the first node off right and invoke draw
    rectNode1.setOrigin(30, 10);
    conn.invalidate();
    ignoreDrawingOperations();
    conn.draw((DrawingContext) mockDrawingContext.proxy());
    assertEquals(4, conn.getPoints().size());
    assertEquals(rectNode1.getAbsoluteX2(), conn.getPoints().get(0).getX());
    
    // now pull the second node off right and invoke draw
    rectNode2.setOrigin(170, 10);
    conn.invalidate();
    ignoreDrawingOperations();
    conn.draw((DrawingContext) mockDrawingContext.proxy());
    assertEquals(4, conn.getPoints().size());
    assertEquals(rectNode2.getAbsoluteX1(), conn.getPoints().get(3).getX());
  }
}
