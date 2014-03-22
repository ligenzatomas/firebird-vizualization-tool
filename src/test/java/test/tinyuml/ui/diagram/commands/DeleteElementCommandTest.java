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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.jmock.Mock;
import org.jmock.cglib.MockObjectTestCase;
import org.tinyuml.draw.CompositeNode;
import org.tinyuml.draw.Connection;
import org.tinyuml.draw.DiagramElement;
import org.tinyuml.draw.Node;
import org.tinyuml.ui.diagram.commands.DeleteElementCommand;
import org.tinyuml.ui.diagram.commands.DiagramEditorNotification;

/**
 * Test class for DeleteElementCommand.
 * @author Wei-ju Wu
 * @version 1.0
 */
public class DeleteElementCommandTest extends MockObjectTestCase {
  private Mock mockNotification = mock(DiagramEditorNotification.class);
  private Mock mockNode1 = mock(Node.class), mockNode2 = mock(Node.class);
  private Mock mockConnection = mock(Connection.class);
  private Mock mockParent = mock(CompositeNode.class);
  private DeleteElementCommand removeNodeCommand, removeConnectionCommand;
  private Collection<Connection> connections = new ArrayList<Connection>();

  /**
   * {@inheritDoc}
   */
  @Override
  protected void setUp() {
    mockNode1.expects(once()).method("getParent").
      will(returnValue(mockParent.proxy()));
    mockConnection.expects(once()).method("getParent").
      will(returnValue(mockParent.proxy()));
    List<DiagramElement> nodesToRemove = new ArrayList<DiagramElement>();
    nodesToRemove.add((DiagramElement) mockNode1.proxy());
    
    List<DiagramElement> connectionsToRemove = new ArrayList<DiagramElement>();
    connectionsToRemove.add((DiagramElement) mockConnection.proxy());
    removeNodeCommand = new DeleteElementCommand((DiagramEditorNotification)
      mockNotification.proxy(), nodesToRemove);
    removeConnectionCommand = new DeleteElementCommand(
      (DiagramEditorNotification) mockNotification.proxy(),
      connectionsToRemove);
    connections.add((Connection) mockConnection.proxy());
  }

  /**
   * Tests the run()/undo()/redo() method on removing a node.
   */
  public void testRunUndoRedoRemoveNode() {
    setRunRemoveNodeExpectations();
    removeNodeCommand.run();
    setUndoRemoveNodeExpectations();
    removeNodeCommand.undo();
    setRunRemoveNodeExpectations();
    removeNodeCommand.redo();
  }
  
  /**
   * Sets the run remove node expectations.
   */
  private void setRunRemoveNodeExpectations() {
    mockNode1.expects(once()).method("getParent")
      .will(returnValue(mockParent.proxy()));

    // detach connections
    mockNode1.expects(once()).method("getConnections")
      .will(returnValue(connections));
    mockConnection.expects(atLeastOnce()).method("getNode1")
      .will(returnValue(mockNode1.proxy()));
    mockConnection.expects(atLeastOnce()).method("getNode2")
      .will(returnValue(mockNode2.proxy()));
    mockNode2.expects(once()).method("removeConnection")
      .with(eq(mockConnection.proxy()));
    mockConnection.expects(once()).method("getParent")
      .will(returnValue(mockParent.proxy()));
    mockParent.expects(once()).method("removeChild")
      .with(eq(mockConnection.proxy()));

    // remove the element
    mockParent.expects(once()).method("removeChild").
      with(eq(mockNode1.proxy()));
    mockNotification.expects(once()).method("notifyElementRemoved").
      with(eq(mockNode1.proxy()));    
  }

  /**
   * Sets the undo remove node expecations.
   */
  private void setUndoRemoveNodeExpectations() {
    // reattach the connections
    mockNode1.expects(once()).method("getConnections")
      .will(returnValue(connections));
    mockConnection.expects(atLeastOnce()).method("getNode1")
      .will(returnValue(mockNode1.proxy()));
    mockConnection.expects(atLeastOnce()).method("getNode2")
      .will(returnValue(mockNode2.proxy()));
    mockNode2.expects(once()).method("addConnection")
      .with(eq(mockConnection.proxy()));
    mockConnection.expects(once()).method("getParent")
      .will(returnValue(mockParent.proxy()));
    mockParent.expects(once()).method("addChild")
      .with(eq(mockConnection.proxy()));
    
    // readd the element
    mockParent.expects(once()).method("addChild").
      with(eq(mockNode1.proxy()));
    mockNotification.expects(once()).method("notifyElementAdded").
      with(eq(mockNode1.proxy()));    
  }
  
  /**
   * Tests the run()/undo()/redo() method for removing a connection.
   */
  public void testRedoRemoveConnection() {
    setRunRemoveConnectionExpectations();
    removeConnectionCommand.run();
    setUndoRemoveConnectionExpectations();
    removeConnectionCommand.undo();    
    setRunRemoveConnectionExpectations();
    removeConnectionCommand.redo();
  }
  /**
   * Sets the expectations on run() for removing a Connection.
   */
  private void setRunRemoveConnectionExpectations() {
    // detach connections
    mockConnection.expects(atLeastOnce()).method("getNode1")
      .will(returnValue(mockNode1.proxy()));
    mockConnection.expects(atLeastOnce()).method("getNode2")
      .will(returnValue(mockNode2.proxy()));
    mockNode1.expects(once()).method("removeConnection")
      .with(eq(mockConnection.proxy()));
    mockNode2.expects(once()).method("removeConnection")
      .with(eq(mockConnection.proxy()));
    mockConnection.expects(once()).method("getParent")
      .will(returnValue(mockParent.proxy()));

    // remove the element
    mockParent.expects(once()).method("removeChild").
      with(eq(mockConnection.proxy()));
    mockNotification.expects(once()).method("notifyElementRemoved").
      with(eq(mockConnection.proxy()));    
  }

  /**
   * Sets the expectations for undoing a remove of a Connection.
   */
  private void setUndoRemoveConnectionExpectations() {
    // detach connections
    mockConnection.expects(atLeastOnce()).method("getNode1")
      .will(returnValue(mockNode1.proxy()));
    mockConnection.expects(atLeastOnce()).method("getNode2")
      .will(returnValue(mockNode2.proxy()));
    mockNode1.expects(once()).method("addConnection")
      .with(eq(mockConnection.proxy()));
    mockNode2.expects(once()).method("addConnection")
      .with(eq(mockConnection.proxy()));

    // remove the element
    mockParent.expects(once()).method("addChild").
      with(eq(mockConnection.proxy()));
    mockNotification.expects(once()).method("notifyElementAdded").
      with(eq(mockConnection.proxy()));    
  }  
}
