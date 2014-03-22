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
package test.tinyuml.ui.model;

import java.util.ArrayList;
import java.util.List;
import org.jmock.Mock;
import org.jmock.cglib.MockObjectTestCase;
import org.tinyuml.model.NamedElement;
import org.tinyuml.model.UmlDiagram;
import org.tinyuml.model.UmlModel;
import org.tinyuml.ui.model.DiagramTreeModel;
import org.tinyuml.umldraw.shared.GeneralDiagram;

/**
 * A test class for the DiagramTreeModel.
 *
 * @author Wei-ju Wu
 * @version 1.0
 */
public class DiagramTreeModelTest extends MockObjectTestCase {
  private Mock mockModel = mock(UmlModel.class);
  private DiagramTreeModel treeModel = new DiagramTreeModel();

  /**
   * Tests the setter methods.
   */
  public void testSetters() {
    mockModel.expects(once()).method("addModelListener").with(eq(treeModel));
    mockModel.expects(once()).method("getDiagrams")
      .will(returnValue(new ArrayList()));
    treeModel.setModel((UmlModel) mockModel.proxy());
    
    Mock mockDiagram0 = mock(GeneralDiagram.class);
    mockDiagram0.expects(once()).method("addNameChangeListener")
      .with(eq(treeModel));
    treeModel.diagramAdded((GeneralDiagram) mockDiagram0.proxy());
    treeModel.nameChanged((NamedElement) mockDiagram0.proxy());

    List<UmlDiagram> diagrams = new ArrayList<UmlDiagram>();
    diagrams.add((UmlDiagram) mockDiagram0.proxy());
    mockModel.expects(once()).method("removeModelListener").with(eq(treeModel));
    mockModel.expects(once()).method("getDiagrams").will(returnValue(diagrams));
    mockDiagram0.expects(once()).method("removeNameChangeListener")
      .with(eq(treeModel));
    Mock mockModel2 = mock(UmlModel.class);
    mockModel2.expects(once()).method("addModelListener").with(eq(treeModel));
    mockModel2.expects(once()).method("getDiagrams")
      .will(returnValue(new ArrayList()));
    treeModel.setModel((UmlModel) mockModel2.proxy());
  }
}
