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
import java.util.List;

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
		ICell cell = event.getParentCell();
		IDimensionElement element = key.getDimensionElement(dimensionIndex);
		Double value = findBottomLeafValue(event.getCube(), key.clone(), element);
		if (value != null) {
			cell.setValue(measureIndex, value);
		}
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
		Key key = event.getKey();
		ICell cell = event.getCell();
		int measureIndex = event.getMeasureIndex();
		if (this.measureIndex != measureIndex) {
			// setting correct aggregated value not required
			return;
		}

		IDimensionElement element = key.getDimensionElement(dimensionIndex);
		if (element.isLeaf()) {
			// setting correct aggregated value not required
			return;
		}

		// key is a parent, find correct bottom leaf containing a value
		Double value = findBottomLeafValue(event.getCube(), key.clone(), element);
		if (value != null) {
			cell.setValue(measureIndex, value);
		}
	}

	/**
	 * @param cube
	 * @param key
	 * @param element
	 * @return
	 */
	protected Double findBottomLeafValue(ICube cube, Key key, IDimensionElement element) {
		List<IDimensionElement> children = element.getDimensionElements(); 
		for (int i = children.size() - 1; i != -1 ; i--) {
			IDimensionElement child = children.get(i);
			if (!child.isLeaf()) {
				// recursive call
				Double value = findBottomLeafValue(cube, key, child);
				if (value != null) {
					return value;
				}
			} else {
				key.setDimensionElement(dimensionIndex, child);
				ICell cell = cube.getCell(key);
				if (cell == null) {
					return null;
				}
				Double value = cell.getValue(measureIndex);
				if (value != null) {
					return value;
				}
			}
		}
		return null;
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
