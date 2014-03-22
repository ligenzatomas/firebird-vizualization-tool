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
import java.text.ParseException;
import javax.swing.JComboBox;
import org.tinyuml.model.Multiplicity;
import org.tinyuml.model.Relation.ReadingDirection;
import org.tinyuml.model.UmlRelation;
import org.tinyuml.umldraw.structure.Association;
import org.tinyuml.util.ApplicationResources;

/**
 * An edit dialog for associations.
 * @author  Wei-ju Wu
 * @version 1.0
 */
@SuppressWarnings("PMD")
public class EditAssociationDialog extends javax.swing.JDialog {

  private Association association;

  /**
   * Creates new form EditAssociationDialog
   * @param parent the parent frame
   * @param anAssociation the edited Association
   * @param modal whether the dialog is to be modal
   */
  public EditAssociationDialog(java.awt.Window parent,
      Association anAssociation, boolean modal) {
    super(parent, Dialog.ModalityType.APPLICATION_MODAL);
    association = anAssociation;
    initComponents();
    myPostInit();
  }

  /**
   * Post-Initialization code.
   */
  private void myPostInit() {
    getRootPane().setDefaultButton(okButton);
    assocNameTextField.setText(association.getModelElement().getName());
    showMultiplicitiesCb.setSelected(association.showMultiplicities());
    nameVisibilityCb.setSelected(association.showName());
    UmlRelation relation = (UmlRelation) association.getModelElement();
    ReadingDirection direction = relation.getNameReadingDirection();
    switch (direction) {
      case LEFT_RIGHT:
        l2rRadioButton.setSelected(true);
        break;
      case RIGHT_LEFT:
        r2lRadioButton.setSelected(true);
        break;
      default:
        noDirectionRadioButton.setSelected(true);
        break;
    }
    showRolesCb.setEnabled(false);
    roleANameTextField.setEnabled(false);
    roleBNameTextField.setEnabled(false);
    multiplicityACb.setSelectedItem(
      relation.getElement1Multiplicity().toString());
    multiplicityBCb.setSelectedItem(
      relation.getElement2Multiplicity().toString());
  }

  /**
   * Returns the association name.
   * @return the association name
   */
  private String getAssociationName() { return assocNameTextField.getText(); }

  /**
   * Transfers the data to the association object.
   */
  private void transferDataToAssociation() {
    UmlRelation relation = (UmlRelation) association.getModelElement();
    relation.setName(getAssociationName());
    association.setShowName(nameVisibilityCb.isSelected());
    setReadingDirection(relation);
    association.setShowMultiplicities(showMultiplicitiesCb.isSelected());
    try {
      relation.setElement1Multiplicity(Multiplicity.getInstanceFromString(
        multiplicityACb.getSelectedItem().toString()));
      relation.setElement2Multiplicity(Multiplicity.getInstanceFromString(
        multiplicityBCb.getSelectedItem().toString()));
    } catch (ParseException ignore) {
      // do not catch it here, but in the verification
      ignore.printStackTrace();
    }
  }

  /**
   * Sets the reading direction.
   * @param relation the relation
   */
  private void setReadingDirection(UmlRelation relation) {
    if (l2rRadioButton.isSelected()) {
      relation.setNameReadingDirection(ReadingDirection.LEFT_RIGHT);
    } else if (r2lRadioButton.isSelected()) {
      relation.setNameReadingDirection(ReadingDirection.RIGHT_LEFT);
    } else {
      relation.setNameReadingDirection(ReadingDirection.UNDEFINED);
    }
  }

  /**
   * Validates the input fields.
   * @return true if everything was ok, false otherwise
   */
  private boolean validateInput() {
    if (!verifyMultiplicity(multiplicityACb)) {
      multiplicityACb.getEditor().selectAll();
      multiplicityACb.requestFocusInWindow();
      return false;
    }
    if (!verifyMultiplicity(multiplicityBCb)) {
      multiplicityBCb.getEditor().selectAll();
      multiplicityBCb.requestFocusInWindow();
      return false;
    }
    return true;
  }

  /**
   * Verifies the specified combobox if the selected item contains a valid
   * Multiplicity string.
   * @param combobox the JComboBox
   * @return true if valid, false otherwise
   */
  private boolean verifyMultiplicity(JComboBox combobox) {
    try {
      Multiplicity.getInstanceFromString(combobox.getSelectedItem().toString());
      return true;
    } catch (ParseException ex) {
      return false;
    }
  }

  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
  private void initComponents() {
    directionButtonGroup = new javax.swing.ButtonGroup();
    nameVisibilityCb = new javax.swing.JCheckBox();
    associationNameLabel = new javax.swing.JLabel();
    assocNameTextField = new javax.swing.JTextField();
    cancelButton = new javax.swing.JButton();
    okButton = new javax.swing.JButton();
    jPanel1 = new javax.swing.JPanel();
    noDirectionRadioButton = new javax.swing.JRadioButton();
    l2rRadioButton = new javax.swing.JRadioButton();
    r2lRadioButton = new javax.swing.JRadioButton();
    jPanel2 = new javax.swing.JPanel();
    jLabel1 = new javax.swing.JLabel();
    roleANameTextField = new javax.swing.JTextField();
    jLabel2 = new javax.swing.JLabel();
    multiplicityACb = new javax.swing.JComboBox();
    jPanel3 = new javax.swing.JPanel();
    jLabel3 = new javax.swing.JLabel();
    roleBNameTextField = new javax.swing.JTextField();
    jLabel4 = new javax.swing.JLabel();
    multiplicityBCb = new javax.swing.JComboBox();
    showRolesCb = new javax.swing.JCheckBox();
    showMultiplicitiesCb = new javax.swing.JCheckBox();

    setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
    setTitle(ApplicationResources.getInstance().getString("dialog.assocproperties.title")
    );
    nameVisibilityCb.setText(ApplicationResources.getInstance().getString("stdcaption.visible")
    );
    nameVisibilityCb.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
    nameVisibilityCb.setMargin(new java.awt.Insets(0, 0, 0, 0));

    associationNameLabel.setText(ApplicationResources.getInstance().getString("stdcaption.name")
    );

    cancelButton.setText(ApplicationResources.getInstance().getString("stdcaption.cancel"));
    cancelButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cancelButtonActionPerformed(evt);
      }
    });

    okButton.setText(ApplicationResources.getInstance().getString("stdcaption.ok"));
    okButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        okButtonActionPerformed(evt);
      }
    });

    jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(ApplicationResources.getInstance().getString("dialog.assocdirection.title")
    ));
    directionButtonGroup.add(noDirectionRadioButton);
    noDirectionRadioButton.setSelected(true);
    noDirectionRadioButton.setText(ApplicationResources.getInstance().getString("dialog.assocdirection.none"));
    noDirectionRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
    noDirectionRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));

    directionButtonGroup.add(l2rRadioButton);
    l2rRadioButton.setText(ApplicationResources.getInstance().getString("dialog.assocdirection.leftright"));
    l2rRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
    l2rRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));

    directionButtonGroup.add(r2lRadioButton);
    r2lRadioButton.setText(ApplicationResources.getInstance().getString("dialog.assocdirection.rightleft"));
    r2lRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
    r2lRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));

    javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
    jPanel1.setLayout(jPanel1Layout);
    jPanel1Layout.setHorizontalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel1Layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(noDirectionRadioButton)
        .addGap(31, 31, 31)
        .addComponent(l2rRadioButton)
        .addGap(31, 31, 31)
        .addComponent(r2lRadioButton)
        .addContainerGap(163, Short.MAX_VALUE))
    );
    jPanel1Layout.setVerticalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel1Layout.createSequentialGroup()
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(noDirectionRadioButton)
          .addComponent(l2rRadioButton)
          .addComponent(r2lRadioButton))
        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(ApplicationResources.getInstance().getString("dialog.assoc.elementa.title")));
    jLabel1.setText(ApplicationResources.getInstance().getString("dialog.assoc.role"));

    jLabel2.setText(ApplicationResources.getInstance().getString("dialog.assoc.multiplicity"));

    multiplicityACb.setEditable(true);
    multiplicityACb.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0..1", "1", "*", "1..*" }));

    javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
    jPanel2.setLayout(jPanel2Layout);
    jPanel2Layout.setHorizontalGroup(
      jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel2Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(jLabel2)
          .addComponent(jLabel1))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(jPanel2Layout.createSequentialGroup()
            .addGap(10, 10, 10)
            .addComponent(multiplicityACb, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE))
          .addComponent(roleANameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 295, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addGap(109, 109, 109))
    );
    jPanel2Layout.setVerticalGroup(
      jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel2Layout.createSequentialGroup()
        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel1)
          .addComponent(roleANameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel2)
          .addComponent(multiplicityACb, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(ApplicationResources.getInstance().getString("dialog.assoc.elementb.title")));
    jLabel3.setText(ApplicationResources.getInstance().getString("dialog.assoc.role"));

    jLabel4.setText(ApplicationResources.getInstance().getString("dialog.assoc.multiplicity"));

    multiplicityBCb.setEditable(true);
    multiplicityBCb.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0..1", "1", "*", "1..*" }));

    javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
    jPanel3.setLayout(jPanel3Layout);
    jPanel3Layout.setHorizontalGroup(
      jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel3Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(jLabel3)
          .addComponent(jLabel4))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(roleBNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 295, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addGroup(jPanel3Layout.createSequentialGroup()
            .addGap(10, 10, 10)
            .addComponent(multiplicityBCb, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)))
        .addGap(109, 109, 109))
    );
    jPanel3Layout.setVerticalGroup(
      jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel3Layout.createSequentialGroup()
        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel3)
          .addComponent(roleBNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel4)
          .addComponent(multiplicityBCb, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    showRolesCb.setText(ApplicationResources.getInstance().getString("dialog.assoc.showroles"));
    showRolesCb.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
    showRolesCb.setMargin(new java.awt.Insets(0, 0, 0, 0));

    showMultiplicitiesCb.setText(ApplicationResources.getInstance().getString("dialog.assoc.showmultiplicities"));
    showMultiplicitiesCb.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
    showMultiplicitiesCb.setMargin(new java.awt.Insets(0, 0, 0, 0));

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(layout.createSequentialGroup()
            .addContainerGap()
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addGroup(layout.createSequentialGroup()
                .addComponent(associationNameLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(assocNameTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 331, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(nameVisibilityCb))
              .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(okButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cancelButton))))
          .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addGroup(layout.createSequentialGroup()
            .addContainerGap()
            .addComponent(showRolesCb)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(showMultiplicitiesCb))
          .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 500, Short.MAX_VALUE)
          .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, 500, Short.MAX_VALUE))
        .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(associationNameLabel)
          .addComponent(nameVisibilityCb)
          .addComponent(assocNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(showRolesCb)
          .addComponent(showMultiplicitiesCb))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(cancelButton)
          .addComponent(okButton))
        .addContainerGap())
    );
    pack();
  }// </editor-fold>//GEN-END:initComponents

  private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
    dispose();
  }//GEN-LAST:event_cancelButtonActionPerformed

  private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
    if (validateInput()) {
      transferDataToAssociation();
      dispose();
    }
  }//GEN-LAST:event_okButtonActionPerformed

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JTextField assocNameTextField;
  private javax.swing.JLabel associationNameLabel;
  private javax.swing.JButton cancelButton;
  private javax.swing.ButtonGroup directionButtonGroup;
  private javax.swing.JLabel jLabel1;
  private javax.swing.JLabel jLabel2;
  private javax.swing.JLabel jLabel3;
  private javax.swing.JLabel jLabel4;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JPanel jPanel2;
  private javax.swing.JPanel jPanel3;
  private javax.swing.JRadioButton l2rRadioButton;
  private javax.swing.JComboBox multiplicityACb;
  private javax.swing.JComboBox multiplicityBCb;
  private javax.swing.JCheckBox nameVisibilityCb;
  private javax.swing.JRadioButton noDirectionRadioButton;
  private javax.swing.JButton okButton;
  private javax.swing.JRadioButton r2lRadioButton;
  private javax.swing.JTextField roleANameTextField;
  private javax.swing.JTextField roleBNameTextField;
  private javax.swing.JCheckBox showMultiplicitiesCb;
  private javax.swing.JCheckBox showRolesCb;
  // End of variables declaration//GEN-END:variables

}
