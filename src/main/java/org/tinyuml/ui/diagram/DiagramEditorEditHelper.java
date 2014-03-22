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
package org.tinyuml.ui.diagram;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.event.UndoableEditListener;
import org.tinyuml.draw.DrawingContext.FontType;
import org.tinyuml.draw.Label;
import org.tinyuml.draw.MultiLineLabel;
import org.tinyuml.ui.diagram.commands.SetLabelTextCommand;

/**
 * This class relieves the DiagramEditor class of some responsibilities
 * in order to keep it at reasonable size.
 *
 * @author Wei-ju Wu
 * @version 1.0
 */
public class DiagramEditorEditHelper
implements ActionListener, MouseListener, MouseMotionListener {

  // MouseEvent wrapper
  private transient EditorMouseEvent mouseEvent = new EditorMouseEvent();
  private DiagramEditor diagramEditor;
  /**
   * To edit the captions in the diagram.
   */
  private CaptionEditor captionEditor = new CaptionEditor();
  private MultilineEditor multilineEditor = new MultilineEditor();

  /**
   * Constructor.
   * @param editor the DiagramEditor this helper object belongs to
   */
  public DiagramEditorEditHelper(DiagramEditor editor) {
    diagramEditor = editor;
    diagramEditor.add(captionEditor);
    diagramEditor.add(multilineEditor);

    // Editor listeners
    captionEditor.addActionListener(this);
  }

  // ************************************************************************
  // ***** Label editing
  // *********************************

  /**
   * Adds the specified UndoableEditListener.
   * @param l the UndoableEditListener to add
   */
  public void addUndoableEditListener(UndoableEditListener l) {
    captionEditor.getDocument().addUndoableEditListener(l);
    multilineEditor.getDocument().addUndoableEditListener(l);
  }

  /**
   * Open an editor for the specified Label object.
   * @param label the Label object
   */
  public void editLabel(Label label) {
    if (label != null) {
      if (label instanceof MultiLineLabel) {
        multilineEditor.setFont(diagramEditor.getDrawingContext().getFont(
          FontType.DEFAULT));
        multilineEditor.showEditor(label, diagramEditor.getGraphics());
      } else {
        captionEditor.showEditor(label, diagramEditor.getGraphics());
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  public void actionPerformed(ActionEvent e) {
    stopEditing();
  }

  /**
   * Stops the editing process if one was active.
   * @return true if editor was closed, false if nothing happened
   */
  private boolean stopEditing() {
    TextEditor currentEditor = null;
    if (captionEditor.isVisible()) {
      currentEditor = captionEditor;
    }
    if (multilineEditor.isVisible()) {
      currentEditor = multilineEditor;
    }
    if (currentEditor != null && currentEditor.isVisible()) {
      String text = currentEditor.getText();
      Label label = currentEditor.getLabel();
      SetLabelTextCommand command = new SetLabelTextCommand(label, text);
      diagramEditor.execute(command);
      currentEditor.hideEditor();
      diagramEditor.repaint();
      return true;
    }
    return false;
  }

  /**
   * Cancels the current edit action.
   */
  protected void cancelEditing() {
    if (captionEditor.isVisible()) {
      captionEditor.hideEditor();
    }
  }

  // ************************************************************************
  // ***** MouseListener
  // *********************************

  /**
   * {@inheritDoc}
   */
  public void mousePressed(MouseEvent e) {
   diagramEditor.requestFocusInWindow();
    if (!stopEditing()) {
      diagramEditor.getEditorMode().mousePressed(convertMouseEvent(e));
    }
  }

  /**
   * {@inheritDoc}
   */
  public void mouseReleased(MouseEvent e) {
    if (!stopEditing()) {
      diagramEditor.getEditorMode().mouseReleased(convertMouseEvent(e));
    }
  }

  /**
   * {@inheritDoc}
   */
  public void mouseClicked(MouseEvent e) {
    if (!stopEditing()) {
      diagramEditor.getEditorMode().mouseClicked(convertMouseEvent(e));
    }
  }

  // ************************************************************************
  // ***** MouseMotionListener
  // *********************************

  /**
   * {@inheritDoc}
   */
  public void mouseExited(MouseEvent e) { }

  /**
   * {@inheritDoc}
   */
  public void mouseEntered(MouseEvent e) { }

  /**
   * {@inheritDoc}
   */
  public void mouseMoved(MouseEvent e) {
    EditorMouseEvent evt = convertMouseEvent(e);
    diagramEditor.getEditorMode().mouseMoved(evt);
    notifyCoordinateListeners();
  }

  /**
   * {@inheritDoc}
   */
  public void mouseDragged(MouseEvent e) {
    EditorMouseEvent evt = convertMouseEvent(e);
    diagramEditor.getEditorMode().mouseDragged(evt);
    notifyCoordinateListeners();
  }

  /**
   * Notifies the coordinate listeners.
   * Precondition: Mouse coordinates have been previously transformed.
   */
  private void notifyCoordinateListeners() {
    for (EditorStateListener l : diagramEditor.getEditorListeners()) {
      l.mouseMoved(mouseEvent);
    }
  }

  /**
   * Converts the java.awt.MouseEvent into an EditorMouseEvent.
   * @param e the MouseEvent
   * @return the converted event
   */
  private EditorMouseEvent convertMouseEvent(MouseEvent e) {
    mouseEvent.setMouseEvent(e, diagramEditor.getScaling());
    return mouseEvent;
  }
}
