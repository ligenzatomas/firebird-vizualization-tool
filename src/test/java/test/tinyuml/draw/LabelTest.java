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

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.geom.Dimension2D;
import org.jmock.Mock;
import org.jmock.cglib.MockObjectTestCase;
import org.tinyuml.draw.CompositeNode;
import org.tinyuml.draw.DiagramOperations;
import org.tinyuml.draw.DoubleDimension;
import org.tinyuml.draw.DrawingContext;
import org.tinyuml.draw.Label;
import org.tinyuml.draw.LabelSource;
import org.tinyuml.draw.NullSelection;
import org.tinyuml.draw.SimpleLabel;

/**
 * A test class for Label.
 * @author Wei-ju Wu
 * @version 1.0
 */
public class LabelTest extends MockObjectTestCase {
  // we need to mock out the FontMetrics class by ourselves, because it was
  // not designed for mocking
  static class MyFontMetrics extends FontMetrics {
    public MyFontMetrics() { super(Font.getFont("Dialog")); }
  }
  Mock mockFontMetrics = mock(MyFontMetrics.class);
  Mock mockLabelSource = mock(LabelSource.class);
  Mock mockDrawingContext = mock(DrawingContext.class);
  Mock mockParent = mock(CompositeNode.class);
  CompositeNode parent = (CompositeNode) mockParent.proxy();
  class MyLabel extends SimpleLabel {
    public void setValid(boolean flag) { super.setValid(flag); }
  }
  private MyLabel label = new MyLabel();

  /**
   * {@inheritDoc}
   */
  @Override
  protected void setUp() {
    label.setSource((LabelSource) mockLabelSource.proxy());
    label.setParent(parent);
  }

  /**
   * Tests initial state.
   */
  public void testInitial() {
    assertFalse(label.isValid());
    assertEquals(mockLabelSource.proxy(), label.getSource());
    // no effect
    Mock mockOperations = mock(DiagramOperations.class);
    assertEquals(NullSelection.getInstance(),
      label.getSelection((DiagramOperations) mockOperations.proxy()));
  }
  
  /**
   * Tests the getLabelAt() method.
   */
  public void testGetLabelAt() {
    Label aLabel1 = new SimpleLabel() {
      public boolean contains(double x, double y) { return true; }
    };
    assertEquals(aLabel1, aLabel1.getLabelAt(2.0, 3.0));

    Label aLabel2 = new SimpleLabel() {
      public boolean contains(double x, double y) { return false; }
    };
    assertNull(aLabel2.getLabelAt(2.0, 3.0));
  }
  
  /**
   * Tests the label text property.
   */
  public void testLabelText() {
    mockLabelSource.expects(once()).method("getLabelText").
      will(returnValue("labeltext"));
    assertEquals("labeltext", label.getText());    

    mockLabelSource.expects(atLeastOnce()).method("getLabelText").
      will(returnValue("labeltext"));
    assertEquals("labeltext", label.getText());
    mockLabelSource.expects(atLeastOnce()).method("setLabelText").
      with(eq("anotherlabeltext"));
    label.setText("anotherlabeltext");
  }
  
  /**
   * Tests the valid property.
   */
  public void testValid() {
    label.setValid(true);
    assertTrue(label.isValid());
    label.invalidate();
    assertFalse(label.isValid());
  }
  
  /**
   * Tests the draw method.
   */
  public void testDraw() {
    String labeltext = "text";
    double x = 1, y = 2, w = 3, h = 4;
    mockLabelSource.expects(atLeastOnce()).method("getLabelText").
      will(returnValue(labeltext));
    mockParent.expects(once()).method("getAbsoluteX1").will(returnValue(x));
    mockParent.expects(once()).method("getAbsoluteY1").will(returnValue(y));
    mockDrawingContext.expects(atLeastOnce()).method("getFontMetrics").
      will(returnValue(mockFontMetrics.proxy()));
    mockDrawingContext.expects(once()).method("drawLabel");
    mockFontMetrics.expects(once()).method("getMaxAscent").will(returnValue(5));
    mockFontMetrics.expects(once()).method("stringWidth")
      .will(returnValue(20));
    mockFontMetrics.expects(once()).method("getHeight")
      .will(returnValue(10));
    
    label.draw((DrawingContext) mockDrawingContext.proxy());
  }

  /**
   * Tests the recalculateSize() method.
   */
  public void testCalculateSize() {
    final int width = 50;
    final int height = 10;
    mockFontMetrics.expects(once()).method("stringWidth")
      .will(returnValue(width));
    mockFontMetrics.expects(once()).method("getHeight")
      .will(returnValue(height));
    String labeltext = "text";
    mockLabelSource.expects(atLeastOnce()).method("getLabelText").
      will(returnValue(labeltext));
    mockDrawingContext.expects(once()).method("getFontMetrics").
      will(returnValue(mockFontMetrics.proxy()));
    label.recalculateSize((DrawingContext) mockDrawingContext.proxy());
    assertEquals((double) width, label.getSize().getWidth());
    assertEquals((double) height, label.getSize().getHeight());
  }
  
  /**
   * Tests the centerHorizontally() method.
   */
  public void testCenterHorizontally() {
    Dimension2D size = new DoubleDimension(100, 30);
    label.setSize(40, 10);
    label.setOrigin(0, 12);
    mockParent.expects(once()).method("getSize").will(returnValue(size));
    label.centerHorizontally();
    assertEquals(12.0, label.getOrigin().getY());
    assertEquals(30.0, label.getOrigin().getX());
  }
}
