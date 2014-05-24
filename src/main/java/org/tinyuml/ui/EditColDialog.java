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
import org.firebirdvisualizationtool.database.firebird.DatabaseCharacterSets;
import org.tinyuml.model.Column;
import org.tinyuml.model.DatabaseModel;
import org.tinyuml.model.Domain;
import org.tinyuml.util.ApplicationResources;
import org.tinyuml.util.DocumentFilterFactory;

/**
 *
 * @author Tomáš Ligenza
 */
public class EditColDialog extends javax.swing.JDialog {

	private boolean isOk			= false;
	
	// flags
	private boolean editDomain		= false;
	private boolean editDataType	= false;
	private boolean editSize		= false;
	private boolean editScale		= false;
	private boolean editCharset		= false;
	private boolean editCollate		= false;
	private boolean editNN			= false;
	
	private Column column;
	private DatabaseModel databaseModel;
	
	/**
	 * Creates new form EditDomainDialog
	 * @param parent
	 * @param aCol
	 * @param aDatabaseModel
	 */
	public EditColDialog(JDialog parent, Column aCol, DatabaseModel aDatabaseModel) {
		
		super(parent, "Create new column", true, null);
		
		if(aCol == null) {
			column = Column.getPrototype().create("", ColumnTypes.CHAR, false);
		} else {
			column = aCol;
		}
		
		databaseModel = aDatabaseModel;
		
		initComponents();
		modelInit();
		listenersInit();
	}
	
	/**
	 * Inicializace modelu pro comboboxy
	 */
	private void modelInit() {
		
		DefaultComboBoxModel model = databaseModel.getDomainsComboBoxModel();
		domainComboBox.setModel(model);
		
		// first item is NULL DOMAIN (no domain)
		if(model.getSize() > 1) {
			editDomain = true;
		}
		
		datatypeComboBox.setModel(DataType.getTableColTypes());
		charsetComboBox.setModel(DatabaseCharacterSets.getColumnCharacters());
		
		valueInit();
	}
	
	/**
	 * Pridani listeneru k formularovym polim
	 */
	private void listenersInit() {
		
		// column name
		nameTextField.getDocument().addDocumentListener(new DocumentListener() {

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
				
				column.setName(nameTextField.getText());
			}
		});
		
		((PlainDocument) nameTextField.getDocument()).setDocumentFilter(
			DocumentFilterFactory.buildFilter(DocumentFilterFactory.DocumentFilterType.DATABASE_COLUMN_NAME_LENGTH, false));
		
		// if columns data type is domain
		// other data type settings must be uneditable
		domainComboBox.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				
				if(e.getStateChange() == ItemEvent.SELECTED) {
					
					Domain domain = (Domain) e.getItem();
					
					if(!domain.getName().equals(Domain.NULL_DOMAIN))
						column.setDomain(domain);
					
					checkEditable();
				}
			}
		});
		
		// data type influences other fields
		datatypeComboBox.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				
				if(e.getStateChange() == ItemEvent.SELECTED) {
					
					ColumnTypes colType = (ColumnTypes) e.getItem();
					column.setColType(colType);
					
					checkEditable();
				}
			}
		});
		
		// not null
		notNullCheckBox.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				
				if(e.getStateChange() == ItemEvent.DESELECTED)
					column.setNn(false);
				else
					column.setNn(true);
			}
		});
		
		// size (could be only int value) added filter
		((PlainDocument) sizeTextField.getDocument()).setDocumentFilter(
			DocumentFilterFactory.buildFilter(DocumentFilterFactory.DocumentFilterType.INT, false));
		
		sizeTextField.getDocument().addDocumentListener(new DocumentListener() {

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
				
				String text = sizeTextField.getText();
				
				if(!text.isEmpty())
					column.setSize(Integer.parseInt(text));
			}
		});
		
		// scale (could be only int value) added filter
		((PlainDocument) scaleTextField.getDocument()).setDocumentFilter(
			DocumentFilterFactory.buildFilter(DocumentFilterFactory.DocumentFilterType.INT, false));
		
		scaleTextField.getDocument().addDocumentListener(new DocumentListener() {

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
				
				String text = scaleTextField.getText();
				
				if(!text.isEmpty())
					column.setScale(Integer.parseInt(text));
			}
		});
		
		charsetComboBox.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				
				if(e.getStateChange() == ItemEvent.SELECTED) {
					
					CharacterSet set = (CharacterSet) e.getItem();
					column.setCharacterSet(set.getName());
					
					// collate combo box update and save init value
					collateComboBox.setModel(DatabaseCharacterSets.getColumnCollateBySet(set.getName()));
					column.setCollate((String) collateComboBox.getModel().getSelectedItem());
				} else {
					
					collateComboBox.setModel(new DefaultComboBoxModel());
					column.setCharacterSet("");
				}
			}
		});
		
		// collate
		collateComboBox.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				
				if(e.getStateChange() == ItemEvent.SELECTED) {
					
					column.setCollate((String) e.getItem());
				} else {
					column.setCollate("");
				}
			}
		});
		
		// default value
		defaultTextArea.getDocument().addDocumentListener(new DocumentListener() {

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
				
				column.setDefaultValue(defaultTextArea.getText());
			}
		});
		
		// check
		checkTextArea.getDocument().addDocumentListener(new DocumentListener() {

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
				
				column.setCheck(checkTextArea.getText());
			}
		});
	}
	
	/**
	 * Nacteni hodnot domeny
	 * Pri editaci se zatim vsechna editace zakaze
	 */
	private void valueInit() {
		
		nameTextField.setText(column.getName());
		
		datatypeComboBox.setSelectedItem(column.getColType());
		
		if(column.getDomain() != null
			&& !column.getDomain().getName().equals(Domain.NULL_DOMAIN)) {
			domainComboBox.setSelectedItem(column.getDomain());
		} else {
			editDataType = true;
			editNN = true;
		}
		
		if(column.getNn())
			notNullCheckBox.setSelected(true);

		if(column.getSize() > 0) {
			sizeTextField.setText(String.valueOf(column.getSize()));
			editSize = true;
		}

		if(column.getScale() > 0) {
			scaleTextField.setText(String.valueOf(column.getScale()));
			editScale = true;
		}

		if(column.getCharacterSet() != null
			&& !column.getCharacterSet().isEmpty()) {
			
			charsetComboBox.setSelectedItem(DatabaseCharacterSets.getCharacterSetByName(column.getCharacterSet()));
			editCharset = true;
			
			// collate combo box model update
			collateComboBox.setModel(DatabaseCharacterSets.getColumnCollateBySet(column.getCharacterSet()));
			editCollate = true;
		} else {
			collateComboBox.setModel(new DefaultComboBoxModel());
		}

		if(column.getCollate() != null 
			&& !column.getCollate().isEmpty()) {
			
			collateComboBox.setSelectedItem(column.getCollate());
			editCollate = true;
		}

		if(column.getDefaultValue() != null
			&& !column.getDefaultValue().isEmpty())
			defaultTextArea.setText(column.getDefaultValue());

		if(column.getCheck() != null
			&& !column.getCheck().isEmpty())
			checkTextArea.setText(column.getCheck());

		checkEditable();
	}
	
	/**
	 * Checking forms state depending on selected/nonselected domain
	 * or data type
	 */
	private void checkEditable() {
		
		Domain domain = column.getDomain();
		ColumnTypes type = column.getColType();
		
		if(domain != null
			&& !domain.getName().equals(Domain.NULL_DOMAIN)) {
			
			editDataType	= false;
			editSize		= false;
			editScale		= false;
			editCharset		= false;
			editCollate		= false;
			editNN			= false;
			
		} else {
			
			switch(type) {
			
				case CHAR: case VARCHAR:
					editSize	= true;
					editScale	= false;
					scaleTextField.setText("");
					editCharset = true;
					editCollate = true;
				break;

				case INTEGER: case SMALLINT: case BIGINT: 
				case FLOAT: case DOUBLE_PRECISION: 
				case DATE: case TIME: case TIMESTAMP:
				case ARRAY: case BLOB:
					editSize	= false;
					sizeTextField.setText("");
					editScale	= false;
					scaleTextField.setText("");
					editCharset = false;
					charsetComboBox.setSelectedIndex(-1);
					editCollate = false;
					collateComboBox.setSelectedIndex(-1);
				break;

				case NUMERIC: case DECIMAL:
					editSize	= true;
					editScale	= true;
					editCharset = false;
					charsetComboBox.setSelectedIndex(-1);
					editCollate = false;
					collateComboBox.setSelectedIndex(-1);
				break;
			}
		}
		
		redrawDialog();
	}
	
	/**
	 * Prekresleni form poli podle nastavenych priznaku
	 */
	private void redrawDialog() {
		
		domainComboBox.setEnabled(editDomain);
		datatypeComboBox.setEnabled(editDataType);
		sizeTextField.setEnabled(editSize);
		scaleTextField.setEnabled(editScale);
		charsetComboBox.setEnabled(editCharset);
		collateComboBox.setEnabled(editCollate);
		notNullCheckBox.setEnabled(editNN);
	}
	
	public boolean isOk() { return isOk; }
	
	public Column getColumn() {
		
		return column;
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
        nameLabel = new javax.swing.JLabel();
        nameTextField = new javax.swing.JTextField();
        datatypeLabel = new javax.swing.JLabel();
        datatypeComboBox = new javax.swing.JComboBox();
        sizeLabel = new javax.swing.JLabel();
        sizeTextField = new javax.swing.JTextField();
        scaleLabel = new javax.swing.JLabel();
        scaleTextField = new javax.swing.JTextField();
        charsetLabel = new javax.swing.JLabel();
        charsetComboBox = new javax.swing.JComboBox();
        collateLabel = new javax.swing.JLabel();
        collateComboBox = new javax.swing.JComboBox();
        notNullCheckBox = new javax.swing.JCheckBox();
        defaultLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        defaultTextArea = new javax.swing.JTextArea();
        checkLabel = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        checkTextArea = new javax.swing.JTextArea();
        domainComboBox = new javax.swing.JComboBox();
        domainLabel = new javax.swing.JLabel();
        cancellButton = new javax.swing.JButton();
        okButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Create new column");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        nameLabel.setText("Column name:");

        datatypeLabel.setText("Datatype:");

        datatypeComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        sizeLabel.setText("Size:");

        scaleLabel.setText("Scale:");

        charsetLabel.setText("Charset:");

        charsetComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        collateLabel.setText("Collate:");

        collateComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        notNullCheckBox.setText("Not null");

        defaultLabel.setText("Default:");

        defaultTextArea.setColumns(20);
        defaultTextArea.setRows(5);
        jScrollPane1.setViewportView(defaultTextArea);

        checkLabel.setText("Check:");

        checkTextArea.setColumns(20);
        checkTextArea.setRows(5);
        jScrollPane2.setViewportView(checkTextArea);

        domainComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        domainLabel.setText("Domain:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(checkLabel)
                    .addComponent(defaultLabel)
                    .addComponent(collateLabel)
                    .addComponent(charsetLabel)
                    .addComponent(scaleLabel)
                    .addComponent(sizeLabel)
                    .addComponent(datatypeLabel)
                    .addComponent(nameLabel)
                    .addComponent(domainLabel))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(nameTextField)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 654, Short.MAX_VALUE)
                    .addComponent(jScrollPane2)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(domainComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(sizeTextField)
                                    .addComponent(datatypeComboBox, 0, 198, Short.MAX_VALUE)
                                    .addComponent(scaleTextField)
                                    .addComponent(charsetComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(collateComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(18, 18, 18)
                                .addComponent(notNullCheckBox)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nameLabel)
                    .addComponent(nameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 9, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(domainComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(domainLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(datatypeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(datatypeLabel)
                    .addComponent(notNullCheckBox))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(sizeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sizeLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(scaleTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(scaleLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(charsetComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(charsetLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(collateComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(collateLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(defaultLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(checkLabel))
                .addContainerGap())
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

		column.repairInput();
		
		if(column.checkValid()) {
			
			isOk = true;
			dispose();
		} else {
			
			if(JOptionPane.showConfirmDialog(this
				, ApplicationResources.getInstance().getString("database.col.question")
				, UIManager.getString("OptionPane.titleText")
				, JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION)
				
				dispose();
		}
    }//GEN-LAST:event_okButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancellButton;
    private javax.swing.JComboBox charsetComboBox;
    private javax.swing.JLabel charsetLabel;
    private javax.swing.JLabel checkLabel;
    private javax.swing.JTextArea checkTextArea;
    private javax.swing.JComboBox collateComboBox;
    private javax.swing.JLabel collateLabel;
    private javax.swing.JComboBox datatypeComboBox;
    private javax.swing.JLabel datatypeLabel;
    private javax.swing.JLabel defaultLabel;
    private javax.swing.JTextArea defaultTextArea;
    private javax.swing.JComboBox domainComboBox;
    private javax.swing.JLabel domainLabel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JTextField nameTextField;
    private javax.swing.JCheckBox notNullCheckBox;
    private javax.swing.JButton okButton;
    private javax.swing.JLabel scaleLabel;
    private javax.swing.JTextField scaleTextField;
    private javax.swing.JLabel sizeLabel;
    private javax.swing.JTextField sizeTextField;
    // End of variables declaration//GEN-END:variables
}
