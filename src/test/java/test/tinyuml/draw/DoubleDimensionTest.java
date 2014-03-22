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
package test.tinyuml.draw;

import junit.framework.*;
import org.tinyuml.draw.DoubleDimension;

/**
 * A test for DoubleDimension.
 * @author Wei-ju Wu
 * @version 1.0
 */
public class DoubleDimensionTest extends TestCase {

  /**
   * Tests the methods.
   */
  public void testMethods() {
    DoubleDimension dim = new DoubleDimension();
    assertEquals(0.0, dim.getWidth());
    assertEquals(0.0, dim.getHeight());
    
    dim.setSize(1.0, 2.3);
    assertEquals(1.0, dim.getWidth());
    assertEquals(2.3, dim.getHeight());
    
    DoubleDimension dim2 = new DoubleDimension(3.1, 5.2);
    assertEquals(3.1, dim2.getWidth());
    assertEquals(5.2, dim2.getHeight());
    assertEquals("DoubleDimension[3.1, 5.2]", dim2.toString());
  }
  
  /**
   * Tests the equals and hashCode methods.
   */
  public void testEquals() {
    DoubleDimension dim1 = new DoubleDimension(3.2, 5.3);
    DoubleDimension dim2 = new DoubleDimension(3.2, 5.3);
    DoubleDimension dim3 = new DoubleDimension(51.2, 15.3);
    assertFalse(dim1.equals(null));
    assertTrue(dim1.equals(dim1));
    assertTrue(dim1.equals(dim2));
    assertTrue(dim2.equals(dim1));
    assertFalse(dim1.equals(dim3));
    assertFalse(dim1.equals("someotherobject"));

    assertEquals(dim1.hashCode(), dim2.hashCode());
  }
}
