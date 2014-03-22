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

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import org.jmock.Mock;
import org.jmock.cglib.MockObjectTestCase;
import org.tinyuml.draw.DrawingContext;
import org.tinyuml.draw.SimpleArrowTip;

/**
 * Test for SimpleArrowTip.
 * @author Wei-ju Wu
 * @version 1.0
 */
public class SimpleArrowTipTest extends MockObjectTestCase {

  private SimpleArrowTip arrowtip = new SimpleArrowTip();

  /**
   * Tests the draw() method.
   */
  public void testDraw() {
    Mock mockDrawingContext = mock(DrawingContext.class);
    Mock mockTransform = mock(AffineTransform.class);
    Point2D endpoint = new Point2D.Double(10.0, 10.0);
    mockTransform.expects(atLeastOnce()).method("transform");
    mockDrawingContext.expects(atLeastOnce()).method("drawLine");
    arrowtip.draw((DrawingContext) mockDrawingContext.proxy(), endpoint,
      (AffineTransform) mockTransform.proxy());
  }
}
