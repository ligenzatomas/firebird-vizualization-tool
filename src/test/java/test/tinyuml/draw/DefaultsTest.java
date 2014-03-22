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
import org.tinyuml.draw.Defaults;

/**
 * A test class for Defaults.
 * @author Wei-ju Wu
 * @version 1.0
 */
public class DefaultsTest extends TestCase {

  /**
   * Tests the getter methods.
   */
  public void testGetters() {
    Defaults defaults = Defaults.getInstance();
    assertEquals(5.0, defaults.getMarginLeft());
    assertEquals(5.0, defaults.getMarginRight());
    assertEquals(defaults.getMarginLeft() + defaults.getMarginRight(),
      defaults.getMarginSide());
    assertEquals(5.0, defaults.getMarginTop());
    assertEquals(5.0, defaults.getMarginBottom());
  }
}
