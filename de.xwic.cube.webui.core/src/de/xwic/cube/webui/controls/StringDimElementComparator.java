/**
 * 
 */
package de.xwic.cube.webui.controls;

import java.util.Comparator;

import de.xwic.cube.IDimensionElement;

/**
 * Simple String compare of Dimension KEY!
 * 
 * @author ronny
 *
 */
public class StringDimElementComparator implements Comparator<IDimensionElement> {

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(IDimensionElement o1, IDimensionElement o2) {
		if (o1 == null && o2 == null) {
			return 0;
		}
		if (o1 == null) {
			return -1;
		}
		if (o2 == null) {
			return 1;
		}
		
		return o1.getKey().compareTo(o2.getKey());
	}
}
