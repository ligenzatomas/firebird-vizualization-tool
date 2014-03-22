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
package test.tinyuml.ui.diagram;

import java.awt.Component;
import java.awt.event.MouseEvent;
import javax.swing.JLabel;
import junit.framework.*;
import org.tinyuml.draw.Scaling;
import org.tinyuml.ui.diagram.EditorMouseEvent;

/**
 * Tests the EditorMouseEvent class.
 * @author Wei-ju Wu
 * @version 1.0
 */
public class EditorMouseEventTest extends TestCase {
  
  private double EPS = 0.001;
  
  /**
   * Tests the creation methods.
   */
  public void testCreation() {
    Component comp = new JLabel();
    int id = 12345;
    long now = System.currentTimeMillis();
    int modifiers = 0, clickCount = 2;
    int x = 134, y = 312;
    boolean popupTrigger = true;
    Scaling scaling = Scaling.SCALING_50;    
    MouseEvent event1 = new MouseEvent(comp, 12345, now, modifiers, x, y,
      clickCount, popupTrigger, MouseEvent.BUTTON2);
    MouseEvent event2 = new MouseEvent(comp, 12345, now, modifiers, x, y,
      1, !popupTrigger, MouseEvent.BUTTON1);
    
    EditorMouseEvent evt = new EditorMouseEvent();
    evt.setMouseEvent(event1, scaling);
    
    // Check assertions
    assertTrue(Math.abs(evt.getX() - (x * 2)) < EPS);
    assertTrue(Math.abs(evt.getY() - (y * 2)) < EPS);
    assertTrue(evt.isPopupTrigger());
    assertEquals(2, evt.getClickCount());
    assertFalse(evt.isMainButton());
    assertEquals(event1, evt.getMouseEvent());

    evt.setMouseEvent(event2, scaling);
    
    // Check assertions
    assertFalse(evt.isPopupTrigger());
    assertEquals(1, evt.getClickCount());
    assertTrue(evt.isMainButton());
    assertEquals(event2, evt.getMouseEvent());
  }
}
