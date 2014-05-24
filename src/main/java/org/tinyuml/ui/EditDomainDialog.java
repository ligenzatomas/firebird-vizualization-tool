/**
 * Copyright 2014 Tomáš Ligenza
 *
 * This file is part of Firebird Visualization Tool.
 *
 * Firebird Visualization Tool is free software; you can redistribute it and/or modify
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
 * along with Firebird Visualization Tool; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.tinyuml.ui;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.PlainDocument;
import org.firebirdvisualizationtool.database.firebird.CharacterSet;
import org.firebirdvisualizationtool.database.firebird.DatabaseCharacterSets;
import org.firebirdvisualizationtool.database.firebird.ColumnTypes;
import static org.firebirdvisualizationtool.database.firebird.ColumnTypes.ARRAY;
import static org.firebirdvisualizationtool.database.firebird.ColumnTypes.BIGINT;
import static org.firebirdvisualizationtool.database.firebird.ColumnTypes.BLOB;
import static org.firebirdvisualizationtool.database.firebird.ColumnTypes.CHAR;
import static org.firebirdvisualizationtool.database.firebird.ColumnTypes.DATE;
import static org.firebirdvisualizationtool.database.firebird.ColumnTypes.DECIMAL;
import static org.firebirdvisualizationtool.database.firebird.ColumnTypes.DOUBLE_PRECISION;
import static org.firebirdvisualizationtool.database.firebird.ColumnTypes.FLOAT;
import static org.firebirdvisualizationtool.database.firebird.ColumnTypes.INTEGER;
import static org.firebirdvisualizationtool.database.firebird.ColumnTypes.NUMERIC;
import static org.firebirdvisualizationtool.database.firebird.ColumnTypes.SMALLINT;
import static org.firebirdvisualizationtool.database.firebird.ColumnTypes.TIME;
import static org.firebirdvisualizationtool.database.firebird.ColumnTypes.TIMESTAMP;
import static org.firebirdvisualizationtool.database.firebird.ColumnTypes.VARCHAR;
import org.firebirdvisualizationtool.database.firebird.DataType;
import org.tinyuml.model.Domain;
import org.tinyuml.util.ApplicationResources;
import org.tinyuml.util.DocumentFilterFactory;

/**
 *
 * @author Tomáš Ligenza
 */
public class EditDomainDialog extends javax.swing.JDialog {

	private boolean isOk		= false;
	
	// pokud se nacita domena z modelu (editace domeny)
	// zabranuje klasickemu chovani (checkEditu pri loadu)
	private boolean notEditable = false;
	
	// flagy, zda ma byt pole editovatelne
	private boolean editSize	= false;
	private boolean editScale	= false;
	private boolean editCharset	= false;
	private boolean editCollate	= false;
	
	private Domain domain;
	
	/**
	 * Creates new form EditDomainDialog
	 */
	public EditDomainDialog(JDialog parent, Domain domain) {
		
		super(parent, "Create new domain", true, null);
		
		// pri vytvoreni nove
		if(domain == null)
			this.domain = Domain.getPrototype().create("Domain", ColumnTypes.INTEGER, false);
		else {
			
			// TODO editace domen
			// je treba promyslet update domeny
			// navic update domeny je ve FB spojen se zmenou integrity sloupce a souvisejici PSQL je neplatne
			this.domain = domain;
			notEditable = true;
		}
		
		initComponents();
		modelInit();
		listenersInit();
		valueInit();
	}
	
	/**
	 * Inicializace modelu pro comboboxy
	 */
	private void modelInit() {
		
		datatypeDomainComboBox.setModel(DataType.getTableColTypes());
		charsetDomainComboBox.setModel(DatabaseCharacterSets.getColumnCharacters());
		
		// neni zadany charset - neni mozne zjistit collate
		collateDomainComboBox.setModel(new DefaultComboBoxModel());
	}
	
	/**
	 * Nacteni hodnot domeny
	 * Pri editaci se zatim vsechna editace zakaze
	 */
	private void valueInit() {
		
		nameDomainTextField.setText(domain.getName());
		datatypeDomainComboBox.setSelectedItem(domain.getColType());
		
		if(notEditable) {
			
			if(domain.getNn())
				notNullDomainCheckBox.setSelected(true);
			
			if(domain.getSize() > 0) {
				sizeDomainTextField.setText(String.valueOf(domain.getSize()));
				editSize = true;
			}
			
			if(domain.getScale() > 0) {
				scaleDomainTextField.setText(String.valueOf(domain.getScale()));
				editScale = true;
			}
			
			if(!domain.getCharacterSet().isEmpty()) {
				charsetDomainComboBox.setSelectedItem(DatabaseCharacterSets.getCharacterSetByName(domain.getCharacterSet()));
				editCharset = true;
			}
			
			if(domain.getCollate() != null && !domain.getCollate().isEmpty()) {
				collateDomainComboBox.setSelectedItem(domain.getCollate());
				editCollate = true;
			}
			
			if(!domain.getDefaultValue().isEmpty())
				defaultDomainTextArea.setText(domain.getDefaultValue());
			
			if(!domain.getCheck().isEmpty())
				checkDomainTextArea.setText(domain.getCheck());
			
			nameDomainTextField.setEnabled(false);
			datatypeDomainComboBox.setEnabled(false);
			notNullDomainCheckBox.setEnabled(false);
			sizeDomainTextField.setEnabled(false);
			scaleDomainTextField.setEnabled(false);
			charsetDomainComboBox.setEnabled(false);
			collateDomainComboBox.setEnabled(false);
			defaultDomainTextArea.setEnabled(false);
			checkDomainTextArea.setEnabled(false);
			
			notEditable = false;
		} else {
			checkEditable();
		}
	}
	
	/**
	 * Pridani listeneru k formularovym polim
	 */
	private void listenersInit() {
		
		// nazev domeny
		nameDomainTextField.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void insertUpdate(DocumentEvent e) {
				updateModel();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				updateModel();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				updateModel();
			}
			
			private void updateModel() {
				
				domain.setName(nameDomainTextField.getText());
			}
		});
		
		// datovy typ domeny - ovlivnuje pres checkEditable editaci dalsich formularovych poli
		datatypeDomainComboBox.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				
				if(e.getStateChange() == ItemEvent.SELECTED) {
					
					ColumnTypes colType = (ColumnTypes) e.getItem();
					domain.setColType(colType);
					
					checkEditable();
				}
			}
		});
		
		// not null
		notNullDomainCheckBox.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				
				if(e.getStateChange() == ItemEvent.DESELECTED)
					domain.setNn(false);
				else
					domain.setNn(true);
			}
		});
		
		// size (ma filter pouze na cislice)
		((PlainDocument) sizeDomainTextField.getDocument()).setDocumentFilter(
			DocumentFilterFactory.buildFilter(DocumentFilterFactory.DocumentFilterType.INT, false));
		
		sizeDomainTextField.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void insertUpdate(DocumentEvent e) {
				updateModel();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				updateModel();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				updateModel();
			}
			
			private void updateModel() {
				
				String text = sizeDomainTextField.getText();
				
				if(!text.isEmpty())
					domain.setSize(Integer.parseInt(text));
			}
		});
		
		// scale - ma filter pouze na cislice
		((PlainDocument) scaleDomainTextField.getDocument()).setDocumentFilter(
			DocumentFilterFactory.buildFilter(DocumentFilterFactory.DocumentFilterType.INT, false));
		
		scaleDomainTextField.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void insertUpdate(DocumentEvent e) {
				updateModel();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				updateModel();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				updateModel();
			}
			
			private void updateModel() {
				
				String text = scaleDomainTextField.getText();
				
				if(!text.isEmpty())
					domain.setScale(Integer.parseInt(text));
			}
		});
		
		// charset ovlivnuje nacteni modelu collate a ulozi prvni nactenou hodnotu
		// to totiz item listener u collate combo boxu nezvlada
		charsetDomainComboBox.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				
				if(e.getStateChange() == ItemEvent.SELECTED) {
					
					CharacterSet set = (CharacterSet) e.getItem();
					
					collateDomainComboBox.setModel(DatabaseCharacterSets.getColumnCollateBySet(set.getName()));
					
					domain.setCharacterSet(set.getName());
					
					if(!notEditable)
						domain.setCollate((String) collateDomainComboBox.getModel().getSelectedItem());
				} else {
					
					collateDomainComboBox.setModel(new DefaultComboBoxModel());
					domain.setCharacterSet("");
				}
			}
		});
		
		// collate
		collateDomainComboBox.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				
				if(e.getStateChange() == ItemEvent.SELECTED) {
					
					domain.setCollate((String) e.getItem());
				} else {
					domain.setCollate("");
				}
			}
		});
		
		// default value
		defaultDomainTextArea.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void insertUpdate(DocumentEvent e) {
				updateModel();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				updateModel();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				updateModel();
			}
			
			private void updateModel() {
				
				domain.setDefaultValue(defaultDomainTextArea.getText());
			}
		});
		
		// check
		checkDomainTextArea.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void insertUpdate(DocumentEvent e) {
				updateModel();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				updateModel();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				updateModel();
			}
			
			private void updateModel() {
				
				domain.setCheck(checkDomainTextArea.getText());
			}
		});
	}
	
	/**
	 * Nastaveni editace form poli podle datoveho typu domeny
	 */
	private void checkEditable() {
		
		ColumnTypes type = domain.getColType();
		
		switch(type) {
			
			case CHAR: case VARCHAR:
				editSize	= true;
				editScale	= false;
				scaleDomainTextField.setText("");
				editCharset = true;
				editCollate = true;
			break;
				
			case INTEGER: case SMALLINT: case BIGINT: 
			case FLOAT: case DOUBLE_PRECISION: 
			case DATE: case TIME: case TIMESTAMP:
			case ARRAY: case BLOB:
				editSize	= false;
				sizeDomainTextField.setText("");
				editScale	= false;
				scaleDomainTextField.setText("");
				editCharset = false;
				charsetDomainComboBox.setSelectedIndex(-1);
				editCollate = false;
				collateDomainComboBox.setSelectedIndex(-1);
			break;
				
			case NUMERIC: case DECIMAL:
				editSize	= true;
				editScale	= true;
				editCharset = false;
				charsetDomainComboBox.setSelectedIndex(-1);
				editCollate = false;
				collateDomainComboBox.setSelectedIndex(-1);
			break;
		}
		
		redrawDialog();
	}
	
	/**
	 * Prekresleni form poli podle nastavenych priznaku
	 */
	private void redrawDialog() {
		
		sizeDomainTextField.setEnabled(editSize);
		scaleDomainTextField.setEnabled(editScale);
		charsetDomainComboBox.setEnabled(editCharset);
		
		collateDomainComboBox.setEnabled(editCollate);
	}
	
	public boolean isOk() { return isOk; }
	
	public Domain getDomain() {
		
		return domain;
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        nameDomainLabel = new javax.swing.JLabel();
        nameDomainTextField = new javax.swing.JTextField();
        datatypeDomainLabel = new javax.swing.JLabel();
        datatypeDomainComboBox = new javax.swing.JComboBox();
        sizeDomainLabel = new javax.swing.JLabel();
        sizeDomainTextField = new javax.swing.JTextField();
        scaleDomainLabel = new javax.swing.JLabel();
        scaleDomainTextField = new javax.swing.JTextField();
        charsetDomainLabel = new javax.swing.JLabel();
        charsetDomainComboBox = new javax.swing.JComboBox();
        collateDomainLabel = new javax.swing.JLabel();
        collateDomainComboBox = new javax.swing.JComboBox();
        notNullDomainCheckBox = new javax.swing.JCheckBox();
        defaultDomainLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        defaultDomainTextArea = new javax.swing.JTextArea();
        checkDomainLabel = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        checkDomainTextArea = new javax.swing.JTextArea();
        cancellButton = new javax.swing.JButton();
        okButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Create new domain");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        nameDomainLabel.setText("Domain name:");

        datatypeDomainLabel.setText("Datatype:");

        datatypeDomainComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        sizeDomainLabel.setText("Size:");

        scaleDomainLabel.setText("Scale:");

        charsetDomainLabel.setText("Charset:");

        charsetDomainComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        collateDomainLabel.setText("Collate:");

        collateDomainComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        notNullDomainCheckBox.setText("Not null");

        defaultDomainLabel.setText("Default:");

        defaultDomainTextArea.setColumns(20);
        defaultDomainTextArea.setRows(5);
        jScrollPane1.setViewportView(defaultDomainTextArea);

        checkDomainLabel.setText("Check:");

        checkDomainTextArea.setColumns(20);
        checkDomainTextArea.setRows(5);
        jScrollPane2.setViewportView(checkDomainTextArea);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(checkDomainLabel)
                    .addComponent(defaultDomainLabel)
                    .addComponent(collateDomainLabel)
                    .addComponent(charsetDomainLabel)
                    .addComponent(scaleDomainLabel)
                    .addComponent(sizeDomainLabel)
                    .addComponent(datatypeDomainLabel)
                    .addComponent(nameDomainLabel))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(nameDomainTextField)
                    .addComponent(jScrollPane1)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(sizeDomainTextField)
                            .addComponent(datatypeDomainComboBox, 0, 198, Short.MAX_VALUE)
                            .addComponent(scaleDomainTextField)
                            .addComponent(charsetDomainComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(collateDomainComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addComponent(notNullDomainCheckBox)
                        .addGap(0, 357, Short.MAX_VALUE))
                    .addComponent(jScrollPane2))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nameDomainLabel)
                    .addComponent(nameDomainTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(datatypeDomainLabel)
                    .addComponent(datatypeDomainComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(notNullDomainCheckBox))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(sizeDomainLabel)
                    .addComponent(sizeDomainTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(scaleDomainLabel)
                    .addComponent(scaleDomainTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(charsetDomainLabel)
                    .addComponent(charsetDomainComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(collateDomainLabel)
                    .addComponent(collateDomainComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(defaultDomainLabel)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addComponent(checkDomainLabel)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 173, Short.MAX_VALUE)
                        .addContainerGap())))
        );

        cancellButton.setText(ApplicationResources.getInstance().getString("stdcaption.cancel"));
        cancellButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancellButtonActionPerformed(evt);
            }
        });

        okButton.setText(ApplicationResources.getInstance().getString("stdcaption.ok"));
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(okButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cancellButton)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancellButton)
                    .addComponent(okButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cancellButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancellButtonActionPerformed

		dispose();
    }//GEN-LAST:event_cancellButtonActionPerformed

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed

		domain.repairInput();
		
		if(domain.checkValid()) {
			
			isOk = true;
			dispose();
		} else {
			
			if(JOptionPane.showConfirmDialog(this
				, ApplicationResources.getInstance().getString("database.domain.question")
				, UIManager.getString("OptionPane.titleText")
				, JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION)
				
				dispose();
		}
    }//GEN-LAST:event_okButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancellButton;
    private javax.swing.JComboBox charsetDomainComboBox;
    private javax.swing.JLabel charsetDomainLabel;
    private javax.swing.JLabel checkDomainLabel;
    private javax.swing.JTextArea checkDomainTextArea;
    private javax.swing.JComboBox collateDomainComboBox;
    private javax.swing.JLabel collateDomainLabel;
    private javax.swing.JComboBox datatypeDomainComboBox;
    private javax.swing.JLabel datatypeDomainLabel;
    private javax.swing.JLabel defaultDomainLabel;
    private javax.swing.JTextArea defaultDomainTextArea;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel nameDomainLabel;
    private javax.swing.JTextField nameDomainTextField;
    private javax.swing.JCheckBox notNullDomainCheckBox;
    private javax.swing.JButton okButton;
    private javax.swing.JLabel scaleDomainLabel;
    private javax.swing.JTextField scaleDomainTextField;
    private javax.swing.JLabel sizeDomainLabel;
    private javax.swing.JTextField sizeDomainTextField;
    // End of variables declaration//GEN-END:variables
}
