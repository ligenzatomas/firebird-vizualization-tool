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
import org.tinyuml.model.Relation;
import org.tinyuml.model.RelationEndType;
import org.tinyuml.ui.diagram.commands.DiagramEditorNotification;
import org.tinyuml.ui.diagram.commands.SetConnectionNavigabilityCommand;
import org.tinyuml.umldraw.shared.UmlConnection;

/**
 *
 * @author Wei-ju Wu
 * @version 1.0
 */
public class SetConnectionNavigabilityCommandTest extends MockObjectTestCase {

  private Mock mockEditorNotification = mock(DiagramEditorNotification.class);
  private SetConnectionNavigabilityCommand sourceCommand, targetCommand;
  private Mock mockUmlConnection = mock(UmlConnection.class);
  private Mock mockRelation = mock(Relation.class);

  /**
   * {@inheritDoc}
   */
  @Override
  protected void setUp() {
    sourceCommand = new SetConnectionNavigabilityCommand(
      (DiagramEditorNotification) mockEditorNotification.proxy(),
      (UmlConnection) mockUmlConnection.proxy(),
      RelationEndType.SOURCE, true);
    targetCommand = new SetConnectionNavigabilityCommand(
      (DiagramEditorNotification) mockEditorNotification.proxy(),
      (UmlConnection) mockUmlConnection.proxy(),
      RelationEndType.TARGET, true);
  }
  
  /**
   * Tests run()/undo()/redo() on the source.
   */
  public void testRunUndoRedoSource() {
    mockUmlConnection.expects(atLeastOnce()).method("getModelElement")
      .will(returnValue(mockRelation.proxy()));
    
    setRunSourceExpectations();
    sourceCommand.run();
    setUndoSourceExpectations();
    sourceCommand.undo();
    setRunSourceExpectations();
    sourceCommand.redo();
  }
  
  /**
   * Sets the run() expectations for the source.
   */
  private void setRunSourceExpectations() {
    mockRelation.expects(once()).method("isNavigableToElement1")
      .will(returnValue(false));
    mockRelation.expects(once()).method("setNavigableToElement1")
      .with(eq(true));
    mockEditorNotification.expects(once()).method("notifyElementsMoved");    
  }
  
  /**
   * Sets the undo() expectations for the target.
   */
  private void setUndoSourceExpectations() {
    mockRelation.expects(once()).method("setNavigableToElement1")
      .with(eq(false));
    mockEditorNotification.expects(once()).method("notifyElementsMoved");
  }
  
  /**
   * Tests run()/undo()/redo() on the target.
   */
  public void testRunUndoRedoTarget() {    
    mockUmlConnection.expects(atLeastOnce()).method("getModelElement")
      .will(returnValue(mockRelation.proxy()));
    
    setRunTargetExpectations();
    targetCommand.run();
    setUndoTargetExpectations();
    targetCommand.undo();
    setRunTargetExpectations();
    targetCommand.redo();
  }
  
  /**
   * Sets the run() expectations for the target.
   */
  private void setRunTargetExpectations() {
    mockRelation.expects(once()).method("isNavigableToElement2")
      .will(returnValue(false));
    mockRelation.expects(once()).method("setNavigableToElement2")
      .with(eq(true));
    mockEditorNotification.expects(once()).method("notifyElementsMoved");    
  }

  /**
   * Sets the undo() expectations for the target.
   */
  private void setUndoTargetExpectations() {
    mockRelation.expects(once()).method("setNavigableToElement2")
      .with(eq(false));
    mockEditorNotification.expects(once()).method("notifyElementsMoved");
  }
}
