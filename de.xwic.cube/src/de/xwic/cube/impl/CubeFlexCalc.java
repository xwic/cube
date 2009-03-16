/**
 * 
 */
package de.xwic.cube.impl;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import de.xwic.cube.ICube;
import de.xwic.cube.IDimension;
import de.xwic.cube.IDimensionElement;
import de.xwic.cube.IMeasure;
import de.xwic.cube.Key;

/**
 * This cube implementation stores only the leaf cells. Aggregated values are stored
 * in a flexible cache.
 * 
 * @author Florian Lippisch
 */
public class CubeFlexCalc extends Cube implements ICube, Externalizable {

	private Map<Key, Cell> cache = new HashMap<Key, Cell>();
	
	/**
	 * INTERNAL: This constructor is used by the serialization mechanism. 
	 */
	public CubeFlexCalc() {
		super(); 
	}
	
	/**
	 * @param dataPool 
	 * @param key
	 * @param measures 
	 * @param dimensions 
	 */
	public CubeFlexCalc(DataPool dataPool, String key, IDimension[] dimensions, IMeasure[] measures) {
		super(dataPool, key, dimensions, measures);
	}


	/* (non-Javadoc)
	 * @see de.xwic.cube.impl.Cube#applyValueChange(int, de.xwic.cube.Key, int, double)
	 */
	@Override
	protected int applyValueChange(int idx, Key key, int measureIndex, double diff) {

		// this implementation does not "aggregate" during write. The non-leaf cells stay empty.
		
		Cell cell = getCell(key, true);
		Double oldValue = cell.getValue(measureIndex);
		cell.setValue(measureIndex, oldValue != null ? oldValue.doubleValue() + diff : diff);
		return 1;
		
	}
	
	/* (non-Javadoc)
	 * @see de.xwic.cube.impl.Cube#getCell(de.xwic.cube.Key, boolean)
	 */
	@Override
	protected Cell getCell(Key key, boolean createNew) {

		if (key.containsLeafsOnly()) {
			// is leaf key
			return super.getCell(key, createNew);
		}

		// check cache
		// ....
		Cell result = null;
		if (cache.containsKey(key)) {
			result = cache.get(key);
		} else {
			// create cell
			
			if (false) {
				Cell cell = new Cell(measureMap.size());
				boolean hasData = calcCell(0, key.clone(), cell);
				
				cache.put(key, hasData ? cell : null);
				
				result = hasData ? cell : null;
			} else {
				Cell[] cells = serialCalc(new Key[] { key.clone() });
				cache.put(key, cells[0]);
				result = cells[0];
			}
		}
		if (result == null && createNew) {
			result = new Cell(measureMap.size());
		}
		return result;
	}
	
	private Cell[] serialCalc(Key[] keys) {
		
		Cell[] cells = new Cell[keys.length];
		
		for(Entry<Key, Cell> entry: data.entrySet()) {
		
			Cell rawCell = entry.getValue();
			for (int i = 0; i < keys.length; i++) {
				if (keys[i].isSubKey(entry.getKey())) {
					
					if (cells[i] == null) {
						cells[i] = new Cell(measureMap.size());
					}
					
					for (int m = 0; m < rawCell.values.length; m++) {
						if (rawCell.values[m] != null) {
							if (cells[i].values[m] == null) {
								cells[i].values[m] = rawCell.values[m];
							} else {
								cells[i].values[m] = cells[i].values[m] + rawCell.values[m];
							}
						}
					}
					
				}
			}
			
		}
		
		return cells;
		
	}
	
	/**
	 * @param i
	 * @param key
	 * @param cell
	 * @return
	 */
	private boolean calcCell(int idx, Key key, Cell cell) {

		boolean hasData = false;
		if (key.containsLeafsOnly()) {
			Cell rawCell = data.get(key);
			if (rawCell != null) {
				for (int i = 0; i < rawCell.values.length; i++) {
					if (rawCell.values[i] != null) {
						if (cell.values[i] == null) {
							cell.values[i] = rawCell.values[i];
						} else {
							cell.values[i] = cell.values[i] + rawCell.values[i];
						}
					}
				}
				hasData = true;
			}
		} else {
			IDimensionElement elmCurr = key.getDimensionElement(idx);
			if (!elmCurr.isLeaf()) {
				Key subKey = key.clone();
				// splash and iterate over children
				for (Iterator<IDimensionElement> it =  elmCurr.getDimensionElements().iterator(); it.hasNext(); ) {
					IDimensionElement de = it.next();
					subKey.setDimensionElement(idx, de);
					hasData |= calcCell(idx, subKey, cell);
				}
			} else {
				hasData |= calcCell(idx + 1, key, cell);
			}
		}
		return hasData;
	}

	/* (non-Javadoc)
	 * @see java.io.Externalizable#readExternal(java.io.ObjectInput)
	 */
	@SuppressWarnings("unchecked")
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {

		int version = in.readInt();
		if (version != 1) {
			throw new IOException("Can not deserialize cube -> data file version is " + version + ", but expected 1");
		}
		key = (String) in.readObject();
		title = (String) in.readObject();
		allowSplash = in.readBoolean();
		dataPool = (DataPool) in.readObject();
		dimensionMap = (Map<String, IDimension>) in.readObject();
		measureMap = (Map<String, IMeasure>) in.readObject();
		
		// read data
		int size = in.readInt();
		int dimSize = dimensionMap.size();
		
		data = new HashMap<Key, Cell>(size);
		for (int i = 0; i < size; i++) {
			IDimensionElement[] keyElements = new IDimensionElement[dimSize];
			for (int dIdx = 0; dIdx < dimSize; dIdx++) {
				keyElements[dIdx] = (IDimensionElement)in.readObject();
			}
			Key key = new Key(keyElements);
			Cell cell = (Cell)in.readObject();
			data.put(key, cell);
		}
		
	}

	/* (non-Javadoc)
	 * @see java.io.Externalizable#writeExternal(java.io.ObjectOutput)
	 */
	public void writeExternal(ObjectOutput out) throws IOException {

		// serialize -> write the cube data.
		out.writeInt(1); // version number
		out.writeObject(key);
		out.writeObject(title);
		out.writeBoolean(allowSplash);
		out.writeObject(dataPool);
		out.writeObject(dimensionMap);
		out.writeObject(measureMap);
		
		// write data...
		out.writeInt(data.size());
		for(Entry<Key, Cell> entry: data.entrySet()) {
			
			for (IDimensionElement elm : entry.getKey().getDimensionElements()) {
				out.writeObject(elm);
			}
			out.writeObject(entry.getValue());
			
		}
		
		
	}
	
}
