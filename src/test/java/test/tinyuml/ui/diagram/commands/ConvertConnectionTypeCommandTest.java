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
import org.tinyuml.draw.Connection;
import org.tinyuml.ui.diagram.commands.ConvertConnectionTypeCommand;
import org.tinyuml.ui.diagram.commands.DiagramEditorNotification;
import org.tinyuml.umldraw.shared.UmlConnection;

/**
 * Tests the ConvertConnectionTypeCommand class.
 * @author Wei-ju Wu
 * @version 1.0
 */
public class ConvertConnectionTypeCommandTest extends MockObjectTestCase {

  private Mock mockNotification = mock(DiagramEditorNotification.class);
  private Mock mockUmlConnection = mock(UmlConnection.class);
  private Mock mockOldConnection = mock(Connection.class);
  private Mock mockNewConnection = mock(Connection.class);
  private ConvertConnectionTypeCommand command;
  
  /**
   * {@inheritDoc}
   */
  @Override
  protected void setUp() {
    command = new ConvertConnectionTypeCommand((DiagramEditorNotification)
      mockNotification.proxy(), (UmlConnection) mockUmlConnection.proxy(),
      (Connection) mockNewConnection.proxy());
  }
  
  /**
   * Tests the run()/undo()/redo() commands.
   */
  public void testRunUndoRedo() {
    setRunExpectations();
    command.run();
    setUndoExpectations();
    command.undo();
    setRunExpectations();
    command.redo();
  }
  
  /**
   * Sets the run() expectations.
   */
  private void setRunExpectations() {
    mockUmlConnection.expects(once()).method("getConnection")
      .will(returnValue(mockOldConnection.proxy()));
    mockNewConnection.expects(once()).method("copyData")
      .with(eq(mockOldConnection.proxy()));
    mockUmlConnection.expects(once()).method("setConnection")
      .with(eq(mockNewConnection.proxy()));
    mockNotification.expects(once()).method("notifyElementsMoved");
  }
  
  /**
   * Sets the undo() expectations.
   */
  private void setUndoExpectations() {
    mockUmlConnection.expects(once()).method("setConnection")
      .with(eq(mockOldConnection.proxy()));
    mockNotification.expects(once()).method("notifyElementsMoved");
  }
}
