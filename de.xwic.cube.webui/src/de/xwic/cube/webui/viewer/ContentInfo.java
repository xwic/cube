/**
 * 
 */
package de.xwic.cube.webui.viewer;

import java.util.ArrayList;
import java.util.List;

import de.xwic.cube.IDimensionElement;

/**
 * @author Florian Lippisch
 */
public class ContentInfo {

	private List<IDimensionElement> elements = new ArrayList<IDimensionElement>();
	private Object userData = null;
	private ICubeDataProvider cubeDataProvider = null;
	private int level = 0;
	
	
	/**
	 * @param element 
	 * @param contentProvider
	 */
	public ContentInfo(ICubeDataProvider cubeDataProvider, IDimensionElement element) {
		super();
		this.cubeDataProvider = cubeDataProvider;
		this.elements.add(element);
	}

	/**
	 * @param contentProvider
	 * @param elements
	 */
	public ContentInfo(ICubeDataProvider cubeDataProvider, List<IDimensionElement> elements) {
		super();
		this.cubeDataProvider = cubeDataProvider;
		this.elements = elements;
	}
	
	/**
	 * @return the userData
	 */
	public Object getUserData() {
		return userData;
	}
	/**
	 * @param userData the userData to set
	 */
	public void setUserData(Object userData) {
		this.userData = userData;
	}
	/**
	 * @return the elements
	 */
	public List<IDimensionElement> getElements() {
		return elements;
	}

	/**
	 * @return the cubeDataProvider
	 */
	public ICubeDataProvider getCubeDataProvider() {
		return cubeDataProvider;
	}

	/**
	 * @param cubeDataProvider the cubeDataProvider to set
	 */
	public void setCubeDataProvider(ICubeDataProvider cubeDataProvider) {
		this.cubeDataProvider = cubeDataProvider;
	}

	/**
	 * @return the level
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * @param level the level to set
	 */
	public void setLevel(int level) {
		this.level = level;
	}
	
}
