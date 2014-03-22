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

import java.awt.Cursor;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.JPopupMenu;
import org.tinyuml.draw.DiagramElement;
import org.tinyuml.draw.DrawingContext;
import org.tinyuml.draw.Label;
import org.tinyuml.draw.MultiSelection;
import org.tinyuml.draw.NullElement;
import org.tinyuml.draw.NullSelection;
import org.tinyuml.draw.RubberbandSelector;
import org.tinyuml.draw.Selection;
import org.tinyuml.util.AppCommandListener;
import org.tinyuml.umldraw.shared.UmlDiagramElement;

/**
 * This class handles selections of diagram elements, this includes all
 * non-line elements and multiple selections. It also handles displaying
 * the context menu.
 *
 * @author Wei-ju Wu
 * @version 1.0
 */
public class SelectionHandler implements EditorMode {

  private DiagramEditor editor;
  private Selection currentSelection = NullSelection.getInstance();
  private Set<SelectionListener> listeners =
    new HashSet<SelectionListener>();
  private ContextMenuBuilder menubuilder = new ContextMenuBuilder();

  /**
   * The selector for rubber band selection.
   */
  private RubberbandSelector selector = new RubberbandSelector();

  /**
   * Constructor.
   * @param anEditor the editor
   */
  public SelectionHandler(DiagramEditor anEditor) {
    editor = anEditor;
    selector.setDiagram(editor.getDiagram());
  }

  /**
   * Deselects all elements.
   */
  public void deselectAll() { currentSelection = NullSelection.getInstance(); }

  /**
   * {@inheritDoc}
   */
  public void mouseClicked(EditorMouseEvent e) {
    if (e.isMainButton()) {
      handleSelectionOnMouseClicked(e);
    }
  }

  /**
   * Handles the selection on a mouseClicked event.
   * @param e the EditorMouseEvent
   */
  private void handleSelectionOnMouseClicked(EditorMouseEvent e) {
    double mx = e.getX(), my = e.getY();
    // this is a pretty ugly cast, it is needed in order to use the getLabel()
    // method which is not a base DiagramElement method
    DiagramElement previousSelected = currentSelection.getElement();
    DiagramElement element = editor.getDiagram().getChildAt(mx, my);
    if (element instanceof UmlDiagramElement && previousSelected == element) {
      Label label = element.getLabelAt(mx, my);
      if (label != null) {
        editor.editLabel(label);
      } else if (e.getClickCount() >= 2) {
        editor.editProperties(element);
      }
    } else if (editor.getDiagram().getLabelAt(mx, my) != null) {
      // Edit the diagram name
      editor.editLabel(editor.getDiagram().getLabelAt(mx, my));
    } else {
      if (element == NullElement.getInstance()) {
        element = editor.getDiagram();
      }
      currentSelection = element.getSelection(editor);
    }
    editor.redraw();
    notifyListeners();
  }

  /**
   * {@inheritDoc}
   */
  public void mousePressed(EditorMouseEvent e) {
    handleSelectionOnMousePress(e);
    if (e.isPopupTrigger()) {
      displayContextMenu(e);
    }
  }

  /**
   * Displays the context menu.
   * @param e the EditorMouseEvent
   */
  private void displayContextMenu(EditorMouseEvent e) {
    double mx = e.getX(), my = e.getY();
    Selection selection = getSelection(mx, my);
    if (!nothingSelected()) {
      JPopupMenu menu = menubuilder.createContextMenu(selection);
      menu.show(editor, e.getMouseEvent().getX(), e.getMouseEvent().getY());
    }
  }

  /**
   * Handle the selection on a mousePressed event.
   * @param e the EditorMouseEvent
   */
  private void handleSelectionOnMousePress(EditorMouseEvent e) {
    double mx = e.getX(), my = e.getY();
    currentSelection = getSelection(mx, my);
    // Dragging only if left mouse button was pressed
    if (e.isMainButton()) {
      if (nothingSelected() && editor.getDiagram().contains(mx, my)) {
        currentSelection = selector;
      }
      currentSelection.startDragging(mx, my);
    }
  }

  /**
   * Returns true if no element was selected.
   * @return true if no element was selected
   */
  private boolean nothingSelected() {
    return currentSelection == NullSelection.getInstance() ||
      currentSelection.getElement() == editor.getDiagram();
  }

  /**
   * Sets the current selection for the specified mouse coordinates. Returns
   * true if an element was clicked, false otherwise
   * @param mx the mapped x coordinate
   * @param my the mapped y coordinate
   * @return the selection object, a NullSelection instance otherwise
   */
  private Selection getSelection(double mx, double my) {
    if (!nothingSelected() && currentSelection.contains(mx, my)) {
      return currentSelection;
    }
    DiagramElement element = editor.getDiagram().getChildAt(mx, my);
    if (element != NullElement.getInstance()) {
      // select the element
      return element.getSelection(editor);
    }
    return editor.getDiagram().getSelection(editor);
  }

  /**
   * {@inheritDoc}
   */
  public void mouseReleased(EditorMouseEvent e) {
    handleSelectionOnMouseReleased(e);
    if (e.isPopupTrigger()) {
      displayContextMenu(e);
    }
  }

  /**
   * Handles the current selection on a mouse released.
   * @param e the EditorMouseEvent
   */
  private void handleSelectionOnMouseReleased(EditorMouseEvent e) {
    double mx = e.getX(), my = e.getY();
    if (currentSelection.isDragging()) {
      currentSelection.stopDragging(mx, my);
      if (currentSelection instanceof RubberbandSelector) {
        setRubberbandSelection((RubberbandSelector) currentSelection);
      }
      editor.redraw();
    }
    // notify selection listeners
    notifyListeners();
  }

  /**
   * Sets the current selection to a rubber band selection if available.
   * @param rubberband the RubberbandSelector
   */
  private void setRubberbandSelection(RubberbandSelector rubberband) {
    if (rubberband.getSelectedElements().size() == 1) {
      currentSelection = rubberband.getSelectedElements().get(0)
        .getSelection(editor);
    } else if (rubberband.getSelectedElements().size() > 1) {
      currentSelection = new MultiSelection(editor,
        rubberband.getSelectedElements());
    } else {
      currentSelection = editor.getDiagram().getSelection(editor);
    }
  }

  /**
   * {@inheritDoc}
   */
  public void mouseMoved(EditorMouseEvent e) {
    double mx = e.getX(), my = e.getY();
    if (currentSelection.contains(mx, my)) {
      editor.setCursor(currentSelection.getCursorForPosition(mx, my));
    } else {
      editor.setCursor(Cursor.getDefaultCursor());
    }
  }

  /**
   * {@inheritDoc}
   */
  public void mouseDragged(EditorMouseEvent e) {
    double mx = e.getX(), my = e.getY();
    if (currentSelection.isDragging()) {
      currentSelection.updatePosition(mx, my);
      editor.repaint();
    }
  }

  /**
   * {@inheritDoc}
   */
  public void draw(DrawingContext drawingContext) {
    currentSelection.draw(drawingContext);
    selector.draw(drawingContext);
  }

  /**
   * {@inheritDoc}
   */
  public void stateChanged() {
    currentSelection.updateDimensions();
  }

  /**
   * This method is called if the editor removed an element. If the current
   * selection contains the removed element, that selection is removed.
   * @param element the removed element
   */
  public void elementRemoved(DiagramElement element) {
    if (currentSelection.getElements().contains(element)) {
      currentSelection = NullSelection.getInstance();
    }
  }

  /**
   * {@inheritDoc}
   */
  public void cancel() {
    currentSelection.cancelDragging();
  }

  /**
   * Returns the currently selected element.
   * @return the selected element
   */
  public List<DiagramElement> getSelectedElements() {
    return currentSelection.getElements();
  }

  /**
   * Adds the specified SelectionListener.
   * @param l the listener to add
   */
  public void addSelectionListener(SelectionListener l) {
    listeners.add(l);
  }

  /**
   * Removes the specified SelectionListener.
   * @param l the listener to remove
   */
  public void removeSelectionListener(SelectionListener l) {
    listeners.remove(l);
  }

  /**
   * Notifies all listeners.
   */
  private void notifyListeners() {
    for (SelectionListener l : listeners) {
      l.selectionStateChanged();
    }
  }

  /**
   * Adds the specified AppCommandListener.
   * @param l the AppCommandListener to add
   */
  public void addAppCommandListener(AppCommandListener l) {
    menubuilder.addAppCommandListener(l);
  }
}
