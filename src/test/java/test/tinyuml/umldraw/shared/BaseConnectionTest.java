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
package test.tinyuml.umldraw.shared;

import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import org.jmock.Mock;
import org.jmock.cglib.MockObjectTestCase;
import org.tinyuml.draw.CompositeNode;
import org.tinyuml.draw.Connection;
import org.tinyuml.draw.DiagramElement;
import org.tinyuml.draw.DiagramOperations;
import org.tinyuml.draw.DrawingContext;
import org.tinyuml.draw.LineConnectMethod;
import org.tinyuml.draw.Node;
import org.tinyuml.draw.Selection;
import org.tinyuml.draw.SimpleConnection;
import org.tinyuml.model.Relation;
import org.tinyuml.model.UmlRelation;
import org.tinyuml.umldraw.shared.BaseConnection;
import org.tinyuml.umldraw.shared.UmlConnectionSelection;

/**
 * Tests the BaseConnection class.
 * @author Wei-ju Wu
 * @version 1.0
 */
public class BaseConnectionTest extends MockObjectTestCase {

  class MyBaseConnection extends BaseConnection {
  }
  private BaseConnection connection;
  private Mock mockConnection = mock(Connection.class);
  private Mock mockOperations = mock(DiagramOperations.class);
  
  /**
   * {@inheritDoc}
   */
  @Override
  protected void setUp() {
    connection = new MyBaseConnection();
    connection.setConnection((Connection) mockConnection.proxy());
  }
  
  /**
   * Tests simple setters and getters.
   */
  public void testSimpleSettersAndGetters() {
    assertFalse(connection.canNestElements());
    assertFalse(connection.isNestable());
    assertEquals(mockConnection.proxy(), connection.getConnection());
    Mock mockRelation = mock(Relation.class);
    connection.setRelation((Relation) mockRelation.proxy());
    assertEquals(mockRelation.proxy(), connection.getModelElement());
    assertNull(connection.getLabelAt(2, 2));
  }
  
  /**
   * Tests the delegation to the embedded connection object.
   */
  public void testDelegation() {
    mockConnection.expects(once()).method("isRectilinear")
      .will(returnValue(true));
    assertTrue(connection.isRectilinear());
    
    mockConnection.expects(once()).method("isDashed")
      .will(returnValue(false));
    assertFalse(connection.isDashed());
    
    mockConnection.expects(once()).method("copyData");
    connection.copyData(null);
    
    AffineTransform transform = new AffineTransform();
    mockConnection.expects(once()).method("calculateRotationInEndPoint1")
      .will(returnValue(transform));
    assertEquals(transform, connection.calculateRotationInEndPoint1());
    
    mockConnection.expects(once()).method("calculateRotationInEndPoint2")
      .will(returnValue(transform));
    assertEquals(transform, connection.calculateRotationInEndPoint2());
    
    Mock mockNode = mock(Node.class);
    mockConnection.expects(once()).method("setNode1")
      .with(eq(mockNode.proxy()));
    connection.setNode1((Node) mockNode.proxy());
    mockConnection.expects(once()).method("getNode1")
      .will(returnValue(mockNode.proxy()));
    assertEquals(mockNode.proxy(), connection.getNode1());
    
    mockConnection.expects(once()).method("setNode2")
      .with(eq(mockNode.proxy()));
    connection.setNode2((Node) mockNode.proxy());
    mockConnection.expects(once()).method("getNode2")
      .will(returnValue(mockNode.proxy()));
    assertEquals(mockNode.proxy(), connection.getNode2());
    
    Mock mockDrawingContext = mock(DrawingContext.class);
    mockConnection.expects(once()).method("draw")
      .with(eq(mockDrawingContext.proxy()));
    connection.draw((DrawingContext) mockDrawingContext.proxy());
    List<Point2D> points = new ArrayList<Point2D>();
    mockConnection.expects(once()).method("getPoints")
      .will(returnValue(points));
    assertTrue(points == connection.getPoints());
    
    mockConnection.expects(once()).method("contains").with(eq(1.0), eq(2.0))
      .will(returnValue(true));
    assertTrue(connection.contains(1, 2));
    Rectangle2D bounds = new Rectangle2D.Double();
    mockConnection.expects(once()).method("intersects").with(eq(bounds))
      .will(returnValue(false));
    assertFalse(connection.intersects(bounds));
    mockConnection.expects(once()).method("getAbsoluteBounds")
      .will(returnValue(bounds));
    assertEquals(bounds, connection.getAbsoluteBounds());

    assertNotNull(connection.toString());
    Mock mockRelation = mock(Relation.class);
    connection.setRelation((Relation) mockRelation.proxy());
    mockRelation.expects(once()).method("toString")
      .will(returnValue("relation"));
    assertEquals("relation", connection.toString());
    
    mockConnection.expects(once()).method("isVisible").with(eq(bounds))
      .will(returnValue(true));
    assertTrue(connection.isVisible(bounds));
    Mock mockParent = mock(CompositeNode.class);
    mockConnection.expects(once()).method("setParent")
      .with(eq(mockParent.proxy()));
    connection.setParent((CompositeNode) mockParent.proxy());
    mockConnection.expects(once()).method("getParent")
      .will(returnValue(mockParent.proxy()));
    assertEquals(mockParent.proxy(), connection.getParent());
    
    mockConnection.expects(once()).method("recalculateSize")
      .with(eq(mockDrawingContext.proxy()));
    connection.recalculateSize((DrawingContext) mockDrawingContext.proxy());
    
    Mock mockDiagramOperations = mock(DiagramOperations.class);
    Mock mockSelection = mock(Selection.class);
    mockConnection.expects(once()).method("getSelection")
      .with(eq(mockDiagramOperations.proxy()))
      .will(returnValue(mockSelection.proxy()));
    assertTrue(connection.getSelection((DiagramOperations)
      mockDiagramOperations.proxy()) instanceof UmlConnectionSelection);
    
    mockConnection.expects(once()).method("isValid").will(returnValue(false));
    assertFalse(connection.isValid());
    mockConnection.expects(once()).method("invalidate");
    connection.invalidate();
    mockConnection.expects(once()).method("setIsDashed").with(eq(true));
    connection.setIsDashed(true);
    mockConnection.expects(once()).method("setPoints").with(eq(points));
    connection.setPoints(points);
    mockConnection.expects(once()).method("resetPoints");
    connection.resetPoints();
 
    Point2D endpoint = new Point2D.Double();
    mockConnection.expects(once()).method("getEndPoint1")
      .will(returnValue(endpoint));
    assertTrue(endpoint == connection.getEndPoint1());
    mockConnection.expects(once()).method("getEndPoint2")
      .will(returnValue(endpoint));
    assertTrue(endpoint == connection.getEndPoint2());
    
    Mock mockConnectMethod = mock(LineConnectMethod.class);
    mockConnection.expects(once()).method("getConnectMethod")
      .will(returnValue(mockConnectMethod.proxy()));
    assertEquals(mockConnectMethod.proxy(), connection.getConnectMethod());
    
    mockConnection.expects(once()).method("getSelection")
      .will(returnValue(mockSelection.proxy()));
    assertEquals(connection, connection.getSelection(
      (DiagramOperations) mockOperations.proxy()).getElement());
    
    Line2D segment = new Line2D.Double();
    mockConnection.expects(once()).method("getSegmentAtPoint")
      .with(eq(1.0), eq(2.0)).will(returnValue(segment));
    assertEquals(segment, connection.getSegmentAtPoint(1.0, 2.0));
    
    List<Line2D> segments = new ArrayList<Line2D>();
    segments.add(segment);
    mockConnection.expects(once()).method("getSegments")
      .will(returnValue(segments));
    assertEquals(segments, connection.getSegments());
    
    mockConnection.expects(once()).method("isAncestor")
      .will(returnValue(true));
    assertTrue(connection.isAncestor((DiagramElement) mockParent.proxy()));
  }

  /**
   * Tests the clone() method.
   */
  public void testClone() {
    MyBaseConnection conn = new MyBaseConnection();
    SimpleConnection simpleconn = new SimpleConnection();
    Relation relation = new UmlRelation();
    conn.setConnection(simpleconn);
    conn.setRelation(relation);
    MyBaseConnection cloned = (MyBaseConnection) conn.clone();
    assertTrue(cloned.getConnection() != conn.getConnection());
    assertTrue(cloned.getModelElement() != conn.getModelElement());
  }
}
