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

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.util.List;
import org.jmock.Mock;
import org.jmock.cglib.MockObjectTestCase;
import org.tinyuml.draw.CompositeNode;
import org.tinyuml.draw.Connection;
import org.tinyuml.draw.Diagram;
import org.tinyuml.draw.DiagramElement;
import org.tinyuml.draw.DiagramOperations;
import org.tinyuml.draw.DrawingContext;
import org.tinyuml.draw.Node;
import org.tinyuml.draw.RectilinearConnection;
import org.tinyuml.draw.RectilinearConnectionSelection;
import org.tinyuml.draw.RectilinearLineBuilder;
import org.tinyuml.util.Command;

/**
 *
 * @author Wei-ju Wu
 * @version 1.0
 */
public class RectilinearConnectionSelectionTest extends MockObjectTestCase {

  private static final double EPS = 0.001;
  
  private CompositeNode parent = new MyAbsoluteParent();
  private Node horizNode1 = new MyCompositeNode();
  private Node horizNode2 = new MyCompositeNode();
  private Node vertNode1 = new MyCompositeNode();
  private Node vertNode2 = new MyCompositeNode();
  private Node rectNode1 = new MyCompositeNode();
  private Node rectNode2 = new MyCompositeNode();
  private Connection horizConn = new RectilinearConnection();  
  private Connection vertConn = new RectilinearConnection();  
  private Connection rectConn = new RectilinearConnection();
  private RectilinearConnectionSelection horizSelection, vertSelection,
    rectSelection;
  private Mock mockDrawingContext = mock(DrawingContext.class);
  private DiagramOperations operations = new DiagramOperations() {
    public void moveElements(Command[] moveOperations) { }
    public void resizeElement(Node element, Point2D newpos,
      Dimension2D newsize) { }
    public void editProperties(DiagramElement element) { }
    public Diagram getDiagram() { return null; }
    public void setNewConnectionPoints(Connection conn, List<Point2D> points) {
      conn.setPoints(points);
    }
  };
  
  /**
   * {@inheritDoc}
   */
  @Override
  protected void setUp() {
    setupHorizontalConnection();
    setupVerticalConnection();
    setupRectilinearConnection();
  }
  
  /**
   * Sets up the horizontal connection.
   */
  private void setupHorizontalConnection() {
    horizNode1.setOrigin(10.0, 100.0);
    horizNode2.setOrigin(120.0, 98.0); // slightly above
    horizSelection = setupConnection(horizNode1, horizNode2, horizConn);    
  }
  
  /**
   * Sets up the vertical connection.
   */
  private void setupVerticalConnection() {
    vertNode1.setOrigin(10.0, 100.0);
    vertNode2.setOrigin(12.0, 180.0); // slightly right
    vertSelection = setupConnection(vertNode1, vertNode2, vertConn);        
  }
  
  /**
   * Sets up the rectilinear connection.
   */
  private void setupRectilinearConnection() {
    rectNode1.setOrigin(10.0, 100.0);
    rectNode2.setOrigin(120.0, 180.0);
    rectSelection = setupConnection(rectNode1, rectNode2, rectConn);    
  }
  
  /**
   * Sets up a connection.
   * @param node1 the first node
   * @param node2 the second node
   * @param conn the connection
   * @return the selection
   */
  private RectilinearConnectionSelection setupConnection(Node node1, Node node2,
    Connection conn) {
    node1.setParent(parent);
    node1.setSize(80.0, 40.0);
    node2.setParent(parent);
    node2.setSize(80.0, 40.0);
    conn.setParent(parent);
    bindConnection(conn, node1, node2);
    conn.setPoints(
      RectilinearLineBuilder.getInstance().calculateLineSegments(node1, node2));
    return (RectilinearConnectionSelection)
      conn.getSelection(operations);        
  }

  /**
   * Binds the connection to the nodes.
   * @param conn the connection
   * @param node1 the first node
   * @param node2 the second node
   */
  private void bindConnection(Connection conn, Node node1, Node node2) {
    conn.setNode1(node1);
    conn.setNode2(node2);
    node1.addConnection(conn);
    node2.addConnection(conn);
  }
  
  // *************************************************************************
  // ***** Testing vertical drag
  // *************************************************************************
  /**
   * Tests dragging a horizontal segment, just a little, so it is within the
   * nodes.
   */
  public void testDragHorizontalSegmentJustALittleUp() {
    // the connection is on 119
    horizSelection.startDragging(110.0, 120.0);
    assertTrue(horizSelection.isDragging());
    horizSelection.updatePosition(110.0, 110.0);
    horizSelection.stopDragging(110.0, 110.0);
    assertFalse(horizSelection.isDragging());
    
    // number of points stays at two
    assertEquals(2, horizConn.getPoints().size());
    assertTrue(equals(horizConn.getPoints().get(0).getX(),
      horizNode1.getAbsoluteX2()));
    assertTrue(equals(109, horizConn.getPoints().get(0).getY()));
    assertTrue(equals(horizNode2.getAbsoluteX1(),
      horizConn.getPoints().get(1).getX()));
    // is still horizontal
    assertTrue("connection should be horizontal",
      equals(horizConn.getPoints().get(0).getY(),
      horizConn.getPoints().get(1).getY()));
  }

  /**
   * Tests dragging a horizontal segment, enough to disconnect from nodes, so
   * two points will be generated
   */
  public void testDragHorizontalSegmentUpDisconnectFromNodes() {
    // the connection is on 119
    horizSelection.startDragging(110.0, 120.0);
    dragUp(horizSelection, 110.0, 120.0, 80.0);
    horizSelection.stopDragging(110.0, 80.0);
    
    // number of points extended to four
    assertEquals(4, horizConn.getPoints().size());
    // first point
    assertTrue(equals(horizNode1.getAbsoluteX2(),
      horizConn.getPoints().get(0).getX()));
    assertTrue(equals(horizNode1.getAbsoluteY1(),
      horizConn.getPoints().get(0).getY()));
    
    // The dragged segment is still parallel
    checkHorizontalMiddleSegmentAssertions();
    
    // fourth point
    assertTrue(equals(horizNode2.getAbsoluteX1(),
      horizConn.getPoints().get(3).getX()));
    assertTrue(equals(horizNode2.getAbsoluteY1(),
      horizConn.getPoints().get(3).getY()));
  }

  /**
   * Tests dragging a horizontal segment, enough to disconnect from nodes, so
   * two points will be generated
   */
  public void testDragHorizontalSegmentDownDisconnectFromNodes() {
    // the connection is on 119
    horizSelection.startDragging(110.0, 120.0);
    dragDown(horizSelection, 110.0, 120.0, 160.0);
    horizSelection.stopDragging(110.0, 160.0);
    
    // number of points extended to four
    assertEquals(4, horizConn.getPoints().size());
    // first point
    assertTrue(equals(horizNode1.getAbsoluteX2(),
      horizConn.getPoints().get(0).getX()));
    assertTrue(equals(horizNode1.getAbsoluteY2(),
      horizConn.getPoints().get(0).getY()));

    // The dragged segment is still parallel
    checkHorizontalMiddleSegmentAssertions();

    // fourth point
    assertTrue(equals(horizNode2.getAbsoluteX1(),
      horizConn.getPoints().get(3).getX()));
    assertTrue(equals(horizNode2.getAbsoluteY2(),
      horizConn.getPoints().get(3).getY()));
  }
  
  /**
   * Drags the horizontal segment out and in again. The intended result should
   * be that the outer nodes disappear.
   */
  public void testDragHorizontalSegmentDownDisconnectFromNodesAndJoinAgain() {
    // the connection is on 119
    horizSelection.startDragging(110.0, 120.0);
    dragDown(horizSelection, 110.0, 120.0, 170.0);
    dragUp(horizSelection, 110.0, 170.0, 120.0);
    horizSelection.stopDragging(110.0, 120.0);
    
    // number of points is two, everything should stay the same
    assertEquals(2, horizSelection.getEditPoints().size());
    assertEquals(2, horizConn.getPoints().size());
    assertTrue(equals(horizConn.getPoints().get(0).getY(),
      horizConn.getPoints().get(1).getY()));
    assertTrue(equals(horizConn.getPoints().get(0).getX(),
      horizNode1.getAbsoluteX2()));
    assertTrue(equals(horizConn.getPoints().get(1).getX(),
      horizNode2.getAbsoluteX1()));
    
    // Do it differently, first drag, stop then drag back and stop
    // the connection is on 119
    horizSelection.startDragging(110.0, 120.0);
    dragDown(horizSelection, 110.0, 120.0, 170.0);
    horizSelection.stopDragging(110.0, 170.0);
    assertEquals(4, horizSelection.getEditPoints().size());
    
    horizSelection.startDragging(110.0, 170.0);
    dragUp(horizSelection, 110.0, 170.0, 120.0);
    horizSelection.stopDragging(110.0, 120.0);

    // number of points is two, everything should stay the same
    assertEquals(2, horizSelection.getEditPoints().size());
    assertEquals(2, horizConn.getPoints().size());
    assertTrue(equals(horizConn.getPoints().get(0).getY(),
      horizConn.getPoints().get(1).getY()));
    assertTrue(equals(horizConn.getPoints().get(0).getX(),
      horizNode1.getAbsoluteX2()));
    assertTrue(equals(horizConn.getPoints().get(1).getX(),
      horizNode2.getAbsoluteX1()));
  }
  
  /**
   * Checks the middle segment assertions. Always true for dragged out
   * horizontal segments.
   */
  private void checkHorizontalMiddleSegmentAssertions() {
    assertTrue(equals(horizConn.getPoints().get(1).getY(),
      horizConn.getPoints().get(2).getY()));
    assertTrue(equals(horizConn.getPoints().get(1).getX(),
      horizConn.getPoints().get(0).getX()));
    assertTrue(equals(horizConn.getPoints().get(2).getX(),
      horizConn.getPoints().get(3).getX()));    
  }

  // *************************************************************************
  // ***** Testing horizontal drag
  // *************************************************************************

  /**
   * Tests dragging a vertical segment, just a little, so it is within the
   * nodes bounds.
   */
  public void testDragVerticalSegmentJustALittleRight() {
    // the connection is on (51, 140) -> (51, 180.0)
    vertSelection.startDragging(51.0, 160.0);
    assertTrue(vertSelection.isDragging());
    vertSelection.updatePosition(70.0, 160.0);
    vertSelection.stopDragging(70.0, 160.0);
    assertFalse(vertSelection.isDragging());
    
    // number of points stays at two
    assertEquals(2, vertConn.getPoints().size());
    assertTrue(equals(vertNode1.getAbsoluteY2(),
      vertConn.getPoints().get(0).getY()));
    assertTrue(equals(70.0, vertConn.getPoints().get(0).getX()));
    assertTrue(equals(vertNode2.getAbsoluteY1(),
      vertConn.getPoints().get(1).getY()));
    // is still vertical
    assertTrue("connection should be vertical",
      equals(vertConn.getPoints().get(0).getX(),
      vertConn.getPoints().get(1).getX()));
  }

  /**
   * Tests dragging a vertical segment, enough to disconnect from nodes, so
   * two points will be generated
   */
  public void testDragVerticalSegmentLeftDisconnectFromNodes() {
    // the connection is on (51, 140) -> (51, 180.0)
    vertSelection.startDragging(51.0, 160.0);
    dragLeft(vertSelection, 51.0, -20.0, 170.0);
    vertSelection.stopDragging(-20.0, 170.0);
    
    // number of points extended to four
    assertEquals(4, vertConn.getPoints().size());
    // first point
    assertTrue(equals(vertNode1.getAbsoluteX1(),
      vertConn.getPoints().get(0).getX()));
    assertTrue(equals(vertNode1.getAbsoluteY2(),
      vertConn.getPoints().get(0).getY()));
    
    // The dragged segment is still parallel
    checkVerticalMiddleSegmentAssertions();
    
    // fourth point
    assertTrue(equals(vertNode2.getAbsoluteX1(),
      vertConn.getPoints().get(3).getX()));
    assertTrue(equals(vertNode2.getAbsoluteY1(),
      vertConn.getPoints().get(3).getY()));
  }

  /**
   * Tests dragging a vertical segment to the right, enough to disconnect from
   * nodes, so two additional points will be generated
   */
  public void testDragVerticalSegmentRightDisconnectFromNodes() {
    // the connection is on (51, 140) -> (51, 180.0)
    vertSelection.startDragging(51.0, 160.0);
    dragRight(vertSelection, 51.0, 120.0, 170.0);
    vertSelection.stopDragging(120.0, 170.0);
    
    // number of points extended to four
    assertEquals(4, vertConn.getPoints().size());
    // first point
    assertTrue(equals(vertNode1.getAbsoluteX2(),
      vertConn.getPoints().get(0).getX()));
    assertTrue(equals(vertNode1.getAbsoluteY2(),
      vertConn.getPoints().get(0).getY()));
    
    // The dragged segment is still parallel
    checkVerticalMiddleSegmentAssertions();
    
    // fourth point
    assertTrue(equals(vertNode2.getAbsoluteX2(),
      vertConn.getPoints().get(3).getX()));
    assertTrue(equals(vertNode2.getAbsoluteY1(),
      vertConn.getPoints().get(3).getY()));
  }

  /**
   * Checks the middle segment assertions for vertical segments. Always true
   * for dragged out vertical segments.
   */
  private void checkVerticalMiddleSegmentAssertions() {
    assertTrue(equals(vertConn.getPoints().get(1).getX(),
      vertConn.getPoints().get(2).getX()));
    assertTrue(equals(vertConn.getPoints().get(1).getY(),
      vertConn.getPoints().get(0).getY()));
    assertTrue(equals(vertConn.getPoints().get(2).getY(),
      vertConn.getPoints().get(3).getY()));    
  }
   
  // *************************************************************************
  // ***** Testing rectilinear setup, this is the complicated one
  // *************************************************************************
  
  /**
   * Tests dragging the upper horizontal segment to the middle of the second
   * node, the resulting connection should connecto to the node at the
   * intersection point.
   */
  public void testDragHorizontalSegmentToNode2MiddleRectConnection() {
    rectSelection.startDragging(120.0, 120.0);
    dragDown(rectSelection, 120.0, 120.0, 190.0);
    rectSelection.stopDragging(120.0, 190.0);    
    assertEquals(3, rectConn.getPoints().size());
    assertTrue(equals(rectNode1.getAbsoluteX2(),
      rectConn.getPoints().get(0).getX()));
    assertTrue(equals(rectNode1.getAbsoluteY2(),
      rectConn.getPoints().get(0).getY()));
    assertTrue(equals(rectConn.getPoints().get(1).getY(),
      rectConn.getPoints().get(2).getY()));
    assertTrue(equals(rectNode2.getAbsoluteX1(),
      rectConn.getPoints().get(2).getX()));
  }

  // *************************************************************************
  // ***** Testing rectilinear setup, dragging single points
  // *************************************************************************
  
  /**
   * Tests that the drag point on the right side of the first node can only
   * be dragged in y direction.
   */
  public void testDragPointConnectedToNodeOnlyInYDirection() {
    Point2D point0 = rectConn.getPoints().get(0);
    double p0x = point0.getX(), p0y = point0.getY();
    rectSelection.startDragging(p0x, p0y);
    rectSelection.updatePosition(p0x + 5, p0y + 5);
    rectSelection.updatePosition(p0x + 10, p0y + 10);
    rectSelection.updatePosition(p0x + 15, p0y + 15);
    rectSelection.stopDragging(p0x + 15, p0y + 15);
    assertEquals(3, rectConn.getPoints().size());
    Point2D point0after = rectConn.getPoints().get(0);
    assertEquals(p0x, point0after.getX());
    assertEquals(p0y + 15, point0after.getY());    
  }

  /**
   * Tests that the drag point on the upper side of the first node can only
   * be dragged in x direction.
   */
  public void testDragPointConnectedToNodeOnlyInxDirection() {
    Point2D pointn = rectConn.getPoints().get(rectConn.getPoints().size() - 1);
    double pnx = pointn.getX(), pny = pointn.getY();
    rectSelection.startDragging(pnx, pny);
    // drag up and left
    rectSelection.updatePosition(pnx - 5, pny - 5);
    rectSelection.updatePosition(pnx - 10, pny - 10);
    rectSelection.updatePosition(pnx - 15, pny - 15);
    rectSelection.stopDragging(pnx - 15, pny - 15);
    assertEquals(3, rectConn.getPoints().size());
    Point2D pointnafter = rectConn.getPoints().get(
      rectConn.getPoints().size() - 1);
    assertEquals(pny, pointnafter.getY()); // y did not change
    assertEquals(pnx - 15, pointnafter.getX());    
  }
  
  /**
   * Tests dragging a point from the middle.
   */
  public void testDragUnconstrainedControlPoint() {
    Point2D pointn = rectConn.getPoints().get(1);
    double pnx = pointn.getX(), pny = pointn.getY();
    rectSelection.startDragging(pnx, pny);
    // drag up and left
    rectSelection.updatePosition(pnx - 5, pny - 5);
    rectSelection.updatePosition(pnx - 10, pny - 10);
    rectSelection.updatePosition(pnx - 15, pny - 15);
    rectSelection.stopDragging(pnx - 15, pny - 15);
    assertEquals(3, rectConn.getPoints().size());
    Point2D pointnafter = rectConn.getPoints().get(1);
    // Both points changed
    assertEquals(pny - 15, pointnafter.getY());
    assertEquals(pnx - 15, pointnafter.getX());
    // the connected points moved in parallel
    assertEquals(pny - 15, rectConn.getPoints().get(0).getY());
    assertEquals(pnx - 15, rectConn.getPoints().get(2).getX());
  }

  // *************************************************************************
  // ***** Helper methods
  // *************************************************************************
  /**
   * Returns a tolerant comparison of two doubles.
   * @param value1 the first value
   * @param value2 the second value
   * @return true if the two values are not more than EPS away, false otherwise
   */
  private boolean equals(double value1, double value2) {
    return Math.abs(value1 - value2) < EPS;
  }

  /**
   * Simulates a stream of drag down events. Some errors only show if a larger
   * number of updatePosition() methods is called.
   * @param selection the selection object
   * @param x the x coordinate
   * @param starty the start y value
   * @param endy the end y value, needs to be >= starty
   */
  private void dragDown(RectilinearConnectionSelection selection, double x,
    double starty, double endy) {
    for (double y = starty; y <= endy; y += 10.0) {
      selection.updatePosition(x, y);
    }
  }
  
  /**
   * Simulates a stream of drag up events. Some errors only show if a larger
   * number of updatePosition() methods is called.
   * @param selection the selection object
   * @param x the x coordinate
   * @param starty the start y value
   * @param endy the end y value, needs to be <= starty
   */
  private void dragUp(RectilinearConnectionSelection selection, double x,
    double starty, double endy) {
    for (double y = starty; y >= endy; y -= 10.0) {
      selection.updatePosition(x, y);
    }
  }

  /**
   * Simulates a stream of drag right events. Some errors only show if a larger
   * number of updatePosition() methods is called.
   * @param selection the selection object
   * @param startx the start x coordinate
   * @param endx the end x value, should be >= startx
   * @param y coordinate
   */
  private void dragRight(RectilinearConnectionSelection selection,
    double startx, double endx, double y) {
    for (double x = startx; x <= endx; x += 10.0) {
      selection.updatePosition(x, y);
    }
  }

  /**
   * Simulates a stream of drag left events. Some errors only show if a larger
   * number of updatePosition() methods is called.
   * @param selection the selection object
   * @param startx the start x coordinate
   * @param endx the end x value, should be <= startx
   * @param y coordinate
   */
  private void dragLeft(RectilinearConnectionSelection selection,
    double startx, double endx, double y) {
    for (double x = startx; x >= endx; x -= 10.0) {
      selection.updatePosition(x, y);
    }
  }
}
