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
package org.tinyuml.ui;

import java.awt.Component;
import java.awt.Container;
import java.io.File;
import javax.swing.JMenuBar;
import org.tinyuml.ui.diagram.DiagramEditor;

/**
 * The ApplicationShell interface is implemented by the top level container
 * of the application.
 * @author Wei-ju Wu
 * @version 1.0
 */
public interface ApplicationShell {

  /**
   * Sets the JMenuBar.
   * @param menubar the JMenuBar
   */
  void setJMenuBar(JMenuBar menubar);

  /**
   * Returns the component that represents the shell.
   * @return the shell component
   */
  Component getShellComponent();

  /**
   * Returns the shell's content pane.
   * @return the content pane
   */
  Container getContentPane();

  /**
   * Sets the status title.
   * @param file the current file
   */
  void setTitle(File file);

  /**
   * Quits the application.
   */
  void quitApplication();

  /**
   * Returns the current editor.
   * @return the current editor
   */
  DiagramEditor getCurrentEditor();

  /**
   * Returns the menu manager.
   * @return the menu manager
   */
  MenuManager getMenuManager();
}
