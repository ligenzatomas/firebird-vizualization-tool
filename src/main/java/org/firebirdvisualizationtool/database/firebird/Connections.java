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

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import org.tinyuml.util.Crypter;

/**
 *
 * @author Tomáš Ligenza
 */
public class Connections {
	
	private LinkedList<ConnectionProperties> connections = new LinkedList<ConnectionProperties>();
	private final String connectionFileName = "connections.dat";
	
	private Connections() {
		
		File file = new File(connectionFileName);
		
		if(file.exists()) {
			
			try {
				connections = (LinkedList<ConnectionProperties>) Crypter.decryptObjectFromFile(connectionFileName);
			} catch (IOException ex) {
				Logger.getLogger(Connections.class.getName()).log(Level.SEVERE, null, ex);
			} catch (ClassNotFoundException ex) {
				Logger.getLogger(Connections.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}

	public LinkedList<ConnectionProperties> getConnections() {
		return connections;
	}

	public void setConnections(LinkedList<ConnectionProperties> connections) {
		this.connections = connections;
	}
	
	public boolean saveConnections() {
		
		File file = new File(connectionFileName);
		
		try {
			file.createNewFile();
		} catch (IOException ex) {
			Logger.getLogger(Connections.class.getName()).log(Level.SEVERE, null, ex);
		}

		if(file.canWrite()) {

			Crypter.encryptObjectToFile(connections, connectionFileName);

			return true;
		}
		
		return false;
	}
	
	public DefaultComboBoxModel<ConnectionProperties> getConnectionsComboBoxModel() {
		
		DefaultComboBoxModel<ConnectionProperties> model = new DefaultComboBoxModel<ConnectionProperties>();
		
		for(ConnectionProperties cp : connections) {
			
			model.addElement(cp);
		}
		
		return model;
	}
	
	public static Connections getInstance() {
		return ConnectionsHolder.INSTANCE;
	}
	
	private static class ConnectionsHolder {

		private static final Connections INSTANCE = new Connections();
	}
}
