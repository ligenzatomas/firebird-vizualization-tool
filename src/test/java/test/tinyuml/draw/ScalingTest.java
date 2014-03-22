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
import org.tinyuml.draw.Scaling;

/**
 * Test class for Scaling.
 * @author Wei-ju Wu
 * @version 1.0
 */
public class ScalingTest extends TestCase {
  
  /**
   * Tests the Scaling objects.
   */
  public void testScalings() {
    assertEquals(0.5, Scaling.SCALING_50.getScaleFactor());
    assertEquals(0.75, Scaling.SCALING_75.getScaleFactor());
    assertEquals(1.0, Scaling.SCALING_100.getScaleFactor());
    assertEquals(1.5, Scaling.SCALING_150.getScaleFactor());
  }
}
