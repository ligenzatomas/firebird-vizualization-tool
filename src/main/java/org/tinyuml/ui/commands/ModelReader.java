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
package org.tinyuml.ui.commands;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import org.tinyuml.ui.model.TinyUmlProject;
import org.tinyuml.util.ApplicationResources;

/**
 * Reads a model from a file. Models are stored and retrieved using
 * Serialization.
 *
 * @author Wei-ju Wu
 * @version 1.0
 */
public final class ModelReader extends FileHandler {

  private static ModelReader instance = new ModelReader();

  /**
   * Returns the singleton instance.
   * @return the singleton instance
   */
  public static ModelReader getInstance() { return instance; }

  /**
   * Reads a TinyUmlProject object from a file.
   * @param file the file
   * @return the TinyUmlProject object
   * @throws IOException if I/O error occurred
   */
  public TinyUmlProject readModel(File file) throws IOException {
    FileInputStream fis = null;
    BufferedInputStream bis = null;
    ObjectInputStream in = null;
    try {
      fis = new FileInputStream(file);
      bis = new BufferedInputStream(fis);
      in = new ObjectInputStream(bis);
      return (TinyUmlProject) in.readObject();
    } catch (ClassNotFoundException ex) {
      throw new IOException(ApplicationResources.getInstance()
        .getString("error.readfile.message"));
    } finally {
      if (in != null) in.close();
      if (bis != null) bis.close();
      if (fis != null) fis.close();
    }
  }

  /**
   * {@inheritDoc}
   */
  public String getSuffix() { return ".tsm"; }
}
