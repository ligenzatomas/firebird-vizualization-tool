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
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Tomáš Ligenza
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
