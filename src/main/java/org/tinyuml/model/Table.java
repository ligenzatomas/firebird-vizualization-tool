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


package org.tinyuml.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.firebirdvisualizationtool.database.firebird.DatabaseTableCol;
import org.firebirdvisualizationtool.database.firebird.IndexTypes;

/**
 *
 * @author Tomáš Ligenza
 */
public class Table extends DefaultNamedElement {
	
	private static final long serialVersionUID = 1331820746185764074L;
	
	private List<Column> cols = new ArrayList<Column>();
	private List<Index> indexes = new ArrayList<Index>();
	private List<ForeignKey> foreignKeys = new ArrayList<ForeignKey>();
	private List<Trigger> triggers = new ArrayList<Trigger>();
	
	private static Table prototype;
	
	public static Table getPrototype() {
		
		if (prototype == null) prototype = new Table();
		
		return prototype;
	}

	protected Table() { }
	
	@Override
	public Object clone() {
		
		Table cloned = null;
		cloned = (Table) super.clone();
		
		cloned.cols = new ArrayList<Column>();
		for(Column c : cols) {
			cloned.cols.add(c);
		}
		
		cloned.indexes = new ArrayList<Index>();
		for(Index c : indexes) {
			cloned.indexes.add(c);
		}
		
		cloned.foreignKeys = new ArrayList<ForeignKey>();
		for(ForeignKey c : foreignKeys) {
			cloned.foreignKeys.add(c);
		}
		
		cloned.triggers = new ArrayList<Trigger>();
		for(Trigger c : triggers) {
			cloned.triggers.add(c);
		}
		
		return cloned;
	}
	
	public Table create(String sName) {
		
		Table cloned = null;
		cloned = (Table) super.clone();
		
		cloned.cols = new ArrayList<Column>();
		cloned.indexes = new ArrayList<Index>();
		cloned.foreignKeys = new ArrayList<ForeignKey>();
		cloned.triggers = new ArrayList<Trigger>();
		
		cloned.setName(sName);
		
		return cloned;
	}
	
	public void copy(Table aTable) {
		
		this.cols = aTable.getCols();
		this.indexes = aTable.getIndexes();
		this.foreignKeys = aTable.getForeignKeys();
		this.triggers = aTable.getTriggers();
		
		this.setName(aTable.getName());
	}

	public List<Column> getCols() {
		return cols;
	}

	public void setCols(List<Column> cols) {
		this.cols = cols;
	}

	public List<Index> getIndexes() {
		return indexes;
	}

	public void setIndexes(List<Index> indexes) {
		this.indexes = indexes;
	}

	public List<ForeignKey> getForeignKeys() {
		return foreignKeys;
	}

	public void setForeignKeys(List<ForeignKey> foreignKeys) {
		this.foreignKeys = foreignKeys;
	}

	public List<Trigger> getTriggers() {
		return triggers;
	}

	public void setTriggers(List<Trigger> triggers) {
		this.triggers = triggers;
	}
	
	public Column getColumnByName(String name) {
		
		Column column = null;
		
		for(Column col : getCols()) {
			
			if(col.getName().equals(name))
				column = col;
		}
		
		return column;
	}
	
	public boolean isPrimaryKeyColByName(String name) {
		
		for(Index index : this.indexes) {
			
			if(index.getiIndexType().equals(IndexTypes.PRIMARY)) {
				
				for(IndexColumn col : index.getCols()) {
					
					if(col.getName().equals(name))
						return true;
				}
			}
		}
		
		return false;
	}
	
	public boolean isPrimaryKey() {
		
		for(Index index : this.indexes) {
			
			if(index.getiIndexType().equals(IndexTypes.PRIMARY))
				return true;
		}
		
		return false;
	}
	
	public boolean isUniqueKey() {
		
		for(Index index : this.indexes) {
			
			if(index.getiIndexType().equals(IndexTypes.UNIQUE))
				return true;
		}
		
		return false;
	}
	
	public boolean isIndex() {
		
		for(Index index : this.indexes) {
			
			if(index.getiIndexType().equals(IndexTypes.INDEX))
				return true;
		}
		
		return false;
	}
	
	public boolean isForeignKey() {
		
		return !foreignKeys.isEmpty();
	}
	
	public String getDropSql() {
		
		return "DROP TABLE " + getName() + ";";
	}
	
	public String getCreateSql() {
		
		List<String> listScript = new ArrayList<String>();
		String script = "";
		
		// cols
		String cols = "";
		List<String> colsScript = new ArrayList<String>();
		
		Iterator colsIterator = this.cols.iterator();
		
		while(colsIterator.hasNext()) {
			
			colsScript.add(DatabaseTableCol.getSqlString(((Column) colsIterator.next())));
		}
		
		if(!colsScript.isEmpty()) {
			
			StringBuilder colsBuilder = new StringBuilder();
			colsBuilder.append( colsScript.remove(0));

			for( String s : colsScript) {
				colsBuilder.append( ", \n");
				colsBuilder.append( s);
			}
			
			cols = colsBuilder.toString();
		}
		
		if(!cols.isEmpty())
			listScript.add(cols);
		
		// primary key
		String primaryKey = "";
		
		if(isPrimaryKey()) {
			
			List<String> primaryScript = new ArrayList<String>();
			
			for(Index i : this.indexes) {
			
				if(i.getiIndexType().equals(IndexTypes.PRIMARY)) {
					
					for(IndexColumn col : i.getCols()) {
						primaryScript.add(col.getName());
					}
				}
			}
			
			if(!primaryScript.isEmpty()) {
			
				StringBuilder primaryBuilder = new StringBuilder();
				primaryBuilder.append( primaryScript.remove(0));

				for( String s : primaryScript) {
					primaryBuilder.append( ",");
					primaryBuilder.append( s);
				}

				primaryKey = "PRIMARY KEY (" + primaryBuilder.toString() + ")";
			}
		}
		
		if(!primaryKey.isEmpty())
			listScript.add(primaryKey);
		
		// unique key
		String uniqueKey = "";
		
		if(isUniqueKey()) {
			
			List<String> uniqueScript = new ArrayList<String>();
			
			for(Index i : this.indexes) {
			
				if(i.getiIndexType().equals(IndexTypes.UNIQUE)) {
					
					List<String> unique = new ArrayList<String>();
					
					for(IndexColumn col : i.getCols()) {
						unique.add(col.getName());
					}
					
					StringBuilder uniqueB = new StringBuilder();
					uniqueB.append( unique.remove(0));

					for( String s : unique) {
						uniqueB.append( ",");
						uniqueB.append( s);
					}

					uniqueScript.add("CONSTRAINT " + i.getName() + " UNIQUE (" + uniqueB.toString() + ")");
				}
			}
			
			if(!uniqueScript.isEmpty()) {
			
				StringBuilder uniqueBuilder = new StringBuilder();
				uniqueBuilder.append( uniqueScript.remove(0));

				for( String s : uniqueScript) {
					uniqueBuilder.append( ",");
					uniqueBuilder.append( s);
				}

				uniqueKey = uniqueBuilder.toString();
			}
		}
		
		if(!uniqueKey.isEmpty())
			listScript.add(uniqueKey);
		
		StringBuilder scriptBuilder = new StringBuilder();
		scriptBuilder.append( listScript.remove(0));

		for( String s : listScript) {
			scriptBuilder.append( ", \n");
			scriptBuilder.append( s);
		}
		
		script = "CREATE TABLE " + getName() + " (\n" + scriptBuilder.toString() + "\n);";

		return script;
	}
	
	public String getTriggerSqlDrop() {
		
		String script = "";
		List<String> triggerScript = new ArrayList<String>();
		
		for(Trigger trigger : getTriggers()) {
			
			triggerScript.add(trigger.getDropSql());
		}
		
		if(!triggerScript.isEmpty()) {
			StringBuilder builder = new StringBuilder();
			builder.append( triggerScript.remove(0));

			for( String s : triggerScript) {
				builder.append( "\n\n");
				builder.append( s);
			}

			script = builder.toString();
		}
		
		return script;
	}
	
	public String getTriggerSql() {
		
		String script = "";
		List<String> triggerScript = new ArrayList<String>();
		
		for(Trigger trigger : getTriggers()) {
			
			triggerScript.add(trigger.getCreateSql(getName()));
		}
		
		if(!triggerScript.isEmpty()) {
			StringBuilder builder = new StringBuilder();
			builder.append( triggerScript.remove(0));

			for( String s : triggerScript) {
				builder.append( "\n\n");
				builder.append( s);
			}

			script = builder.toString();
		}
		
		return script;
	}
	
	public String getIndexSql() {
		
		String script = "";
		
		if(isIndex()) {
			
			List<String> indexScript = new ArrayList<String>();

			for(Index index : getIndexes()) {

				if(index.getiIndexType().equals(IndexTypes.INDEX)) {
					
					indexScript.add(index.getCreateSql(getName()));
				}
			}

			StringBuilder builder = new StringBuilder();
			builder.append( indexScript.remove(0));

			for( String s : indexScript) {
				builder.append( "\n");
				builder.append( s);
			}

			script = builder.toString();
		}
		
		return script;
	}
	
	public String getFkSql() {
		
		String script = "";
		
		if(isForeignKey()) {
			
			List<String> foreignScript = new ArrayList<String>();

			for(ForeignKey fk : getForeignKeys()) {

				foreignScript.add(fk.getCreateSql(getName()));
			}

			StringBuilder builder = new StringBuilder();
			builder.append( foreignScript.remove(0));

			for( String s : foreignScript) {
				builder.append( "\n");
				builder.append( s);
			}

			script = builder.toString();
		}
		
		return script;
	}
	
	public String getFkSqlDrop() {
		
		String script = "";
		
		if(isForeignKey()) {
			
			List<String> foreignScript = new ArrayList<String>();

			for(ForeignKey fk : getForeignKeys()) {

				foreignScript.add(fk.getDropSql(getName()));
			}

			StringBuilder builder = new StringBuilder();
			builder.append( foreignScript.remove(0));

			for( String s : foreignScript) {
				builder.append( "\n");
				builder.append( s);
			}

			script = builder.toString();
		}
		
		return script;
	}
}
