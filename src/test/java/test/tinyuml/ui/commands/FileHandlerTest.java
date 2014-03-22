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
package test.tinyuml.ui.commands;

import java.io.File;
import junit.framework.*;
import org.tinyuml.ui.commands.FileHandler;
import org.tinyuml.ui.commands.ModelReader;
import org.tinyuml.ui.commands.ModelWriter;
import org.tinyuml.ui.commands.PngExporter;
import org.tinyuml.ui.commands.SvgExporter;

/**
 * Tests the abstract FileHandler class.
 * @author Wei-ju Wu
 * @version 1.0
 */
public class FileHandlerTest extends TestCase {
  
  private static class MyFileHandler extends FileHandler {
    protected String getSuffix() { return ".suff"; }
    public File getFileWithExtension(File file) {
      return super.getFileWithExtension(file);
    }
  }
  private static class MyPngExporter extends PngExporter {
    public String getSuffix() { return super.getSuffix(); }
  }
  
  private static class MySvgExporter extends SvgExporter {
    public String getSuffix() { return super.getSuffix(); }
  }
  
  /**
   * Tests the getFileWithExtension() method.
   */
  public void testGetFileWithExtension() {
    File withExtension = new File("myfile.suff");
    File withoutExtension = new File("myfile");
    MyFileHandler filehandler = new MyFileHandler();
    assertEquals("myfile.suff",
      filehandler.getFileWithExtension(withExtension).getName());
    assertEquals("myfile.suff",
      filehandler.getFileWithExtension(withoutExtension).getName());
  }
  
  /**
   * Tests the suffixes.
   */
  public void testSuffixes() {
    assertEquals(".tsm", ModelReader.getInstance().getSuffix());
    assertEquals(".tsm", ModelWriter.getInstance().getSuffix());
    assertEquals(".png", new MyPngExporter().getSuffix());
    assertEquals(".svg", new MySvgExporter().getSuffix());
  }
}
