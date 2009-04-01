/**
 * 
 */
package de.xwic.cube.webui.viewer;

import java.util.Comparator;

/**
 * Default Comparator that sorts by title.
 * @author lippisch
 */
public class DimensionNavigationSort implements Comparator<INavigationElement> {

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(INavigationElement o1, INavigationElement o2) {
		return o1.getTitle().compareTo(o2.getTitle());
	}

}
