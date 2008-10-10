/**
 * 
 */
package de.xwic.cube.impl;

import java.io.Serializable;

import de.xwic.cube.IMeasure;

/**
 * @author Florian Lippisch
 */
public class Measure extends Identifyable implements IMeasure, Serializable {

	private static final long serialVersionUID = 4379759311293100433L;
	private final DataPool dataPool;

	/**
	 * @param dataPool 
	 * @param key
	 */
	public Measure(DataPool dataPool, String key) {
		super(key);
		this.dataPool = dataPool;
	}

	/* (non-Javadoc)
	 * @see de.xwic.cube.IMeasure#remove()
	 */
	public void remove() {
		dataPool.removeMeasure(this);
	}
}
