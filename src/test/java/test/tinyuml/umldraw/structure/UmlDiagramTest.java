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
package test.tinyuml.umldraw.structure;

import java.awt.geom.Point2D;
import org.jmock.Mock;
import org.jmock.cglib.MockObjectTestCase;
import org.tinyuml.draw.CompositeNode;
import org.tinyuml.draw.Connection;
import org.tinyuml.draw.DiagramElement;
import org.tinyuml.draw.Node;
import org.tinyuml.draw.NullElement;
import org.tinyuml.draw.SimpleConnection;
import org.tinyuml.umldraw.shared.GeneralDiagram;
import org.tinyuml.model.UmlModel;
import org.tinyuml.umldraw.structure.StructureDiagram;
import test.tinyuml.draw.MyCompositeNode;

/**
 * Test class for UmlDiagam.
 * @author Wei-ju Wu
 * @version 1.0
 */
public class UmlDiagramTest extends MockObjectTestCase {

  private Mock mockModel = mock(UmlModel.class);
  private GeneralDiagram diagram;

  /**
   * {@inheritDoc}
   */
  @Override
  protected void setUp() {
    diagram = new StructureDiagram((UmlModel) mockModel.proxy());
  }

  /**
   * Tests the initial state.
   */
  public void testInitialState() {
    assertNull(diagram.getLabelText());
    assertTrue(diagram.isGridVisible());
    assertNull(diagram.getParent());
    assertNotNull(diagram.getElementFactory());
  }

  /**
   * Tests the simple setter methods.
   */
  public void testSimpleSetters() {
    diagram.setLabelText("myname");
    assertEquals("myname", diagram.getLabelText());
    diagram.setGridVisible(false);
    assertFalse(diagram.isGridVisible());

    // setting the parent has no effect
    Mock mockParent = mock(CompositeNode.class);
    diagram.setParent((CompositeNode) mockParent.proxy());
    assertNull(diagram.getParent());
    // positions are equal the absolute ones
    diagram.setOrigin(2.0, 3.0);
    assertEquals(2.0, diagram.getAbsoluteX1());
    assertEquals(3.0, diagram.getAbsoluteY1());
    // and vice versa
    diagram.setAbsolutePos(6.0, 5.0);
    assertEquals(6.0, diagram.getOrigin().getX());
    assertEquals(5.0, diagram.getOrigin().getY());
  }
  
  /**
   * Tests the nodeMoved() method.
   */
  public void testNodeMoved() {
    // Set the initial sizes
    double originalWidth = 50.0, originalHeight = 60.0;
    diagram.setOrigin(0, 0);
    diagram.setSize(originalWidth, originalHeight);

    // simulate a node that is inside the bounds
    Mock mockNode1 = mock(Node.class);
    double node1X2 = 10.0, node1Y2 = 5.0;
    mockNode1.expects(atLeastOnce()).method("getAbsoluteX2").
      will(returnValue(node1X2));
    mockNode1.expects(atLeastOnce()).method("getAbsoluteY2").
      will(returnValue(node1Y2));
    diagram.nodeMoved((Node) mockNode1.proxy());
    assertEquals(originalWidth, diagram.getSize().getWidth());
    assertEquals(originalHeight, diagram.getSize().getHeight());

    // simulate a node that is moved outside the bounds, resize the diagram
    Mock mockNode2 = mock(Node.class);
    double node2X2 = 55.0, node2Y2 = 67.0;
    mockNode2.expects(atLeastOnce()).method("getAbsoluteX2").
      will(returnValue(node2X2));
    mockNode2.expects(atLeastOnce()).method("getAbsoluteY2").
      will(returnValue(node2Y2));
    diagram.nodeMoved((Node) mockNode2.proxy());
    assertTrue(diagram.getSize().getWidth() >= node2X2);
    assertTrue(diagram.getSize().getHeight() >= node2Y2);
  }

  /**
   * Tests the overridden version of addNode().
   */
  public void testAddNode() {
    // Set the initial sizes
    double originalWidth = 50.0, originalHeight = 60.0;
    diagram.setOrigin(0, 0);
    diagram.setSize(originalWidth, originalHeight);

    // simulate a node that is inside the bounds
    Mock mockNode1 = mock(Node.class);
    double node1X2 = 10.0, node1Y2 = 5.0;
    mockNode1.expects(once()).method("setParent").with(eq(diagram));
    mockNode1.expects(atLeastOnce()).method("getAbsoluteX2").
      will(returnValue(node1X2));
    mockNode1.expects(atLeastOnce()).method("getAbsoluteY2").
      will(returnValue(node1Y2));
    diagram.addChild((DiagramElement) mockNode1.proxy());
    assertEquals(originalWidth, diagram.getSize().getWidth());
    assertEquals(originalHeight, diagram.getSize().getHeight());

    // simulate a node that is moved outside the bounds, resize the diagram
    Mock mockNode2 = mock(Node.class);
    double node2X2 = 55.0, node2Y2 = 67.0;
    mockNode2.expects(once()).method("setParent").with(eq(diagram));
    mockNode2.expects(atLeastOnce()).method("getAbsoluteX2").
      will(returnValue(node2X2));
    mockNode2.expects(atLeastOnce()).method("getAbsoluteY2").
      will(returnValue(node2Y2));
    diagram.addChild((DiagramElement) mockNode2.proxy());
    assertTrue(diagram.getSize().getWidth() >= node2X2);
    assertTrue(diagram.getSize().getHeight() >= node2Y2);
    diagram.removeChild((DiagramElement) mockNode2.proxy());

    // tests the overridden getChildAt
    mockNode1.expects(atLeastOnce()).method("contains").
      with(eq(11.0), eq(6.0)).will(returnValue(true));
    assertEquals(mockNode1.proxy(), diagram.getChildAt(11.0, 6.0));    
  }

  /**
   * Tests the snapping method.
   */
  public void testSnapPos() {
    diagram.setSnapToGrid(false);
    assertFalse(diagram.isSnapToGrid());
    diagram.setSnapToGrid(true);
    assertTrue(diagram.isSnapToGrid());
    diagram.setGridSize(5);
    assertEquals(5, diagram.getGridSize());
    Point2D point = new Point2D.Double(16.3, 28.5);
    diagram.snap(point);
    assertEquals(15.0, point.getX());
    assertEquals(30.0, point.getY());
  }
  
  /**
   * Tests adding Connections.
   */
  public void testAddRemoveConnection() {
    Connection conn = new SimpleConnection() {
      public boolean contains(double mx, double my) { return true; }
    };
    diagram.addChild(conn);
    assertEquals(diagram, conn.getParent());
    assertEquals(conn, diagram.getChildAt(6.0, 5.0));
    diagram.removeChild(conn);
    assertEquals(NullElement.getInstance(), diagram.getChildAt(6.0, 5.0));
  }
  
  /**
   * Tests the label.
   */
  public void testLabel() {
    diagram.setLabelText("MyLabelText");
    assertEquals("MyLabelText", diagram.getLabelText());
    assertNotNull(diagram.getLabelAt(diagram.getAbsoluteX1() + 5.0,
      diagram.getAbsoluteY1() + 5.0));
    assertNull(diagram.getLabelAt(1000.0, 2000.0));
  }
  
  /**
   * Tests the overridden getChildren() method, which contains both connections
   * and nodes.
   */
  public void testOverriddenGetChildren() {
    Connection conn = new SimpleConnection();
    Mock mockNode = mock(Node.class);
    mockNode.expects(atLeastOnce()).method("getAbsoluteX2")
      .will(returnValue(2.0));
    mockNode.expects(atLeastOnce()).method("getAbsoluteY2")
      .will(returnValue(2.0));
    mockNode.expects(once()).method("setParent").with(eq(diagram));
    diagram.addChild((DiagramElement) mockNode.proxy());
    diagram.addChild(conn);
    assertTrue(diagram.getChildren().contains(mockNode.proxy()));
    assertTrue(diagram.getChildren().contains(conn));
  }

  /**
   * Tests the bring to front method.
   */
  public void testBringToFront() {
    MyCompositeNode child1 = new MyCompositeNode();
    MyCompositeNode child2 = new MyCompositeNode();
    MyCompositeNode child3 = new MyCompositeNode();
    diagram.addChild(child1);
    diagram.addChild(child2);
    diagram.addChild(child3);
    diagram.bringChildToFront(child1);
    assertEquals("child1 should now be at index 2",
      2, diagram.getChildren().indexOf(child1));
  }
  
  /**
   * Tests the putToBack() method.
   */
  public void testPutToBack() {
    MyCompositeNode child1 = new MyCompositeNode();
    MyCompositeNode child2 = new MyCompositeNode();
    MyCompositeNode child3 = new MyCompositeNode();
    diagram.addChild(child1);
    diagram.addChild(child2);
    diagram.addChild(child3);
    diagram.putChildToBack(child3);
    assertEquals("child3 should now be at index 0",
      0, diagram.getChildren().indexOf(child3));    
  }
}
