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
package org.tinyuml.ui.model;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import org.tinyuml.model.DatabaseModel;
import org.tinyuml.model.UmlDiagram;
import org.tinyuml.model.UmlModel;

/**
 * A serializable project class to store model information and open diagrams
 * in order to persist and restore a configuration.
 *
 * @author Wei-ju Wu
 * @version 1.0
 */
public class TinyUmlProject implements Serializable {

  private static final long serialVersionUID = -7416279147552646801L;
  private UmlModel model;
  private DatabaseModel databaseModel;
  private List<UmlDiagram> openDiagrams;

  /**
   * Constructor.
   * @param aModel the UmlModel object
   */
  public TinyUmlProject(UmlModel aModel, DatabaseModel aDatabaseModel) {
    model = aModel;
	databaseModel = aDatabaseModel;
    openDiagrams = new LinkedList<UmlDiagram>();
  }

  /**
   * Returns the model.
   * @return the model
   */
  public UmlModel getModel() { return model; }
  
  public DatabaseModel getDatabaseModel() { return databaseModel; }

  /**
   * Returns the open diagrams.
   * @return the open diagrams
   */
  public List<UmlDiagram> getOpenDiagrams() { return openDiagrams; }

  /**
   * Adds an open diagram.
   * @param diagram the diagram to add
   */
  public void addOpenDiagram(UmlDiagram diagram) {
    openDiagrams.add(diagram);
  }
}
