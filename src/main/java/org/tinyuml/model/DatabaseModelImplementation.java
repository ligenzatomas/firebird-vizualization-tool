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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.TreeSet;
import javax.swing.DefaultComboBoxModel;

/**
 *
 * @author Tomáš Ligenza
 */
public class DatabaseModelImplementation implements DatabaseModel {
	
	private static final long serialVersionUID = 2083631602016544488L;
	
	private TreeSet<Sequence>	sequences			= new TreeSet<Sequence>(new DefaultNamedElementComparator());
	private TreeSet<Domain>		domains				= new TreeSet<Domain>(new DefaultNamedElementComparator());
	private TreeSet<Table>		tables				= new TreeSet<Table>(new DefaultNamedElementComparator());
	private TreeSet<Procedure>	procedures			= new TreeSet<Procedure>(new DefaultNamedElementComparator());
	private TreeSet<View>		views				= new TreeSet<View>(new DefaultNamedElementComparator());
	
	public DatabaseModelImplementation() {}
	
	private void writeObject(ObjectOutputStream stream) throws IOException {
		
		stream.writeObject(sequences);
		stream.writeObject(domains);
		stream.writeObject(tables);
		stream.writeObject(procedures);
		stream.writeObject(views);
	}

	private void readObject(ObjectInputStream stream)
		throws IOException, ClassNotFoundException {
		
		sequences	= (TreeSet<Sequence>)	stream.readObject();
		domains		= (TreeSet<Domain>)		stream.readObject();
		tables		= (TreeSet<Table>)		stream.readObject();
		procedures	= (TreeSet<Procedure>)	stream.readObject();
		views		= (TreeSet<View>)		stream.readObject();
	}
	
	/**
	 * Returning model with all existing tables names
	 * 
	 * @return DefaultComboBoxModel
	 */
	@Override
	public DefaultComboBoxModel getTablesComboBoxModel() {
		
		DefaultComboBoxModel model = new DefaultComboBoxModel();
		
		Iterator<Table> iterator = tables.iterator();
		
		while(iterator.hasNext()) {
			
			model.addElement(iterator.next().getName());
		}
		
		return model;
	}
	
	/**
	 * Returning model with all existing domains
	 * 
	 * @return DefaultComboBoxModel<Domain>
	 */
	@Override
	public DefaultComboBoxModel<Domain> getDomainsComboBoxModel() {
		
		DefaultComboBoxModel model = new DefaultComboBoxModel();
		
		// adding NULL domain (to choose no domain)
		model.addElement(Domain.getPrototype().create(Domain.NULL_DOMAIN, null, false));
		
		Iterator<Domain> iterator = domains.iterator();
		
		while(iterator.hasNext()) {
			
			model.addElement(iterator.next());
		}
		
		return model;
	}
	
	@Override
	public void addSequence(Sequence aSequence) {
		
		sequences.add(aSequence);
	}

	@Override
	public boolean containsSequence(Sequence aSequence) {
		
		for(Sequence seq : sequences.descendingSet()) {
			
			if(seq.getName().equals(aSequence.getName()))
				return true;
		}
		
		return false;
	}

	@Override
	public TreeSet<Sequence> getSequences() {
		
		return sequences;
	}
	
	@Override
	public void setSequences(TreeSet<Sequence> sequences) {
		
		this.sequences = sequences;
	}
	
	@Override
	public void addView(View aView) {
		
		views.add(aView);
	}

	@Override
	public boolean containsView(View aView) {
		
		for(View view : views.descendingSet()) {
			
			if(view.getName().equals(aView.getName()))
				return true;
		}
		
		return false;
	}
	
	@Override
	public boolean containsViewByName(String name) {
		
		for(View view : views.descendingSet()) {
			
			if(view.getName().equals(name))
				return true;
		}
		
		return false;
	}

	@Override
	public TreeSet<View> getViews() {
		
		return views;
	}
	
	@Override
	public void setViews(TreeSet<View> views) {
		
		this.views = views;
	}
	
	@Override
	public void addProcedure(Procedure aProcedure) {
		
		procedures.add(aProcedure);
	}

	@Override
	public boolean containsProcedure(Procedure aProcedure) {
		
		for(Procedure pro : procedures.descendingSet()) {
			
			if(pro.getName().equals(aProcedure.getName()))
				return true;
		}
		
		return false;
	}
	
	@Override
	public boolean containsProcedureByName(String name) {
		
		for(Procedure pro : procedures.descendingSet()) {
			
			if(pro.getName().equals(name))
				return true;
		}
		
		return false;
	}

	@Override
	public TreeSet<Procedure> getProcedures() {
		
		return procedures;
	}
	
	@Override
	public void setProcedures(TreeSet<Procedure> procedures) {
		
		this.procedures = procedures;
	}
	
	@Override
	public void addDomain(Domain aDomain) {
		
		domains.add(aDomain);
	}

	@Override
	public boolean containsDomain(Domain aDomain) {
		
		for(Domain dom : domains.descendingSet()) {
			
			if(dom.getName().equals(aDomain.getName()))
				return true;
		}
		
		return false;
	}

	@Override
	public TreeSet<Domain> getDomains() {
		
		return domains;
	}
	
	@Override
	public void setDomains(TreeSet<Domain> aDomains) {
		
		this.domains = aDomains;
	}
	
	@Override
	public void addTable(Table aTable) {
		
		tables.add(aTable);
	}

	@Override
	public boolean containsTable(Table aTable) {
		
		for(Table tab : tables.descendingSet()) {
			
			if(tab.getName().equals(aTable.getName()))
				return true;
		}
		
		return false;
	}

	@Override
	public TreeSet<Table> getTables() {
		
		return tables;
	}
	
	@Override
	public void setTables(TreeSet<Table> aTables) {
		
		this.tables = aTables;
	}
	
	@Override
	public Table getTableByName(String name) {
		
		for(Table tab : tables.descendingSet()) {
			
			if(tab.getName().equals(name))
				return tab;
		}
		
		return null;
	}
	
	@Override
	public boolean containsTableByName(String name) {
		
		for(Table tab : tables.descendingSet()) {
			
			if(tab.getName().equals(name))
				return true;
		}
		
		return false;
	}
}
