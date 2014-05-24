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

import java.io.Serializable;
import java.util.TreeSet;
import javax.swing.DefaultComboBoxModel;

/**
 *
 * @author Tomáš Ligenza
 */
public interface DatabaseModel extends Serializable {

	void addSequence(Sequence aSequence);
	
	boolean containsSequence(Sequence aSequence);
	
	TreeSet<Sequence> getSequences();
	
	void setSequences(TreeSet<Sequence> sequences);
	
	void addView(View aView);
	
	boolean containsView(View aView);
	
	boolean containsViewByName(String name);
	
	TreeSet<View> getViews();
	
	void setViews(TreeSet<View> aViews);
	
	void addProcedure(Procedure aProcedure);
	
	boolean containsProcedure(Procedure aProcedure);
	
	boolean containsProcedureByName(String name);
	
	TreeSet<Procedure> getProcedures();
	
	void setProcedures(TreeSet<Procedure> aProcedures);
	
	void addDomain(Domain aSequence);
	
	boolean containsDomain(Domain aSequence);
	
	TreeSet<Domain> getDomains();
	
	void setDomains(TreeSet<Domain> sequences);
	
	DefaultComboBoxModel getDomainsComboBoxModel();
	
	void addTable(Table aTable);
	
	boolean containsTable(Table aTable);
	
	TreeSet<Table> getTables();
	
	void setTables(TreeSet<Table> tables);
	
	Table getTableByName(String name);
	
	boolean containsTableByName(String name);
	
	DefaultComboBoxModel getTablesComboBoxModel();
	
}