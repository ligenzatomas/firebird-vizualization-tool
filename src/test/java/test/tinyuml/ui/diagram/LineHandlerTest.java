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
package test.tinyuml.ui.diagram;

import org.jmock.Mock;
import org.jmock.cglib.MockObjectTestCase;
import org.tinyuml.umldraw.structure.Dependency;
import org.tinyuml.umldraw.shared.DiagramElementFactory;
import org.tinyuml.draw.DrawingContext;
import org.tinyuml.model.RelationEndType;
import org.tinyuml.umldraw.shared.GeneralDiagram;
import org.tinyuml.umldraw.shared.UmlNode;
import org.tinyuml.ui.diagram.DiagramEditor;
import org.tinyuml.ui.diagram.EditorMouseEvent;
import org.tinyuml.ui.diagram.LineHandler;
import org.tinyuml.model.RelationType;
import org.tinyuml.draw.LineConnectMethod;
import org.tinyuml.draw.SimpleLineConnectMethod;

/**
 * A test class for LineHandler.
 * @author Wei-ju Wu
 * @version 1.0
 */
public class LineHandlerTest extends MockObjectTestCase {

  private Mock mockEditor = mock(DiagramEditor.class);
  private Mock mockEvent = mock(EditorMouseEvent.class);
  private Mock mockDiagram = mock(GeneralDiagram.class);
  private Mock mockDrawingContext = mock(DrawingContext.class);
  private Mock mockFactory = mock(DiagramElementFactory.class);
  //private Mock mockDependency = mock(Dependency.class);
  private Mock mockNode1 = mock(UmlNode.class);
  private Mock mockNode2 = mock(UmlNode.class);
  private Mock mockConnectMethod = mock(LineConnectMethod.class);
  private LineHandler handler =
    new LineHandler((DiagramEditor) mockEditor.proxy());
  
  /**
   * Tests initial state.
   */
  public void testInitial() {
    assertFalse(handler.isDragging());
    // no op
    handler.stateChanged();
    handler.mouseClicked(null);
    handler.mouseMoved(null);
  }

  /**
   * Sets the start drag expectations.
   */
  private void setStartDragExpectations() {
    mockEvent.expects(once()).method("getX").will(returnValue(100.0));
    mockEvent.expects(once()).method("getY").will(returnValue(110.0));
    mockEditor.expects(atLeastOnce()).method("getDiagram")
      .will(returnValue(mockDiagram.proxy()));
    mockDiagram.expects(once()).method("getChildAt")
      .with(eq(100.0), eq(110.0)).will(returnValue(mockNode1.proxy()));
    mockNode1.expects(once()).method("acceptsConnection")
      .with(eq(RelationType.DEPENDENCY), eq(RelationEndType.SOURCE),
            eq(null)).will(returnValue(true));
  }
  
  /**
   * Simulates dragging.
   */
  public void testDragging() {
    Dependency dependency = (Dependency) Dependency.getPrototype().clone();
    setStartDragExpectations();
    handler.setRelationType(RelationType.DEPENDENCY, (LineConnectMethod)
      mockConnectMethod.proxy());
    handler.mousePressed((EditorMouseEvent) mockEvent.proxy());
    assertTrue(handler.isDragging());
    // Drag
    mockEvent.expects(once()).method("getX").will(returnValue(150.0));
    mockEvent.expects(once()).method("getY").will(returnValue(180.0));
    mockEditor.expects(once()).method("redraw");
    handler.mouseDragged((EditorMouseEvent) mockEvent.proxy());
    // draw
    mockConnectMethod.expects(once()).method("drawLineSegments");
    handler.draw((DrawingContext) mockDrawingContext.proxy());
    // release
    mockConnectMethod.expects(once())
      .method("generateAndSetPointsToConnection");
    mockEvent.expects(once()).method("getX").will(returnValue(150.0));
    mockEvent.expects(once()).method("getY").will(returnValue(180.0));
    mockDiagram.expects(once()).method("getChildAt")
      .with(eq(150.0), eq(180.0)).will(returnValue(mockNode2.proxy()));
    mockDiagram.expects(once()).method("getElementFactory")
      .will(returnValue(mockFactory.proxy()));
    mockFactory.expects(once()).method("createConnection")
      .will(returnValue(dependency));
    mockEditor.expects(once()).method("execute");
    mockEditor.expects(once()).method("redraw");
    mockNode2.expects(once()).method("acceptsConnection")
      .with(eq(RelationType.DEPENDENCY), eq(RelationEndType.TARGET),
            eq(mockNode1.proxy())).will(returnValue(true));
    handler.mouseReleased((EditorMouseEvent) mockEvent.proxy());
  }
  
  /**
   * Simulates a cancelled drag operation.
   */
  public void testCancelDragging() {    
    setStartDragExpectations();
    handler.setRelationType(RelationType.DEPENDENCY, (LineConnectMethod)
      mockConnectMethod.proxy());
    handler.mousePressed((EditorMouseEvent) mockEvent.proxy());
    assertTrue(handler.isDragging());
    handler.cancel();
    assertFalse(handler.isDragging());
  }
}
