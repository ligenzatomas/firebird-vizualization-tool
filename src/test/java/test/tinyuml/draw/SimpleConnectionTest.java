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

import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import org.jmock.Mock;
import org.jmock.cglib.MockObjectTestCase;
import org.tinyuml.draw.CompositeNode;
import org.tinyuml.draw.Connection;
import org.tinyuml.draw.DiagramOperations;
import org.tinyuml.draw.DrawingContext;
import org.tinyuml.draw.Node;
import org.tinyuml.draw.Selection;
import org.tinyuml.draw.SimpleConnection;
import org.tinyuml.draw.SimpleLineConnectMethod;
import org.tinyuml.model.UmlRelation;

/**
 *
 * @author Wei-ju Wu
 * @version 1.0
 */
public class SimpleConnectionTest extends MockObjectTestCase {

  private Line2D line = new Line2D.Double();
  private UmlRelation association = new UmlRelation();
  private SimpleConnection connection = new SimpleConnection();
  private Mock mockOperations = mock(DiagramOperations.class);
  private Mock mockDrawingContext = mock(DrawingContext.class);
  private Mock mockNode1 = mock(Node.class);
  private Mock mockNode2 = mock(Node.class);
  private Mock mockParent = mock(CompositeNode.class);
  private CompositeNode parent = (CompositeNode) mockParent.proxy();
  private DrawingContext drawingContext =
    (DrawingContext) mockDrawingContext.proxy();
  private DiagramOperations operations = (DiagramOperations)
    mockOperations.proxy();
  private List<Point2D> points = new ArrayList<Point2D>();

  /**
   * {@inheritDoc}
   */
  @Override
  protected void setUp() {
    mockNode1.expects(once()).method("addNodeChangeListener")
      .with(eq(connection));
    mockNode2.expects(once()).method("addNodeChangeListener")
      .with(eq(connection));
    points.add(new Point2D.Double());
    points.add(new Point2D.Double());
    connection.setNode1((Node) mockNode1.proxy());
    connection.setNode2((Node) mockNode2.proxy());
    connection.setPoints(points);
  }
  
  /**
   * Tests the clone() method.
   */
  public void testClone() {
    SimpleConnection cloned = (SimpleConnection) connection.clone();
    assertEquals(cloned.getPoints(), connection.getPoints());
    assertTrue(cloned.getPoints() != connection.getPoints());
    for (int i = 0; i < cloned.getPoints().size(); i++) {
      assertTrue(connection.getPoints().get(i) != cloned.getPoints().get(i));
    }
  }
  
  /**
   * {@inheritDoc}
   */
  public void setNodesToNull() {
    // set connection nodes to some different nodes
    Mock mockNode3 = mock(Node.class);
    Mock mockNode4 = mock(Node.class);
    
    mockNode1.expects(once()).method("removeNodeChangeListener")
      .with(eq(connection));
    mockNode3.expects(once()).method("addNodeChangeListener")
      .with(eq(connection));
    connection.setNode1((Node) mockNode3.proxy());
    
    mockNode2.expects(once()).method("removeNodeChangeListener")
      .with(eq(connection));
    mockNode4.expects(once()).method("addNodeChangeListener")
      .with(eq(connection));
    connection.setNode2(null);
  }

  /**
   * Tests the initial state.
   */
  public void testInitial() {
    // has no effect
    connection.recalculateSize(drawingContext);
    // always true at the moment
    assertTrue(connection.isVisible(new Rectangle(0, 0, 120, 100)));
    assertEquals(2, connection.getPoints().size());
    assertNull(connection.getLabelAt(0.0, 0.0));
    assertFalse(connection.isRectilinear());
    assertEquals(SimpleLineConnectMethod.getInstance(),
      connection.getConnectMethod());
  }

  /**
   * Tests the setter methods.
   */
  public void testSetters() {
    connection.setParent(parent);
    assertEquals(parent, connection.getParent());
  }
  
  // Tests the connection selection in this class as well since
  // we already have a setup
  
  /**
   * Tests the selection.
   */
  public void testSelection() {
    Selection selection = connection.getSelection(operations);
    assertEquals(connection, selection.getElement());
  }
  
  /**
   * Tests the draw() method.
   */
  public void testDrawInvalidated() {
    Mock mockDrawingContext = mock(DrawingContext.class);
    mockNode1.expects(once()).method("getAbsCenterX").
      will(returnValue(50.0));
    mockNode1.expects(once()).method("getAbsCenterY").
      will(returnValue(50.0));
    mockNode2.expects(once()).method("getAbsCenterX").
      will(returnValue(200.0));
    mockNode2.expects(once()).method("getAbsCenterY").
      will(returnValue(260.0));
    mockNode1.expects(once()).method("calculateIntersection");
    mockNode2.expects(once()).method("calculateIntersection");
    
    // draw dashed
    connection.invalidate();
    connection.setIsDashed(true);
    mockDrawingContext.expects(once()).method("drawDashedLine");
    connection.draw((DrawingContext) mockDrawingContext.proxy());
    // draw non-dashed
    connection.setIsDashed(false);
    mockDrawingContext.expects(once()).method("drawLine");
    connection.draw((DrawingContext) mockDrawingContext.proxy());
    assertTrue(connection.isValid());
  }

  /**
   * Tests the methods that influence the isValid() property.
   */
  public void testInvalidationPaths() {
    connection.setValid(true);
    assertTrue(connection.isValid());
    connection.nodeMoved((Node) mockNode1.proxy());
    assertFalse(connection.isValid());
    connection.setValid(true);
    connection.nodeResized((Node) mockNode1.proxy());
    assertFalse(connection.isValid());
    connection.setValid(true);
    connection.invalidate();
    assertFalse(connection.isValid());
  }
  
  /**
   * Reassigns nodes.
   */
  public void testReassignNodes() {
    Mock mockNode1a = mock(Node.class), mockNode2a = mock(Node.class);
    mockNode1.expects(once()).method("removeNodeChangeListener")
      .with(eq(connection));
    mockNode1a.expects(once()).method("addNodeChangeListener")
      .with(eq(connection));
    connection.setNode1((Node) mockNode1a.proxy());
    
    mockNode2.expects(once()).method("removeNodeChangeListener")
      .with(eq(connection));
    mockNode2a.expects(once()).method("addNodeChangeListener")
      .with(eq(connection));
    connection.setNode2((Node) mockNode2a.proxy());
  }

  /**
   * Tests the resetPoints() method.
   */
  public void testResetPoints() {
    setupForReset();
    connection.setPoints(points);
    setResetExpectations();
    connection.resetPoints();
    checkResetAssertions(connection);
  }  

  /**
   * Tests that copyData() also resets the points.
   */
  public void testCopyData() {
    setupForReset();
    SimpleConnection copy = new SimpleConnection();
    setResetExpectations();
    mockNode1.expects(once()).method("addNodeChangeListener").with(eq(copy));
    mockNode2.expects(once()).method("addNodeChangeListener").with(eq(copy));
    copy.copyData(connection);
    checkResetAssertions(copy);
  }
  
  /**
   * Sets the expectations for reset().
   */
  private void setResetExpectations() {
    mockNode1.expects(atLeastOnce()).method("getAbsCenterX")
      .will(returnValue(10.0));
    mockNode1.expects(atLeastOnce()).method("getAbsCenterY")
      .will(returnValue(10.0));
    mockNode1.expects(once()).method("calculateIntersection");
    mockNode2.expects(atLeastOnce()).method("getAbsCenterX")
      .will(returnValue(50.0));
    mockNode2.expects(atLeastOnce()).method("getAbsCenterY")
      .will(returnValue(50.0));
    mockNode2.expects(once()).method("calculateIntersection");
  }
  
  /**
   * Checks the reset assertions on the specified connection.
   * @param conn the connection to check
   */
  private void checkResetAssertions(Connection conn) {
    assertEquals(2, conn.getPoints().size());
    assertEquals(new Point2D.Double(10.0, 10.0), conn.getPoints().get(0));
    assertEquals(new Point2D.Double(50.0, 50.0), conn.getPoints().get(1));
  }
  
  /**
   * Sets five points placed arbitrarly in the space.
   */
  private void setupForReset() {
    List<Point2D> points = new ArrayList<Point2D>();
    points.add(new Point2D.Double(12313, 154543));
    points.add(new Point2D.Double(6564, 14234));
    points.add(new Point2D.Double(2313, 7657));
    points.add(new Point2D.Double(231323, 76573));
    points.add(new Point2D.Double(23, 73));    
  }
}
