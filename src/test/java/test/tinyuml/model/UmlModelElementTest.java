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
import org.jmock.MockObjectTestCase;
import org.tinyuml.model.AbstractUmlModelElement;
import org.tinyuml.model.UmlModelElement;
import org.tinyuml.model.UmlModelElementListener;

/**
 * A test class for UmlModelElement.
 * @author Wei-ju Wu
 * @version 1.0
 */
public class UmlModelElementTest extends MockObjectTestCase {

  class MyModelElement extends AbstractUmlModelElement {
    // made public for testing
    public void notifyElementChanged() {
      super.notifyElementChanged();
    }
  }
  
  /**
   * Tests the clone() method.
   */
  public void testClone() {
    Mock mockListener = mock(UmlModelElementListener.class);
    UmlModelElement element = new MyModelElement();
    element.addModelElementListener((UmlModelElementListener)
      mockListener.proxy());
    UmlModelElement cloned = (UmlModelElement) element.clone();
    assertTrue(element != cloned);
    assertTrue(element.getModelElementListeners() !=
      cloned.getModelElementListeners());
  }

  /**
   * Tests the UmlModelElementListener methods.
   */
  public void testModelElementListener() {
    Mock mockListener = mock(UmlModelElementListener.class);
    MyModelElement elem = new MyModelElement();
    // add it twice to ensure no aliasing occurs
    elem.addModelElementListener((UmlModelElementListener)
      mockListener.proxy());
    elem.addModelElementListener((UmlModelElementListener)
      mockListener.proxy());
    // will be notified
    mockListener.expects(once()).method("elementChanged").with(eq(elem));
    elem.notifyElementChanged();
    // clone the element and make sure the listener is not invoked on the clone
    mockListener.expects(never()).method("elementChanged").with(eq(elem));
    MyModelElement clone = (MyModelElement) elem.clone();
    clone.notifyElementChanged();
    // remove the listener
    elem.removeModelElementListener((UmlModelElementListener)
      mockListener.proxy());
    mockListener.expects(never()).method("elementChanged").with(eq(elem));
    elem.notifyElementChanged();
  }
}
