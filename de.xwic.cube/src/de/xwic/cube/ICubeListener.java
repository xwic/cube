/**
 * $Id: $
 *
 * Copyright (c) 2009 NetApp.
 * All rights reserved.

 * de.xwic.cube.ICubeListener.java
 * Created on Apr 8, 2009
 * 
 * @author JBORNEMA
 */
package de.xwic.cube;


/**
 * Created on Apr 8, 2009
 * @author JBORNEMA
 */

public interface ICubeListener {

	/**
	 * Cell value changed event
	 * @param key
	 * @param cell
	 * @param measureIndex
	 * @param diff
	 */
	void onCellValueChanged(Key key, ICell cell, int measureIndex, double diff);

}
