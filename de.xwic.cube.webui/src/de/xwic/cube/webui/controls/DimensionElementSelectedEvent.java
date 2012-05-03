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

import java.util.ArrayList;
import java.util.List;

import de.jwic.events.ElementSelectedEvent;
import de.xwic.cube.IDimensionElement;

/**
 * Created on Apr 28, 2009
 * 
 * @author jbornema
 */

public class DimensionElementSelectedEvent extends ElementSelectedEvent {

	protected List<IDimensionElement> previousDimensionElements;

	/**
	 * @param source
	 * @param selectedElement
	 */
	public DimensionElementSelectedEvent(Object source, IDimensionElement selectedDimensionElement,
			IDimensionElement previousDimensionElement) {
		super(source, selectedDimensionElement);
		this.previousDimensionElements = new ArrayList<IDimensionElement>();
		this.previousDimensionElements.add(previousDimensionElement);
	}

	/**
	 * @param source
	 * @param selectedElement
	 */
	public DimensionElementSelectedEvent(Object source, List<IDimensionElement> selectedDimensionElements,
			List<IDimensionElement> previousDimensionElements) {
		super(source, selectedDimensionElements);
		this.previousDimensionElements = previousDimensionElements;
	}

	/**
	 * @return the previousDimensionElement
	 */
	public IDimensionElement getPreviousDimensionElement() {
		return previousDimensionElements.get(0);
	}

	/**
	 * Returns a list of previous seleceted elements.
	 * 
	 * @return the previousDimensionElements.
	 */
	public List<IDimensionElement> getPreviousDimensionElements() {
		return previousDimensionElements;
	}

	/**
	 * @return the selected dimensionElement
	 */
	public IDimensionElement getDimensionElement() {
		if (super.getElement() instanceof List<?>) {

			return (IDimensionElement) ((List<?>) super.getElement()).get(0);
		}
		return (IDimensionElement) super.getElement();
	}

	/**
	 * Returns a list of selected dimensionElements in case we have a
	 * multiselector.
	 * 
	 * @return the selected dimensionElements in case we have a multiselector.
	 */
	public List<?> getDimensionElements() {
		if (super.getElement() instanceof List<?>) {
			return (List<?>) super.getElement();
		}
		return null;
	}

	/**
	 * @return Returns the element.
	 */
	public Object getElement() {
		return getDimensionElement();
	}
}
