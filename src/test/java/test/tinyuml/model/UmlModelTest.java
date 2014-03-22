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
package test.tinyuml.model;

import org.jmock.Mock;
import org.jmock.cglib.MockObjectTestCase;
import org.tinyuml.model.UmlDiagram;
import org.tinyuml.model.UmlModel;
import org.tinyuml.model.UmlModelElement;
import org.tinyuml.model.UmlModelImpl;
import org.tinyuml.model.UmlModelListener;

/**
 * A test class for the UmlModel class.
 * @author Wei-ju Wu
 * @version 1.0
 */
public class UmlModelTest extends MockObjectTestCase {

  private UmlModel model = new UmlModelImpl();
  private int modelChangedCounter = 0;
  private Mock mockElement1 = mock(UmlModelElement.class);
  private Mock mockElement2 = mock(UmlModelElement.class);
  private UmlModelElement element1 = (UmlModelElement) mockElement1.proxy();
  private UmlModelElement element2 = (UmlModelElement) mockElement2.proxy();
    
  /**
   * Tests the initial state.
   */
  public void testInitial() {
    assertEquals("there should be no elements", 0, model.getElements().size());
    assertEquals("UML model", model.toString());
  }
  
  /**
   * Tests the addElement() method.
   */
  public void testAddElement() {
    model.addElement(element1);
    model.addElement(element1);
    assertEquals("element1 should be added", 1, model.getElements().size());
    assertTrue(model.contains(element1));
    
    model.addElement(element2);
    assertEquals("element 2 should be added", 2, model.getElements().size());
    assertTrue(model.contains(element1));
    assertTrue(model.contains(element2));
  }
  
  /**
   * Tests the addDiagram() method.
   */
  public void testAddDiagram() {
    assertEquals(0, model.getDiagrams().size());
    Mock mockDiagram1 = mock(UmlDiagram.class);
    model.addDiagram((UmlDiagram) mockDiagram1.proxy());
    assertEquals(1, model.getDiagrams().size());
    
    Mock mockDiagram2 = mock(UmlDiagram.class);
    Mock mockListener = mock(UmlModelListener.class);
    model.addModelListener((UmlModelListener) mockListener.proxy());
    mockListener.expects(once()).method("diagramAdded")
      .with(eq(mockDiagram2.proxy()));
    model.addDiagram((UmlDiagram) mockDiagram2.proxy());
    assertEquals(2, model.getDiagrams().size());    
  }
  
  /**
   * Test the removeDiagram() method.
   */
  public void testRemoveDiagram() {
    Mock mockDiagram1 = mock(UmlDiagram.class);
    model.addDiagram((UmlDiagram) mockDiagram1.proxy());
    
    Mock mockListener = mock(UmlModelListener.class);
    model.addModelListener((UmlModelListener) mockListener.proxy());
    mockListener.expects(once()).method("diagramRemoved")
      .with(eq(mockDiagram1.proxy()));
    model.removeDiagram((UmlDiagram) mockDiagram1.proxy());
    assertEquals(0, model.getDiagrams().size());
  }
}
