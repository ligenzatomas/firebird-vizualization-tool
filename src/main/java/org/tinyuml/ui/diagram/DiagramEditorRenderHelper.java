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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import org.tinyuml.draw.DrawingContext;
import org.tinyuml.draw.DrawingContextImpl;
import org.tinyuml.draw.Scaling;

/**
 * This class takes over the responsibility of rendering the DiagramEditor
 * component. The DiagramEditor class has the tendency to get too large, so
 * outfactoring some functionality into helpers seems to be a viable tradeoff.
 *
 * @author Wei-ju Wu
 * @version 1.0
 */
public class DiagramEditorRenderHelper {

  private Scaling scaling = Scaling.SCALING_100;
  private DrawingContext drawingContext = new DrawingContextImpl();
  private DiagramEditor diagramEditor;

  /**
   * Constructor.
   * @param editor the DiagramEditor instance.
   */
  public DiagramEditorRenderHelper(DiagramEditor editor) {
    diagramEditor = editor;
  }

  /**
   * Returns this editor's scaling.
   * @return the scaling
   */
  protected Scaling getScaling() { return scaling; }

  /**
   * Sets the scaling.
   * @param aScaling the scaling
   */
  protected void setScaling(Scaling aScaling) { scaling = aScaling; }

  /**
   * Returns the DrawingContext.
   * @return the DrawingContext
   */
  protected DrawingContext getDrawingContext() { return drawingContext; }

  /**
   * Paints this component with a specified bounds object.
   * @param g the graphics context
   * @param bounds the bounding rectangle to repaint
   * @param toScreen true if rendered to screen, false otherwise
   * otherwise
   */
  public void paintComponent(Graphics g, Rectangle bounds, boolean toScreen) {
    Graphics2D g2d = (Graphics2D) g;
    setRenderingHints(g2d);
    if (scaling.getScaleFactor() != 1.0) {
      g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
        RenderingHints.VALUE_INTERPOLATION_BILINEAR);
      g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
        RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
    }

    boolean gridVisible = diagramEditor.getDiagram().isGridVisible();
    Color background = Color.GRAY;
    if (toScreen) {
      // Scaling is only interesting if rendering to screen
      scaleDiagram(g2d);
    } else {
      diagramEditor.getDiagram().setGridVisible(false);
      background = Color.WHITE;
    }
    clearScreen(g, bounds, background);
    drawingContext.setGraphics2D(g2d, bounds);
    diagramEditor.getDiagram().draw(drawingContext);
    // Draw user interface specific elements (e.g. selections)
    if (toScreen) {
      diagramEditor.getEditorMode().draw(drawingContext);
    }
    restoreRenderingHints(g2d);
    diagramEditor.getDiagram().setGridVisible(gridVisible);
  }

  /**
   * Sets the rendering hints used in the editor.
   * @param g2d the Graphics2D object
   */
  private void setRenderingHints(Graphics2D g2d) {
    g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
      RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
      RenderingHints.VALUE_ANTIALIAS_ON);
  }

  /**
   * Resets the rendering hints used in the editor.
   * @param g2d the Graphics2D object
   */
  private void restoreRenderingHints(Graphics2D g2d) {
    g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
      RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT);
    /*
    if (scaling.getScaleFactor() != 1.0) {
      g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
        RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
    }*/
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
      RenderingHints.VALUE_ANTIALIAS_DEFAULT);
  }

  /**
   * Fills the screen with the background color.
   * @param g the Graphics object
   * @param bounds the bounds to draw within
   * @param color the background color
   */
  private void clearScreen(Graphics g, Rectangle bounds, Color color) {
    g.setColor(color);
    g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
  }

  /**
   * Scales the diagram.
   * @param g2d the Graphics2D object
   */
  private void scaleDiagram(Graphics2D g2d) {
    double scaleFactor = scaling.getScaleFactor();
    g2d.scale(scaleFactor, scaleFactor);
  }
}
