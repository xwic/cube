/*
 * de.xwic.cube.webui.controls.LeafDimensionSelectorControl 
 */
package de.xwic.cube.webui.controls;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;

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
public class LeafDimensionSelectorControl extends HTMLElement implements IResourceControl{

	private IDimensionElement dimensionElement = null;
	private List<IDimensionElement> flatList = new ArrayList<IDimensionElement>();
	
	private List<ElementSelectedListener> listeners = new ArrayList<ElementSelectedListener>();
	
	/**
	 * Constructor.
	 * @param container
	 * @param name
	 * @param dimension
	 */
	public LeafDimensionSelectorControl(IControlContainer container, String name, IDimension dimension) {
		this(container, name, dimension, null);
	}
	/**
	 * @param container
	 * @param name
	 */
	public LeafDimensionSelectorControl(IControlContainer container, String name, IDimension dimension, IDimensionFilter filter) {
		super(container, name);
		
		setCssClass("xcube-leafsel");
		
		if (dimension == null) {
			throw new NullPointerException("Dimension must not be null");
		}
		
		if (dimension.isLeaf()) {
			dimensionElement = dimension;
			flatList.add(dimension);
 		} else {
 			addLeafs(dimension, filter);
 			if (flatList.size() > 0) {
 				dimensionElement = flatList.get(0);
 			}
 		}
		
	}
	/**
	 * @param filter 
	 * @param dimension
	 */
	private void addLeafs(IDimensionElement elm, IDimensionFilter filter) {
		if (filter == null || filter.accept(elm)) {
			if (elm.isLeaf()) {
				flatList.add(elm);
			} else {
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
		this.dimensionElement = dimensionElement;
		requireRedraw();
		fireEvent(new ElementSelectedEvent(this, dimensionElement));
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
	
	/* (non-Javadoc)
	 * @see de.jwic.base.IResourceControl#attachResource(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public void attachResource(HttpServletRequest req, HttpServletResponse res) throws IOException {
		
		res.setContentType("text/json");
		PrintWriter pw = res.getWriter();
		
		JSONArray ja = new JSONArray();
		for (IDimensionElement de : flatList) {
			ja.put(de.getKey());
		}
		
		
		pw.println(ja);
		
		pw.close();
		res.getOutputStream().close();
		
	}
	
}
