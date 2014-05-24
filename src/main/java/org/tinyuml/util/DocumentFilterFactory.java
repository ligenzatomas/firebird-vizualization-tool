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

import javax.swing.JOptionPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;
import org.firebirdvisualizationtool.database.firebird.DatabaseMetaData;

/**
 *
 * @author Tomáš Ligenza
 */
public class DocumentFilterFactory {
	
	public enum DocumentFilterType { INT, DATABASE_COLUMN_NAME_LENGTH };
	
	private static final int maxLength = DatabaseMetaData.getDatabaseNameMaxLenght();
	
	public static DocumentFilter buildFilter(DocumentFilterType type, boolean notNull) {
		
		DocumentFilter filter = new DocumentFilter();
		
		switch(type) {
			
			case INT:
				filter = new IntFilter(notNull);
			break;
				
			case DATABASE_COLUMN_NAME_LENGTH:
				filter = new DatabaseColumnNameLengthFilter();
			break;
		}
		
		return filter;
	}
	
	static class DatabaseColumnNameLengthFilter extends DocumentFilter {
		
		@Override
		public void insertString(FilterBypass fb, int offset, String string,
			AttributeSet attr) throws BadLocationException {

			Document doc = fb.getDocument();
			StringBuilder sb = new StringBuilder();
			sb.append(doc.getText(0, doc.getLength()));
			sb.insert(offset, string);

			if (test(sb.toString())) {
				super.insertString(fb, offset, string, attr);
			} else {
				JOptionPane.showMessageDialog(null,
					ApplicationResources.getInstance().getString("filter.length.error") + "(" + maxLength + ")");
			}
		}

		private boolean test(String text) {
			
			return text.length() <= maxLength;
		}

		@Override
		public void replace(FilterBypass fb, int offset, int length, String text,
			AttributeSet attrs) throws BadLocationException {

			Document doc = fb.getDocument();
			StringBuilder sb = new StringBuilder();
			sb.append(doc.getText(0, doc.getLength()));
			sb.replace(offset, offset + length, text);

			if (test(sb.toString())) {
				super.replace(fb, offset, length, text, attrs);
			} else {
				JOptionPane.showMessageDialog(null, 
					ApplicationResources.getInstance().getString("filter.length.error") + "(" + maxLength + ")");
			}

		}

		@Override
		public void remove(FilterBypass fb, int offset, int length)
			throws BadLocationException {

			Document doc = fb.getDocument();
			StringBuilder sb = new StringBuilder();
			sb.append(doc.getText(0, doc.getLength()));
			sb.delete(offset, offset + length);

			if (test(sb.toString())) {
				super.remove(fb, offset, length);
			} else {
				JOptionPane.showMessageDialog(null, 
					ApplicationResources.getInstance().getString("filter.length.error") + "(" + maxLength + ")");
			}

		}
	}
	
	static class IntFilter extends DocumentFilter {
		
		private final boolean notNull;
		
		public IntFilter(boolean notNull) {
			
			this.notNull = notNull;
		}
	
		@Override
		public void insertString(FilterBypass fb, int offset, String string,
			AttributeSet attr) throws BadLocationException {

			Document doc = fb.getDocument();
			StringBuilder sb = new StringBuilder();
			sb.append(doc.getText(0, doc.getLength()));
			sb.insert(offset, string);

			if (test(sb.toString())) {
				super.insertString(fb, offset, string, attr);
			} else {
				JOptionPane.showMessageDialog(null, ApplicationResources.getInstance().getString("filter.numeric.error"));
			}
		}

		private boolean test(String text) {
			
			if(!notNull && text.isEmpty())
				return true;
			
			try {
				Integer.parseInt(text);
				return true;
			} catch (NumberFormatException e) {
				return false;
			}
		}

		@Override
		public void replace(FilterBypass fb, int offset, int length, String text,
			AttributeSet attrs) throws BadLocationException {

			Document doc = fb.getDocument();
			StringBuilder sb = new StringBuilder();
			sb.append(doc.getText(0, doc.getLength()));
			sb.replace(offset, offset + length, text);

			if (test(sb.toString())) {
				super.replace(fb, offset, length, text, attrs);
			} else {
				JOptionPane.showMessageDialog(null, ApplicationResources.getInstance().getString("filter.numeric.error"));
			}

		}

		@Override
		public void remove(FilterBypass fb, int offset, int length)
			throws BadLocationException {

			Document doc = fb.getDocument();
			StringBuilder sb = new StringBuilder();
			sb.append(doc.getText(0, doc.getLength()));
			sb.delete(offset, offset + length);

			if (test(sb.toString())) {
				super.remove(fb, offset, length);
			} else {
				JOptionPane.showMessageDialog(null, ApplicationResources.getInstance().getString("filter.numeric.error"));
			}

		}
	}
}
