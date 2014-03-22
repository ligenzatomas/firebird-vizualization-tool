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
package test.tinyuml.umldraw.sequence;

import java.awt.geom.Dimension2D;
import org.jmock.Mock;
import org.jmock.cglib.MockObjectTestCase;
import org.tinyuml.draw.Compartment;
import org.tinyuml.draw.DoubleDimension;
import org.tinyuml.draw.DrawingContext;
import org.tinyuml.draw.Label;
import org.tinyuml.model.UmlLifeLine;
import org.tinyuml.umldraw.sequence.LifeLineElement;

/**
 * A test class for the LifeLineElement.
 * @author Wei-ju Wu
 * @version 1.0
 */
public class LifeLineElementTest extends MockObjectTestCase {

  private UmlLifeLine umlLifeLine = (UmlLifeLine)
    UmlLifeLine.getPrototype().clone();
  private LifeLineElement lifeline;
  
  /**
   * {@inheritDoc}
   */
  @Override
  protected void setUp() throws Exception {
    lifeline = (LifeLineElement) LifeLineElement.getPrototype().clone();
    lifeline.setModelElement(umlLifeLine);
  }
  
  /**
   * Tests the initial state.
   */
  public void testInitialState() {
    assertEquals(umlLifeLine, lifeline.getModelElement());
    assertFalse(lifeline.isNestable());
    assertFalse(lifeline.canNestElements());
    assertNull(lifeline.getLabelText());
    lifeline.setLabelText("mylabel");
    assertEquals("mylabel", lifeline.getLabelText());
    assertEquals("mylabel", umlLifeLine.getName());
    assertTrue(lifeline.acceptsConnection(null, null, null));
  } 

  /**
   * Tests the clone() method.
   */
  public void testClone() {
    LifeLineElement cloned = (LifeLineElement) lifeline.clone();
    assertNotSame(lifeline, cloned);
    assertNotSame(lifeline.getModelElement(), cloned.getModelElement());
    assertNotSame(lifeline.getMainCompartment(), cloned.getMainCompartment());
    assertTrue(cloned == cloned.getMainCompartment().getParent());
    assertEquals(1, cloned.getMainCompartment().getLabels().size());
    Label label = cloned.getMainCompartment().getLabels().get(0);
    assertTrue(cloned.getMainCompartment() == label.getParent());
    assertTrue(cloned == label.getSource());
  }
  
  /**
   * Tests the delegation.
   */
  public void testDelegation() {
    Mock mockCompartment = mock(Compartment.class);
    Mock mockLabel = mock(Label.class);
    Mock mockDrawingContext = mock(DrawingContext.class);
    lifeline.setMainCompartment((Compartment) mockCompartment.proxy());
    mockCompartment.expects(once()).method("getLabelAt").with(eq(3.0), eq(4.0))
      .will(returnValue(mockLabel.proxy()));
    assertEquals(mockLabel.proxy(), lifeline.getLabelAt(3.0, 4.0));
    mockCompartment.expects(once()).method("invalidate");
    lifeline.invalidate();
    mockCompartment.expects(once()).method("isValid").will(returnValue(false));
    assertFalse(lifeline.isValid());
    mockCompartment.expects(once()).method("recalculateSize")
      .with(eq(mockDrawingContext.proxy()));
    lifeline.recalculateSize((DrawingContext) mockDrawingContext.proxy());
    Dimension2D minsize = new DoubleDimension(20.0, 30.0);
    mockCompartment.expects(once()).method("getMinimumSize")
      .will(returnValue(minsize));
    assertEquals(minsize, lifeline.getMinimumSize());
    mockCompartment.expects(once()).method("setMinimumSize")
      .with(eq(10.0), eq(20.0));
    lifeline.setMinimumSize(10.0, 20.0);
    Dimension2D size = new DoubleDimension(22.0, 32.0);
    mockCompartment.expects(once()).method("getSize")
      .will(returnValue(size));
    assertEquals(size, lifeline.getSize());
    mockCompartment.expects(once()).method("setSize").with(eq(10.0), eq(20.0));
    lifeline.setSize(10.0, 20.0);
  }
  
  /**
   * Tests drawing, no recalculate.
   */
  public void testDrawValid() {
    Mock mockCompartment = mock(Compartment.class);
    Mock mockLabel = mock(Label.class);
    Mock mockDrawingContext = mock(DrawingContext.class);
    lifeline.setMainCompartment((Compartment) mockCompartment.proxy());
    mockCompartment.expects(once()).method("isValid").will(returnValue(true));
    mockCompartment.expects(once()).method("draw")
      .with(eq(mockDrawingContext.proxy()));
    mockCompartment.expects(atLeastOnce()).method("getAbsCenterX")
      .will(returnValue(10.0));
    mockCompartment.expects(atLeastOnce()).method("getAbsoluteY2")
      .will(returnValue(20.0));
    mockDrawingContext.expects(once()).method("drawDashedLine");
    lifeline.draw((DrawingContext) mockDrawingContext.proxy());    
  }
  
  /**
   * Tests drawing, with recalculate.
   */
  public void testDrawInvalid() {    
    Mock mockCompartment = mock(Compartment.class);
    Mock mockLabel = mock(Label.class);
    Mock mockDrawingContext = mock(DrawingContext.class);
    lifeline.setMainCompartment((Compartment) mockCompartment.proxy());
    mockCompartment.expects(once()).method("isValid").will(returnValue(false));
    mockCompartment.expects(once()).method("recalculateSize");
    mockCompartment.expects(once()).method("draw")
      .with(eq(mockDrawingContext.proxy()));
    mockCompartment.expects(atLeastOnce()).method("getAbsCenterX")
      .will(returnValue(10.0));
    mockCompartment.expects(atLeastOnce()).method("getAbsoluteY2")
      .will(returnValue(20.0));
    mockDrawingContext.expects(once()).method("drawDashedLine");
    lifeline.draw((DrawingContext) mockDrawingContext.proxy());    
  }
}
