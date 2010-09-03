/**
 * 
 */
package de.xwic.cube.impl;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.xwic.cube.DimensionBehavior;
import de.xwic.cube.ICell;
import de.xwic.cube.ICellProvider;
import de.xwic.cube.ICubeListener;
import de.xwic.cube.IDimension;
import de.xwic.cube.IDimensionElement;
import de.xwic.cube.IDimensionResolver;
import de.xwic.cube.IKeyProvider;
import de.xwic.cube.IMeasure;
import de.xwic.cube.Key;
import de.xwic.cube.IDataPool.CubeType;
import de.xwic.cube.event.CellAggregatedEvent;

/**
 * This cube implementation stores only the leaf cells. Aggregated values are stored
 * in a flexible cache.
 * 
 * @author Florian Lippisch
 */
public class CubePreCache extends CubeFlexCalc {

	private static final long serialVersionUID = 2L;
	
	protected boolean autoCachePaths = false;
	protected boolean enableBuildIndex = false;
	protected Collection<CachePath> cachePaths = new HashSet<CachePath>();
	protected Collection<CachePath> newCachePaths = new HashSet<CachePath>();
	protected Collection<Key> newCacheKeys = new HashSet<Key>();
	protected Collection<CachePathCellAggregatedEvent> newCachePathCellAggregatedEvents = new ArrayList<CachePathCellAggregatedEvent>();

	protected transient boolean buildCacheForPaths = false;
	protected transient boolean buildCacheForPathsAgain = false;
	protected transient int buildCacheForPathsTime = 0;
	protected transient int buildCacheForPathsTimeout = 0;
	
	// Commons log
	protected transient Log log;
	{
		log = LogFactory.getLog(CubePreCache.class);
	}

	public class CachePath implements Serializable {
		private static final long serialVersionUID = 1L;

		private CachePathDimensionDepth[] dimensionsDepth;
		
		public CachePath(Key key) {
			dimensionsDepth = new CachePathDimensionDepth[dimensionMap.size()];
			
			for (int i = 0; i < dimensionMap.size(); i++) {
				IDimensionElement element = key.getDimensionElement(i);
				dimensionsDepth[i] = new CachePathDimensionDepth(i);
				int depth = element.getDepth();
				if (dimensionBehavior[i].isFlagged(DimensionBehavior.FLAG_NO_AGGREGATION)) {
					// for disabled dimension aggregation use -1 as the depth
					depth = -1;
				}
				// get depth, for disabled aggregation use depth = 0
				// int depth = dimensionBehavior[i].isFlagged(DimensionBehavior.FLAG_NO_AGGREGATION) ? 0 : element.getDepth();
				dimensionsDepth[i].depth = depth;
			}
		}
		/*
		public boolean matches(Key key) {
			for (int i = 0; i < dimensionsDepth.length; i++) {
				CachePathDimensionDepth path = dimensionsDepth[i];
				if (path == null) {
					continue;
				}
				if (!path.matches(key)) {
					return false;
				}
			}
			return true;
		}
		*/
		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + Arrays.hashCode(dimensionsDepth);
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
			CachePath other = (CachePath) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (!Arrays.equals(dimensionsDepth, other.dimensionsDepth))
				return false;
			return true;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			for (CachePathDimensionDepth depth : dimensionsDepth) {
				sb.append(depth);
			}
			return sb.toString();
		}

		private CubePreCache getOuterType() {
			return CubePreCache.this;
		}

		/**
		 * @param rawKey
		 * @return
		 */
		public Key makePathKey(Key rawKey) {
			Key key = createNewKey(new IDimensionElement[dimensionMap.size()]);
			for (CachePathDimensionDepth dimensionDepth : dimensionsDepth) {
				IDimensionElement element = rawKey.getDimensionElement(dimensionDepth.dimensionIndex);
				int depth = element.getDepth();
				// check for DimensionBehavior
				if (dimensionBehavior[dimensionDepth.dimensionIndex].isFlagged(DimensionBehavior.FLAG_NO_AGGREGATION)) {
					// dimension is not aggregated, so nothing to adjust here
				} else {
					for (; depth > dimensionDepth.depth; depth--) {
						element = element.getParent();
					}
				}
				key.setDimensionElement(dimensionDepth.dimensionIndex, element);
			}
			dimensionResolver.adjustKey(key, rawKey);
			return key;
		}
	}
	
	public class CachePathDimensionDepth implements Serializable {
		private static final long serialVersionUID = 1L;
		
		/** Dimension for cache path */
		int dimensionIndex;
		
		/** Depth level of cache path for this dimension */
		int depth;
		
		public CachePathDimensionDepth(int dimensionIndex) {
			this.dimensionIndex = dimensionIndex;
		}
		/*
		public boolean matches(Key key) {
			IDimensionElement element = key.getDimensionElement(dimensionIndex);
			if (element.getDepth() == depth) {
				return true;
			}
			return false;
		}
		*/
		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + depth;
			result = prime * result + dimensionIndex;
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
			CachePathDimensionDepth other = (CachePathDimensionDepth) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (depth != other.depth)
				return false;
			if (dimensionIndex != other.dimensionIndex)
				return false;
			return true;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append('[').append(new ArrayList<IDimension>(dimensionMap.values()).get(dimensionIndex));
			sb.append("/").append(depth).append(']');
			return sb.toString();
		}

		private CubePreCache getOuterType() {
			return CubePreCache.this;
		}
	}
	
	public class CachePathCellAggregatedEvent {
		protected Key childKey;
		protected ICell childCell;
		protected Key parentKey;
		protected ICell parentCell;

		public CellAggregatedEvent use(CellAggregatedEvent event) {
			event.setChildCell(childCell);
			event.setChildKey(childKey);
			event.setParentCell(parentCell);
			event.setParentKey(parentKey);
			return event;
		}
	}
	
	/**
	 * INTERNAL: This constructor is used by the serialization mechanism. 
	 */
	public CubePreCache() {
		super(); 
	}
	
	/**
	 * @param dataPool 
	 * @param key
	 * @param measures 
	 * @param dimensions 
	 */
	public CubePreCache(DataPool dataPool, String key, IDimension[] dimensions, IMeasure[] measures) {
		super(dataPool, key, dimensions, measures);
	}


	/**
	 * Creates a pre-cache Cube using flex-calc fields.
	 * The flexCube is replace in data pool with this new instance.
	 * This constructor should only be called when converting a flex cube to pre-cache.
	 * @param flexCube
	 */
	public CubePreCache(CubeFlexCalc flexCube) {
		this.allowSplash = flexCube.allowSplash;
		this.cache = flexCube.cache;
		this.cubeListeners = flexCube.cubeListeners;
		this.data = flexCube.data;
		this.dataPool = flexCube.dataPool;
		this.dimensionMap = flexCube.dimensionMap;
		this.dimensionResolver = flexCube.dimensionResolver;
		this.externalizeCache = flexCube.externalizeCache;
		this.key = flexCube.key;
		this.maxCacheSize = flexCube.maxCacheSize;
		this.measureMap = flexCube.measureMap;
		this.rootIndex = flexCube.rootIndex;
		this.title = flexCube.title;
		
		// replace cube
		dataPool.replaceCube(flexCube, this);
	}

	/* (non-Javadoc)
	 * @see java.io.Externalizable#readExternal(java.io.ObjectInput)
	 */
	@SuppressWarnings("unchecked")
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {

		int version = in.readInt();
		if (version < 1 || version > 3) {
			throw new IOException("Cannot deserialize cube -> data file version is " + version + ", but expected 1..2");
		}
		key = (String) in.readObject();
		title = (String) in.readObject();
		allowSplash = in.readBoolean();
		dataPool = (DataPool) in.readObject();
		dimensionMap = (Map<String, IDimension>) in.readObject();
		measureMap = (Map<String, IMeasure>) in.readObject();
		
		cubeListeners = (List<ICubeListener>)in.readObject();
		dimensionResolver = (IDimensionResolver)in.readObject();
		
		dimensionBehavior = new DimensionBehavior[dimensionMap.size()];
		for (int i = 0; i < dimensionBehavior.length; i++) {
			dimensionBehavior[i] = DimensionBehavior.DEFAULT;
		}
		
		if (version > 1) {
			keyProvider = (IKeyProvider)in.readObject();
			cellProvider = (ICellProvider)in.readObject();
			if (version > 2) {
				for (int i = 0; i < dimensionBehavior.length; i++) {
					dimensionBehavior[i] = (DimensionBehavior)in.readObject();
				}
			}
		}
		
		serializeData = in.readBoolean();

		int size = 0;
		int dimSize = dimensionMap.size();
		
		// read data
		if (!serializeData) {
			// optimized data read
			size = in.readInt();
			
			data = newHashMap(size);
			for (int i = 0; i < size; i++) {
				Key key = createNewKey(null);
				key.readObject(in, dimSize);
				Cell cell = (Cell)in.readObject();
				data.put(key, cell);
			}
		} else {
			// customer Key implementation
			data = (Map<Key, ICell>)in.readObject();
		}
		
		externalizeCache = in.readBoolean();
		// read cache paths settings
		autoCachePaths = in.readBoolean();
		if (autoCachePaths && externalizeCache) {
			cachePaths = (Collection<CachePath>)in.readObject();
			newCachePaths = (Collection<CachePath>)in.readObject();
			newCacheKeys = (Collection<Key>)in.readObject();
		}

		if (externalizeCache) {
			size = in.readInt();
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
		
		buildIndex();
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
		out.writeObject(cubeListeners);
		out.writeObject(dimensionResolver);
		out.writeObject(keyProvider);
		out.writeObject(cellProvider);
		
		for (int i = 0; i < dimensionBehavior.length; i++) {
			out.writeObject(dimensionBehavior[i]);
		}

		out.writeBoolean(serializeData);
		
		// write data...
		if (!serializeData) {
			out.writeInt(data.size());
			for(Entry<Key, ICell> entry: data.entrySet()) {
				
				for (IDimensionElement elm : entry.getKey().getDimensionElements()) {
					out.writeObject(elm);
				}
				out.writeObject(entry.getValue());
				
			}
		} else {
			out.writeObject(data);
		}
	
		// save cache
		out.writeBoolean(externalizeCache);

		// write cache paths settings
		out.writeBoolean(autoCachePaths);
		if (autoCachePaths && externalizeCache) {
			out.writeObject(cachePaths);
			out.writeObject(newCachePaths);
			out.writeObject(newCacheKeys);
		}
		
		if (externalizeCache) {
			out.writeInt(cache.size());
			for(Entry<Key, CachedCell> entry: cache.entrySet()) {
				
				out.writeObject(entry.getKey());
				out.writeObject(entry.getValue());
				
			}
		}		
	}
	
	/**
	 * Data has been written or the cube has been cleared. 
	 */
	public void clearCache() {
		super.clearCache();
		clearCachePaths();
	}

	/**
	 * Clear cache paths.
	 */
	public void clearCachePaths() {
		// clear cache paths
		cachePaths.clear();
		newCachePaths.clear();
		newCacheKeys.clear();
		newCachePathCellAggregatedEvents.clear();
	}
	
	/**
	 * Updates cache with newCachePaths identified in autoCachePaths mode.
	 * When complete, CachePaths are moved to cachePaths set.
	 */
	public synchronized void buildCacheForPaths() {

		if (newCachePaths.size() == 0) {
			// nothing to calculate
			return;
		}
		
		if (buildCacheForPaths) {
			// signal to build cache again
			buildCacheForPathsAgain = true;
			return;
		}
		
		buildCacheForPaths = true;
		try {
			
			String cubeInfo = "Cube '" + getKey() + "': ";
			
			log.info(cubeInfo + "building cache for " + newCachePaths.size() + " new paths: " + newCachePaths);

			long start = System.currentTimeMillis();
			
			do {
			
				// remove newCacheKeys from cache, so cells are not double aggregated
				for (Key key : newCacheKeys) {
					cache.remove(key);
				}
				newCacheKeys.clear();
				
				if (buildCacheForPathsAgain) {
					// clear previous build cache
					for(Iterator<Key> it = cache.keySet().iterator(); it.hasNext();) {
						Key key = it.next();
						CachePath cachePath = new CachePath(key);
						if (newCachePaths.contains(cachePath)) {
							// remove element
							it.remove();
						}
					}
					buildCacheForPathsAgain = false;
				}
				
				// iterate all raw (leaf) cells and calculate the paths
				for(Entry<Key, ICell> entry: data.entrySet()) {
					Key rawKey = entry.getKey();
					ICell rawCell = entry.getValue();
					cachePathCell(rawKey, rawCell);
				}
				
				// throw collected event, might end in recursive call due to getCell
				if (newCachePathCellAggregatedEvents.size() > 0) {
					CellAggregatedEvent cae = new CellAggregatedEvent();
					cae.setCube(this);
					for (CachePathCellAggregatedEvent event : newCachePathCellAggregatedEvents) {
						// might call getCell again that would end up in recursive call here again, identified with buildCacheForPathsAgain
						onCellAggregated(event.use(cae));
						if (buildCacheForPathsAgain) {
							// stop build
							break;
						}
					}
					newCachePathCellAggregatedEvents.clear();
				}
			} while (buildCacheForPathsAgain); 
			
			// move paths and clear newCachePaths
			cachePaths.addAll(newCachePaths);
			newCachePaths.clear();

			// set new calculation time
			calcCellTime = (int)(System.currentTimeMillis() - start);
			
			log.info(cubeInfo + "build cache finished in " + calcCellTime + " msec. (total cache size: " + cache.size() + ")");
			
		} finally {
			buildCacheForPaths = false;
		}
		
	}

	/**
	 * Checks if key's path is cache already or not. It fills newCachePaths for new paths.
	 * @param key
	 * @return
	 */
	protected boolean isCachedKey(Key key) {
		if (!autoCachePaths) {
			return false;
		}
		
		CachePath newPath = new CachePath(key);
		
		if (newCachePaths.contains(newPath)) {
			return false;
		}
		
		if (cachePaths.contains(newPath)) {
			return true;
		}
		
		// new cache path, use for next build run
		newCachePaths.add(newPath);
		
		return false;
	}
	
	@Override
	protected ICell probeCachedCell(Key key, boolean createNew) {
		if (autoCachePaths) {
			int timeout = buildCacheForPathsTimeout;
			if (timeout == 0 || timeout < buildCacheForPathsTime) {
				// use last buildCache time
				timeout = buildCacheForPathsTime;
			}
			if (timeout == 0) {
				// initial default timeout set to 1000 msec
				timeout = 1000;
			}
			if (calcCellTime >= timeout) {
				calcCellTime = 0;
				// build new cache from scratch
				buildCacheForPaths();
				// recursive call to getCell() should NOT end in an infinite loop!
				return getCell(key, createNew);
			}
		}
		return null;
	}

	@Override
	protected synchronized void addNewCachedKey(Key key) {
		if (autoCachePaths) {
			if (newCacheKeys == null) {
				newCacheKeys = new HashSet<Key>();
			}
			newCacheKeys.add(key.clone());
		}
	}

	/**
	 * Caches and aggregate rawCell data to cachePaths.
	 * @param key
	 * @param rawCell
	 */
	protected void cachePathCell(Key key, ICell rawCell) {
		
		for (CachePath path : newCachePaths) {
			Key k = path.makePathKey(key);
			if (k == null) {
				// don't use this cell on this CachePath
				continue;
			}
			CachedCell cc = cache.get(k);
			if (cc == null) {
				cc = new CachedCell(createNewCell(k, measureMap.size()));
				cache.put(k, cc);
			}
			// aggregate rawCell
			aggregateCells(cc.cell, rawCell);

			if (cubeListeners.size() > 0) {
				// invoke ICubeListener
				CachePathCellAggregatedEvent event = new CachePathCellAggregatedEvent();
				event.childKey = key;
				event.childCell = rawCell;
				event.parentKey = k;
				event.parentCell = cc.cell;

				// moved to buildCachePaths due to infinite loops might occur
				newCachePathCellAggregatedEvents.add(event);
			}
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
	
	/**
	 * @return the autoCachePaths
	 */
	public boolean isAutoCachePaths() {
		return autoCachePaths;
	}

	/**
	 * @param autoCachePaths the autoCachePaths to set
	 */
	public void setAutoCachePaths(boolean autoCachePaths) {
		this.autoCachePaths = autoCachePaths;
	}

	/**
	 * @return the buildCacheForPathsTimeout
	 */
	public int getBuildCacheForPathsTimeout() {
		return buildCacheForPathsTimeout;
	}

	/**
	 * @param buildCacheForPathsTimeout the buildCacheForPathsTimeout to set
	 */
	public void setBuildCacheForPathsTimeout(int buildCacheForPathsTimeout) {
		this.buildCacheForPathsTimeout = buildCacheForPathsTimeout;
	}

	/**
	 * @param externalizeCache the externalizeCache to set
	 */
	public void setExternalizeCache(boolean externalizeCache) {
		this.externalizeCache = externalizeCache;
	}

	@Override
	public CubeType getCubeType() {
		return CubeType.PRE_CACHE;
	}
	
	@Override
	protected void buildIndex() {
		if (enableBuildIndex) {
			super.buildIndex();
		}
	}

	/**
	 * @return the enableBuildIndex
	 */
	public boolean isEnableBuildIndex() {
		return enableBuildIndex;
	}

	/**
	 * @param enableBuildIndex the enableBuildIndex to set
	 */
	public void setEnableBuildIndex(boolean enableBuildIndex) {
		if (this.enableBuildIndex != enableBuildIndex && !enableBuildIndex) {
			// disable build index by clearing the map
			rootIndex.clear();
		}
		this.enableBuildIndex = enableBuildIndex;
	}
	
	
}
