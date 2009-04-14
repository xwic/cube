/**
 * 
 */
package de.xwic.cube.impl;

import java.io.BufferedReader;
import java.io.Externalizable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Map.Entry;

import de.xwic.cube.ICube;
import de.xwic.cube.ICubeCacheControl;
import de.xwic.cube.ICubeListener;
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
public class CubeFlexCalc extends Cube implements ICube, Externalizable, ICubeCacheControl {

	private static final long serialVersionUID = 1L;
	
	public Map<Key, CachedCell> cache = new HashMap<Key, CachedCell>();
	private Map<IDimensionElement, Set<Key>> rootIndex = new HashMap<IDimensionElement, Set<Key>>();

	private boolean massUpdateMode = false;
	private int maxCacheSize = 100000;
	
	/**
	 * @author lippisch
	 *
	 */
	public final class CacheCellComparator implements
			Comparator<Entry<Key, CachedCell>> {
		/* (non-Javadoc)
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public int compare(Entry<Key, CachedCell> o1, Entry<Key, CachedCell> o2) {
			long score1 = o1.getValue().score();
			long score2 = o2.getValue().score();
			if (score1 == score2) {
				return 0;
			}
			if (score1 < score2) {
				return 1;
			}
			return -1;
		}
	}

	public class CachedCell implements Serializable {
		private static final long serialVersionUID = 2L;
		Cell cell;
		/** Number of times the cell has been accessed */
		long hits = 0;
		/** Number of cells aggregated to compute this value */
		long leafCount = 0;
		/** Number of refresh-cycles passed where this element was not read. */
		long unusedCount = 0;
		CachedCell(Cell cell) {
			this.cell = cell;
		}
		public long score() {
			return ((hits / 10) + 1) / (unusedCount + 1);
		}
	}
	
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

		if (key.isLeaf()) {
			// is leaf key
			return super.getCell(key, createNew);
		}

		// check cache
		// ....
		Cell result = null;
		if (cache.containsKey(key)) {
			CachedCell cc = cache.get(key);
			cc.hits++;
			cc.unusedCount = 0;
			result = cc != null ? cc.cell : null;
		} else {
			// create cell
			synchronized (this) { // must sync, otherwise the cache might get damaged
				CachedCell cc = null;
				if (false) {
					Cell cell = new Cell(measureMap.size());
					boolean hasData = calcCell(0, key.clone(), cell);
					result = hasData ? cell : null;
					
				} else {
					if (key.getDimensionElement(0).getDepth() > 0 && rootIndex.size() > 0) {
						// use indexed calculation
						cc = calcCellFromIndex(key);
					} else {
						CachedCell[] cells = serialCalc(new Key[] { key.clone() });
						cc = cells[0];
					}
				}
				if (cc != null) {
					cc.hits++;
					cache.put(key, cc);
					result = cc.cell;
				}
			}
		}
		if (result == null && createNew) {
			result = new Cell(measureMap.size());
		}
		return result;
	}
	
	/**
	 * @param key
	 * @return
	 */
	private CachedCell calcCellFromIndex(Key searchKey) {
		CachedCell cc = new CachedCell(null);
		
		Set<Key> keys = rootIndex.get(searchKey.getDimensionElement(0));
		if (keys != null) {
			for (Key key : keys) {
				if (searchKey.isSubKey(key)) {
					cc.leafCount++;
					Cell rawCell = data.get(key);
					if (rawCell != null) {
					
						if (cc.cell == null) {
							cc.cell = new Cell(measureMap.size());
						}
						aggregateCells(cc.cell, rawCell);
					}
					
				}
			}
		}
		
		return cc;
	}

	/**
	 * Aggregates the valueCell to the cell.
	 * @param cell
	 * @param valueCell
	 */
	private void aggregateCells(Cell cell, Cell valueCell) {
		for (int m = 0; m < valueCell.values.length; m++) {
			if (valueCell.values[m] != null) {
				if (cell.values[m] == null) {
					cell.values[m] = valueCell.values[m];
				} else {
					cell.values[m] = cell.values[m] + valueCell.values[m];
				}
			}
		}
	}

	/**
	 * Calculate the requested value(s) by aggregating all leafs.
	 * @param keys
	 * @return
	 */
	private CachedCell[] serialCalc(Key[] keys) {
		
		CachedCell[] cachedCells = new CachedCell[keys.length];
		for (int i = 0; i < keys.length; i++) {
			cachedCells[i] = new CachedCell(null);
		}
		
		for(Entry<Key, Cell> entry: data.entrySet()) {
		
			Cell rawCell = entry.getValue();
			for (int i = 0; i < keys.length; i++) {
				if (keys[i].isSubKey(entry.getKey())) {
					cachedCells[i].leafCount++;
					if (cachedCells[i].cell == null) {
						cachedCells[i].cell = new Cell(measureMap.size());
					}
					aggregateCells(cachedCells[i].cell, rawCell);
					
				}
			}
			
		}
		
		return cachedCells;
		
	}
	
	/* (non-Javadoc)
	 * @see de.xwic.cube.impl.Cube#removeEmptyCells(de.xwic.cube.Key, int)
	 */
	@Override
	protected void removeEmptyCells(Key key, int measureIndex) {
		if (key.isLeaf()) { // only execute on leafs...
			super.removeEmptyCells(key, measureIndex);
		}
	}
	
	/**
	 * @param i
	 * @param key
	 * @param cell
	 * @return
	 */
	private boolean calcCell(int idx, Key key, Cell cell) {

		boolean hasData = false;
		if (key.isLeaf()) {
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
		if (version < 1 || version > 3) {
			throw new IOException("Can not deserialize cube -> data file version is " + version + ", but expected 1..3");
		}
		key = (String) in.readObject();
		title = (String) in.readObject();
		allowSplash = in.readBoolean();
		dataPool = (DataPool) in.readObject();
		dimensionMap = (Map<String, IDimension>) in.readObject();
		measureMap = (Map<String, IMeasure>) in.readObject();
		
		if (version > 2) {
			cellValueChangedListeners = (List<ICubeListener>)in.readObject();
		}

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
		
		if (version > 1) {
			size = in.readInt();
			cache = new HashMap<Key, CachedCell>(size);
			for (int i = 0; i < size; i++) {
				IDimensionElement[] keyElements = new IDimensionElement[dimSize];
				for (int dIdx = 0; dIdx < dimSize; dIdx++) {
					keyElements[dIdx] = (IDimensionElement)in.readObject();
				}
				Key key = new Key(keyElements);
				CachedCell cell = (CachedCell)in.readObject();
				cache.put(key, cell);
			}
		} else {
			cache = new HashMap<Key, CachedCell>();
		}
		
		buildIndex();
	}
	
	/* (non-Javadoc)
	 * @see de.xwic.cube.impl.Cube#writeFinished()
	 */
	@Override
	public void massUpdateFinished() {
		
		// build index.
		buildIndex();
		massUpdateMode = false;
		
	}
	
	/* (non-Javadoc)
	 * @see de.xwic.cube.impl.Cube#beginMassUpdate()
	 */
	@Override
	public void beginMassUpdate() {
		// nothing to do
		massUpdateMode = true;
	}
	
	/**
	 * Build an index for all elements of the first defined dimension.
	 * 
	 */
	private void buildIndex() {
		
		rootIndex.clear();
		for(Entry<Key, Cell> entry: data.entrySet()) {
			IDimensionElement elm = entry.getKey().getDimensionElement(0);
			IDimensionElement e = elm;
			// do not build a cache for the root element, as it would just include all keys.
			while (!(e instanceof IDimension)) {
				Set<Key> keys = rootIndex.get(e);
				if (keys == null) {
					keys = new HashSet<Key>();
					rootIndex.put(e, keys);
				}
				keys.add(entry.getKey());
				e = e.getParent();
			}
		}
		
	}

	/* (non-Javadoc)
	 * @see java.io.Externalizable#writeExternal(java.io.ObjectOutput)
	 */
	public void writeExternal(ObjectOutput out) throws IOException {

		// serialize -> write the cube data.
		int version = 3;
		out.writeInt(version); // version number
		out.writeObject(key);
		out.writeObject(title);
		out.writeBoolean(allowSplash);
		out.writeObject(dataPool);
		out.writeObject(dimensionMap);
		out.writeObject(measureMap);
		out.writeObject(cellValueChangedListeners);
		
		// write data...
		out.writeInt(data.size());
		for(Entry<Key, Cell> entry: data.entrySet()) {
			
			for (IDimensionElement elm : entry.getKey().getDimensionElements()) {
				out.writeObject(elm);
			}
			out.writeObject(entry.getValue());
			
		}
	
		// save cache
		out.writeInt(cache.size());
		for(Entry<Key, CachedCell> entry: cache.entrySet()) {
			
			for (IDimensionElement elm : entry.getKey().getDimensionElements()) {
				out.writeObject(elm);
			}
			out.writeObject(entry.getValue());
			
		}
		
	}
	
	/* (non-Javadoc)
	 * @see de.xwic.cube.impl.Cube#setCellValue(de.xwic.cube.Key, de.xwic.cube.IMeasure, double)
	 */
	@Override
	public int setCellValue(Key key, IMeasure measure, double value) {
		if (!massUpdateMode) {
			clearCache();
			rootIndex.clear();
		}
		return super.setCellValue(key, measure, value);
	}
	
	/**
	 * Data has been written or the cube has been cleared. 
	 */
	protected void clearCache() {
		cache.clear();
		rootIndex.clear();
	}
	
	/* (non-Javadoc)
	 * @see de.xwic.cube.impl.Cube#clear()
	 */
	@Override
	public void clear() {
		clearCache();
		super.clear();
	}
	
	/* (non-Javadoc)
	 * @see de.xwic.cube.impl.Cube#clear(de.xwic.cube.IMeasure)
	 */
	@Override
	public void clear(IMeasure measure) {
		clearCache();
		super.clear(measure);
	}
	
	/* (non-Javadoc)
	 * @see de.xwic.cube.impl.Cube#clear(de.xwic.cube.IMeasure, de.xwic.cube.Key)
	 */
	@Override
	public void clear(IMeasure measure, Key key) {
		clearCache();
		super.clear(measure, key);
	}
	
	
	/**
	 * Print detailed informations about the internal
	 * data structure for debug and optimization.
	 * @param out
	 */
	public void printStats(PrintStream out) {
		
		out.println("Data : " + data.size());
		out.println("Cache: " + cache.size());
		out.println("RootIndex: " + rootIndex.size());
		if (rootIndex.size() > 0) {
			int total = 0;
			for (Set<Key> keys : rootIndex.values()) {
				total += keys.size();
			}
			out.println("Total Ref: " + total);
			out.println("Avr. Size: " + (total / rootIndex.size()));
		}
		
	}
	
	/* (non-Javadoc)
	 * @see de.xwic.cube.impl.ICubeCacheControl#printCacheProfile(java.io.PrintStream)
	 */
	public void printCacheProfile(PrintStream out) {
		
		List<Entry<Key, CachedCell>> entries = new ArrayList<Entry<Key,CachedCell>>();
		entries.addAll(cache.entrySet());
		Collections.sort(entries, new CacheCellComparator());
		out.println("score;hits;leafs;unusedCycles;key");
		for (Entry<Key, CachedCell> entry : entries) {
			CachedCell cc = entry.getValue();
			out.print(cc.score());
			out.print(";");
			out.print(cc.hits);
			out.print(";");
			out.print(cc.leafCount);
			out.print(";");
			out.print(cc.unusedCount);
			out.print(";");
			out.println(entry.getKey().toString());
		}
		
	}
	
	/* (non-Javadoc)
	 * @see de.xwic.cube.ICubeCacheControl#buildCacheFromStats(java.io.InputStream)
	 */
	public synchronized void buildCacheFromStats(InputStream stream) throws IOException {
		
		cache.clear();
		
		BufferedReader in = new BufferedReader(new InputStreamReader(stream));
		String line = in.readLine(); // skip header
		
		Map<Key, CachedCell> cellMap = new HashMap<Key, CachedCell>();
		
		while ((line = in.readLine()) != null) {
			StringTokenizer stk = new StringTokenizer(line, ";");
			/*int score = */ Integer.parseInt(stk.nextToken());
			CachedCell cc = new CachedCell(null);
			cc.hits = Integer.parseInt(stk.nextToken());
			cc.leafCount = Integer.parseInt(stk.nextToken());
			cc.unusedCount = Integer.parseInt(stk.nextToken());
			String keyString = stk.nextToken("");
			try {
				Key key = createKey(keyString);
				cellMap.put(key, cc);
			} catch (IllegalArgumentException ie) {
				// the key is (no longer) supported. -> Simply ignore.
			}
		}
		
		// now batch-refresh cache
		Key[] keys = new Key[cellMap.size()];
		int idx = 0;
		for (Key key : cellMap.keySet()) {
			keys[idx++] = key;
		}
		
		CachedCell[] cells = serialCalc(keys);
		for (int i = 0; i < cells.length; i++) {
			CachedCell oldCell = cellMap.get(keys[i]);
			cells[i].hits = oldCell.hits;
			cells[i].unusedCount = oldCell.unusedCount + 1;
			cache.put(keys[i], cells[i]);
		}
		
	}
	
	/* (non-Javadoc)
	 * @see de.xwic.cube.impl.ICubeCacheControl#refreshCache()
	 */
	public synchronized void refreshCache() {

		List<Entry<Key, CachedCell>> entries = new ArrayList<Entry<Key,CachedCell>>();
		entries.addAll(cache.entrySet());
		Collections.sort(entries, new CacheCellComparator());
		
		int idx = 0;
		for (Entry<Key, CachedCell> entry : entries) {
			
			CachedCell cc = entry.getValue();
			if (idx++ > maxCacheSize) {
				cache.remove(entry.getKey());
			} else {
				cc.unusedCount++;
			}
			
		}
		
	}
	
	/* (non-Javadoc)
	 * @see de.xwic.cube.ICubeCacheControl#getCacheSize()
	 */
	public int getCacheSize() {
		return cache.size();
	}

	/* (non-Javadoc)
	 * @see de.xwic.cube.impl.ICubeCacheControl#getMaxCacheSize()
	 */
	public int getMaxCacheSize() {
		return maxCacheSize;
	}

	/* (non-Javadoc)
	 * @see de.xwic.cube.impl.ICubeCacheControl#setMaxCacheSize(int)
	 */
	public void setMaxCacheSize(int maxCacheSize) {
		this.maxCacheSize = maxCacheSize;
	}
	
}
