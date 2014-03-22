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

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import javax.swing.JLabel;
import org.jmock.Mock;
import org.jmock.cglib.MockObjectTestCase;
import org.tinyuml.draw.CompositeNode;
import org.tinyuml.umldraw.shared.DiagramElementFactory;
import org.tinyuml.draw.DrawingContext;
import org.tinyuml.draw.NullElement;
import org.tinyuml.draw.Scaling;
import org.tinyuml.umldraw.shared.GeneralDiagram;
import org.tinyuml.model.ElementType;
import org.tinyuml.ui.diagram.CreationHandler;
import org.tinyuml.ui.diagram.DiagramEditor;
import org.tinyuml.ui.diagram.EditorMouseEvent;
import org.tinyuml.umldraw.shared.UmlNode;

/**
 * Test class for CreationHandler.
 * @author Wei-ju Wu
 * @version 1.0
 */
public class CreationHandlerTest extends MockObjectTestCase {
  
  private Mock mockEditor = mock(DiagramEditor.class);
  private Mock mockEvent = mock(EditorMouseEvent.class);
  private Mock mockDiagram = mock(GeneralDiagram.class);
  private Mock mockNode = mock(UmlNode.class);
  private Mock mockDrawingContext = mock(DrawingContext.class);
  private Mock mockFactory = mock(DiagramElementFactory.class);
  private CreationHandler handler =
    new CreationHandler((DiagramEditor) mockEditor.proxy());
  
  /**
   * Tests initial state.
   */
  public void testInitial() {
    handler.stateChanged();
    handler.mouseClicked(null);
    handler.mouseReleased(null);
    handler.mouseDragged(null);
    handler.cancel();
  }
  
  /**
   * Tests moving the mouse and draw().
   */
  public void testMouseMoveAndDraw() {
    mockEditor.expects(atLeastOnce()).method("getDiagram")
      .will(returnValue(mockDiagram.proxy()));
    mockDiagram.expects(atLeastOnce()).method("getElementFactory")
      .will(returnValue(mockFactory.proxy()));
    mockNode.expects(atLeastOnce()).method("setParent")
      .with(eq(mockDiagram.proxy()));
    mockFactory.expects(once()).method("createNode")
      .will(returnValue(mockNode.proxy()));
    handler.setElementType(ElementType.CLASS);

    int id = 12345;
    long now = System.currentTimeMillis();
    int modifiers = 0, clickCount = 2;
    int x = 5, y = 5;
    Component comp = new JLabel();
    boolean popupTrigger = true;
    MouseEvent mouseEvent = new MouseEvent(comp, 12345, now, modifiers, x, y,
      clickCount, popupTrigger, MouseEvent.BUTTON2);
    EditorMouseEvent event = new EditorMouseEvent();
    event.setMouseEvent(mouseEvent, Scaling.SCALING_100);
    mockEditor.expects(once()).method("redraw");
    handler.mouseMoved(event);
    
    // expectations
    Mock mockNester = mock(CompositeNode.class);
    mockDiagram.expects(once()).method("getChildAt")
      .will(returnValue(mockNester.proxy()));
    mockNester.expects(once()).method("canNestElements")
      .will(returnValue(true));
    mockNester.expects(once()).method("getAbsoluteBounds")
      .will(returnValue(new Rectangle2D.Double(0, 0, 100, 100)));
    mockNode.expects(once()).method("recalculateSize");
    mockNode.expects(once()).method("getAbsoluteBounds")
      .will(returnValue(new Rectangle2D.Double(5, 5, 10, 10)));
    mockDrawingContext.expects(exactly(2)).method("drawRectangle");
    handler.draw((DrawingContext) mockDrawingContext.proxy());
  }
  
  /**
   * Tests the creation.
   */
  public void testCreate() {
    mockEditor.expects(atLeastOnce()).method("getDiagram")
      .will(returnValue(mockDiagram.proxy()));
    mockDiagram.expects(atLeastOnce()).method("getElementFactory")
      .will(returnValue(mockFactory.proxy()));
    mockEditor.expects(atLeastOnce()).method("execute");
    mockNode.expects(atLeastOnce()).method("setParent")
      .with(eq(mockDiagram.proxy()));
    mockDiagram.expects(atLeastOnce()).method("getChildAt")
      .will(returnValue(NullElement.getInstance()));
    
    // create class
    mockFactory.expects(once()).method("createNode")
      .will(returnValue(mockNode.proxy()));
    handler.setElementType(ElementType.CLASS);
    handler.mousePressed((EditorMouseEvent) mockEvent.proxy());

    // create package
    mockFactory.expects(once()).method("createNode")
      .will(returnValue(mockNode.proxy()));
    handler.setElementType(ElementType.PACKAGE);
    handler.mousePressed((EditorMouseEvent) mockEvent.proxy());

    // create component
    mockFactory.expects(once()).method("createNode")
      .will(returnValue(mockNode.proxy()));
    handler.setElementType(ElementType.COMPONENT);
    handler.mousePressed((EditorMouseEvent) mockEvent.proxy());

    // create note
    mockFactory.expects(once()).method("createNode")
      .will(returnValue(mockNode.proxy()));
    handler.setElementType(ElementType.NOTE);
    handler.mousePressed((EditorMouseEvent) mockEvent.proxy());
  }

  /**
   * Tests the creation.
   */
  public void testCreateInNester() {
    Rectangle2D nodeBounds = new Rectangle2D.Double(0, 0, 10, 10);
    Mock mockNester = mock(CompositeNode.class);
    mockEditor.expects(atLeastOnce()).method("getDiagram")
      .will(returnValue(mockDiagram.proxy()));
    mockDiagram.expects(atLeastOnce()).method("getElementFactory")
      .will(returnValue(mockFactory.proxy()));
    mockEditor.expects(atLeastOnce()).method("execute");
    mockNode.expects(atLeastOnce()).method("setParent")
      .with(eq(mockDiagram.proxy()));
    mockDiagram.expects(atLeastOnce()).method("getChildAt")
      .will(returnValue(mockNester.proxy()));
    mockNester.expects(once()).method("canNestElements")
      .will(returnValue(true));
    mockNester.expects(once()).method("getAbsoluteBounds")
      .will(returnValue(new Rectangle2D.Double(0, 0, 100, 100)));
    
    // create class
    mockFactory.expects(once()).method("createNode")
      .will(returnValue(mockNode.proxy()));
    handler.setElementType(ElementType.CLASS);
    handler.setCachedBounds(nodeBounds);
    handler.mousePressed((EditorMouseEvent) mockEvent.proxy());

    // create package
    mockFactory.expects(once()).method("createNode")
      .will(returnValue(mockNode.proxy()));
    handler.setElementType(ElementType.PACKAGE);
    handler.mousePressed((EditorMouseEvent) mockEvent.proxy());

    // create component
    mockFactory.expects(once()).method("createNode")
      .will(returnValue(mockNode.proxy()));
    handler.setElementType(ElementType.COMPONENT);
    handler.mousePressed((EditorMouseEvent) mockEvent.proxy());

    // create note
    mockFactory.expects(once()).method("createNode")
      .will(returnValue(mockNode.proxy()));
    handler.setElementType(ElementType.NOTE);
    handler.mousePressed((EditorMouseEvent) mockEvent.proxy());
  }
}
