/**
 * $Id: $
 *
 * Copyright (c) 2009 NetApp.
 * All rights reserved.

 * de.xwic.cube.webui.controls.DimensionElementSelectedEvent.java
 * Created on Apr 28, 2009
 * 
 * @author jbornema
 */
package de.xwic.cube.webui.controls;

import de.jwic.events.ElementSelectedEvent;
import de.xwic.cube.IDimensionElement;

/**
 * Created on Apr 28, 2009
 * @author jbornema
 */

public class DimensionElementSelectedEvent extends ElementSelectedEvent {

	protected IDimensionElement previousDimensionElement;
	
	/**
	 * @param source
	 * @param selectedElement
	 */
	public DimensionElementSelectedEvent(Object source, IDimensionElement selectedDimensionElement, IDimensionElement previousDimensionElement) {
		super(source, selectedDimensionElement);
		this.previousDimensionElement = previousDimensionElement;
	}

	/**
	 * @return the previousDimensionElement
	 */
	public IDimensionElement getPreviousDimensionElement() {
		return previousDimensionElement;
	}
	
	/**
	 * @return the selected dimensionElement
	 */
	public IDimensionElement getDimensionElement() {
		return (IDimensionElement)getElement();
	}
}
