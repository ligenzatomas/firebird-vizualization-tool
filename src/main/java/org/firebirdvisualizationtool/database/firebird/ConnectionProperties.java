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

import java.io.Serializable;
import java.util.Properties;

/**
 *
 * @author Tomáš Ligenza
 */
public class ConnectionProperties extends Properties implements Serializable {
	
	private static final long serialVersionUID = 4128337032838746168L;

	private final String URL = "jdbc:firebirdsql";
	private final String DEFAULT_PORT = "3050";
	private final String DEFAULT_CHARSET = "UTF8";
	
	public ConnectionProperties(String name) {
		
		super();
		
		setName(name);
		setPort(DEFAULT_PORT);
		setEncoding(DEFAULT_CHARSET);
	}
	
	public ConnectionProperties(String name, String host, String file, String user, String port, String encoding, char[] password) {
		
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
		String s = getProperty("name");
		
		if(s == null)
			s = "";
		
		return s;
	}

	public final void setName(String name) {
		setProperty("name", name);
	}

	public String getHost() {
		String s = getProperty("host");
		
		if(s == null)
			s = "";
		
		return s;
	}

	public final void setHost(String host) {
		setProperty("host", host);
	}

	public String getFile() {
		String s = getProperty("file");
		
		if(s == null)
			s = "";
		
		return s;
	}

	public final void setFile(String file) {
		setProperty("file", file);
	}

	public String getUser() {
		String s = getProperty("user");
		
		if(s == null)
			s = "";
		
		return s;
	}

	public final void setUser(String user) {
		setProperty("user", user);
	}

	public String getPort() {
		String s = getProperty("port");
		
		if(s == null)
			s = "";
		
		return s;
	}

	public final void setPort(String port) {
		setProperty("port", port);
	}

	public String getEncoding() {
		String s = getProperty("encoding");
		
		if(s == null)
			s = "";
		
		return s;
	}

	public final void setEncoding(String encoding) {
		setProperty("encoding", encoding);
	}

	public char[] getPassword() {
		String password = getProperty("password");
		
		if(password != null)
			return password.toCharArray();
		
		return "".toCharArray();
	}

	public final void setPassword(char[] password) {
		setProperty("password", String.valueOf(password));
	}
}
