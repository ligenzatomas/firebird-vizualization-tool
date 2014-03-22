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
import org.tinyuml.draw.CompositeNode;
import org.tinyuml.draw.Connection;
import org.tinyuml.draw.MoveNodeOperation;
import org.tinyuml.draw.Node;
import org.tinyuml.draw.TranslateConnectionOperation;
import org.tinyuml.ui.diagram.commands.DiagramEditorNotification;
import org.tinyuml.ui.diagram.commands.MoveElementCommand;
import org.tinyuml.util.Command;

/**
 * A test class for MoveElementCommand.
 * @author Wei-ju Wu
 * @version 1.0
 */
public class MoveElementCommandTest extends MockObjectTestCase {

  private Mock mockNode = mock(Node.class);
  private Mock mockConn = mock(Connection.class);
  private Mock mockNewParent = mock(CompositeNode.class);
  private Mock mockOldParent = mock(CompositeNode.class);
  private Mock mockNotification = mock(DiagramEditorNotification.class);
  private double oldx = 100.0, oldy = 120.0;
  private double absx = 10.0, absy = 20.0;
  private double transx = 30.0, transy = 40.0;
  private List<Point2D> originalPoints = new ArrayList<Point2D>();
  private List<Point2D> translatedPoints = new ArrayList<Point2D>();
  private MoveElementCommand command;

  /**
   * {@inheritDoc}
   */
  @Override
  protected void setUp() {
    originalPoints.add(new Point2D.Double(50.0, 50.0));
    originalPoints.add(new Point2D.Double(100.0, 100.0));
    translatedPoints.add(new Point2D.Double(50.0 + transx, 50.0 + transy));
    translatedPoints.add(new Point2D.Double(100.0 + transx, 100.0 + transy));
    
    mockNode.expects(once()).method("getAbsoluteX1").will(returnValue(oldx));
    mockNode.expects(once()).method("getAbsoluteY1").will(returnValue(oldy));
    mockNode.expects(atLeastOnce()).method("getParent")
      .will(returnValue(mockOldParent.proxy()));    
    MoveNodeOperation op1 = new MoveNodeOperation((Node) mockNode.proxy(),
      (CompositeNode) mockNewParent.proxy(), new Point2D.Double(absx, absy));
    TranslateConnectionOperation op2 = new TranslateConnectionOperation(
      (Connection) mockConn.proxy(), transx, transy);
    command = new MoveElementCommand((DiagramEditorNotification)
      mockNotification.proxy(), new Command[] {op1, op2});
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
    // expectations
    mockOldParent.expects(once()).method("removeChild")
      .with(eq(mockNode.proxy()));
    mockNewParent.expects(once()).method("addChild")
      .with(eq(mockNode.proxy()));
    mockNode.expects(once()).method("setAbsolutePos").
      with(eq(absx), eq(absy));
    mockConn.expects(once()).method("getPoints")
      .will(returnValue(originalPoints));
    mockConn.expects(once()).method("setPoints")
      .with(eq(translatedPoints));
    mockNotification.expects(once()).method("notifyElementsMoved");    
  }
  
  /**
   * Sets the expectations for undo().
   */
  private void setUndoExpectations() {
    // expectations
    mockNode.expects(once()).method("setAbsolutePos")
      .with(eq(oldx), eq(oldy));
    mockNewParent.expects(once()).method("removeChild")
      .with(eq(mockNode.proxy()));
    mockOldParent.expects(once()).method("addChild")
      .with(eq(mockNode.proxy()));
    mockConn.expects(once()).method("setPoints")
      .with(eq(originalPoints));
    mockNotification.expects(once()).method("notifyElementsMoved");
  }}
