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
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import org.jmock.Mock;
import org.jmock.cglib.MockObjectTestCase;
import org.tinyuml.draw.CompositeNode;
import org.tinyuml.draw.DiagramElement;
import org.tinyuml.draw.DrawingContext;
import org.tinyuml.draw.NullElement;
import org.tinyuml.draw.RubberbandSelector;

/**
 * A test class for RubberbandSelector.
 * @author Wei-ju Wu
 * @version 1.0
 */
public class RubberbandSelectorTest extends MockObjectTestCase {
  private Mock mockDrawingContext = mock(DrawingContext.class);
  private RubberbandSelector selector = new RubberbandSelector();
  private Mock mockCompositeNode = mock(CompositeNode.class);
  
  /**
   * Tests initial state.
   */
  public void testInitial() {
    selector.setDiagram((CompositeNode) mockCompositeNode.proxy());
    assertFalse(selector.isDragging());
    // no ops
    selector.updateDimensions();
    assertFalse(selector.contains(1, 2));
    assertEquals(Cursor.getDefaultCursor(),
      selector.getCursorForPosition(100.0, 120.0));
    assertEquals(NullElement.getInstance(), selector.getElement());
    assertEquals(0, selector.getElements().size());
  }
  
  /**
   * Tests a complete drag.
   */
  public void testCompleteDrag() {
    selector.setDiagram((CompositeNode) mockCompositeNode.proxy());
    selector.startDragging(10.0, 20.0);
    assertTrue(selector.isDragging());
    selector.updatePosition(30.0, 50.0);
    mockDrawingContext.expects(once()).method("drawRubberband")
      .with(eq(10.0), eq(20.0), eq(20.0), eq(30.0));
    selector.draw((DrawingContext) mockDrawingContext.proxy());
    Rectangle2D bounds = new Rectangle2D.Double(10.0, 20.0, 20.0, 30.0);
    List<DiagramElement> elements = new ArrayList<DiagramElement>();
    mockCompositeNode.expects(once()).method("getChildrenInSpecifiedBounds")
      .with(eq(bounds), eq(elements));
    selector.stopDragging(30.0, 50.0);
    assertEquals(elements, selector.getSelectedElements());
    assertFalse(selector.isDragging());
  }
  
  /**
   * Tests a cancelled drag.
   */
  public void testCancelDrag() {
    selector.startDragging(10.0, 20.0);
    assertTrue(selector.isDragging());
    selector.cancelDragging();
    assertFalse(selector.isDragging());
  }
}
