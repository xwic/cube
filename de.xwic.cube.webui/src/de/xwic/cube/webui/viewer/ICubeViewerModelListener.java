/**
 * 
 */
package de.xwic.cube.webui.viewer;

/**
 * @author Florian Lippisch
 */
public interface ICubeViewerModelListener {

	public void filterUpdated(CubeViewerModelEvent event);

	/**
	 * @param event
	 */
	public void cubeUpdated(CubeViewerModelEvent event);

	/**
	 * A cell has been selected.
	 * @param event
	 */
	public void cellSelected(CubeViewerModelEvent event);
	
}
