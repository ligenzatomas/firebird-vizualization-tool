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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.TreeSet;
import org.tinyuml.model.Column;
import org.tinyuml.model.DefaultNamedElementComparator;
import org.tinyuml.model.Domain;
import org.tinyuml.model.ForeignKey;
import org.tinyuml.model.Index;
import org.tinyuml.model.Procedure;
import org.tinyuml.model.Sequence;
import org.tinyuml.model.Table;
import org.tinyuml.model.Trigger;
import org.tinyuml.model.View;

/**
 *
 * @author Tomáš Ligenza
 */
public class DatabaseMetaData {
	
	private static String actualSqlScript = "";
	
	public static int getDatabaseNameMaxLenght() {
		
		int length = 32;
		
		try {
			
			if(DatabaseConnection.getConnection() != null && DatabaseConnection.isValid()) {
				
				length = DatabaseConnection.getConnection().getMetaData().getMaxColumnNameLength();
			}
		} catch (SQLException ex) {
			throw new RuntimeException(ex.getSQLState());
		}
		
		return length;
	}
	
	public static ColumnTypes getColumnDataType(int dataType, int dataSubtype) {
		
		switch(dataType) {
			
			case 7:
				
				if(dataSubtype == 0)
					return ColumnTypes.SMALLINT;
				if(dataSubtype == 1)
					return ColumnTypes.NUMERIC;
				if(dataSubtype == 2)
					return ColumnTypes.DECIMAL;
			break;
			case 8:
				
				if(dataSubtype == 0)
					return ColumnTypes.INTEGER;
				if(dataSubtype == 1)
					return ColumnTypes.NUMERIC;
				if(dataSubtype == 2)
					return ColumnTypes.DECIMAL;
			break;
			case 10:
				return ColumnTypes.FLOAT;
			case 12:
				return ColumnTypes.DATE;
			case 13:
				return ColumnTypes.TIME;
			case 14:
				return ColumnTypes.CHAR;
			case 16:
				
				if(dataSubtype == 0)
					return ColumnTypes.BIGINT;
				if(dataSubtype == 1)
					return ColumnTypes.NUMERIC;
				if(dataSubtype == 2)
					return ColumnTypes.DECIMAL;
			break;
			case 27:
				return ColumnTypes.DOUBLE_PRECISION;
			case 35:
				return ColumnTypes.TIMESTAMP;
			case 37:
				return ColumnTypes.VARCHAR;
			case 261:
				return ColumnTypes.BLOB;
			default:
				return ColumnTypes.ARRAY;
		}
		
		return null;
	}
	
	public static IndexTypes getIndexType(String index) {
		
		if(index != null && index.equals("PRIMARY KEY"))
			return IndexTypes.PRIMARY;
		else if(index != null && index.equals("UNIQUE"))
			return IndexTypes.UNIQUE;
		else
			return IndexTypes.INDEX;
	}
	
	public static IndexOrders getIndexOrder(int order) {
		
		if(order == 1)
			return IndexOrders.DESC;
		else
			return IndexOrders.ASC;
	}
	
	public static ForeignKeyActions getForeignKeyAction(String action) {
		
		if(action.equals("NO ACTION"))
			return ForeignKeyActions.NO_ACTION;
		else if (action.equals("SET DEFAULT"))
			return ForeignKeyActions.SET_DEFAULT;
		else if (action.equals("SET NULL"))
			return ForeignKeyActions.SET_NULL;
		else if (action.equals("CASCADE"))
			return ForeignKeyActions.CASCADE;
		else
			return ForeignKeyActions.RESTRICT;
	}
	
	public static Trigger setTriggerType(Trigger trigger, int type) {
		
		switch(type) {
			
			case 1:
				trigger.setFiringTime(TriggerFiringTime.BEFORE);
				trigger.setInsert(true);
			break;
				
			case 3:
				trigger.setFiringTime(TriggerFiringTime.BEFORE);
				trigger.setUpdate(true);
			break;
				
			case 5:
				trigger.setFiringTime(TriggerFiringTime.BEFORE);
				trigger.setDelete(true);
			break;
				
			case 17:
				trigger.setFiringTime(TriggerFiringTime.BEFORE);
				trigger.setInsert(true);
				trigger.setUpdate(true);
			break;
				
			case 25:
				trigger.setFiringTime(TriggerFiringTime.BEFORE);
				trigger.setInsert(true);
				trigger.setDelete(true);
			break;
				
			case 27:
				trigger.setFiringTime(TriggerFiringTime.BEFORE);
				trigger.setUpdate(true);
				trigger.setDelete(true);
			break;
				
			case 113:
				trigger.setFiringTime(TriggerFiringTime.BEFORE);
				trigger.setInsert(true);
				trigger.setUpdate(true);
				trigger.setDelete(true);
			break;
				
			case 2:
				trigger.setFiringTime(TriggerFiringTime.AFTER);
				trigger.setInsert(true);
			break;
				
			case 4:
				trigger.setFiringTime(TriggerFiringTime.AFTER);
				trigger.setUpdate(true);
			break;
				
			case 6:
				trigger.setFiringTime(TriggerFiringTime.AFTER);
				trigger.setDelete(true);
			break;
				
			case 18:
				trigger.setFiringTime(TriggerFiringTime.AFTER);
				trigger.setInsert(true);
				trigger.setUpdate(true);
			break;
				
			case 26:
				trigger.setFiringTime(TriggerFiringTime.AFTER);
				trigger.setInsert(true);
				trigger.setDelete(true);
			break;
				
			case 28:
				trigger.setFiringTime(TriggerFiringTime.AFTER);
				trigger.setUpdate(true);
				trigger.setDelete(true);
			break;
				
			case 114:
				trigger.setFiringTime(TriggerFiringTime.AFTER);
				trigger.setInsert(true);
				trigger.setUpdate(true);
				trigger.setDelete(true);
			break;
		}
		
		return trigger;
	}
	
	private static void setSelectDomainsScript() {
		
		actualSqlScript = 
			"SELECT " +
			" TRIM(F.RDB$FIELD_NAME) RDB$FIELD_NAME," +
			" TRIM(F.RDB$FIELD_TYPE) RDB$FIELD_TYPE," +
			" F.RDB$NULL_FLAG," +
			" TRIM(CH.RDB$CHARACTER_SET_NAME) FIELD_CHARSET," +
			" TRIM(DCO.RDB$COLLATION_NAME) FIELD_COLLATION," +
			" TRIM(F.RDB$DEFAULT_SOURCE) FIELD_DEFAULT," +
			" TRIM(F.RDB$VALIDATION_SOURCE) FIELD_CHECK," +
			" F.RDB$FIELD_SUB_TYPE," +
			" CASE F.RDB$FIELD_TYPE" +
			"    WHEN 7 THEN" +
			"      CASE F.RDB$FIELD_SUB_TYPE" +
			"        WHEN 1 THEN F.RDB$FIELD_PRECISION" +
			"        WHEN 2 THEN F.RDB$FIELD_PRECISION" +
			"      END" +
			"    WHEN 8 THEN" +
			"      CASE F.RDB$FIELD_SUB_TYPE" +
			"        WHEN 1 THEN F.RDB$FIELD_PRECISION" +
			"        WHEN 2 THEN F.RDB$FIELD_PRECISION" +
			"      END" +
			"    WHEN 14 THEN (TRUNC(F.RDB$FIELD_LENGTH / CH.RDB$BYTES_PER_CHARACTER))" +
			"    WHEN 16 THEN" +
			"      CASE F.RDB$FIELD_SUB_TYPE" +
			"        WHEN 1 THEN F.RDB$FIELD_PRECISION" +
			"        WHEN 2 THEN F.RDB$FIELD_PRECISION" +
			"      END" +
			"    WHEN 37 THEN (TRUNC(F.RDB$FIELD_LENGTH / CH.RDB$BYTES_PER_CHARACTER))" +
			"  END FIELD_SIZE," +
			" CASE F.RDB$FIELD_TYPE" +
			"    WHEN 7 THEN" +
			"      CASE F.RDB$FIELD_SUB_TYPE" +
			"        WHEN 1 THEN (-F.RDB$FIELD_SCALE)" +
			"        WHEN 2 THEN (-F.RDB$FIELD_SCALE)" +
			"      END" +
			"    WHEN 8 THEN" +
			"      CASE F.RDB$FIELD_SUB_TYPE" +
			"        WHEN 1 THEN (-F.RDB$FIELD_SCALE)" +
			"        WHEN 2 THEN (-F.RDB$FIELD_SCALE)" +
			"      END" +
			"    WHEN 16 THEN" +
			"      CASE F.RDB$FIELD_SUB_TYPE" +
			"        WHEN 1 THEN (-F.RDB$FIELD_SCALE)" +
			"        WHEN 2 THEN (-F.RDB$FIELD_SCALE)" +
			"      END" +
			"  END FIELD_SCALE " +
			"FROM RDB$FIELDS F " +
			"LEFT JOIN RDB$CHARACTER_SETS CH ON (CH.RDB$CHARACTER_SET_ID = F.RDB$CHARACTER_SET_ID) " +
			"LEFT JOIN RDB$COLLATIONS DCO ON " +
			"((DCO.RDB$COLLATION_ID = F.RDB$COLLATION_ID) AND (DCO.RDB$CHARACTER_SET_ID = F.RDB$CHARACTER_SET_ID)) " +
			"WHERE (F.RDB$FIELD_NAME NOT STARTING WITH 'RDB$') AND (COALESCE(F.RDB$SYSTEM_FLAG, 0) = 0)";
	}
	
	private static void setSelectSequencesScript() {
		
		actualSqlScript = 
			"SELECT TRIM(RDB$GENERATOR_NAME) RDB$GENERATOR_NAME, RDB$GENERATOR_ID, RDB$SYSTEM_FLAG, RDB$DESCRIPTION " +
			"FROM RDB$GENERATORS " +
			"WHERE (COALESCE(RDB$SYSTEM_FLAG, 0) = 0)";
	}
	
	private static void setSelectTablesScript() {
		
		actualSqlScript = 
			"SELECT DISTINCT " +
				"TRIM(RF.rdb$relation_name) TABLE_NAME " +
			"FROM rdb$fields F " +
			"JOIN rdb$relation_fields RF on RF.rdb$field_source = F.rdb$field_name " +
			"WHERE COALESCE(F.RDB$SYSTEM_FLAG, 0) = 0 AND RF.RDB$VIEW_CONTEXT IS NULL";
	}
	
	private static void setSelectColumnScript(String tableName) {
		
		actualSqlScript = 
			"SELECT " +
			"IIF(F.RDB$FIELD_NAME NOT STARTING WITH 'RDB$', 1, 2) as is_domain, " +
			"TRIM(RF.rdb$relation_name) TABLE_NAME, " +
			"TRIM(F.RDB$FIELD_NAME) FIELD_NAME, " +
			"TRIM(RF.rdb$field_name) REAL_NAME, " +
			"F.RDB$FIELD_TYPE, " +
			"RF.RDB$NULL_FLAG, " +
			"TRIM(CH.RDB$CHARACTER_SET_NAME) FIELD_CHARSET, " +
			"TRIM(DCO.RDB$COLLATION_NAME) FIELD_COLLATION, " +
			"TRIM(F.RDB$DEFAULT_SOURCE) FIELD_DEFAULT, " +
			"TRIM(F.RDB$VALIDATION_SOURCE) FIELD_CHECK, " +
			"F.RDB$FIELD_SUB_TYPE, " +
			"CASE F.RDB$FIELD_TYPE " +
			"   WHEN 7 THEN " +
			"     CASE F.RDB$FIELD_SUB_TYPE " +
			"       WHEN 1 THEN F.RDB$FIELD_PRECISION " +
			"       WHEN 2 THEN F.RDB$FIELD_PRECISION " +
			"     END " +
			"   WHEN 8 THEN " +
			"     CASE F.RDB$FIELD_SUB_TYPE " +
			"       WHEN 1 THEN F.RDB$FIELD_PRECISION " +
			"       WHEN 2 THEN F.RDB$FIELD_PRECISION " +
			"     END " +
			"   WHEN 14 THEN (TRUNC(F.RDB$FIELD_LENGTH / CH.RDB$BYTES_PER_CHARACTER)) " +
			"   WHEN 16 THEN " +
			"     CASE F.RDB$FIELD_SUB_TYPE " +
			"       WHEN 1 THEN F.RDB$FIELD_PRECISION " +
			"       WHEN 2 THEN F.RDB$FIELD_PRECISION " +
			"     END " +
			"   WHEN 37 THEN (TRUNC(F.RDB$FIELD_LENGTH / CH.RDB$BYTES_PER_CHARACTER)) " +
			" END FIELD_SIZE, " +
			"CASE F.RDB$FIELD_TYPE " +
			"   WHEN 7 THEN " +
			"     CASE F.RDB$FIELD_SUB_TYPE " +
			"       WHEN 1 THEN (-F.RDB$FIELD_SCALE) " +
			"       WHEN 2 THEN (-F.RDB$FIELD_SCALE) " +
			"     END " +
			"   WHEN 8 THEN " +
			"     CASE F.RDB$FIELD_SUB_TYPE " +
			"       WHEN 1 THEN (-F.RDB$FIELD_SCALE) " +
			"       WHEN 2 THEN (-F.RDB$FIELD_SCALE) " +
			"     END" +
			"   WHEN 16 THEN " +
			"     CASE F.RDB$FIELD_SUB_TYPE " +
			"       WHEN 1 THEN (-F.RDB$FIELD_SCALE) " +
			"       WHEN 2 THEN (-F.RDB$FIELD_SCALE) " +
			"     END " +
			" END FIELD_SCALE " +
			"FROM rdb$fields F " +
			"JOIN rdb$relation_fields RF on RF.rdb$field_source = F.rdb$field_name " +
			"LEFT JOIN RDB$CHARACTER_SETS CH ON (CH.RDB$CHARACTER_SET_ID = F.RDB$CHARACTER_SET_ID) " +
			"LEFT JOIN RDB$COLLATIONS DCO ON " +
			"    ((DCO.RDB$COLLATION_ID = F.RDB$COLLATION_ID) AND (DCO.RDB$CHARACTER_SET_ID = F.RDB$CHARACTER_SET_ID)) " +
			"WHERE COALESCE(F.RDB$SYSTEM_FLAG, 0) = 0 AND RF.RDB$VIEW_CONTEXT IS NULL AND RF.rdb$relation_name = '" + tableName + "'";
	}
	
	private static void setSelectIndexesScript(String tableName) {
		
		actualSqlScript = 
			"SELECT S.RDB$INDEX_NAME INDEX_NAME, RC.*, I.*, S.* " +
			"FROM RDB$INDICES I " +
			"LEFT JOIN RDB$INDEX_SEGMENTS S ON I.RDB$INDEX_NAME = S.RDB$INDEX_NAME " +
			"LEFT JOIN RDB$RELATION_CONSTRAINTS RC ON RC.RDB$INDEX_NAME = S.RDB$INDEX_NAME " +
			"WHERE RDB$FOREIGN_KEY IS NULL AND I.RDB$SYSTEM_FLAG = 0 AND I.RDB$RELATION_NAME = '" + tableName + "'";
	}
	
	private static void setSelectForeignKeysScript(String tableName) {
		
		actualSqlScript = 
			"SELECT rc.RDB$RELATION_NAME FK_TABLE, " +
			" flds_fk.rdb$field_name FK_FIELD, " +
			" rc2.rdb$relation_name PK_TABLE, " +
			" flds_pk.rdb$field_name PK_FIELD, " +
			" rc.RDB$CONSTRAINT_NAME CONSTRAINT_NAME, " +
			" rfc.RDB$CONST_NAME_UQ TO_INDEX, " +
			" flds_fk.rdb$field_position FIELD_POSITION, " +
			" rfc.RDB$UPDATE_RULE UPDATE_RULE, " +
			" rfc.RDB$DELETE_RULE DELETE_RULE " +
			"FROM RDB$RELATION_CONSTRAINTS rc " +
			"LEFT JOIN rdb$ref_constraints rfc ON (rc.RDB$CONSTRAINT_NAME = rfc.RDB$CONSTRAINT_NAME) " +
			"LEFT JOIN rdb$index_segments flds_fk ON (flds_fk.rdb$index_name = rc.rdb$index_name) " +
			"LEFT JOIN rdb$relation_constraints rc2 ON (rc2.rdb$constraint_name = rfc.rdb$const_name_uq) " +
			"LEFT JOIN rdb$index_segments flds_pk ON " +
				"((flds_pk.rdb$index_name = rc2.rdb$index_name) and (flds_fk.rdb$field_position = flds_pk.rdb$field_position)) " +
			"WHERE rc.RDB$CONSTRAINT_TYPE = 'FOREIGN KEY' " +
			"AND rc.RDB$RELATION_NAME = '" + tableName + "' " +
			"ORDER BY rc.RDB$CONSTRAINT_NAME, flds_fk.rdb$field_position";
	}
	
	private static void setSelectTriggersScript(String tableName) {
		
		actualSqlScript = 
			"SELECT TRIM(RDB$TRIGGER_NAME) TRIGGER_NAME, " +
			"RDB$TRIGGER_SEQUENCE, RDB$TRIGGER_TYPE, RDB$TRIGGER_SOURCE, " +
			"RDB$TRIGGER_BLR, RDB$DESCRIPTION, RDB$TRIGGER_INACTIVE, " +
			"RDB$SYSTEM_FLAG, RDB$FLAGS, RDB$DB_KEY " +
			"FROM RDB$TRIGGERS " +
			"WHERE (RDB$SYSTEM_FLAG IS NULL OR RDB$SYSTEM_FLAG=0) AND TRIM(RDB$RELATION_NAME) = '" + tableName + "'";
	}
	
	private static void setSelectProceduresScript() {
		
		actualSqlScript = 
			"SELECT * FROM RDB$PROCEDURES";
	}
	
	private static void setSelectViewsScript() {
		
		actualSqlScript = 
			"SELECT DISTINCT TRIM(V.RDB$VIEW_NAME) RDB$VIEW_NAME, R.RDB$VIEW_SOURCE " +
			"FROM RDB$VIEW_RELATIONS V " +
			"LEFT JOIN RDB$RELATIONS R ON V.RDB$VIEW_NAME=R.RDB$RELATION_NAME";
	}
	
	public static TreeSet<Domain> getDatabaseDomains(Connection conn) throws SQLException {
		
		TreeSet<Domain> domains = new TreeSet<Domain>(new DefaultNamedElementComparator());
		
		try {
			
			Statement statement = conn.createStatement();
			setSelectDomainsScript();
			
			ResultSet results = statement.executeQuery(actualSqlScript);
			
			while(results.next()) {
				
				Domain d = Domain.getPrototype().create(
						results.getString("RDB$FIELD_NAME").trim()
						, getColumnDataType(results.getInt("RDB$FIELD_TYPE"), results.getInt("RDB$FIELD_SUB_TYPE"))
						, results.getBoolean("RDB$NULL_FLAG"));
				
				d.setCharacterSet(results.getString("FIELD_CHARSET"));
				d.setCollate(results.getString("FIELD_COLLATION"));
				d.setDefaultValue(results.getString("FIELD_DEFAULT"));
				d.setCheck(results.getString("FIELD_CHECK"));
				d.setScale(results.getInt("FIELD_SCALE"));
				d.setSize(results.getInt("FIELD_SIZE"));
				
				d.repairInput();
				
				if(d.checkValid())
					domains.add((Domain) d.clone());
			}
			
			statement.close();
			
		} catch (SQLException ex) {
			
			String message = ex.getMessage() + " IN " + actualSqlScript;
			
			throw new SQLException(message);
		}
		
		return domains;
	}
	
	public static TreeSet<Sequence> getDatabaseSequences(Connection conn) throws SQLException {
		
		TreeSet<Sequence> sequences = new TreeSet<Sequence>(new DefaultNamedElementComparator());
		
		try {
			
			Statement statement = conn.createStatement();
			setSelectSequencesScript();
			
			ResultSet results = statement.executeQuery(actualSqlScript);
			
			while(results.next()) {
				
				Sequence s = Sequence.getPrototype().create(
						results.getString("RDB$GENERATOR_NAME").trim());
				
				s.setInitValue(0);
				
				if(s.checkValid())
					sequences.add((Sequence) s.clone());
			}
			
			statement.close();
			
		} catch (SQLException ex) {
			
			String message = ex.getMessage() + " IN " + actualSqlScript;
			
			throw new SQLException(message);
		}
		
		return sequences;
	}
	
	public static TreeSet<Table> getDatabaseTables(Connection conn) throws SQLException {
		
		TreeSet<Table> tables = new TreeSet<Table>(new DefaultNamedElementComparator());
		
		try {
			
			Statement statement = conn.createStatement();
			setSelectTablesScript();
			
			ResultSet results = statement.executeQuery(actualSqlScript);
			
			while(results.next()) {
				
				Table t = Table.getPrototype().create(results.getString("TABLE_NAME").trim());
				
				tables.add((Table) t.clone());
			}
			
			Statement s = null;
			
			for(Table t : tables) {
				
				s = conn.createStatement();
				setSelectColumnScript(t.getName());
				
				ResultSet r = s.executeQuery(actualSqlScript);
				
				while(r.next()) {
					
					Column c = Column.getPrototype().create(
						r.getString("REAL_NAME").trim()
						, getColumnDataType(r.getInt("RDB$FIELD_TYPE"), r.getInt("RDB$FIELD_SUB_TYPE"))
						, r.getBoolean("RDB$NULL_FLAG"));
					
					c.setCharacterSet(r.getString("FIELD_CHARSET"));
					c.setCollate(r.getString("FIELD_COLLATION"));
					c.setDefaultValue(r.getString("FIELD_DEFAULT"));
					c.setCheck(r.getString("FIELD_CHECK"));
					c.setScale(r.getInt("FIELD_SCALE"));
					c.setSize(r.getInt("FIELD_SIZE"));
					
					c.repairInput();
					
					if(c.checkValid())
						t.getCols().add((Column) c.clone());
				}
				
				s = conn.createStatement();
				setSelectIndexesScript(t.getName());
				
				r = s.executeQuery(actualSqlScript);
				
				Index i = null;
				
				while(r.next()) {
					
					if(i != null && !i.getName().equals(r.getString("INDEX_NAME").trim()) && i.checkValid()) {
						t.getIndexes().add((Index) i.clone());
						i = null;
					}
					
					if(i == null) {
						i = Index.getPrototype().create(
								r.getString("INDEX_NAME").trim()
								, getIndexType(r.getString("RDB$CONSTRAINT_TYPE")));
						
						i.setiIndexOrder(getIndexOrder(r.getInt("RDB$INDEX_TYPE")));
					}
					
					i.addCol(Boolean.TRUE, r.getString("RDB$FIELD_NAME").trim(), IndexOrders.ASC);
				}
				
				if(i != null && !t.getIndexes().contains(i) && i.checkValid())
					t.getIndexes().add((Index) i.clone());
				
				s = conn.createStatement();
				setSelectForeignKeysScript(t.getName());
				
				r = s.executeQuery(actualSqlScript);
				
				ForeignKey k = null;
				
				while(r.next()) {
					
					if(k != null && !k.getName().equals(r.getString("CONSTRAINT_NAME").trim()) && k.checkValid()) {
						t.getForeignKeys().add((ForeignKey) k.clone());
						k = null;
					}
					
					if(k == null) {
						k = ForeignKey.getPrototype().create(
							r.getString("CONSTRAINT_NAME").trim()
							, r.getString("PK_TABLE").trim()
							, getForeignKeyAction(r.getString("UPDATE_RULE").trim())
							, getForeignKeyAction(r.getString("DELETE_RULE").trim()));
					}
					
					k.addKeyCol(Boolean.TRUE, r.getString("FK_FIELD").trim());
					k.addReferencedCol(Boolean.TRUE, r.getString("PK_FIELD").trim());
				}
				
				if(k != null && !t.getForeignKeys().contains(k) && k.checkValid())
					t.getForeignKeys().add((ForeignKey) k.clone());
				
				s = conn.createStatement();
				setSelectTriggersScript(t.getName());
				
				r = s.executeQuery(actualSqlScript);
				
				while(r.next()) {
					
					Trigger tr = Trigger.getPrototype().create(r.getString("TRIGGER_NAME").trim());
					
					setTriggerType(tr, r.getInt("RDB$TRIGGER_TYPE"));
					tr.setActive(!r.getBoolean("RDB$TRIGGER_INACTIVE"));
					tr.setCommand(r.getString("RDB$TRIGGER_SOURCE").trim());
					
					if(tr.checkValid())
						t.getTriggers().add((Trigger) tr.clone());
				}
			}
			
			if(s != null)
				s.close();
			statement.close();
			
		} catch (SQLException ex) {
			
			String message = ex.getMessage() + " IN " + actualSqlScript;
			
			throw new SQLException(message);
		}
		
		return tables;
	}
	
	public static TreeSet<Procedure> getDatabaseProcedures(Connection conn) throws SQLException {
		
		TreeSet<Procedure> procedures = new TreeSet<Procedure>(new DefaultNamedElementComparator());
		
		try {
			
			Statement statement = conn.createStatement();
			setSelectProceduresScript();
			
			ResultSet results = statement.executeQuery(actualSqlScript);
			
			while(results.next()) {
				
				Procedure p = Procedure.getPrototype().create(
						results.getString("RDB$PROCEDURE_NAME").trim());
				
				p.setSource(results.getString("RDB$PROCEDURE_SOURCE").trim());
				
				procedures.add((Procedure) p.clone());
			}
			
			statement.close();
			
		} catch (SQLException ex) {
			
			String message = ex.getMessage() + " IN " + actualSqlScript;
			
			throw new SQLException(message);
		}
		
		return procedures;
	}
	
	public static TreeSet<View> getDatabaseViews(Connection conn) throws SQLException {
		
		TreeSet<View> views = new TreeSet<View>(new DefaultNamedElementComparator());
		
		try {
			
			Statement statement = conn.createStatement();
			setSelectViewsScript();
			
			ResultSet results = statement.executeQuery(actualSqlScript);
			
			while(results.next()) {
				
				View v = View.getPrototype().create(
						results.getString("RDB$VIEW_NAME").trim());
				
				v.setSource(results.getString("RDB$VIEW_SOURCE").trim());
				
				views.add((View) v.clone());
			}
			
			statement.close();
			
		} catch (SQLException ex) {
			
			String message = ex.getMessage() + " IN " + actualSqlScript;
			
			throw new SQLException(message);
		}
		
		return views;
	}
}
