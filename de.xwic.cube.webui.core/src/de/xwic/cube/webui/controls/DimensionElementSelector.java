/*
 * de.xwic.cube.webui.controls.LeafDimensionSelectorControl 
 */
package de.xwic.cube.webui.controls;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONWriter;

import de.jwic.base.IControlContainer;
import de.jwic.base.IResourceControl;
import de.jwic.base.IncludeJsOption;
import de.jwic.base.JavaScriptSupport;
import de.jwic.controls.HTMLElement;
import de.jwic.events.ElementSelectedEvent;
import de.jwic.events.ElementSelectedListener;
import de.xwic.cube.IDimension;
import de.xwic.cube.IDimensionElement;
import de.xwic.cube.webui.viewer.IDimensionFilter;

/**
 * This selector control allows the selection of a leaf DimensionElement and the
 * easy iteration over elements. It is commonly used for a time dimension.
 * 
 * 
 * @author lippisch
 */
@JavaScriptSupport
public class DimensionElementSelector extends HTMLElement implements IResourceControl {

	private IDimension dimension;
	private List<IDimensionElement> dimensionElements = new ArrayList<IDimensionElement>();
	private List<IDimensionElement> flatList = new ArrayList<IDimensionElement>();
	private boolean selectLeafsOnly = false;
	private boolean isMultiSelection = false;
	private String defaultTitle = "- All -";
	private String defaultMultipleTitle = "- Multiple -";

	private boolean showDimensionTitle = false;

	private Comparator<IDimensionElement> sortComparator = null;

	private List<ElementSelectedListener> listeners;
	{
		if (listeners == null) {
			listeners = new ArrayList<ElementSelectedListener>();
		}
	}
	private final IDimensionFilter filter;

	/**
	 * 
	 * @param container
	 * @param name
	 * @param dimension
	 * @param multiSelection
	 */
	public DimensionElementSelector(IControlContainer container, String name, IDimension dimension,
			boolean multiSelection) {
		this(container, name, dimension, null, multiSelection);
	}

	/**
	 * Constructor.
	 * 
	 * @param container
	 * @param name
	 * @param dimension
	 */
	public DimensionElementSelector(IControlContainer container, String name, IDimension dimension) {
		this(container, name, dimension, null, false);
	}

	/**
	 * 
	 * @param container
	 * @param name
	 * @param dimension
	 * @param filter
	 */
	public DimensionElementSelector(IControlContainer container, String name, IDimension dimension,
			IDimensionFilter filter) {
		this(container, name, dimension, filter, false);
	}

	/**
	 * @param container
	 * @param name
	 */
	public DimensionElementSelector(IControlContainer container, String name, IDimension dimension,
			IDimensionFilter filter, boolean multiSelection) {
		super(container, name);
		this.dimension = dimension;
		this.filter = filter;
		this.isMultiSelection = multiSelection;

		setCssClass("xcube-leafsel");

		if (dimension == null) {
			throw new NullPointerException("Dimension must not be null");
		}

		loadData();

	}

	public void loadData() {
		flatList.clear();

		IDimensionElement dimensionElement = null;

		if (dimension.isLeaf()) {
			dimensionElement = dimension;
			flatList.add(dimension);
		} else {
			addLeafs(dimension, filter);

			if (dimensionElement == null || !flatList.contains(dimensionElement)) {
				if (flatList.size() > 0) {
					dimensionElement = flatList.get(0);
					fireEvent(new ElementSelectedEvent(this, dimensionElement));
				} else {
					dimensionElement = dimension;
				}
			}
		}

		dimensionElements.clear();
		dimensionElements.add(dimensionElement);
	}

	/**
	 * @param filter
	 * @param dimension
	 */
	private void addLeafs(IDimensionElement elm, IDimensionFilter filter) {
		if (filter == null || filter.accept(elm)) {
			if (!selectLeafsOnly || elm.isLeaf()) {
				flatList.add(elm);

			}
			if (!elm.isLeaf()) {
				for (IDimensionElement de : elm.getDimensionElements()) {
					addLeafs(de, filter);
				}
			}

		}

	}

	/**
	 * Add an element selected listener.
	 * 
	 * @param listener
	 */
	public void addElementSelectedListener(ElementSelectedListener listener) {
		if (listeners == null) {
			listeners = new ArrayList<ElementSelectedListener>();
		}
		listeners.add(listener);
	}

	/**
	 * Remove an element selected listener.
	 * 
	 * @param listener
	 */
	public void removeElementSelectedListener(ElementSelectedListener listener) {
		listeners.remove(listener);
	}

	/**
	 * Fire the selection event.
	 * 
	 * @param event
	 */
	protected void fireEvent(ElementSelectedEvent event) {
		ElementSelectedListener[] lst = new ElementSelectedListener[listeners.size()];
		lst = listeners.toArray(lst);
		for (ElementSelectedListener l : lst) {
			l.elementSelected(event);
		}
	}

	/**
	 * @return the first selected dimensionElement.
	 */
	public IDimensionElement getDimensionElement() {
		return dimensionElements.get(0);
	}

	/**
	 * @return the list with selected dimensionElements.
	 */
	public List<IDimensionElement> getDimensionElements() {
		return dimensionElements;
	}

	/**
	 * @return the list with selected dimensionElements paths.
	 */
	@IncludeJsOption
	public List<String> getDimensionElementsPaths() {

		List<String> selectedPaths = new ArrayList<String>();
		for (Iterator<IDimensionElement> iterator = dimensionElements.iterator(); iterator.hasNext();) {
			IDimensionElement elem = (IDimensionElement) iterator.next();
			String path = elem.getPath();
			if(path == null || path.isEmpty()){
				continue;
			}
			selectedPaths.add(elem.getPath());
		}
		return selectedPaths;
	}
	
	/**
	 * @param dimensionElement
	 *            the dimensionElement to set
	 */
	public void setDimensionElement(IDimensionElement dimensionElement) {
		if (dimensionElements.size() == 1 && dimensionElements.get(0) == dimensionElement) {
			//no need to refresh.
			return;
		}
		List<IDimensionElement> previousDimensionElements = this.dimensionElements;
		this.dimensionElements = new ArrayList<IDimensionElement>();
		this.dimensionElements.add(dimensionElement);
		requireRedraw();
		fireEvent(new DimensionElementSelectedEvent(this, dimensionElements, previousDimensionElements));

	}

	/**
	 * Set the list of selected dimension elements.
	 * 
	 * @param dimensionElements
	 */
	public void setDimensionElements(List<IDimensionElement> dimensionElements) {
		IDimensionElement previousDimensionElements = this.dimensionElements.get(0);
		this.dimensionElements = dimensionElements;
		fireEvent(new DimensionElementSelectedEvent(this, dimensionElements.get(0), previousDimensionElements));
	}

	/**
	 * Set the list of selected dimension elements.
	 * 
	 * @param dimensionElements
	 */
	public void setDimensionElements(IDimensionElement... dimensionElements) {
		this.dimensionElements = new ArrayList<IDimensionElement>();
		for (IDimensionElement dimElem : dimensionElements) {
			this.dimensionElements.add(dimElem);
		}

	}

	/**
	 * Returns the element that follows the next element.
	 * 
	 * @return
	 */
	public IDimensionElement getNext() {
		if (!isEnabled() || this.isMultiSelection) {
			return null;
		}
		int idx = flatList.indexOf(dimensionElements.get(0));
		if (idx + 1 < flatList.size()) {
			return flatList.get(idx + 1);
		}
		return null;
	}

	/**
	 * Return the element before the current element.
	 * 
	 * @return
	 */
	public IDimensionElement getPrev() {
		if (!isEnabled() || this.isMultiSelection) {
			return null;
		}
		int idx = flatList.indexOf(dimensionElements.get(0));
		if (idx > 0) {
			return flatList.get(idx - 1);
		}
		return null;
	}

	/**
	 * Element selected.
	 * 
	 * @param path
	 */
	public void actionSelection(String path) {
		if (isMultiSelection && !path.isEmpty()) {
			// parse path for ## delimiter
			String[] paths = path.split("##");
			List<IDimensionElement> dimElements = new ArrayList<IDimensionElement>();
			for (int i = 0; i < paths.length; i++) {
				IDimensionElement elm = dimension.parsePath(paths[i]);
				dimElements.add(elm);
			}
			setDimensionElements(dimElements);
			requireRedraw();
		} else {
			IDimensionElement elm = dimension.parsePath(path);
			setDimensionElement(elm);
			requireRedraw();
		}
	}

	/**
	 * Select next leaf.
	 */
	public void actionNext() {
		IDimensionElement next = getNext();
		if (next != null) {
			setDimensionElement(next);
		}
	}

	/**
	 * Select previous leaf.
	 */
	public void actionPrev() {
		IDimensionElement prev = getPrev();
		if (prev != null) {
			setDimensionElement(prev);
		}
	}

	/**
	 * Choose last element.
	 */
	public void actionLast() {
		if (flatList.size() > 0) {
			setDimensionElement(flatList.get(flatList.size() - 1));
		}
	}

	/**
	 * Choose the first element.
	 */
	public void actionFirst() {
		if (flatList.size() > 0) {
			setDimensionElement(flatList.get(0));
		}
	}

	/**
	 * @return the dimension
	 */
	public IDimension getDimension() {
		return dimension;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seede.jwic.base.IResourceControl#attachResource(javax.servlet.http.
	 * HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public void attachResource(HttpServletRequest req, HttpServletResponse res) throws IOException {

		log.debug("Data Requested..");
		res.setContentType("text/json");
		PrintWriter pw = res.getWriter();

		// get paths of selected elements
		List<String> selectedPaths = new ArrayList<String>();
		for (Iterator<IDimensionElement> iterator = dimensionElements.iterator(); iterator.hasNext();) {
			IDimensionElement elem = (IDimensionElement) iterator.next();
			selectedPaths.add(elem.getPath());
		}
		// build object tree and send it...
		JSONWriter jw = new JSONWriter(pw);
		try {

			jw.object();
			jw.key("dimension");
			jw.value(dimension.getDimension().getKey());
			jw.key("isMulti");
			jw.value(isMultiSelection);
			jw.key("selection");
			jw.value(selectedPaths);
			jw.key("elements");
			jw.array();
			addData(jw, dimension.getDimensionElements());
			jw.endArray();

			jw.endObject();

		} catch (JSONException e) {
			res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error: " + e);
			log.error("Error while sending dimension elements...", e);
		}

		pw.close();

	}

	/**
	 * @param jw
	 * @param dimension2
	 * @throws JSONException
	 */
	private void addData(JSONWriter jw, List<IDimensionElement> list) throws JSONException {

		if (sortComparator != null) {
			// need to copy the list in order to modify it.
			List<IDimensionElement> tempList = new ArrayList<IDimensionElement>();
			tempList.addAll(list);
			Collections.sort(tempList, sortComparator);
			list = tempList;
		}

		for (IDimensionElement child : list) {
			if (filter == null || filter.accept(child)) {
				jw.object();
				jw.key("key").value(child.getKey());
				jw.key("title").value(child.getTitle());
				jw.key("elements");
				jw.array();
				addData(jw, child.getDimensionElements());
				jw.endArray();
				jw.endObject();
			}
		}
	}

	/**
	 * @return the selectLeafsOnly
	 */
	@IncludeJsOption
	public boolean isSelectLeafsOnly() {
		return selectLeafsOnly;
	}

	/**
	 * @param selectLeafsOnly
	 *            the selectLeafsOnly to set
	 */
	public void setSelectLeafsOnly(boolean selectLeafsOnly) {
		this.selectLeafsOnly = selectLeafsOnly;
		flatList = new ArrayList<IDimensionElement>();
		addLeafs(dimension, filter);

		// check selected elements to be leafs
		List<IDimensionElement> oldSelectedElems = dimensionElements;
		dimensionElements = new ArrayList<IDimensionElement>();

		for (Iterator<IDimensionElement> iterator = oldSelectedElems.iterator(); iterator.hasNext();) {
			IDimensionElement element = (IDimensionElement) iterator.next();
			if (element.isLeaf()) {
				dimensionElements.add(element);
			}

		}

		// set the first from flat list.
		if (dimensionElements.size() <= 0 && flatList.size() > 0) {
			dimensionElements.add(flatList.get(0));
		}

	}

	/**
	 * @return the showDimensionTitle
	 */
	@IncludeJsOption
	public boolean isShowDimensionTitle() {
		return showDimensionTitle;
	}

	/**
	 * @param showDimensionTitle
	 *            the showDimensionTitle to set
	 */
	public void setShowDimensionTitle(boolean showDimensionTitle) {
		this.showDimensionTitle = showDimensionTitle;
	}

	/**
	 * @return the sortComparator
	 */
	public Comparator<IDimensionElement> getSortComparator() {
		return sortComparator;
	}

	/**
	 * @param sortComparator
	 *            the sortComparator to set
	 */
	public void setSortComparator(Comparator<IDimensionElement> sortComparator) {
		this.sortComparator = sortComparator;
	}

	/**
	 * Flag for multiple selection.
	 * 
	 * @return the isMultiSelection
	 */
	@IncludeJsOption
	public boolean isMultiSelection() {
		return isMultiSelection;
	}
	
	/**
	 * @param isMultiSelection
	 */
	public void setMultiSelection(boolean isMultiSelection) {
		this.isMultiSelection = isMultiSelection;
	}
	
	/**
	 * @param defaultTitle
	 */
	public void setDefaultTitle(String defaultTitle) {
		this.defaultTitle = defaultTitle;
	}
	
	/**
	 * @param defaultMultipleTitle
	 */
	public void setDefaultMultipleTitle(String defaultMultipleTitle){
		this.defaultMultipleTitle = defaultMultipleTitle;
	}
	
	/**
	 * @return
	 */
	@IncludeJsOption
	public String getDefaultTitle() {
		return this.defaultTitle;
	}
	
	public String getDefaultMultipleTitle() {
		return defaultMultipleTitle;
	}
	
}
