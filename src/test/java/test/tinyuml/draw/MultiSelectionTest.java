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
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import org.jmock.core.Constraint;
import org.tinyuml.draw.Connection;
import org.tinyuml.draw.Diagram;
import org.tinyuml.draw.DiagramElement;
import org.tinyuml.draw.DiagramOperations;
import org.tinyuml.draw.DrawingContext;
import org.tinyuml.draw.Selection;
import org.tinyuml.draw.MultiSelection;
import org.tinyuml.draw.Node;

/**
 * Tests the MultiSelection class.
 * Note: We need tests to make sure that multi-selections are only moved if
 * the nodes connected to the contained selected connections are also in the
 * selection. Otherwise, this could lead to strange effects.
 * @author Wei-ju Wu
 * @version 1.0
 */
public class MultiSelectionTest extends MockObjectTestCase {

  private MultiSelection selection;
  private Mock mockOperations = mock(DiagramOperations.class);
  private Mock mockElem1 = mock(Node.class), mockElem2 = mock(Node.class),
    mockConn = mock(Connection.class);
  private Mock mockSelection1 = mock(Selection.class),
    mockSelection2 = mock(Selection.class),
    mockSelection3 = mock(Selection.class);
  private Mock mockDrawingContext = mock(DrawingContext.class);
  
  /**
   * {@inheritDoc}
   */
  @Override
  protected void setUp() {
    List<DiagramElement> elements = new ArrayList<DiagramElement>();
    elements.add((Node) mockElem1.proxy());
    elements.add((Node) mockElem2.proxy());
    elements.add((DiagramElement) mockConn.proxy());
    
    mockElem1.expects(once()).method("getSelection")
      .will(returnValue(mockSelection1.proxy()));
    mockElem2.expects(once()).method("getSelection")
      .will(returnValue(mockSelection2.proxy()));
    mockConn.expects(once()).method("getSelection")
      .will(returnValue(mockSelection3.proxy()));
    selection = new MultiSelection((DiagramOperations) mockOperations.proxy(),
      elements);
  }
  
  /**
   * Tests the setup state.
   */
  public void testInitial() {
    assertEquals(3, selection.getElements().size());
    assertNull(selection.getElement());
  }
  
  /**
   * Tests the draw() method.
   */
  public void testDraw() {
    mockSelection1.expects(once()).method("draw")
      .with(eq(mockDrawingContext.proxy()));
    mockSelection2.expects(once()).method("draw")
      .with(eq(mockDrawingContext.proxy()));
    mockSelection3.expects(once()).method("draw")
      .with(eq(mockDrawingContext.proxy()));
    selection.draw((DrawingContext) mockDrawingContext.proxy());
  }
  
  /**
   * Tests the contains() method.
   */
  public void testContains() {
    mockSelection1.expects(once()).method("contains").will(returnValue(false));
    mockSelection2.expects(once()).method("contains").will(returnValue(false));
    mockSelection3.expects(once()).method("contains").will(returnValue(false));
    assertFalse(selection.contains(2.0, 3.0));
    
    mockSelection1.expects(once()).method("contains").will(returnValue(false));
    mockSelection2.expects(once()).method("contains").will(returnValue(true));
    assertTrue(selection.contains(2.0, 3.0));
  }
  
  /**
   * Tests the getCursorForPosition() method.
   */
  public void testGetCursorForPosition() {
    assertEquals(Cursor.getDefaultCursor(),
      selection.getCursorForPosition(1.0, 2.0));
  }
  
  /**
   * Tests a full drag.
   */
  public void testFullDrag() {
    mockElem1.expects(atLeastOnce()).method("getAbsoluteBounds")
      .will(returnValue(new Rectangle2D.Double(1.0, 1.0, 20.0, 20.0)));
    mockElem2.expects(atLeastOnce()).method("getAbsoluteBounds")
      .will(returnValue(new Rectangle2D.Double(40.0, 50.0, 20.0, 20.0)));
    mockConn.expects(atLeastOnce()).method("getAbsoluteBounds")
      .will(returnValue(new Rectangle2D.Double(10.0, 20.0, 20.0, 50.0)));
    
    selection.startDragging(1.0, 2.0);    
    Mock mockDiagram = mock(Diagram.class);
    mockOperations.expects(atLeastOnce()).method("getDiagram")
      .will(returnValue(mockDiagram.proxy()));
    mockDiagram.expects(atLeastOnce()).method("getOrigin")
      .will(returnValue(new Point2D.Double(0, 0)));
    mockDiagram.expects(atLeastOnce()).method("snap");
    selection.updatePosition(10.0, 20.0);
    
    Constraint[] drawParams = {
      eq(10.0), eq(19.0), eq(59.0), eq(69.0), NULL
    };
    mockDrawingContext.expects(once()).method("drawRectangle").with(drawParams);
    selection.draw((DrawingContext) mockDrawingContext.proxy());
    assertTrue(selection.isDragging());
    
    mockSelection1.expects(once()).method("updateDimensions");
    mockSelection2.expects(once()).method("updateDimensions");
    mockSelection3.expects(once()).method("updateDimensions");
    Collection<DiagramElement> nodesOnly = new ArrayList<DiagramElement>();
    nodesOnly.add((DiagramElement) mockElem1.proxy());
    nodesOnly.add((DiagramElement) mockElem2.proxy());
    
    mockElem1.expects(atLeastOnce()).method("getAbsoluteX1")
      .will(returnValue(10.0));
    mockElem1.expects(atLeastOnce()).method("getAbsoluteY1")
      .will(returnValue(10.0));
    mockElem1.expects(atLeastOnce()).method("getParent")
      .will(returnValue(mockDiagram.proxy()));
    mockElem2.expects(atLeastOnce()).method("getAbsoluteX1")
      .will(returnValue(10.0));
    mockElem2.expects(atLeastOnce()).method("getAbsoluteY1")
      .will(returnValue(10.0));
    mockElem2.expects(atLeastOnce()).method("getParent")
      .will(returnValue(mockDiagram.proxy()));
    mockOperations.expects(once()).method("moveElements");
    selection.stopDragging(15.0, 25.0);
    assertFalse(selection.isDragging());
  }

  /**
   * Tests a cancelled drag.
   */
  public void testCancelledDrag() {
    mockElem1.expects(atLeastOnce()).method("getAbsoluteBounds")
      .will(returnValue(new Rectangle2D.Double(1.0, 1.0, 20.0, 20.0)));
    mockElem2.expects(atLeastOnce()).method("getAbsoluteBounds")
      .will(returnValue(new Rectangle2D.Double(40.0, 50.0, 20.0, 20.0)));
    mockConn.expects(atLeastOnce()).method("getAbsoluteBounds")
      .will(returnValue(new Rectangle2D.Double(120.0, 150.0, 20.0, 20.0)));

    selection.startDragging(1.0, 2.0);
    assertTrue(selection.isDragging());
    selection.cancelDragging();
    assertFalse(selection.isDragging());
  }
}
