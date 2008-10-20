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

import de.xwic.cube.ICell;
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

		data = new HashMap<Key, Cell>(capacity);
		
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
			cell = new Cell();
			Key newKey = key.clone();
			newKey.setModifyable(false);
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
		
		return splashAndWriteValue(0, key, measure, value);
		
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
		
		ICell cell = getCell(key, true);
		
		Double oldValue = cell.getValue(measure);
		double newValue = (oldValue != null ? value + oldValue.doubleValue() : value);

		return splashAndWriteValue(0, key, measure, newValue);
		
	}

	/**
	 * @param key
	 * @param measure
	 * @param value
	 */
	private int splashAndWriteValue(int idx, Key key, IMeasure measure, double value) {
		
		int cellsModified = 0;
		if (key.containsLeafsOnly()) {
			ICell cell = getCell(key, true);
			
			Double oldValue = cell.getValue(measure);
			double diff = (oldValue != null ? value - oldValue.doubleValue() : value);
			
//			System.out.println("Start ApplyChange to: " + key);
			cellsModified += applyValueChange(0, key, measure, diff);
			
		} else {
			IDimensionElement elmCurr = key.getDimensionElement(idx);
			if (!elmCurr.isLeaf()) {
				Key subKey = key.clone();
				subKey.setModifyable(true);
				// splash and iterate over children
				double total = elmCurr.getElementsTotalWeight();
				double atom = total != 0 ? value / total : 0;
				double rest = value;
				for (Iterator<IDimensionElement> it =  elmCurr.getDimensionElements().iterator(); it.hasNext(); ) {
					IDimensionElement de = it.next();
					double elmValue =  it.hasNext() ? de.getWeight() * atom : rest;
					rest -= elmValue;
					subKey.setDimensionElement(idx, de);
					cellsModified += splashAndWriteValue(idx, subKey, measure, elmValue);
				}
			} else {
				cellsModified += splashAndWriteValue(idx + 1, key, measure, value);
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
	private int applyValueChange(int idx, Key key, IMeasure measure, double diff) {
		
		int cellsModified = 0;
		if (idx == dimensionMap.size()) {
			Cell cell = getCell(key, true);
			Double oldValue = cell.getValue(measure);
			cell.setValue(measure, oldValue != null ? oldValue.doubleValue() + diff : diff);
			cellsModified = 1;
			//System.out.println("Changed Cell " + key + " from " + oldValue + " to " + cell.getValue(measure));
		} else {
			Key myCursor = key.clone();
			myCursor.setModifyable(true);
			IDimensionElement element = key.getDimensionElement(idx);
			do {
				cellsModified += applyValueChange(idx + 1, myCursor, measure, diff);
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
		if (cell != null) {
			return cell.getValue(measure);
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see de.xwic.cube.ICube#reset()
	 */
	public void reset() {
		data.clear();
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
	 * @return the dataPool
	 */
	public DataPool getDataPool() {
		return dataPool;
	}
}
