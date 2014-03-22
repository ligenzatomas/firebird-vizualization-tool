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
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import org.jmock.Mock;
import org.jmock.cglib.MockObjectTestCase;
import org.tinyuml.draw.AbstractCompositeNode;
import org.tinyuml.draw.CompositeNode;
import org.tinyuml.draw.DiagramElement;
import org.tinyuml.draw.DrawingContext;
import org.tinyuml.draw.Label;
import org.tinyuml.draw.Node;
import org.tinyuml.draw.NodeChangeListener;
import org.tinyuml.draw.NodeSelection;
import org.tinyuml.draw.NullElement;

/**
 * A test class for AbstractCompositeNode.
 * @author Wei-ju Wu
 * @version 1.0
 */
public class AbstractCompositeNodeTest extends MockObjectTestCase {

  class MyCompositeNode extends AbstractCompositeNode {
    public Label getLabelAt(double x, double y) { return null; }
    public void setSelection(NodeSelection selection) {
      super.setSelection(selection);
    }
    public Collection<NodeChangeListener> getNodeChangeListeners() {
      return super.getNodeChangeListeners();
    }
  }
  private CompositeNode parent = new MyAbsoluteParent();
  
  /**
   * Tests initial state.
   */
  public void testInitial() {
    MyCompositeNode node = new MyCompositeNode();
    assertEquals(0, node.getChildren().size());
  }
  
  /**
   * Tests clone() method.
   */
  public void testClone() {
    MyCompositeNode node = new MyCompositeNode();
    Node child = new MyCompositeNode();
    node.addChild(child);
    MyCompositeNode cloned = (MyCompositeNode) node.clone();
    
    // AbstractCompositeNode
    assertEquals(node.getChildren().size(), cloned.getChildren().size());
    assertFalse(cloned.getChildren() == node.getChildren());
    for (DiagramElement clonedChild : cloned.getChildren()) {
      assertEquals(cloned, clonedChild.getParent());
    }
    for (DiagramElement originalChild : node.getChildren()) {
      assertEquals(node, originalChild.getParent());
    }    
  }

  /**
   * Tests the container methods of AbstractNode.
   */
  public void testChild() {
    MyCompositeNode node = new MyCompositeNode();
    node.setParent(parent);
    node.setOrigin(0, 0);
    node.setSize(100, 100);
    // test parent assignment
    Node child = new MyCompositeNode();
    child.setOrigin(5, 5);
    child.setSize(10, 10);
    // add the same child twice
    node.addChild(child);
    node.addChild(child);
    assertEquals("child should only be added once",
      1, node.getChildren().size());
    assertEquals(node, child.getParent());
    node.removeChild(child);
    assertEquals(node, child.getParent());

    // test getChildAt()
    node.addChild(child);
    assertEquals(child, node.getChildAt(8, 8));
    assertEquals(NullElement.getInstance(), node.getChildAt(15, 18));
  }
  
  /**
   * Returns a child which is nested in a hierarchy.
   */
  public void testGetNestedChild() {
    MyCompositeNode node = new MyCompositeNode();
    node.setParent(parent);
    node.setOrigin(0, 0);
    node.setSize(100, 100);
    // test parent assignment
    CompositeNode child = new MyCompositeNode();
    child.setOrigin(5, 5);
    child.setSize(80, 80);
    node.addChild(child);

    Node childchild = new MyCompositeNode();
    childchild.setOrigin(5, 5);
    childchild.setSize(70, 70);
    child.addChild(childchild);
    
    // retrieve childchild
    assertEquals(childchild, node.getChildAt(15, 20));
    assertEquals(node, child.getParent());
    assertEquals(child, childchild.getParent());
    
    assertTrue(childchild.isAncestor(child));
    assertTrue(childchild.isAncestor(parent));
    assertTrue(child.isAncestor(parent));
    assertFalse(parent.isAncestor(child));
    assertFalse(child.isAncestor(childchild));
  }

  /**
   * Tests the draw() method.
   */
  public void testDraw() {
    MyCompositeNode node = new MyCompositeNode();
    Rectangle clipbounds = new Rectangle();
    Mock mockDrawingContext = mock(DrawingContext.class);
    Mock mockChild1 = mock(DiagramElement.class);
    Mock mockChild2 = mock(DiagramElement.class);
    mockChild1.expects(once()).method("setParent");
    mockChild2.expects(once()).method("setParent");
    node.addChild((DiagramElement) mockChild1.proxy());
    node.addChild((DiagramElement) mockChild2.proxy());
    // draw all children, only the second is visible
    mockDrawingContext.expects(once()).method("getClipBounds").
      will(returnValue(clipbounds));
    mockChild1.expects(once()).method("isVisible").
      with(eq(clipbounds)).will(returnValue(false));
    mockChild2.expects(once()).method("isVisible").
      with(eq(clipbounds)).will(returnValue(true));
    mockChild2.expects(once()).method("draw").
      with(eq(mockDrawingContext.proxy()));
    node.draw((DrawingContext) mockDrawingContext.proxy());
  }

  /**
   * Tests the recalculateSize() method.
   */
  public void testRecalculateSize() {
    MyCompositeNode node = new MyCompositeNode();
    Mock mockDrawingContext = mock(DrawingContext.class);
    Mock mockChild1 = mock(DiagramElement.class);
    Mock mockChild2 = mock(DiagramElement.class);
    mockChild1.expects(once()).method("setParent");
    mockChild2.expects(once()).method("setParent");
    node.addChild((DiagramElement) mockChild1.proxy());
    node.addChild((DiagramElement) mockChild2.proxy());
    // calculate all children
    mockChild1.expects(once()).method("recalculateSize").
      with(eq(mockDrawingContext.proxy()));
    mockChild2.expects(once()).method("recalculateSize").
      with(eq(mockDrawingContext.proxy()));
    node.recalculateSize((DrawingContext) mockDrawingContext.proxy());
  }
  
  /**
   * Tests getChildrenInSpecifiedBounds().
   */
  public void testGetChildrenInSpecifiedBounds() {
    // we need to catch the following elements: the ones enclosed in the
    // bounds and the ones overlapping the bounds, but not the ones
    // enclosing the bounds
    MyCompositeNode compositeNode = new MyCompositeNode();
    compositeNode.setParent(parent);
    compositeNode.setOrigin(0, 0);
    compositeNode.setSize(200, 200);
    MyCompositeNode child1 = new MyCompositeNode();
    MyCompositeNode child2 = new MyCompositeNode();
    MyCompositeNode child3 = new MyCompositeNode();
    child1.setOrigin(20, 20); // (20, 20) to (30, 30)
    child1.setSize(10, 10);
    child2.setOrigin(30, 40); // (30, 40) to (40, 50)
    child2.setSize(10, 10);
    
    child3.setOrigin(70, 70); // (70, 70) to (100, 100)
    child3.setSize(30, 30);
    MyCompositeNode child3_1 = new MyCompositeNode();
    child3_1.setOrigin(2, 2); // (72, 72) to (82, 82)
    child3_1.setSize(10, 10);
    child3.addChild(child3_1);
    compositeNode.addChild(child1);
    compositeNode.addChild(child2);
    compositeNode.addChild(child3);
    
    Collection<DiagramElement> collection = new ArrayList<DiagramElement>();
    Rectangle2D bounds = new Rectangle2D.Double();
    // first case: includes the whole
    bounds.setRect(-10, -10, 250, 250);
    compositeNode.getChildrenInSpecifiedBounds(bounds, collection);
    assertEquals(4, collection.size());
    // second case: outside
    collection = new ArrayList<DiagramElement>();
    bounds.setRect(300, 200, 250, 250);
    compositeNode.getChildrenInSpecifiedBounds(bounds, collection);
    assertEquals(0, collection.size());
    // third case: intersects parent and two children, one is contained,
    // one intersected
    collection = new ArrayList<DiagramElement>();
    bounds.setRect(0, 15, 45, 45); // from (0, 15) to (45, 60)
    compositeNode.getChildrenInSpecifiedBounds(bounds, collection);
    assertEquals(2, collection.size());
    assertTrue(collection.contains(child1));
    assertTrue(collection.contains(child2));
    
    // fourth case: intersects parent but no child is intersected
    collection = new ArrayList<DiagramElement>();
    bounds.setRect(-5, -5, 20, 20); // from (-5, -5) to (15, 15)
    compositeNode.getChildrenInSpecifiedBounds(bounds, collection);
    assertEquals(0, collection.size());
    
    // fifth case: intersect a  child that has children, at least one of the
    // child's children is intersected
    collection = new ArrayList<DiagramElement>();
    bounds.setRect(70, 70, 10, 10); // from (70, 70) to (80, 80)
    compositeNode.getChildrenInSpecifiedBounds(bounds, collection);
    assertEquals(2, collection.size());
    assertTrue(collection.contains(child3));
    assertTrue(collection.contains(child3_1));
    
    // sixth case: intersects a child that has children, no children are
    // intersected
    collection = new ArrayList<DiagramElement>();
    bounds.setRect(90, 90, 5, 5); // from (70, 70) to (80, 80)
    compositeNode.getChildrenInSpecifiedBounds(bounds, collection);
    assertEquals(1, collection.size());
    assertTrue(collection.contains(child3));
  }

  
  /**
   * Tests the bring to front method.
   */
  public void testBringToFront() {
    MyCompositeNode node = new MyCompositeNode();
    MyCompositeNode child1 = new MyCompositeNode();
    MyCompositeNode child2 = new MyCompositeNode();
    MyCompositeNode child3 = new MyCompositeNode();
    node.addChild(child1);
    node.addChild(child2);
    node.addChild(child3);
    node.bringChildToFront(child1);
    assertEquals("child1 should now be at index 2",
      2, node.getChildren().indexOf(child1));
  }
  
  /**
   * Tests the putToBack() method.
   */
  public void testPutToBack() {
    MyCompositeNode node = new MyCompositeNode();
    MyCompositeNode child1 = new MyCompositeNode();
    MyCompositeNode child2 = new MyCompositeNode();
    MyCompositeNode child3 = new MyCompositeNode();
    node.addChild(child1);
    node.addChild(child2);
    node.addChild(child3);
    node.putChildToBack(child3);
    assertEquals("child3 should now be at index 0",
      0, node.getChildren().indexOf(child3));    
  }

  /**
   * Tests the cascading of the nodeMoved() notification.
   */
  public void testNodeMovedWithNestedChildren() {
    MyCompositeNode node = new MyCompositeNode();
    node.setParent(parent);
    MyCompositeNode child1 = new MyCompositeNode();
    MyCompositeNode child2 = new MyCompositeNode();
    Mock mockListener1 = mock(NodeChangeListener.class);
    Mock mockListener2 = mock(NodeChangeListener.class);
    Mock mockListener3 = mock(NodeChangeListener.class);
    
    node.addNodeChangeListener((NodeChangeListener) mockListener1.proxy());
    child1.addNodeChangeListener((NodeChangeListener) mockListener2.proxy());
    child2.addNodeChangeListener((NodeChangeListener) mockListener3.proxy());
    node.addChild(child1);
    node.addChild(child2);
    
    mockListener1.expects(once()).method("nodeMoved");
    mockListener2.expects(once()).method("nodeMoved");
    mockListener3.expects(once()).method("nodeMoved");
    node.setAbsolutePos(300, 400);
  }
}
