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
package org.tinyuml.ui.diagram;

import java.awt.Dialog;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import org.tinyuml.model.UmlClass;
import org.tinyuml.model.UmlProperty;
import org.tinyuml.model.UmlStereotype;
import org.tinyuml.ui.model.StringTableModel;
import org.tinyuml.umldraw.structure.ClassElement;
import org.tinyuml.util.ApplicationResources;

/**
 * An editor for class properties
 * @author  Wei-ju Wu
 * @version 1.0
 */
@SuppressWarnings("PMD")
public class EditClassDialog extends javax.swing.JDialog {

  private ClassElement classElement;
  private StringTableModel methodTableModel = new StringTableModel();
  private StringTableModel attributeTableModel = new StringTableModel();
  private StringTableModel stereotypeTableModel = new StringTableModel();
  private boolean isOk;

  /** Creates new form EditClassDialog */
  public EditClassDialog(java.awt.Window parent, ClassElement aClassElement,
    boolean modal) {
    super(parent, Dialog.ModalityType.APPLICATION_MODAL);
    classElement = aClassElement;
    myInitModels();
    initComponents();
    myPostInit();
  }

  /**
   * Returns true if ok was clicked.
   * @return true if ok was clicked
   */
  public boolean isOk() { return isOk; }

  /**
   * Initializes the models in this dialog.
   */
  private void myInitModels() {
    UmlClass umlclass = (UmlClass) classElement.getModelElement();
    for (UmlProperty method : umlclass.getMethods()) {
      methodTableModel.addEntry(method.getName());
    }
    for (UmlProperty attribute : umlclass.getAttributes()) {
      attributeTableModel.addEntry(attribute.getName());
    }
    for (UmlStereotype stereotype : umlclass.getStereotypes()) {
      stereotypeTableModel.addEntry(stereotype.getName());
    }
  }

  /**
   * Initialization after model and components are initialized.
   */
  private void myPostInit() {
    docVisibilityCb.setEnabled(false);
    UmlClass umlclass = (UmlClass) classElement.getModelElement();
    abstractCb.setSelected(umlclass.isAbstract());
    attributeVisibilityCb.setSelected(classElement.showAttributes());
    methodVisibilityCb.setSelected(classElement.showOperations());
    stereotypeVisibilityCb.setSelected(classElement.showStereotypes());
    methodTable.getTableHeader().setVisible(false);
    attributeTable.getTableHeader().setVisible(false);
    stereotypeTable.getTableHeader().setVisible(false);
    nameTextField.setText(umlclass.getName());
    getRootPane().setDefaultButton(okButton);
  }

  /**
   * Returns the state of the abstract check box.
   * @return the state of the abstract check box
   */
  public boolean classIsAbstract() { return abstractCb.isSelected(); }

  /**
   * Returns the state of the show methods check box.
   * @return the state of the show methods check box
   */
  public boolean showOperations() { return methodVisibilityCb.isSelected(); }

  /**
   * Returns the state of the show attributes check box.
   * @return the state of the show attributes check box
   */
  public boolean showAttributes() { return attributeVisibilityCb.isSelected(); }

  /**
   * Returns the state of the show stereotypes check box.
   * @return the state of the show stereotypes check box
   */
  public boolean showStereotypes() {
    return stereotypeVisibilityCb.isSelected();
  }

  /**
   * Returns the value in the name text field.
   * @return the name
   */
  @Override
  public String getName() { return nameTextField.getText(); }

  /**
   * Returns the method list entered in the dialog.
   * @return the method list
   */
  public List<UmlProperty> getMethods() {
    List<String> methodStrings =
      ((StringTableModel) methodTable.getModel()).getEntries();
    List<UmlProperty> result = new ArrayList<UmlProperty>();
    for (String methodString : methodStrings) {
      UmlProperty property = (UmlProperty) UmlProperty.getPrototype().clone();
      property.setName(methodString);
      result.add(property);
    }
    return result;
  }

  /**
   * Returns the attribute list entered in the dialog.
   * @return the attribute list
   */
  public List<UmlProperty> getAttributes() {
    List<String> attributeStrings =
      ((StringTableModel) attributeTable.getModel()).getEntries();
    List<UmlProperty> result = new ArrayList<UmlProperty>();
    for (String attributeString : attributeStrings) {
      UmlProperty property = (UmlProperty) UmlProperty.getPrototype().clone();
      property.setName(attributeString);
      result.add(property);
    }
    return result;
  }

  /**
   * Returns the stereotype list entered in the dialog.
   * @return the stereotype list
   */
  public List<UmlStereotype> getStereotypes() {
    List<String> stereoStrings =
      ((StringTableModel) stereotypeTable.getModel()).getEntries();
    List<UmlStereotype> result = new ArrayList<UmlStereotype>();
    for (String stereoString : stereoStrings) {
      UmlStereotype stereotype = (UmlStereotype)
        UmlStereotype.getPrototype().clone();
      stereotype.setName(stereoString);
      result.add(stereotype);
    }
    return result;
  }

  private void moveUpSelectedTableEntry(JTable table) {
    StringTableModel tableModel = (StringTableModel) table.getModel();
    int row = table.getSelectedRow();
    if (row > 0 && row < tableModel.getRowCount()) {
      tableModel.moveUpEntry(row);
      table.setRowSelectionInterval(row - 1, row - 1);
    }
  }

  private void moveDownSelectedTableEntry(JTable table) {
    StringTableModel tableModel = (StringTableModel) table.getModel();
    int row = table.getSelectedRow();
    if (row >= 0 && row < tableModel.getRowCount() - 1) {
      tableModel.moveDownEntry(row);
      table.setRowSelectionInterval(row + 1, row + 1);
    }
  }

  private void deleteSelectedTableEntry(JTable table) {
    StringTableModel tableModel = (StringTableModel) table.getModel();
    int row = table.getSelectedRow();
    if (row >= 0 && row < tableModel.getRowCount()) {
      tableModel.removeEntryAt(row);
    }
  }

  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
  private void initComponents() {
    tabbedPane = new javax.swing.JTabbedPane();
    propertyPanel = new javax.swing.JPanel();
    jPanel1 = new javax.swing.JPanel();
    attribMoveUpButton = new javax.swing.JButton();
    attribMoveDownButton = new javax.swing.JButton();
    attribAddButton = new javax.swing.JButton();
    attribDeleteButton = new javax.swing.JButton();
    attributeVisibilityCb = new javax.swing.JCheckBox();
    jScrollPane1 = new javax.swing.JScrollPane();
    attributeTable = new javax.swing.JTable();
    attributeTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
    jPanel2 = new javax.swing.JPanel();
    methodVisibilityCb = new javax.swing.JCheckBox();
    jScrollPane2 = new javax.swing.JScrollPane();
    methodTable = new javax.swing.JTable();
    methodMoveUpButton = new javax.swing.JButton();
    methodMoveDownButton = new javax.swing.JButton();
    methodAddButton = new javax.swing.JButton();
    methodDeleteButton = new javax.swing.JButton();
    abstractCb = new javax.swing.JCheckBox();
    jLabel1 = new javax.swing.JLabel();
    nameTextField = new javax.swing.JTextField();
    jPanel3 = new javax.swing.JPanel();
    stereoScrollPane = new javax.swing.JScrollPane();
    stereotypeTable = new javax.swing.JTable();
    stereotypeMoveUpButton = new javax.swing.JButton();
    stereotypeMoveDownButton = new javax.swing.JButton();
    deleteStereotypeButton = new javax.swing.JButton();
    addStereotypeButton = new javax.swing.JButton();
    stereotypeVisibilityCb = new javax.swing.JCheckBox();
    jPanel4 = new javax.swing.JPanel();
    docVisibilityCb = new javax.swing.JCheckBox();
    jScrollPane3 = new javax.swing.JScrollPane();
    docTextArea = new javax.swing.JTextArea();
    okButton = new javax.swing.JButton();
    cancelButton = new javax.swing.JButton();

    setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
    setTitle(ApplicationResources.getInstance().getString("dialog.classproperties.title"));
    jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(ApplicationResources.getInstance().getString("stdcaption.attributes")));
    attribMoveUpButton.setText(ApplicationResources.getInstance().getString("stdcaption.moveup"));
    attribMoveUpButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        moveUpSelectedAttribute(evt);
      }
    });

    attribMoveDownButton.setText(ApplicationResources.getInstance().getString("stdcaption.movedown"));
    attribMoveDownButton.setActionCommand(ApplicationResources.getInstance().getString("dialog.text.movedown"));
    attribMoveDownButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        moveDownSelectedAttribute(evt);
      }
    });

    attribAddButton.setText(ApplicationResources.getInstance().getString("stdcaption.add"));
    attribAddButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        addAttribute(evt);
      }
    });

    attribDeleteButton.setText(ApplicationResources.getInstance().getString("stdcaption.delete"));
    attribDeleteButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        deleteSelectedAttribute(evt);
      }
    });

    attributeVisibilityCb.setText(ApplicationResources.getInstance().getString("dialog.visible.text"));
    attributeVisibilityCb.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
    attributeVisibilityCb.setLabel(ApplicationResources.getInstance().getString("stdcaption.visible"));
    attributeVisibilityCb.setMargin(new java.awt.Insets(0, 0, 0, 0));

    jScrollPane1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
    attributeTable.setModel(attributeTableModel);
    jScrollPane1.setViewportView(attributeTable);

    javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
    jPanel1.setLayout(jPanel1Layout);
    jPanel1Layout.setHorizontalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel1Layout.createSequentialGroup()
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(attributeVisibilityCb)
          .addGroup(jPanel1Layout.createSequentialGroup()
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
              .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(attribAddButton, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(attribDeleteButton, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE))
              .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 341, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addComponent(attribMoveDownButton, javax.swing.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)
              .addComponent(attribMoveUpButton, javax.swing.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE))))
        .addContainerGap())
    );
    jPanel1Layout.setVerticalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel1Layout.createSequentialGroup()
        .addComponent(attributeVisibilityCb)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(jPanel1Layout.createSequentialGroup()
            .addComponent(attribMoveUpButton)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(attribMoveDownButton))
          .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(attribDeleteButton)
          .addComponent(attribAddButton))
        .addContainerGap())
    );

    jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(ApplicationResources.getInstance().getString("stdcaption.methods")));
    methodVisibilityCb.setSelected(true);
    methodVisibilityCb.setText(ApplicationResources.getInstance().getString("dialog.visible.text"));
    methodVisibilityCb.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
    methodVisibilityCb.setLabel(ApplicationResources.getInstance().getString("stdcaption.visible"));
    methodVisibilityCb.setMargin(new java.awt.Insets(0, 0, 0, 0));

    jScrollPane2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
    methodTable.setModel(methodTableModel);
    jScrollPane2.setViewportView(methodTable);

    methodMoveUpButton.setText(ApplicationResources.getInstance().getString("stdcaption.moveup"));
    methodMoveUpButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        moveUpSelectedMethod(evt);
      }
    });

    methodMoveDownButton.setText(ApplicationResources.getInstance().getString("stdcaption.movedown"));
    methodMoveDownButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        moveDownSelectedMethod(evt);
      }
    });

    methodAddButton.setText(ApplicationResources.getInstance().getString("stdcaption.add"));
    methodAddButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        addMethod(evt);
      }
    });

    methodDeleteButton.setText(ApplicationResources.getInstance().getString("stdcaption.delete"));
    methodDeleteButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        deleteSelectedMethod(evt);
      }
    });

    javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
    jPanel2.setLayout(jPanel2Layout);
    jPanel2Layout.setHorizontalGroup(
      jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel2Layout.createSequentialGroup()
        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(methodVisibilityCb)
          .addGroup(jPanel2Layout.createSequentialGroup()
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(methodAddButton, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(methodDeleteButton, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE))
              .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 342, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
              .addComponent(methodMoveUpButton, javax.swing.GroupLayout.DEFAULT_SIZE, 108, Short.MAX_VALUE)
              .addComponent(methodMoveDownButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 108, Short.MAX_VALUE))))
        .addContainerGap())
    );
    jPanel2Layout.setVerticalGroup(
      jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel2Layout.createSequentialGroup()
        .addComponent(methodVisibilityCb)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(jPanel2Layout.createSequentialGroup()
            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
              .addComponent(methodDeleteButton)
              .addComponent(methodAddButton)))
          .addGroup(jPanel2Layout.createSequentialGroup()
            .addComponent(methodMoveUpButton)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(methodMoveDownButton)))
        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    abstractCb.setText(ApplicationResources.getInstance().getString("stdcaption.abstract"));
    abstractCb.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
    abstractCb.setMargin(new java.awt.Insets(0, 0, 0, 0));

    jLabel1.setText(ApplicationResources.getInstance().getString("stdcaption.name"));

    javax.swing.GroupLayout propertyPanelLayout = new javax.swing.GroupLayout(propertyPanel);
    propertyPanel.setLayout(propertyPanelLayout);
    propertyPanelLayout.setHorizontalGroup(
      propertyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(propertyPanelLayout.createSequentialGroup()
        .addGroup(propertyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(propertyPanelLayout.createSequentialGroup()
            .addContainerGap()
            .addComponent(jLabel1)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(nameTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 402, Short.MAX_VALUE))
          .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(jPanel2, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addGroup(propertyPanelLayout.createSequentialGroup()
            .addContainerGap()
            .addComponent(abstractCb)))
        .addContainerGap())
    );
    propertyPanelLayout.setVerticalGroup(
      propertyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(propertyPanelLayout.createSequentialGroup()
        .addContainerGap()
        .addGroup(propertyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel1)
          .addComponent(nameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(abstractCb)
        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );
    tabbedPane.addTab(ApplicationResources.getInstance().getString("stdcaption.properties"), propertyPanel);

    stereoScrollPane.setBorder(javax.swing.BorderFactory.createEtchedBorder());
    stereotypeTable.setModel(stereotypeTableModel);
    stereoScrollPane.setViewportView(stereotypeTable);

    stereotypeMoveUpButton.setText(ApplicationResources.getInstance().getString("stdcaption.moveup"));
    stereotypeMoveUpButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        moveUpSelectedStereotype(evt);
      }
    });

    stereotypeMoveDownButton.setText(ApplicationResources.getInstance().getString("stdcaption.movedown"));
    stereotypeMoveDownButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        moveDownSelectedStereotype(evt);
      }
    });

    deleteStereotypeButton.setText(ApplicationResources.getInstance().getString("stdcaption.delete"));
    deleteStereotypeButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        deleteSelectedStereotype(evt);
      }
    });

    addStereotypeButton.setText(ApplicationResources.getInstance().getString("stdcaption.add"));
    addStereotypeButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        addStereotype(evt);
      }
    });

    stereotypeVisibilityCb.setText(ApplicationResources.getInstance().getString("stdcaption.visible"));
    stereotypeVisibilityCb.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
    stereotypeVisibilityCb.setMargin(new java.awt.Insets(0, 0, 0, 0));

    javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
    jPanel3.setLayout(jPanel3Layout);
    jPanel3Layout.setHorizontalGroup(
      jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel3Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(jPanel3Layout.createSequentialGroup()
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addGroup(jPanel3Layout.createSequentialGroup()
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 163, Short.MAX_VALUE)
                .addComponent(addStereotypeButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(deleteStereotypeButton))
              .addComponent(stereoScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 367, Short.MAX_VALUE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
              .addComponent(stereotypeMoveDownButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
              .addComponent(stereotypeMoveUpButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
          .addComponent(stereotypeVisibilityCb))
        .addContainerGap())
    );
    jPanel3Layout.setVerticalGroup(
      jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel3Layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(stereotypeVisibilityCb)
        .addGap(15, 15, 15)
        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(jPanel3Layout.createSequentialGroup()
            .addComponent(stereotypeMoveUpButton)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(stereotypeMoveDownButton))
          .addComponent(stereoScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(deleteStereotypeButton)
          .addComponent(addStereotypeButton))
        .addContainerGap(315, Short.MAX_VALUE))
    );
    tabbedPane.addTab(ApplicationResources.getInstance().getString("stdcaption.stereotypes"), jPanel3);

    docVisibilityCb.setText(ApplicationResources.getInstance().getString("stdcaption.documentation"));
    docVisibilityCb.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
    docVisibilityCb.setMargin(new java.awt.Insets(0, 0, 0, 0));

    jScrollPane3.setBorder(javax.swing.BorderFactory.createEtchedBorder());
    docTextArea.setColumns(20);
    docTextArea.setRows(5);
    jScrollPane3.setViewportView(docTextArea);

    javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
    jPanel4.setLayout(jPanel4Layout);
    jPanel4Layout.setHorizontalGroup(
      jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel4Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 472, Short.MAX_VALUE)
          .addComponent(docVisibilityCb))
        .addContainerGap())
    );
    jPanel4Layout.setVerticalGroup(
      jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel4Layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(docVisibilityCb)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addContainerGap(301, Short.MAX_VALUE))
    );
    tabbedPane.addTab(ApplicationResources.getInstance().getString("stdcaption.documentation"), jPanel4);

    okButton.setText(ApplicationResources.getInstance().getString("stdcaption.ok"));
    okButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        closeDialog(evt);
      }
    });

    cancelButton.setText(ApplicationResources.getInstance().getString("stdcaption.cancel"));
    cancelButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cancelDialog(evt);
      }
    });

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addComponent(tabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 497, Short.MAX_VALUE)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
        .addContainerGap(320, Short.MAX_VALUE)
        .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(cancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
        .addComponent(tabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 533, Short.MAX_VALUE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(okButton)
          .addComponent(cancelButton))
        .addContainerGap())
    );
    pack();
  }// </editor-fold>//GEN-END:initComponents

  private void moveDownSelectedStereotype(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_moveDownSelectedStereotype
    moveDownSelectedTableEntry(stereotypeTable);
  }//GEN-LAST:event_moveDownSelectedStereotype

  private void moveUpSelectedStereotype(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_moveUpSelectedStereotype
    moveUpSelectedTableEntry(stereotypeTable);
  }//GEN-LAST:event_moveUpSelectedStereotype

  private void deleteSelectedStereotype(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteSelectedStereotype
    deleteSelectedTableEntry(stereotypeTable);
  }//GEN-LAST:event_deleteSelectedStereotype

  private void addStereotype(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addStereotype
    ((StringTableModel) stereotypeTable.getModel()).addEntry("<<stereotype>>");
  }//GEN-LAST:event_addStereotype

  private void cancelDialog(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelDialog
    dispose();
  }//GEN-LAST:event_cancelDialog

  private void moveDownSelectedMethod(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_moveDownSelectedMethod
    moveDownSelectedTableEntry(methodTable);
  }//GEN-LAST:event_moveDownSelectedMethod

  private void moveUpSelectedMethod(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_moveUpSelectedMethod
    moveUpSelectedTableEntry(methodTable);
  }//GEN-LAST:event_moveUpSelectedMethod

  private void deleteSelectedMethod(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteSelectedMethod
    deleteSelectedTableEntry(methodTable);
  }//GEN-LAST:event_deleteSelectedMethod

  private void addMethod(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addMethod
    ((StringTableModel) methodTable.getModel()).addEntry(
      "+method(param1, param2) : Type");
  }//GEN-LAST:event_addMethod

  private void moveDownSelectedAttribute(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_moveDownSelectedAttribute
    moveDownSelectedTableEntry(attributeTable);
  }//GEN-LAST:event_moveDownSelectedAttribute

  private void moveUpSelectedAttribute(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_moveUpSelectedAttribute
    moveUpSelectedTableEntry(attributeTable);
  }//GEN-LAST:event_moveUpSelectedAttribute

  private void deleteSelectedAttribute(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteSelectedAttribute
    deleteSelectedTableEntry(attributeTable);
  }//GEN-LAST:event_deleteSelectedAttribute

  private void addAttribute(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addAttribute
    ((StringTableModel) attributeTable.getModel()).addEntry("attribute : Type");
  }//GEN-LAST:event_addAttribute

  private void closeDialog(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeDialog
    isOk = true;
    dispose();
  }//GEN-LAST:event_closeDialog

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JCheckBox abstractCb;
  private javax.swing.JButton addStereotypeButton;
  private javax.swing.JButton attribAddButton;
  private javax.swing.JButton attribDeleteButton;
  private javax.swing.JButton attribMoveDownButton;
  private javax.swing.JButton attribMoveUpButton;
  private javax.swing.JTable attributeTable;
  private javax.swing.JCheckBox attributeVisibilityCb;
  private javax.swing.JButton cancelButton;
  private javax.swing.JButton deleteStereotypeButton;
  private javax.swing.JTextArea docTextArea;
  private javax.swing.JCheckBox docVisibilityCb;
  private javax.swing.JLabel jLabel1;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JPanel jPanel2;
  private javax.swing.JPanel jPanel3;
  private javax.swing.JPanel jPanel4;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JScrollPane jScrollPane2;
  private javax.swing.JScrollPane jScrollPane3;
  private javax.swing.JButton methodAddButton;
  private javax.swing.JButton methodDeleteButton;
  private javax.swing.JButton methodMoveDownButton;
  private javax.swing.JButton methodMoveUpButton;
  private javax.swing.JTable methodTable;
  private javax.swing.JCheckBox methodVisibilityCb;
  private javax.swing.JTextField nameTextField;
  private javax.swing.JButton okButton;
  private javax.swing.JPanel propertyPanel;
  private javax.swing.JScrollPane stereoScrollPane;
  private javax.swing.JButton stereotypeMoveDownButton;
  private javax.swing.JButton stereotypeMoveUpButton;
  private javax.swing.JTable stereotypeTable;
  private javax.swing.JCheckBox stereotypeVisibilityCb;
  private javax.swing.JTabbedPane tabbedPane;
  // End of variables declaration//GEN-END:variables

}
