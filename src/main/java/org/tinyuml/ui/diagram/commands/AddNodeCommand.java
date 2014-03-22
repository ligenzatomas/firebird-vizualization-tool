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
package org.tinyuml.ui.diagram.commands;

import javax.swing.undo.AbstractUndoableEdit;
import org.tinyuml.draw.CompositeElement;
import org.tinyuml.draw.Node;
import org.tinyuml.util.Command;

/**
 * This class implements a command to add nodes. It is introduced, because
 * AddElementCommand can not handle setting positions with nesting very
 * well.
 *
 * @author Wei-ju Wu
 * @version 1.0
 */
public class AddNodeCommand extends AbstractUndoableEdit implements Command {
  private DiagramEditorNotification notification;
  private Node node;
  private CompositeElement parent;
  private double absx, absy;

  /**
   * Constructor.
   * @param editorNotification a DiagramEditorNotification object
   * @param parent the parent component
   * @param aNode the created node
   * @param x the absolute x position
   * @param y the absolute y position
   */
  public AddNodeCommand(DiagramEditorNotification editorNotification,
    CompositeElement parent, Node aNode, double x, double y) {
    this.parent = parent;
    node = aNode;
    absx = x;
    absy = y;
    notification = editorNotification;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void undo() {
    super.undo();
    parent.removeChild(node);
    notification.notifyElementRemoved(node);
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
    parent.addChild(node);
    node.setAbsolutePos(absx, absy);
    notification.notifyElementAdded(node);
  }
}
