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

package org.tinyuml.ui.wizard;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import javax.swing.text.BadLocationException;
import org.ciscavate.cjwizard.PageFactory;
import org.ciscavate.cjwizard.StackWizardSettings;
import org.ciscavate.cjwizard.WizardContainer;
import org.ciscavate.cjwizard.WizardListener;
import org.ciscavate.cjwizard.WizardPage;
import org.ciscavate.cjwizard.WizardSettings;
import org.firebirdvisualizationtool.database.firebird.ConnectionProperties;
import org.firebirdvisualizationtool.database.firebird.Connections;
import org.firebirdvisualizationtool.database.firebird.DatabaseConnection;
import org.firebirdvisualizationtool.database.firebird.SqlScriptCreator;
import org.tinyuml.model.DatabaseModel;
import org.tinyuml.util.ApplicationResources;

/**
 *
 * @author Tomáš Ligenza
 */
public class ForwardWizard extends JDialog {
   
   private final DatabaseModel databaseModel;
   
   private ConnectionProperties connectionProperties;
   
   private Boolean dropObjects = false;
   private Boolean skipForeignKeys = false;
   
   private Boolean exportTables = false;
   private Boolean exportViews = false;
   private Boolean exportProcedures = false;
   private Boolean exportSequencers = false;
   private Boolean exportDomains = false;
   
   private String sqlScript;
   
   public ForwardWizard(DatabaseModel aDatabaseModel) {
      
      final WizardContainer wc =
         new WizardContainer(new TestFactory(),
                             new WizardPageTemplate(new String[] {"Connection", "Options", "Elements", "SQL script", "Commit"}),
                             new StackWizardSettings());
      
      wc.addWizardListener(new WizardListener(){
         @Override
         public void onCanceled(List<WizardPage> path, WizardSettings settings) {
           
            ForwardWizard.this.dispose();
         }

         @Override
         public void onFinished(List<WizardPage> path, WizardSettings settings) {
            
            ForwardWizard.this.dispose();
         }

         @Override
         public void onPageChanged(WizardPage newPage, List<WizardPage> path) {
            
            ForwardWizard.this.setTitle(newPage.getDescription());
         }
      });
      
      this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	  this.setMinimumSize(new Dimension(860, 800));
      this.getContentPane().add(wc);
	  this.setModal(true);
      this.pack();
	  
	  this.databaseModel = aDatabaseModel;
   }

   /**
    * Implementation of PageFactory to generate the wizard pages needed
    * for the wizard.
    */
   private class TestFactory implements PageFactory{
      
      /* (non-Javadoc)
       * @see org.ciscavate.cjwizard.PageFactory#createPage(java.util.List, org.ciscavate.cjwizard.WizardSettings)
       */
      @Override
      public WizardPage createPage(List<WizardPage> path,
            WizardSettings settings) {
         
         WizardPage page = buildPage(path.size(), settings);


         return page;
      }

		 private WizardPage buildPage(int pageCount, final WizardSettings settings) {
			  switch (pageCount)  {
				 case 0: 
					return new WizardPage("Connection", "Set connection to Firebird database server") {
						{
							JLabel jLabel2 = new javax.swing.JLabel();
							JComboBox<ConnectionProperties> jComboBox1 = new javax.swing.JComboBox();

							jLabel2.setText("Connections:");
							jLabel2.setFont(jLabel2.getFont().deriveFont(Font.BOLD));
							
							jComboBox1.setRenderer(new BasicComboBoxRenderer() {
								public Component getListCellRendererComponent(
									JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
								{
									super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

									if (value instanceof ConnectionProperties)
									{
										ConnectionProperties foo = (ConnectionProperties)value;
										setText( foo.getName());
									}

									return this;
								}
							});
							
							jComboBox1.addItemListener(new ItemListener() {

								@Override
								public void itemStateChanged(ItemEvent e) {
									
									if(e.getStateChange() == ItemEvent.SELECTED) {
										
										ConnectionProperties conn = (ConnectionProperties) e.getItem();
										
										if(conn.isValid()) {
											
											try {

												if(DatabaseConnection.testConnection(
													conn.getConnectionUrl(), conn)) {

													JOptionPane.showMessageDialog(
														null
														, "Úspěšně navázáno spojení s databází (" 
															+ conn.getConnectionUrl()
															+ ")"
														, "Test spojení s databází"
														, JOptionPane.INFORMATION_MESSAGE);
													
													connectionProperties = conn;

												} else {

													JOptionPane.showMessageDialog(
														null
														, ApplicationResources.getInstance().getString("database.connection.driver")
														, ApplicationResources.getInstance().getString("database.connection.title")
														, JOptionPane.INFORMATION_MESSAGE);
												}

											} catch (ClassNotFoundException ex) {

												JOptionPane.showMessageDialog(
													null
													, ApplicationResources.getInstance().getString("database.connection.driver")
													, ApplicationResources.getInstance().getString("database.connection.title")
													, JOptionPane.INFORMATION_MESSAGE);

											} catch (SQLException ex) {

												JOptionPane.showMessageDialog(
													null
													, ApplicationResources.getInstance().getString("database.connection.sql") + ex.getMessage()
													, ApplicationResources.getInstance().getString("database.connection.title")
													, JOptionPane.INFORMATION_MESSAGE);
											}

										} else {

											JOptionPane.showMessageDialog(
												null
												, ApplicationResources.getInstance().getString("database.connection.params")
												, ApplicationResources.getInstance().getString("database.connection.title")
												, JOptionPane.INFORMATION_MESSAGE);
										}
									}
								}
							});
							
							jComboBox1.setModel(Connections.getInstance().getConnectionsComboBoxModel());
							
							if(jComboBox1.getModel().getSize() > 0)
								connectionProperties = jComboBox1.getItemAt(0);
							
							javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
							this.setLayout(layout);
							layout.setHorizontalGroup(
								layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addGroup(layout.createSequentialGroup()
									.addContainerGap()
									.addComponent(jLabel2)
									.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
									.addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
									.addContainerGap(248, Short.MAX_VALUE))
							);
							layout.setVerticalGroup(
								layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addGroup(layout.createSequentialGroup()
									.addContainerGap()
									.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
										.addComponent(jLabel2)
										.addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
									.addContainerGap(264, Short.MAX_VALUE))
							);
						}
					};
				case 1:
					return new WizardPage("Options", "Set options for generating DDL SQL script"){
						 {
							JPanel jPanel1 = new javax.swing.JPanel();
							JCheckBox jCheckBox1 = new javax.swing.JCheckBox();
							JCheckBox jCheckBox2 = new javax.swing.JCheckBox();

							jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Options"));

							jCheckBox1.setText("Drop objects before each create object");
							jCheckBox1.addItemListener(new ItemListener() {

								@Override
								public void itemStateChanged(ItemEvent e) {
									
									if(e.getStateChange() == ItemEvent.SELECTED)
										dropObjects = true;
									else
										dropObjects = false;
								}
							});

							jCheckBox2.setText("Skip creation of foreing keys");
							jCheckBox2.addItemListener(new ItemListener() {

								@Override
								public void itemStateChanged(ItemEvent e) {
									
									if(e.getStateChange() == ItemEvent.SELECTED)
										skipForeignKeys = true;
									else
										skipForeignKeys = false;
								}
							});

							javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
							jPanel1.setLayout(jPanel1Layout);
							jPanel1Layout.setHorizontalGroup(
								jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addGroup(jPanel1Layout.createSequentialGroup()
									.addContainerGap()
									.addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
										.addComponent(jCheckBox1)
										.addComponent(jCheckBox2))
									.addContainerGap(262, Short.MAX_VALUE))
							);
							jPanel1Layout.setVerticalGroup(
								jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addGroup(jPanel1Layout.createSequentialGroup()
									.addContainerGap()
									.addComponent(jCheckBox1)
									.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
									.addComponent(jCheckBox2)
									.addContainerGap(196, Short.MAX_VALUE))
							);

							javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
							this.setLayout(layout);
							layout.setHorizontalGroup(
								layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addGroup(layout.createSequentialGroup()
									.addContainerGap()
									.addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
									.addContainerGap())
							);
							layout.setVerticalGroup(
								layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addGroup(layout.createSequentialGroup()
									.addContainerGap()
									.addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
									.addContainerGap())
							);
						 }
					};
				case 2:
					return new WizardPage("Elements", "Select elements"){
						{
							JPanel jPanel1 = new javax.swing.JPanel();
							JCheckBox jCheckBox1 = new javax.swing.JCheckBox();
							JPanel jPanel2 = new javax.swing.JPanel();
							JCheckBox jCheckBox2 = new javax.swing.JCheckBox();
							JPanel jPanel3 = new javax.swing.JPanel();
							JCheckBox jCheckBox3 = new javax.swing.JCheckBox();
							JPanel jPanel4 = new javax.swing.JPanel();
							JCheckBox jCheckBox4 = new javax.swing.JCheckBox();
							JPanel jPanel5 = new javax.swing.JPanel();
							JCheckBox jCheckBox5 = new javax.swing.JCheckBox();

							jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Tables"));
							
							jCheckBox1.addItemListener(new ItemListener() {

								@Override
								public void itemStateChanged(ItemEvent e) {
									
									exportTables = e.getStateChange() == ItemEvent.SELECTED;
								}
							});

							int tableSize = databaseModel.getTables().size();
							jCheckBox1.setText("Export table objects (" + tableSize + ")");
							
							if(tableSize > 0)
								jCheckBox1.setSelected(true);

							javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
							jPanel1.setLayout(jPanel1Layout);
							jPanel1Layout.setHorizontalGroup(
								jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addGroup(jPanel1Layout.createSequentialGroup()
									.addContainerGap()
									.addComponent(jCheckBox1)
									.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
							);
							jPanel1Layout.setVerticalGroup(
								jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addGroup(jPanel1Layout.createSequentialGroup()
									.addContainerGap()
									.addComponent(jCheckBox1)
									.addContainerGap(9, Short.MAX_VALUE))
							);

							jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Views"));
							
							jCheckBox2.addItemListener(new ItemListener() {

								@Override
								public void itemStateChanged(ItemEvent e) {
									
									exportViews = e.getStateChange() == ItemEvent.SELECTED;
								}
							});

							int viewSize = databaseModel.getViews().size();
							jCheckBox2.setText("Export view objects (" + viewSize + ")");
							
							if(viewSize > 0)
								jCheckBox2.setSelected(true);

							javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
							jPanel2.setLayout(jPanel2Layout);
							jPanel2Layout.setHorizontalGroup(
								jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addGroup(jPanel2Layout.createSequentialGroup()
									.addContainerGap()
									.addComponent(jCheckBox2)
									.addContainerGap(254, Short.MAX_VALUE))
							);
							jPanel2Layout.setVerticalGroup(
								jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addGroup(jPanel2Layout.createSequentialGroup()
									.addContainerGap()
									.addComponent(jCheckBox2)
									.addContainerGap(9, Short.MAX_VALUE))
							);

							jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Procedures"));							
							
							jCheckBox3.addItemListener(new ItemListener() {

								@Override
								public void itemStateChanged(ItemEvent e) {
									
									exportProcedures = e.getStateChange() == ItemEvent.SELECTED;
								}
							});

							int proceduresSize = databaseModel.getProcedures().size();
							jCheckBox3.setText("Export procedure objects (" + proceduresSize + ")");
							
							if(proceduresSize > 0)
								jCheckBox3.setSelected(true);

							javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
							jPanel3.setLayout(jPanel3Layout);
							jPanel3Layout.setHorizontalGroup(
								jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addGroup(jPanel3Layout.createSequentialGroup()
									.addContainerGap()
									.addComponent(jCheckBox3)
									.addContainerGap(254, Short.MAX_VALUE))
							);
							jPanel3Layout.setVerticalGroup(
								jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addGroup(jPanel3Layout.createSequentialGroup()
									.addContainerGap()
									.addComponent(jCheckBox3)
									.addContainerGap(9, Short.MAX_VALUE))
							);

							jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Sequencers"));
							
							jCheckBox4.addItemListener(new ItemListener() {

								@Override
								public void itemStateChanged(ItemEvent e) {
									
									exportSequencers = e.getStateChange() == ItemEvent.SELECTED;
								}
							});

							int sequencesSize = databaseModel.getSequences().size();
							jCheckBox4.setText("Export sequencers (" + sequencesSize + ")");
							
							if(sequencesSize > 0)
								jCheckBox4.setSelected(true);

							javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
							jPanel4.setLayout(jPanel4Layout);
							jPanel4Layout.setHorizontalGroup(
								jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addGroup(jPanel4Layout.createSequentialGroup()
									.addContainerGap()
									.addComponent(jCheckBox4)
									.addContainerGap(254, Short.MAX_VALUE))
							);
							jPanel4Layout.setVerticalGroup(
								jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addGroup(jPanel4Layout.createSequentialGroup()
									.addContainerGap()
									.addComponent(jCheckBox4)
									.addContainerGap(9, Short.MAX_VALUE))
							);
							
							jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("Domains"));
							
							jCheckBox5.addItemListener(new ItemListener() {

								@Override
								public void itemStateChanged(ItemEvent e) {
									
									exportDomains = e.getStateChange() == ItemEvent.SELECTED;
								}
							});

							int domainsSize = databaseModel.getDomains().size();
							jCheckBox5.setText("Export domains (" + domainsSize + ")");
							
							if(domainsSize > 0)
								jCheckBox5.setSelected(true);

							javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
							jPanel5.setLayout(jPanel5Layout);
							jPanel5Layout.setHorizontalGroup(
								jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addGroup(jPanel5Layout.createSequentialGroup()
									.addContainerGap()
									.addComponent(jCheckBox5)
									.addContainerGap(254, Short.MAX_VALUE))
							);
							jPanel5Layout.setVerticalGroup(
								jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addGroup(jPanel5Layout.createSequentialGroup()
									.addContainerGap()
									.addComponent(jCheckBox5)
									.addContainerGap(9, Short.MAX_VALUE))
							);

							javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
							this.setLayout(layout);
							layout.setHorizontalGroup(
								layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addGroup(layout.createSequentialGroup()
									.addContainerGap()
									.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
										.addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(jPanel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(jPanel5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
									.addContainerGap())
							);
							layout.setVerticalGroup(
								layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addGroup(layout.createSequentialGroup()
									.addContainerGap()
									.addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
									.addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
									.addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
									.addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
									.addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
									.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
							);
						}
						
						public void rendering(List<WizardPage> path, WizardSettings settings) {
							super.rendering(path, settings);
							
							setNextText("Next >");
							setPrevEnabled(false);
						}
					};
				case 3:
					return new WizardPage("SQL script", "Edit DDL SQL script"){
						{
							JLabel jLabel1 = new javax.swing.JLabel();
							JScrollPane jScrollPane1 = new javax.swing.JScrollPane();
							final JEditorPane jEditorPane1 = new javax.swing.JEditorPane();
							JButton jButton1 = new javax.swing.JButton();
							JButton jButton2 = new javax.swing.JButton();

							jLabel1.setText("DDL SQL script to execute:");

							jScrollPane1.setViewportView(jEditorPane1);
							
							jEditorPane1.getDocument().addDocumentListener(new DocumentListener() {

								@Override
								public void insertUpdate(DocumentEvent e) {
									try {
										setText(e.getDocument().getText(0, e.getDocument().getLength()));
									} catch (BadLocationException ex) {
										Logger.getLogger(ForwardWizard.class.getName()).log(Level.SEVERE, null, ex);
									}
								}

								@Override
								public void removeUpdate(DocumentEvent e) {
									try {
										setText(e.getDocument().getText(0, e.getDocument().getLength()));
									} catch (BadLocationException ex) {
										Logger.getLogger(ForwardWizard.class.getName()).log(Level.SEVERE, null, ex);
									}
								}

								@Override
								public void changedUpdate(DocumentEvent e) {
									try {
										setText(e.getDocument().getText(0, e.getDocument().getLength()));
									} catch (BadLocationException ex) {
										Logger.getLogger(ForwardWizard.class.getName()).log(Level.SEVERE, null, ex);
									}
								}
								
								private void setText(String text) {
									
									sqlScript = text;
								}
							});
							
							try {
								jEditorPane1.getDocument().insertString(
									0
									, SqlScriptCreator.getInstance().getDatabaseModelSqlScript(
										databaseModel
										, dropObjects
										, skipForeignKeys
										, exportTables
										, exportViews
										, exportProcedures
										, exportSequencers
										, exportDomains)
									, null);
							} catch (BadLocationException ex) {
								Logger.getLogger(ForwardWizard.class.getName()).log(Level.SEVERE, null, ex);
							}

							jButton1.setText("Save to file");
							
							jButton1.addActionListener(new ActionListener() {
								
								public void actionPerformed(ActionEvent e) {
									JFileChooser fileChooser = new JFileChooser();
									fileChooser.setDialogTitle("Save DDL SQL script");
									fileChooser.setFileFilter(new FileNameExtensionFilter(
										"SQL script (*.sql)", "sql"));
									fileChooser.setAcceptAllFileFilterUsed(false);
									
									if (fileChooser.showSaveDialog(jEditorPane1) ==
										JFileChooser.APPROVE_OPTION) {
										
										File file = fileChooser.getSelectedFile();
										
										if(!file.getName().endsWith(".sql")) {
											file = new File(file.getParent(), file.getName().replaceAll("(\\.)+([\\s\\S])*", "") + ".sql");
										}
										
										PrintWriter oWriter = null;
										try {
											oWriter = new PrintWriter(file);
										} catch (FileNotFoundException ex) {
											Logger.getLogger(ForwardWizard.class.getName()).log(Level.SEVERE, null, ex);
										}
										try {
											oWriter.write(jEditorPane1.getDocument().getText(0, jEditorPane1.getDocument().getLength()));
											oWriter.close();
										} catch (BadLocationException ex) {
											Logger.getLogger(ForwardWizard.class.getName()).log(Level.SEVERE, null, ex);
										}
									}
								}
							});

							jButton2.setText("Copy to clipboard");
							
							jButton2.addActionListener(new ActionListener() {
								public void actionPerformed(java.awt.event.ActionEvent evt) {
									try {
										StringSelection selection = new StringSelection(
											jEditorPane1.getDocument().getText(0, jEditorPane1.getDocument().getLength()));
										Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
										clipboard.setContents(selection, selection);
									} catch (BadLocationException ex) {
										Logger.getLogger(ForwardWizard.class.getName()).log(Level.SEVERE, null, ex);
									}
								}
							});

							javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
							this.setLayout(layout);
							layout.setHorizontalGroup(
								layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addGroup(layout.createSequentialGroup()
									.addContainerGap()
									.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
										.addComponent(jScrollPane1)
										.addGroup(layout.createSequentialGroup()
											.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
												.addComponent(jLabel1)
												.addGroup(layout.createSequentialGroup()
													.addComponent(jButton1)
													.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
													.addComponent(jButton2)))
											.addGap(0, 211, Short.MAX_VALUE)))
									.addContainerGap())
							);
							layout.setVerticalGroup(
								layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addGroup(layout.createSequentialGroup()
									.addContainerGap()
									.addComponent(jLabel1)
									.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
									.addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 600, javax.swing.GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
									.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
										.addComponent(jButton1)
										.addComponent(jButton2))
									.addContainerGap(13, Short.MAX_VALUE))
							);
						}
						
						public void rendering(List<WizardPage> path, WizardSettings settings) {
							super.rendering(path, settings);
							
							setNextText("Commit >");
						}
					};
				case 4:
					return new WizardPage("Commit", "Commit progress"){
						{
							JLabel jLabel1 = new javax.swing.JLabel();
							JLabel jLabel2 = new javax.swing.JLabel();
							JScrollPane jScrollPane1 = new javax.swing.JScrollPane();
							final JEditorPane jEditorPane1 = new javax.swing.JEditorPane();

							jLabel1.setText("Logs from commit:");

							jScrollPane1.setViewportView(jEditorPane1);
							
							javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
							this.setLayout(layout);
							layout.setHorizontalGroup(
								layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addGroup(layout.createSequentialGroup()
									.addContainerGap()
									.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
										.addComponent(jScrollPane1)
										.addGroup(layout.createSequentialGroup()
											.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
												.addComponent(jLabel1))
											.addGap(0, 211, Short.MAX_VALUE))
										.addGroup(layout.createSequentialGroup()
											.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
												.addComponent(jLabel2))
											.addGap(0, 211, Short.MAX_VALUE)))
									.addContainerGap())
							);
							layout.setVerticalGroup(
								layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addGroup(layout.createSequentialGroup()
									.addContainerGap()
									.addComponent(jLabel1)
									.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
									.addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 600, javax.swing.GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
									.addComponent(jLabel2)
									.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
									.addContainerGap(13, Short.MAX_VALUE))
							);
							
							ConnectionProperties conn = connectionProperties;
										
							if(conn != null && conn.isValid()) {

								try {

									DatabaseConnection.initConnection(conn.getConnectionUrl(), connectionProperties);
									
									if(DatabaseConnection.isValid()) {
										
										Statement statement = DatabaseConnection.getConnection().createStatement();
										statement.getConnection().setAutoCommit(false);
										String state = "";
										
										try {
											
											String[] statements = sqlScript.replaceAll("COMMIT;", "").split(";");
											
											for(String s : statements) {
												
												state = s.trim();
												
												if(!state.isEmpty() && !statement.execute(state)) {
													jLabel2.setText("See log for exceptions.");
												} else {
													jLabel2.setText("Commit successful.");
												}
											}
										} catch (SQLException e) {
											
											do {
												try {
													statement.getConnection().rollback();
													jEditorPane1.getDocument().insertString(0, e.getMessage() + "\n IN \n" + state, null);
												} catch (BadLocationException ex) {
													Logger.getLogger(ForwardWizard.class.getName()).log(Level.SEVERE, null, ex);
												}
												
												e = e.getNextException();
												
											} while(e instanceof SQLException);
										}
										
										statement.getConnection().commit();

									} else {

										JOptionPane.showMessageDialog(
											null
											, ApplicationResources.getInstance().getString("database.connection.nonworking")
											, ApplicationResources.getInstance().getString("database.connection.title")
											, JOptionPane.INFORMATION_MESSAGE);
									}

								} catch (ClassNotFoundException ex) {

									JOptionPane.showMessageDialog(
										null
										, ApplicationResources.getInstance().getString("database.connection.driver")
										, ApplicationResources.getInstance().getString("database.connection.title")
										, JOptionPane.INFORMATION_MESSAGE);


								} catch (SQLException ex) {

									JOptionPane.showMessageDialog(
										null
										, ApplicationResources.getInstance().getString("database.connection.sql") + ex.getMessage()
										, ApplicationResources.getInstance().getString("database.connection.title")
										, JOptionPane.INFORMATION_MESSAGE);
								}

							} else {

								JOptionPane.showMessageDialog(
									null
									, ApplicationResources.getInstance().getString("database.connection.params")
									, ApplicationResources.getInstance().getString("database.connection.title")
									, JOptionPane.INFORMATION_MESSAGE);
							}
						}

						public void rendering(List<WizardPage> path, WizardSettings settings) {
							 super.rendering(path, settings);

							 setFinishEnabled(true);
							 setNextEnabled(false);
							 setPrevEnabled(true);
						}
					};
			  }
			  return null;
		 }
      
   }
}
