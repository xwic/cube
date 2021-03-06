/**
 * 
 */
package de.xwic.cube.webui.viewer;

import de.xwic.cube.Key;

/**
 * Provides the cell data for the cube viewer. The data may be retrieved from
 * the cube or from some other source.
 * @author Florian Lippisch
 */
public interface ICubeDataProvider {

	/**
	 * Returns the priority of this data provider. The DataProvider with the highest priority is used
	 * to generate the cell data. This is required because there are 2 cubeDataProviders per cell and
	 * only one can generate the data. 
	 * @return
	 */
	public int getPriority();
	
	/**
	 * Returns the cell data.
	 * @param model
	 * @param row
	 * @param col
	 * @return
	 */
	public String getCellData(CubeViewerModel model, ContentInfo row, ContentInfo col);

	/**
	 * Used by getCellData to retrieve the cell from the cube.
	 * Null is allowed for row and col.
	 * @param model
	 * @param row
	 * @param col
	 * @return
	 */
	public Key createCursor(CubeViewerModel model, ContentInfo row, ContentInfo col);

}
