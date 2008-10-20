/**
 * 
 */
package de.xwic.cube.webui.viewer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import de.xwic.cube.ICube;
import de.xwic.cube.IDimension;
import de.xwic.cube.IDimensionElement;
import de.xwic.cube.IMeasure;
import de.xwic.cube.IValueFormat;
import de.xwic.cube.Key;

/**
 * Model that controls the CubeViewer.
 * 
 * @author Florian Lippisch
 */
public class CubeViewerModel {

	private enum EventType { FILTER_UPDATE }; 
		private ICube cube = null;

	private IMeasure measure = null;
	private List<INavigationProvider> rowProvider = new ArrayList<INavigationProvider>();
	private List<INavigationProvider> columnProvider = new ArrayList<INavigationProvider>();
	
	private Map<IDimension, IDimensionElement> filter = new HashMap<IDimension, IDimensionElement>(); 
	
	private Set<String> expandedElements = new HashSet<String>(); 
	
	private IValueFormat valueFormat;
	private Key baseKey = null;
	
	private List<ICubeViewerModelListener> listeners = new ArrayList<ICubeViewerModelListener>();

	private final Locale locale;
	
	/**
	 * Constructor.
	 * @param locale
	 */
	public CubeViewerModel(Locale locale) {
		this.locale = locale;
		
	}
	
	/**
	 * Add a listener.
	 * @param listener
	 */
	public synchronized void addCubeViewerModelListener(ICubeViewerModelListener listener) {
		listeners.add(listener);
	}
	
	/**
	 * Remove a listener.
	 * @param listener
	 */
	public synchronized void removeCubeViewerModelListener(ICubeViewerModelListener listener) {
		listeners.remove(listener);
	}

	/**
	 * @param filter_update
	 * @param event
	 */
	private void fireEvent(EventType eventType, CubeViewerModelEvent event) {
		ICubeViewerModelListener[] l = new ICubeViewerModelListener[listeners.size()];
		l = listeners.toArray(l);
		for (ICubeViewerModelListener listener : l) {
			switch (eventType) {
			case FILTER_UPDATE: 
				listener.filterUpdated(event);
				break;
			}
		}
	}
	
	
	/**
	 * @return the cube
	 */
	public ICube getCube() {
		return cube;
	}

	/**
	 * @param cube the cube to set
	 */
	public void setCube(ICube cube) {
		this.cube = cube;
		baseKey = cube.createKey("");
	}

	/**
	 * @return the measure
	 */
	public IMeasure getMeasure() {
		return measure;
	}

	/**
	 * @param measure the measure to set
	 */
	public void setMeasure(IMeasure measure) {
		this.measure = measure;
		fireEvent(EventType.FILTER_UPDATE, new CubeViewerModelEvent(this));
		valueFormat = measure.getValueFormatProvider().createValueFormat(locale);
	}

	/**
	 * @return the verticals
	 */
	public List<INavigationProvider> getRowProvider() {
		return rowProvider;
	}

	/**
	 * @return the horizontals
	 */
	public List<INavigationProvider> getColumnProvider() {
		return columnProvider;
	}

	/**
	 * @return
	 */
	public Key getTotalKey() {
		return baseKey.clone();
	}


	/**
	 * @return the numberFormat
	 */
	public IValueFormat getValueFormat() {
		return valueFormat;
	}

	/**
	 * @param navigationProvider
	 */
	public void addColumnNavigationProvider(INavigationProvider navigationProvider) {
		columnProvider.add(navigationProvider);
	}

	/**
	 * @param vertical
	 * @param dimension
	 */
	public void addRowNavigationProvider(INavigationProvider navigationProvider) {
		rowProvider.add(navigationProvider);
	}


	/**
	 * @return
	 */
	public Key createCursor() {
		// later: inject filter 
		Key key = baseKey.clone();
		key.setModifyable(true);
		return key;
	}


	/**
	 * @param dimVert
	 * @return
	 */
	public int getDimensionIndex(IDimension dimVert) {
		return cube.getDimensionIndex(dimVert);
	}


	/**
	 * Returns true if the specified element is expanded.
	 * @param elementId
	 * @return
	 */
	public boolean isExpanded(String elementId) {
		return expandedElements.contains(elementId);
	}
	
	/**
	 * Expand an element.
	 * @param elementId
	 */
	public void expand(String elementId) {
		expandedElements.add(elementId);
	}

	/**
	 * Collapse an element.
	 * @param elementId
	 */
	public void collapse(String elementId) {
		expandedElements.remove(elementId);
	}


	/**
	 * @param dimension
	 */
	public void applyFilter(IDimensionElement dimensionElement) {
		filter.put(dimensionElement.getDimension(), dimensionElement);
		
		updateBaseKey();
		fireEvent(EventType.FILTER_UPDATE, new CubeViewerModelEvent(this));
	}


	/**
	 * 
	 */
	private void updateBaseKey() {
		
		baseKey = cube.createKey("");
		baseKey.setModifyable(true);
		for (IDimension dim : filter.keySet()) {
			int idx = cube.getDimensionIndex(dim);
			baseKey.setDimensionElement(idx, filter.get(dim));
		}
		
	}
	
}
