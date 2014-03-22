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
package test.tinyuml.ui.model;

import junit.framework.*;
import org.tinyuml.ui.model.StringTableModel;

/**
 * A test class for the StringTableModel.
 * @author Wei-ju Wu
 * @version 1.0
 */
public class StringTableModelTest extends TestCase {
  
  private StringTableModel tableModel = new StringTableModel();

  /**
   * Tests the initial state.
   */
  public void testInitial() {
    assertEquals(0, tableModel.getEntries().size());
    assertEquals(0, tableModel.getRowCount());
    assertEquals(1, tableModel.getColumnCount());
    assertNull(tableModel.getColumnName(0));
    // always holds true
    assertTrue(tableModel.isCellEditable(0, 0));
    assertTrue(tableModel.isCellEditable(3, 4));
  }
  
  /**
   * Tests adding and deleting entries.
   */
  public void testEditEntries() {
    tableModel.addEntry("entry1");
    tableModel.addEntry("entry2");
    tableModel.addEntry("entry3");
    assertEquals(3, tableModel.getRowCount());
    assertEquals(3, tableModel.getEntries().size());
    assertEquals("entry1", tableModel.getEntries().get(0));
    assertEquals("entry2", tableModel.getEntries().get(1));
    assertEquals("entry3", tableModel.getEntries().get(2));
    
    tableModel.removeEntryAt(1);
    assertEquals(2, tableModel.getRowCount());
    assertEquals("entry1", tableModel.getEntries().get(0));
    assertEquals("entry3", tableModel.getEntries().get(1));
    assertEquals("entry1", tableModel.getValueAt(0, 0));
    assertEquals("entry3", tableModel.getValueAt(1, 0));
    tableModel.setValueAt("changed", 1, 0);
    assertEquals("changed", tableModel.getValueAt(1, 0));
  }

  /**
   * Tests moving of entries.
   */
  public void testMoveEntries() {
    tableModel.addEntry("entry1");
    tableModel.addEntry("entry2");
    tableModel.addEntry("entry3");
    tableModel.moveDownEntry(0);
    assertEquals("entry2", tableModel.getValueAt(0, 0));
    assertEquals("entry1", tableModel.getValueAt(1, 0));
    assertEquals("entry3", tableModel.getValueAt(2, 0));
    tableModel.moveUpEntry(2);
    assertEquals("entry2", tableModel.getValueAt(0, 0));
    assertEquals("entry3", tableModel.getValueAt(1, 0));
    assertEquals("entry1", tableModel.getValueAt(2, 0));
  }
}
