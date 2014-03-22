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

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import org.jmock.Mock;
import org.jmock.cglib.MockObjectTestCase;
import org.tinyuml.draw.DoubleDimension;
import org.tinyuml.draw.Node;
import org.tinyuml.ui.diagram.commands.ResizeElementCommand;
import org.tinyuml.ui.diagram.commands.DiagramEditorNotification;

/**
 * Test class for the ResizeElementCommand.
 * @author Wei-ju Wu
 * @version 1.0
 */
public class ResizeElementCommandTest extends MockObjectTestCase {
  private Mock mockNotification = mock(DiagramEditorNotification.class);
  private Mock mockNode = mock(Node.class);
  private Point2D newpos = new Point2D.Double(3.0, 4.0);
  private Dimension2D newsize = new DoubleDimension(12, 15);
  private ResizeElementCommand command;
  private double oldx = 2.0, oldy = 12.0;
  private double oldwidth = 100.0, oldheight = 55.0;
  private Dimension2D oldsize = new DoubleDimension(oldwidth, oldheight);

  /**
   * {@inheritDoc}
   */
  @Override
  protected void setUp() {
    mockNode.expects(once()).method("getAbsoluteX1").will(returnValue(oldx));
    mockNode.expects(once()).method("getAbsoluteY1").will(returnValue(oldy));
    mockNode.expects(atLeastOnce()).method("getSize").
      will(returnValue(oldsize));
    command = new ResizeElementCommand((DiagramEditorNotification)
      mockNotification.proxy(), (Node) mockNode.proxy(), newpos, newsize);
  }
  
  /**
   * Tests the run()/undo()/redo() method.
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
    mockNode.expects(once()).method("setAbsolutePos").with(eq(newpos.getX()),
      eq(newpos.getY()));
    mockNode.expects(once()).method("setSize").with(eq(newsize.getWidth()),
      eq(newsize.getHeight()));
    mockNotification.expects(once()).method("notifyElementResized").
      with(eq(mockNode.proxy()));    
  }
  
  /**
   * Sets the expectations for undo().
   */
  private void setUndoExpectations() {
    mockNode.expects(once()).method("setAbsolutePos").with(eq(oldx), eq(oldy));
    mockNode.expects(once()).method("setSize").with(eq(oldwidth),
      eq(oldheight));
    mockNotification.expects(once()).method("notifyElementResized").
      with(eq(mockNode.proxy()));    
  }  
}
