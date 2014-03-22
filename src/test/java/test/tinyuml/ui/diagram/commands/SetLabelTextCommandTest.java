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
import org.tinyuml.draw.CompositeNode;
import org.tinyuml.draw.DrawingContext;
import org.tinyuml.draw.Label;
import org.tinyuml.ui.diagram.commands.SetLabelTextCommand;

/**
 *
 * @author Wei-ju Wu
 * @version 1.0
 */
public class SetLabelTextCommandTest extends MockObjectTestCase {

  private Mock mockParent = mock(CompositeNode.class);
  private Mock mockLabel = mock(Label.class);
  private SetLabelTextCommand command;
  private String text = "newtext";
  private String originaltext = "original";

  /**
   * {@inheritDoc}
   */
  @Override
  protected void setUp() {
    mockLabel.expects(once()).method("getText").will(returnValue(originaltext));
    command = new SetLabelTextCommand((Label) mockLabel.proxy(), text);
  }

  /**
   * Tests the run()/undo()/redo() method.
   */
  public void testRunUndoRedo() {
    mockLabel.expects(once()).method("setText").with(eq(text));
    command.run();
    mockLabel.expects(once()).method("setText").with(eq(originaltext));
    command.undo();    
    mockLabel.expects(once()).method("setText").with(eq(text));
    command.redo();
  }
}
