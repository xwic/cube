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

import de.xwic.cube.ICubeListener;
import de.xwic.cube.event.CellAggregatedEvent;
import de.xwic.cube.event.CellValueChangedEvent;

/**
 * Created on Apr 16, 2009
 * @author JBORNEMA
 */

public abstract class AbstractCubeListener implements ICubeListener {

	/* (non-Javadoc)
	 * @see de.xwic.cube.ICubeListener#onCellAggregated(de.xwic.cube.event.CellAggregatedEvent)
	 */
	public void onCellAggregated(CellAggregatedEvent event) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see de.xwic.cube.ICubeListener#onCellValueChanged(de.xwic.cube.event.CellValueChangedEvent)
	 */
	public void onCellValueChanged(CellValueChangedEvent event) {
		// TODO Auto-generated method stub
		
	}

}
