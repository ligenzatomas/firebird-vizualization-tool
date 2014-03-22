/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tinyuml.model;

import java.io.Serializable;
import java.util.TreeMap;

/**
 *
 * @author cml
 */
public interface DatabaseModel extends Serializable {

	void addSequence(Sequence aSequence);
	
	boolean containsSequence(Sequence aSequence);
	
	TreeMap<String, Sequence> getSequences();
	
	void setSequences(TreeMap<String, Sequence> sequences);
	
	void addDomain(Domain aSequence);
	
	boolean containsDomain(Domain aSequence);
	
	TreeMap<String, Domain> getDomains();
	
	void setDomains(TreeMap<String, Domain> sequences);
}