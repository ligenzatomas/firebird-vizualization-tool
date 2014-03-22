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
package test.tinyuml.util;

import junit.framework.*;
import org.tinyuml.util.MethodCall;

/**
 * A test for the MethodCall class.
 * @author Wei-ju Wu
 * @version 1.0
 */
public class MethodCallTest extends TestCase {

  /**
   * Just a mockup class to check if the call was performed.
   */
  public static class MyObject {
    private String callparam;
    public void mymethod(String param) {
      callparam = param;
    }
  }

  /**
   * Tests usage of the MethodCall class.
   * @throws Exception if error occurred
   */
  public void testMethodCall() throws Exception {
    MyObject myobj = new MyObject();
    MethodCall mycall = new MethodCall(myobj.getClass().getMethod("mymethod",
      String.class), "wascalled");
    mycall.call(myobj);
    assertEquals(myobj.getClass().getMethod("mymethod", String.class),
      mycall.getMethod());
    assertEquals("wascalled", myobj.callparam);
  }
}
