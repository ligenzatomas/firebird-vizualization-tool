/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tinyuml.util;

import javax.swing.JOptionPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;

/**
 *
 * @author cml
 */
public class DocumentFilterFactory {
	
	public enum DocumentFilterType { INT };
	
	public static DocumentFilter buildFilter(DocumentFilterType type, boolean notNull) {
		
		DocumentFilter filter = new DocumentFilter();
		
		switch(type) {
			
			case INT:
				filter = new IntFilter(notNull);
			break;
		}
		
		return filter;
	}
	
	static class IntFilter extends DocumentFilter {
		
		private boolean notNull;
		
		public IntFilter(boolean notNull) {
			
			this.notNull = notNull;
		}
	
		public void insertString(FilterBypass fb, int offset, String string,
			AttributeSet attr) throws BadLocationException {

			Document doc = fb.getDocument();
			StringBuilder sb = new StringBuilder();
			sb.append(doc.getText(0, doc.getLength()));
			sb.insert(offset, string);

			if (test(sb.toString())) {
				super.insertString(fb, offset, string, attr);
			} else {
				JOptionPane.showMessageDialog(null, "Je nutné zadat pouze číselnou hodnotu!");
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
				JOptionPane.showMessageDialog(null, "Je nutné zadat pouze číselnou hodnotu!");
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
				JOptionPane.showMessageDialog(null, "Je nutné zadat pouze číselnou hodnotu!");
			}

		}
	}
}
