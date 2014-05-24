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

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.List;
import org.tinyuml.draw.CompositeNode;
import org.tinyuml.draw.DrawingContext;
import org.tinyuml.draw.Label;
import org.tinyuml.draw.LabelSource;
import org.tinyuml.draw.Node;
import org.tinyuml.draw.RectilinearConnection;
import org.tinyuml.draw.SimpleLabel;
import org.tinyuml.model.Relation;
import org.tinyuml.model.RelationType;
import org.tinyuml.umldraw.shared.BaseConnection;

/**
 *
 * @author Tomáš Ligenza
 */
public class Relationship extends BaseConnection {

	private static Relationship prototype;
	private static final long serialVersionUID = 7441677812023019011L;
	
	private RelationType relationType;
	
	private Label multiplicity1Label;
	private Label multiplicity2Label;
	
	private boolean showMultiplicities = true;

	/**
	 * Returns the prototype instance.
	 * @return the prototype instance
	 */
	public static Relationship getPrototype() {
		
		if (prototype == null) prototype = new Relationship();

		return prototype;
	}

	/**
	 * Constructor.
	 */
	private Relationship() {
		
		setConnection(new RectilinearConnection());
		setupMultiplicityLabels();
	}

	public RelationType getRelationType() {
		return relationType;
	}

	public void setRelationType(RelationType relationType) {
		this.relationType = relationType;
	}

	/**
	 * Returns the value of the showMultiplicities property.
	 * @return the value of the showMultiplicities property
	 */
	public boolean showMultiplicities() { return showMultiplicities; }

	/**
	 * Sets the showMultiplicities property.
	 * @param flag the value of the showMultiplicities property
	 */
	public void setShowMultiplicities(boolean flag) { showMultiplicities = flag; }

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object clone() {
		
		Relationship cloned = (Relationship) super.clone();

		cloned.setupMultiplicityLabels();

		cloned.multiplicity1Label.setParent(multiplicity1Label.getParent());
		cloned.multiplicity2Label.setParent(multiplicity2Label.getParent());
		
		return cloned;
	}

	/**
	 * Sets the multiplicity label sources.
	 */
	private void setupMultiplicityLabels() {
		
		multiplicity1Label = new SimpleLabel();
		multiplicity1Label.setSource(new LabelSource() {
		  /**
		   * {@inheritDoc}
		   */
		  public String getLabelText() {
			return getRelation().getElement1Multiplicity().toString();
		  }

		  /**
		   * {@inheritDoc}
		   */
		  public void setLabelText(String aText) { }
		});

		multiplicity2Label = new SimpleLabel();
		multiplicity2Label.setSource(new LabelSource() {
		  /**
		   * {@inheritDoc}
		   */
		  public String getLabelText() {
			return getRelation().getElement2Multiplicity().toString();
		  }

		  /**
		   * {@inheritDoc}
		   */
		  public void setLabelText(String aText) { }
		});
	}

	/**
	 * Returns the multiplicity label for element 1.
	 * @return the multiplicity label for element 1
	 */
	public Label getMultiplicity1Label() { return multiplicity1Label; }

	/**
	 * Returns the multiplicity label for element 2.
	 * @return the multiplicity label for element 2
	 */
	public Label getMultiplicity2Label() { return multiplicity2Label; }

	/**
	 * Returns the model element, which is always an instance of Relation.
	 * @return the model element
	 */
	public Relation getRelation() { return (Relation) getModelElement(); }

	/**
	 * Returns the RelationType.
	 * @return the RelationType
	 */
	public RelationType getRelationshipType() { return relationType; }

	/**
	 * Sets the RelationType.
	 * @param aRelationType the RelationType
	 */
	public void setRelationshipType(RelationType aRelationType) {
		
	  relationType = aRelationType;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setParent(CompositeNode parent) {
		
		super.setParent(parent);
		multiplicity1Label.setParent(parent);
		multiplicity2Label.setParent(parent);
	}

	/**
	 * {@inheritDoc}
	 */
	public void draw(DrawingContext drawingContext) {
		
		super.draw(drawingContext);
		
		drawLabels(drawingContext);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Label getLabelAt(double xcoord, double ycoord) {
	  return null;
	}

	/**
	 * Draws the connection labels.
	 * @param drawingContext the DrawingContext
	 */
	private void drawLabels(DrawingContext drawingContext) {
	  
		if (showMultiplicities) {
			positionLabel(multiplicity1Label, getNode1(), getEndPoint1());
			positionLabel(multiplicity2Label, getNode2(), getEndPoint2());
			multiplicity1Label.draw(drawingContext);
			multiplicity2Label.draw(drawingContext);
		}
	}

	/**
	 * Positions a label relative to an endpoint.
	 * @param label the label
	 * @param node the node
	 * @param endpoint the end point
	 */
	private void positionLabel(Label label, Node node, Point2D endpoint) {
		
		Direction direction = getPointDirection(node, endpoint);
		double x, y;
		
		switch (direction) {
			case NORTH:
				x = endpoint.getX() + 5;
				y = endpoint.getY() - label.getSize().getHeight();
				break;
			case SOUTH:
				x = endpoint.getX() + 5;
				y = endpoint.getY() + 5;
				break;
			case EAST:
				x = endpoint.getX() + 5;
				y = endpoint.getY() + 5;
				break;
			case WEST:
			default:
				x = endpoint.getX() - label.getSize().getWidth() - 10;
				y = endpoint.getY() + 5;
				break;
		}
		label.setAbsolutePos(x, y);
	}

	/**
	 * A direction of an end point relative to its connected node.
	 */
	private enum Direction  { NORTH, SOUTH, EAST, WEST }

	/**
	 * Determines the direction the point is relative to the node.
	 * @param node the node
	 * @param point the point
	 * @return the direction
	 */
	private Direction getPointDirection(Node node, Point2D point) {
		
		if (point.getX() >= node.getAbsoluteX2()) {
			return Direction.EAST;
		}
		if (point.getX() <= node.getAbsoluteX1()) {
			return Direction.WEST;
		}
		if (point.getY() <= node.getAbsoluteY1()) {
			return Direction.NORTH;
		}
		
		return Direction.SOUTH;
	}

	/**
	 * Sets the position for the name label.
	 */
	private void positionNameLabel() {
	  
	  List<Line2D> segments = getSegments();
	  Line2D middlesegment = segments.get(segments.size() / 2);
	  int x = (int) (middlesegment.getX2() + middlesegment.getX1()) / 2;
	  int y = (int) (middlesegment.getY2() + middlesegment.getY1()) / 2;
	}
}