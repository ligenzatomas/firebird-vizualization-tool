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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.undo.UndoManager;
import org.tinyuml.model.DatabaseModel;
import org.tinyuml.model.DatabaseModelImplementation;
import org.tinyuml.model.UmlDiagram;
import org.tinyuml.model.UmlModel;
import org.tinyuml.model.UmlModelImpl;
import org.tinyuml.model.UmlModelListener;
import org.tinyuml.ui.diagram.DiagramEditor;
import org.tinyuml.ui.diagram.EditorMouseEvent;
import org.tinyuml.ui.diagram.EditorStateListener;
import org.tinyuml.ui.diagram.SelectionListener;
import org.tinyuml.ui.model.DiagramTreeModel;
import org.tinyuml.ui.model.TinyUmlProject;
import org.tinyuml.umldraw.shared.GeneralDiagram;
import org.tinyuml.util.Command;

/**
 * This class holds the common elements that the TinyUML default application
 * consists of.
 *
 * @author Wei-ju Wu
 * @version 1.0
 */
public class ApplicationState implements EditorStateListener, SelectionListener,
ChangeListener, FocusListener, TreeSelectionListener, UmlModelListener {

  private JTabbedPane tabbedPane;
  private JLabel coordLabel = new JLabel("    ");
  private JLabel memLabel = new JLabel("    ");
  private UmlModel umlModel;
  private DatabaseModel databaseModel;
  private DiagramEditor currentEditor;
  private Timer timer = new Timer();
  private ApplicationCommandDispatcher appCommandDispatcher;
  private EditorCommandDispatcher editorDispatcher;
  private MainToolbarManager toolbarmanager;
  private MenuManager menumanager;
  private File currentFile;
  private EditorFactory editorFactory;
  private DiagramTreeModel treeModel = new DiagramTreeModel();
  private Component currentFocusedComponent;

  private ApplicationShell shell;
  // The command processor to hold this application's operations.
  private UndoManager undoManager;

  /**
   * Constructor.
   * @param aShell the application shell
   */
  public ApplicationState(ApplicationShell aShell) {
    shell = aShell;
    undoManager = new UndoManager();
    appCommandDispatcher = new ApplicationCommandDispatcher(this);
    editorDispatcher = new EditorCommandDispatcher(shell);
    shell.getContentPane().add(createEditorArea(), BorderLayout.CENTER);
    editorFactory = new EditorFactory(shell, this, tabbedPane);
    installMainToolbar();
    installMenubar();
    installStatusbar();
    newProject();
  }

  /**
   * Returns the UML model.
   * @return the UML model
   */
  public UmlModel getUmlModel() { return umlModel; }
  
  public DatabaseModel getDatabaseModel() { return databaseModel; }

  /**
   * Returns the application shell.
   * @return the shell
   */
  public ApplicationShell getShell() { return shell; }

  /**
   * Returns the MenuManager.
   * @return the MenuManager
   */
  public MenuManager getMenuManager() { return menumanager; }

  /**
   * Returns the UndoManager.
   * @return the UndoManager
   */
  public UndoManager getUndoManager() { return undoManager; }

  /**
   * Returns the currently focused component.
   * @return the currently focused component
   */
  public Component getCurrentFocusedComponent() {
    return currentFocusedComponent;
  }

  /**
   * {@inheritDoc}
   */
  public DiagramEditor getCurrentEditor() { return currentEditor; }

  /**
   * Creates the tabbed pane for the editor area.
   * @return the tabbed pane
   */
  private JComponent createEditorArea() {
    JSplitPane splitpane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
    tabbedPane = new JTabbedPane();
    tabbedPane.setFocusable(false);
    tabbedPane.setPreferredSize(new Dimension(800, 600));
    tabbedPane.addChangeListener(this);
    splitpane.setRightComponent(tabbedPane);

    DiagramTree tree = new DiagramTree(this, treeModel);
    tree.addFocusListener(this);
    tree.getSelectionModel().addTreeSelectionListener(this);
    JScrollPane spane = new JScrollPane(tree);
    spane.setPreferredSize(new Dimension(150, 300));
    splitpane.setLeftComponent(spane);
    splitpane.setOneTouchExpandable(true);
    return splitpane;
  }

  /**
   * Adds the tool bar.
   */
  private void installMainToolbar() {
    toolbarmanager = new MainToolbarManager();
    toolbarmanager.addCommandListener(appCommandDispatcher);
    toolbarmanager.addCommandListener(editorDispatcher);
    shell.getContentPane().add(toolbarmanager.getToolbar(), BorderLayout.NORTH);
  }

  /**
   * Adds the menubar.
   */
  private void installMenubar() {
    menumanager = new MenuManager();
    menumanager.addCommandListener(appCommandDispatcher);
    menumanager.addCommandListener(editorDispatcher);
    shell.setJMenuBar(menumanager.getMenuBar());
  }

  /**
   * Adds a status bar.
   */
  private void installStatusbar() {
    JPanel statusbar = new JPanel(new BorderLayout());
    statusbar.add(coordLabel, BorderLayout.WEST);
    statusbar.add(memLabel, BorderLayout.EAST);
    shell.getContentPane().add(statusbar, BorderLayout.SOUTH);
  }

  /**
   * Stops all threads that were started in this object.
   */
  public void stopThreads() {
    timer.cancel();
    timer.purge();
  }

  /**
   * Sets up and starts the timer task.
   */
  public void scheduleMemTimer() {
    TimerTask task = new TimerTask() {
      public void run() {
        SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            memLabel.setText(getMemString());
          }
        });
      }
    };
    // every 5 seconds
    timer.schedule(task, 2000, 5000);
  }

  /**
   * Sets the current file.
   * @param file the current file
   */
  public void setCurrentFile(File file) {
    currentFile = file;
    shell.setTitle(file);
  }

  /**
   * Returns the current file.
   * @return the current file
   */
  public File getCurrentFile() { return currentFile; }

  /**
   * Creates a new project.
   */
  protected void newProject() {
    undoManager.discardAllEdits();
    tabbedPane.removeAll();
    umlModel = new UmlModelImpl();
	databaseModel = new DatabaseModelImplementation();
    editorFactory.reset();
    //openNewStructureEditor();
	openNewEEREditor();
    treeModel.setModel(umlModel);
    umlModel.addModelListener(this);
  }

  /**
   * Restores the state from the specified project.
   * @param project the project
   */
  protected void restoreFromProject(TinyUmlProject project) {
    undoManager.discardAllEdits();
    umlModel = project.getModel();
	databaseModel = project.getDatabaseModel();
    umlModel.addModelListener(this);
    treeModel.setModel(umlModel);
    tabbedPane.removeAll();
    for (UmlDiagram diagram : project.getOpenDiagrams()) {
      openExistingStructureEditor((GeneralDiagram) diagram);
    }
  }

  /**
   * Prepares and creates a project object for writing.
   * @return the project object
   */
  protected TinyUmlProject createProjectForWrite() {
    TinyUmlProject result = new TinyUmlProject(umlModel, databaseModel);
    for (int i = 0; i < tabbedPane.getTabCount(); i++) {
      EditorPanel editorPanel = (EditorPanel) tabbedPane.getComponentAt(i);
      result.addOpenDiagram(editorPanel.getDiagramEditor().getDiagram());
    }
    return result;
  }

  // ***********************************************************************
  // ****** Event handling
  // **************************************
  /**
   * Creates the memory information string.
   * @return the memory status string
   */
  private String getMemString() {
    long free = Runtime.getRuntime().freeMemory();
    long total = Runtime.getRuntime().totalMemory();
    long used = total - free;
    used /= (1024 * 1024);
    total /= (1024 * 1024);
    return String.format("used: %dM total: %dM   ", used, total);
  }

  /**
   * Query the specified editor state and set the menu and the toolbars
   * accordingly.
   */
  protected void updateMenuAndToolbars() {
    menumanager.enableMenuItem("UNDO", canUndo());
    menumanager.enableMenuItem("REDO", canRedo());
    toolbarmanager.setEnabled("UNDO", canUndo());
    toolbarmanager.setEnabled("REDO", canRedo());
    // dependent on the component that has the focus, enable the
    // delete options and the view menu options
    selectionStateChanged();
    menumanager.enableViewMenuItems(getCurrentEditor() != null);
  }

  // ************************************************************************
  // **** EditorStateListener
  // *****************************************

  /**
   * {@inheritDoc}
   */
  public void mouseMoved(EditorMouseEvent event) {
    coordLabel.setText(String.format("(%.1f, %.1f)", event.getX(),
      event.getY()));
  }

  /**
   * {@inheritDoc}
   */
  public void stateChanged(DiagramEditor editor) {
    updateMenuAndToolbars();
  }

  /**
   * {@inheritDoc}
   */
  public void elementAdded(DiagramEditor editor) {
    updateMenuAndToolbars();
  }

  /**
   * {@inheritDoc}
   */
  public void elementRemoved(DiagramEditor editor) {
    updateMenuAndToolbars();
  }

  // ************************************************************************
  // **** SelectionListener
  // *****************************************

  /**
   * {@inheritDoc}
   */
  public void selectionStateChanged() {
    boolean hasSelection = false;
    if (currentFocusedComponent instanceof DiagramTree) {
      DiagramTree tree = (DiagramTree) currentFocusedComponent;
      TreePath path = tree.getSelectionPath();
      if (path != null) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)
          path.getLastPathComponent();
        hasSelection = tree.getModel().isLeaf(node);
      }
    } else {
      hasSelection = getCurrentEditor() != null &&
        getCurrentEditor().canDelete();
    }
    /*
    menumanager.enableMenuItem("CUT", hasSelection);
    menumanager.enableMenuItem("COPY", hasSelection);
     */
    menumanager.enableMenuItem("DELETE", hasSelection);
    /*
    toolbarmanager.enableButton("CUT", hasSelection);
    toolbarmanager.enableButton("COPY", hasSelection);
     */
    toolbarmanager.setEnabled("DELETE", hasSelection);
  }

  /**
   * Returns the modification state.
   * @return the modification state
   */
  protected boolean isModified() {
    return canUndo();
  }

  // ************************************************************************
  // ***** Visible Editor management
  // ****************************************

  /**
   * Opens a new sequence editor.
   */
  protected void openNewSequenceEditor() {
    EditorPanel editorPanel = editorFactory.openNewSequenceEditor(umlModel);
    currentEditor = editorPanel.getDiagramEditor();
    addDiagramEditorEvents(editorPanel);
  }

  /**
   * Opens a new structure editor.
   */
  protected void openNewStructureEditor() {
    EditorPanel editorPanel = editorFactory.openNewStructureEditor(umlModel);
    currentEditor = editorPanel.getDiagramEditor();
    addDiagramEditorEvents(editorPanel);
  }
  
	protected void openNewEEREditor() {
		EditorPanel editorPanel = editorFactory.openNewEEREditor(umlModel);
		currentEditor = editorPanel.getDiagramEditor();
		addDiagramEditorEvents(editorPanel);
	}

  /**
   * Opens an existing structure editor.
   * @param diagram the diagram
   */
  protected void openExistingStructureEditor(GeneralDiagram diagram) {
    if (!isAlreadyOpen(diagram)) {
      EditorPanel editorPanel =
        editorFactory.openStructureEditor(diagram);
      currentEditor = editorPanel.getDiagramEditor();
      addDiagramEditorEvents(editorPanel);
    }
  }

  /**
   * Determines whether the specified diagram is already opened in the
   * editor.
   * @param diagram the diagram
   * @return true if already openend, false otherwise
   */
  private boolean isAlreadyOpen(GeneralDiagram diagram) {
    for (int i = 0; i < tabbedPane.getTabCount(); i++) {
      EditorPanel editorPanel = (EditorPanel) tabbedPane.getComponentAt(i);
      if (editorPanel.getDiagramEditor().getDiagram() == diagram) return true;
    }
    return false;
  }

  /**
   * Associates the events to the specified editor.
   * @param editorPanel the editor panel
   */
  private void addDiagramEditorEvents(EditorPanel editorPanel) {
    editorPanel.addEditorStateListener(this);
    editorPanel.addSelectionListener(this);
    editorPanel.addAppCommandListener(editorDispatcher);
    editorPanel.addAppCommandListener(appCommandDispatcher);
  }

  /**
   * {@inheritDoc}
   */
  public void stateChanged(ChangeEvent e) {
    EditorPanel editorPanel = (EditorPanel)
      tabbedPane.getSelectedComponent();
    currentEditor = editorPanel == null ? null : editorPanel.getDiagramEditor();
    if (currentEditor != null) {
      currentEditor.requestFocusInWindow();
    }
    updateMenuAndToolbars();
  }

  /**
   * Returns the canUndo status.
   * @return true if can undo, false otherwise
   */
  private boolean canUndo() { return undoManager.canUndo(); }

  /**
   * Returns the canRedo status.
   * @return true if can redo, false otherwise
   */
  private boolean canRedo() { return undoManager.canRedo(); }

  /**
   * {@inheritDoc}
   */
  public void focusGained(FocusEvent e) {
    currentFocusedComponent = e.getComponent();
    updateMenuAndToolbars();
  }

  /**
   * {@inheritDoc}
   */
  public void focusLost(FocusEvent e) { }

  /**
   * {@inheritDoc}
   */
  public void valueChanged(TreeSelectionEvent e) { selectionStateChanged(); }

  /**
   * Runs the specified command by this editor's CommandProcessor, which makes
   * the operation reversible.
   * @param command the command to run
   */
  public void execute(Command command) {
    UndoableEditEvent event = new UndoableEditEvent(this, command);
    undoManager.undoableEditHappened(event);
    // We need to run() after notifying the UndoManager in order to ensure
    // correct menu behaviour
    command.run();
  }

  /**
   * {@inheritDoc}
   */
  public void diagramAdded(UmlDiagram diagram) { }

  /**
   * {@inheritDoc}
   */
  public void diagramRemoved(UmlDiagram diagram) {
    for (int i = 0; i < tabbedPane.getTabCount(); i++) {
      EditorPanel editorPanel = (EditorPanel) tabbedPane.getComponentAt(i);
      if (editorPanel.getDiagramEditor().getDiagram() == diagram) {
        tabbedPane.removeTabAt(i);
      }
    }
  }
}
