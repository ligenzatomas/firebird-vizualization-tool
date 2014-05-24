/*
 * Copyright (C) 2014 cml
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package org.tinyuml.ui.wizard;

import java.awt.GridLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.TreeSet;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;
import org.tinyuml.model.DatabaseModel;
import org.tinyuml.model.Procedure;
import org.tinyuml.model.Table;
import org.tinyuml.model.View;
import org.tinyuml.ui.diagram.EERDiagramEditor;

/**
 * TODO - not working now
 * @author Tomáš Ligenza
 */
public class ExportProgress extends JDialog {
	
	private static final String s = "Connecting database";
    private final JProgressBar progressBar = new JProgressBar(0, 100);
    private final JLabel label = new JLabel(s, JLabel.CENTER);
	private TwoWorker task;

    public ExportProgress(JDialog aParent) {
		
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

    public void runCalc(EERDiagramEditor editor, DatabaseModel aDatabaseModel, boolean exportDomains, boolean exportSequencers, boolean exportTables, boolean exportViews, boolean exportProcedures) {
		
        progressBar.setIndeterminate(true);
        task = new TwoWorker(editor, aDatabaseModel, exportDomains, exportSequencers, exportTables, exportViews, exportProcedures);
        task.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent e) {
				System.out.println(" propertyChange " + e.getPropertyName() + " " + e.getNewValue().toString());
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

        private final EERDiagramEditor editor;
		private final DatabaseModel databaseModel;
		
		private Boolean exportTables;
		private Boolean exportViews;
		private Boolean exportProcedures;
		private Boolean exportSequencers;
		private Boolean exportDomains;
		
		public TwoWorker(EERDiagramEditor aEditor, DatabaseModel aDatabaseModel, boolean aExportDomains, boolean aExportSequencers, boolean aExportTables, boolean aExportViews, boolean aExportProcedures) {
			
			this.editor = aEditor;
			this.databaseModel = aDatabaseModel;
			
			this.exportDomains = aExportDomains;
			this.exportProcedures = aExportProcedures;
			this.exportSequencers = aExportSequencers;
			this.exportTables = aExportTables;
			this.exportViews = aExportViews;
			
			System.out.println(" TwoWorker " + aExportDomains+ aExportSequencers+ aExportTables+ aExportViews+ aExportProcedures);
		}

        @Override
        protected String doInBackground() throws Exception {
			
			int countAll = 1;
			int done = 1;
			//setProgress(0);
			publish("Preparing ...");
			System.out.println(" doInBackground ");
			System.out.println(" doInBackground2 " + exportDomains+ exportSequencers+ exportTables+ exportViews+ exportProcedures);
			//if(exportDomains)
				countAll += databaseModel.getDomains().size();

			//if(exportSequencers)
				countAll += databaseModel.getSequences().size();

			//if(exportTables)
				countAll += databaseModel.getTables().size();

			//if(exportViews)
				countAll += databaseModel.getViews().size();

			//if(exportProcedures)
				countAll += databaseModel.getProcedures().size();

			//if(exportTables)
				countAll += databaseModel.getTables().size();
			
			System.out.println(" doInBackground3 " + databaseModel.getTables().size());
			
			//if(exportDomains) {
				System.out.println(" domains1 ");
				publish("Getting domains ...");
				editor.createAllDomains(databaseModel.getDomains());
				System.out.println(" domains2 ");
				done += databaseModel.getDomains().size();
				System.out.println(" domains3 ");
				System.out.println(" countAll " + countAll + " " + done);
				System.out.println(" countAll " + countAll + " " + done + " " + (((done / countAll) * 100)));
				setProgress(20);
			//}

			//if(exportSequencers) {
				System.out.println(" sequencers ");
				publish("Getting sequencers ...");
				editor.createAllSequences(databaseModel.getSequences());
				done += databaseModel.getSequences().size();
				System.out.println(" countAll " + countAll + " " + done);
				System.out.println(" countAll " + countAll + " " + done + " " + ((done / countAll) * 100));
				setProgress( 40);
			//}
			
			System.out.println(" doInBackground4 " + exportTables);

			//if(exportTables) {
				System.out.println(" tables ");
				publish("Getting tables ...");
				TreeSet<Table> tables = (TreeSet<Table>) databaseModel.getTables().clone();
				for(Table table : tables) {

					editor.createTable(table);
					done++;
					System.out.println(" countAll " + countAll + " " + done);
					System.out.println(" countAll float " + (float) done / countAll );
					System.out.println(" countAll " + countAll + " " + done + " " +  ((done / countAll) * 100));
					setProgress( 60);
				}
			//}

			//if(exportViews) {
				System.out.println(" views ");
				publish("Getting views ...");
				for(View view : databaseModel.getViews()) {

					editor.createView(view);
					done++;
					System.out.println(" countAll " + countAll + " " + done);
					System.out.println(" countAll " + countAll + " " + done + " " +  ((done / countAll) * 100));
					setProgress( 80);
				}
			//}

			//if(exportProcedures) {
				System.out.println(" procedures ");
				publish("Getting procedures ...");
				for(Procedure procedure : databaseModel.getProcedures()) {

					editor.createProcedure(procedure);
					done++;
					System.out.println(" countAll " + countAll + " " + done);
					System.out.println(" countAll " + countAll + " " + done + " " +  ((done / countAll) * 100));
					setProgress( 90);
				}
			//}

			//if(exportTables) {
				System.out.println(" connections ");
				publish("Creating connections ...");
				editor.createAllConnections();
				done += databaseModel.getTables().size();
				System.out.println(" countAll " + countAll + " " + done);
				System.out.println(" countAll " + countAll + " " + done + " " +  ((done / countAll) * 100));
				setProgress( 99);
			//}
				
			setProgress(100);
			
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
