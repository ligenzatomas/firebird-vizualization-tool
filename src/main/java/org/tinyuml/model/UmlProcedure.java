/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tinyuml.model;

/**
 *
 * @author cml
 */
public class UmlProcedure extends AbstractUmlModelElement {
	
	private String source = 
		"SET TERM ^ ;\n" +
		"\n" +
		"CREATE PROCEDURE name \n" +
		" ( input_parameter_name < datatype>, ... ) \n" +
		"RETURNS \n" +
		" ( output_parameter_name < datatype>, ... )\n" +
		"AS \n" +
		"DECLARE VARIABLE variable_name < datatype>; \n" +
		"BEGIN\n" +
		"  /* write your code here */ \n" +
		"END^\n" +
		"\n" +
		"SET TERM ; ^";
	
	private static UmlProcedure prototype;
	
	public static UmlProcedure getPrototype() {
	
		if (prototype == null) prototype = new UmlProcedure();
	
		return prototype;
	}
	
	private UmlProcedure() { }
	
	public Object clone() {
		
		UmlProcedure cloned = (UmlProcedure) super.clone();
		
		cloned.source = 
			"SET TERM ^ ;\n" +
			"\n" +
			"CREATE PROCEDURE name \n" +
			" ( input_parameter_name < datatype>, ... ) \n" +
			"RETURNS \n" +
			" ( output_parameter_name < datatype>, ... )\n" +
			"AS \n" +
			"DECLARE VARIABLE variable_name < datatype>; \n" +
			"BEGIN\n" +
			"  /* write your code here */ \n" +
			"END^\n" +
			"\n" +
			"SET TERM ; ^";
		
		return cloned;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}
}
