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
import org.tinyuml.ui.diagram.commands.EditConnectionPointsCommand;

/**
 * Tests the EditConnectionPointsCommand class.
 * @author Wei-ju Wu
 * @version 1.0
 */
public class EditConnectionPointsCommandTest extends MockObjectTestCase {

  private EditConnectionPointsCommand command;
  private List<Point2D> newpoints = new ArrayList<Point2D>();
  private List<Point2D> oldpoints = new ArrayList<Point2D>();
  private Mock mockConnection = mock(Connection.class);
  private Mock mockNotification = mock(DiagramEditorNotification.class);

  /**
   * {@inheritDoc}
   */
  @Override
  protected void setUp() throws Exception {
    newpoints.add(new Point2D.Double(100.0, 105.0));
    newpoints.add(new Point2D.Double(150.0, 125.0));
    oldpoints.add(new Point2D.Double(10.0, 15.0));
    oldpoints.add(new Point2D.Double(15.0, 25.0));
    command = new EditConnectionPointsCommand((DiagramEditorNotification)
      mockNotification.proxy(), (Connection) mockConnection.proxy(),
      newpoints);
  }
  
  /**
   * Tests the run()/undo()/redo() methods.
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
   * Sets the expectations for run().
   */
  private void setRunExpectations() {
    mockConnection.expects(once()).method("getPoints")
      .will(returnValue(oldpoints));
    mockConnection.expects(once()).method("setPoints")
      .with(eq(newpoints));
    mockNotification.expects(once()).method("notifyElementsMoved");
  }
  
  /**
   * Sets the expectations for undo().
   */
  private void setUndoExpectations() {
    mockConnection.expects(once()).method("setPoints")
      .with(eq(oldpoints));
    mockNotification.expects(once()).method("notifyElementsMoved");    
  }
}
