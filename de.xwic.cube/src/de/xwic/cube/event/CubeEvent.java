/**
 * $Id: $
 *
 * Copyright (c) 2009 NetApp.
 * All rights reserved.

 * de.xwic.cube.event.CubeEvent.java
 * Created on Apr 22, 2009
 * 
 * @author JBORNEMA
 */
package de.xwic.cube.event;

import de.xwic.cube.ICube;

/**
 * Created on Apr 22, 2009
 * @author JBORNEMA
 */

public abstract class CubeEvent {

	protected ICube cube;

	/**
	 * @return the cube
	 */
	public ICube getCube() {
		return cube;
	}

	/**
	 * @param cube the cube to set
	 */
	public void setCube(ICube cube) {
		this.cube = cube;
	}
	
}
