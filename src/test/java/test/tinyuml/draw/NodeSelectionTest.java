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
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import org.jmock.Mock;
import org.jmock.cglib.MockObjectTestCase;
import org.jmock.core.Constraint;
import org.tinyuml.draw.CompositeNode;
import org.tinyuml.draw.Diagram;
import org.tinyuml.draw.DiagramOperations;
import org.tinyuml.draw.DoubleDimension;
import org.tinyuml.draw.DrawingContext;
import org.tinyuml.draw.Node;
import org.tinyuml.draw.NodeSelection;
import org.tinyuml.draw.NullElement;
import org.tinyuml.umldraw.shared.GeneralDiagram;

/**
 * A test class for NodeSelection.
 * @author Wei-ju Wu
 * @version 1.0
 */
public class NodeSelectionTest extends MockObjectTestCase {
  private Mock mockOperations = mock(DiagramOperations.class);
  private Mock mockNode = mock(Node.class);
  private Mock mockParent = mock(CompositeNode.class);
  private Mock mockDiagram = mock(GeneralDiagram.class);
  private Node node = (Node) mockNode.proxy();
  private NodeSelection selection;
  private double x = 2.0, y = 5.0, width = 320.0, height = 420.0;
  private Mock mockDrawingContext = mock(DrawingContext.class);

  /**
   * {@inheritDoc}
   */
  protected void setUp() {
    mockNode.expects(atLeastOnce()).method("getAbsoluteX1")
      .will(returnValue(x));
    mockNode.expects(atLeastOnce()).method("getAbsoluteY1")
      .will(returnValue(y));
    mockNode.expects(atLeastOnce()).method("getSize")
      .will(returnValue(new DoubleDimension(width, height)));
    mockNode.expects(once()).method("addNodeChangeListener");
    selection = new NodeSelection((DiagramOperations) mockOperations.proxy(),
      (Node) mockNode.proxy());
  }

  /**
   * Tests initial state.
   */
  public void testInitial() {
    assertEquals(node, selection.getElement());
    assertEquals(1, selection.getElements().size());
    assertTrue(selection.getElements().contains(node));
    selection.cancelDragging(); // this is a No-Op
    selection.nodeMoved(node); // this also
    assertFalse(selection.isDragging());
  }
  
  /**
   * Tests the contains method.
   */
  public void testContains() {
    // inside bounds
    mockNode.expects(once()).method("contains").with(eq(x + 5), eq(y + 5))
      .will(returnValue(true));
    assertTrue(selection.contains(x + 5, y + 5));
    // outside bounds
    mockNode.expects(atLeastOnce()).method("contains").will(returnValue(false));
    assertFalse(selection.contains(1000.0, 2000.0));
    // test all handles
    assertTrue(selection.contains(x - 1, y - 1)); // NW handle
    assertTrue(selection.contains(x + width + 2, y - 1)); // NE handle
    assertTrue(selection.contains(x + width + 1, y - 1)); // NE handle
    assertTrue(selection.contains(x + width + 1, y + height + 1)); // SE handle
  }

  /**
   * Tests getCursorForPosition().
   */
  public void testGetCursorForPosition() {
    // Get cursors
    // outside
    assertEquals(Cursor.getDefaultCursor(),
      selection.getCursorForPosition(1000.0, 2000.0));
    // directly inside
    assertEquals(Cursor.getDefaultCursor(),
      selection.getCursorForPosition(x + 5, y + 5));
    // NW
    assertEquals(Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR),
      selection.getCursorForPosition(x - 1, y - 1));
    // NE
    assertEquals(Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR),
      selection.getCursorForPosition(x + width + 1, y - 1));
    // SW
    assertEquals(Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR),
      selection.getCursorForPosition(x - 1, y + height + 1));
    // SE
    assertEquals(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR),
      selection.getCursorForPosition(x + width + 1, y + height + 1));
  }
  
  /**
   * Tests the draw() operation, four handles are drawn.
   */
  public void testDraw() {
    Mock mockDrawingContext = mock(DrawingContext.class);
    mockDrawingContext.expects(exactly(4)).method("fillRectangle");
    selection.draw((DrawingContext) mockDrawingContext.proxy());
  }
  
  /**
   * Fixture for standard drag operations.
   */
  private void setStandardDragFixture() {
    Point2D origin = new Point2D.Double(0, 0);
    // start dragging somewhere in the selection
    selection.startDragging(100.0, 100.0);
    assertTrue(selection.isDragging());
    // move it a little
    mockOperations.expects(atLeastOnce()).method("getDiagram").
      will(returnValue(mockDiagram.proxy()));
    mockDiagram.expects(atLeastOnce()).method("getOrigin").
      will(returnValue(origin));
    mockDiagram.expects(atLeastOnce()).method("snap");
    selection.updatePosition(150.0, 150.0);
    // and draw it
    Constraint[] params = {eq(52.0), eq(55.0), eq(320.0), eq(420.0), NULL};
    mockDrawingContext.expects(once()).method("drawRectangle").
      with(params);
    selection.draw((DrawingContext) mockDrawingContext.proxy());    
  }
  
  /**
   * Sets the expectations for reparenting.
   */
  private void setReparentingFixture(Mock oldParent, Mock newParent) {
    oldParent.expects(once()).method("removeChild")
      .with(eq(mockNode.proxy()));
    newParent.expects(once()).method("addChild").with(eq(mockNode.proxy()));
    mockNode.expects(once()).method("setAbsolutePos");
  }
  
  private void setYesDropTargetExpectations(Mock mockNewParent) {
    mockDiagram.expects(atLeastOnce()).method("getChildAt")
      .will(returnValue(mockNewParent.proxy()));
    mockNewParent.expects(atLeastOnce()).method("canNestElements")
      .will(returnValue(true));
    mockNewParent.expects(atLeastOnce()).method("isAncestor")
      .with(eq(mockNode.proxy())).will(returnValue(false));
    mockNewParent.expects(atLeastOnce()).method("getAbsoluteBounds")
      .will(returnValue(new Rectangle2D.Double(0, 0, 100, 100)));
    mockDrawingContext.expects(once()).method("drawRectangle");
  }
  
  /**
   * Simulates dragging into a nesting parent.
   */
  public void testDragIntoNestingParent() {
    Mock mockNewParent = mock(CompositeNode.class);
    setYesDropTargetExpectations(mockNewParent);
    Rectangle2D nodeBounds = new Rectangle2D.Double(5, 5, 10, 10);
    mockNode.expects(atLeastOnce()).method("getAbsoluteBounds")
      .will(returnValue(nodeBounds));
    setStandardDragFixture();

    // stop dragging
    Mock mockOldParent = mock(CompositeNode.class);
    mockNode.expects(atLeastOnce()).method("getParent")
      .will(returnValue(mockOldParent.proxy()));
    mockOperations.expects(once()).method("moveElements");
    selection.stopDragging(150.0, 150.0);
    assertFalse(selection.isDragging());
  }

  /**
   * Simulates dragging out of a nesting parent.
   */
  public void testDragOutOfNestingParent() {
    mockDiagram.expects(atLeastOnce()).method("getChildAt")
      .will(returnValue(NullElement.getInstance()));

    setStandardDragFixture();
    // stop dragging
    Mock mockOldParent = mock(CompositeNode.class);
    mockNode.expects(atLeastOnce()).method("getParent")
      .will(returnValue(mockOldParent.proxy()));
    mockOperations.expects(once()).method("moveElements");
    selection.stopDragging(150.0, 150.0);
    assertFalse(selection.isDragging());
  }

  /**
   * Simulates a dragging operation for moving.
   */
  public void testDragMoveRegular() {
    // Here we return the NullElement in order to drag without dropping
    // into a container
    mockDiagram.expects(atLeastOnce()).method("getChildAt")
      .will(returnValue(NullElement.getInstance()));
    setStandardDragFixture();
    // stop dragging
    mockNode.expects(atLeastOnce()).method("getParent")
      .will(returnValue(mockDiagram.proxy()));
    mockOperations.expects(once()).method("moveElements");
    selection.stopDragging(150.0, 150.0);
    assertFalse(selection.isDragging());
  }
  
  /**
   * Tests the the element can not leave the left or top bounds
   */
  public void testDragMoveLeftBounds() {
    Point2D origin = new Point2D.Double(0, 0);
    // start dragging somewhere in the selection
    selection.startDragging(20.0, 10.0);
    assertTrue(selection.isDragging());
    // move it a little too far to the left
    Mock mockDiagram = mock(Diagram.class);
    mockOperations.expects(atLeastOnce()).method("getDiagram").
      will(returnValue(mockDiagram.proxy()));
    mockDiagram.expects(atLeastOnce()).method("getOrigin").
      will(returnValue(origin));
    mockDiagram.expects(atLeastOnce()).method("snap");
    selection.updatePosition(-10.0, 10.0);
    // and draw it, the selection is within the bounds
    Constraint[] params = {eq(0.0), eq(5.0), eq(320.0), eq(420.0), NULL};
    mockDrawingContext.expects(once()).method("drawRectangle").
      with(params);
    // Here we return the NullElement in order to drag without dropping
    // into a container
    mockDiagram.expects(atLeastOnce()).method("getChildAt")
      .will(returnValue(NullElement.getInstance()));
    selection.draw((DrawingContext) mockDrawingContext.proxy());    

    // move it a little too far to the top
    selection.updatePosition(15.0, -20.0);
    // and draw it, the selection is within the bounds
    Constraint[] params2 = {eq(0.0), eq(0.0), eq(320.0), eq(420.0), NULL};
    mockDrawingContext.expects(once()).method("drawRectangle").
      with(params2);
    selection.draw((DrawingContext) mockDrawingContext.proxy());    
  }

  // *************************************************************************
  // ***** Resizing from the SE
  // *****************************************
  
  /**
   * Sets the expectations for resize.
   */
  private void setResizeExpectations(Dimension2D minsize) {
    mockNode.expects(atLeastOnce()).method("getMinimumSize").
      will(returnValue(minsize)); 
    mockOperations.expects(once()).method("getDiagram")
      .will(returnValue(mockDiagram.proxy()));
    mockDiagram.expects(once()).method("snap");
  }

  private void setFullResizeExpectations(Dimension2D minsize) {
    setResizeExpectations(minsize);
    mockNode.expects(atLeastOnce()).method("getParent")
      .will(returnValue(mockParent.proxy()));
    mockParent.expects(once()).method("getAbsoluteX1").will(returnValue(-10.0));
    mockParent.expects(once()).method("getAbsoluteY1").will(returnValue(-10.0));
    mockNode.expects(once()).method("getAbsoluteX2")
      .will(returnValue(x + width));
    mockNode.expects(once()).method("getAbsoluteY2")
      .will(returnValue(y + height));
    mockParent.expects(once()).method("getAbsoluteX2")
      .will(returnValue(380.0));
    mockParent.expects(once()).method("getAbsoluteY2")
      .will(returnValue(460.0));
  }

  /**
   * Simulates a dragging operation for resizing.
   */
  public void testDragResizeSe() {
    Dimension2D minimumSize = new DoubleDimension(10, 10);
    // start dragging somewhere in the SE handle (322+, 425+)
    selection.startDragging(324.0, 427.0);
    assertTrue(selection.isDragging());
    // make it a little bigger
    setFullResizeExpectations(minimumSize);
    
    selection.updatePosition(330.0, 450.0);
    // and draw it
    // don't forget that sizes are truncated to the local system'
    Constraint[] params = {eq(2.0), eq(5.0), eq(326.0), eq(443.0), NULL};
    mockDrawingContext.expects(once()).method("drawRectangle").
      with(params);
    selection.draw((DrawingContext) mockDrawingContext.proxy());
    // stop dragging
    mockOperations.expects(once()).method("resizeElement").
      with(eq(selection.getElement()), eq(new Point2D.Double(2.0, 5.0)),
           eq(new DoubleDimension(326.0, 443.0)));
    selection.stopDragging(330.0, 450.0);
    assertFalse(selection.isDragging());
  }

  /**
   * Simulates a dragging operation for resizing with truncation.
   */
  public void testDragResizeSeWithTruncation() {
    // At SE resize, the position is < 22 and 25
    Dimension2D minimumSize = new DoubleDimension(20, 20);
    // start dragging somewhere in the SE handle (322+, 425+)
    selection.startDragging(324.0, 427.0);
    assertTrue(selection.isDragging());
    // make it a little smaller
    setResizeExpectations(minimumSize);
    selection.updatePosition(21.0, 23.0);
    // and draw it
    // don't forget that sizes are truncated to the local system'
    Constraint[] params = {eq(2.0), eq(5.0), eq(20.0), eq(20.0), NULL};
    mockDrawingContext.expects(once()).method("drawRectangle").
      with(params);
    selection.draw((DrawingContext) mockDrawingContext.proxy());
  }

  // *************************************************************************
  // ***** Resizing from the NW
  // *****************************************
  /**
   * Simulates a dragging operation for resizing on the NW corner.
   */
  public void testDragResizeNw() {
    Point2D origin = new Point2D.Double(0, 0);
    Dimension2D minimumSize = new DoubleDimension(20, 20);
    // start dragging somewhere in the NW handle (2-, 5-)
    selection.startDragging(1, 3);
    assertTrue(selection.isDragging());
    // make it a little bigger
    setFullResizeExpectations(minimumSize);
    
    selection.updatePosition(-10.0, -10.0);
    // and draw it
    // don't forget that sizes are truncated to the local system'
    Constraint[] params = {eq(-9.0), eq(-8.0), eq(331.0), eq(433.0), NULL};
    mockDrawingContext.expects(once()).method("drawRectangle").
      with(params);
    selection.draw((DrawingContext) mockDrawingContext.proxy());
    // stop dragging
    mockOperations.expects(once()).method("resizeElement").
      with(eq(selection.getElement()), eq(new Point2D.Double(-9.0, -8.0)),
           eq(new DoubleDimension(331.0, 433.0)));
    selection.stopDragging(-10.0, -10.0);
    assertFalse(selection.isDragging());
  }

  /**
   * Simulates a dragging operation for resizing with truncation, NW corner.
   */
  public void testDragResizeNwWithTruncation() {
    // At NW resize, the position is > (322 - 20) and (425 - 20)
    Dimension2D minimumSize = new DoubleDimension(20, 20);
    // start dragging somewhere in the NW handle (2-, 5-)
    selection.startDragging(1.0, 3.0);
    assertTrue(selection.isDragging());
    // make it a little smaller, dragging it to the lower right
    setResizeExpectations(minimumSize);
    selection.updatePosition(308.0, 410.0);
    // and draw it
    // don't forget that sizes are truncated to the local system'
    Constraint[] params = {eq(302.0), eq(405.0), eq(20.0), eq(20.0), NULL};
    mockDrawingContext.expects(once()).method("drawRectangle").
      with(params);
    selection.draw((DrawingContext) mockDrawingContext.proxy());
  }

  // *************************************************************************
  // ***** Resizing from the NE
  // *****************************************
  /**
   * Simulates a dragging operation for resizing on the NE corner.
   */
  public void testDragResizeNe() {
    Point2D origin = new Point2D.Double(0, 0);
    Dimension2D minimumSize = new DoubleDimension(20, 20);
    // start dragging somewhere in the NW handle (322+, 5-)
    selection.startDragging(324, 3);
    assertTrue(selection.isDragging());
    // make it a little bigger
    setFullResizeExpectations(minimumSize);
    mockNode.expects(once()).method("getParent")
      .will(returnValue(mockParent.proxy()));
    
    selection.updatePosition(340.0, -10.0);
    // and draw it
    // don't forget that sizes are truncated to the local system'
    Constraint[] params = {eq(2.0), eq(-8.0), eq(336.0), eq(433.0), NULL};
    mockDrawingContext.expects(once()).method("drawRectangle").
      with(params);
    selection.draw((DrawingContext) mockDrawingContext.proxy());
    // stop dragging
    mockOperations.expects(once()).method("resizeElement").
      with(eq(selection.getElement()), eq(new Point2D.Double(2.0, -8.0)),
           eq(new DoubleDimension(336.0, 433.0)));
    selection.stopDragging(340.0, -10.0);
    assertFalse(selection.isDragging());
  }

  /**
   * Simulates a dragging operation for resizing with truncation, NW corner.
   */
  public void testDragResizeNeWithTruncation() {
    // At NE resize, the position is < 22 and > (425 - 20)
    Dimension2D minimumSize = new DoubleDimension(20, 20);
    // start dragging somewhere in the NW handle (2-, 5-)
    selection.startDragging(324, 3);
    assertTrue(selection.isDragging());
    // make it a little smaller, dragging it to the lower left
    setResizeExpectations(minimumSize);
    selection.updatePosition(18.0, 410.0);
    // and draw it
    // don't forget that sizes are truncated to the local system'
    Constraint[] params = {eq(2.0), eq(405.0), eq(20.0), eq(20.0), NULL};
    mockDrawingContext.expects(once()).method("drawRectangle").
      with(params);
    selection.draw((DrawingContext) mockDrawingContext.proxy());
  }

  // *************************************************************************
  // ***** Resizing from the SW
  // *****************************************

  /**
   * Simulates a dragging operation for resizing on the SW corner.
   */
  public void testDragResizeSw() {
    Point2D origin = new Point2D.Double(0, 0);
    Dimension2D minimumSize = new DoubleDimension(20, 20);
    // start dragging somewhere in the SW handle (2-, 425+)
    selection.startDragging(1, 427);
    assertTrue(selection.isDragging());
    // make it a little bigger
    setFullResizeExpectations(minimumSize);

    selection.updatePosition(-10.0, 440.0);
    // and draw it
    // don't forget that sizes are truncated to the local system'
    Constraint[] params = {eq(-9.0), eq(5.0), eq(331.0), eq(433.0), NULL};
    mockDrawingContext.expects(once()).method("drawRectangle").
      with(params);
    selection.draw((DrawingContext) mockDrawingContext.proxy());
    // stop dragging
    mockOperations.expects(once()).method("resizeElement").
      with(eq(selection.getElement()), eq(new Point2D.Double(-9.0, 5.0)),
           eq(new DoubleDimension(331.0, 433.0)));
    selection.stopDragging(-10.0, 440.0);
    assertFalse(selection.isDragging());
  }

  /**
   * Simulates a dragging operation for resizing with truncation, SW corner.
   */
  public void testDragResizeSwWithTruncation() {
    // At SW resize, the position is x > (322 - 20) and y < 22
    Dimension2D minimumSize = new DoubleDimension(20, 20);
    // start dragging somewhere in the SW handle (2-, 425+)
    selection.startDragging(1, 427);
    assertTrue(selection.isDragging());
    // make it a little smaller, dragging it to the lower left
    setResizeExpectations(minimumSize);
    selection.updatePosition(310.0, 15);
    // and draw it
    // don't forget that sizes are truncated to the local system'
    Constraint[] params = {eq(302.0), eq(5.0), eq(20.0), eq(20.0), NULL};
    mockDrawingContext.expects(once()).method("drawRectangle").
      with(params);
    selection.draw((DrawingContext) mockDrawingContext.proxy());
  }
}
