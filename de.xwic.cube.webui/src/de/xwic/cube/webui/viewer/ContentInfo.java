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
	private int level = Integer.MIN_VALUE;
	private boolean clickable = false;
	private String extraClickInfo = null;
	private int row = 0;
	private int col = 0;
	private String group;
	
	
	public ContentInfo(ICubeDataProvider cubeDataProvider) {
		super();
		this.cubeDataProvider = cubeDataProvider;
	}
	
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
	 * @param clickable the clickable to set
	 */
	public void setClickable(boolean clickable) {
		this.clickable = clickable;
	}
	
	public String getExtraClickInfo() {
		return extraClickInfo ;
	}

	/**
	 * Returns true if the data is clickable.
	 * @return
	 */
	public boolean isClickable() {
		return clickable ;
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

	/**
	 * @param extraClickInfo the extraClickInfo to set
	 */
	public void setExtraClickInfo(String extraClickInfo) {
		this.extraClickInfo = extraClickInfo;
	}

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public int getCol() {
		return col;
	}

	public void setCol(int col) {
		this.col = col;
	}
	
	void setGroup(String group) {
		this.group = group;
	}
	public String getGroup(){
		return this.group;
	}
	
	
}
