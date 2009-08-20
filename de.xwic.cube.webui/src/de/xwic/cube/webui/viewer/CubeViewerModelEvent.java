/**
 * 
 */
package de.xwic.cube.webui.viewer;

import de.xwic.cube.Key;

/**
 * @author Florian Lippisch
 */
public class CubeViewerModelEvent {

	private Object source;
	private Key selectionKey = null;
	private String[] selectionArguments = null;
	private String elementId;

	
	
	/**
	 * @param source
	 * @param elementId
	 */
	public CubeViewerModelEvent(Object source, String elementId) {
		super();
		this.source = source;
		this.elementId = elementId;
	}

	/**
	 * @param source
	 * @param selectionKey
	 * @param selectionArguments
	 */
	public CubeViewerModelEvent(Object source, Key selectionKey, String[] selectionArguments) {
		super();
		this.source = source;
		this.selectionKey = selectionKey;
		this.selectionArguments = selectionArguments;
	}

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

	/**
	 * @return the selectionKey
	 */
	public Key getSelectionKey() {
		return selectionKey;
	}

	/**
	 * @return the selectionArguments
	 */
	public String[] getSelectionArguments() {
		return selectionArguments;
	}

	/**
	 * @return the elementId
	 */
	public String getElementId() {
		return elementId;
	}

}
