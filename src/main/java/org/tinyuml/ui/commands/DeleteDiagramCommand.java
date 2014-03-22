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
package org.tinyuml.ui.commands;

import javax.swing.undo.AbstractUndoableEdit;
import org.tinyuml.model.UmlDiagram;
import org.tinyuml.model.UmlModel;
import org.tinyuml.util.Command;

/**
 * An undoable command that removes diagrams from a model.
 *
 * @author Wei-ju Wu
 * @version 1.0
 */
public class DeleteDiagramCommand extends AbstractUndoableEdit
implements Command {

  private UmlModel model;
  private UmlDiagram diagram;

  /**
   * Constructor.
   * @param aModel the model
   * @param aDiagram the diagram
   */
  public DeleteDiagramCommand(UmlModel aModel, UmlDiagram aDiagram) {
    model = aModel;
    diagram = aDiagram;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void undo() {
    super.undo();
    model.addDiagram(diagram);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void redo() {
    super.redo();
    run();
  }

  /**
   * {@inheritDoc}
   */
  public void run() {
    model.removeDiagram(diagram);
  }
}
