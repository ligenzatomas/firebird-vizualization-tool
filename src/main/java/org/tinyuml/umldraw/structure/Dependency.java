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
package org.tinyuml.umldraw.structure;

import org.tinyuml.draw.DrawingContext;
import org.tinyuml.draw.SimpleConnection;
import org.tinyuml.umldraw.shared.BaseConnection;
import org.tinyuml.draw.SimpleArrowTip;

/**
 * This class implements a dependency. It is implemented by inheritance and
 * overrides the draw() method completely.
 *
 * @author Wei-ju Wu
 * @version 1.0
 */
public final class Dependency extends BaseConnection {

  private static final long serialVersionUID = -4618365497931544032L;
  private static Dependency prototype;

  /**
   * Returns the prototype instance.
   * @return the prototype instance
   */
  public static Dependency getPrototype() {
    if (prototype == null) prototype = new Dependency();
    return prototype;
  }

  /**
   * Constructor.
   */
  private Dependency() {
    setConnection(new SimpleConnection());
    setIsDashed(true);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void draw(DrawingContext drawingContext) {
    super.draw(drawingContext);
    // Draw the arrow here, has to be rotated around the tip
    new SimpleArrowTip().draw(drawingContext, getEndPoint2(),
      calculateRotationInEndPoint2());
  }
}
