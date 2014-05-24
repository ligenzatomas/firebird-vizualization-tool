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
import javax.swing.DefaultComboBoxModel;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

/**
 *
 * @author Tomáš Ligenza
 */

public class DatabaseCharacterSets {
	
	private CharacterSets characterSets;
	
	private static DatabaseCharacterSets prototype;
	
	private DatabaseCharacterSets() {};
	
	public static DatabaseCharacterSets getPrototype() {
		
		if(prototype == null) {
			
			prototype = new DatabaseCharacterSets();
			prototype.setCharacterSets(loadSetsFromXml());
		}
		
		return prototype;
	}
	
	public static CharacterSet getCharacterSetByName(String name) {
		
		CharacterSet charSet = new CharacterSet();
		
		for (CharacterSet set : getPrototype().getCharacterSets().getCharacterSets()) {
			
			if(set.getName().equals(name))
				charSet = set;
		}
		
		return charSet;
	}
	
	public static DefaultComboBoxModel<CharacterSet> getColumnCharacters() {
		
		DefaultComboBoxModel model = new DefaultComboBoxModel();
		
		for (CharacterSet set : getPrototype().getCharacterSets().getCharacterSets()) {
			
			model.addElement(set);
		}
		
		return model;
	}
	
	public static DefaultComboBoxModel<String> getColumnCollateBySet(String setName) {
		
		DefaultComboBoxModel model = new DefaultComboBoxModel();
		
		for (CharacterSet set : getPrototype().getCharacterSets().getCharacterSets()) {
			
			if(set.getName().equals(setName)) {
				
				for (String collation : set.getCollations()) {
					
					model.addElement(collation);
				}
			}
		}
		
		return model;
	}

	public CharacterSets getCharacterSets() {
		
		return characterSets;
	}

	public void setCharacterSets(CharacterSets characterSets) {
		
		this.characterSets = characterSets;
	}
	
	private static CharacterSets loadSetsFromXml() {
		
		CharacterSets sets = new CharacterSets();
		
		try {  
     
			// create JAXB context and initializing Marshaller  
			JAXBContext jaxbContext = JAXBContext.newInstance(CharacterSets.class, CharacterSet.class);
  
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();  
     
			// specify the location and name of xml file to be read  
			//File XMLfile = new File("/home/cml/Dokumenty/School/Bakalářka/firebirdvisualizationtool/tinyuml-code/tinyuml/trunk/src/main/java/org/firebirdvisualizationtool/database/firebird/character_sets.xml");  
			File XMLfile = new File("character_sets.xml");  
     
     
			// this will create Java object - country from the XML file  
			sets = (CharacterSets) jaxbUnmarshaller.unmarshal(XMLfile); 
  
		} catch (JAXBException e) {  
		 // some exception occured  
		 e.printStackTrace();  
		}  
		
		return sets;
  
	}
}
