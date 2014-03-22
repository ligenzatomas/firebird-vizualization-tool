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
import org.tinyuml.draw.CompositeNode;
import org.tinyuml.model.RelationEndType;
import org.tinyuml.model.RelationType;
import org.tinyuml.model.UmlRelation;
import org.tinyuml.umldraw.shared.NoteConnection;
import org.tinyuml.umldraw.shared.NoteElement;
import org.tinyuml.umldraw.structure.ClassElement;

/**
 * A test class for NoteElement.
 * @author Wei-ju Wu
 * @version 1.0
 */
public class NoteElementTest extends MockObjectTestCase {

  /**
   * Tests the clone() method.
   */
  public void testClone() {
    Mock mockParent1 = mock(CompositeNode.class);
    NoteElement note = NoteElement.getPrototype();
    assertNull(note.getParent());
    assertNull(note.getModelElement());
    note.setOrigin(0, 0);
    note.setSize(100, 80);
    note.setLabelText("oldlabel");
    note.setParent((CompositeNode) mockParent1.proxy());
    
    NoteElement cloned = (NoteElement) note.clone();
    assertTrue(note != cloned);
    assertEquals(note.getParent(), cloned.getParent());
    assertEquals("oldlabel", cloned.getLabelText());
    note.setLabelText("mylabel");
    assertFalse(cloned.getLabelText() == note.getLabelText());
    
    // now test the label
    mockParent1.expects(atLeastOnce()).method("getAbsoluteX1")
      .will(returnValue(0.0));
    mockParent1.expects(atLeastOnce()).method("getAbsoluteY1")
      .will(returnValue(0.0));
    assertNotNull(cloned.getLabelAt(20, 20));
    assertNull(cloned.getLabelAt(-1.0, -2.0));
    assertTrue(cloned.getLabelAt(20.0, 20.0) != note.getLabelAt(20.0, 20.0));
    assertTrue(cloned.getLabelAt(20.0, 20.0).getSource() == cloned);
    assertTrue(cloned.getLabelAt(20.0, 20.0).getParent() == cloned);
  }
  
  /**
   * Tests the acceptsConnection() method.
   */
  public void testAcceptsConnection() {
    NoteElement note = NoteElement.getPrototype();
    ClassElement anyumlnode = ClassElement.getPrototype();
    assertTrue(note.acceptsConnection(RelationType.NOTE_CONNECTOR,
      RelationEndType.UNSPECIFIED, anyumlnode));
    assertTrue(note.acceptsConnection(RelationType.NOTE_CONNECTOR,
      RelationEndType.SOURCE, anyumlnode));
    assertTrue(note.acceptsConnection(RelationType.NOTE_CONNECTOR,
      RelationEndType.TARGET, anyumlnode));
    assertFalse(note.acceptsConnection(RelationType.DEPENDENCY,
      RelationEndType.UNSPECIFIED, anyumlnode));
  }
  
  /**
   * Tests the NoteConnection class.
   */
  public void testNoteConnection() {
    // relation has no effect
    NoteConnection connection = NoteConnection.getPrototype();
    connection.setRelation(new UmlRelation());
    assertNull(connection.getModelElement());
  }
}
