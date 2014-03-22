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
package test.tinyuml.ui.commands;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import org.tinyuml.model.UmlDiagram;
import org.tinyuml.model.UmlModel;
import org.tinyuml.ui.commands.DeleteDiagramCommand;

/**
 * A test class for DeleteDiagramCommand.
 * @author Wei-ju Wu
 * @version 1.0
 */
public class DeleteDiagramCommandTest extends MockObjectTestCase {

  private Mock mockUmlModel = mock(UmlModel.class);
  private Mock mockUmlDiagram = mock(UmlDiagram.class);
  private DeleteDiagramCommand deleteCommand;

  /**
   * {@inheritDoc}
   */
  @Override
  protected void setUp() throws Exception {
    deleteCommand = new DeleteDiagramCommand((UmlModel) mockUmlModel.proxy(),
      (UmlDiagram) mockUmlDiagram.proxy());
  }
  
  /**
   * Tests run, undo and redo.
   */
  public void testRunUndoRedo() {
    mockUmlModel.expects(once()).method("removeDiagram")
      .with(eq(mockUmlDiagram.proxy()));
    deleteCommand.run();
    mockUmlModel.expects(once()).method("addDiagram")
      .with(eq(mockUmlDiagram.proxy()));
    deleteCommand.undo();
    mockUmlModel.expects(once()).method("removeDiagram")
      .with(eq(mockUmlDiagram.proxy()));
    deleteCommand.redo();
  }
}
