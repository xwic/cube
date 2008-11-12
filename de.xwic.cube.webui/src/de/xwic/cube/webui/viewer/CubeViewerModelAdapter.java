/*
 * de.xwic.cube.webui.viewer.CubeViewerModelAdapter 
 */
package de.xwic.cube.webui.viewer;

/**
 * Adapter for CubeViewerModelListener.
 * @author lippisch
 */
public abstract class CubeViewerModelAdapter implements ICubeViewerModelListener {

	/* (non-Javadoc)
	 * @see de.xwic.cube.webui.viewer.ICubeViewerModelListener#cubeUpdated(de.xwic.cube.webui.viewer.CubeViewerModelEvent)
	 */
	@Override
	public void cubeUpdated(CubeViewerModelEvent event) {

	}

	/* (non-Javadoc)
	 * @see de.xwic.cube.webui.viewer.ICubeViewerModelListener#filterUpdated(de.xwic.cube.webui.viewer.CubeViewerModelEvent)
	 */
	@Override
	public void filterUpdated(CubeViewerModelEvent event) {

	}

	/* (non-Javadoc)
	 * @see de.xwic.cube.webui.viewer.ICubeViewerModelListener#cellSelected(de.xwic.cube.webui.viewer.CubeViewerModelEvent)
	 */
	@Override
	public void cellSelected(CubeViewerModelEvent event) {
				
	}
}
