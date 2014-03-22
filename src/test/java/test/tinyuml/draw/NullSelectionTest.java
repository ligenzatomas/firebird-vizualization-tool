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
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import org.tinyuml.draw.DrawingContext;
import org.tinyuml.draw.NullElement;
import org.tinyuml.draw.NullSelection;
import org.tinyuml.draw.Selection;

/**
 * This class test the NullSelection class.
 *
 * @author Wei-ju Wu
 * @version 1.0
 */
public class NullSelectionTest extends MockObjectTestCase {

  private Mock mockDrawingContext = mock(DrawingContext.class);
  private DrawingContext drawingContext = (DrawingContext)
    mockDrawingContext.proxy();

  /**
   * Tests the methods of the NullSelection object.
   */
  public void testState() {
    double x = 1, y = 2;
    Selection selection = NullSelection.getInstance();
    assertEquals(NullElement.getInstance(), selection.getElement());
    assertEquals(0, selection.getElements().size());
    assertFalse(selection.isDragging());
    assertFalse(selection.contains(x, y));
    assertEquals(Cursor.getDefaultCursor(),
      selection.getCursorForPosition(x, y));
    // These methods have no effect, just check they don't throw
    // exceptions
    selection.startDragging(x, y);
    selection.stopDragging(x, y);
    selection.cancelDragging();
    selection.updatePosition(x, y);
    selection.draw(drawingContext);
    selection.updateDimensions();
  }
}
