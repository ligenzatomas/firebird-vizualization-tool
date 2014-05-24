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

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import org.tinyuml.draw.CompositeNode;
import org.tinyuml.draw.DiagramElement;
import org.tinyuml.draw.DrawingContext;
import org.tinyuml.draw.Node;
import org.tinyuml.model.ElementType;
import org.tinyuml.ui.diagram.commands.AddNodeCommand;

/**
 * This class implements the editor creation mode. Creation is triggered on the
 * mouse down event, which has the nice side-effect that the created element
 * will then be selected after creation. All other methods have an empty
 * implementation.
 *
 * @author Wei-ju Wu
 * @version 1.0
 */
public class CreationHandler implements EditorMode {

  private static final Color ACCEPT_COLOR = new Color(0, 220, 50);
  private DiagramEditor editor;
  private ElementType elementType;
  private Node element;
  private Point2D tmpPos = new Point2D.Double();
  private Rectangle2D cachedBounds;

  /**
   * Constructor.
   * @param editor the DiagramEditor
   */
  public CreationHandler(DiagramEditor editor) {
    this.editor = editor;
  }

  /**
   * Artificially set the cached bounds from outside for testing.
   * @param bounds the bounds
   */
  public void setCachedBounds(Rectangle2D bounds) {
    cachedBounds = bounds;
  }

  /**
   * Sets the ElementType.
   * @param type the ElementType
   */
  public void setElementType(ElementType type) {
    elementType = type;
    element = editor.getDiagram().getElementFactory().createNode(elementType);
    element.setParent(editor.getDiagram());
    cachedBounds = null;
  }
  
  public Node createElement() {
	  
	CompositeNode parent = editor.getDiagram();
	
	AddNodeCommand createCommand = new AddNodeCommand(
		editor, parent, element, editor.getX(), editor.getY());
	
	editor.execute(createCommand);
	
	return element;
  }

  /**
   * {@inheritDoc}
   */
  public void mouseClicked(EditorMouseEvent event) { }

  /**
   * {@inheritDoc}
   */
  public void mousePressed(EditorMouseEvent event) {
    CompositeNode parent = editor.getDiagram();
    DiagramElement possibleParent = editor.getDiagram().getChildAt(
      tmpPos.getX(), tmpPos.getY());
    if (isNestingCondition(possibleParent)) {
      parent = (CompositeNode) possibleParent;
    }
    AddNodeCommand createCommand = new AddNodeCommand(editor,
      parent, element, tmpPos.getX(), tmpPos.getY());
    editor.execute(createCommand);
  }

  /**
   * {@inheritDoc}
   */
  public void mouseReleased(EditorMouseEvent event) { }

  /**
   * {@inheritDoc}
   */
  public void mouseDragged(EditorMouseEvent event) { }

  /**
   * {@inheritDoc}
   */
  public void mouseMoved(EditorMouseEvent event) {
    tmpPos.setLocation(event.getX(), event.getY());
    editor.redraw();
  }

  /**
   * {@inheritDoc}
   */
  public void draw(DrawingContext drawingContext) {
    DiagramElement elem = editor.getDiagram().getChildAt(tmpPos.getX(),
      tmpPos.getY());
    if (isNestingCondition(elem, drawingContext)) {
      Rectangle2D bounds = cachedBounds;
      drawingContext.drawRectangle(bounds.getX() - 5, bounds.getY() - 5,
        bounds.getWidth() + 10, bounds.getHeight() + 10, ACCEPT_COLOR, null);
    }
    drawSilhouette(drawingContext);
  }

  /**
   * Determines if the node can be nested in the specified nester object.
   * This is the case if canNestElements() is true and the node is within
   * the nester's bounds. This version uses the cached bounds object in
   * order to be used without a drawing context.
   * @param nester the nester object
   * @return true if the nesting condition is true, false otherwise
   */
  private boolean isNestingCondition(DiagramElement nester) {
    if (cachedBounds == null) return false;
    return nester.canNestElements() &&
      nester.getAbsoluteBounds().contains(cachedBounds);
  }

  /**
   * Determines if the node can be nested in the specified nester object.
   * This is the case if canNestElements() is true and the node is within
   * the nester's bounds.
   * @param nester the nester object
   * @param drawingContext the drawingContext
   * @return true if the nesting condition is true, false otherwise
   */
  private boolean isNestingCondition(DiagramElement nester,
    DrawingContext drawingContext) {
    return nester.canNestElements() &&
      nester.getAbsoluteBounds().contains(getElementBounds(drawingContext));
  }

  /**
   * Returns the element bounds for the current node to be created. The size
   * of the element is only known if put into a drawing context and setting
   * a temporary parent.
   * @param drawingContext the drawing context
   * @return the element bounds
   */
  private Rectangle2D getElementBounds(DrawingContext drawingContext) {
    if (cachedBounds == null) {
      element.recalculateSize(drawingContext);
      cachedBounds = element.getAbsoluteBounds();
    }
    cachedBounds.setRect(tmpPos.getX(), tmpPos.getY(),
      cachedBounds.getWidth(), cachedBounds.getHeight());
    return cachedBounds;
  }

  /**
   * Draws the silhouette of the element to be added.
   * @param drawingContext the drawing context
   */
  private void drawSilhouette(DrawingContext drawingContext) {
    Rectangle2D bounds = getElementBounds(drawingContext);
    drawingContext.drawRectangle(bounds.getX(), bounds.getY(),
      bounds.getWidth(), bounds.getHeight(), null);
  }

  /**
   * {@inheritDoc}
   */
  public void stateChanged() { }

  /**
   * {@inheritDoc}
   */
  public void cancel() { }
}
