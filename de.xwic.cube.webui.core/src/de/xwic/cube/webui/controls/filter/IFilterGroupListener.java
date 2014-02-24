package de.xwic.cube.webui.controls.filter;

/**
 * @author bogdan
 *
 */
public interface IFilterGroupListener {
	/**
	 * @param filter
	 */
	public void preApply(IFilter filter);
	
	/**
	 * @param filterName
	 * @param filterState
	 */
	public void saveFilterProfile(FilterGroupProfile profile);
	
}
