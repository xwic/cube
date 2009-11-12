/**
 * $Id: $
 *
 * Copyright (c) 2009 NetApp.
 * All rights reserved.

 * de.xwic.cube.event.CellValueChangedEvent.java
 * Created on Apr 22, 2009
 * 
 * @author JBORNEMA
 */
package de.xwic.cube.event;

import de.xwic.cube.ICell;
import de.xwic.cube.ICube;
import de.xwic.cube.Key;

/**
 * Created on Apr 22, 2009
 * 
 * @author JBORNEMA
 */

public class CellValueChangedEvent extends CubeEvent {

	protected Key key;

	protected ICell cell;

	protected int measureIndex;

	protected double valueDifference;

	/**
	 * Default constructor	
	 */
	public CellValueChangedEvent() {
	}

	/**
	 * @param cube
	 * @param key
	 * @param cell
	 * @param measureIndex
	 * @param diff
	 */
	public CellValueChangedEvent(ICube cube, Key key, ICell cell, int measureIndex, double diff) {
		this.cube = cube;
		this.key = key;
		this.cell = cell;
		this.measureIndex = measureIndex;
		this.valueDifference = diff;
	}

	/**
	 * @return the key
	 */
	public Key getKey() {
		return key;
	}

	/**
	 * @param key
	 *            the key to set
	 */
	public void setKey(Key key) {
		this.key = key;
	}

	/**
	 * @return the cell
	 */
	public ICell getCell() {
		return cell;
	}

	/**
	 * @param cell
	 *            the cell to set
	 */
	public void setCell(ICell cell) {
		this.cell = cell;
	}

	/**
	 * @return the measureIndex
	 */
	public int getMeasureIndex() {
		return measureIndex;
	}

	/**
	 * @param measureIndex
	 *            the measureIndex to set
	 */
	public void setMeasureIndex(int measureIndex) {
		this.measureIndex = measureIndex;
	}

	/**
	 * @return the valueDifference
	 */
	public double getValueDifference() {
		return valueDifference;
	}

	/**
	 * @param valueDifference
	 *            the valueDifference to set
	 */
	public void setValueDifference(double valueDifference) {
		this.valueDifference = valueDifference;
	}

}
