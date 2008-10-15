/**
 * 
 */
package de.xwic.cube.webui.viewer;

import java.util.List;

/**
 * @author Florian Lippisch
 */
public interface INavigationProvider extends INavigationElementProvider {

	/**
	 * Returns the depth of this navigation structure.
	 * @return
	 */
	public NavigationSize getNavigationSize();

}
