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

import de.xwic.cube.event.CellAggregatedEvent;
import de.xwic.cube.event.CellValueChangedEvent;


/**
 * Created on Apr 8, 2009
 * @author JBORNEMA
 */

public interface ICubeListener {

	/**
	 * Cell value changed event
	 * @param CellValueChangedEvent
	 */
	void onCellValueChanged(CellValueChangedEvent event);

	/**
	 * Cell aggregated event
	 * @param CellAggregatedEvent
	 */
	void onCellAggregated(CellAggregatedEvent event);

}
