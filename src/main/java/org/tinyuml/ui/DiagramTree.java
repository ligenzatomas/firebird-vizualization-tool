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

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import org.tinyuml.ui.model.DiagramTreeModel;
import org.tinyuml.umldraw.shared.GeneralDiagram;

/**
 * A specialized tree component to display diagrams.
 *
 * @author Wei-ju Wu
 * @version 1.0
 */
public class DiagramTree extends JTree implements MouseListener {

  /**
   * Do not worry about serializing this component, we won't.
   */
  private static final long serialVersionUID = 1L;
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
    appState = null;
  }

  /**
   * Constructor.
   * @param anAppState the ApplicationState
   * @param treeModel the tree model
   */
  public DiagramTree(ApplicationState anAppState, DiagramTreeModel treeModel) {
    super(treeModel);
    appState = anAppState;
    addMouseListener(this);
    setRootVisible(false);
    setShowsRootHandles(true);
  }

  /**
   * {@inheritDoc}
   */
  public void mouseClicked(MouseEvent e) {
    if (e.getClickCount() == 2) {
      TreePath path = getPathForLocation(e.getX(), e.getY());
      DefaultMutableTreeNode node = (DefaultMutableTreeNode)
      path.getLastPathComponent();
      if (node.getUserObject() instanceof GeneralDiagram) {
        appState.openExistingStructureEditor((GeneralDiagram)
          node.getUserObject());
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  public void mousePressed(MouseEvent e) { }

  /**
   * {@inheritDoc}
   */
  public void mouseReleased(MouseEvent e) { }

  /**
   * {@inheritDoc}
   */
  public void mouseEntered(MouseEvent e) { }

  /**
   * {@inheritDoc}
   */
  public void mouseExited(MouseEvent e) { }
}
