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
package test.tinyuml.draw;

import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import org.jmock.Mock;
import org.jmock.cglib.MockObjectTestCase;
import org.tinyuml.draw.AbstractNode;
import org.tinyuml.draw.CompositeNode;
import org.tinyuml.draw.Connection;
import org.tinyuml.draw.DiagramOperations;
import org.tinyuml.draw.DrawingContext;
import org.tinyuml.draw.Label;
import org.tinyuml.draw.Node;
import org.tinyuml.draw.NodeChangeListener;
import org.tinyuml.draw.NodeSelection;
import org.tinyuml.draw.Selection;
import org.tinyuml.model.UmlModelElement;

/**
 *
 * @author Wei-ju Wu
 * @version 1.0
 */
public class AbstractNodeTest extends MockObjectTestCase {
  
  private Mock mockModelElement = mock(UmlModelElement.class);
  private UmlModelElement modelElement = (UmlModelElement)
    mockModelElement.proxy();

  /**
   * Makes some methods public.
   */
  class MyNode extends AbstractNode {
    public Label getLabelAt(double x, double y) { return null; }
    public void setSelection(NodeSelection selection) {
      super.setSelection(selection);
    }
    public void recalculateSize(DrawingContext drawingContext) { }
    public void draw(DrawingContext drawingContext) { }
    public Collection<NodeChangeListener> getNodeChangeListeners() {
      return super.getNodeChangeListeners();
    }
  }
  private static final double EPS = 0.001;
  private MyNode node = new MyNode();
  private Mock mockParent = mock(CompositeNode.class);
  private CompositeNode parent = (CompositeNode) mockParent.proxy();
  private Mock mockOperations = mock(DiagramOperations.class);
  private DiagramOperations operations = (DiagramOperations)
    mockOperations.proxy();
  
  /**
   * Tests initial state.
   */
  public void testInitial() {
    assertNull(node.getParent());
    assertNotNull(node.getOrigin());
    assertNotNull(node.getSize());
    assertNotNull(node.getMinimumSize());
    assertTrue(node.isValid());
    assertEquals(0, node.getConnections().size());
    assertFalse(node.isNestable());
    assertFalse(node.canNestElements());
  }
  
  /**
   * Tests the clone() method.
   */
  public void testClone() {
    mockParent.expects(atLeastOnce()).method("getAbsoluteX1")
      .will(returnValue(0.0));
    mockParent.expects(atLeastOnce()).method("getAbsoluteY1")
      .will(returnValue(0.0));
    
    node.setParent(parent);
    node.getSelection(operations);
    MyNode cloned = (MyNode) node.clone();
    assertFalse(cloned == node);
    assertTrue(cloned.getParent() == node.getParent());
    assertEquals(cloned.getOrigin(), node.getOrigin());
    assertFalse(cloned.getOrigin() == node.getOrigin());
    assertEquals(cloned.getSize(), node.getSize());
    assertFalse(cloned.getSize() == node.getSize());
    assertFalse(cloned.getConnections() == node.getConnections());
    assertFalse(cloned.getNodeChangeListeners() ==
                node.getNodeChangeListeners());
    assertEquals(cloned.getMinimumSize(), node.getMinimumSize());
    assertFalse(cloned.getMinimumSize() == node.getMinimumSize());
    
    // Selections
    assertFalse(node.getSelection(operations) ==
                cloned.getSelection(operations));
  }
  
  /**
   * Tests the setter methods.
   */
  public void testSetters() {
    // expectations
    mockParent.expects(atLeastOnce()).method("getAbsoluteX1").
      will(returnValue(2.0));
    mockParent.expects(atLeastOnce()).method("getAbsoluteY1").
      will(returnValue(3.0));

    node.setParent(parent);
    node.setOrigin(1.0, 3.3);
    node.setSize(3.2, 4.1);
    node.setMinimumSize(5.1, 6.3);
    
    assertEquals(parent, node.getParent());
    assertEquals(1.0, node.getOrigin().getX());
    assertEquals(3.3, node.getOrigin().getY());
    assertEquals(3.2, node.getSize().getWidth());
    assertEquals(4.1, node.getSize().getHeight());
    assertEquals(5.1, node.getMinimumSize().getWidth());
    assertEquals(6.3, node.getMinimumSize().getHeight());
    
    // Absolute position
    assertEquals(node.getOrigin().getX() + parent.getAbsoluteX1(),
                 node.getAbsoluteX1());
    assertEquals(node.getOrigin().getY() + parent.getAbsoluteY1(),
                 node.getAbsoluteY1());
    node.setAbsolutePos(8.3, 9.5);
    assertEquals(8.3, node.getAbsoluteX1());
    assertEquals(9.5, node.getAbsoluteY1());
    assertEquals(8.3 - parent.getAbsoluteX1(), node.getOrigin().getX());
    assertEquals(9.5 - parent.getAbsoluteY1(), node.getOrigin().getY());
    
    // set width and height separately
    node.setWidth(10.3);
    assertEquals(4.1, node.getSize().getHeight());
    assertEquals(10.3, node.getSize().getWidth());
    node.setHeight(31.1);
    assertEquals(10.3, node.getSize().getWidth());
    assertEquals(31.1, node.getSize().getHeight());
  }
  
  /**
   * Tests the contains() method.
   */
  public void testContains() {
    // expectations
    mockParent.expects(atLeastOnce()).method("getAbsoluteX1").
      will(returnValue(2.5));
    mockParent.expects(atLeastOnce()).method("getAbsoluteY1").
      will(returnValue(2.6));
    
    node.setOrigin(3.0, 3.0);
    node.setSize(1.0, 1.0);
    node.setParent(parent);
    
    // contains is in absolute coordinates
    assertTrue(node.contains(5.8, 5.7));
    assertFalse(node.contains(3.1, 3.3));
  }
  
  /**
   * Tests absolute center calculation.
   */
  public void testGetAbsoluteCenter() {
    // expectations
    mockParent.expects(atLeastOnce()).method("getAbsoluteX1").
      will(returnValue(0.0));
    mockParent.expects(atLeastOnce()).method("getAbsoluteY1").
      will(returnValue(0.0));

    node.setOrigin(3.0, 5.0);
    node.setSize(2.0, 3.0);
    node.setParent(parent);
    assertEquals(4.0, node.getAbsCenterX());
    assertEquals(6.5, node.getAbsCenterY());
  }

  /**
   * Tests the getAbsoluteX1()/getAbsoluteY1() methods.
   */
  public void testGetAbsoluteX1Y1() {
    double parentX = 2.5, parentY = 3.5;
    double nodeX = 3.0, nodeY = 5.0;
    double nodeWidth = 2.0, nodeHeight = 5.0;
    // expectations
    mockParent.expects(atLeastOnce()).method("getAbsoluteX1").
      will(returnValue(parentX));
    mockParent.expects(atLeastOnce()).method("getAbsoluteY1").
      will(returnValue(parentY));

    node.setOrigin(nodeX, nodeY);
    node.setSize(nodeWidth, nodeHeight);
    node.setParent(parent);
    assertEquals(parentX + nodeX + nodeWidth, node.getAbsoluteX2());
    assertEquals(parentY + nodeY + nodeHeight, node.getAbsoluteY2());
  }

  /**
   * Tests the isVisible() method.
   */
  public void testIsVisible() {
    // expectations
    mockParent.expects(atLeastOnce()).method("getAbsoluteX1").
      will(returnValue(2.5));
    mockParent.expects(atLeastOnce()).method("getAbsoluteY1").
      will(returnValue(2.5));

    // origin: (5.5, 5.5) x1, y1: (6.5, 6.5)
    node.setOrigin(3.0, 3.0);
    node.setSize(1.0, 1.0);
    node.setParent(parent);
    
    // isVisible is in absolute coordinates
    Rectangle clipBounds = new Rectangle(1, 1, 4, 4);
    assertFalse(node.isVisible(clipBounds));
    
    // Translate to overlap with (5.5, 5.5), and now, the areas are overlapping
    clipBounds = new Rectangle(2, 2, 4, 5);
    assertTrue(node.isVisible(clipBounds));
    
    // Make it so big that it encloses the area
    clipBounds = new Rectangle(5, 5, 10, 10);
    assertTrue(node.isVisible(clipBounds));
  }
  
  /**
   * Tests NodeChangeListener notification cases.
   */
  public void testNodeChangeListenerNotification() {
    // expectations
    mockParent.expects(atLeastOnce()).method("getAbsoluteX1").
      will(returnValue(0.0));
    mockParent.expects(atLeastOnce()).method("getAbsoluteY1").
      will(returnValue(0.0));

    Mock mockListener = mock(NodeChangeListener.class);
    NodeChangeListener listener = (NodeChangeListener) mockListener.proxy();
    node.setParent(parent);
    node.addNodeChangeListener(listener);

    // expects the listener to be called
    mockListener.expects(once()).method("nodeMoved").with(eq(node));
    node.setAbsolutePos(2.0, 3.0);
    // but not the second time, when nothing changed
    node.setAbsolutePos(2.0, 3.0);

    // expects the listener to be called
    mockListener.expects(once()).method("nodeResized").with(eq(node));
    node.setSize(3.0, 4.0);

    // after removal, the listener will not be called anymore
    node.removeNodeChangeListener(listener);
    mockListener.expects(never()).method("nodeMoved").with(eq(node));
    mockListener.expects(never()).method("nodeResized").with(eq(node));
    node.setAbsolutePos(2.0, 3.0);
    node.setSize(2.0, 3.0);
  }
  
  /**
   * Tests the selection.
   */
  public void testSelection() {
    Mock mockOperations = mock(DiagramOperations.class);
    node.setParent(parent);
    mockParent.expects(atLeastOnce()).method("getAbsoluteX1").
      will(returnValue(0.0));
    mockParent.expects(atLeastOnce()).method("getAbsoluteY1").
      will(returnValue(0.0));
    Selection selection =
      node.getSelection((DiagramOperations) mockOperations.proxy());
    assertEquals(node, selection.getElement());
    node.setSize(3.0, 4.0); // we should in fact    
  }
  
  /**
   * Tests that the setSize() method updates the Selection.
   */
  public void testSetSizeUpdatesSelection() {
    Mock mockSelection = mock(NodeSelection.class);
    Mock mockChangeListener = mock(NodeChangeListener.class);
    node.addNodeChangeListener((NodeChangeListener) mockChangeListener.proxy());
    node.setSelection((NodeSelection) mockSelection.proxy());
    
    mockChangeListener.expects(once()).method("nodeResized").with(eq(node));
    node.setSize(30.0, 33.0);
  }
  
  /**
   * Tests the intersects() method with a Line2D.
   */
  public void testIntersectsWithLine2D() {
    Line2D line = new Line2D.Double();
    mockParent.expects(atLeastOnce()).method("getAbsoluteX1").
      will(returnValue(0.0));
    mockParent.expects(atLeastOnce()).method("getAbsoluteY1").
      will(returnValue(0.0));
    node.setParent(parent);
    node.setAbsolutePos(10, 10);
    node.setSize(100, 100);
    // this line intersects the node
    line.setLine(20, 15, 80, 15);
    assertTrue(node.intersects(line));
    line.setLine(20, 205, 80, 205);
    assertFalse(node.intersects(line));
  }
  
  /**
   * Tests the intersects() method.
   */
  public void testIntersects() {
    mockParent.expects(atLeastOnce()).method("getAbsoluteX1").
      will(returnValue(0.0));
    mockParent.expects(atLeastOnce()).method("getAbsoluteY1").
      will(returnValue(0.0));
    node.setParent(parent);
    node.setAbsolutePos(10, 10);
    node.setSize(100, 100);
    Rectangle2D bounds = new Rectangle2D.Double();
    bounds.setRect(5, 5, 30, 40);
    assertTrue(node.intersects(bounds));
    bounds.setRect(2000, 2000, 10, 10);
    assertFalse(node.intersects(bounds));
  }
  
  /**
   * Tests the calculateIntersection() method.
   */
  public void testCalculateIntersection() {
    mockParent.expects(atLeastOnce()).method("getAbsoluteX1").
      will(returnValue(0.0));
    mockParent.expects(atLeastOnce()).method("getAbsoluteY1").
      will(returnValue(0.0));
    node.setParent(parent);
    Point2D intersect = new Point2D.Double();
    Line2D line = new Line2D.Double();
    node.setAbsolutePos(10, 10);
    node.setSize(100, 100);
    
    // intersect on the left side
    line.setLine(5, 15, 20, 15);
    node.calculateIntersection(line, intersect);
    assertTrue(Math.abs(intersect.getX() - 10) < EPS);
    assertTrue(Math.abs(intersect.getY() - 15) < EPS);
    
    // intersect on the top side
    line.setLine(10, 0, 15, 20);
    node.calculateIntersection(line, intersect);
    assertTrue(Math.abs(intersect.getX() - 12.5) < EPS);
    assertTrue(Math.abs(intersect.getY() - 10.0) < EPS);
    
    // intersect on the right side
    line.setLine(120, 15, 60, 25);
    node.calculateIntersection(line, intersect);
    assertTrue(Math.abs(intersect.getX() - 110.0) < EPS);
    assertTrue(Math.abs(intersect.getY() - 16.6666) < EPS);
    
    // intersect on the bottom side
    line.setLine(50, 150, 60, 50);
    node.calculateIntersection(line, intersect);
    assertTrue(Math.abs(intersect.getX() - 54.0) < EPS);
    assertTrue(Math.abs(intersect.getY() - 110.0) < EPS);
  }
  
  /**
   * Tests adding and removing connections.
   */
  public void testConnections() {
    Mock mockConnection = mock(Connection.class);
    node.addConnection((Connection) mockConnection.proxy());
    assertEquals(1, node.getConnections().size());
    node.removeConnection((Connection) mockConnection.proxy());
    assertEquals(0, node.getConnections().size());
  }  
}
