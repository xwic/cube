/**
 * 
 */
package de.xwic.cube.webui.viewer;

import java.util.List;

/**
 * @author Florian Lippisch
 */
public interface INavigationElementProvider {
	public enum NavigationProviderTypes {NORMAL, TOTAL, EMPTY, SECTION, TOTAL_TOP, TOTAL_DARK_GREEN_ROW, TOTAL_LIGHT_GREEN_ROW};
	

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
