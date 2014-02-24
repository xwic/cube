package de.xwic.cube.webui.controls.filter;

import de.xwic.cube.IDimensionElement;

/**
 * @author bogdan
 *
 * @param the type of the filter
 */
public interface IFilter {
	
	/**
	 * @return
	 */
	public String getId();
	
	/**
	 * @return
	 */
	public String save();
	
	/**
	 * @param state
	 */
	public void load(String state);
	
	/**
	 * Applies this filter
	 */
	public void applyFilter();
	
	/**
	 * @return
	 */
	public boolean isInGroup();
	
	/**
	 * @param inGroup
	 */
	public void setInGroup(boolean inGroup);
}
