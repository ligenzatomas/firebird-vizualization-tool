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

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import org.jmock.Mock;
import org.jmock.cglib.MockObjectTestCase;
import org.tinyuml.draw.Connection;
import org.tinyuml.ui.diagram.commands.DiagramEditorNotification;
import org.tinyuml.ui.diagram.commands.ResetConnectionPointsCommand;

/**
 * Test for the ResetConnectionPointsCommand.
 * @author Wei-ju Wu
 * @version 1.0
 */
public class ResetConnectionPointsCommandTest extends MockObjectTestCase {

  private Mock mockNotification = mock(DiagramEditorNotification.class);
  private Mock mockConnection = mock(Connection.class);
  private ResetConnectionPointsCommand command;
  private List<Point2D> originalPoints = new ArrayList<Point2D>();

  /**
   * {@inheritDoc}
   */
  @Override
  protected void setUp() {
    command = new ResetConnectionPointsCommand((DiagramEditorNotification)
      mockNotification.proxy(), (Connection) mockConnection.proxy());
    originalPoints.add(new Point2D.Double(1.0, 2.0));
    originalPoints.add(new Point2D.Double(5.0, 6.0));
  }
  
  /**
   * Tests the run()/undo()/redo() command.
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
   * Expectations for the run() method.
   */
  private void setRunExpectations() {
    mockConnection.expects(once()).method("getPoints")
      .will(returnValue(originalPoints));
    mockConnection.expects(once()).method("resetPoints");
    mockNotification.expects(once()).method("notifyElementsMoved");
  }
  
  /**
   * Expecations for the undo() method.
   */
  private void setUndoExpectations() {
    mockConnection.expects(once()).method("setPoints")
      .with(eq(originalPoints));
    mockNotification.expects(once()).method("notifyElementsMoved");    
  }
}
