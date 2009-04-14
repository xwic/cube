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
import de.xwic.cube.ICubeListener;
import de.xwic.cube.Key;

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

public class CountLoader implements ICubeListener, Serializable {

	private static final long serialVersionUID = 1L;
	
	protected transient Map<Object, Object> mapCounts;
	protected Map<Key, Set<Object>> keyCounts;
	
	protected int measureIndex;
	protected Integer countOnMeasureIndex; 

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
		return measureIndex;
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
	
	/* (non-Javadoc)
	 * @see de.xwic.cube.ICubeListener#onCellValueChanged(de.xwic.cube.Key, de.xwic.cube.impl.Cell, int, double)
	 */
	public void onCellValueChanged(Key key, ICell cell, int measureIndex, double diff) {
		if (countOnMeasureIndex != null && measureIndex != countOnMeasureIndex) {
			// don't count on with this measure
			return;
		}
		Set<Object> objects = keyCounts.get(key);
		if (objects == null) {
			objects = new HashSet<Object>();
			keyCounts.put(key.clone(), objects);
			objects.add(countOn);
			cell.setValue(this.measureIndex, 1d);
			return;
		}
		if (objects.contains(countOn)) {
			// nothing to do
			return;
		}
		objects.add(countOn);
		
		// increase count
		Double value = cell.getValue(this.measureIndex);
		value = value != null ? value + 1d : 1d;
		cell.setValue(this.measureIndex, value);
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
	 * @param countOn the countOn to set
	 */
	public void setCountOn(Object countOn) {
		Object obj = mapCounts.get(countOn);
		if (obj == null) {
			obj = countOn;
			mapCounts.put(obj, obj);
		}
		this.countOn = obj;
	}

	/**
	 * @return the countOnMeasureIndex
	 */
	public Integer getCountOnMeasureIndex() {
		return countOnMeasureIndex;
	}

	/**
	 * @param countOnMeasureIndex the countOnMeasureIndex to set
	 */
	public void setCountOnMeasureIndex(Integer countOnMeasureIndex) {
		this.countOnMeasureIndex = countOnMeasureIndex;
	}

	/**
	 * Configures this loader to use configuration from srcLoader.
	 * @param srcLoader
	 */
	public void configure(CountLoader srcLoader) {
		if (srcLoader.keyCounts != null) {
			keyCounts = srcLoader.keyCounts;
		}
	}

}
