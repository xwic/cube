/**
 * 
 */
package de.xwic.cube.webui.viewer;


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
