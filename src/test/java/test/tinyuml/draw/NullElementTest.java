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
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import org.tinyuml.draw.DiagramOperations;
import org.tinyuml.draw.DrawingContext;
import org.tinyuml.draw.NullElement;
import org.tinyuml.draw.NullSelection;

/**
 * A test class for the NullShape class.
 * @author Wei-ju Wu
 * @version 1.0
 */
public class NullElementTest extends MockObjectTestCase {
  
  private Mock mockDrawingContext = mock(DrawingContext.class);
  private DrawingContext drawingContext = (DrawingContext)
    mockDrawingContext.proxy();
  private Mock mockOperations = mock(DiagramOperations.class);
  private DiagramOperations operations = (DiagramOperations)
    mockOperations.proxy();
    
  /**
   * Tests the methods of NullShape.
   */
  public void testMethods() {
    NullElement elem = NullElement.getInstance();
    elem.recalculateSize(drawingContext);
    elem.draw(drawingContext);
    assertFalse(elem.isVisible(new Rectangle(0, 0, 1000, 1000)));
    assertFalse(elem.isVisible(null));
    assertEquals(NullSelection.getInstance(), elem.getSelection(operations));
    assertNull(elem.getLabelAt(1, 2));
  }
}
