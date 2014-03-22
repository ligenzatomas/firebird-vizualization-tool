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

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import junit.framework.*;
import org.tinyuml.draw.GeometryUtil;
import org.tinyuml.draw.GeometryUtil.Orientation;

/**
 * A test for GeometryUtil methods.
 * @author Wei-ju Wu
 * @version 1.0
 */
public class GeometryUtilTest extends TestCase {
  
  private static final double EPS = 0.001;
  
  /**
   * Tests the outcode method.
   */
  public void testOutcode() {
    Point2D pInexact = new Point2D.Double(), pExact = new Point2D.Double();
    Rectangle2D rect = new Rectangle2D.Double(0, 0, 10, 10);
    pExact.setLocation(10, 5);
    pInexact.setLocation(10.00001, 5.00001);
    assertEquals("regular outcode() says outside", 4, rect.outcode(pInexact));
    assertEquals("custom outcode() should say inside", 0,
      GeometryUtil.getInstance().outcode(rect, pInexact));
  }

  /**
   * Tests the line intersection method.
   */
  public void testComputeIntersection() {
    Line2D line0 = new Line2D.Double();
    Line2D line1 = new Line2D.Double();
    Point2D intersection = new Point2D.Double();
    line0.setLine(0, 50, 100, 50);
    line1.setLine(50, 0, 50, 100);
    
    GeometryUtil.getInstance().computeLineIntersection(line0, line1,
      intersection);
    assertTrue(Math.abs(50 - intersection.getX()) < EPS);
    assertTrue(Math.abs(50 - intersection.getY()) < EPS);
  }

  /**
   * Tests the inverse intersection from above.
   */
  public void testComputeIntersectionInverse() {
    Line2D line0 = new Line2D.Double();
    Line2D line1 = new Line2D.Double();
    Point2D intersection = new Point2D.Double();
    line0.setLine(50, 0, 50, 100);
    line1.setLine(0, 50, 100, 50);
    
    GeometryUtil.getInstance().computeLineIntersection(line0, line1,
      intersection);
    assertTrue(Math.abs(50 - intersection.getX()) < EPS);
    assertTrue(Math.abs(50 - intersection.getY()) < EPS);    
  }

  /**
   * Tests if the specified ranges overlap.
   */
  public void testRangesOverlap() {
    GeometryUtil util = GeometryUtil.getInstance();
    // disjoint
    assertFalse(util.rangesOverlap(1, 10, 20, 25));
    // r1 == r2
    assertTrue(util.rangesOverlap(1, 10, 1, 10));
    // r1 in r2
    assertTrue(util.rangesOverlap(12, 20, 10, 25));
    // r2 in r1
    assertTrue(util.rangesOverlap(50, 100, 75, 80));
    // r1 and r2 have overlapping region
    assertTrue(util.rangesOverlap(30, 45, 40, 70));
  }
  
  /**
   * Tests the equals() method.
   */
  public void testDoubleEquals() {
    assertFalse(GeometryUtil.getInstance().equals(20.0, 20.3));
    assertTrue(GeometryUtil.getInstance().equals(20.0, 20.000001));
  }
  
  /**
   * Tests the getOrientation() method.
   */
  public void testGetOrientation() {
    Point2D p1 = new Point2D.Double(), p2 = new Point2D.Double();
    GeometryUtil util = GeometryUtil.getInstance();
    p1.setLocation(10, 10);
    p2.setLocation(100, 10);
    assertEquals(Orientation.HORIZONTAL, util.getSegmentOrientation(p1, p2));
    p1.setLocation(10, 10);
    p2.setLocation(10, 100);
    assertEquals(Orientation.VERTICAL, util.getSegmentOrientation(p1, p2));
    p1.setLocation(10, 10);
    p2.setLocation(10, 10);
    assertEquals(Orientation.UNDEFINED, util.getSegmentOrientation(p1, p2));
    p1.setLocation(10, 10);
    p2.setLocation(20, 20);
    assertEquals(Orientation.UNDEFINED, util.getSegmentOrientation(p1, p2));
  }
}
