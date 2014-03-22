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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import org.jmock.Mock;
import org.jmock.cglib.MockObjectTestCase;
import org.tinyuml.draw.DrawingContext.FontType;
import org.tinyuml.draw.DrawingContextImpl;

/**
 * A test class for DrawingContextImpl class.
 * @author Wei-ju Wu
 * @version 1.0
 */
public class DrawingContextImplTest extends MockObjectTestCase {

  private DrawingContextImpl drawingContext = new DrawingContextImpl();
  private Mock mockG2d = mock(Graphics2D.class);
  private Rectangle bounds = new Rectangle(0, 0, 1000, 1000);
  
  /**
   * {@inheritDoc}
   */
  @Override
  protected void setUp() throws Exception {
    drawingContext.setGraphics2D((Graphics2D) mockG2d.proxy(), bounds);
  }
  
  /**
   * Tests initial state and delegation.
   */
  public void testInitialAndDelegation() {
    assertEquals(mockG2d.proxy(), drawingContext.getGraphics2D());
    assertEquals(bounds, drawingContext.getClipBounds());
  }
  
  /**
   * Tests the font methods.
   */
  public void testFontSpecific() {
    assertNotNull(drawingContext.getFont(FontType.DEFAULT));
    assertNotNull(drawingContext.getFont(FontType.ABSTRACT_ELEMENT));
    assertNotNull(drawingContext.getFont(FontType.ELEMENT_NAME));
    mockG2d.expects(once()).method("getFontMetrics").will(returnValue(null));
    assertNull(drawingContext.getFontMetrics(FontType.DEFAULT));
  }
  
  /**
   * Tests the drawLabel() method.
   */
  public void testDrawLabel() {
    mockG2d.expects(once()).method("setFont");
    mockG2d.expects(once()).method("drawString");
    drawingContext.drawLabel("text", 1, 2, FontType.DEFAULT);
  }
  
  /**
   * Tests the drawDashedLine() method.
   */
  public void testDrawDashedLine() {
    mockG2d.expects(once()).method("setStroke");
    mockG2d.expects(once()).method("setColor").with(eq(Color.BLACK));
    mockG2d.expects(once()).method("draw");
    drawingContext.drawDashedLine(0, 1, 100, 101);
  }
  
  /**
   * Tests the drawGridLine() method.
   */
  public void testDrawGridLne() {
    mockG2d.expects(once()).method("setStroke");
    mockG2d.expects(once()).method("setColor");
    mockG2d.expects(once()).method("draw");
    drawingContext.drawGridLine(0, 1, 100, 101);
  }
  
  /**
   * Tests the drawLine() method.
   */
  public void testDrawLine() {
    mockG2d.expects(once()).method("setStroke");
    mockG2d.expects(once()).method("setColor").with(eq(Color.BLACK));
    mockG2d.expects(once()).method("draw");
    drawingContext.drawLine(0, 1, 100, 101);
  }
  
  /**
   * Tests the drawRectangle() method.
   */
  public void testDrawRectangle() {
    // general
    
    mockG2d.expects(once()).method("setStroke");
    mockG2d.expects(once()).method("setColor").with(eq(Color.BLUE));
    mockG2d.expects(once()).method("fill");
    mockG2d.expects(once()).method("setColor").with(eq(Color.RED));
    mockG2d.expects(once()).method("draw");
    drawingContext.drawRectangle(0, 1, 100, 101, Color.RED, Color.BLUE);
     
    // unfilled
    mockG2d.expects(once()).method("setStroke");
    mockG2d.expects(once()).method("setColor").with(eq(Color.BLACK));
    mockG2d.expects(once()).method("draw");
    drawingContext.drawRectangle(0, 1, 100, 101, null);

    // filled
    mockG2d.expects(once()).method("setStroke");
    mockG2d.expects(once()).method("setColor").with(eq(Color.BLACK));
    mockG2d.expects(once()).method("setColor").with(eq(Color.WHITE));
    mockG2d.expects(once()).method("draw");
    mockG2d.expects(once()).method("fill");
    drawingContext.drawRectangle(0, 1, 100, 101, Color.WHITE);
  }

  /**
   * Tests the fillRectangle() method.
   */
  public void testFillRectangle() {
    mockG2d.expects(once()).method("setColor").with(eq(Color.WHITE));
    mockG2d.expects(once()).method("fill");
    drawingContext.fillRectangle(0, 1, 100, 101, Color.WHITE);
  }

  /**
   * Tests the drawRubberband() method.
   */
  public void testDrawRubberband() {
    mockG2d.expects(once()).method("setStroke");
    mockG2d.expects(once()).method("setColor");
    mockG2d.expects(once()).method("draw");
    drawingContext.drawRubberband(0, 1, 100, 101);
  }

  /**
   * Tests the draw() method with a shape and a fill color.
   */
  public void testDrawShape() {
    Mock mockShape = mock(Shape.class);
    mockG2d.expects(once()).method("setColor").with(eq(Color.WHITE));
    mockG2d.expects(once()).method("fill").with(eq(mockShape.proxy()));
    mockG2d.expects(once()).method("setColor").with(eq(Color.BLACK));
    mockG2d.expects(once()).method("setStroke");
    mockG2d.expects(once()).method("draw").with(eq(mockShape.proxy()));    
    drawingContext.draw((Shape) mockShape.proxy(), Color.WHITE);
  }
}
