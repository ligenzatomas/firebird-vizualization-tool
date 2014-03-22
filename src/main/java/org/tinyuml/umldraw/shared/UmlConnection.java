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
package org.tinyuml.umldraw.shared;

import org.tinyuml.draw.Connection;
import org.tinyuml.model.Relation;

/**
 * The base interface for UML connections.
 * @author Wei-ju Wu
 * @version 1.0
 */
public interface UmlConnection extends Connection, UmlDiagramElement {

  /**
   * Sets the Relation object.
   * @param aRelation the Relation object
   */
  void setRelation(Relation aRelation);

  /**
   * Sets the Connection object.
   * @param aConnection the Connection object
   */
  void setConnection(Connection aConnection);

  /**
   * Returns the embedded connection.
   * @return the embedded connection
   */
  Connection getConnection();
}
