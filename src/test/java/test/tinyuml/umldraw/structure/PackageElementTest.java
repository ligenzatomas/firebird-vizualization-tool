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
package test.tinyuml.umldraw.structure;

import org.jmock.Mock;
import org.jmock.cglib.MockObjectTestCase;
import org.tinyuml.draw.NullElement;
import org.tinyuml.model.UmlClass;
import org.tinyuml.model.UmlPackage;
import org.tinyuml.umldraw.structure.PackageElement;
import org.tinyuml.umldraw.shared.GeneralDiagram;
import org.tinyuml.model.UmlModel;
import org.tinyuml.umldraw.structure.ClassElement;
import org.tinyuml.umldraw.structure.StructureDiagram;
import test.tinyuml.draw.MyAbsoluteParent;

/**
 * A test class for PackageShape.
 * @author Wei-ju Wu
 * @version 1.0
 */
public class PackageElementTest extends MockObjectTestCase {

  private Mock mockUmlModel = mock(UmlModel.class);
  private GeneralDiagram diagram =
    new StructureDiagram((UmlModel) mockUmlModel.proxy());
  private UmlPackage pkg = (UmlPackage) UmlPackage.getPrototype().clone();
  private PackageElement pkgElem = (PackageElement)
    PackageElement.getPrototype().clone();
  private MyAbsoluteParent parent = new MyAbsoluteParent();

  /**
   * {@inheritDoc}
   */
  @Override
  protected void setUp() {
    pkgElem.setUmlPackage(pkg);
    pkgElem.setParent(parent);
    pkgElem.setOrigin(0, 0);
    pkgElem.setSize(100, 80);
  }

  /**
   * Tests the basic setter methods.
   */
  public void testBasicSetters() {
    assertTrue(pkgElem.isNestable());
    assertTrue(pkgElem.canNestElements());
    
    pkg.setName("mypackage");
    assertEquals("mypackage", pkgElem.getLabelText());
    assertEquals(pkg, pkgElem.getModelElement());
    
    // sets the name
    pkgElem.setLabelText("yourpackage");
    assertEquals("yourpackage", pkg.getName());
    assertEquals("yourpackage", pkgElem.getLabelText());
  }
  
  /**
   * Tests that addChild() puts the node into the main compartment.
   */
  public void testAddRemoveChild() {
    ClassElement clss = (ClassElement) ClassElement.getPrototype().clone();
    clss.setModelElement((UmlClass) UmlClass.getPrototype().clone());
    clss.getModelElement().setName("myclass");
    clss.setOrigin(0, 0);
    clss.setSize(100, 100);
    pkgElem.addChild(clss);
    pkgElem.addChild(clss);

    assertTrue(pkgElem.getMainCompartment().getChildren().contains(clss));
    assertEquals(pkgElem.getMainCompartment(), clss.getParent());
    assertEquals(1, pkgElem.getMainCompartment().getChildren().size());
    
    assertEquals(1, pkgElem.getChildren().size());
    assertTrue(pkgElem.getChildren().contains(clss));
    assertEquals(clss, pkgElem.getChildAt(10, 30));
    
    // Remove the child
    pkgElem.removeChild(clss);
    assertFalse(pkgElem.getMainCompartment().getChildren().contains(clss));
    assertEquals(0, pkgElem.getMainCompartment().getChildren().size());
    
    assertEquals(0, pkgElem.getChildren().size());
    assertFalse(pkgElem.getChildren().contains(clss));
    assertEquals(NullElement.getInstance(), pkgElem.getChildAt(10, 30));
  }
  
  /**
   * Test adding a Package nested in a Package.
   */
  public void testAddDoubleNestedPackage() {
    PackageElement nestedPackage = (PackageElement) pkgElem.clone();
    nestedPackage.setOrigin(5, 5);
    nestedPackage.setSize(80, 50);
    pkgElem.addChild(nestedPackage);
    assertTrue(pkgElem.getChildren().contains(nestedPackage));
    assertEquals(pkgElem.getMainCompartment(), nestedPackage.getParent());
    assertEquals(parent, pkgElem.getParent());
    assertEquals(pkgElem, pkgElem.getMainCompartment().getParent());
    assertEquals(5.0, nestedPackage.getAbsoluteX1());
    assertEquals(17.0, nestedPackage.getAbsoluteY1());
    
    // now move the outer package
    pkgElem.setAbsolutePos(100, 100);
    assertEquals(105.0, nestedPackage.getAbsoluteX1());
    assertEquals(117.0, nestedPackage.getAbsoluteY1());
  }

  /**
   * Tests the clone() method.
   */
  public void testClone() {
    pkg.setName("mypackage");
    pkgElem.setUmlPackage(pkg);
    PackageElement cloned = (PackageElement) pkgElem.clone();

    assertTrue(pkgElem != cloned);
    assertEquals(pkgElem.getModelElement().getName(),
                 cloned.getModelElement().getName());
    assertTrue(pkgElem.getModelElement() != cloned.getModelElement());
    
    assertTrue(pkgElem.getMainLabel() != cloned.getMainLabel());
    assertTrue(cloned.getMainLabel().getSource() == cloned);
    assertTrue(cloned.getMainLabel().getParent() ==
               cloned.getMainCompartment());
    
    assertTrue(pkgElem.getTabCompartment() != cloned.getTabCompartment());
    assertTrue(cloned.getTabCompartment().getParent() == cloned);

    assertTrue(pkgElem.getMainCompartment() != cloned.getMainCompartment());
    assertTrue(cloned.getMainCompartment().getParent() == cloned);
    assertEquals(1, cloned.getMainCompartment().getLabels().size());
    assertTrue(cloned.getMainCompartment().getLabels()
                .contains(cloned.getMainLabel()));
  }
}
