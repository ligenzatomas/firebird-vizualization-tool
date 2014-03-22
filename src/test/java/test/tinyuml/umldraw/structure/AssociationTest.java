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
package test.tinyuml.umldraw.structure;

import java.awt.FontMetrics;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import org.jmock.Mock;
import org.jmock.cglib.MockObjectTestCase;
import org.tinyuml.draw.DrawingContext;
import org.tinyuml.model.UmlRelation;
import org.tinyuml.umldraw.structure.Association;
import org.tinyuml.umldraw.structure.Association.AssociationType;
import test.tinyuml.draw.MyAbsoluteParent;
import test.tinyuml.draw.MyCompositeNode;

/**
 * Test class for the Association class.
 * @author Wei-ju Wu
 * @version 1.0
 */
public class AssociationTest extends MockObjectTestCase {
  private Association prototype = (Association)
    Association.getPrototype().clone();
  private MyAbsoluteParent parent = new MyAbsoluteParent();
  private MyCompositeNode node1 = new MyCompositeNode();
  private MyCompositeNode node2 = new MyCompositeNode();
  private UmlRelation relation = new UmlRelation();
  private Mock mockDrawingContext = mock(DrawingContext.class);
  static class MyFontMetrics extends FontMetrics {
    public MyFontMetrics() {
        super(null);
    }
  }
  private Mock mockFontMetrics = mock(MyFontMetrics.class);

  /**
   * {@inheritDoc]
   */
  @Override
  protected void setUp() throws Exception {
    prototype.setAssociationType(AssociationType.UNSPECIFIED);
    prototype.setRelation(relation);
    prototype.setParent(parent);
    node1.setParent(parent);
    node2.setParent(parent);
    prototype.setNode1(node1);
    prototype.setNode2(node2);
    node1.setOrigin(0, 5);
    node1.setSize(10, 10);
    node2.setOrigin(20, 5);
    node2.setSize(10, 10);
    List<Point2D> points = new ArrayList<Point2D>();
    points.add(new Point2D.Double(10, 10));
    points.add(new Point2D.Double(20, 10));
    prototype.setPoints(points);
  }
  
  /**
   * Tests the default values.
   */
  public void testDefaults() {
    assertFalse(prototype.showMultiplicities());    
    assertFalse(prototype.showName());
    assertEquals(AssociationType.UNSPECIFIED, prototype.getAssociationType());
    // no effect
    assertNull(prototype.getLabelAt(0, 0));
    prototype.getMultiplicity1Label().getSource().setLabelText("");
    prototype.getMultiplicity2Label().getSource().setLabelText("");
  }
  
  /**
   * Tests the setter methods.
   */
  public void testSetters() {
    assertNotNull(prototype.getMultiplicity1Label());
    assertNotNull(prototype.getMultiplicity2Label());
    assertNotNull(prototype.getNameLabel());
    assertEquals(parent, prototype.getMultiplicity1Label().getParent());
    assertEquals(parent, prototype.getMultiplicity2Label().getParent());
    assertEquals(parent, prototype.getNameLabel().getParent());
    prototype.setAssociationType(AssociationType.AGGREGATION);
    assertEquals(AssociationType.AGGREGATION, prototype.getAssociationType());
    prototype.setShowMultiplicities(true);
    assertTrue(prototype.showMultiplicities());
    prototype.setShowName(true);
    assertTrue(prototype.showName());
  }
  
  /**
   * Tests the clone() method.
   */
  public void testClone() {
    prototype.setAssociationType(AssociationType.COMPOSITION);
    prototype.setShowName(true);
    prototype.setShowMultiplicities(false);
    Association cloned = (Association) prototype.clone();
    
    assertEquals(prototype.getAssociationType(), cloned.getAssociationType());
    assertEquals(parent, cloned.getMultiplicity1Label().getParent());
    assertEquals(parent, cloned.getMultiplicity2Label().getParent());
    assertEquals(parent, cloned.getNameLabel().getParent());
    assertTrue(cloned.getMultiplicity1Label() !=
      prototype.getMultiplicity1Label());
    assertTrue(cloned.getMultiplicity1Label() !=
      prototype.getMultiplicity2Label());
    assertTrue(cloned.getNameLabel() != prototype.getNameLabel());
    
    assertNotNull(cloned.getMultiplicity1Label().getSource());
    assertNotNull(cloned.getMultiplicity2Label().getSource());
    assertNotNull(cloned.getNameLabel().getSource());
    
    assertTrue(cloned.getMultiplicity1Label().getSource() !=
      prototype.getMultiplicity1Label().getSource());
    assertTrue(cloned.getMultiplicity2Label().getSource() !=
      prototype.getMultiplicity1Label().getSource());
    assertTrue(cloned.getNameLabel().getSource() == cloned.getNameLabel());
    assertTrue(cloned.showName());
    assertFalse(cloned.showMultiplicities());
  }
  
  /**
   * Tests the draw() method.
   */
  public void testDrawNoLabels() {
    mockDrawingContext.expects(atLeastOnce()).method("drawLine");
    prototype.draw((DrawingContext) mockDrawingContext.proxy());
  }

  /**
   * Tests the draw() method.
   */
  public void testDrawWithLabelsEastWest() {
    setFontMetricsExpectationsWithLabelsShowing();
    prototype.setShowMultiplicities(true);
    prototype.setShowName(true);
    mockDrawingContext.expects(atLeastOnce()).method("drawLine");
    prototype.draw((DrawingContext) mockDrawingContext.proxy());
  }

  /**
   * Tests the draw() method, NORTH-SOUTH placement.
   */
  public void testDrawWithLabelsNorthSouth() {
    node1.setOrigin(5, 0);
    node1.setSize(10, 10);
    node2.setOrigin(5, 20);
    node2.setSize(10, 10);
    List<Point2D> points = new ArrayList<Point2D>();
    points.add(new Point2D.Double(10, 10));
    points.add(new Point2D.Double(10, 20));
    prototype.setPoints(points);
    
    setFontMetricsExpectationsWithLabelsShowing();
    prototype.setShowMultiplicities(true);
    prototype.setShowName(true);
    mockDrawingContext.expects(atLeastOnce()).method("drawLine");
    prototype.draw((DrawingContext) mockDrawingContext.proxy());
  }


  /**
   * Expectations for recalculateSize().
   */
  private void setFontMetricsExpectationsWithLabelsShowing() {
    FontMetrics fontMetrics = (FontMetrics) mockFontMetrics.proxy();
    mockDrawingContext.expects(atLeastOnce()).method("getFontMetrics")
      .will(returnValue(fontMetrics));    
    mockFontMetrics.expects(atLeastOnce()).method("getMaxAscent")
      .will(returnValue(19));
    mockFontMetrics.expects(atLeastOnce()).method("stringWidth")
      .will(returnValue(20));
    mockFontMetrics.expects(atLeastOnce()).method("getHeight")
      .will(returnValue(20));
    mockDrawingContext.expects(atLeastOnce()).method("drawLabel");
  }

  /**
   * Tests the draw() method with composition and arrow.
   */
  public void testDrawCompositionWithArrow() {
    prototype.setAssociationType(AssociationType.COMPOSITION);
    relation.setNavigableToElement2(true);
    try {
      mockDrawingContext.expects(atLeastOnce()).method("drawLine");
      mockDrawingContext.expects(atLeastOnce()).method("draw");
      prototype.draw((DrawingContext) mockDrawingContext.proxy());
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  /**
   * Tests the draw() method with composition and arrow.
   */
  public void testDrawAggregationWithArrow() {
    prototype.setAssociationType(AssociationType.AGGREGATION);
    relation.setNavigableToElement2(true);
    try {
      mockDrawingContext.expects(atLeastOnce()).method("drawLine");
      mockDrawingContext.expects(atLeastOnce()).method("draw");
      prototype.draw((DrawingContext) mockDrawingContext.proxy());
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  /**
   * Tests the draw() method with two arrows.
   */
  public void testDrawUnspecifiedWithTwoArrow() {
    relation.setNavigableToElement1(true);
    relation.setNavigableToElement2(true);
    try {
      mockDrawingContext.expects(atLeastOnce()).method("drawLine");
      prototype.draw((DrawingContext) mockDrawingContext.proxy());
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }
}
