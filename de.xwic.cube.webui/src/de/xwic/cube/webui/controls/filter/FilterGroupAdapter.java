package de.xwic.cube.webui.controls.filter;

/**
 * @author bogdan
 *
 */
public abstract class FilterGroupAdapter implements IFilterGroupListener {

	/* (non-Javadoc)
	 * @see de.xwic.cube.webui.controls.filter.IFilterGroupListener#preApply(de.xwic.cube.webui.controls.filter.IFilter)
	 */
	@Override
	public void preApply(IFilter filter) {
	}

	/* (non-Javadoc)
	 * @see de.xwic.cube.webui.controls.filter.IFilterGroupListener#saveFilterProfile(de.xwic.cube.webui.controls.filter.FilterGroupProfile)
	 */
	@Override
	public void saveFilterProfile(FilterGroupProfile profile) {
	}

}
