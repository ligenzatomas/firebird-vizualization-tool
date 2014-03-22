/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tinyuml.ui;

import java.awt.Dialog;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.TreeMap;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import org.firebirdvisualizationtool.database.firebird.DatabaseColumnTypes;
import org.tinyuml.model.Domain;
import org.tinyuml.ui.model.DomainModel;
import org.tinyuml.util.ApplicationResources;
import org.tinyuml.util.TableAdjuster;

/**
 *
 * @author cml
 */
public class ManageDomains extends javax.swing.JDialog {
	
	private boolean isOk;
	
	private DomainModel domainModel = new DomainModel();
	private TableAdjuster adjuster = new TableAdjuster();
	
	public ManageDomains(java.awt.Window parent, TreeMap<String, Domain> aDomains) {
		
		super(parent, Dialog.ModalityType.APPLICATION_MODAL);
		
		for(Domain seq : aDomains.values()) {
			
			domainModel.addEntry(seq);
		}
		
		initComponents();
		myInit();
	}
	
	public void myInit() {
		
		domainTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		domainTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		adjuster.adjustColumns(domainTable);
		
		domainTable.addPropertyChangeListener( new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent e) {
				
				System.out.println(" propertyChange ");
				adjuster.adjustColumns(domainTable);
			}
		});
		
		domainTable.getModel().addTableModelListener( new TableModelListener() {

			@Override
			public void tableChanged(TableModelEvent e) {
				
				System.out.println(" tableChanged ");
				
				if (e.getType() == TableModelEvent.UPDATE
						&& e.getColumn() > 0) {
					
					int column = domainTable.convertColumnIndexToView(e.getColumn());
					
					adjuster.adjustColumn( column, domainTable );

				} else {

					adjuster.adjustColumns(domainTable);
				}
			}
		});
		
		domainTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				
				int index = domainTable.convertRowIndexToModel(domainTable.getSelectedRow());
				
				System.out.println(" selectChange - index - " + index + " - isAdjusting - " + e.getValueIsAdjusting());
				
				if(e.getValueIsAdjusting())
					return;
				else if(index < 0)
					deleteDomainButton.setEnabled(false);
				else
					deleteDomainButton.setEnabled(true);
			}
		});
		
		// TODO editace domeny, zatim needitujeme (je nutne zkontrolovat konzistenci se vsemi procedurami a triggery PSQL
		/*
		domainTable.addMouseListener(new MouseAdapter() {
			
			public void mouseClicked(MouseEvent e) {
				
				if(e.getClickCount() >= 2) {
					
					int row = domainTable.convertColumnIndexToModel(domainTable.getSelectedRow());
				}
			}
		});*/
	}
	
	public TreeMap<String, Domain> getDomains() {
		
		TreeMap<String, Domain> domains = new TreeMap<String, Domain>();
		
		for (Domain dom : domainModel.getEntries()) {
			
			if(dom.checkValid()) {
				
				domains.put(dom.getName(), dom);
			}
		}
		
		return domains;
	}
	
	public boolean isOk() { return isOk; }
	
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        domainTable = new javax.swing.JTable();
        cancellButton = new javax.swing.JButton();
        okButton = new javax.swing.JButton();
        addDomainButton = new javax.swing.JButton();
        deleteDomainButton = new javax.swing.JButton();
        editDomainButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Domains manager");

        domainTable.setModel(domainModel);
        jScrollPane1.setViewportView(domainTable);

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

        addDomainButton.setText("Přidat doménu");
        addDomainButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addDomainButtonActionPerformed(evt);
            }
        });

        deleteDomainButton.setText("Smazat doménu");
        deleteDomainButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteDomainButtonActionPerformed(evt);
            }
        });

        editDomainButton.setText("Editovat doménu");
        editDomainButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editDomainButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 620, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(addDomainButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(editDomainButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(deleteDomainButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(okButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cancellButton)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 550, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancellButton)
                    .addComponent(okButton)
                    .addComponent(addDomainButton)
                    .addComponent(deleteDomainButton)
                    .addComponent(editDomainButton))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cancellButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancellButtonActionPerformed

		dispose();
    }//GEN-LAST:event_cancellButtonActionPerformed

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed

		isOk = true;
		dispose();
    }//GEN-LAST:event_okButtonActionPerformed

    private void addDomainButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addDomainButtonActionPerformed

		EditDomainDialog dialog = new EditDomainDialog(this, null);
		
		dialog.setLocationRelativeTo(this);
		dialog.setVisible(true);
		
		if(dialog.isOk()) {

			Domain domain = dialog.getDomain();
			
			if(domain.checkValid()) {
				
				domainModel.addEntry(domain);
			}
		
			int row = domainModel.getRowCount() - 1;

			domainTable.changeSelection(domainTable.convertRowIndexToView(row), 0, false, false);
			domainTable.requestFocus();
		}
    }//GEN-LAST:event_addDomainButtonActionPerformed

    private void deleteDomainButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteDomainButtonActionPerformed

		/** TODO DODELAT KONTROLU, ZDA NEMA DOMENA ZAVISLOST NA NEJAKEM OBJEKTU (TREBA SLOUPEC u TABULKY
		 *  
		 *	PREDSTAVA
		 *	nejlepe asi domena sama obsahuje informace o zavislosti na nejakem objektu a pokud je ma, neni mozne ji vymazat, ale je mozne je vypsat
		 *  asi na zavislosti udelat nejaky objekt, nebot jsou u vice obejktu v db
		**/
		
		int row = domainTable.convertRowIndexToModel(domainTable.getSelectedRow());
		
		if(row >= 0) {
			
			domainModel.removeEntryAt(row--);
			
			if(row >= 0)
				domainTable.changeSelection(domainTable.convertRowIndexToView(row), 0, false, false);
		}
    }//GEN-LAST:event_deleteDomainButtonActionPerformed

    private void editDomainButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editDomainButtonActionPerformed

		int row = domainTable.convertRowIndexToModel(domainTable.getSelectedRow());
		
		if(row >= 0) {
			
			EditDomainDialog dialog = new EditDomainDialog(this, domainModel.getEntries().get(row));
		
			dialog.setLocationRelativeTo(this);
			dialog.setVisible(true);

			if(dialog.isOk()) {

				Domain domain = dialog.getDomain();

				domainModel.getEntries().set(row, domain);

				domainTable.requestFocus();
			}
		}
    }//GEN-LAST:event_editDomainButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addDomainButton;
    private javax.swing.JButton cancellButton;
    private javax.swing.JButton deleteDomainButton;
    private javax.swing.JTable domainTable;
    private javax.swing.JButton editDomainButton;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton okButton;
    // End of variables declaration//GEN-END:variables
}
