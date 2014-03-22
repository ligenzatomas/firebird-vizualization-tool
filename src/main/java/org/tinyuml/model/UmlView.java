/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tinyuml.model;

/**
 *
 * @author cml
 */
public class UmlView extends AbstractUmlModelElement {
	
	private static final long serialVersionUID = 1888655958087475309L;
	
	private String source = 
		"CREATE VIEW name ( view_column, ...)\n" +
		"AS\n" +
		"/* write select statement here */\n" +
		"WITH CHECK OPTION;";
	
	private static UmlView prototype;
	
	public static UmlView getPrototype() {
	
		if (prototype == null) prototype = new UmlView();
	
		return prototype;
	}
	
	private UmlView() { }
	
	public Object clone() {
		
		UmlView cloned = (UmlView) super.clone();
		
		cloned.source = 
			"CREATE VIEW name ( view_column, ...)\n" +
			"AS\n" +
			"/* write select statement here */\n" +
			"WITH CHECK OPTION;";
		
		return cloned;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}
}
