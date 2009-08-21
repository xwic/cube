/*
 * de.xwic.cube.webui.controls.LeafDimensionSelectorControl 
 */
package de.xwic.cube.webui.controls;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONWriter;

import de.jwic.base.IControlContainer;
import de.jwic.base.IResourceControl;
import de.jwic.controls.HTMLElement;
import de.jwic.events.ElementSelectedEvent;
import de.jwic.events.ElementSelectedListener;
import de.xwic.cube.IDimension;
import de.xwic.cube.IDimensionElement;
import de.xwic.cube.webui.viewer.IDimensionFilter;

/**
 * This selector control allows the selection of a leaf DimensionElement 
 * and the easy iteration over elements. It is commonly used for a 
 * time dimension.
 * 
 * 
 * @author lippisch
 */
public class DimensionElementSelector extends HTMLElement implements IResourceControl{

	private IDimension dimension;
	private IDimensionElement dimensionElement = null;
	private List<IDimensionElement> flatList = new ArrayList<IDimensionElement>();
	private boolean selectLeafsOnly = false;
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
	 * Constructor.
	 * @param container
	 * @param name
	 * @param dimension
	 */
	public DimensionElementSelector(IControlContainer container, String name, IDimension dimension) {
		this(container, name, dimension, null);
	}
	/**
	 * @param container
	 * @param name
	 */
	public DimensionElementSelector(IControlContainer container, String name, IDimension dimension, IDimensionFilter filter) {
		super(container, name);
		this.dimension = dimension;
		this.filter = filter;
		
		setCssClass("xcube-leafsel");
		
		if (dimension == null) {
			throw new NullPointerException("Dimension must not be null");
		}
		
		loadData();
		
	}
	
	public void loadData() {
		flatList.clear();
		
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
	 * @param listener
	 */
	public void removeElementSelectedListener(ElementSelectedListener listener) {
		listeners.remove(listener);
	}
	
	/**
	 * Fire the selection event.
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
	 * @return the dimensionElement
	 */
	public IDimensionElement getDimensionElement() {
		return dimensionElement;
	}

	/**
	 * @param dimensionElement the dimensionElement to set
	 */
	public void setDimensionElement(IDimensionElement dimensionElement) {
		IDimensionElement previousDimensionElement = this.dimensionElement;
		this.dimensionElement = dimensionElement;
		requireRedraw();
		fireEvent(new DimensionElementSelectedEvent(this, dimensionElement, previousDimensionElement));
	}

	/**
	 * Returns the element that follows the next element.
	 * @return
	 */
	public IDimensionElement getNext() {
		int idx = flatList.indexOf(dimensionElement);
		if (idx + 1 < flatList.size()) {
			return flatList.get(idx + 1);
		} 
		return null;
	}
	
	/**
	 * Return the element before the current element.
	 * @return
	 */
	public IDimensionElement getPrev() {
		int idx = flatList.indexOf(dimensionElement);
		if (idx > 0) {
			return flatList.get(idx - 1);
		}
		return null;
	}
	
	/**
	 * Element selected.
	 * @param path
	 */
	public void actionSelection(String path) {
		IDimensionElement elm = dimension.parsePath(path);
		setDimensionElement(elm);
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


	/* (non-Javadoc)
	 * @see de.jwic.base.IResourceControl#attachResource(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public void attachResource(HttpServletRequest req, HttpServletResponse res) throws IOException {
		
		log.debug("Data Requested..");
		res.setContentType("text/json");
		PrintWriter pw = res.getWriter();
		
		// build object tree and send it...
		
		JSONWriter jw = new JSONWriter(pw);
		try {
			
			jw.object();
			jw.key("dimension");
			jw.value(dimension.getDimension().getKey());
			jw.key("selection").value(dimensionElement != null ? dimensionElement.getPath() : null);
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
	public boolean isSelectLeafsOnly() {
		return selectLeafsOnly;
	}
	/**
	 * @param selectLeafsOnly the selectLeafsOnly to set
	 */
	public void setSelectLeafsOnly(boolean selectLeafsOnly) {
		this.selectLeafsOnly = selectLeafsOnly;
		flatList = new ArrayList<IDimensionElement>();
		addLeafs(dimension, filter);
		
		if (dimensionElement != null && !dimensionElement.isLeaf() && flatList.size() > 0) {
			dimensionElement = flatList.get(0);
		}
		
	}
	/**
	 * @return the showDimensionTitle
	 */
	public boolean isShowDimensionTitle() {
		return showDimensionTitle;
	}
	/**
	 * @param showDimensionTitle the showDimensionTitle to set
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
	 * @param sortComparator the sortComparator to set
	 */
	public void setSortComparator(Comparator<IDimensionElement> sortComparator) {
		this.sortComparator = sortComparator;
	}

}
