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
package org.tinyuml.umldraw.sequence;

import java.util.HashMap;
import java.util.Map;
import org.tinyuml.model.ElementType;
import org.tinyuml.model.RelationType;
import org.tinyuml.model.UmlLifeLine;
import org.tinyuml.model.UmlModel;
import org.tinyuml.model.UmlRelation;
import org.tinyuml.umldraw.shared.GeneralDiagram;
import org.tinyuml.umldraw.shared.NoteConnection;
import org.tinyuml.umldraw.shared.NoteElement;
import org.tinyuml.umldraw.shared.UmlConnection;
import org.tinyuml.umldraw.shared.UmlDiagramElement;

/**
 * This class specializes on GeneralDiagram, providing the elements available
 * in a sequence diagram.
 *
 * @author Wei-ju Wu
 * @version 1.0
 */
public class SequenceDiagram extends GeneralDiagram {

  /**
   * Constructor.
   * @param umlModel the Uml model
   */
  public SequenceDiagram(UmlModel umlModel) {
    super(umlModel);
  }

  /**
   * {@inheritDoc}
   */
  protected Map<ElementType, UmlDiagramElement> setupElementPrototypeMap() {
    Map<ElementType, UmlDiagramElement> elementPrototypes =
      new HashMap<ElementType, UmlDiagramElement>();

    NoteElement notePrototype = (NoteElement)
      NoteElement.getPrototype().clone();
    elementPrototypes.put(ElementType.NOTE, notePrototype);
    LifeLineElement lifeLinePrototype = (LifeLineElement)
      LifeLineElement.getPrototype().clone();
    UmlLifeLine umlLifeLine = (UmlLifeLine) UmlLifeLine.getPrototype().clone();
    lifeLinePrototype.setModelElement(umlLifeLine);
    umlLifeLine.setName("Lifeline 1");
    elementPrototypes.put(ElementType.LIFE_LINE, lifeLinePrototype);
    return elementPrototypes;
  }

  /**
   * {@inheritDoc}
   */
  protected Map<RelationType, UmlConnection> setupConnectionPrototypeMap() {
    Map<RelationType, UmlConnection> connectionPrototypes =
      new HashMap<RelationType, UmlConnection>();
    connectionPrototypes.put(RelationType.NOTE_CONNECTOR,
      NoteConnection.getPrototype());
    UmlRelation messageRelation = new UmlRelation();
    messageRelation.setName("message()");
    SynchronousMessageConnection msgConn = (SynchronousMessageConnection)
      SynchronousMessageConnection.getPrototype().clone();
    msgConn.setRelation(messageRelation);
    connectionPrototypes.put(RelationType.MESSAGE, msgConn);
    return connectionPrototypes;
  }
}
