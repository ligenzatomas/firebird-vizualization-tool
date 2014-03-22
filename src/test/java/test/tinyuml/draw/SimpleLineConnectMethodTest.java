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

import java.awt.geom.Point2D;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import org.tinyuml.draw.CompositeNode;
import org.tinyuml.draw.Connection;
import org.tinyuml.draw.DrawingContext;
import org.tinyuml.draw.Node;
import org.tinyuml.draw.SimpleConnection;
import org.tinyuml.draw.SimpleLineConnectMethod;

/**
 * A test class for SimpleLineConnectMethod.
 * @author Wei-ju Wu
 * @version 1.0
 */
public class SimpleLineConnectMethodTest extends MockObjectTestCase {

  private Connection conn = new SimpleConnection();
  private Node node1 = new MyCompositeNode();
  private Node node2 = new MyCompositeNode();
  private CompositeNode parent = new MyAbsoluteParent();
  private Mock mockDrawingContext = mock(DrawingContext.class);

  /**
   * {@inheritDoc}
   */
  @Override
  protected void setUp() throws Exception {
    node1.setParent(parent);
    node2.setParent(parent);
  }
  
  /**
   * Tests the generateAndSetPointsToConnection() method.
   */
  public void testGenerateAndSetPointsToConnection() {
    SimpleLineConnectMethod.getInstance().generateAndSetPointsToConnection(conn,
      node1, node2, null, null);
    assertEquals(2, conn.getPoints().size());
  }
  
  /**
   * Tests the draw() method.
   */
  public void testDraw() {
    Point2D p1 = new Point2D.Double(100, 6);
    Point2D p2 = new Point2D.Double(200, 16);
    mockDrawingContext.expects(once()).method("drawLine")
      .with(eq(p1.getX()), eq(p1.getY()), eq(p2.getX()), eq(p2.getY()));
    SimpleLineConnectMethod.getInstance().drawLineSegments((DrawingContext)
      mockDrawingContext.proxy(), p1, p2);
  }
}