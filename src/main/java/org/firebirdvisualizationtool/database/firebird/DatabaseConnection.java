/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.firebirdvisualizationtool.database.firebird;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 *
 * @author cml
 */
public class DatabaseConnection {
	
	private static Connection connection;
	
	private DatabaseConnection() {}
	
	public static void initConnection(String url, Properties properties) 
		throws SQLException, ClassNotFoundException{
		
		if(connection == null) {
			
			Class.forName("org.firebirdsql.jdbc.FBDriver");
			connection = DriverManager.getConnection(url, properties);
		}
	}
	
	public static Connection getConnection() {
		
		return connection;
	}
	
	public static boolean isValid() throws SQLException {
		
		return connection.isValid(0);
	}
}
