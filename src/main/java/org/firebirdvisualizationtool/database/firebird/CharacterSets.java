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

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Tomáš Ligenza
 */

@XmlRootElement(name = "CharacterSets")
public class CharacterSets {
	
	protected List<CharacterSet> characterSets;

	public CharacterSets() {
		
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
