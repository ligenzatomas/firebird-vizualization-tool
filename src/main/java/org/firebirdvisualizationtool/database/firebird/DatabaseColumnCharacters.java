/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.firebirdvisualizationtool.database.firebird;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;
import javax.swing.DefaultComboBoxModel;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author cml
 */

public class DatabaseColumnCharacters {
	
	private DatabaseCharacterSets characterSets;
	
	private static DatabaseColumnCharacters prototype;
	
	private DatabaseColumnCharacters() {};
	
	public static DatabaseColumnCharacters getPrototype() {
		
		if(prototype == null) {
			
			prototype = new DatabaseColumnCharacters();
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

	public DatabaseCharacterSets getCharacterSets() {
		
		return characterSets;
	}

	public void setCharacterSets(DatabaseCharacterSets characterSets) {
		
		this.characterSets = characterSets;
	}
	
	private static DatabaseCharacterSets loadSetsFromXml() {
		
		DatabaseCharacterSets sets = new DatabaseCharacterSets();
		
		try {  
     
			// create JAXB context and initializing Marshaller  
			JAXBContext jaxbContext = JAXBContext.newInstance(DatabaseCharacterSets.class, CharacterSet.class);
  
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();  
     
			// specify the location and name of xml file to be read  
			File XMLfile = new File("/home/cml/Dokumenty/School/Bakalářka/firebirdvisualizationtool/tinyuml-code/tinyuml/trunk/src/main/java/org/firebirdvisualizationtool/database/firebird/character_sets.xml");  
     
			// this will create Java object - country from the XML file  
			sets = (DatabaseCharacterSets) jaxbUnmarshaller.unmarshal(XMLfile); 
  
		} catch (JAXBException e) {  
		 // some exception occured  
		 e.printStackTrace();  
		}  
		
		return sets;
  
	}
}
