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

import java.awt.Cursor;
import org.jmock.Mock;
import org.jmock.cglib.MockObjectTestCase;
import org.tinyuml.draw.DrawingContext;
import org.tinyuml.draw.Selection;
import org.tinyuml.umldraw.shared.UmlConnection;
import org.tinyuml.umldraw.shared.UmlConnectionSelection;

/**
 *
 * @author Wei-ju Wu
 * @version 1.0
 */
public class UmlConnectionSelectionTest extends MockObjectTestCase {

  private Mock mockConnection = mock(UmlConnection.class);
  private Mock mockSelection = mock(Selection.class);
  private UmlConnectionSelection connselection;
  
  /**
   * {@inheritDoc}
   */
  @Override
  protected void setUp() {
    connselection = new UmlConnectionSelection(
      (UmlConnection) mockConnection.proxy(),
      (Selection) mockSelection.proxy());
  }
  
  /**
   * Tests the method that do not go to the wrapped selection.
   */
  public void testElementsNotWrapped() {
    assertEquals(mockConnection.proxy(), connselection.getElement());
    assertEquals(1, connselection.getElements().size());
    assertTrue(connselection.getElements().contains(mockConnection.proxy()));
  }
  
  /**
   * Tests the delegation to the wrapped selection.
   */
  public void testDelegation() {
    mockSelection.expects(once()).method("isDragging").will(returnValue(true));
    assertTrue(connselection.isDragging());
    mockSelection.expects(once()).method("startDragging")
      .with(eq(1.0), eq(3.0));
    connselection.startDragging(1.0, 3.0);
    mockSelection.expects(once()).method("stopDragging")
      .with(eq(70.0), eq(92.0));
    connselection.stopDragging(70.0, 92.0);
    mockSelection.expects(once()).method("cancelDragging");
    connselection.cancelDragging();
    mockSelection.expects(once()).method("updatePosition")
      .with(eq(70.0), eq(92.0));
    connselection.updatePosition(70.0, 92.0);
    mockSelection.expects(once()).method("contains")
      .with(eq(71.0), eq(93.0)).will(returnValue(true));
    assertTrue(connselection.contains(71.0, 93.0));
    Mock mockDrawingContext = mock(DrawingContext.class);
    mockSelection.expects(once()).method("draw")
      .with(eq(mockDrawingContext.proxy()));
    connselection.draw((DrawingContext) mockDrawingContext.proxy());
    
    mockSelection.expects(once()).method("getCursorForPosition")
      .with(eq(23.0), eq(24.0)).will(returnValue(Cursor.getDefaultCursor()));
    assertEquals(Cursor.getDefaultCursor(),
      connselection.getCursorForPosition(23.0, 24.0));
    mockSelection.expects(once()).method("updateDimensions");
    connselection.updateDimensions();
  }
}
