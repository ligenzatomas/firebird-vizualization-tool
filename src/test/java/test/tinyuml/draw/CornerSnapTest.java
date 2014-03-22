/**
 * $Id$
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
import org.jmock.Mock;
import org.jmock.cglib.MockObjectTestCase;
import org.tinyuml.draw.CornerSnap;
import org.tinyuml.draw.DoubleDimension;
import org.tinyuml.draw.Snapping;

/**
 *
 * @author Wei-ju Wu
 * @version 1.0
 */
public class CornerSnapTest extends MockObjectTestCase {
  
  private Mock mockSnapping = mock(Snapping.class);
  private Point2D pos = new Point2D.Double(10.0, 20.0);
  private Dimension2D size = new DoubleDimension(30, 40);
  private CornerSnap cornersnap = new CornerSnap(
    (Snapping) mockSnapping.proxy(), pos, size);

  /**
   * Tests snapping in the right lower corner.
   */
  public void testSnapRightLower() {
    Point2D rightlower = new Point2D.Double(40.0, 60.0);
    mockSnapping.expects(atLeastOnce()).method("snap").with(eq(rightlower));
    cornersnap.snapRightLower();
  }

  /**
   * Tests snapping in the right upper corner.
   */
  public void testSnapRightUpper() {
    Point2D rightupper = new Point2D.Double(40.0, 20.0);
    mockSnapping.expects(atLeastOnce()).method("snap").with(eq(rightupper));
    cornersnap.snapRightUpper();
  }
  
  /**
   * Tests snapping in the left upper corner.
   */
  public void testSnapLeftUpper() {
    Point2D leftupper = new Point2D.Double(10.0, 20.0);
    mockSnapping.expects(atLeastOnce()).method("snap").with(eq(leftupper));
    cornersnap.snapLeftUpper();
  }

  /**
   * Tests snapping in the left lower corner.
   */
  public void testSnapLeftLower() {
    Point2D leftlower = new Point2D.Double(10.0, 60.0);
    mockSnapping.expects(atLeastOnce()).method("snap").with(eq(leftlower));
    cornersnap.snapLeftLower();
  }
}
