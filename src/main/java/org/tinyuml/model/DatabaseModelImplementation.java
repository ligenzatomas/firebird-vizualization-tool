/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tinyuml.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;

/**
 *
 * @author cml
 */
public class DatabaseModelImplementation implements DatabaseModel {

	private TreeMap<String, Sequence> sequences = new TreeMap<String, Sequence>();
	private TreeMap<String, Domain> domains		= new TreeMap<String, Domain>();
	
	public DatabaseModelImplementation() {}
	
	private void writeObject(ObjectOutputStream stream) throws IOException {
		
		stream.writeObject(sequences);
		stream.writeObject(domains);
	}

	private void readObject(ObjectInputStream stream)
		throws IOException, ClassNotFoundException {
		
		sequences	= (TreeMap<String, Sequence>) stream.readObject();
		domains		= (TreeMap<String, Domain>) stream.readObject();
	}
	
	@Override
	public void addSequence(Sequence aSequence) {
		
		sequences.put(aSequence.getName(), aSequence);
	}

	@Override
	public boolean containsSequence(Sequence aSequence) {
		
		return sequences.containsKey(aSequence.getName());
	}

	@Override
	public TreeMap<String, Sequence> getSequences() {
		
		return sequences;
	}
	
	@Override
	public void setSequences(TreeMap<String, Sequence> sequences) {
		
		this.sequences = sequences;
	}
	
	@Override
	public void addDomain(Domain aDomain) {
		
		domains.put(aDomain.getName(), aDomain);
	}

	@Override
	public boolean containsDomain(Domain aDomain) {
		
		return domains.containsKey(aDomain.getName());
	}

	@Override
	public TreeMap<String, Domain> getDomains() {
		
		return domains;
	}
	
	@Override
	public void setDomains(TreeMap<String, Domain> aDomains) {
		
		this.domains = aDomains;
	}
}
