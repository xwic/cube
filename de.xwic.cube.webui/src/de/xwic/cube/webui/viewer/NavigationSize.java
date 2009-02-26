/**
 * 
 */
package de.xwic.cube.webui.viewer;

/**
 * Simple type to specify the depth and columnCount of an NavigationProvider.
 * @author Florian Lippisch
 */
public class NavigationSize {

	public int depth = 0;
	public int cells = 0;
	
	/**
	 * Default Constructor.
	 */
	public NavigationSize() {
		
	}
	/**
	 * @param depth
	 * @param cells
	 */
	public NavigationSize(int depth, int cells) {
		super();
		this.depth = depth;
		this.cells = cells;
	}
	
}
