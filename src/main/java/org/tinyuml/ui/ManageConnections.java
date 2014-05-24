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

import org.firebirdvisualizationtool.database.firebird.ConnectionProperties;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.PlainDocument;
import org.firebirdvisualizationtool.database.firebird.CharacterSet;
import org.firebirdvisualizationtool.database.firebird.DatabaseCharacterSets;
import org.firebirdvisualizationtool.database.firebird.DatabaseConnection;
import org.tinyuml.util.ApplicationResources;
import org.tinyuml.util.DocumentFilterFactory;

/**
 *
 * @author Tomáš Ligenza
 */
public class ManageConnections extends javax.swing.JDialog {

	private boolean isOk;
	
	private DefaultListModel<ConnectionProperties> connectionsListModel = new DefaultListModel<ConnectionProperties>();
	
	private int lastSelection = -1;
	private boolean loading = false;
	
	public ManageConnections(java.awt.Window parent, List<ConnectionProperties> connections) {
		
		super(parent, Dialog.ModalityType.APPLICATION_MODAL);
		
		if(connections != null) {
			for(ConnectionProperties conn : connections) {

				if(conn.isValid())
					connectionsListModel.addElement(conn);
			}
		}
		
		initComponents();
		
		myInit();
	}
	
	private void myInit() {
		
		connectionsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		connectionsList.setModel(connectionsListModel);
		
		connectionsList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				
				int index = connectionsList.getSelectedIndex();
				
				if(e.getValueIsAdjusting() || index < 0)
					return;
				
				saveConnectionProperty(lastSelection);
				
				lastSelection = index;
				
				loadConnectionProperty(lastSelection);
			}
		});
		
		if(!connectionsListModel.isEmpty()) {
			connectionsList.setSelectedIndex(0);
			lastSelection = 0;
		}
		
		connectionsList.setCellRenderer(new DefaultListCellRenderer() {

			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
				
				if(value instanceof ConnectionProperties)
					return super.getListCellRendererComponent(list, ((ConnectionProperties) value).getName(), index, isSelected, cellHasFocus);
				else
					return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			}
		});
		
		nameTextField.getDocument().addDocumentListener(getDocumentListener());
		hostTextField.getDocument().addDocumentListener(getDocumentListener());
		fileTextField.getDocument().addDocumentListener(getDocumentListener());
		
		((PlainDocument) portTextField.getDocument()).setDocumentFilter(
			DocumentFilterFactory.buildFilter(DocumentFilterFactory.DocumentFilterType.INT, false));
		portTextField.getDocument().addDocumentListener(getDocumentListener());
		
		userTextField.getDocument().addDocumentListener(getDocumentListener());
		passwordField.getDocument().addDocumentListener(getDocumentListener());
		
		encodingComboBox.setModel(DatabaseCharacterSets.getColumnCharacters());
		encodingComboBox.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				
				int row = connectionsList.getSelectedIndex();
				
				saveConnectionProperty(row);
			}
		});
	}
	
	private DocumentListener getDocumentListener() {
		
		return new DocumentListener() {

			@Override
			public void insertUpdate(DocumentEvent e) {
				
				int row = connectionsList.getSelectedIndex();
				
				saveConnectionProperty(row);
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				
				int row = connectionsList.getSelectedIndex();
				
				saveConnectionProperty(row);
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				
				int row = connectionsList.getSelectedIndex();
				
				saveConnectionProperty(row);
			}
		};
	}
	
	private void saveConnectionProperty(int row) {
		
		if(row >= 0
			&& loading == false) {
			
			ConnectionProperties conn = connectionsListModel.getElementAt(row);
			
			conn.setName(nameTextField.getText());
			conn.setHost(hostTextField.getText());
			conn.setFile(fileTextField.getText());
			conn.setPort(portTextField.getText());
			
			conn.setEncoding(((CharacterSet) encodingComboBox.getSelectedItem()).getName());
			
			conn.setUser(userTextField.getText());
			conn.setPassword(passwordField.getPassword());
			
			connectionsListModel.setElementAt(conn, row);
		}
	}
	
	private void loadConnectionProperty(int row) {
		
		loading = true;
		
		ConnectionProperties conn = connectionsListModel.elementAt(row);
		
		nameTextField.setText(conn.getName());
		hostTextField.setText(conn.getHost());
		fileTextField.setText(conn.getFile());
		
		String port = conn.getPort();
		
		if(port != null)
			portTextField.setText(port);
		else
			portTextField.setText("3050");
		
		encodingComboBox.getModel().setSelectedItem(DatabaseCharacterSets.getCharacterSetByName(conn.getEncoding()));
		
		userTextField.setText(conn.getUser());
		
		char[] pass = conn.getPassword();
		
		if(pass != null)
			passwordField.setText(new String(pass));
		
		passwordField.setEchoChar('*');
		
		loading = false;
	}
	
	public LinkedList<ConnectionProperties> getConnections() {
		
		LinkedList<ConnectionProperties> list = new LinkedList<ConnectionProperties>();
		
		for(int i = 0; i < connectionsListModel.size(); i++) {
			
			ConnectionProperties conn = connectionsListModel.get(i);
			
			if(conn.isValid())
				list.add(conn);
		}
		
		return list;
	}
	
	public boolean isOk() {
		
		return isOk;
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        connectionsScrollPane = new javax.swing.JScrollPane();
        connectionsList = new javax.swing.JList();
        addButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();
        parametersPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        nameTextField = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        hostTextField = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        fileTextField = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        portTextField = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel5 = new javax.swing.JLabel();
        userTextField = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        passwordField = new javax.swing.JPasswordField();
        jLabel7 = new javax.swing.JLabel();
        encodingComboBox = new javax.swing.JComboBox();
        cancelButton = new javax.swing.JButton();
        okButton = new javax.swing.JButton();
        testButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        connectionsScrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder("Connections"));

        connectionsList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        connectionsScrollPane.setViewportView(connectionsList);

        addButton.setText("New");
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });

        deleteButton.setText("Delete");
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });

        parametersPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Parameters"));

        jLabel1.setText("Name");

        jLabel2.setText("Host");

        jLabel3.setText("File");

        jLabel4.setText("Port");

        jLabel5.setText("User");

        jLabel6.setText("Password");

        jLabel7.setText("Encoding");

        encodingComboBox.setModel(DatabaseCharacterSets.getColumnCharacters());

        javax.swing.GroupLayout parametersPanelLayout = new javax.swing.GroupLayout(parametersPanel);
        parametersPanel.setLayout(parametersPanelLayout);
        parametersPanelLayout.setHorizontalGroup(
            parametersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(parametersPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(parametersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(parametersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(parametersPanelLayout.createSequentialGroup()
                            .addComponent(jLabel1)
                            .addGap(18, 18, 18)
                            .addComponent(nameTextField))
                        .addGroup(parametersPanelLayout.createSequentialGroup()
                            .addComponent(jLabel2)
                            .addGap(18, 18, 18)
                            .addComponent(hostTextField)
                            .addGap(18, 18, 18)
                            .addComponent(jLabel4)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(portTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(parametersPanelLayout.createSequentialGroup()
                            .addComponent(jLabel3)
                            .addGap(18, 18, 18)
                            .addComponent(fileTextField))
                        .addComponent(jSeparator1)
                        .addGroup(parametersPanelLayout.createSequentialGroup()
                            .addComponent(jLabel5)
                            .addGap(18, 18, 18)
                            .addComponent(userTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addComponent(jLabel6)
                            .addGap(18, 18, 18)
                            .addComponent(passwordField, javax.swing.GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE)))
                    .addGroup(parametersPanelLayout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addGap(18, 18, 18)
                        .addComponent(encodingComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        parametersPanelLayout.setVerticalGroup(
            parametersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(parametersPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(parametersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(nameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(parametersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(hostTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(portTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(parametersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(fileTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(parametersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(userTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(passwordField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(parametersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(encodingComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        okButton.setText("Ok");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        testButton.setText("Test connection");
        testButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                testButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(connectionsScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addComponent(addButton, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(deleteButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(parametersPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addComponent(testButton, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(connectionsScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 489, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(parametersPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addButton)
                    .addComponent(deleteButton)
                    .addComponent(cancelButton)
                    .addComponent(okButton)
                    .addComponent(testButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed

		dispose();
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed

		isOk = true;
		dispose();
    }//GEN-LAST:event_okButtonActionPerformed

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed

		connectionsListModel.addElement(new ConnectionProperties("Connection"));
		
		int row = connectionsListModel.getSize() - 1;
		
		connectionsList.setSelectedIndex(row);
		nameTextField.requestFocus();
    }//GEN-LAST:event_addButtonActionPerformed

    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed

		int row = connectionsList.getSelectedIndex();
		
		if(row >= 0) {
			
			connectionsListModel.removeElementAt(row--);
			
			if(row >= 0) {
				
				lastSelection = row;
				loadConnectionProperty(row);
				
				connectionsList.setSelectedIndex(row);
			}
		}
    }//GEN-LAST:event_deleteButtonActionPerformed

    private void testButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_testButtonActionPerformed

		int row = connectionsList.getSelectedIndex();
		
		if(row >= 0) {
			
			ConnectionProperties conn = connectionsListModel.getElementAt(row);
			
			if(conn.isValid()) {
				try {
					
					if(DatabaseConnection.testConnection(conn.getConnectionUrl(), conn)) {
						
						JOptionPane.showMessageDialog(
							mainPanel
							, ApplicationResources.getInstance().getString("database.connection.working") + " (" 
								+ conn.getConnectionUrl()
								+ ")"
							, ApplicationResources.getInstance().getString("database.connection.title")
							, JOptionPane.INFORMATION_MESSAGE);
						
					} else {
						
						JOptionPane.showMessageDialog(
							mainPanel
							, ApplicationResources.getInstance().getString("database.connection.nonworking")
							, ApplicationResources.getInstance().getString("database.connection.title")
							, JOptionPane.INFORMATION_MESSAGE);
					}
					
				} catch (ClassNotFoundException ex) {
					
					JOptionPane.showMessageDialog(
						mainPanel
						, ApplicationResources.getInstance().getString("database.connection.driver")
						, ApplicationResources.getInstance().getString("database.connection.title")
						, JOptionPane.INFORMATION_MESSAGE);
					
				} catch (SQLException ex) {
					
					JOptionPane.showMessageDialog(
						mainPanel
						, ApplicationResources.getInstance().getString("database.connection.sql") + ex.getMessage()
						, ApplicationResources.getInstance().getString("database.connection.title")
						, JOptionPane.INFORMATION_MESSAGE);
				}
				
			} else {
				
				JOptionPane.showMessageDialog(
					mainPanel
					, ApplicationResources.getInstance().getString("database.connection.params")
					, ApplicationResources.getInstance().getString("database.connection.title")
					, JOptionPane.INFORMATION_MESSAGE);
			}
		}
    }//GEN-LAST:event_testButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JButton cancelButton;
    private javax.swing.JList connectionsList;
    private javax.swing.JScrollPane connectionsScrollPane;
    private javax.swing.JButton deleteButton;
    private javax.swing.JComboBox encodingComboBox;
    private javax.swing.JTextField fileTextField;
    private javax.swing.JTextField hostTextField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JTextField nameTextField;
    private javax.swing.JButton okButton;
    private javax.swing.JPanel parametersPanel;
    private javax.swing.JPasswordField passwordField;
    private javax.swing.JTextField portTextField;
    private javax.swing.JButton testButton;
    private javax.swing.JTextField userTextField;
    // End of variables declaration//GEN-END:variables
}
