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
package test.tinyuml.ui.diagram.commands;

import org.jmock.Mock;
import org.jmock.cglib.MockObjectTestCase;
import org.tinyuml.draw.CompositeElement;
import org.tinyuml.draw.DiagramElement;
import org.tinyuml.ui.diagram.commands.AddConnectionCommand;
import org.tinyuml.ui.diagram.commands.DiagramEditorNotification;

/**
 *
 * @author Wei-ju Wu
 * @version 1.0
 */
public class AddConnectionCommandTest extends MockObjectTestCase {

  private Mock mockNotification = mock(DiagramEditorNotification.class);
  private Mock mockParent = mock(CompositeElement.class);
  private Mock mockElement = mock(DiagramElement.class);
  private CompositeElement parent = (CompositeElement) mockParent.proxy();
  private DiagramElement element = (DiagramElement) mockElement.proxy();
  private AddConnectionCommand command = new AddConnectionCommand(
    (DiagramEditorNotification) mockNotification.proxy(), parent, element);

  /**
   * Tests the run() method.
   */
  public void testRun() {
    setRunExpectations();
    command.run();
  }
  
  /**
   * Tests the redo() method.
   */
  public void testRedo() {
    setRunExpectations();
    command.run();
    setUndoExpectations();
    command.undo();
    setRunExpectations();
    command.redo();
  }
  
  /**
   * Run expectations.
   */
  private void setRunExpectations() {
    // expectations
    mockNotification.expects(once()).method("notifyElementAdded").
      with(eq(element));
    mockParent.expects(once()).method("addChild").
      with(eq(element));    
  }
  
  /**
   * Tests the undo() method.
   */
  public void testUndo() {
    setUndoExpectations();
    command.undo();
  }
  
  /**
   * Sets the expectations for undo().
   */
  private void setUndoExpectations() {
    // expectations
    mockNotification.expects(once()).method("notifyElementRemoved").
      with(eq(element));
    mockParent.expects(once()).method("removeChild").
      with(eq(element));    
  }
}
