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
import java.awt.Rectangle;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import org.jmock.Mock;
import org.jmock.cglib.MockObjectTestCase;
import org.jmock.core.Constraint;
import org.tinyuml.draw.Compartment;
import org.tinyuml.draw.Compartment.Alignment;
import org.tinyuml.draw.CompositeNode;
import org.tinyuml.draw.DiagramElement;
import org.tinyuml.draw.DoubleDimension;
import org.tinyuml.draw.DrawingContext;
import org.tinyuml.draw.Label;
import org.tinyuml.draw.SimpleLabel;

/**
 *
 * @author Wei-ju Wu
 * @version 1.0
 */
public class CompartmentTest extends MockObjectTestCase {

  private static final double MIN_WIDTH = 80;
  private static final double MIN_HEIGHT = 10;
  private Mock mockParent = mock(CompositeNode.class);
  private Compartment compartment = new Compartment();
  private Mock mockChild = mock(DiagramElement.class);
  private Mock mockDrawingContext = mock(DrawingContext.class);
  private Mock mockLabel = mock(Label.class);

  /**
   * {@inheritDoc}
   */
  @Override
  protected void setUp() {
    compartment.setParent((CompositeNode) mockParent.proxy());
  }

  /**
   * Tests initial state.
   */
  public void testInitial() {
    assertEquals(MIN_WIDTH, compartment.getMinimumSize().getWidth());
    assertEquals(MIN_HEIGHT, compartment.getMinimumSize().getHeight());
    assertEquals(MIN_WIDTH, compartment.getSize().getWidth());
    assertEquals(MIN_HEIGHT, compartment.getSize().getHeight());
    assertFalse(compartment.isValid());
    assertEquals(Color.WHITE, compartment.getBackground());
  }
  
  /**
   * Tests the simple setters.
   */
  public void testSetters() {
    compartment.setBackground(Color.BLACK);
    assertEquals(Color.BLACK, compartment.getBackground());
  }

  /**
   * Tests the recalculateSize() method without labels.
   */
  public void testRecalculateSizeNoLabels() {
    mockParent.expects(once()).method("getSize")
      .will(returnValue(new DoubleDimension(10, 4)));
    compartment.recalculateSize((DrawingContext) mockDrawingContext.proxy());
    assertEquals(new DoubleDimension(MIN_WIDTH, MIN_HEIGHT),
      compartment.getSize());
    assertTrue(compartment.isValid());
  }

  /**
   * Parent is wider than the compartment, width will follow.
   */
  public void testRecalculateSizeNoLabelsLargerParent() {
    mockParent.expects(atLeastOnce()).method("getSize")
      .will(returnValue(new DoubleDimension(200, 80)));
    compartment.recalculateSize((DrawingContext) mockDrawingContext.proxy());
    assertEquals(new DoubleDimension(200, MIN_HEIGHT),
      compartment.getSize());    
    assertTrue(compartment.isValid());
  }

  // *************************************************************************
  // ***** Labels added
  // ****************************

  /**
   * The addLabel() expectations.
   */
  private void setAddLabelExpectations() {
    mockLabel.expects(once()).method("setParent").with(eq(compartment));    
  }

  /**
   * Adds a Label.
   */
  public void testAddLabel() {
    setAddLabelExpectations();
    compartment.addLabel((Label) mockLabel.proxy());
    // invalidate is delegated to the label
    mockLabel.expects(once()).method("invalidate");
    compartment.invalidate();

    mockLabel.expects(once()).method("contains")
      .with(eq(1.0), eq(2.0))
      .will(returnValue(true));
    assertNotNull(compartment.getLabelAt(1.0, 2.0));

    mockLabel.expects(once()).method("contains")
      .with(eq(11.0), eq(21.0))
      .will(returnValue(false));
    assertNull(compartment.getLabelAt(11.0, 21.0));
    mockLabel.expects(once()).method("isValid")
      .will(returnValue(true));
    assertFalse(compartment.isValid());
  }

  /**
   * Sets the expections for recalculateSize() with one Label.
   */
  private void setRecalculateSizeWithOneLabelExpectations(
    Dimension2D parentSize) {
    mockParent.expects(atLeastOnce()).method("getSize")
      .will(returnValue(parentSize));
    mockLabel.expects(once()).method("recalculateSize")
      .with(eq(mockDrawingContext.proxy()));
    mockLabel.expects(atLeastOnce()).method("getOrigin")
      .will(returnValue(new Point2D.Double(0.0, 0.0)));
    mockLabel.expects(atLeastOnce()).method("setOrigin")
      .with(eq(0.0), eq(5.0));
    mockLabel.expects(atLeastOnce()).method("getSize")
      .will(returnValue(new DoubleDimension(80.0, 12.0)));
  }

  /**
   * Resizing with one label, parent width is smaller than minimum width.
   */
  public void testRecalculateSizeWithLabelParentSmallerThanMinimum() {
    setAddLabelExpectations();
    compartment.addLabel((Label) mockLabel.proxy());
    
    // Sets a size larger than the minimum
    mockLabel.expects(once()).method("invalidate");
    compartment.setSize(120, 40);
    
    // parent width is smaller even than minimum
    setRecalculateSizeWithOneLabelExpectations(new DoubleDimension(10, 4));
    compartment.recalculateSize((DrawingContext) mockDrawingContext.proxy());
    assertEquals(120.0, compartment.getSize().getWidth());
  }

  /**
   * Resizing with one label, parent width is smaller than compartment width,
   * but larger than minimum width.
   */
  public void testRecalculateSizeWithLabelParentSmallerButLargerMinimum() {
    setAddLabelExpectations();
    compartment.addLabel((Label) mockLabel.proxy());
    
    // Sets a size larger than the minimum
    mockLabel.expects(atLeastOnce()).method("invalidate");
    compartment.setSize(120, 40);

    // parent width is smaller than compartment width, but larger than
    // minimum
    setRecalculateSizeWithOneLabelExpectations(new DoubleDimension(90, 4));
    compartment.recalculateSize((DrawingContext) mockDrawingContext.proxy());
    assertEquals(90.0, compartment.getSize().getWidth());
  }

  /**
   * Resizing with one label, parent width is greater than compartment width.
   */
  public void testRecalculateSizeWithLabelParentWidthGreater() {
    setAddLabelExpectations();
    compartment.addLabel((Label) mockLabel.proxy());
    
    // Sets a size larger than the minimum
    mockLabel.expects(atLeastOnce()).method("invalidate");
    compartment.setSize(120, 40);

    // parent width is greater than compartment width
    setRecalculateSizeWithOneLabelExpectations(new DoubleDimension(150, 4));
    compartment.recalculateSize((DrawingContext) mockDrawingContext.proxy());
    assertEquals(150.0, compartment.getSize().getWidth());
  }
  
  /**
   * Sets the expecations for draw().
   */
  private void setDrawExpectations() {
    Constraint[] rectParams = { eq(0.0), eq(0.0), eq(90.0), eq(22.0),
      eq(Color.WHITE) };
    mockParent.expects(once()).method("getAbsoluteX1").will(returnValue(0.0));
    mockParent.expects(once()).method("getAbsoluteY1").will(returnValue(0.0));
    mockDrawingContext.expects(once()).method("drawRectangle")
      .with(rectParams);
    mockLabel.expects(once()).method("draw")
     .with(eq(mockDrawingContext.proxy()));
    mockLabel.expects(atLeastOnce()).method("isValid")
     .will(returnValue(true));
    mockDrawingContext.expects(once()).method("getClipBounds")
      .will(returnValue(new Rectangle(1000, 1000)));
  }

  /**
   * Tests the draw() method.
   */
  public void testDraw() {
    setAddLabelExpectations();
    compartment.addLabel((Label) mockLabel.proxy());
    Dimension2D parentSize = new DoubleDimension(10, 4);
    setRecalculateSizeWithOneLabelExpectations(parentSize);
    setDrawExpectations();
    mockLabel.expects(once()).method("centerHorizontally");
    compartment.draw((DrawingContext) mockDrawingContext.proxy());
    assertTrue(compartment.isValid());
  }

  /**
   * Tests the draw() method, labels are left aligned.
   */
  public void testDrawLeftAligned() {
    setAddLabelExpectations();
    compartment.setAlignment(Alignment.LEFT);
    compartment.addLabel((Label) mockLabel.proxy());
    Dimension2D parentSize = new DoubleDimension(10, 4);
    setRecalculateSizeWithOneLabelExpectations(parentSize);
    setDrawExpectations();
    mockLabel.expects(once()).method("setOrigin").with(eq(5.0), eq(0.0));
    compartment.draw((DrawingContext) mockDrawingContext.proxy());
    assertTrue(compartment.isValid());
  }
  /**
   * Tests the clone() method.
   */
  public void testClone() {
    Label oldlabel = new SimpleLabel();
    oldlabel.setOrigin(0, 0);
    oldlabel.setSize(100, 100);
    Compartment original = new Compartment();
    original.setParent((CompositeNode) mockParent.proxy());
    compartment.addLabel(oldlabel);
    
    // Test cloned object
    mockParent.expects(atLeastOnce()).method("getAbsoluteX1")
      .will(returnValue(0.0));
    mockParent.expects(atLeastOnce()).method("getAbsoluteY1")
      .will(returnValue(0.0));
    
    Compartment cloned = (Compartment) compartment.clone();
    assertTrue(cloned.getParent() == compartment.getParent());
    assertNotNull(cloned.getLabelAt(5, 5));
    assertTrue(compartment.getLabelAt(5, 5) != cloned.getLabelAt(5, 5));
    assertTrue(cloned.getLabelAt(5, 5).getParent() == cloned);
    assertTrue(compartment.getLabelAt(5, 5).getSource() ==
               cloned.getLabelAt(5, 5).getSource());
  }
}
