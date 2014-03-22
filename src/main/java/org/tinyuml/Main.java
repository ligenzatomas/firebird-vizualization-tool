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
package org.tinyuml;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import org.tinyuml.ui.AppFrame;

/**
 * This is the start class of the TinyUML application.
 *
 * @author Wei-ju Wu
 * @version 1.0
 */
public final class Main {

  /**
   * Private constructor.
   */
  private Main() { }

  /**
   * The start method for this application.
   * @param args the command line parameters
   */
  public static void main(String[] args) {

    // Mac OS X settings
    if (System.getProperty("mrj.version") != null) {
      System.setProperty("apple.laf.useScreenMenuBar", "true");
      System.setProperty("com.apple.eawt.CocoaComponent.CompatibilityMode",
          "false");
      System.setProperty("com.apple.mrj.application.apple.menu.about.name",
          "TinyUML");
    }

    SwingUtilities.invokeLater(new Runnable() {
      /**
       * {@inheritDoc}
       */
      public void run() {
        try {
          UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
          JFrame frame = new AppFrame();
          frame.setVisible(true);
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
    });
  }
}
