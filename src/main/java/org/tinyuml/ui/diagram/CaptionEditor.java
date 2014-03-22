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

import java.awt.FontMetrics;
import java.awt.Graphics;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.tinyuml.draw.Label;

/**
 * This class acts as an in-editor text input element.
 *
 * @author Wei-ju Wu
 * @version 1.0
 */
public class CaptionEditor extends JTextField
implements DocumentListener, TextEditor {

  private Label currentLabel;

  /**
   * Constructor. Initially hidden.
   */
  public CaptionEditor() {
    setBorder(null);
    hideEditor();
    getDocument().addDocumentListener(this);
  }

  /**
   * {@inheritDoc}
   */
  public Label getLabel() { return currentLabel; }

  /**
   * {@inheritDoc}
   */
  public void hideEditor() {
    setEditable(false);
    setEnabled(false);
    setVisible(false);
  }

  /**
   * {@inheritDoc}
   */
  public void showEditor(Label aLabel, Graphics g) {
    currentLabel = aLabel;
    String text = currentLabel.getText();
    int width = g.getFontMetrics().stringWidth(text) + 10;
    int height = g.getFontMetrics().getHeight();
    setText(text);
    setSize(width, height);
    setLocation((int) aLabel.getAbsoluteX1(), (int) aLabel.getAbsoluteY1());
    setEditable(true);
    setEnabled(true);
    setVisible(true);
    requestFocusInWindow();
    selectAll();
  }

  /**
   * {@inheritDoc}
   */
  public void insertUpdate(DocumentEvent e) {
    String text = getText();
    FontMetrics fm = getGraphics().getFontMetrics();
    int width = fm.stringWidth(text);
    if (width > getWidth()) {
      setSize(width + 5, getHeight());
    }
  }

  /**
   * {@inheritDoc}
   */
  public void removeUpdate(DocumentEvent e) { }

  /**
   * {@inheritDoc}
   */
  public void changedUpdate(DocumentEvent e) { }
}
