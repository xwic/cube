/**
 * 
 */
package de.xwic.cube.impl;

import java.io.Serializable;

/**
 * @author jbornema
 *
 */
public class CellExt extends Cell {

	private static final long serialVersionUID = 1L;
	
	protected Serializable userObject = null;
	
	/**
	 * 
	 */
	public CellExt() {
	}

	/**
	 * @param measureSize
	 */
	public CellExt(int measureSize) {
		super(measureSize);
	}

	/**
	 * @return the userObject
	 */
	public Object getUserObject() {
		return userObject;
	}

	/**
	 * @param userObject the userObject to set
	 */
	public void setUserObject(Serializable userObject) {
		this.userObject = userObject;
	}

}
