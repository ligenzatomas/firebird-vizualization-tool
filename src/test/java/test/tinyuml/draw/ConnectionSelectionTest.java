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

import java.awt.Cursor;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import org.jmock.Mock;
import org.jmock.cglib.MockObjectTestCase;
import org.tinyuml.draw.Connection;
import org.tinyuml.draw.ConnectionSelection;
import org.tinyuml.draw.DiagramOperations;
import org.tinyuml.draw.DrawingContext;

/**
 * A test class for ConnectionSelection. We test the protocol here and therefore
 * apply a call log as described in "Test driven development".
 * @author Wei-ju Wu
 * @version 1.0
 */
public class ConnectionSelectionTest extends MockObjectTestCase {

  private StringBuilder log = new StringBuilder();
  private Mock mockConnection = mock(Connection.class);
  private List<Point2D> editpoints = new ArrayList<Point2D>();
  private Mock mockDrawingContext = mock(DrawingContext.class);
  private Mock mockOperations = mock(DiagramOperations.class);
  class MyConnectionSelection extends ConnectionSelection {
    MyConnectionSelection() {
      super((DiagramOperations) mockOperations.proxy(),
        (Connection) mockConnection.proxy());
    }
    public List<Point2D> getEditPoints() {
      return editpoints;
    }
    protected void startDragControlPoint(double xcoord, double ycoord) {
      log.append("startDragControlPoint");
      setIsDragControlPoint(true);
    }
    protected void finishDragControlPoint(double xcoord, double ycoord) {
      log.append("finishDragControlPoint");
      setIsDragControlPoint(false);
    }
    protected void startDragSegment(double xcoord, double ycoord) {
      log.append("startDragSegment");
      setIsDragSegment(true);
    }
    protected void finishDragSegment(double xcoord, double ycoord) {
      log.append("finishDragSegment");
      setIsDragSegment(false);
    }
    public void updatePosition(double xcoord, double ycoord) {
      log.append("updatePosition");
    }
  }
  
  private ConnectionSelection selection = new MyConnectionSelection();

  /**
   * {@inheritDoc}
   */
  @Override
  protected void setUp() {
    editpoints.add(new Point2D.Double(0.0, 0.0));
    editpoints.add(new Point2D.Double(10.0, 10.0));
    editpoints.add(new Point2D.Double(20.0, 20.0));
  }

  /**
   * Tests the simple getter methods.
   */
  public void testInitial() {
    assertEquals(mockConnection.proxy(), selection.getElement());
    assertEquals(1, selection.getElements().size());
    assertTrue(selection.getElements().contains(mockConnection.proxy()));
    // these have no effect
    assertEquals(Cursor.getDefaultCursor(),
      selection.getCursorForPosition(3.0, 1.0));
    selection.updateDimensions();
  }

  /**
   * Tests the contains() method.
   */
  public void testContains() {
    mockConnection.expects(once()).method("contains").with(eq(1.0), eq(2.3)).
      will(returnValue(true));
    assertTrue(selection.contains(1.0, 2.3));
  }
  
  /**
   * Tests the draw() method.
   */
  public void testDraw() {
    mockDrawingContext = mock(DrawingContext.class);
    expectGetPoints();
    mockDrawingContext.expects(exactly(2)).method("fillRectangle");
    selection.draw((DrawingContext) mockDrawingContext.proxy());
  }
  
  /**
   * Tests dragging a control point protocol.
   */
  public void testDragControlPoint() {
    expectGetPointsThreeControlPoints();
    selection.startDragging(2.0, 3.0);
    assertEquals("startDragControlPoint", log.toString());
    expectDrawWhileDragging(3);
    selection.draw((DrawingContext) mockDrawingContext.proxy());
    mockOperations.expects(once()).method("setNewConnectionPoints");
    selection.stopDragging(10.0, 20.0);
    assertFalse(selection.isDragging());
    assertEquals("startDragControlPointfinishDragControlPoint", log.toString());
  }
  
  /**
   * Tests dragging a control point and cancel protocol.
   */
  public void testDragControlPointCancelled() {
    expectGetPointsThreeControlPoints();
    selection.startDragging(2.0, 3.0);
    assertEquals("startDragControlPoint", log.toString());
    selection.cancelDragging();
    assertFalse(selection.isDragging());
    assertEquals("startDragControlPoint", log.toString());
  }
  
  /**
   * Tests dragging a segment protocol.
   */
  public void testDragSegment() {
    expectGetPoints();
    selection.startDragging(5.0, 1.0);
    assertEquals("startDragSegment", log.toString());
    expectDrawWhileDragging(2);
    selection.draw((DrawingContext) mockDrawingContext.proxy());
    mockOperations.expects(once()).method("setNewConnectionPoints");
    selection.stopDragging(10.0, 20.0);
    assertFalse(selection.isDragging());
    assertEquals("startDragSegmentfinishDragSegment", log.toString());    
  }
  
  /**
   * Tests dragging a segment and cancel protocol.
   */
  public void testDragSegmentCancelled() {
    expectGetPoints();
    selection.startDragging(5.0, 1.0);
    assertEquals("startDragSegment", log.toString());
    selection.cancelDragging();
    assertFalse(selection.isDragging());
    assertEquals("startDragSegment", log.toString());
  }
  
  // **********************************************************************
  // *** Simulate mock functionality
  // *************************************
  
  /**
   * Expectations for draw() during a dragging operation.
   * @param numControlPoints the number of control points
   */
  private void expectDrawWhileDragging(int numControlPoints) {
    mockDrawingContext.expects(once()).method("drawLine")
      .with(eq(0.0), eq(0.0), eq(10.0), eq(10.0));
    mockDrawingContext.expects(once()).method("drawLine")
      .with(eq(10.0), eq(10.0), eq(20.0), eq(20.0));
     mockDrawingContext.expects(exactly(numControlPoints))
      .method("fillRectangle");
  }
  
  /**
   * Expectation fixture for getPoints().
   */
  private void expectGetPoints() {
    List<Point2D> points = new ArrayList<Point2D>();
    points.add(new Point2D.Double(1.0, 1.0));
    points.add(new Point2D.Double(10.0, 1.0));
    mockConnection.expects(atLeastOnce()).method("getPoints")
      .will(returnValue(points));    
  }  

  /**
   * Expectations fixture for getPoints(), with three control points.
   */
  private void expectGetPointsThreeControlPoints() {
    List<Point2D> points = new ArrayList<Point2D>();
    points.add(new Point2D.Double(1.0, 1.0));
    points.add(new Point2D.Double(10.0, 10.0));
    points.add(new Point2D.Double(30.0, 30.0));
    mockConnection.expects(atLeastOnce()).method("getPoints")
      .will(returnValue(points));
  }
}
