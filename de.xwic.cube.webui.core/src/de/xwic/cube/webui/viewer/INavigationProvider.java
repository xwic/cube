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
	
	/**
	 * Returns how many cells the elements should be indented. The default
	 * value should be 0.
	 * @return
	 */
	public int getIndention();
	
}
