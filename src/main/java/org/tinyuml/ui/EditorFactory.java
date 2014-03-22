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
import javax.swing.JTabbedPane;
import org.tinyuml.model.NameChangeListener;
import org.tinyuml.model.NamedElement;
import org.tinyuml.model.UmlModel;
import org.tinyuml.ui.diagram.DiagramEditor;
import org.tinyuml.ui.diagram.EERDiagramEditor;
import org.tinyuml.ui.diagram.SequenceDiagramEditor;
import org.tinyuml.ui.diagram.StructureDiagramEditor;
import org.tinyuml.umldraw.sequence.SequenceDiagram;
import org.tinyuml.umldraw.shared.GeneralDiagram;
import org.tinyuml.umldraw.eer.EERDiagram;
import org.tinyuml.umldraw.structure.StructureDiagram;
import org.tinyuml.util.ApplicationResources;

/**
 * A manager class for the available structure diagrams in the application.
 * It maintains the UI context for in order to properly create the editor.
 *
 * @author Wei-ju Wu
 * @version 1.0
 */
public class EditorFactory {

  private ApplicationShell shell;
  private ApplicationState appState;
  private JTabbedPane tabbedPane;
  private int structureCounter = 1, sequenceCounter = 1, eerCounter = 1;

  /**
   * Constructor.
   * @param aShell the application shell
   * @param anAppState the application state object
   * @param aTabbedPane the tabbed pane
   */
  public EditorFactory(ApplicationShell aShell, ApplicationState anAppState,
    JTabbedPane aTabbedPane) {
    shell = aShell;
    appState = anAppState;
    tabbedPane = aTabbedPane;
  }

  /**
   * Resets the internal counter.
   */
  public void reset() {
    structureCounter = 1;
  }
  
	/**
	 * Opens a new structure editor and displays it in the editor area.
	 * @param umlModel the UmlModel the diagram belongs to
	 * @return the editor panel
	 */
	public EditorPanel openNewEEREditor(UmlModel umlModel) {
		GeneralDiagram diagram = new EERDiagram(umlModel);
		diagram.setLabelText(ApplicationResources.getInstance().getString(
			"stdcaption.eerdiagram") + " " + (eerCounter++));
		umlModel.addDiagram(diagram);
		return createEditorPanel(new EERDiagramEditor(
			shell.getShellComponent(), diagram),
			new EEREditorToolbarManager());
	}

  /**
   * Opens a new structure editor and displays it in the editor area.
   * @param umlModel the UmlModel the diagram belongs to
   * @return the editor panel
   */
  public EditorPanel openNewStructureEditor(UmlModel umlModel) {
    GeneralDiagram diagram = new StructureDiagram(umlModel);
    diagram.setLabelText(ApplicationResources.getInstance().getString(
      "stdcaption.structurediagram") + " " + (structureCounter++));
    umlModel.addDiagram(diagram);
    return createEditorPanel(new StructureDiagramEditor(
      shell.getShellComponent(), diagram),
      new StaticStructureEditorToolbarManager());
  }

  /**
   * Opens a structure editor for an existing diagram.
   * @param diagram the diagram
   * @return the editor panel
   */
  public EditorPanel openStructureEditor(GeneralDiagram diagram) {
    return createEditorPanel(new StructureDiagramEditor(
      shell.getShellComponent(), diagram),
      new StaticStructureEditorToolbarManager());
  }

  /**
   * Creates an editor for the specified diagram and adds it to the tabbed
   * pane.
   * @param diagramEditor the diagram editor
   * @param toolbarManager the ToolbarManager
   * @return the StructureEditor panel
   */
  private EditorPanel createEditorPanel(DiagramEditor diagramEditor,
    ToolbarManager toolbarManager) {
    EditorPanel editor = new EditorPanel(diagramEditor, toolbarManager);
    GeneralDiagram diagram = diagramEditor.getDiagram();
    final Component comp = tabbedPane.add(diagram.getLabelText(), editor);
    final int index = tabbedPane.indexOfComponent(comp);
    tabbedPane.setToolTipTextAt(index, diagram.getLabelText());
    diagram.addNameChangeListener(new NameChangeListener() {
      /** {@inheritDoc} */
      public void nameChanged(NamedElement element) {
        tabbedPane.setTitleAt(index, element.getName());
        tabbedPane.setToolTipTextAt(index, element.getName());
      }
   });
   tabbedPane.setSelectedComponent(editor);
   tabbedPane.setTabComponentAt(index, new ClosableTabComponent(tabbedPane));
   editor.getDiagramEditor().addFocusListener(appState);
   editor.getDiagramEditor().addUndoableEditListener(appState.getUndoManager());
   return editor;
  }

  /**
   * Creates a new Sequence editor.
   * @param umlModel the UmlModel
   * @return the editor panel
   */
  public EditorPanel openNewSequenceEditor(UmlModel umlModel) {
    GeneralDiagram diagram = new SequenceDiagram(umlModel);
    diagram.setLabelText(ApplicationResources.getInstance().getString(
      "stdcaption.sequencediagram") + " " + (sequenceCounter++));
    umlModel.addDiagram(diagram);
    return createEditorPanel(new SequenceDiagramEditor(
      shell.getShellComponent(), diagram),
      new SequenceEditorToolbarManager());
  }
}
