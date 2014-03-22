/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.firebirdvisualizationtool.database.firebird;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author cml
 */

@XmlRootElement(name = "CharacterSet")
@XmlAccessorType(XmlAccessType.FIELD)
public class CharacterSet {
	
	@XmlElement(name = "name")
	protected String name;
	
	@XmlElementWrapper(name = "collations")
	@XmlElement(name = "collation")
	protected List<String> collations = new ArrayList<String>();

	public CharacterSet() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getCollations() {
		return collations;
	}

	public void setCollations(List<String> collations) {
		this.collations = collations;
	}
	
	@Override
	public String toString() {
		
		return getName();
	}
}
