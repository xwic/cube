/**
 * $Id: $
 *
 * Copyright (c) 2009 NetApp.
 * All rights reserved.

 * de.xwic.cube.util.DimensionLeafLoader.java
 * Created on Apr 22, 2009
 * 
 * @author JBORNEMA
 */
package de.xwic.cube.util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.xwic.cube.ICell;
import de.xwic.cube.ICube;
import de.xwic.cube.ICubeListener;
import de.xwic.cube.IDimensionElement;
import de.xwic.cube.IMeasureLoader;
import de.xwic.cube.Key;
import de.xwic.cube.event.CellAggregatedEvent;
import de.xwic.cube.event.CellValueChangedEvent;

/**
 * Created on Apr 22, 2009
 * @author JBORNEMA
 */

public class DimensionLeafLoader implements IMeasureLoader, ICubeListener, Serializable {

	private final static long serialVersionUID = 7136121096573453167l;
	
	protected int dimensionIndex;
	protected int measureIndex;

	protected transient boolean cellValueChangedEnabled;
	protected transient Map<IDimensionElement, IDimensionElement> leafMap;
	{
		leafMap = new HashMap<IDimensionElement, IDimensionElement>();
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
		DimensionLeafLoader other = (DimensionLeafLoader)obj;
		return other.measureIndex == measureIndex && other.dimensionIndex == dimensionIndex;
	}

	/* (non-Javadoc)
	 * @see de.xwic.cube.ICubeListener#onCellAggregated(de.xwic.cube.event.CellAggregatedEvent)
	 */
	public void onCellAggregated(CellAggregatedEvent event) {
		Key key = event.getParentKey();
		IDimensionElement element = key.getDimensionElement(dimensionIndex);
		if (element.isLeaf()) {
			// setting correct aggregated value not required
			return;
		}
		ICell cell = event.getParentCell();
		IDimensionElement leaf = findLeaf(event.getCube(), key, element);
		if (leaf != null) {
			// leaf found, use its value
			Key clone = key.clone();
			clone.setDimensionElement(dimensionIndex, leaf);
			ICell leafCell = event.getCube().getCell(clone);
			cell.setValue(measureIndex, leafCell != null ? leafCell.getValue(measureIndex) : null);
		} else {
			// no leaf found set null
			cell.setValue(measureIndex, null);
		}
	}

	/**
	 * @param cube
	 * @param key
	 * @return
	 */
	protected IDimensionElement findLeaf(ICube cube, Key key, IDimensionElement parent) {
		if (leafMap == null) {
			leafMap = new HashMap<IDimensionElement, IDimensionElement>();			
		}
		IDimensionElement leaf = leafMap.get(parent);
		if (leaf != null) {
			return leaf;
		}
		// find leaf with data on configured measure
		key = cube.createKey();
		
		try {
			List<IDimensionElement> children = parent.getDimensionElements(); 
			for (int i = children.size() - 1; i != -1 ; i--) {
				IDimensionElement child = children.get(i);
				if (!child.isLeaf()) {
					// recursive call
					leaf = findLeaf(cube, key, child);
					if (leaf != null) {
						return leaf;
					}
				} else {
					// child is leaf, if value exists return
					key.setDimensionElement(dimensionIndex, child);
					ICell cell = cube.getCell(key);
					if (cell != null) {
						leaf = child;
						return leaf;
					}
				}
			}
		} finally {
			leafMap.put(parent, leaf);
		}
		// no leaf found
		return null;
	}

	/* (non-Javadoc)
	 * @see de.xwic.cube.ICubeListener#onCellValueChanged(de.xwic.cube.event.CellValueChangedEvent)
	 */
	public void onCellValueChanged(CellValueChangedEvent event) {
		// this method is implemented for Cube implementation
		// it should not do anything on CubeFlexCalc
		if (!cellValueChangedEnabled) {
			// nothing to do
			return;
		}
		throw new RuntimeException("Method not yet supported");
	}

	/**
	 * @return the dimensionIndex
	 */
	public int getDimensionIndex() {
		return dimensionIndex;
	}

	/**
	 * @param dimensionIndex the dimensionIndex to set
	 */
	public void setDimensionIndex(int dimensionIndex) {
		this.dimensionIndex = dimensionIndex;
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

	/* (non-Javadoc)
	 * @see de.xwic.cube.IMeasureLoader#clear()
	 */
	public void clear() {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see de.xwic.cube.IMeasureLoader#configure(de.xwic.cube.IMeasureLoader)
	 */
	public void configure(IMeasureLoader fromLoader) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see de.xwic.cube.IMeasureLoader#setObjectFocus(java.lang.Object)
	 */
	public void setObjectFocus(Object objectFocus) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see de.xwic.cube.IMeasureLoader#isExtension()
	 */
	public boolean isExtension() {
		// use exiting measure logic and replace just the aggregation part 
		return true;
	}

	/**
	 * @return the cellValueChangedEnabled
	 */
	public boolean isCellValueChangedEnabled() {
		return cellValueChangedEnabled;
	}

	/**
	 * @param cellValueChangedEnabled the cellValueChangedEnabled to set
	 */
	public void setCellValueChangedEnabled(boolean cellValueChangedEnabled) {
		this.cellValueChangedEnabled = cellValueChangedEnabled;
	}

}
