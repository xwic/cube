/**
 * 
 */
package de.xwic.cube.webui.viewer;

import java.util.List;

/**
 * @author Florian Lippisch
 */
public interface INavigationElementProvider {
	public enum NavigationProviderTypes {NORMAL, TOTAL, EMPTY, SECTION, TOTAL_TOP};
	

	/**
	 * Returns the NavigationElements.
	 * @return
	 */
	public List<INavigationElement> getNavigationElements();

	/**
	 * @return
	 */
	public NavigationProviderTypes getNavigationProviderType();

}
