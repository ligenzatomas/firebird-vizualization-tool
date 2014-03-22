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

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import javax.swing.undo.AbstractUndoableEdit;
import org.tinyuml.draw.DoubleDimension;
import org.tinyuml.draw.Node;
import org.tinyuml.util.Command;

/**
 * This class implements a resizing command.
 *
 * @author Wei-ju Wu
 * @version 1.0
 */
public class ResizeElementCommand extends AbstractUndoableEdit
implements Command {

  private DiagramEditorNotification notification;
  private Node element;
  private Point2D newpos, oldpos = new Point2D.Double();
  private Dimension2D newsize, oldsize = new DoubleDimension();

  /**
   * Constructor.
   * @param aNotification the DiagramEditorNotification object
   * @param anElement the element to resize
   * @param aNewPos the new position
   * @param aNewSize the new size
   */
  public ResizeElementCommand(DiagramEditorNotification aNotification,
    Node anElement, Point2D aNewPos, Dimension2D aNewSize) {
    notification = aNotification;
    element = anElement;
    newpos = aNewPos;
    newsize = aNewSize;
    oldpos.setLocation(element.getAbsoluteX1(), element.getAbsoluteY1());
    oldsize.setSize(element.getSize());
  }

  /**
   * {@inheritDoc}
   */
  public void run() {
    element.setAbsolutePos(newpos.getX(), newpos.getY());
    element.setSize(newsize.getWidth(), newsize.getHeight());
    notification.notifyElementResized(element);
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
  @Override
  public void undo() {
    super.undo();
    element.setAbsolutePos(oldpos.getX(), oldpos.getY());
    element.setSize(oldsize.getWidth(), oldsize.getHeight());
    notification.notifyElementResized(element);
  }
}
