/**
 * 
 */
package de.xwic.cube.impl;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import de.xwic.cube.ICell;
import de.xwic.cube.ICellListener;
import de.xwic.cube.ICube;
import de.xwic.cube.IDimension;
import de.xwic.cube.IDimensionElement;
import de.xwic.cube.IMeasure;
import de.xwic.cube.Key;

/**
 * @author Florian Lippisch
 */
public class Cube extends Identifyable implements ICube, Serializable {

	private static final long serialVersionUID = -970760385292258831L;
	private final DataPool dataPool;
	private Map<String, IDimension> dimensionMap = new LinkedHashMap<String, IDimension>();
	private Map<String, IMeasure> measureMap = new LinkedHashMap<String, IMeasure>();
	private Map<Key, Cell> data;
	
	private boolean allowSplash = true;

	/**
	 * @param dataPool 
	 * @param key
	 * @param measures 
	 * @param dimensions 
	 */
	public Cube(DataPool dataPool, String key, IDimension[] dimensions, IMeasure[] measures) {
		super(key);
		this.dataPool = dataPool;
		
		if (dimensions == null) {
			throw new NullPointerException("dimensions must not be null");
		}
		if (measures == null) {
			throw new NullPointerException("measures must not be null");
		}
		
		int capacity = 1;
		for (IDimension dimension : dimensions) {
			if (dimensionMap.containsKey(dimension.getKey())) {
				throw new IllegalArgumentException("The list of dimensions contains a duplicate entry: " + dimension.getKey());
			}
			dimensionMap.put(dimension.getKey(), dimension);
			capacity = capacity * (dimension.totalSize() + 1);
		}
		
		for (IMeasure measure : measures) {
			if (measureMap.containsKey(measure.getKey())) {
				throw new IllegalArgumentException("The list of measures contains a duplicate entry: " + measure.getKey());
			}
			measureMap.put(measure.getKey(), measure);
		}
		
		data = new HashMap<Key, Cell>(500);
		
	}

	/* (non-Javadoc)
	 * @see de.xwic.cube.ICube#getDimensions()
	 */
	public Collection<IDimension> getDimensions() {
		return Collections.unmodifiableCollection(dimensionMap.values());
	}
	
	/* (non-Javadoc)
	 * @see de.xwic.cube.ICube#getMeasures()
	 */
	public Collection<IMeasure> getMeasures() {
		return Collections.unmodifiableCollection(measureMap.values());
	}
	
	/* (non-Javadoc)
	 * @see de.xwic.cube.ICube#remove()
	 */
	public void remove() {
		dataPool.removeCube(this);
	}
	
	/* (non-Javadoc)
	 * @see de.xwic.cube.ICube#getCell(de.xwic.cube.Key)
	 */
	public ICell getCell(Key key) {
		return getCell(key, false);
	}
	
	private Cell getCell(Key key, boolean createNew) {
		Cell cell = data.get(key);
		if (cell == null && createNew) {
			cell = new Cell(measureMap.size());
			Key newKey = key.clone();
			data.put(newKey, cell);
		}
		return cell;
	}
	
	/* (non-Javadoc)
	 * @see de.xwic.cube.ICube#setCellValue(de.xwic.cube.Key, de.xwic.cube.impl.Measure, double)
	 */
	public int setCellValue(Key key, IMeasure measure, double value) {
		
		if (!allowSplash && !key.containsLeafsOnly()) {
			// splash not implemented yet.
			throw new IllegalArgumentException("The key must contain only leafs.");
		}
		
		if (measure.isFunction()) {
			throw new IllegalArgumentException("The specified measure is a function. Functional measures can not hold data.");
		}
		
		return splashAndWriteValue(0, key, getMeasureIndex(measure), value);
		
	}

	/**
	 * Add a value to the existing value in the specified cell.
	 * @param key
	 * @param measure
	 * @param value
	 * @return
	 */
	public int addCellValue(Key key, IMeasure measure, double value) {
		
		if (!allowSplash && !key.containsLeafsOnly()) {
			// splash not implemented yet.
			throw new IllegalArgumentException("The key must contain only leafs.");
		}
		
		int measureIndex = getMeasureIndex(measure);
		ICell cell = getCell(key, true);
		
		Double oldValue = cell.getValue(measureIndex);
		double newValue = (oldValue != null ? value + oldValue.doubleValue() : value);

		return splashAndWriteValue(0, key, measureIndex, newValue);
		
	}

	/**
	 * @param key
	 * @param measure
	 * @param value
	 */
	private int splashAndWriteValue(int idx, Key key, int measureIndex, Double value) {
		
		int cellsModified = 0;
		if (key.containsLeafsOnly()) {
			ICell cell = getCell(key, value != null);
			
			if (cell == null) { // can only happens when the value is null too
				// simply exit, because the data is null anyway
				return 0;
			}
			
			Double oldValue = cell.getValue(measureIndex);
			double newValue = value != null ? value.doubleValue() : 0.0d;
			double diff = (oldValue != null ? newValue - oldValue.doubleValue() : newValue);
			
//			System.out.println("Start ApplyChange to: " + key);
			cellsModified += applyValueChange(0, key, measureIndex, diff);
			
		} else {
			IDimensionElement elmCurr = key.getDimensionElement(idx);
			if (!elmCurr.isLeaf()) {
				Key subKey = key.clone();
				// splash and iterate over children
				if (value != null) {
					double total = elmCurr.getElementsTotalWeight();
					double atom = total != 0 ? value / total : 0;
					double rest = value;
					for (Iterator<IDimensionElement> it =  elmCurr.getDimensionElements().iterator(); it.hasNext(); ) {
						IDimensionElement de = it.next();
						double elmValue =  it.hasNext() ? de.getWeight() * atom : rest;
						rest -= elmValue;
						subKey.setDimensionElement(idx, de);
						cellsModified += splashAndWriteValue(idx, subKey, measureIndex, elmValue);
					}
				} else {
					for (Iterator<IDimensionElement> it =  elmCurr.getDimensionElements().iterator(); it.hasNext(); ) {
						IDimensionElement de = it.next();
						subKey.setDimensionElement(idx, de);
						cellsModified += splashAndWriteValue(idx, subKey, measureIndex, null);
					}
				}
			} else {
				cellsModified += splashAndWriteValue(idx + 1, key, measureIndex, value);
			}
		}

		// remove empty cells 
		if (value == null) {
			Cell cell = getCell(key, false);
			if (cell != null) {
				cell.setValue(measureIndex, null);
				if (cell.isEmpty()) {
					data.remove(key);
				}
			}
		}
		
		return cellsModified;
	}

	/**
	 * @param i
	 * @param key
	 * @param measure
	 * @param diff
	 */
	private int applyValueChange(int idx, Key key, int measureIndex, double diff) {
		
		int cellsModified = 0;
		if (idx == dimensionMap.size()) {
			Cell cell = getCell(key, true);
			Double oldValue = cell.getValue(measureIndex);
			cell.setValue(measureIndex, oldValue != null ? oldValue.doubleValue() + diff : diff);
			cellsModified = 1;
			//System.out.println("Changed Cell " + key + " from " + oldValue + " to " + cell.getValue(measure));
		} else {
			Key myCursor = key.clone();
			IDimensionElement element = key.getDimensionElement(idx);
			do {
				cellsModified += applyValueChange(idx + 1, myCursor, measureIndex, diff);
				if (element != null) {
					element = element.getParent();
					myCursor.setDimensionElement(idx, element);
				}
			} while (element != null);
		}
		return cellsModified;
	}

	/* (non-Javadoc)
	 * @see de.xwic.cube.ICube#getCellValue(de.xwic.cube.Key, de.xwic.cube.IMeasure)
	 */
	public Double getCellValue(Key key, IMeasure measure) {
		ICell cell = getCell(key);
		if (measure.isFunction()) {
			return measure.getFunction().computeValue(this, key, cell, measure);
		} 
		if (cell != null) {
			return cell.getValue(getMeasureIndex(measure));
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see de.xwic.cube.ICube#reset()
	 */
	public void reset() {
		clear();
	}
	
	/* (non-Javadoc)
	 * @see de.xwic.cube.ICube#clear()
	 */
	public void clear() {
		data.clear();
	}
	/* (non-Javadoc)
	 * @see de.xwic.cube.ICube#clear(de.xwic.cube.IMeasure)
	 */
	public void clear(IMeasure measure) {
		int mIdx = getMeasureIndex(measure);
		for (Iterator<Key> it = data.keySet().iterator(); it.hasNext(); ) {
			Key key = it.next();
			Cell cell = data.get(key);
			cell.setValue(mIdx, null);
			if (cell.isEmpty()) {
				it.remove();
			}
		}
		
	}
	/* (non-Javadoc)
	 * @see de.xwic.cube.ICube#clear(de.xwic.cube.IMeasure, de.xwic.cube.Key)
	 */
	public void clear(IMeasure measure, Key key) {
		
		// start removing the value 
		int measureIndex = getMeasureIndex(measure);
		splashAndWriteValue(0, key, measureIndex, null);
		
	}
	
	/* (non-Javadoc)
	 * @see de.xwic.cube.ICube#createKey(java.lang.String)
	 */
	public Key createKey(String key) {
		
		IDimensionElement[] elements = new IDimensionElement[dimensionMap.size()];
		// prefill with the dimension elements
		int idx = 0;
		for (IDimension dim : dimensionMap.values()) {
			elements[idx++] = dim;
		}
		
		idx = 0;
		int pos = 0;
		int start;
		while ((start = key.indexOf('[', pos)) != -1) {
			int end = key.indexOf(']', start);
			if (end == -1) {
				throw new IllegalArgumentException("Missing ending ]");
			}
			String part = key.substring(start + 1, end);
			int idxDimSep = part.indexOf(':');
			int idxDimPos;
			String elmKeys;
			IDimension dimension;
			// does the key contain a dimension key?
			if (idxDimSep == -1) { // no key given -> use index
				dimension = elements[idx].getDimension();
				idxDimPos = idx;
				elmKeys = part;
			} else { // found key -> look for dimension
				String dimKey = part.substring(0, idxDimSep);
				elmKeys = part.substring(idxDimSep + 1);
				dimension = dimensionMap.get(dimKey);
				if (dimension == null) {
					throw new IllegalArgumentException("The specified dimension '" + dimKey + "' does not exist in this cube.");
				}

				idxDimPos = -1;
				for (int i = 0; i < elements.length; i++) {
					if (elements[i].getDimension().equals(dimension)) {
						idxDimPos = i;
						break;
					}
				}
				if (idxDimPos == -1) {
					throw new IllegalArgumentException("The specified dimension '" + dimKey + "' exists but is no longer provided in the map!.");
				}
			}
			if (!"*".equals(elmKeys)) { // specific key given.
				int idxPathSep;
				int idxPathStart = 0;
				IDimensionElement dimElm = dimension;
				do {
					idxPathSep = elmKeys.indexOf('/', idxPathStart);
					String elmKey;
					if (idxPathSep == -1) {
						elmKey = elmKeys.substring(idxPathStart);
					} else {
						elmKey = elmKeys.substring(idxPathStart, idxPathSep);
					}
					dimElm = dimElm.getDimensionElement(elmKey);
					idxPathStart = idxPathSep + 1;
				} while (idxPathSep != -1);
				elements[idxDimPos] = dimElm;
			}
			pos = end;
			idx++;
		}
		return new Key(elements);
	}
	
	/* (non-Javadoc)
	 * @see de.xwic.cube.ICube#getCellValue(java.lang.String, de.xwic.cube.IMeasure)
	 */
	public Double getCellValue(String keyString, IMeasure measure) {
		return getCellValue(createKey(keyString), measure);
	}
	
	/* (non-Javadoc)
	 * @see de.xwic.cube.ICube#getDimensionIndex(de.xwic.cube.IDimension)
	 */
	public int getDimensionIndex(IDimension dimVert) {
		int idx = 0;
		for (IDimension dim : dimensionMap.values()) {
			if (dim.equals(dimVert)) {
				return idx;
			}
			idx++;
		}
		throw new IllegalArgumentException("The specified dimension is not used in this cube.");
	}

	/**
	 * Returns the measure index.
	 * @param measure
	 * @return
	 */
	public int getMeasureIndex(IMeasure measure) {
		int idx = 0;
		for (IMeasure m : measureMap.values()) {
			if (m.equals(measure)) {
				return idx;
			}
			idx++;
		}
		throw new IllegalArgumentException("The specified measure is not used int his cube.");
	}
	
	/**
	 * @return the dataPool
	 */
	public DataPool getDataPool() {
		return dataPool;
	}
	
	/* (non-Javadoc)
	 * @see de.xwic.cube.ICube#size()
	 */
	public int getSize() {
		return data.size();
	}

	/* (non-Javadoc)
	 * @see de.xwic.cube.ICube#forEachCell(de.xwic.cube.ICellListener)
	 */
	public void forEachCell(ICellListener listener) {
		for(Entry<Key, Cell> entry: data.entrySet()) {
			if(!listener.onCell(entry.getKey(), entry.getValue())) {
				return;
			}
		}
	}
	
}
