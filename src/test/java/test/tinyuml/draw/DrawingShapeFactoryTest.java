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

import java.awt.geom.Dimension2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import junit.framework.*;
import org.tinyuml.draw.DoubleDimension;
import org.tinyuml.draw.DrawingShapeFactory;

/**
 *
 * @author Wei-ju Wu
 * @version 1.0
 */
public class DrawingShapeFactoryTest extends TestCase {

  DrawingShapeFactory factory = DrawingShapeFactory.getInstance();
  
  /**
   * Tests the createRect2d() methods.
   */
  public void testCreateRect2d() {

    // Create Rectangle 1
    Point2D p0 = new Point2D.Double(1.0, 3.0);
    Dimension2D size = new DoubleDimension(4, 5);

    Rectangle2D rect = factory.createRect2d(p0, size);
    assertEquals(1.0, rect.getX());
    assertEquals(3.0, rect.getY());
    assertEquals(4.0, rect.getWidth());
    assertEquals(5.0, rect.getHeight());

    // Create Rectangle 2
    rect = factory.createRect2d(1, 2, 3, 4);
    assertEquals(1.0, rect.getX());
    assertEquals(2.0, rect.getY());
    assertEquals(3.0, rect.getWidth());
    assertEquals(4.0, rect.getHeight());
    
    // Create Rectangle 3
    rect = factory.createRect2d(p0, 5, 9);
    assertEquals(1.0, rect.getX());
    assertEquals(3.0, rect.getY());
    assertEquals(5.0, rect.getWidth());
    assertEquals(9.0, rect.getHeight());
  }
  
  /**
   * Tests the createLine2d() methods.
   */
  public void testCreateLine2d() {

    Point2D p0 = new Point2D.Double(1.0, 3.0);
    Point2D p1 = new Point2D.Double(11.0, 23.0);
    Line2D line = factory.createLine2d(p0, p1);
    assertEquals(1.0, line.getX1());
    assertEquals(3.0, line.getY1());
    assertEquals(11.0, line.getX2());
    assertEquals(23.0, line.getY2());

    line = factory.createLine2d(1, 2, 3, 4);
    assertEquals(1.0, line.getX1());
    assertEquals(2.0, line.getY1());
    assertEquals(3.0, line.getX2());
    assertEquals(4.0, line.getY2());
  }

  /**
   * Tests valid Stroke creation, stroke appearance is not tested.
   */
  public void testCreateStrokes() {
    assertNotNull(factory.createDiagramBorderStroke());
    assertNotNull(factory.createGridStroke());
    assertNotNull(factory.getStandardStroke());
    assertNotNull(factory.getDashedStroke());
  }
}
