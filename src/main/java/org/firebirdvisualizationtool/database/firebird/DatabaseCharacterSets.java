/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.firebirdvisualizationtool.database.firebird;

import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author cml
 */

@XmlRootElement(name = "DatabaseCharacterSets")
public class DatabaseCharacterSets {
	
	protected List<CharacterSet> characterSets;

	public DatabaseCharacterSets() {
		
		this.characterSets = new ArrayList<CharacterSet>();
	}

	@XmlElement(name = "CharacterSet")
	public List<CharacterSet> getCharacterSets() {
		return characterSets;
	}

	public void setCharacterSets(List<CharacterSet> character_sets) {
		this.characterSets = character_sets;
	}
}
