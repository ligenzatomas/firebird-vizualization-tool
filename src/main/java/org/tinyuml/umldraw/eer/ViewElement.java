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

package org.tinyuml.umldraw.eer;

import java.awt.geom.Dimension2D;
import org.tinyuml.draw.AbstractCompositeNode;
import org.tinyuml.draw.Compartment;
import org.tinyuml.draw.DoubleDimension;
import org.tinyuml.draw.DrawingContext;
import org.tinyuml.draw.Label;
import org.tinyuml.draw.LabelSource;
import org.tinyuml.draw.SimpleLabel;
import org.tinyuml.model.RelationEndType;
import org.tinyuml.model.RelationType;
import org.tinyuml.model.UmlModelElement;
import org.tinyuml.model.UmlModelElementListener;
import org.tinyuml.model.UmlView;
import org.tinyuml.umldraw.shared.UmlNode;

/**
 *
 * @author Tomáš Ligenza
 */
public class ViewElement extends AbstractCompositeNode implements 
	LabelSource, UmlNode, UmlModelElementListener {
	
	private static final long serialVersionUID = 2254935877352345066L;
	
	private UmlView viewData;
	private Label mainLabel;
	
	private Compartment mainCompartment;

	private static ViewElement prototype;
	
	public static ViewElement getPrototype() {
		
		if (prototype == null) prototype = new ViewElement();
		
		return prototype;
	}
	
	/**
	* Private constructor.
	*/
	private ViewElement() {
		
		mainCompartment = new Compartment();
		
		mainLabel = new SimpleLabel();
		mainLabel.setSource(this);
		
		mainCompartment.addLabel(mainLabel);
		mainCompartment.setParent(this);
	}
	
	/**
	* {@inheritDoc}
	*/
	@Override
	public Object clone() {
		
		ViewElement cloned = (ViewElement) super.clone();
		
		if (viewData != null) {
			cloned.viewData = (UmlView) viewData.clone();
			cloned.viewData.addModelElementListener(cloned);
		}
		
		cloned.mainLabel = (Label) mainLabel.clone();
		cloned.mainLabel.setSource(cloned);
		
		cloned.mainCompartment = (Compartment) mainCompartment.clone();
		cloned.mainCompartment.setParent(cloned);
		cloned.mainCompartment.removeAllLabels();
		cloned.mainCompartment.addLabel(cloned.mainLabel);
		
		return cloned;
	}
	
	/**
	* Returns the main label for testing purposes.
	* @return the main label
	*/
	public Label getMainLabel() { return mainLabel; }

	/**
	* Returns the main compartment for testing purposes.
	* @return the main compartment
	*/
	public Compartment getMainCompartment() { return mainCompartment; }

	/**
	* Sets the main compartment for testing purposes.
	* @param aCompartment the compartment to set
	*/
	public void setMainCompartment(Compartment aCompartment) {
		mainCompartment = aCompartment;
	}

	public void setModelElement(UmlView aModelElement) {
		
		viewData = aModelElement;
		
		if (viewData != null) {
			viewData.addModelElementListener(this);
		}
	}
	
	public UmlModelElement getModelElement() { return viewData; }
	
	public String getLabelText() { 
		
		return ((UmlView) getModelElement()).getViewModel().getName(); 
	}

	public void setLabelText(String aText) { 
		
		((UmlView) getModelElement()).getViewModel().setName(aText);
	}

	public void draw(DrawingContext drawingContext) {
	
		if (!isValid()) {
			recalculateSize(drawingContext);
		}
		
		mainCompartment.draw(drawingContext);
	}

	public void invalidate() {
	
		mainCompartment.invalidate();
	}

	public boolean isValid() {
	
		boolean result = mainCompartment.isValid();
	
		return result;
	}
	
	public Label getLabelAt(double mx, double my) {
	
		Label label = mainCompartment.getLabelAt(mx, my);
	
		return label;
	}

	public boolean acceptsConnection(RelationType associationType,
		RelationEndType as, UmlNode with) {
		
		return false;
	}
	
	@Override
	public void elementChanged(UmlModelElement element) {
		
		reinitMainCompartment();
		invalidate();
	}
	
	private void reinitMainCompartment() {
		
		mainCompartment.removeAllLabels();
		mainCompartment.addLabel(mainLabel);
	}
	
	@Override
	public String toString() {
		return getModelElement().getName();
	}

	// ************************************************************************
	// ****** Size calculation
	// *********************************
	
	@Override
	public Dimension2D getMinimumSize() {
		return new DoubleDimension(calculateMinimumWidth(),
			calculateMinimumHeight());
	}

	@Override
	public void recalculateSize(DrawingContext drawingContext) {
		
		recalculateMainCompartment(drawingContext);
		
		double totalWidth = calculateTotalWidth();
		// do not invalidate the sub elements
		setSizePlain(totalWidth, calculateTotalHeight());
		// adjust main compartment, in case the other compartments made this
		// element wider
		
		mainCompartment.setWidth(totalWidth);
		
		resizeLastCompartmentToFit();
		notifyNodeResized();
	}

	private void recalculateMainCompartment(DrawingContext drawingContext) {
		mainCompartment.recalculateSize(drawingContext);
	}
	
	private double getOperationsY() {
		double result = getMainCompartmentHeight();
		
		return result;
	}

	/**
	* Determines the total height of this element.
	* @return the total height
	*/
	private double calculateTotalHeight() {
	
		double compartmentHeightSum = getMainCompartmentHeight();
	
		return Math.max(compartmentHeightSum, getSize().getHeight());
	}

	/**
	* Determines the total width of this element.
	* @return the total width
	*/
	private double calculateTotalWidth() {
	
		double maxwidth = Math.max(mainCompartment.getSize().getWidth(),
			getSize().getWidth());
		
		return maxwidth;
	}

	/**
	* Calculates the minimum width of this element.
	* @return the minimum width
	*/
	private double calculateMinimumWidth() {
	
		double minimumWidth = mainCompartment.getMinimumSize().getWidth();
	
		return minimumWidth;
	}

	/**
	* Calculates the minimum height of this element.
	* @return the minimum height
	*/
	private double calculateMinimumHeight() {
	
		double minimumHeight = mainCompartment.getMinimumSize().getHeight();
	
		return minimumHeight;
	}

	/**
	* Resizes the last visible compartment to fit within the total height.
	*/
	private void resizeLastCompartmentToFit() {
	
		Compartment lastCompartment = getLastVisibleCompartment();
		double diffHeight = getSize().getHeight() - getCompartmentHeightSum();
		lastCompartment.setHeight(
				lastCompartment.getSize().getHeight() + diffHeight);
	}

	/**
	* Returns the last visible compartment.
	* @return the last visible compartment
	*/
	private Compartment getLastVisibleCompartment() {
	
		Compartment lastCompartment = mainCompartment;
	
		return lastCompartment;
	}

	/**
	* Returns the sum of compartment heights.
	* @return the sum of compartment heights
	*/
	private double getCompartmentHeightSum() {
	
		double result = mainCompartment.getSize().getHeight();
	
		return result;
	}

	/**
	* Returns the height of the compartment.
	* @return the height of the compartment
	*/
	private double getMainCompartmentHeight() {
		return mainCompartment.getSize().getHeight();
	}

	/**
	* {@inheritDoc}
	*/
	@Override
	public boolean isNestable() { return true; }
}