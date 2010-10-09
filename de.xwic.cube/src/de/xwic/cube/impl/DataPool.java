/**
 * 
 */
package de.xwic.cube.impl;

import java.io.Serializable;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.xwic.cube.ICube;
import de.xwic.cube.IDataPool;
import de.xwic.cube.IDataPoolManager;
import de.xwic.cube.IDimension;
import de.xwic.cube.IDimensionElement;
import de.xwic.cube.IMeasure;
import de.xwic.cube.StorageException;

/**
 * @author Florian Lippisch
 */
public class DataPool extends Identifyable implements IDataPool, Serializable {

	private static final long serialVersionUID = -8857844492316508015L;
	private Map<String, ICube> cubeMap = new LinkedHashMap<String, ICube>();
	private transient Map<String, SoftReference<ICube>> softRefCubeMap = new LinkedHashMap<String, SoftReference<ICube>>();
	private Map<String, IDimension> dimensionMap = new LinkedHashMap<String, IDimension>();
	private Map<String, IMeasure> measureMap = new LinkedHashMap<String, IMeasure>();
	private transient DataPoolManager dataPoolManager;

	
	/**
	 * @param dataPoolManager 
	 * @param key
	 */
	public DataPool(DataPoolManager dataPoolManager, String key) {
		super(key);
		this.dataPoolManager = dataPoolManager;
	}
	
	/* (non-Javadoc)
	 * @see de.xwic.cube.IDataPool#getDimensions()
	 */
	public Collection<IDimension> getDimensions() {
		return Collections.unmodifiableCollection(dimensionMap.values());
	}

	/* (non-Javadoc)
	 * @see de.xwic.cube.IDataPool#createDimension(java.lang.String)
	 */
	public synchronized IDimension createDimension(String key) {
		if (dimensionMap.containsKey(key)) {
			throw new IllegalArgumentException("A dimension with that key already exists: " + key);
		}
		Dimension newDim = new Dimension(this, key);
		dimensionMap.put(key, newDim);
		return newDim;
	}
	
	/* (non-Javadoc)
	 * @see de.xwic.cube.IDataPool#getDimension(java.lang.String)
	 */
	public IDimension getDimension(String key) {
		IDimension dim = dimensionMap.get(key);
		if (dim == null) {
			throw new IllegalArgumentException("A dimension with that key does not exist: " + key);
		}
		return dim;
	}
	
	/* (non-Javadoc)
	 * @see de.xwic.cube.IDataPool#containsDimension(java.lang.String)
	 */
	public boolean containsDimension(String key) {
		return dimensionMap.containsKey(key);
	}
	
	/* (non-Javadoc)
	 * @see de.xwic.cube.IDataPool#getCubes()
	 */
	public Collection<ICube> getCubes() {
		List<ICube> result = new ArrayList<ICube>();
		for (Iterator<Entry<String, SoftReference<ICube>>> it = softRefCubeMap.entrySet().iterator(); it.hasNext();) {
			Entry<String, SoftReference<ICube>> entry = it.next();
			SoftReference<ICube> ref = entry.getValue();
			ICube cube = ref.get();
			if (cube != null) {
				result.add(cube);
			} else {
				softRefCubeMap.remove(entry.getKey());
			}
		}
		result.addAll(cubeMap.values());
		return result;
	}
	
	/* (non-Javadoc)
	 * @see de.xwic.cube.IDataPool#createCube(java.lang.String,
	 * de.xwic.cube.Dimension[], de.xwic.cube.Measure[])
	 */
	public synchronized ICube createCube(String key, IDimension[] dimensions, IMeasure[] measures) {
		return createCube(key, dimensions, measures, CubeType.DEFAULT);
	}

	/*(non-Javadoc)
	 * @see de.xwic.cube.IDataPool#createCube(java.lang.String,
	 * de.xwic.cube.Dimension[], de.xwic.cube.Measure[],
	 * de.xwic.cube.IDataPool.CubeType)
	 */
	public synchronized ICube createCube(String key, IDimension[] dimensions, IMeasure[] measures, CubeType type) {
		return createCube(key, dimensions, measures, type, false);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.xwic.cube.IDataPool#createCube(java.lang.String,
	 * de.xwic.cube.Dimension[], de.xwic.cube.Measure[],
	 * de.xwic.cube.IDataPool.CubeType)
	 */
	public synchronized ICube createCube(String key, IDimension[] dimensions, IMeasure[] measures, CubeType type, boolean softRerenced) {

		if (cubeMap.containsKey(key)) {
			throw new IllegalArgumentException("A cube with that key already exists: " + key + " (permanent)");
		} else if (softRefCubeMap.containsKey(key)) {
			SoftReference<ICube> ref = softRefCubeMap.get(key);
			if (ref != null && ref.get() != null) {
				throw new IllegalArgumentException("A cube with that key already exists: " + key +  "(softReferenced)");
			}
		} 
		ICube newCube;
		switch (type) {
		case FLEX_CALC:
			newCube = new CubeFlexCalc(this, key, dimensions, measures);
			break;
		case INDEXED:
			newCube = new CubeIndexed(this, key, dimensions, measures);
			break;
		case PRE_CACHE:
			newCube = new CubePreCache(this, key, dimensions, measures);
			break;
		default:
			newCube = new Cube(this, key, dimensions, measures);
		}
		if (!softRerenced) {
			cubeMap.put(key, newCube);
		} else {
			softRefCubeMap.put(key, new SoftReference<ICube>(newCube));
		}
		return newCube;
	}
	
	/* (non-Javadoc)
	 * @see de.xwic.cube.IDataPool#getCube(java.lang.String)
	 */
	public ICube getCube(String key) {
		ICube cube = cubeMap.get(key);
		if (cube == null) {
			SoftReference<ICube> ref = softRefCubeMap.get(key);
			if (ref != null) {
				ICube softRefCube = ref.get();
				if (softRefCube != null) {
					return softRefCube;
				} else {
					cubeMap.remove(key);
				}
			}
			throw new IllegalArgumentException("A cube with that key does not exist: " + key);
		} else {
			return cube;
		}
	}
	
	/* (non-Javadoc)
	 * @see de.xwic.cube.IDataPool#containsCube(java.lang.String)
	 */
	public boolean containsCube(String key) {

		boolean result = cubeMap.containsKey(key);
		if (!result) {
			SoftReference<ICube> ref = softRefCubeMap.get(key);
			if (ref != null) {
				ICube cube = ref.get();
				if (cube != null) {
					return true;
				} else {
					softRefCubeMap.remove(key);
				}
			}
			return false;
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see de.xwic.cube.IDataPool#getMeasures()
	 */
	public Collection<IMeasure> getMeasures() {
		return Collections.unmodifiableCollection(measureMap.values());
	}

	/* (non-Javadoc)
	 * @see de.xwic.cube.IDataPool#createMeasure(java.lang.String)
	 */
	public synchronized IMeasure createMeasure(String key) {
		if (measureMap.containsKey(key)) {
			throw new IllegalArgumentException("A measure with that key already exists: " + key);
		}
		Measure newMeasure = new Measure(this, key);
		measureMap.put(key, newMeasure);
		return newMeasure;
	}
	
	/* (non-Javadoc)
	 * @see de.xwic.cube.IDataPool#getMeasure(java.lang.String)
	 */
	public IMeasure getMeasure(String key) {
		IMeasure measure = measureMap.get(key);
		if (measure == null) {
			throw new IllegalArgumentException("A measure with that key does not exist: " + key);
		}
		return measure;
	}
	
	/* (non-Javadoc)
	 * @see de.xwic.cube.IDataPool#containsMeasure(java.lang.String)
	 */
	public boolean containsMeasure(String key) {
		return measureMap.containsKey(key);
	}

	/**
	 * INTERNAL: remove the specified dimension.
	 * @param dimension
	 */
	void removeDimension(Dimension dimension) {
		// TODO check if cubes exist that use this dimension.
		// TODO check that cube is empty (no cells exist).
		if (dimensionMap.containsKey(dimension.getKey())) {
			dimensionMap.remove(dimension.getKey());
		}
		
	}

	/**
	 * @param cube
	 */
	void removeCube(ICube cube) {
		if (cubeMap.containsKey(cube.getKey())) {
			cubeMap.remove(cube.getKey());
		} else if (softRefCubeMap.containsKey(cube.getKey())) {
			softRefCubeMap.remove(cube.getKey());
		}
		
	}

	/**
	 * @param measure
	 */
	void removeMeasure(Measure measure) {
		// TODO check if its used in a cube.
		if (measureMap.containsKey(measure.getKey())) {
			measureMap.remove(measure.getKey());
		}
		
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
//		final int prime = 31;
		int result = super.hashCode();
//		result = prime * result
//				+ ((dataPoolManager == null) ? 0 : dataPoolManager.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
//		final DataPool other = (DataPool) obj;
//		if (dataPoolManager == null) {
//			if (other.dataPoolManager != null)
//				return false;
//		} else if (!dataPoolManager.equals(other.dataPoolManager))
//			return false;
		return true;
	}

	/**
	 * @return the dataPoolManager
	 */
	public IDataPoolManager getDataPoolManager() {
		return dataPoolManager;
	}

	/**
	 * This method is invoked by the DataPoolManager after restoring/deserializing
	 * the DataPool. It is used to restore/recreate transient objects.
	 * @param dataPoolManager the dataPoolManager to set
	 */
	void setDataPoolManager(DataPoolManager dataPoolManager) {
		softRefCubeMap = new LinkedHashMap<String, SoftReference<ICube>>();
		this.dataPoolManager = dataPoolManager;
	}
	
	/**
	 * Store the DataPool. 
	 * @throws StorageException 
	 */
	public void save() throws StorageException {
		dataPoolManager.saveDataPool(this);
	}
	
	/* (non-Javadoc)
	 * @see de.xwic.cube.IDataPool#delete()
	 */
	public void delete() throws StorageException {
		dataPoolManager.deleteDataPool(this);
		
	}

	/* (non-Javadoc)
	 * @see de.xwic.cube.IDataPool#parseDimensionElementId(java.lang.String)
	 */
	public IDimensionElement parseDimensionElementId(String id) {
		/**
		 * Copied to CubeUtil.parseDimensionElementId(String) and used there as well ! ! !
		 */ 
		int start = id.indexOf('[');
		if (start == -1) {
			throw new IllegalArgumentException("Missing starting [");
		}
		int end = id.indexOf(']', start);
		if (end == -1) {
			throw new IllegalArgumentException("Missing ending ]");
		}
		String part = id.substring(start + 1, end);
		int idxDimSep = part.indexOf(':');
		String elmKeys;
		IDimension dimension;
		// does the key contain a dimension key?
		if (idxDimSep == -1) { // no key given
			throw new IllegalArgumentException("No dimension key found");
		}
		
		String dimKey = part.substring(0, idxDimSep);
		elmKeys = part.substring(idxDimSep + 1);
		dimension = getDimension(dimKey);
		IDimensionElement element = dimension;
		if (!"*".equals(elmKeys)) { // specific key given.
			int idxPathSep;
			int idxPathStart = 0;
			do {
				idxPathSep = elmKeys.indexOf('/', idxPathStart);
				String elmKey;
				if (idxPathSep == -1) {
					elmKey = elmKeys.substring(idxPathStart);
				} else {
					elmKey = elmKeys.substring(idxPathStart, idxPathSep);
				}
				element = element.getDimensionElement(elmKey);
				idxPathStart = idxPathSep + 1;
			} while (idxPathSep != -1);
		}
		return element;
	}
	
	/**
	 * replaces the old cube with the new cube
	 * @param oldCubeKey
	 * @param newCubeKey
	 * @param newCube
	 */
	void replaceCube(ICube oldCube, ICube newCube) {
		if (this != oldCube.getDataPool()) {
			throw new IllegalArgumentException("Cubes do not share the same DataPool");
		}
		if(cubeMap.containsKey(oldCube.getKey())) {
			cubeMap.put(oldCube.getKey(), newCube);
		} else if (softRefCubeMap.containsKey(oldCube.getKey())) {
			softRefCubeMap.put(oldCube.getKey(), new SoftReference<ICube>(newCube));
		}
	}
}
