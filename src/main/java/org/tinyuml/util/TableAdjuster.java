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

package org.tinyuml.util;

import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

/**
 *
 * @author Tomáš Ligenza
 */
public class TableAdjuster {
	
	public TableAdjuster() {}
	
	public void adjustColumns(JTable table) {
		
		TableColumnModel tcm = table.getColumnModel();

		for (int i = 0; i < tcm.getColumnCount(); i++) {
			
			adjustColumn(i, table);
		}
	}
	
	public void adjustColumn(final int column, JTable table) {
		
		TableColumn tableColumn = table.getColumnModel().getColumn(column);

		if (! tableColumn.getResizable()) return;

		int columnHeaderWidth = getColumnHeaderWidth( column, table );
		int columnDataWidth   = getColumnDataWidth( column, table );
		int preferredWidth    = Math.max(columnHeaderWidth, columnDataWidth);

		updateTableColumn(column, preferredWidth, table);
	}
	
	private int getColumnHeaderWidth(int column, JTable table) {
		
		TableColumn tableColumn = table.getColumnModel().getColumn(column);
		Object value = tableColumn.getHeaderValue();
		TableCellRenderer renderer = tableColumn.getHeaderRenderer();

		if (renderer == null) {
			
			renderer = table.getTableHeader().getDefaultRenderer();
		}

		Component c = renderer.getTableCellRendererComponent(table, value, false, false, 0, column);
		
		return c.getPreferredSize().width;
	}
	
	private int getColumnDataWidth(int column, JTable table) {

		int preferredWidth = 0;
		int maxWidth = table.getColumnModel().getColumn(column).getMaxWidth();

		for (int row = 0; row < table.getRowCount(); row++) {
			
    		preferredWidth = Math.max(preferredWidth, getCellDataWidth(row, column, table));

			if (preferredWidth >= maxWidth)
			    break;
		}

		return preferredWidth;
	}
	
	private int getCellDataWidth(int row, int column, JTable table) {

		TableCellRenderer cellRenderer = table.getCellRenderer(row, column);
		Component c = table.prepareRenderer(cellRenderer, row, column);
		int width = c.getPreferredSize().width + table.getIntercellSpacing().width;

		return width;
	}
	
	private void updateTableColumn(int column, int width, JTable table) {
		
		final TableColumn tableColumn = table.getColumnModel().getColumn(column);

		if (! tableColumn.getResizable()) return;

		width += 15;
		
		width = Math.max(width, tableColumn.getPreferredWidth());

		table.getTableHeader().setResizingColumn(tableColumn);
		tableColumn.setWidth(width);
	}
}
