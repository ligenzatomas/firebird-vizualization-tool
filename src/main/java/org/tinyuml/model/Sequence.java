/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tinyuml.model;

/**
 *
 * @author cml
 */
public final class Sequence extends DefaultNamedElement {
	
	private static final long serialVersionUID = -173112122522078726L;
	
	private long initValue = 0;
	
	private static Sequence prototype;
	
	public static Sequence getPrototype() {
		
		if (prototype == null) prototype = new Sequence();
		
		return prototype;
	}

	private Sequence() { }
	
	@Override
	public Object clone() {
		
		Sequence cloned = null;
		cloned = (Sequence) super.clone();
		
		return cloned;
	}
	
	public Sequence create(String sName) {
		
		Sequence cloned = null;
		cloned = (Sequence) super.clone();
		
		cloned.setName(sName);
		
		return cloned;
	}

	public long getInitValue() {
		
		return initValue;
	}

	public void setInitValue(long value) {
		
		this.initValue = value;
	}
	
	public boolean checkValid() {
		
		return !getName().isEmpty();
	}
}