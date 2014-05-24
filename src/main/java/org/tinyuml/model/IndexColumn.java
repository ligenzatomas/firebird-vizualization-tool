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
import org.firebirdvisualizationtool.database.firebird.IndexOrders;

/**
 *
 * @author Tomáš Ligenza
 */
public class IndexColumn implements Serializable {
	
	private static final long serialVersionUID = -3906491831142197415L;
		
	private Boolean checked;
	
	private String name;
	private IndexOrders oOrder;

	public IndexColumn(Boolean check, String name, IndexOrders oOrder) {
		this.checked = check;
		this.name = name;
		this.oOrder = oOrder;
	}
	
	public Boolean getChecked() {
		return checked;
	}

	public void setChecked(Boolean checked) {
		this.checked = checked;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public IndexOrders getoOrder() {
		return oOrder;
	}

	public void setoOrder(IndexOrders oOrder) {
		this.oOrder = oOrder;
	}

}
