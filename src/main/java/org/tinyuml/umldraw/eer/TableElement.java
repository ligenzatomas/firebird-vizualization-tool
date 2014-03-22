/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tinyuml.umldraw.eer;

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
import org.tinyuml.model.UmlTable;
import org.tinyuml.umldraw.shared.UmlModelElementLabelSource;
import org.tinyuml.umldraw.shared.UmlNode;
import java.awt.geom.Dimension2D;
import javax.swing.JOptionPane;
import org.tinyuml.model.ElementNameChangeListener;
import org.tinyuml.model.UmlTableCol;
import org.tinyuml.model.UmlTableForeignKey;
import org.tinyuml.model.UmlTableIndex;

/**
 *
 * @author cml
 */
public class TableElement extends AbstractCompositeNode implements 
	LabelSource, UmlNode, UmlModelElementListener {
	
	private static final long serialVersionUID = 8583471299020666928L;
	
	private UmlTable tableData;
	private Label mainLabel;
	
	private Compartment mainCompartment;
	private Compartment colsCompartment;
	private Compartment indexesCompartment;
	private Compartment foreignKeysCompartment;
	
	private boolean showCols = true, showIndexes = true
		, showForeignKeys = true;
	
	private ElementNameChangeListener nameChangeListener;

	private static TableElement prototype;
	
	public static TableElement getPrototype() {
		
		if (prototype == null) prototype = new TableElement();
		
		return prototype;
	}
	
	/**
	* Private constructor.
	*/
	private TableElement() {
		
		mainCompartment = new Compartment();
		colsCompartment = new Compartment();
		indexesCompartment = new Compartment();
		foreignKeysCompartment = new Compartment();
		
		mainLabel = new SimpleLabel();
		mainLabel.setSource(this);
		
		mainCompartment.addLabel(mainLabel);
		mainCompartment.setParent(this);
		
		colsCompartment.setParent(this);
		colsCompartment.setAlignment(Compartment.Alignment.LEFT);
		
		indexesCompartment.setParent(this);
		indexesCompartment.setAlignment(Compartment.Alignment.LEFT);
		
		foreignKeysCompartment.setParent(this);
		foreignKeysCompartment.setAlignment(Compartment.Alignment.LEFT);
	}
	
	/**
	* {@inheritDoc}
	*/
	@Override
	public Object clone() {
		
		TableElement cloned = (TableElement) super.clone();
		
		if (tableData != null) {
			cloned.tableData = (UmlTable) tableData.clone();
			cloned.tableData.addModelElementListener(cloned);
		}
		
		cloned.mainLabel = (Label) mainLabel.clone();
		cloned.mainLabel.setSource(cloned);
		
		cloned.mainCompartment = (Compartment) mainCompartment.clone();
		cloned.mainCompartment.setParent(cloned);
		cloned.mainCompartment.removeAllLabels();
		cloned.mainCompartment.addLabel(cloned.mainLabel);
		
		cloned.colsCompartment = (Compartment) colsCompartment.clone();
		cloned.colsCompartment.setParent(cloned);
		cloned.indexesCompartment = (Compartment) indexesCompartment.clone();
		cloned.indexesCompartment.setParent(cloned);
		cloned.foreignKeysCompartment = (Compartment) foreignKeysCompartment.clone();
		cloned.foreignKeysCompartment.setParent(cloned);
		
		return cloned;
	}
	
	public void addNameChangeListener(ElementNameChangeListener nameChangeListener) {
		
		this.nameChangeListener = nameChangeListener;
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

	public Compartment getColsCompartment() {
		return colsCompartment;
	}

	public void setColsCompartment(Compartment aCompartment) {
		colsCompartment = aCompartment;
	}

	public Compartment getIndexesCompartment() {
		return indexesCompartment;
	}

	public void setIndexesCompartment(Compartment aCompartment) {
		indexesCompartment = aCompartment;
	}
	
	public Compartment getForeignKeysCompartment() {
		return foreignKeysCompartment;
	}

	public void setForeignKeysCompartment(Compartment aCompartment) {
		foreignKeysCompartment = aCompartment;
	}

	public void setModelElement(UmlTable aModelElement) {
		
		tableData = aModelElement;
		
		if (tableData != null) {
			tableData.addModelElementListener(this);
		}
	}
	
	public UmlModelElement getModelElement() { return tableData; }
	
	public void setShowCols(boolean flag) {
		
		if (showCols && !flag) {
			setHeight(getSize().getHeight() -
				colsCompartment.getSize().getHeight());
		}
	
		showCols = flag;
		invalidate();
	}
	
	public boolean showCols() { return showCols; }
	
	public void setShowIndexes(boolean flag) {
		
		if (showIndexes && !flag) {
			setHeight(getSize().getHeight() -
				indexesCompartment.getSize().getHeight());
		}
	
		showIndexes = flag;
		invalidate();
	}
	
	public boolean showIndexes() { return showIndexes; }
	
	public void setShowForeignKeys(boolean flag) {
	
		if (showForeignKeys && !flag) {
			setHeight(getSize().getHeight() -
				foreignKeysCompartment.getSize().getHeight());
		}
	
		showForeignKeys = flag;
		invalidate();
	}
	
	public boolean showForeignKeys() { return showForeignKeys; }

	public String getLabelText() { return getModelElement().getName(); }

	public void setLabelText(String aText) { 
		
		if(nameChangeListener != null
			&& nameChangeListener.nameChanged(getModelElement().getName(), aText)) {
			
			getModelElement().setName(aText);
		} else {
			JOptionPane.showMessageDialog(null, "Zadané jméno tabulky již existuje");
		}
	}

	public void draw(DrawingContext drawingContext) {
	
		if (!isValid()) {
			recalculateSize(drawingContext);
		}
		
		mainCompartment.draw(drawingContext);
	
		if (showCols) colsCompartment.draw(drawingContext);
		if (showIndexes) indexesCompartment.draw(drawingContext);
		if (showForeignKeys) foreignKeysCompartment.draw(drawingContext);
	}

	public void invalidate() {
	
		mainCompartment.invalidate();
	
		colsCompartment.invalidate();
		indexesCompartment.invalidate();
		foreignKeysCompartment.invalidate();
	}

	public boolean isValid() {
	
		boolean result = mainCompartment.isValid();
	
		if (showCols) {
			result &= colsCompartment.isValid();
		}
	
		if (showIndexes) {
			result &= indexesCompartment.isValid();
		}
		
		if (showForeignKeys) {
			result &= foreignKeysCompartment.isValid();
		}
	
		return result;
	}
	
	public Label getLabelAt(double mx, double my) {
	
		Label label = mainCompartment.getLabelAt(mx, my);
	
		return label;
	}

	public boolean acceptsConnection(RelationType associationType,
		RelationEndType as, UmlNode with) {
		
		if(with != null) {
			
			// pozor tato trida je nyni objekt, ktery connection akceptuje, element je tedy ten, ze ktereho connection vychazi
			
			UmlModelElement element = with.getModelElement();
		
			if(element instanceof UmlTable) {

				//UmlTable table = (UmlTable) element;
				
				if(tableData.isPrimaryKey()) {
					
					
					
					return true;
				}
			}
			
			return false;
		}
		
		return true;
	}
	
	@Override
	public void elementChanged(UmlModelElement element) {
		
		colsCompartment.removeAllLabels();
		for (UmlTableCol tableCol : ((UmlTable) element).getCols()) {
			Label label = new SimpleLabel();
			label.setSource(new UmlModelElementLabelSource(tableCol));
			colsCompartment.addLabel(label);
		}
		
		indexesCompartment.removeAllLabels();
		for (UmlTableIndex property : ((UmlTable) element).getIndexes()) {
			Label label = new SimpleLabel();
			label.setSource(new UmlModelElementLabelSource(property));
			indexesCompartment.addLabel(label);
		}
		
		foreignKeysCompartment.removeAllLabels();
		for (UmlTableForeignKey property : ((UmlTable) element).getForeignKeys()) {
			Label label = new SimpleLabel();
			label.setSource(new UmlModelElementLabelSource(property));
			foreignKeysCompartment.addLabel(label);
		}
		
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
		recalculateColsCompartment(drawingContext);
		recalculateIndexesCompartment(drawingContext);
		recalculateForeignKeysCompartment(drawingContext);
		
		double totalWidth = calculateTotalWidth();
		
		setSizePlain(totalWidth, calculateTotalHeight());
		
		mainCompartment.setWidth(totalWidth);
		colsCompartment.setWidth(totalWidth);
		indexesCompartment.setWidth(totalWidth);
		foreignKeysCompartment.setWidth(totalWidth);
		
		resizeLastCompartmentToFit();
		notifyNodeResized();
	}

	private void recalculateMainCompartment(DrawingContext drawingContext) {
		
		mainCompartment.recalculateSize(drawingContext);
	}
	
	private void recalculateColsCompartment(DrawingContext drawingContext) {
		colsCompartment.recalculateSize(drawingContext);
		colsCompartment.setOrigin(0, getMainCompartmentHeight());
	}
	
	private void recalculateIndexesCompartment(DrawingContext drawingContext) {
		indexesCompartment.recalculateSize(drawingContext);
		indexesCompartment.setOrigin(0, getColsY());
	}
	
	private void recalculateForeignKeysCompartment(DrawingContext drawingContext) {
		foreignKeysCompartment.recalculateSize(drawingContext);
		foreignKeysCompartment.setOrigin(0, getIndexesY());
	}
	
	private double getColsY() {
		
		double result = getMainCompartmentHeight();
		if (showCols) result += colsCompartment.getSize().getHeight();
		
		return result;
	}
	
	private double getIndexesY() {
		
		double result = getMainCompartmentHeight();
		if (showCols) result += colsCompartment.getSize().getHeight();
		if (showIndexes) result += indexesCompartment.getSize().getHeight();
		
		return result;
	}

	/**
	* Determines the total height of this element.
	* @return the total height
	*/
	private double calculateTotalHeight() {
	
		double compartmentHeightSum = getMainCompartmentHeight();
	
		if (showCols) {
			compartmentHeightSum +=
				colsCompartment.getMinimumSize().getHeight();
		}
	
		if (showIndexes) {
			compartmentHeightSum +=
				indexesCompartment.getMinimumSize().getHeight();
		}
		
		if (showForeignKeys) {
			compartmentHeightSum +=
				foreignKeysCompartment.getMinimumSize().getHeight();
		}
	
		return Math.max(compartmentHeightSum, getSize().getHeight());
	}

	/**
	* Determines the total width of this element.
	* @return the total width
	*/
	private double calculateTotalWidth() {
	
		double maxwidth = Math.max(mainCompartment.getSize().getWidth(),
			getSize().getWidth());
		
		if (showCols) {
			maxwidth = Math.max(maxwidth, colsCompartment.getSize().getWidth());
		}
	
		if (showIndexes) {
			maxwidth = Math.max(maxwidth, indexesCompartment.getSize().getWidth());
		}
		
		if (showForeignKeys) {
			maxwidth = Math.max(maxwidth, foreignKeysCompartment.getSize().getWidth());
		}
		
		return maxwidth;
	}

	/**
	* Calculates the minimum width of this element.
	* @return the minimum width
	*/
	private double calculateMinimumWidth() {
	
		double minimumWidth = mainCompartment.getMinimumSize().getWidth();
	
		if (showCols) {
			minimumWidth = Math.max(minimumWidth,
				colsCompartment.getMinimumSize().getWidth());
		}
	
		if (showIndexes) {
			minimumWidth = Math.max(minimumWidth,
				indexesCompartment.getMinimumSize().getWidth());
		}
		
		if (showForeignKeys) {
			minimumWidth = Math.max(minimumWidth,
				foreignKeysCompartment.getMinimumSize().getWidth());
		}
	
		return minimumWidth;
	}

	/**
	* Calculates the minimum height of this element.
	* @return the minimum height
	*/
	private double calculateMinimumHeight() {
	
		double minimumHeight = mainCompartment.getMinimumSize().getHeight();
	
		if (showCols) {
			minimumHeight += colsCompartment.getMinimumSize().getHeight();
		}
	
		if (showIndexes) {
			minimumHeight += indexesCompartment.getMinimumSize().getHeight();
		}
		
		if (showForeignKeys) {
			minimumHeight += foreignKeysCompartment.getMinimumSize().getHeight();
		}
	
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
	
		if (showCols) lastCompartment = colsCompartment;
		if (showIndexes) lastCompartment = indexesCompartment;
		if (showForeignKeys) lastCompartment = foreignKeysCompartment;
	
		return lastCompartment;
	}

	/**
	* Returns the sum of compartment heights.
	* @return the sum of compartment heights
	*/
	private double getCompartmentHeightSum() {
	
		double result = mainCompartment.getSize().getHeight();
	
		if (showCols) {
			result += colsCompartment.getSize().getHeight();
		}
	
		if (showIndexes) {
			result += indexesCompartment.getSize().getHeight();
		}
		
		if (showForeignKeys) {
			result += foreignKeysCompartment.getSize().getHeight();
		}
	
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
