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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.tinyuml.ui.diagram.DiagramEditor;
import org.tinyuml.util.ApplicationResources;

/**
 * This class implements the Application frame. The top-level UI elements are
 * created here. Application events that affect the entire application are
 * handled here, local event handlers are also installed.
 *
 * @author Wei-ju Wu
 * @version 1.0
 */
public class AppFrame extends JFrame implements ApplicationShell {

  private transient ApplicationState appState;

  /**
   * Reset the transient values for serialization.
   * @param stream an ObjectInputStream
   * @throws IOException if I/O error occured
   * @throws ClassNotFoundException if class was not found
   */
  @SuppressWarnings("PMD.UnusedFormalParameter")
  private void readObject(ObjectInputStream stream)
    throws IOException, ClassNotFoundException {
    appState = new ApplicationState(this);
  }

  /**
   * Creates a new instance of AppFrame.
   */
  public AppFrame() {
    setTitle(getResourceString("application.title"));
    setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    appState = new ApplicationState(this);

    addWindowListener(new WindowAdapter() {
      /**
       * {@inheritDoc}
       */
      public void windowClosing(WindowEvent e) {
        quitApplication();
      }
    });
    pack();
    appState.scheduleMemTimer();
    //setExtendedState(JFrame.MAXIMIZED_BOTH);
  }

  /**
   * {@inheritDoc}
   */
  public DiagramEditor getCurrentEditor() {
    return appState.getCurrentEditor();
  }

  /**
   * {@inheritDoc}
   */
  public MenuManager getMenuManager() { return appState.getMenuManager(); }

  /**
   * {@inheritDoc}
   */
  public Component getShellComponent() { return this; }

  /**
   * Returns the specified resource as a String object.
   * @param property the property name
   * @return the property value
   */
  private String getResourceString(String property) {
    return ApplicationResources.getInstance().getString(property);
  }

  // ************************************************************************
  // **** Event listeners
  // *****************************************
  /**
   * Call this method to exit this application in a clean way.
   */
  public void quitApplication() {
    if (canQuit()) {
      appState.stopThreads();
      dispose();
      Thread.currentThread().interrupt();
    }
  }

  /**
   * Checks if application can be quit safely.
   * @return true if can quit safely, false otherwise
   */
  private boolean canQuit() {
    if (appState.isModified()) {
      return JOptionPane.showConfirmDialog(this,
        ApplicationResources.getInstance().getString("confirm.quit.message"),
        ApplicationResources.getInstance().getString("confirm.quit.title"),
        JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }
    return true;
  }

  /**
   * {@inheritDoc}
   */
  public void setTitle(File currentFile) {
    if (currentFile != null) {
      setTitle(ApplicationResources.getInstance()
        .getString("application.title") + " [" + currentFile.getName() + "]");
    } else {
      setTitle(ApplicationResources.getInstance()
        .getString("application.title"));
    }
  }
}
