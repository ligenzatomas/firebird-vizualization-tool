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

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import org.tinyuml.util.IconLoader;
import org.tinyuml.util.IconLoader.IconType;

/**
 * This class provides a tab component with a close button.
 *
 * @author Wei-ju Wu
 * @version 1.0
 */
public class ClosableTabComponent extends JPanel {

  private JTabbedPane tabbedPane;

  /**
   * A close button for the tab.
   */
  private class CloseButton extends JButton implements ActionListener {

    /**
     * Constructor.
     */
    public CloseButton() {
      setIcon(IconLoader.getInstance().getIcon(IconType.CLOSE_TAB));
      setPreferredSize(new Dimension(17, 17));
      setContentAreaFilled(false);
      setRolloverEnabled(true);
      setBorder(BorderFactory.createEtchedBorder());
      setBorderPainted(false);
      addMouseListener(buttonMouseListener);
      setFocusable(false);
      addActionListener(this);
    }

    /**
     * {@inheritDoc}
     */
    public void actionPerformed(ActionEvent e) {
      tabbedPane.remove(
        tabbedPane.indexOfTabComponent(ClosableTabComponent.this));
    }
  }

  private static MouseListener buttonMouseListener = new MouseAdapter() {
    /**
     * {@inheritDoc}
     */
    public void mouseEntered(MouseEvent e) {
      if (e.getComponent() instanceof AbstractButton) {
        ((AbstractButton) e.getComponent()).setBorderPainted(true);
      }
    }
    /**
     * {@inheritDoc}
     */
    public void mouseExited(MouseEvent e) {
      if (e.getComponent() instanceof AbstractButton) {
        ((AbstractButton) e.getComponent()).setBorderPainted(false);
      }
    }
  };

  /**
   * Constructor.
   * @param aTabbedPane the TabbedPane
   */
  public ClosableTabComponent(JTabbedPane aTabbedPane) {
    super(new FlowLayout(FlowLayout.LEFT, 10, 0));
    tabbedPane = aTabbedPane;
    JLabel label = new JLabel() {
      /**
       * {@inheritDoc}
       */
      @Override
      public String getText() {
        int index = tabbedPane.indexOfTabComponent(ClosableTabComponent.this);
        return index >= 0 ? tabbedPane.getTitleAt(index) : null;
      }
    };
    setBorder(null);
    label.setBorder(null);
    setOpaque(false);
    add(label);
    JButton closeButton = new CloseButton();
    add(closeButton);
  }
}
