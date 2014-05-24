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

import org.tinyuml.model.Column;

/**
 *
 * @author Tomáš Ligenza
 */
public class DatabaseTableCol {
	
	public static String getSqlString(Column column) {
		
		String dataType		= "";
		String character	= "";
		String collate		= "";
		String defaultValue = "";
		String check		= "";
		String notNull		= "";
		
		String sql = 
			column.getName();
		
		if(column.getDomain() == null) {
			switch(column.getColType()) {

				case CHAR: case VARCHAR:
					dataType = " " + column.getColType().name() + "(" + column.getSize() + ")";

					if(column.getCharacterSet() != null && !column.getCharacterSet().isEmpty())
						character = " CHARACTER SET " + column.getCharacterSet();
					if(column.getCollate() != null && !column.getCollate().isEmpty())
						collate = " COLLATE " + column.getCollate();
				break;

				case INTEGER: case SMALLINT: case BIGINT: 
				case FLOAT: case DOUBLE_PRECISION: 
				case DATE: case TIME: case TIMESTAMP:
				case ARRAY: case BLOB:
					dataType = " " + column.getColType().name();
				break;

				case NUMERIC: case DECIMAL:
					dataType = " " + column.getColType().name() + "(" + column.getSize() + "," + column.getScale() + ")";
				break;
			}

			if(!column.getDefaultValue().isEmpty())
				defaultValue = " DEFAULT " + column.getDefaultValue();

			if(!column.getCheck().isEmpty())
				check = " CHECK (" + column.getCheck() + ")";

			if(column.getNn())
				notNull = " NOT NULL";

			sql = sql.concat(dataType + character + defaultValue + check + notNull + collate);
		} else {
			sql = sql.concat(" " + column.getDomain().getName());
		}
		
		return sql;
	}
}
