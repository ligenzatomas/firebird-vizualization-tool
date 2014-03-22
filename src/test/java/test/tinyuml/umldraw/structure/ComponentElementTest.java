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
import org.tinyuml.draw.Compartment;
import org.tinyuml.umldraw.structure.ComponentElement;
import org.tinyuml.draw.CompositeNode;
import org.tinyuml.draw.DoubleDimension;
import org.tinyuml.draw.DrawingContext;
import org.tinyuml.draw.Label;
import org.tinyuml.model.UmlComponent;

/**
 * A test class for ComponentElement.
 * @author Wei-ju Wu
 * @version 1.0
 */
public class ComponentElementTest extends MockObjectTestCase {

  private Mock mockDrawingContext = mock(DrawingContext.class);
  private DrawingContext drawingContext = (DrawingContext)
    mockDrawingContext.proxy();
  private UmlComponent component = (UmlComponent)
    UmlComponent.getPrototype().clone();
  private Mock mockCompartment = mock(Compartment.class);
  private Mock mockLabel = mock(Label.class);
  private Mock mockParent = mock(CompositeNode.class);
  private ComponentElement element = (ComponentElement)
    ComponentElement.getPrototype().clone();
  private ComponentElement mockedElement = (ComponentElement)
    ComponentElement.getPrototype().clone();

  /**
   * {@inheritDoc}
   */
  @Override
  protected void setUp() {
    mockedElement.setModelElement(component);
    mockedElement.setMainCompartment((Compartment) mockCompartment.proxy());
    mockedElement.setParent((CompositeNode) mockParent.proxy());
    element.setModelElement(component);
    element.setLabelText(null);
  }
  
  /**
   * Tests the clone() method.
   */
  public void testClone() {
    ComponentElement cloned = (ComponentElement) element.clone();
    assertTrue(cloned != element);
    assertTrue(cloned.getModelElement() != element.getModelElement());
    assertTrue(cloned.getMainCompartment() != element.getMainCompartment());
    assertTrue(cloned.getMainCompartment().getParent() == cloned);
    // tests the contained label
    assertEquals(1, cloned.getMainCompartment().getLabels().size());
    Label label = cloned.getMainCompartment().getLabels().get(0);
    assertTrue(cloned.getMainCompartment() == label.getParent());
    assertTrue(label.getSource() == cloned);
  }

  /**
   * Tests initial state.
   */
  public void testInitialState() {
    assertFalse(element.canNestElements());
    assertTrue(element.isNestable());
    assertEquals(component, element.getModelElement());
    assertNull(element.getLabelText());
    element.setLabelText("mylabeltext");
    assertEquals("mylabeltext", element.getLabelText());
    assertEquals("mylabeltext", component.getName());
  }
  
  /**
   * Verifies that the sizing methods are all delegated to the wrapped
   * compartment.
   */
  public void testDelegatedMethods() {
    mockCompartment.expects(once()).method("recalculateSize")
      .with(eq(mockDrawingContext.proxy()));
    mockedElement.recalculateSize((DrawingContext) mockDrawingContext.proxy());
    mockCompartment.expects(once()).method("setSize").with(eq(1.0), eq(2.0));
    mockedElement.setSize(1.0, 2.0);
    mockCompartment.expects(once()).method("setMinimumSize")
      .with(eq(3.0), eq(4.0));
    mockedElement.setMinimumSize(3.0, 4.0);
    Dimension2D minsize = new DoubleDimension(11.0, 12.0);
    Dimension2D size = new DoubleDimension(15.0, 18.0);
    mockCompartment.expects(once()).method("getSize").will(returnValue(size));
    assertEquals(size, mockedElement.getSize());
    mockCompartment.expects(once()).method("getMinimumSize")
      .will(returnValue(minsize));
    assertEquals(minsize, mockedElement.getMinimumSize());
    
    mockCompartment.expects(once()).method("invalidate");
    mockedElement.invalidate();
    mockCompartment.expects(once()).method("isValid")
      .will(returnValue(true));
    assertTrue(mockedElement.isValid());
    mockCompartment.expects(once()).method("getLabelAt")
      .with(eq(5.0), eq(6.0))
      .will(returnValue(mockLabel.proxy()));
    assertEquals(mockLabel.proxy(), mockedElement.getLabelAt(5.0, 6.0));
  }
  
  /**
   * Tests the draw() method.
   */
  public void testDraw() {
    // simulate invalid object
    mockCompartment.expects(once()).method("isValid").will(returnValue(false));
    mockCompartment.expects(once()).method("recalculateSize")
      .with(eq(mockDrawingContext.proxy()));
    mockCompartment.expects(once()).method("draw")
      .with(eq(mockDrawingContext.proxy()));
    // drawing the icon
    mockParent.expects(once()).method("getAbsoluteX1").will(returnValue(100.0));
    mockParent.expects(once()).method("getAbsoluteY1").will(returnValue(50.0));
    mockCompartment.expects(atLeastOnce()).method("getSize")
      .will(returnValue(new DoubleDimension(100, 50)));
    mockDrawingContext.expects(atLeastOnce()).method("drawRectangle");
    mockedElement.draw((DrawingContext) mockDrawingContext.proxy());
  }
}
