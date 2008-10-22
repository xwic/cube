/**
 * 
 */
package de.xwic.cube.webui.viewer;

import java.util.ArrayList;
import java.util.List;

import de.xwic.cube.IDimension;
import de.xwic.cube.IDimensionElement;

/**
 * @author Florian Lippisch
 */
public class DimensionNavigationProvider implements INavigationProvider {

	private List<IDimension> dimensions = new ArrayList<IDimension>();
	private final CubeViewerModel model;
	private List<INavigationElement> rootNavElements = new ArrayList<INavigationElement>();
	private ICubeDataProvider dataProvider = new DefaultDimensionDataProvider();

	/**
	 * Wrapper for a DimensionElement.
	 * @author Florian Lippisch
	 */
	private class DimensionNavigationElement implements INavigationElement {

		private IDimensionElement element;
		private List<INavigationElement> childs;
		/**
		 * @param element
		 */
		public DimensionNavigationElement(IDimensionElement element) {
			super();
			this.element = element;
			childs = new ArrayList<INavigationElement>();
			for (IDimensionElement elm : element.getDimensionElements()) {
				childs.add(new DimensionNavigationElement(elm));
			}
		}

		/* (non-Javadoc)
		 * @see de.xwic.cube.webui.viewer.INavigationElement#getNavigationElements()
		 */
		public List<INavigationElement> getNavigationElements() {
			return childs;
		}

		/* (non-Javadoc)
		 * @see de.xwic.cube.webui.viewer.INavigationElement#getElementId()
		 */
		public String getElementId() {
			return element.getID();
		}

		/* (non-Javadoc)
		 * @see de.xwic.cube.webui.viewer.INavigationElement#getSpan()
		 */
		public int getSpan() {
			return 1;
		}

		/* (non-Javadoc)
		 * @see de.xwic.cube.webui.viewer.INavigationElement#getTitle()
		 */
		public String getTitle() {
			return element.getTitle() == null || element.getTitle().length() == 0 ? element.getKey() : element.getTitle();
		}

		/* (non-Javadoc)
		 * @see de.xwic.cube.webui.viewer.INavigationElement#isExpandable()
		 */
		public boolean isExpandable() {
			return element.getDimensionElements().size() != 0;
		}
		
		/* (non-Javadoc)
		 * @see de.xwic.cube.webui.viewer.INavigationElement#hideTotal()
		 */
		public boolean hideTotal() {
			return false;
		}

		/* (non-Javadoc)
		 * @see de.xwic.cube.webui.viewer.INavigationElement#getElementData()
		 */
		public ContentInfo getContentInfo() {
			return new ContentInfo(dataProvider, element);
		}
		
	}
	
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
		createNavigationElements();
	}

	/**
	 * Create a DimensionProvider with one dimension.
	 * @param dimension
	 */
	public DimensionNavigationProvider(CubeViewerModel model, List<IDimension> dimensions) {
		this.model = model;
		this.dimensions.addAll(dimensions);
		createNavigationElements();
	}

	/**
	 * Create a DimensionProvider with one dimension.
	 * @param dimension
	 */
	public DimensionNavigationProvider(CubeViewerModel model, IDimension[] dimensions) {
		this.model = model;
		for (IDimension dim : dimensions) {
			this.dimensions.add(dim);
		}
		createNavigationElements();
	}

	private void createNavigationElements() {
		rootNavElements = new ArrayList<INavigationElement>();
		if (dimensions.size() > 0) {
			IDimension dim = dimensions.get(0); // first one.
			for (IDimensionElement elm : dim.getDimensionElements()) {
				rootNavElements.add(new DimensionNavigationElement(elm));
			}
		}
	}

	/* (non-Javadoc)
	 * @see de.xwic.cube.webui.viewer.INavigationProvider#getNavigationSize()
	 */
	public NavigationSize getNavigationSize() {
		
		NavigationSize size = new NavigationSize();
		size.cells = evaluateSize(size, this, 1);
		return size;
	}


	/**
	 * @param size
	 * @param dimensionNavigationProvider
	 */
	private int evaluateSize(NavigationSize size, INavigationElementProvider parent, int depth) {
		if (parent.getNavigationElements().size() > 0) {
			if (size.depth < depth) {
				size.depth = depth;
			}
		}
		int totalItems = 0;
		for (INavigationElement elm : parent.getNavigationElements()) {
			int items = 1;
			if (elm.isExpandable() && model.isExpanded(elm.getElementId())) {
				items = evaluateSize(size, elm, depth + 1); 
				if (!elm.hideTotal()) {
					items++;
				}
			}
			totalItems += items;
		}
		return totalItems;
		
	}

	/* (non-Javadoc)
	 * @see de.xwic.cube.webui.viewer.INavigationProvider#getNavigationElements()
	 */
	public List<INavigationElement> getNavigationElements() {
		return rootNavElements;
	}

}
