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


package org.firebirdvisualizationtool.database.firebird;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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
public class SqlScriptCreator {
	
	private SqlScriptCreator() {
	}
	
	public static SqlScriptCreator getInstance() {
		return SqlScriptCreatorHolder.INSTANCE;
	}
	
	public static String getCommitString() {
		
		return "\nCOMMIT;\n";
	}
	
	public String getDatabaseModelSqlScript(DatabaseModel databaseModel
		, boolean dropObjects
		, boolean skipForeignKeys
		, boolean exportTables
		, boolean exportViews
		, boolean exportProcedures
		, boolean exportSequencers
		, boolean exportDomains) {
		
		List<String> script = new ArrayList<String>();
		
		List<String> dropProcedures = new ArrayList<String>();
		String procedures = "";
		List<String> dropTriggers = new ArrayList<String>();
		String triggers = "";
		List<String> dropViews = new ArrayList<String>();
		String views = "";
		List<String> dropForeignKeys = new ArrayList<String>();
		String foreignKeys = "";
		List<String> dropTables = new ArrayList<String>();
		String tables = "";
		List<String> dropDomains = new ArrayList<String>();
		String domains = "";
		List<String> dropSequnces = new ArrayList<String>();
		String sequences = "";
		
		String sql = "";
		
		// creating sequences
		if(exportSequencers && !databaseModel.getSequences().isEmpty()) {
			
			Iterator sequenceIterator = databaseModel.getSequences().iterator();

			while(sequenceIterator.hasNext()) {

				script.add(((Sequence) sequenceIterator.next()).getCreateSql());
				
				if(dropObjects) {
					
					dropSequnces.add(((Sequence) sequenceIterator.next()).getDropSql());
				}
			}

			script.add(getCommitString());
			
			if(dropObjects && !dropSequnces.isEmpty()) {
				
				dropSequnces.add(getCommitString());
				
				StringBuilder dropBuilder = new StringBuilder();
				dropBuilder.append( dropSequnces.remove(0));

				for( String s : dropSequnces) {
					dropBuilder.append( "\n");
					dropBuilder.append( s );
				}

				sequences = dropBuilder.toString();
			}
		}
		
		// creating domains
		if(exportDomains && !databaseModel.getDomains().isEmpty()) {
			
			for (Domain domain : databaseModel.getDomains()) {
				
				script.add(domain.getCreateSql());
				
				if(dropObjects) {
					
					dropDomains.add(domain.getDropSql());
				}
			}

			script.add(getCommitString());
			
			if(dropObjects && !dropDomains.isEmpty()) {
				
				dropDomains.add(getCommitString());
				
				StringBuilder dropBuilder = new StringBuilder();
				dropBuilder.append( dropDomains.remove(0));

				for( String s : dropDomains) {
					dropBuilder.append( "\n");
					dropBuilder.append( s );
				}

				domains = dropBuilder.toString();
			}
		}
		
		// creating tables
		if(exportTables && !databaseModel.getTables().isEmpty()) {
		
			for (Table table : databaseModel.getTables()) {
				
				script.add(table.getCreateSql());
				
				if(dropObjects) {
					
					dropTables.add(table.getDropSql());
				}
			}

			script.add(getCommitString());
			
			if(dropObjects && !dropTables.isEmpty()) {
				
				dropTables.add(getCommitString());
				
				StringBuilder dropBuilder = new StringBuilder();
				dropBuilder.append( dropTables.remove(0));

				for( String s : dropTables) {
					dropBuilder.append( "\n");
					dropBuilder.append( s );
				}

				tables = dropBuilder.toString();
			}
		}
		
		// creating views
		if(exportViews && !databaseModel.getViews().isEmpty()) {
			
			for (View view : databaseModel.getViews()) {
				
				script.add(view.getCreateSql());
				
				if(dropObjects) {
					
					dropViews.add(view.getDropSql());
				}
			}

			script.add(getCommitString());
			
			if(dropObjects && !dropViews.isEmpty()) {
				
				dropViews.add(getCommitString());
				
				StringBuilder dropBuilder = new StringBuilder();
				dropBuilder.append( dropViews.remove(0));

				for( String s : dropViews) {
					dropBuilder.append( "\n");
					dropBuilder.append( s );
				}

				views = dropBuilder.toString();
			}
		}
		
		// creating procedures
		if(exportProcedures && !databaseModel.getProcedures().isEmpty()) {
			
			for (Procedure procedure : databaseModel.getProcedures()) {
				
				script.add(procedure.getCreateSql());
				
				if(dropObjects) {
					
					dropProcedures.add(procedure.getDropSql());
				}
			}

			script.add(getCommitString());
			
			if(dropObjects && !dropProcedures.isEmpty()) {
				
				dropProcedures.add(getCommitString());
				
				StringBuilder dropBuilder = new StringBuilder();
				dropBuilder.append( dropProcedures.remove(0));

				for( String s : dropProcedures) {
					dropBuilder.append( "\n");
					dropBuilder.append( s );
				}

				procedures = dropBuilder.toString();
			}
		}
		
		// creating triggers
		if(exportTables) {
			for (Table table : databaseModel.getTables()) {
				
				String triggerSql = table.getTriggerSql();

				if(!triggerSql.isEmpty()) {
					
					script.add(triggerSql);
					script.add(getCommitString());

					if(dropObjects) {

						dropTriggers.add(table.getTriggerSqlDrop());
					}
				}
			}

			if(dropObjects && !dropTriggers.isEmpty()) {

				dropTriggers.add(getCommitString());

				StringBuilder dropBuilder = new StringBuilder();
				dropBuilder.append( dropTriggers.remove(0));

				for( String s : dropTriggers) {
					dropBuilder.append( "\n");
					dropBuilder.append( s );
				}

				triggers = dropBuilder.toString();
			}
		}
		
		// creating indexes
		if(exportTables) {
			for (Table table : databaseModel.getTables()) {
				
				String indexSql = table.getIndexSql();

				if(!indexSql.isEmpty()) {
					script.add(indexSql);
					script.add(getCommitString());
				}
			}
		}
		
		// creating foreign keys
		if(exportTables && !skipForeignKeys) {
			
			for (Table table : databaseModel.getTables()) {
				
				String fkSql = table.getFkSql();

				if(!fkSql.isEmpty()) {
					script.add(fkSql);
					script.add(getCommitString());
					
					if(dropObjects) {
						dropForeignKeys.add(table.getFkSqlDrop());
					}
				}
			}
			
			if(dropObjects && !dropForeignKeys.isEmpty()) {
				
				dropForeignKeys.add(getCommitString());

				StringBuilder dropBuilder = new StringBuilder();
				dropBuilder.append( dropForeignKeys.remove(0));

				for( String s : dropForeignKeys) {
					dropBuilder.append( "\n");
					dropBuilder.append( s );
				}

				foreignKeys = dropBuilder.toString();
			}
		}
		
		StringBuilder builder = new StringBuilder();
		builder.append( script.remove(0));

		for( String s : script) {
			builder.append( "\n");
			builder.append( s);
		}
		
		sql = procedures + triggers + views + foreignKeys + tables + domains + sequences;

		return sql + "\n" + builder.toString();
	}
	
	private static class SqlScriptCreatorHolder {

		private static final SqlScriptCreator INSTANCE = new SqlScriptCreator();
	}
}
