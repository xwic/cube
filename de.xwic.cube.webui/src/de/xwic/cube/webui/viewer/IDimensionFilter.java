/*
 * de.xwic.cube.webui.viewer.IDimensionFilter 
 */
package de.xwic.cube.webui.viewer;

import de.xwic.cube.IDimensionElement;

/**
 * Used to filter DimensionElements.
 * @author lippisch
 */
public interface IDimensionFilter {

	/**
	 * Returns true if the element is accepted.
	 * @param element
	 * @return
	 */
	public boolean accept(IDimensionElement element);
	
}
