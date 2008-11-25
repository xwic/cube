/**
 * 
 */
package de.xwic.cube.impl;

import java.io.Serializable;

import de.xwic.cube.ICell;

/**
 * Represents a cell inside of the cube. A cell contains a value per measure.
 * 
 * @author Florian Lippisch
 */
public class Cell implements ICell, Serializable {

	private static final long serialVersionUID = -4297789024853482650L;
	
	private Double[] values;
	
	Cell(int measureSize) {
		values = new Double[measureSize];
	}
	
	/* (non-Javadoc)
	 * @see de.xwic.cube.ICell#getValue(de.xwic.cube.IMeasure)
	 */
	public Double getValue(int measureIndex) {
		return values[measureIndex];
	}
	
	/*
	 * Change the value in this cell.
	 */
	void setValue(int measureIndex, Double value) {
		values[measureIndex] = value;
	}
}
