/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.firebirdvisualizationtool.database.firebird;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Properties;
import org.tinyuml.database.DatabaseEncoding;
import org.tinyuml.util.Crypter;

/**
 *
 * @author cml
 */
public class ConnectionProperties extends Properties implements Serializable {
	
	private static final long serialVersionUID = 4128337032838746168L;

	private final String URL = "jdbc:firebirdsql";
	
	public ConnectionProperties(String name, String host, String file, String user, String port, DatabaseEncoding encoding, char[] password) {
		
		super();
		
		setName(name);
		setHost(host);
		setFile(file);
		setUser(user);
		setPort(port);
		setEncoding(encoding);
		
		setPassword(password);
	}
	
	public Boolean isValid() {
		
		return !getName().isEmpty() 
			&& !getHost().isEmpty() 
			&& !getFile().isEmpty() 
			&& !getUser().isEmpty() 
			&& getPassword().length > 0;
	}
	
	public String getConnectionUrl() {
		
		return URL + ":" + getHost() + "/" + getPort() + ":" + getFile();
	}

	public String getName() {
		return getProperty("name");
	}

	public final void setName(String name) {
		setProperty("name", name);
	}

	public String getHost() {
		return getProperty("host");
	}

	public final void setHost(String host) {
		setProperty("host", host);
	}

	public String getFile() {
		return getProperty("file");
	}

	public final void setFile(String file) {
		setProperty("file", file);
	}

	public String getUser() {
		return getProperty("user");
	}

	public final void setUser(String user) {
		setProperty("user", user);
	}

	public String getPort() {
		return getProperty("port");
	}

	public final void setPort(String port) {
		setProperty("port", port);
	}

	public DatabaseEncoding getEncoding() {
		return DatabaseEncoding.valueOf(getProperty("encoding"));
	}

	public final void setEncoding(DatabaseEncoding encoding) {
		setProperty("encoding", encoding.toString());
	}

	public char[] getPassword() {
		return getProperty("password").toCharArray();
	}

	public final void setPassword(char[] password) {
		setProperty("password", String.valueOf(password));
	}
}
