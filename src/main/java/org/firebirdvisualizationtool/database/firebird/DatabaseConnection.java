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
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 *
 * @author Tomáš Ligenza
 */
public class DatabaseConnection {
	
	private static Connection connection;
	
	private DatabaseConnection() {}
	
	public static void initConnection(String url, Properties properties) 
		throws SQLException, ClassNotFoundException{
		
		if(connection == null) {
			
			Class.forName("org.firebirdsql.jdbc.FBDriver");
		}
		
		connection = DriverManager.getConnection(url, properties);
	}
	
	public static boolean testConnection(String url, Properties properties) 
		throws ClassNotFoundException, SQLException {
		
		if(connection == null)
			Class.forName("org.firebirdsql.jdbc.FBDriver");
		
		Connection conn = DriverManager.getConnection(url, properties);
		
		return conn.isValid(0);
	}
	
	public static Connection getConnection() {
		
		return connection;
	}
	
	public static boolean isValid() throws SQLException {
		
		return connection.isValid(0);
	}
}
