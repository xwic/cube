/**
 * 
 */
package de.xwic.cube.webui.viewer;

import java.util.List;

/**
 * @author Florian Lippisch
 */
public interface INavigationElementProvider {

	/**
	 * Returns the NavigationElements.
	 * @return
	 */
	public List<INavigationElement> getNavigationElements();
	
}
