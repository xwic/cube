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
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.xwic.cube.DimensionBehavior;
import de.xwic.cube.ICell;
import de.xwic.cube.ICellProvider;
import de.xwic.cube.ICube;
import de.xwic.cube.ICubeCacheControl;
import de.xwic.cube.ICubeListener;
import de.xwic.cube.IDimension;
import de.xwic.cube.IDimensionElement;
import de.xwic.cube.IDimensionResolver;
import de.xwic.cube.IKeyProvider;
import de.xwic.cube.IMeasure;
import de.xwic.cube.Key;
import de.xwic.cube.IDataPool.CubeType;
import de.xwic.cube.event.CellValueChangedEvent;

/**
 * This cube implementation stores only the leaf cells in an indexed array. This
 * provides a faster lookup performance of non-cached elements.
 * 
 * @author Florian Lippisch
 */
public class CubeIndexed extends Cube implements ICube, Externalizable, ICubeCacheControl {

	private static final long serialVersionUID = 1L;
	
	protected Map<Key, CachedCell> cache = new HashMap<Key, CachedCell>();

	protected boolean massUpdateMode = false;
	protected int maxCacheSize = 100000;
	
	protected boolean externalizeCache = true;

	protected transient int calcCellTime = 0;
	
	// Commons log
	private transient Log log;
	{
		log = LogFactory.getLog(CubeIndexed.class);
	}

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
		ICell cell;
		/** Number of times the cell has been accessed */
		long hits = 0;
		/** Number of cells aggregated to compute this value */
		long leafCount = 0;
		/** Number of refresh-cycles passed where this element was not read. */
		long unusedCount = 0;
		CachedCell(ICell cell) {
			this.cell = cell;
		}
		public long score() {
			return ((hits / 10) + 1) / (unusedCount + 1);
		}
	}
	
	
	/**
	 * INTERNAL: This constructor is used by the serialization mechanism. 
	 */
	public CubeIndexed() {
		super(); 
	}
	
	/**
	 * @param dataPool 
	 * @param key
	 * @param measures 
	 * @param dimensions 
	 */
	public CubeIndexed(DataPool dataPool, String key, IDimension[] dimensions, IMeasure[] measures) {
		super(dataPool, key, dimensions, measures);
	}

	/* (non-Javadoc)
	 * @see de.xwic.cube.impl.Cube#createCellStore()
	 */
	@Override
	protected ICellStore createCellStore() {
		return new IndexedDataTable(dimensionMap.size(), measureMap.size());
	}
	
	
	/* (non-Javadoc)
	 * @see de.xwic.cube.impl.Cube#applyValueChange(int, de.xwic.cube.Key, int, double)
	 */
	@Override
	protected int applyValueChange(int idx, Key key, int measureIndex, double diff) {

		// this implementation does not "aggregate" during write. The non-leaf cells stay empty.
		
		ICell cell = getCell(key, true);
		Double oldValue = cell.getValue(measureIndex);
		cell.setValue(measureIndex, oldValue != null ? oldValue.doubleValue() + diff : diff);
		
		// invoke CellValueChangedListener
		onCellValueChanged(new CellValueChangedEvent(this, key, cell, measureIndex, diff));
		return 1;
		
	}
	
	/* (non-Javadoc)
	 * @see de.xwic.cube.impl.Cube#getCell(de.xwic.cube.Key, boolean)
	 */
	@Override
	public ICell getCell(Key key, boolean createNew) {
		
		if (isLeafLikeKey(key)) {
			// is leaf key
			return super.getCell(key, createNew);
		}

		// check cache
		// ....
		ICell result = null;
		CachedCell cc = cache.get(key);
		if (cc != null) {
			cc.hits++;
			cc.unusedCount = 0;
			result = cc.cell;
		} else {
			// create cell
			synchronized (this) { // must sync, otherwise the cache might get damaged

				// re-check for sync issues 
				cc = cache.get(key);
				if (cc == null) { // still empty -> create
					cc = calcCellFromIndex(key);
				}
				if (cc != null) {
					cc.hits++;
					cache.put(key.clone(), cc);
					result = cc.cell;
				}
			}
		}
		if (result == null && createNew) {
			result = createNewCell(key, measureMap.size());
		}
		return result;
	}
	
	/**
	 * @param key
	 * @return
	 */
	private CachedCell calcCellFromIndex(Key searchKey) {
		CachedCell cc = new CachedCell(null);
		
		/* UNUSED code
		int max = dimensionMap.size();
		boolean checkBehavior = false;
		for (DimensionBehavior db : dimensionBehavior) {
			if (db.isFlagged(DimensionBehavior.FLAG_NO_AGGREGATION) || db.isFlagged(DimensionBehavior.FLAG_NO_SPLASH)) {
				checkBehavior = true;
				break;
			}
		}
		*/
		
		IndexedDataTable idt = (IndexedDataTable)data;
		cc.cell = idt.calcCell(searchKey);
		return cc;
	}
	
	/* (non-Javadoc)
	 * @see de.xwic.cube.impl.Cube#removeEmptyCells(de.xwic.cube.Key, int)
	 */
	@Override
	protected void removeEmptyCells(Key key, int measureIndex) {
		if (isLeafLikeKey(key)) { // only execute on leafs...
			super.removeEmptyCells(key, measureIndex);
		}
	}

	/* (non-Javadoc)
	 * @see java.io.Externalizable#readExternal(java.io.ObjectInput)
	 */
	@SuppressWarnings("unchecked")
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {

		int version = in.readInt();
		if (version < 1 || version > 7) {
			throw new IOException("Cannot deserialize cube -> data file version is " + version + ", but expected 1..7");
		}
		key = (String) in.readObject();
		title = (String) in.readObject();
		allowSplash = in.readBoolean();
		dataPool = (DataPool) in.readObject();
		dimensionMap = (Map<String, IDimension>) in.readObject();
		measureMap = (Map<String, IMeasure>) in.readObject();
		
		log.debug("Restoring Cube '" + key + "' ...");
		
		dimensionBehavior = new DimensionBehavior[dimensionMap.size()];
		for (int i = 0; i < dimensionBehavior.length; i++) {
			dimensionBehavior[i] = DimensionBehavior.DEFAULT;
		}
		
		if (version > 2) {
			cubeListeners = (List<ICubeListener>)in.readObject();
		}
		
		if (version > 4) {
			dimensionResolver = (IDimensionResolver)in.readObject();
			
			if (version > 5) {
				keyProvider = (IKeyProvider)in.readObject();
				cellProvider = (ICellProvider)in.readObject();
			}
			if (version > 6) {
				for (int i = 0; i < dimensionBehavior.length; i++) {
					dimensionBehavior[i] = (DimensionBehavior)in.readObject();
				}
			}
			
			serializeData = in.readBoolean();
		}
		
		// read data
		if (!serializeData) {
			// optimized data read
			data = createCellStore();
			data.restore(in, keyProvider);
		} else {
			// customer Key implementation
			data = (ICellStore)in.readObject();
		}
		
		if (version > 3) {
			externalizeCache = in.readBoolean();
			
			// moved to CubePreCache implementation
			if (version < 5) {
				// read cache paths settings
				boolean autoCachePaths = in.readBoolean();
				if (autoCachePaths && externalizeCache) {
					in.readObject(); // cachePaths = (HashSet<CachePath>) 
					in.readObject(); // newCachePaths = (HashSet<CachePath>) 
					in.readObject(); // newCacheKeys = (HashSet<Key>)
				}
			}
		}

		// read cache
		if (externalizeCache) {
			int size = in.readInt();
			int dimSize = dimensionMap.size();
			cache = new HashMap<Key, CachedCell>(size);
			for (int i = 0; i < size; i++) {
				Key key = createNewKey(null);
				key.readObject(in, dimSize);
				CachedCell cell = (CachedCell)in.readObject();
				cache.put(key, cell);
			}
		} else {
			cache = new HashMap<Key, CachedCell>();
		}
		
	}
	
	/* (non-Javadoc)
	 * @see de.xwic.cube.impl.Cube#writeFinished()
	 */
	@Override
	public void massUpdateFinished() {
		
		clearCache(); // clear the cache!
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
	 * Trigger the rebuilding of the index.
	 */
	protected void buildIndex() {
		
		((IndexedDataTable)data).buildIndex();
		
	}

	/* (non-Javadoc)
	 * @see java.io.Externalizable#writeExternal(java.io.ObjectOutput)
	 */
	public void writeExternal(ObjectOutput out) throws IOException {

		// serialize -> write the cube data.
		int version = 7;
		out.writeInt(version); // version number
		out.writeObject(key);
		out.writeObject(title);
		out.writeBoolean(allowSplash);
		out.writeObject(dataPool);
		out.writeObject(dimensionMap);
		out.writeObject(measureMap);
		out.writeObject(cubeListeners);
		out.writeObject(dimensionResolver);
		out.writeObject(keyProvider);
		out.writeObject(cellProvider);
		
		for (int i = 0; i < dimensionBehavior.length; i++) {
			out.writeObject(dimensionBehavior[i]);
		}

		// data serialization mode
		out.writeBoolean(serializeData);
		
		// write data...
		if (!serializeData) {
			// default, optimized data serialization
			data.serialize(out);
		} else {
			// customer Key implementation used, serialize data
			out.writeObject(data);
		}
	
		// save cache
		out.writeBoolean(externalizeCache);

		if (externalizeCache) {
			out.writeInt(cache.size());
			for(Entry<Key, CachedCell> entry: cache.entrySet()) {
				
				for (IDimensionElement elm : entry.getKey().getDimensionElements()) {
					out.writeObject(elm);
				}
				out.writeObject(entry.getValue());
				
			}
		}		
	}
	
	/* (non-Javadoc)
	 * @see de.xwic.cube.impl.Cube#setCellValue(de.xwic.cube.Key, de.xwic.cube.IMeasure, double)
	 */
	@Override
	public int setCellValue(Key key, IMeasure measure, double value) {
		if (!massUpdateMode) {
			clearCache();
			//rootIndex.clear();
		}
		return super.setCellValue(key, measure, value);
	}
	
	/**
	 * Data has been written or the cube has been cleared. 
	 */
	public void clearCache() {
		cache.clear();
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
		
		batchRefreshCache(cellMap);
		
	}

	protected void batchRefreshCache(Map<Key, CachedCell> cellMap) {

		for (Entry<Key, CachedCell> entry : cellMap.entrySet()) {
			Key key = entry.getKey();
			CachedCell cell = calcCellFromIndex(key);
			CachedCell oldCell = entry.getValue();
			cell.hits = oldCell.hits;
			cell.unusedCount = oldCell.unusedCount + 1;
			cache.put(key, cell);
		}
		
	}
	
	/* (non-Javadoc)
	 * @see de.xwic.cube.ICubeCacheControl#buildCacheFromCube(ICube cube)
	 */
	public synchronized void buildCacheFromCube(ICube cube) {
		
		if(cube.getClass().isAssignableFrom(CubeIndexed.class)) {
			
			cache.clear();
									
			batchRefreshCache(((CubeIndexed)cube).cache);	
			
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

	/**
	 * @return the externalizeCache
	 */
	public boolean isExternalizeCache() {
		return externalizeCache;
	}

	/**
	 * @param externalizeCache the externalizeCache to set
	 */
	public void setExternalizeCache(boolean externalizeCache) {
		this.externalizeCache = externalizeCache;
	}

	/* (non-Javadoc)
	 * @see de.xwic.cube.ICube#getCubeType()
	 */
	public CubeType getCubeType() {
		return CubeType.INDEXED;
	}
}
