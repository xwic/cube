/**
 * 
 */
package de.xwic.cube.webui.viewer;

/**
 * @author Florian Lippisch
 */
public class CubeViewerModelEvent {

	private Object source;

	/**
	 * @param source
	 */
	public CubeViewerModelEvent(Object source) {
		super();
		this.source = source;
	}

	/**
	 * @return the source
	 */
	public Object getSource() {
		return source;
	}
	
}
