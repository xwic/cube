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
	 * @param profile
	 * @throws Exception  - if something went wrong in the save process
	 */
	public void saveFilterProfile(FilterGroupProfile profile) throws FilterGroupException;
	
}
