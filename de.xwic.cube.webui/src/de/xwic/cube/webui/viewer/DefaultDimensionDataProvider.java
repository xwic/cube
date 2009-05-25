/**
 * 
 */
package de.xwic.cube.webui.viewer;

import de.xwic.cube.ICube;
import de.xwic.cube.IDimensionElement;
import de.xwic.cube.IMeasure;
import de.xwic.cube.IQuery;
import de.xwic.cube.Key;

/**
 * Default dimension based cube viewer.
 * @author Florian Lippisch
 */
public class DefaultDimensionDataProvider implements ICubeDataProvider {

	protected int priority = 1;
	protected IMeasure fixedMeasure = null;
	
	/* (non-Javadoc)
	 * @see de.xwic.cube.webui.viewer.ICubeDataProvider#getCellData(de.xwic.cube.webui.viewer.CubeViewerModel, de.xwic.cube.webui.viewer.ContentInfo, de.xwic.cube.webui.viewer.ContentInfo)
	 */
	public String getCellData(CubeViewerModel model, ContentInfo row, ContentInfo col) {
		
		ICube cube = model.getCube();
		Key cursor = createCursor(model, row, col);
		IMeasure measure = fixedMeasure != null ? fixedMeasure : model.getMeasure();
		Double value = null;
		if (model.getBaseQuery() == null) {
			// default value retrieval
			value = cube.getCellValue(cursor, measure);
		} else {
			// base query defined, update query based on current cursor
			IQuery query = model.getBaseQuery().clone();
			for (IDimensionElement e : cursor.getDimensionElements()) {
				query.selectDimensionElements(e);
			}
			value = cube.getCellValue(query, measure);
		}
		return value != null ? model.getValueFormat().format(value) : "";
	}

	/* (non-Javadoc)
	 * @see de.xwic.cube.webui.viewer.ICubeDataProvider#createCursor(de.xwic.cube.webui.viewer.CubeViewerModel, de.xwic.cube.webui.viewer.ContentInfo, de.xwic.cube.webui.viewer.ContentInfo)
	 */
	public Key createCursor(CubeViewerModel model, ContentInfo row, ContentInfo col) {
		ICube cube = model.getCube();
		Key cursor = model.createCursor();
		
		if (row != null) {
			for (IDimensionElement elm : row.getElements()) {
				int idx = cube.getDimensionIndex(elm.getDimension());
				cursor.setDimensionElement(idx, elm);
			}
		}
		
		if (col != null) {
			for (IDimensionElement elm : col.getElements()) {
				int idx = cube.getDimensionIndex(elm.getDimension());
				cursor.setDimensionElement(idx, elm);
			}
		}
		
		return cursor;
	}


	/* (non-Javadoc)
	 * @see de.xwic.cube.webui.viewer.ICubeDataProvider#getPriority()
	 */
	public int getPriority() {
		return priority;
	}

	/**
	 * @return the fixedMeasure
	 */
	public IMeasure getFixedMeasure() {
		return fixedMeasure;
	}

	/**
	 * @param fixedMeasure the fixedMeasure to set
	 */
	public void setFixedMeasure(IMeasure fixedMeasure) {
		this.fixedMeasure = fixedMeasure;
	}

	/**
	 * @param priority the priority to set
	 */
	public void setPriority(int priority) {
		this.priority = priority;
	}
}
