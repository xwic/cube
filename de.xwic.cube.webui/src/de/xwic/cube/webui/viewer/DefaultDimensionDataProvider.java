/**
 * 
 */
package de.xwic.cube.webui.viewer;

import de.xwic.cube.ICube;
import de.xwic.cube.IDimensionElement;
import de.xwic.cube.Key;

/**
 * Default dimension based cube viewer.
 * @author Florian Lippisch
 */
public class DefaultDimensionDataProvider implements ICubeDataProvider {

	/* (non-Javadoc)
	 * @see de.xwic.cube.webui.viewer.ICubeDataProvider#getCellData(de.xwic.cube.webui.viewer.CubeViewerModel, de.xwic.cube.webui.viewer.ContentInfo, de.xwic.cube.webui.viewer.ContentInfo)
	 */
	public String getCellData(CubeViewerModel model, ContentInfo row, ContentInfo col) {
		
		ICube cube = model.getCube();
		Key cursor = createKey(model, row, col);
		Double value = cube.getCellValue(cursor, model.getMeasure());
		return value != null ? model.getValueFormat().format(value) : "";
	}
	
	/**
	 * @param model
	 * @param object
	 * @return
	 */
	protected Key createKey(CubeViewerModel model, ContentInfo row, ContentInfo col) {
		ICube cube = model.getCube();
		Key cursor = model.createCursor();
		
		for (IDimensionElement elm : row.getElements()) {
			int idx = cube.getDimensionIndex(elm.getDimension());
			cursor.setDimensionElement(idx, elm);
		}

		for (IDimensionElement elm : col.getElements()) {
			int idx = cube.getDimensionIndex(elm.getDimension());
			cursor.setDimensionElement(idx, elm);
		}
		return cursor;
	}


	/* (non-Javadoc)
	 * @see de.xwic.cube.webui.viewer.ICubeDataProvider#getPriority()
	 */
	public int getPriority() {
		return 1;
	}

}
