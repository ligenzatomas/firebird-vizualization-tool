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

import java.text.ParseException;
import java.util.regex.Pattern;
import junit.framework.*;
import org.tinyuml.model.Multiplicity;

/**
 * Tests the Multiplicity class.
 * @author Wei-ju Wu
 * @version 1.0
 */
public class MultiplicityTest extends TestCase {
  private Multiplicity multiplicity = Multiplicity.getDefaultInstance();

  /**
   * Tests the defaults.
   */
  public void testDefaultsAndConstants() {
    assertEquals(1, Multiplicity.getDefaultInstance().getLowerBound());
    assertEquals(1, Multiplicity.getDefaultInstance().getUpperBound());
    assertEquals(1, Multiplicity.ONE.getLowerBound());
    assertEquals(1, Multiplicity.ONE.getUpperBound());
    assertEquals(0, Multiplicity.N.getLowerBound());
    assertTrue(Multiplicity.N.noUpperLimit());
    assertEquals(1, Multiplicity.ONE_TO_N.getLowerBound());
    assertTrue(Multiplicity.ONE_TO_N.noUpperLimit());
    assertEquals(0, Multiplicity.ZERO_TO_ONE.getLowerBound());
    assertEquals(1, Multiplicity.ZERO_TO_ONE.getUpperBound());
  }
  
  /**
   * Tests the default behaviour.
   */
  public void testDefaults() {
    assertEquals(1, multiplicity.getLowerBound());
    assertEquals(1, multiplicity.getUpperBound());
    assertEquals("1", multiplicity.toString());
  }

  /**
   * Sets bounded values for the range.
   */
  public void testGetBoundedInstance() {
    multiplicity = Multiplicity.getBoundedInstance(2, 8);
    assertEquals(2, multiplicity.getLowerBound());
    assertEquals(8, multiplicity.getUpperBound());
    assertEquals("2..8", multiplicity.toString());
    multiplicity = Multiplicity.getBoundedInstance(7, 7);
    assertEquals(7, multiplicity.getLowerBound());
    assertEquals(7, multiplicity.getUpperBound());
    assertEquals("7", multiplicity.toString());
  }
  
  /**
   * Tests the unlimited functionality.
   */
  public void testSetUnlimited() {
    multiplicity = Multiplicity.getUnboundedInstance(3);
    assertEquals(3, multiplicity.getLowerBound());
    assertTrue(multiplicity.noUpperLimit());
    assertEquals("3..*", multiplicity.toString());

    // Special case 0..* is printed as *
    multiplicity = Multiplicity.getUnboundedInstance(0);
    assertEquals("*", multiplicity.toString());
  }
  
  /**
   * Tests the isValid() method.
   */
  public void testIsValid() {
    multiplicity = Multiplicity.getBoundedInstance(2, 8);
    assertTrue(multiplicity.isValid());
    multiplicity = Multiplicity.getBoundedInstance(7, 7);
    assertTrue(multiplicity.isValid());
    
    multiplicity = Multiplicity.getBoundedInstance(7, 1);
    assertFalse("lower value is greater upper value", multiplicity.isValid());
    // ensure that we don't have aliasing by setting the unlimited after
    // an invalid setting
    multiplicity = Multiplicity.getUnboundedInstance(2);
    assertTrue(multiplicity.isValid());
    multiplicity = Multiplicity.getBoundedInstance(-3, 3);
    assertFalse("lower value negative", multiplicity.isValid());
    multiplicity = Multiplicity.getBoundedInstance(-3, -1);
    assertFalse("both values are negative", multiplicity.isValid());
  }
  
  /**
   * Tests the equals() and hashCode() methods.
   */
  public void testEqualsHashcode() {
    Multiplicity m = Multiplicity.getBoundedInstance(3, 4);
    Multiplicity m2 = Multiplicity.getBoundedInstance(3, 4);
    Multiplicity m3 = Multiplicity.getBoundedInstance(1, 2);

    Multiplicity m4 = Multiplicity.getUnboundedInstance(2);
    Multiplicity m5 = Multiplicity.getUnboundedInstance(2);
    Multiplicity m6 = Multiplicity.getUnboundedInstance(3);

    assertFalse(m.equals(null));
    assertFalse(m.equals("someobject"));
    assertTrue(m.equals(m2));
    assertEquals(m.hashCode(), m2.hashCode());
    assertFalse(m.equals(m3));
    assertFalse(m.equals(m4));
    assertTrue(m4.equals(m5));
    assertFalse(m4.equals(m6));
    assertFalse(Multiplicity.ONE.equals(Multiplicity.ONE_TO_N));
  }
  
  /**
   * Tests creating an instance from a string.
   * @throws Exception if error occurred
   */
  public void testGetInstanceFromString() throws Exception {
    assertEquals(Multiplicity.ONE, Multiplicity.getInstanceFromString("1"));
    assertEquals(Multiplicity.ZERO_TO_ONE,
      Multiplicity.getInstanceFromString("0..1"));
    assertEquals(Multiplicity.ONE, Multiplicity.getInstanceFromString("1..1"));
    assertEquals(Multiplicity.N, Multiplicity.getInstanceFromString("*"));
    assertEquals(Multiplicity.N, Multiplicity.getInstanceFromString("0..*"));
    assertEquals(Multiplicity.ONE_TO_N,
      Multiplicity.getInstanceFromString("1..*"));
    assertEquals(multiplicity.getBoundedInstance(3, 5),
      Multiplicity.getInstanceFromString("3..5"));
    assertEquals(multiplicity.getBoundedInstance(0, 3),
      Multiplicity.getInstanceFromString("0..3"));
    Pattern pattern = Pattern.compile("1|\\*|\\d+\\.\\.(\\d+|\\*)");
    assertTrue(pattern.matcher("1").matches());
    assertTrue(pattern.matcher("*").matches());
    assertTrue(pattern.matcher("1..1").matches());
    assertTrue(pattern.matcher("1..*").matches());
    try {
      Multiplicity.getInstanceFromString("nonsense");
    } catch (ParseException expected) {
      assertEquals("could not parse 'nonsense'", expected.getMessage());
    }
  }
}
