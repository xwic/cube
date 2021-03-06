/**
 * 
 */
package de.xwic.cube.webui.viewer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.xwic.cube.ICube;
import de.xwic.cube.IDimension;
import de.xwic.cube.IDimensionElement;
import de.xwic.cube.IQuery;
import de.xwic.cube.Key;

/**
 * @author Florian Lippisch
 */
public class DimensionNavigationProvider implements INavigationProvider {

	private List<IDimensionElement> dimensions = new ArrayList<IDimensionElement>();
	private final CubeViewerModel model;

	private List<INavigationElement> rootNavElements = new ArrayList<INavigationElement>();
	private ICubeDataProvider dataProvider = new DefaultDimensionDataProvider();

	private boolean hideEmptyElements = false;
	private boolean showRoot = false;
	private boolean hideTotals = false;
	private boolean clickable = false;
	private IDimensionFilter filter = null;
	private boolean hideEmptyRoot = false;
	private Comparator<INavigationElement> sortComparator = null;

	private String rootTitle = null;
	private Object contentUserData = null;

	private int indention = 0;
	private Map<IDimension, Integer> dimensionsDepth = null;

	private Set<IDimensionElement> contentFilter = new HashSet<IDimensionElement>();

	protected boolean navInitialized = false;

	private String extraClickInformation = null;

	private String cssCellClass = "";

	/**
	 * Used to chain multiple dimensions in one navigation.
	 * 
	 * @author Lippisch
	 */
	private class DimensionChain {
		private int position;
		private final DimensionChain parent;

		/**
		 * @return the parent
		 */
		public DimensionChain getParent() {
			return parent;
		}

		/**
		 * @return the element
		 */
		public IDimensionElement getElement() {
			return element;
		}

		private final IDimensionElement element;

		/**
		 * @param position
		 * @param element
		 * @param parent
		 */
		public DimensionChain(int position, DimensionChain parent, IDimensionElement element) {
			super();
			this.position = position;
			this.parent = parent;
			this.element = element;
		}

		/**
		 * Default constructor starts at the first dimension.
		 */
		public DimensionChain() {
			position = 0;
			parent = null;
			element = null;
		}

		public boolean hasNextDimension() {
			return position + 1 < dimensions.size();
		}

		/**
		 * @return
		 */
		public DimensionChain nextDimensionChain(IDimensionElement element) {
			return new DimensionChain(position + 1, this, element);
		}

		/**
		 * Returns the current dimension in the chain.
		 * 
		 * @return
		 */
		public IDimensionElement getNext() {
			return dimensions.get(position);
		}
	}

	/**
	 * Wrapper for a DimensionElement.
	 * 
	 * @author Florian Lippisch
	 */
	private class DimensionNavigationElement implements INavigationElement {

		private IDimensionElement element;
		private List<INavigationElement> childs;
		private final DimensionChain chain;
		private String fixedTitle = null;

		/**
		 * @param element
		 */
		public DimensionNavigationElement(IDimensionElement element, DimensionChain chain) {
			super();
			this.element = element;
			this.chain = chain;
			childs = new ArrayList<INavigationElement>();
			boolean limitDepth = false;
			if (dimensionsDepth != null) {
				// check if at a specified maximum depth level the navigation
				// has to stop
				Integer depth = dimensionsDepth.get(element.getDimension());
				limitDepth = depth != null && element.getDepth() > depth;
			}
			if (element.getDimensionElements().size() != 0 && !limitDepth) {
				for (IDimensionElement elm : element.getDimensionElements()) {
					if ((filter == null || filter.accept(elm)) && (!hideEmptyElements || !isEmpty(elm, chain))) {
						childs.add(new DimensionNavigationElement(elm, chain));
					}
				}
			} else if (chain.hasNextDimension()) {
				// if a leaf is reached, and another dimension is available, add
				// the
				// childs of this dimension. The dimension itself is not added,
				// as it
				// is the same as the leaf element.
				DimensionChain next = chain.nextDimensionChain(element);
				for (IDimensionElement elm : next.getNext().getDimensionElements()) {
					if ((filter == null || filter.accept(elm)) && (!hideEmptyElements || !isEmpty(elm, next))) {
						childs.add(new DimensionNavigationElement(elm, next));
					}
				}

			}
			if (sortComparator != null) {
				Collections.sort(childs, sortComparator);
			}

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * de.xwic.cube.webui.viewer.INavigationElement#getNavigationElements()
		 */
		public List<INavigationElement> getNavigationElements() {
			return childs;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see de.xwic.cube.webui.viewer.INavigationElement#getElementId()
		 */
		public String getElementId() {
			return element.getID();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see de.xwic.cube.webui.viewer.INavigationElement#getSpan()
		 */
		public int getSpan() {
			return 1;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see de.xwic.cube.webui.viewer.INavigationElement#getTitle()
		 */
		public String getTitle() {
			return fixedTitle != null ? fixedTitle
					: (element.getTitle() == null || element.getTitle().length() == 0 ? element.getKey() : element
							.getTitle());
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see de.xwic.cube.webui.viewer.INavigationElement#isExpandable()
		 */
		public boolean isExpandable() {
			return childs.size() != 0;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see de.xwic.cube.webui.viewer.INavigationElement#hideTotal()
		 */
		public boolean hideTotal() {
			return hideTotals;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see de.xwic.cube.webui.viewer.INavigationElement#getElementData()
		 */
		public ContentInfo getContentInfo() {
			// build list of elements from the chain
			return createContentInfo(element, chain);

		}

		private ContentInfo createContentInfo(IDimensionElement dimElement, DimensionChain dimChain) {
			List<IDimensionElement> elements = new ArrayList<IDimensionElement>();
			elements.add(dimElement);
			DimensionChain dc = dimChain;
			while (dc != null) {
				if (dc.getElement() != null) {
					elements.add(dc.getElement());
				}
				dc = dc.getParent();
			}
			// add custom content filter
			// ICube cube = model.getCube();
			for (IDimensionElement elm : getContentFilter()) {
				// int idx = cube.getDimensionIndex(elm.getDimension());
				elements.add(elm);
			}

			ContentInfo contentInfo = new ContentInfo(dataProvider, elements);
			contentInfo.setClickable(clickable);
			contentInfo.setUserData(contentUserData);
			contentInfo.setExtraClickInfo(getExtraClickInformation());
			return contentInfo;
		}

		/**
		 * @return the fixedTitle
		 */
		public String getFixedTitle() {
			return fixedTitle;
		}

		/**
		 * @param fixedTitle
		 *            the fixedTitle to set
		 */
		public void setFixedTitle(String fixedTitle) {
			this.fixedTitle = fixedTitle;
		}

		@Override
		public NavigationProviderTypes getNavigationProviderType() {
			return NavigationProviderTypes.NORMAL;
		}

	}

	/**
	 * Constructor.
	 */
	public DimensionNavigationProvider(CubeViewerModel model) {
		this.model = model;
		initialize();
	}

	/**
	 * Create a DimensionProvider with one dimension.
	 * 
	 * @param dimension
	 */
	public DimensionNavigationProvider(CubeViewerModel model, List<IDimensionElement> dimensions) {
		this.model = model;
		this.dimensions.addAll(dimensions);
		initialize();
	}

	/**
	 * Create a DimensionProvider with one dimension.
	 * 
	 * @param dimension
	 */
	public DimensionNavigationProvider(CubeViewerModel model, IDimensionElement... dimensions) {
		this.model = model;
		for (IDimensionElement dim : dimensions) {
			this.dimensions.add(dim);
		}
		initialize();
	}

	/**
	 * Create a DimensionProvider with one dimension.
	 * 
	 * @param dimension
	 */
	public DimensionNavigationProvider(CubeViewerModel model, IDimensionFilter filter, IDimensionElement... dimensions) {
		this.model = model;
		this.filter = filter;
		for (IDimensionElement dim : dimensions) {
			this.dimensions.add(dim);
		}
		initialize();
	}

	/**
	 * @param elm
	 * @param chain2
	 * @return
	 */
	private boolean isEmpty(IDimensionElement elm, DimensionChain dimChain) {
		if (model.getMeasure() == null) {
			return true;
		}
		ICube cube = model.getCube();
		// Key cursor = model.createCursor();
		Key cursor = dataProvider.createCursor(model, null, null);
		if (cursor == null) {
			// the dataProvider is a special implementation that does not use
			// cube data as it seems.
			return false;
		}

		Double cellValue = null;
		IQuery baseQuery = model.getBaseQuery();

		// add content filter as well
		for (IDimensionElement de : getContentFilter()) {
			int idx = cube.getDimensionIndex(de.getDimension());
			cursor.setDimensionElement(idx, de);
		}

		int idx = cube.getDimensionIndex(elm.getDimension());
		cursor.setDimensionElement(idx, elm);
		DimensionChain dc = dimChain;
		while (dc != null) {
			if (dc.getElement() != null) {
				idx = cube.getDimensionIndex(dc.getElement().getDimension());
				cursor.setDimensionElement(idx, dc.getElement());
			}
			dc = dc.getParent();
		}

		if (null == baseQuery) {
			cellValue = cube.getCellValue(cursor, model.getMeasure());
		} else {
			// get cell using query
			IQuery query = cube.createQuery(cursor);
			Set<IDimension> selectedDimensions = baseQuery.getSelectedDimensions();
			for (Iterator<IDimension> iterator = selectedDimensions.iterator(); iterator.hasNext();) {
				IDimension dimension = (IDimension) iterator.next();
				query.clear(dimension);
				Set<IDimensionElement> dimElemSelections = baseQuery.getSelectedDimensionElements(dimension);
				if (dimElemSelections != null) {
					query.selectDimensionElements(dimElemSelections.toArray(new IDimensionElement[dimElemSelections
							.size()]));
				}
			}
			cellValue = cube.getCellValue(query, model.getMeasure());

		}

		return cellValue == null;
	}

	/**
	 * 
	 */
	private void initialize() {
		// FLI: Do not initialize the navigation elements immediately - wait for
		// other parameters to be set...
		// createNavigationElements();
		navInitialized = false;
		model.addCubeViewerModelListener(new CubeViewerModelAdapter() {
			@Override
			public void cubeUpdated(CubeViewerModelEvent event) {
				createNavigationElements();
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * de.xwic.cube.webui.viewer.ICubeViewerModelListener#filterUpdated
			 * (de.xwic.cube.webui.viewer.CubeViewerModelEvent)
			 */
			@Override
			public void filterUpdated(CubeViewerModelEvent event) {
				createNavigationElements();
			}
		});
	}

	/**
	 * (Re-)creates the navigation elements based on the current settings.
	 */
	public void createNavigationElements() {
		rootNavElements = new ArrayList<INavigationElement>();
		DimensionChain chain = new DimensionChain();
		if (dimensions.size() > 0) {
			IDimensionElement dim = dimensions.get(0); // first one.
			if (showRoot && (!hideEmptyRoot || !isEmpty(dim, chain))) {
				DimensionNavigationElement e = new DimensionNavigationElement(dim, chain);
				e.setFixedTitle(rootTitle);
				rootNavElements.add(e);
			} else {
				for (IDimensionElement elm : dim.getDimensionElements()) {
					if ((filter == null || filter.accept(elm)) && (!hideEmptyElements || !isEmpty(elm, chain))) {
						rootNavElements.add(new DimensionNavigationElement(elm, chain));
					}
				}
			}
		}
		if (sortComparator != null) {
			Collections.sort(rootNavElements, sortComparator);
		}
		navInitialized = true;
	}

	/**
	 * @return the hideEmptyElements
	 */
	public boolean isHideEmptyElements() {
		return hideEmptyElements;
	}

	/**
	 * @param hideEmptyElements
	 *            the hideEmptyElements to set
	 */
	public void setHideEmptyElements(boolean hideEmptyElements) {
		this.hideEmptyElements = hideEmptyElements;
		// force re-initialization
		navInitialized = false;
	}

	/**
	 * @return the showDimension
	 */
	public boolean isShowRoot() {
		return showRoot;
	}

	/**
	 * @param showDimension
	 *            the showDimension to set
	 */
	public void setShowRoot(boolean showDimension) {
		this.showRoot = showDimension;
		navInitialized = false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.xwic.cube.webui.viewer.INavigationProvider#getNavigationSize()
	 */
	public NavigationSize getNavigationSize() {

		if (!navInitialized) {
			createNavigationElements();
		}

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.xwic.cube.webui.viewer.INavigationProvider#getNavigationElements()
	 */
	public List<INavigationElement> getNavigationElements() {
		if (!navInitialized) {
			createNavigationElements();
		}
		
		return rootNavElements;
	}

	/**
	 * @return the hideTotals
	 */
	public boolean isHideTotals() {
		return hideTotals;
	}

	/**
	 * @param hideTotals
	 *            the hideTotals to set
	 */
	public void setHideTotals(boolean hideTotals) {
		this.hideTotals = hideTotals;
	}

	/**
	 * @return the clickable
	 */
	public boolean isClickable() {
		return clickable;
	}

	/**
	 * Set the dimensions to be clickable. Note that a cell is only clickable,
	 * if both row and column providers are clickable!
	 * 
	 * @param clickable
	 *            the clickable to set
	 */
	public void setClickable(boolean clickable) {
		this.clickable = clickable;
	}

	/**
	 * @return the indention
	 */
	public int getIndention() {
		return indention;
	}

	/**
	 * @param indention
	 *            the indention to set
	 */
	public void setIndention(int indention) {
		this.indention = indention;
	}

	/**
	 * @return the filter
	 */
	public IDimensionFilter getFilter() {
		return filter;
	}

	/**
	 * @param filter
	 *            the filter to set
	 */
	public void setFilter(IDimensionFilter filter) {
		this.filter = filter;
		navInitialized = false;
	}

	/**
	 * @return the dataProvider
	 */
	public ICubeDataProvider getDataProvider() {
		return dataProvider;
	}

	/**
	 * @param hideEmptyRoot
	 *            the hideEmptyRoot to set
	 */
	public void setHideEmptyRoot(boolean hideEmptyRoot) {
		this.hideEmptyRoot = hideEmptyRoot;
		// force re-initialization
		navInitialized = false;
	}

	/**
	 * @param dataProvider
	 *            the dataProvider to set
	 */
	public void setDataProvider(ICubeDataProvider dataProvider) {
		this.dataProvider = dataProvider;
	}

	/**
	 * @return the rootTitle
	 */
	public String getRootTitle() {
		return rootTitle;
	}

	/**
	 * @param rootTitle
	 *            the rootTitle to set
	 */
	public void setRootTitle(String rootTitle) {
		this.rootTitle = rootTitle;
		if (showRoot) {
			navInitialized = false;
		}
	}

	/**
	 * @return the hideEmptyRoot
	 */
	protected boolean isHideEmptyRoot() {
		return hideEmptyRoot;
	}

	/**
	 * @return the contentFilter
	 */
	public Set<IDimensionElement> getContentFilter() {
		return contentFilter;
	}

	/**
	 * @return the dimensions
	 */
	public List<IDimensionElement> getDimensions() {
		return dimensions;
	}

	/**
	 * @return the dimensionsDepth
	 */
	public Map<IDimension, Integer> getDimensionsDepth() {
		return dimensionsDepth;
	}

	/**
	 * Add a maximum depth for a dimension (inclusively). Calls
	 * createNavigationElements() method to create updated structure.
	 * 
	 * @param dimension
	 * @param depth
	 */
	public void addDimensionsDepth(IDimensionElement dimension, int depth) {
		if (dimensionsDepth == null) {
			dimensionsDepth = new HashMap<IDimension, Integer>(dimensions.size());
		}
		dimensionsDepth.put(dimension.getDimension(), depth);
		navInitialized = false;
	}

	/**
	 * @return the sortComparator
	 */
	public Comparator<INavigationElement> getSortComparator() {
		return sortComparator;
	}

	/**
	 * @param sortComparator
	 *            the sortComparator to set
	 */
	public void setSortComparator(Comparator<INavigationElement> sortComparator) {
		this.sortComparator = sortComparator;
	}

	/**
	 * @return the contentUserData
	 */
	public Object getContentUserData() {
		return contentUserData;
	}

	/**
	 * @param contentUserData
	 *            the contentUserData to set
	 */
	public void setContentUserData(Object contentUserData) {
		this.contentUserData = contentUserData;
	}

	/**
	 * @return the cubeviewer model
	 */
	public CubeViewerModel getModel() {
		return model;
	}

	/**
	 * @return the extraClickInformation
	 */
	public String getExtraClickInformation() {
		return extraClickInformation;
	}

	/**
	 * @param extraClickInformation
	 *            the extraClickInformation to set
	 */
	public void setExtraClickInformation(String extraClickInformation) {
		this.extraClickInformation = extraClickInformation;
	}

	/**
	 * Replace existing dimensions.
	 * 
	 * @param dimensions
	 */
	public void setDimensions(IDimensionElement... dimensions) {
		this.dimensions.clear();
		for (IDimensionElement elm : dimensions) {
			this.dimensions.add(elm);
		}
		navInitialized = false;
	}

	@Override
	public NavigationProviderTypes getNavigationProviderType() {
		return NavigationProviderTypes.NORMAL;
	}

	public String getCssCellClass() {
		return cssCellClass;
	}

	public void setCssCellClass(String cssCellClass) {
		this.cssCellClass = cssCellClass;
	}
	
	
}
