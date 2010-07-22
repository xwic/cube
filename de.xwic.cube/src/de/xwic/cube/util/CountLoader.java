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
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import de.xwic.cube.ICell;
import de.xwic.cube.ICellLoader;
import de.xwic.cube.ICube;
import de.xwic.cube.ICubeListener;
import de.xwic.cube.IMeasure;
import de.xwic.cube.IMeasureLoader;
import de.xwic.cube.IUserObject;
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

public class CountLoader extends AbstractCubeListener implements IMeasureLoader, ICellLoader, ICubeListener, Serializable {

	private static final long serialVersionUID = 1L;
	
	//protected transient Map<Object, Object> mapCounts = new HashMap<Object, Object>();
	protected Map<Key, Serializable> keyCounts = new HashMap<Key, Serializable>();
	
	protected int measureIndex = -1;
	/**
	 * Index of measure index that is checked to apply count on logic:
	 * -1: count on any
	 */
	protected int countOnMeasureIndex = -1;
	protected String countOnMeasureKey;
	
	protected transient Object countOn;
	protected String key;
	
	/**
	 * @param key
	 */
	public CountLoader(String key) {
		this.key = key;
	}
	
	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CountLoader other = (CountLoader) obj;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		return true;
	}

	/**
	 * Returns the Serializable where the countOn objects are placed
	 * @param key
	 * @return
	 */
	protected Serializable getCounts(Key key, ICell cell) {
		IUserObject object = IUserObject.Helper.getFirstUserObject(cell, key);
		Serializable objects = null;
		if (object != null) {
			objects = object.getUserObject();
		} else {
			objects = keyCounts.get(key);
		}
		return objects;
	}
	
	protected int setCounts(Key key, ICell cell, Serializable objects) {
		IUserObject object = IUserObject.Helper.getFirstUserObject(cell, key);
		if (object != null) {
			object.setUserObject(objects);
		} else {
			keyCounts.put(key, objects);
		}
		if (objects instanceof Collection<?>) {
			int size = ((Collection<?>)objects).size();
			return size;
		}
		return 1;
	}
	
	/* (non-Javadoc)
	 * @see de.xwic.cube.impl.AbstractCubeListener#onCellValueChanged(de.xwic.cube.event.CellValueChangedEvent)
	 */
	public void onCellValueChanged(CellValueChangedEvent event) {
		if (measureIndex == -1) {
			// running in function mode
			return;
		}
		Key key = event.getKey();
		ICell cell = event.getCell();
		int mIndex = event.getMeasureIndex();
		if (countOnMeasureIndex != -1 && mIndex != countOnMeasureIndex && mIndex != measureIndex) {
			// don't count on this measure
			return;
		}
		Serializable objects = getCounts(key, cell);
		
		// add countOn object
		objects = IUserObject.Helper.addObjects(objects, (Serializable)countOn);
		
		int size = setCounts(key, cell, objects);
		cell.setValue(measureIndex, (double)size);	
	}
	
	/* (non-Javadoc)
	 * @see de.xwic.cube.ICellLoader#updateCell(de.xwic.cube.ICube, de.xwic.cube.Key, de.xwic.cube.ICell, java.lang.Object, java.lang.Object)
	 */
	@Override
	public void updateCell(ICube cube, Key key, ICell cell, Object columnName, Object value) {
		if (measureIndex != -1) {
			// running in measure mode
			return;
		}
		Serializable objects = getCounts(key, cell);
		// add countOn object
		objects = IUserObject.Helper.addObjects(objects, (Serializable)value);
		setCounts(key, cell, objects);
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
		Serializable childObjects = getCounts(childKey, childCell);
		if (childObjects == null) {
			// nothing to count
			return;
		}
		Serializable parentObjects = getCounts(parentKey, parentCell);
		
		Serializable objects = IUserObject.Helper.addObjects(parentObjects, childObjects);
		int size = setCounts(parentKey, parentCell, objects);
		// set count
		if (measureIndex != -1) {
			// running in measure mode, so update measure now
			parentCell.setValue(measureIndex, (double)size);
		}
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
	 * @return the countOnMeasureKey
	 */
	public String getCountOnMeasureKey() {
		return countOnMeasureKey;
	}

	/**
	 * @param countOnMeasureKey the countOnMeasureKey to set
	 */
	public void setCountOnMeasureKey(String countOnMeasureKey) {
		this.countOnMeasureKey = countOnMeasureKey;
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
		countOn = objectFocus;
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
		//mapCounts.clear();
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
		if (measureIndex == -1) {
			// running in function mode
			return false;
		}
		if (countOnMeasureIndex == -1 && countOnMeasureKey == null) {
			// accept all measures
			return true;
		}
		if (countOnMeasureKey == null) {
			int idx = cube.getMeasureIndex(measure);
			if (idx == countOnMeasureIndex) {
				countOnMeasureKey = measure.getKey();
			} else {
				return false;
			}
		} else if (countOnMeasureIndex == -1) { 
			if (measure.getKey().equals(countOnMeasureKey)) {
				int idx = cube.getMeasureIndex(measure);
				countOnMeasureIndex = idx;
			}
		}
		return countOnMeasureKey.equals(measure.getKey());
	}

}
