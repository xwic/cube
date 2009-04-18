/**
 * $Id: $
 *
 * Copyright (c) 2009 NetApp.
 * All rights reserved.

 * de.xwic.cube.impl.AbstractCubeListener.java
 * Created on Apr 16, 2009
 * 
 * @author JBORNEMA
 */
package de.xwic.cube.impl;

import de.xwic.cube.ICell;
import de.xwic.cube.ICubeListener;
import de.xwic.cube.Key;

/**
 * Created on Apr 16, 2009
 * @author JBORNEMA
 */

public class AbstractCubeListener implements ICubeListener {

	/* (non-Javadoc)
	 * @see de.xwic.cube.ICubeListener#onCellAggregated(de.xwic.cube.Key, de.xwic.cube.impl.Cell, de.xwic.cube.Key, de.xwic.cube.impl.Cell)
	 */
	public void onCellAggregated(Key childKey, Cell childCell, Key parentKey, Cell parentCell) {
	}

	/* (non-Javadoc)
	 * @see de.xwic.cube.ICubeListener#onCellValueChanged(de.xwic.cube.Key, de.xwic.cube.ICell, int, double)
	 */
	public void onCellValueChanged(Key key, ICell cell, int measureIndex, double diff) {
	}

}
