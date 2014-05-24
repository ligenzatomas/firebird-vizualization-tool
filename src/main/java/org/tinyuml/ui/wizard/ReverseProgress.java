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

import java.awt.GridLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;
import javax.swing.text.BadLocationException;
import org.firebirdvisualizationtool.database.firebird.DatabaseMetaData;
import org.tinyuml.model.DatabaseModel;
import org.tinyuml.model.Domain;
import org.tinyuml.model.Procedure;
import org.tinyuml.model.Sequence;
import org.tinyuml.model.Table;
import org.tinyuml.model.View;

/**
 *
 * @author Tomáš Ligenza
 */
public class ReverseProgress extends JDialog {

    private static final String s = "Connecting database";
    private final JProgressBar progressBar = new JProgressBar(0, 100);
    private final JLabel label = new JLabel(s, JLabel.CENTER);
	private TwoWorker task;

    public ReverseProgress(JDialog aParent) {
		
		this.setResizable(false);
		this.setUndecorated(true);
		
        this.setLayout(new GridLayout(0, 1));
        this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        this.add(progressBar);
        this.add(label);
        this.setSize(261, 100);
        this.setLocationRelativeTo(aParent);
		this.setAlwaysOnTop(true);
    }
	
	public void startDialog() {
		
		this.setVisible(true);
		progressBar.setStringPainted(true);
		progressBar.setValue(0);
		progressBar.setString("0 %");
	}
	
	public void closeDialog() {
		
		this.dispose();
	}

    public void runCalc(Connection aConnection, DatabaseModel aDatabaseModel, JEditorPane aEditorPane) {
		
        progressBar.setIndeterminate(true);
        task = new TwoWorker(aConnection, aDatabaseModel, aEditorPane);
        task.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent e) {

                if ("progress".equals(e.getPropertyName())) {
					
                    progressBar.setIndeterminate(false);
                    progressBar.setValue((Integer) e.getNewValue());
					progressBar.setString((Integer) e.getNewValue() + " %");
                } else if ("state".equals(e.getPropertyName())
					&& "DONE".equals(e.getNewValue().toString())) {
					
					closeDialog();
				} else if ("state".equals(e.getPropertyName())
					&& "STARTED".equals(e.getNewValue().toString())) {
					
					startDialog();
				}
            }
        });
		
        task.execute();
    }

    private class TwoWorker extends SwingWorker<String, String> {

        private final Connection connection;
		private final DatabaseModel databaseModel;
		private final JEditorPane editorPane;
		
		public TwoWorker(Connection aConnection, DatabaseModel aDatabaseModel, JEditorPane aEditorPane) {
			
			this.connection = aConnection;
			this.databaseModel = aDatabaseModel;
			this.editorPane = aEditorPane;
		}

        @Override
        protected String doInBackground() throws Exception {
			
			Statement statement = connection.createStatement();
			statement.getConnection().setAutoCommit(false);

			try {
				publish("Fetching domains");

				TreeSet<Domain> domains = 
					DatabaseMetaData.getDatabaseDomains(connection);
				
				editorPane.getDocument().insertString(
					editorPane.getDocument().getLength()
					, "Fetched " + domains.size() + " domains. \n"
					, null);
				
				setProgress(20);
				publish("Fetching sequences");

				TreeSet<Sequence> sequences = 
					DatabaseMetaData.getDatabaseSequences(connection);
				
				editorPane.getDocument().insertString(
					editorPane.getDocument().getLength()
					, "Fetched " + sequences.size() + " sequences. \n"
					, null);
				
				setProgress(40);
                publish("Fetching tables");

				TreeSet<Table> tables =
					DatabaseMetaData.getDatabaseTables(connection);
				
				editorPane.getDocument().insertString(
					editorPane.getDocument().getLength()
					, "Fetched " + tables.size() + " tables. \n"
					, null);
				
				setProgress(60);
				publish("Fetching procedures");

				TreeSet<Procedure> procedures =
					DatabaseMetaData.getDatabaseProcedures(connection);
				
				editorPane.getDocument().insertString(
					editorPane.getDocument().getLength()
					, "Fetched " + procedures.size() + " procedures. \n"
					, null);
				
				setProgress(80);
				publish("Fetching views");

				TreeSet<View> views = 
					DatabaseMetaData.getDatabaseViews(connection);
				
				editorPane.getDocument().insertString(
					editorPane.getDocument().getLength()
					, "Fetched " + views.size() + " views. \n"
					, null);
				
				setProgress(90);
                publish("Setting model");

				databaseModel.setDomains(domains);
				databaseModel.setSequences(sequences);
				databaseModel.setTables(tables);
				databaseModel.setProcedures(procedures);
				databaseModel.setViews(views);
				
				setProgress(100);
				
			} catch (SQLException e) {

				String message = "";
				
				do {
					message = message.concat(e.getMessage() + "\n");
					
					e = e.getNextException();

				} while(e instanceof SQLException);
				
				statement.getConnection().rollback();
				
				try {

					editorPane.getDocument().insertString(
						editorPane.getDocument().getLength()
						, message
						, null);

				} catch (BadLocationException ex) {
					
					Logger.getLogger(ForwardWizard.class.getName()).log(Level.SEVERE, null, ex);
				}
			} catch (BadLocationException ex) {
					
				Logger.getLogger(ForwardWizard.class.getName()).log(Level.SEVERE, null, ex);
			}

			statement.getConnection().commit();
			
            return "Done";
        }

        @Override
        protected void process(List<String> chunks) {

            for (String s : chunks) {
                label.setText(s);
            }
        }
    }
}
