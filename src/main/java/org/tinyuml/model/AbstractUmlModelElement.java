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
package org.tinyuml.model;

import java.util.Collection;
import java.util.HashSet;

/**
 * This class implements a base UmlModelElement class.
 *
 * @author Wei-ju Wu
 * @version 1.0
 */
public abstract class AbstractUmlModelElement extends DefaultNamedElement
implements UmlModelElement {

  private static final long serialVersionUID = -2557948517888179174L;
  private Collection<UmlModelElementListener> modelListeners =
    new HashSet<UmlModelElementListener>();

  /**
   * {@inheritDoc}
   */
  public void addModelElementListener(UmlModelElementListener l) {
    modelListeners.add(l);
  }

  /**
   * {@inheritDoc}
   */
  public void removeModelElementListener(UmlModelElementListener l) {
    modelListeners.remove(l);
  }

  /**
   * {@inheritDoc}
   */
  public Collection<UmlModelElementListener> getModelElementListeners() {
    return modelListeners;
  }

  /**
   * Notifies the listeners that this element has changed in some way.
   */
  protected void notifyElementChanged() {
    for (UmlModelElementListener l : modelListeners) {
      l.elementChanged(this);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Object clone() {
    AbstractUmlModelElement element = (AbstractUmlModelElement) super.clone();
    element.modelListeners = new HashSet<UmlModelElementListener>();
    return element;
  }
}
