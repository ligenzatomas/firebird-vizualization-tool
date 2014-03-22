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

import org.jmock.Mock;
import org.jmock.cglib.MockObjectTestCase;
import org.tinyuml.draw.DoubleDimension;
import org.tinyuml.draw.DrawingContext;
import org.tinyuml.draw.DrawingContext.FontType;
import org.tinyuml.draw.Label;
import org.tinyuml.draw.LabelSource;
import org.tinyuml.model.Relation.ReadingDirection;
import org.tinyuml.model.UmlRelation;
import org.tinyuml.umldraw.structure.Association;
import org.tinyuml.umldraw.structure.AssociationNameLabel;
import test.tinyuml.draw.MyAbsoluteParent;

/**
 * A test class for the AssociationNameLabel class.
 * @author Wei-ju Wu
 * @version 1.0
 */
public class AssociationNameLabelTest extends MockObjectTestCase {
  
  private AssociationNameLabel label = new AssociationNameLabel();
  private Association association = (Association)
    Association.getPrototype().clone();
  private UmlRelation relation = new UmlRelation();
  private MyAbsoluteParent parent = new MyAbsoluteParent();
  private Mock mockDrawingContext = mock(DrawingContext.class);
  private Mock mockLabel = mock(Label.class);

  /**
   * {@inheritDoc}
   */
  @Override
  protected void setUp() throws Exception {
    label.setAssociation(association);
    label.setParent(parent);
    label.setOrigin(0, 0);
    label.setSize(100, 30);
  }
  
  /**
   * Tests the initial state.
   */
  public void testInitial() {
    assertEquals(label, label.getSource());
    assertEquals("", label.getText());
    assertNotNull(label.getLabel());
    assertNull(label.getLabelAt(300, 300));
    assertEquals(label, label.getLabelAt(30, 10));
  }
  
  /**
   * Tests setting LabelSource methods.
   */
  public void testLabelSourceMethods() {
    label.setSource(new LabelSource() {
      public String getLabelText() { return null; }
      public void setLabelText(String aText) { }
    });
    assertEquals(label, label.getSource()); // is still the same
    label.setLabelText("sometext"); // has no effect
    assertEquals("", label.getLabelText());
    
    association.setRelation(relation);
    label.setLabelText("sometext"); // now setting is possible
    assertEquals("sometext", label.getLabelText());
  }

  /**
   * Tests the delegation.
   */
  public void testDelegation() {
    mockLabel.expects(once()).method("setSource").with(eq(label));
    mockLabel.expects(once()).method("setParent").with(eq(label));
    label.setLabel((Label) mockLabel.proxy());
    
    mockLabel.expects(once()).method("setFontType").with(eq(FontType.DEFAULT));
    label.setFontType(FontType.DEFAULT);
    mockLabel.expects(once()).method("setText").with(eq("atext"));
    label.setText("atext");
    mockLabel.expects(once()).method("getText").will(returnValue("atext"));
    assertEquals("atext", label.getText());
    mockLabel.expects(once()).method("centerHorizontally");
    label.centerHorizontally();
  }
  
  /**
   * Tests the draw() method.
   */
  public void testDraw() {
    mockLabel.expects(once()).method("setSource").with(eq(label));
    mockLabel.expects(once()).method("setParent").with(eq(label));
    label.setLabel((Label) mockLabel.proxy());
    association.setRelation(relation);
    relation.setName("aname");
    mockLabel.expects(once()).method("draw")
      .with(eq(mockDrawingContext.proxy()));
    label.draw((DrawingContext) mockDrawingContext.proxy());
  }

  /**
   * Tests the draw() method, with left to right arrow.
   */
  public void testDrawLeftRightArrow() {
    mockLabel.expects(once()).method("setSource").with(eq(label));
    mockLabel.expects(once()).method("setParent").with(eq(label));
    label.setLabel((Label) mockLabel.proxy());
    association.setRelation(relation);
    relation.setName("aname");
    relation.setNameReadingDirection(ReadingDirection.LEFT_RIGHT);

    setDrawArrowExpectations();
    mockLabel.expects(once()).method("getAbsoluteX2")
      .will(returnValue(100.0));
    label.draw((DrawingContext) mockDrawingContext.proxy());
  }

  /**
   * Tests the draw() method, with right to left arrow.
   */
  public void testDrawRightLeftArrow() {
    mockLabel.expects(once()).method("setSource").with(eq(label));
    mockLabel.expects(once()).method("setParent").with(eq(label));
    label.setLabel((Label) mockLabel.proxy());
    association.setRelation(relation);
    relation.setName("aname");
    relation.setNameReadingDirection(ReadingDirection.RIGHT_LEFT);

    setDrawArrowExpectations();
    mockLabel.expects(once()).method("getAbsoluteX1")
      .will(returnValue(100.0));
    label.draw((DrawingContext) mockDrawingContext.proxy());
  }
  
  /**
   * Sets the expectations for drawing an arrow.
   */
  private void setDrawArrowExpectations() {
    mockLabel.expects(once()).method("draw")
      .with(eq(mockDrawingContext.proxy()));
    mockLabel.expects(once()).method("getSize")
      .will(returnValue(new DoubleDimension(100, 20)));    
    mockDrawingContext.expects(once()).method("draw");
    mockLabel.expects(once()).method("getAbsoluteY1")
      .will(returnValue(0.0));
  }
}
