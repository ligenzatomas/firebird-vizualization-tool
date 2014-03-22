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
import org.tinyuml.model.Multiplicity;
import org.tinyuml.model.Relation.ReadingDirection;
import org.tinyuml.model.UmlModelElement;
import org.tinyuml.model.UmlRelation;

/**
 * This class tests the UmlRelation class.
 *
 * @author Wei-ju Wu
 * @version 1.0
 */
public class UmlRelationTest extends MockObjectTestCase {
  
  private UmlRelation relation = new UmlRelation();

  /**
   * Tests initial state.
   */
  public void testInitial() {
    assertNull(relation.getElement1());
    assertNull(relation.getElement2());
    assertEquals("", relation.toString());
    assertEquals(ReadingDirection.UNDEFINED,
      relation.getNameReadingDirection());
  }
  
  /**
   * Tests the setter method  behaviour.
   */
  public void testSetters() {
    Mock mockSource = mock(UmlModelElement.class);
    Mock mockTarget = mock(UmlModelElement.class);
    UmlModelElement source = (UmlModelElement) mockSource.proxy();
    UmlModelElement target = (UmlModelElement) mockTarget.proxy();
    
    relation.setElement1(source);
    relation.setElement2(target);
    assertEquals(source, relation.getElement1());
    assertEquals(target, relation.getElement2());
    
    // toString()
    mockSource.expects(once()).method("getName").will(returnValue("source"));
    mockTarget.expects(once()).method("getName").will(returnValue("target"));
    assertEquals("source->target", relation.toString());
    
    // name overrides toString()
    relation.setName("association");
    assertEquals("association", relation.toString());
    
    // navigability
    relation.setCanSetElement1Navigability(true);
    relation.setCanSetElement2Navigability(false);
    assertTrue(relation.canSetElement1Navigability());
    assertFalse(relation.canSetElement2Navigability());
    relation.setNavigableToElement1(false);
    relation.setNavigableToElement2(true);
    assertFalse(relation.isNavigableToElement1());
    assertTrue(relation.isNavigableToElement2());
    
    // multiplicity
    relation.setElement1Multiplicity(Multiplicity.N);
    relation.setElement2Multiplicity(Multiplicity.ONE);
    assertEquals(Multiplicity.N, relation.getElement1Multiplicity());
    assertEquals(Multiplicity.ONE, relation.getElement2Multiplicity());
    
    // reading direction
    relation.setNameReadingDirection(ReadingDirection.LEFT_RIGHT);
    assertEquals(ReadingDirection.LEFT_RIGHT,
      relation.getNameReadingDirection());
    relation.setNameReadingDirection(ReadingDirection.RIGHT_LEFT);
    assertEquals(ReadingDirection.RIGHT_LEFT,
      relation.getNameReadingDirection());
  }
  
  /**
   * Tests the clone() method.
   */
  public void testClone() {
    Mock mockSource = mock(UmlModelElement.class);
    Mock mockTarget = mock(UmlModelElement.class);
    UmlModelElement source = (UmlModelElement) mockSource.proxy();
    UmlModelElement target = (UmlModelElement) mockTarget.proxy();    
    relation.setElement1(source);
    relation.setElement2(target);
    relation.setName("name");
    relation.setCanSetElement1Navigability(false);
    relation.setCanSetElement2Navigability(true);
    relation.setNavigableToElement1(true);
    relation.setNavigableToElement2(false);
    
    UmlRelation cloned = (UmlRelation) relation.clone();
    assertEquals(relation.getName(), cloned.getName());
    assertEquals(relation.getElement1(), cloned.getElement1());
    assertEquals(relation.getElement2(), cloned.getElement2());
    assertEquals(relation.canSetElement1Navigability(),
      cloned.canSetElement1Navigability());
    assertEquals(relation.canSetElement2Navigability(),
      cloned.canSetElement2Navigability());
    assertEquals(relation.isNavigableToElement1(),
      cloned.isNavigableToElement1());
    assertEquals(relation.isNavigableToElement2(),
      cloned.isNavigableToElement2());
  }
}
