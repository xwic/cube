/**
 * $Id: $
 *
 * Copyright (c) 2009 NetApp.
 * All rights reserved.

 * de.xwic.cube.util.CountLoader.java
 * Created on Apr 8, 2009
 * 
 * @author JBORNEMA
 */
package de.xwic.cube.util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.xwic.cube.ICell;
import de.xwic.cube.ICube;
import de.xwic.cube.ICubeListener;
import de.xwic.cube.IMeasure;
import de.xwic.cube.IMeasureLoader;
import de.xwic.cube.Key;
import de.xwic.cube.event.CellAggregatedEvent;
import de.xwic.cube.event.CellValueChangedEvent;
import de.xwic.cube.impl.AbstractCubeListener;

/**
 * CountLoader counts distinct objects and stores the count number in the cell for specified measure.
 *
 * Invoke setCountOn(Object) before changing cube cell values. 
 *
 * Check serialization, might allocate a lot of RAM.
 *  
 * Created on Apr 8, 2009
 * @author JBORNEMA
 */

public class CountLoader extends AbstractCubeListener implements IMeasureLoader, ICubeListener, Serializable {

	private static final long serialVersionUID = 1L;
	
	protected transient Map<Object, Object> mapCounts;
	protected Map<Key, Set<Object>> keyCounts;
	
	protected int measureIndex;
	protected int countOnMeasureIndex;
	protected transient String countOnMeasureKey;

	protected transient Object countOn;
	
	{
		mapCounts = new HashMap<Object, Object>();
		keyCounts = new HashMap<Key, Set<Object>>();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return getClass().hashCode() * 31 + measureIndex;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj == null || obj.getClass() != getClass()) {
			return false;
		}
		CountLoader other = (CountLoader)obj;
		return other.measureIndex == measureIndex;
	}
	
	/**
	 * Returns the Set where the countOn objects are place
	 * @param key
	 * @param createNew
	 * @return
	 */
	protected Set<Object> getCounts(Key key, ICell cell, boolean createNew) {
		Set<Object> objects = keyCounts.get(key);
		if (createNew && objects == null) {
			objects = new HashSet<Object>();
			keyCounts.put(key.clone(), objects);
		}
		return objects;
	}
	
	/* (non-Javadoc)
	 * @see de.xwic.cube.impl.AbstractCubeListener#onCellValueChanged(de.xwic.cube.event.CellValueChangedEvent)
	 */
	public void onCellValueChanged(CellValueChangedEvent event) {
		Key key = event.getKey();
		ICell cell = event.getCell();
		int mIndex = event.getMeasureIndex();
		if (mIndex != countOnMeasureIndex) {
			// don't count on this measure
			return;
		}
		Set<Object> objects = getCounts(key, cell, true);
		
		// add countOn object
		objects.add(countOn);

		// set count in cell
		cell.setValue(measureIndex, (double)objects.size());
	}
	
	/* (non-Javadoc)
	 * @see de.xwic.cube.impl.AbstractCubeListener#onCellAggregated(de.xwic.cube.event.CellAggregatedEvent)
	 */
	@Override
	public void onCellAggregated(CellAggregatedEvent event) {
		Key childKey = event.getChildKey();
		Key parentKey = event.getParentKey();
		ICell parentCell = event.getParentCell();
		ICell childCell = event.getChildCell();
		Set<Object> childObjects = getCounts(childKey, childCell, false);
		if (childObjects == null) {
			// nothing to count
			return;
		}
		
		Set<Object> objects = getCounts(parentKey, parentCell, true);
		objects.addAll(childObjects);
		// set count
		parentCell.setValue(measureIndex, (double)objects.size());
	}

	/**
	 * @return the measureIndex
	 */
	public int getMeasureIndex() {
		return measureIndex;
	}

	/**
	 * @param measureIndex the measureIndex to set
	 */
	public void setMeasureIndex(int measureIndex) {
		this.measureIndex = measureIndex;
	}

	/**
	 * @return the countOn
	 */
	public Object getCountOn() {
		return countOn;
	}

	/**
	 * Set object count is applied on. Ensure it is set before cube values are set.
	 * @see de.xwic.cube.IMeasureLoader#setObjectFocus(java.lang.Object)
	 */
	public void setObjectFocus(Object objectFocus) {
		Object obj = mapCounts.get(objectFocus);
		if (obj == null) {
			obj = objectFocus;
			mapCounts.put(obj, obj);
		}
		this.countOn = obj;
	}

	/**
	 * @return the countOnMeasureIndex
	 */
	public int getCountOnMeasureIndex() {
		return countOnMeasureIndex;
	}

	/**
	 * @param countOnMeasureIndex the countOnMeasureIndex to set
	 */
	public void setCountOnMeasureIndex(int countOnMeasureIndex) {
		this.countOnMeasureIndex = countOnMeasureIndex;
	}

	/**
	 * Configures this loader to use configuration from srcLoader.
	 * @param srcLoader
	 */
	public void configure(IMeasureLoader fromLoader) {
		if (!(fromLoader instanceof CountLoader)) {
			return;
		}
		CountLoader srcLoader = (CountLoader)fromLoader;
		if (srcLoader.keyCounts != null) {
			keyCounts = srcLoader.keyCounts;
		}
	}

	/**
	 * Clear the loader
	 */
	public void clear() {
		countOn = null;
		mapCounts.clear();
		keyCounts.clear();
	}

	/* (non-Javadoc)
	 * @see de.xwic.cube.IMeasureLoader#isExtension()
	 */
	public boolean isExtension() {
		// count logic is completely different to existing sum aggregation
		return false;
	}
	
	/* (non-Javadoc)
	 * @see de.xwic.cube.IMeasureLoader#accept(de.xwic.cube.ICube, de.xwic.cube.Key, de.xwic.cube.IMeasure, java.lang.Double)
	 */
	public boolean accept(ICube cube, Key key, IMeasure measure, Double value) {
		if (countOnMeasureKey == null) {
			int idx = cube.getMeasureIndex(measure);
			if (idx == countOnMeasureIndex) {
				countOnMeasureKey = measure.getKey();
			}
		}
		return countOnMeasureKey != null && countOnMeasureKey.equals(measure.getKey());
	}
}
