/**
 * 
 */
package de.xwic.cube.impl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import de.xwic.cube.ICell;
import de.xwic.cube.IMeasure;

/**
 * Represents a cell inside of the cube. A cell contains a value per measure.
 * 
 * @author Florian Lippisch
 */
public class Cell implements ICell, Serializable {

	private static final long serialVersionUID = -4297789024853482650L;
	private Map<IMeasure, Double> values = new HashMap<IMeasure, Double>();
	
	/* (non-Javadoc)
	 * @see de.xwic.cube.ICell#getValue(de.xwic.cube.IMeasure)
	 */
	public Double getValue(IMeasure measure) {
		return values.get(measure);
	}
	
	/*
	 * Change the value in this cell.
	 */
	void setValue(IMeasure measure, Double value) {
		values.put(measure, value);
	}
}
