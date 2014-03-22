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
package org.tinyuml.ui.model;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import org.tinyuml.model.NameChangeListener;
import org.tinyuml.model.NamedElement;
import org.tinyuml.model.UmlDiagram;
import org.tinyuml.model.UmlModel;
import org.tinyuml.model.UmlModelListener;
import org.tinyuml.umldraw.sequence.SequenceDiagram;
import org.tinyuml.umldraw.shared.GeneralDiagram;
import org.tinyuml.umldraw.eer.EERDiagram;
import org.tinyuml.umldraw.structure.StructureDiagram;
import org.tinyuml.util.ApplicationResources;

/**
 * This class implements a TreeModel to display the diagrams.
 *
 * @author Wei-ju Wu
 * @version 1.0
 */
public class DiagramTreeModel extends DefaultTreeModel
implements UmlModelListener, NameChangeListener {

	private UmlModel model;
	private DefaultMutableTreeNode structureFolder, sequenceFolder, eerFolder;

	/**
	 * Constructor.
	 */
	public DiagramTreeModel() {
		super(new DefaultMutableTreeNode("Root node"));
		structureFolder = new DefaultMutableTreeNode(getResourceString(
			"stdcaption.structurediagrams"));
		sequenceFolder = new DefaultMutableTreeNode(getResourceString(
			"stdcaption.sequencediagrams"));
		eerFolder = new DefaultMutableTreeNode(getResourceString(
			"stdcaption.eerdiagrams"));
		insertNodeInto(structureFolder, (DefaultMutableTreeNode) getRoot(), 0);
		insertNodeInto(sequenceFolder, (DefaultMutableTreeNode) getRoot(), 1);
		insertNodeInto(eerFolder, (DefaultMutableTreeNode) getRoot(), 2);
	}

  /**
   * Returns a string from the resource bundle.
   * @param property the property
   * @return the value from the resource bundle
   */
  private String getResourceString(String property) {
    return ApplicationResources.getInstance().getString(property);
  }

  /**
   * Sets the UmlModel.
   * @param aModel the UmlModel
   */
  public void setModel(UmlModel aModel) {
    cleanupOldStructure();
    buildNewStructure(aModel);
  }

	/**
	 * Removes the old structures.
	 */
	private void cleanupOldStructure() {
		if (model != null) {
			model.removeModelListener(this);
			for (UmlDiagram diagram : model.getDiagrams()) {
				((GeneralDiagram) diagram).removeNameChangeListener(this);
			}
		}
		structureFolder.removeAllChildren();
		sequenceFolder.removeAllChildren();
		eerFolder.removeAllChildren();
		nodeStructureChanged(structureFolder);
		nodeStructureChanged(sequenceFolder);
		nodeStructureChanged(eerFolder);
	}

	/**
	 * Build the new tree structure.
	 * @param aModel the model
	 */
	private void buildNewStructure(UmlModel aModel) {
		model = aModel;
		aModel.addModelListener(this);
		for (UmlDiagram diagram : model.getDiagrams()) {
			insertToFolder(diagram);
			addNameChangeListener((GeneralDiagram) diagram);
		}
		nodeStructureChanged(structureFolder);
		nodeStructureChanged(sequenceFolder);
		nodeStructureChanged(eerFolder);
		reload();
	}

  /**
   * {@inheritDoc}
   */
  public void diagramAdded(UmlDiagram diagram) {
    insertToFolder(diagram);
    addNameChangeListener((GeneralDiagram) diagram);
  }

	/**
	 * Inserts the specified diagram to the correct folder.
	 * @param diagram the diagram
	 */
	private void insertToFolder(UmlDiagram diagram) {
		DefaultMutableTreeNode child = new DefaultMutableTreeNode(diagram);
		if (diagram instanceof StructureDiagram) {
			insertNodeInto(child, structureFolder, structureFolder.getChildCount());
		} else if (diagram instanceof SequenceDiagram) {
			insertNodeInto(child, sequenceFolder, sequenceFolder.getChildCount());
		} else if (diagram instanceof EERDiagram) {
			insertNodeInto(child, eerFolder, eerFolder.getChildCount());
		}
	}

  /**
   * Adds a name change listener to the specified diagram.
   * @param diagram the diagram
   */
  private void addNameChangeListener(GeneralDiagram diagram) {
    diagram.addNameChangeListener(this);
  }

	/**
	 * {@inheritDoc}
	 */
	public void diagramRemoved(UmlDiagram diagram) {
		removeFromFolder(structureFolder, diagram);
		removeFromFolder(sequenceFolder, diagram);
		removeFromFolder(eerFolder, diagram);
	}

  /**
   * Removes the specified diagram from the folder if it is found.
   * @param folder the folder
   * @param diagram the diagram
   */
  private void removeFromFolder(DefaultMutableTreeNode folder,
    UmlDiagram diagram) {
    for (int i = 0; i < folder.getChildCount(); i++) {
      DefaultMutableTreeNode node = (DefaultMutableTreeNode)
        folder.getChildAt(i);
      if (node.getUserObject() == diagram) {
        removeNodeFromParent(node);
        break;
      }
    }
  }

	/**
	 * {@inheritDoc}
	 */
	public void nameChanged(NamedElement element) {
		for (int i = 0; i < structureFolder.getChildCount(); i++) {
			DefaultMutableTreeNode treenode = (DefaultMutableTreeNode)
				structureFolder.getChildAt(i);
			if (treenode.getUserObject() == element) {
				nodeChanged(treenode);
				break;
			}
		}
		for (int i = 0; i < eerFolder.getChildCount(); i++) {
			DefaultMutableTreeNode treenode = (DefaultMutableTreeNode)
				eerFolder.getChildAt(i);
			if (treenode.getUserObject() == element) {
				nodeChanged(treenode);
				break;
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isLeaf(Object node) {
		if (node == structureFolder || node == sequenceFolder || node == eerFolder) return false;
		return super.isLeaf(node);
	}
}
