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

import java.awt.geom.Dimension2D;
import org.jmock.Mock;
import org.jmock.cglib.MockObjectTestCase;
import org.tinyuml.umldraw.structure.ClassElement;
import org.tinyuml.draw.Compartment;
import org.tinyuml.draw.Compartment.Alignment;
import org.tinyuml.draw.DoubleDimension;
import org.tinyuml.draw.DrawingContext;
import org.tinyuml.draw.Label;
import org.tinyuml.model.UmlClass;

/**
 * A test class for ClassElement.
 * @author Wei-ju Wu
 * @version 1.0
 */
public class ClassElementTest extends MockObjectTestCase {
  private Mock mockDrawingContext = mock(DrawingContext.class);
  private Mock mockMainCompartment = mock(Compartment.class);
  private Mock mockAttrCompartment = mock(Compartment.class);
  private Mock mockOpCompartment = mock(Compartment.class);
  private UmlClass umlclass = (UmlClass) UmlClass.getPrototype().clone();
  private ClassElement element = (ClassElement)
    ClassElement.getPrototype().clone();
  private ClassElement mockedElement;

  /**
   * {@inheritDoc}
   */
  @Override
  protected void setUp() {
    element.setModelElement(umlclass);
    mockedElement = (ClassElement) ClassElement.getPrototype().clone();
    mockedElement.setModelElement(umlclass);
    mockedElement.setMainCompartment((Compartment) mockMainCompartment.proxy());
    mockedElement.setAttributesCompartment((Compartment)
      mockAttrCompartment.proxy());
    mockedElement.setOperationsCompartment((Compartment)
      mockOpCompartment.proxy());;
  }
  
  /**
   * Tests the clone() method.
   */
  public void testClone() {
    ClassElement cloned = (ClassElement) element.clone();
    assertEquals(1, cloned.getModelElement().getModelElementListeners().size());
    assertTrue(cloned.getModelElement().getModelElementListeners()
      .contains(cloned));
    assertTrue(cloned.getModelElement() != element.getModelElement());
    assertTrue(cloned != element);
    assertTrue(cloned.getMainCompartment() != element.getMainCompartment());
    assertTrue(cloned.getMainCompartment().getParent() == cloned);
    assertTrue(cloned.getAttributesCompartment() !=
               element.getAttributesCompartment());
    assertTrue(cloned.getAttributesCompartment().getParent() == cloned);
    assertTrue(cloned.getOperationsCompartment() !=
               element.getOperationsCompartment());
    assertTrue(cloned.getOperationsCompartment().getParent() == cloned);
    
    // Tests the main label
    assertTrue(cloned.getMainLabel() != element.getMainLabel());
    assertEquals(1, cloned.getMainCompartment().getLabels().size());
    assertEquals(cloned.getMainLabel(),
      cloned.getMainCompartment().getLabels().get(0));
    assertTrue(cloned.getMainCompartment() == cloned.getMainLabel().getParent());
    assertTrue(cloned == cloned.getMainLabel().getSource());
  }

  /**
   * Tests the initial state.
   */
  public void testInitialState() {
    assertTrue(element.isNestable());
    assertFalse(element.canNestElements());
    assertEquals(umlclass, element.getModelElement());
  }
  
  /**
   * Tests the simple setter methods.
   */
  public void testSetters() {
    element.setLabelText("mytext");
    assertEquals("mytext", element.getLabelText());
    assertEquals("mytext", umlclass.getName());
    assertEquals("mytext", element.toString());
  }
  
  /**
   * Tests the getLabelAt() method.
   */
  public void testGetLabelAt() {
    mockMainCompartment.expects(once()).method("getLabelAt")
      .with(eq(2.0), eq(3.0)).will(returnValue(mock(Label.class).proxy()));
    assertNotNull(mockedElement.getLabelAt(2.0, 3.0));
  }
  
  // *************************************************************************
  // ******** Resizing operations
  // ***************************************
  
  private void doRecalculateSizeExpectations(double mainwidth) {
    Dimension2D mainsize = new DoubleDimension(mainwidth, 30);
    Dimension2D opsize = new DoubleDimension(35, 10);
    Dimension2D mainminsize = new DoubleDimension(50, 30);
    Dimension2D opminsize = new DoubleDimension(35, 10);
    mockMainCompartment.expects(once()).method("recalculateSize")
      .with(eq(mockDrawingContext.proxy()));
    mockAttrCompartment.expects(once()).method("recalculateSize")
      .with(eq(mockDrawingContext.proxy()));
    mockOpCompartment.expects(once()).method("recalculateSize")
      .with(eq(mockDrawingContext.proxy()));
    mockMainCompartment.expects(atLeastOnce()).method("getSize")
      .will(returnValue(mainsize));
    //mockMainCompartment.expects(atLeastOnce()).method("getMinimumSize")
    //  .will(returnValue(mainminsize));
    mockOpCompartment.expects(atLeastOnce()).method("getSize")
      .will(returnValue(opsize));
    mockOpCompartment.expects(atLeastOnce()).method("getMinimumSize")
      .will(returnValue(opminsize));
    mockAttrCompartment.expects(once()).method("setOrigin")
      .with(eq(0.0), eq(30.0));
    mockOpCompartment.expects(once()).method("setOrigin")
      .with(eq(0.0), eq(30.0));
    mockMainCompartment.expects(atLeastOnce()).method("setWidth")
      .with(eq(mainsize.getWidth()));
    mockAttrCompartment.expects(atLeastOnce()).method("setWidth")
      .with(eq(mainsize.getWidth()));
  }
  
  /**
   * Tests the recalculateSize() operation.
   */
  public void testRecalculateSize() {
    doRecalculateSizeExpectations(50);
    mockOpCompartment.expects(once()).method("setHeight")
      .with(eq(10.0));
    mockedElement.recalculateSize((DrawingContext) mockDrawingContext.proxy());
    assertEquals(50.0, mockedElement.getSize().getWidth());
    assertEquals(40.0, mockedElement.getSize().getHeight());
  }
  
  /**
   * Tests that after recalculateSize() the size will have taken over the
   * larger one.
   */
  public void testSetSize() {
    mockMainCompartment.expects(atLeastOnce()).method("invalidate");
    mockAttrCompartment.expects(atLeastOnce()).method("invalidate");
    mockOpCompartment.expects(atLeastOnce()).method("invalidate");
    mockedElement.setSize(80, 80);
    
    doRecalculateSizeExpectations(80);
    mockOpCompartment.expects(once()).method("setHeight")
      .with(eq(50.0));
    mockedElement.recalculateSize((DrawingContext) mockDrawingContext.proxy());    
    assertEquals(80.0, mockedElement.getSize().getWidth());
    assertEquals(80.0, mockedElement.getSize().getHeight());
  }
  
  /**
   * Tests the draw() method.
   */
  public void testDraw() {
    doRecalculateSizeExpectations(50);
    mockMainCompartment.expects(once()).method("draw")
      .with(eq(mockDrawingContext.proxy()));
    mockOpCompartment.expects(once()).method("draw")
      .with(eq(mockDrawingContext.proxy()));
    mockMainCompartment.expects(once()).method("isValid")
      .will(returnValue(false));
    mockOpCompartment.expects(once()).method("isValid")
      .will(returnValue(false));
    mockOpCompartment.expects(once()).method("setHeight")
      .with(eq(10.0));
    mockedElement.draw((DrawingContext) mockDrawingContext.proxy());
  }
}
