/**
 * $Id: $
 *
 * Copyright (c) 2009 NetApp.
 * All rights reserved.

 * de.xwic.cube.event.CellAggregatedEvent.java
 * Created on Apr 22, 2009
 * 
 * @author JBORNEMA
 */
package de.xwic.cube.event;

import de.xwic.cube.ICell;
import de.xwic.cube.ICube;
import de.xwic.cube.Key;
import de.xwic.cube.impl.Cell;

/**
 * Created on Apr 22, 2009
 * 
 * @author JBORNEMA
 */

public class CellAggregatedEvent extends CubeEvent {

	protected Key childKey;

	protected ICell childCell;

	protected Key parentKey;

	protected ICell parentCell;

	
	/**
	 * Default constructor
	 */
	public CellAggregatedEvent() {
	}

	/**
	 * @param cube
	 * @param childKey2
	 * @param childCell2
	 * @param parentKey2
	 * @param parentCell2
	 */
	public CellAggregatedEvent(ICube cube, Key childKey, Cell childCell, Key parentKey,	Cell parentCell) {
		this.cube = cube;
		this.childKey = childKey;
		this.childCell = childCell;
		this.parentKey = parentKey;
		this.parentCell = parentCell;
	}

	/**
	 * @return the childKey
	 */
	public Key getChildKey() {
		return childKey;
	}

	/**
	 * @param childKey
	 *            the childKey to set
	 */
	public void setChildKey(Key childKey) {
		this.childKey = childKey;
	}

	/**
	 * @return the childCell
	 */
	public ICell getChildCell() {
		return childCell;
	}

	/**
	 * @param childCell
	 *            the childCell to set
	 */
	public void setChildCell(ICell childCell) {
		this.childCell = childCell;
	}

	/**
	 * @return the parentKey
	 */
	public Key getParentKey() {
		return parentKey;
	}

	/**
	 * @param parentKey
	 *            the parentKey to set
	 */
	public void setParentKey(Key parentKey) {
		this.parentKey = parentKey;
	}

	/**
	 * @return the parentCell
	 */
	public ICell getParentCell() {
		return parentCell;
	}

	/**
	 * @param parentCell
	 *            the parentCell to set
	 */
	public void setParentCell(ICell parentCell) {
		this.parentCell = parentCell;
	}

}
