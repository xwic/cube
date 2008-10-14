/**
 * 
 */
package de.xwic.cube.webui.viewer;

import java.util.ArrayList;
import java.util.List;

import de.xwic.cube.IDimension;

/**
 * @author Florian Lippisch
 */
public class DimensionNavigationProvider implements INavigationProvider {

	private List<IDimension> dimensions = new ArrayList<IDimension>();
	private final CubeViewerModel model;
	
	/**
	 * Constructor.
	 */
	public DimensionNavigationProvider(CubeViewerModel model) {
		this.model = model;
		
	}

	/**
	 * Create a DimensionProvider with one dimension.
	 * @param dimension
	 */
	public DimensionNavigationProvider(CubeViewerModel model, IDimension dimension) {
		this.model = model;
		dimensions.add(dimension);
	}

	/**
	 * Create a DimensionProvider with one dimension.
	 * @param dimension
	 */
	public DimensionNavigationProvider(CubeViewerModel model, List<IDimension> dimensions) {
		this.model = model;
		this.dimensions.addAll(dimensions);
	}

	

}
