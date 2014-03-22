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
package test.tinyuml.umldraw.shared;

import org.jmock.Mock;
import org.jmock.cglib.MockObjectTestCase;
import org.tinyuml.model.NamedElement;
import org.tinyuml.umldraw.shared.UmlModelElementLabelSource;

/**
 * Tests the NamedElementLabelSource.
 * @author Wei-ju Wu
 * @version 1.0
 */
public class NamedElementLabelSourceTest extends MockObjectTestCase {
  private Mock mockNamedElement = mock(NamedElement.class);
  UmlModelElementLabelSource labelsource =
    new UmlModelElementLabelSource((NamedElement) mockNamedElement.proxy());
  
  /**
   * Tests the delegation.
   */
  public void testDelegation() {
    mockNamedElement.expects(once()).method("setName").with(eq("mytext"));
    labelsource.setLabelText("mytext");
    mockNamedElement.expects(once()).method("toString")
      .will(returnValue("anothertext"));
    assertEquals("anothertext", labelsource.getLabelText());
  }
}
