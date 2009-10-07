/**
 * 
 */
package de.xwic.cube;

import java.util.Collection;

/**
 * Container for dimensions, measures and cubes.
 * @author Florian Lippisch
 */
public interface IDataPool extends IIdentifyable {

	public enum CubeType {
		/**
		 * The default cube implementation calculates and stores all aggregation variations.
		 * It is the fastest cube, but requires the most memory.
		 */
		DEFAULT,
		
		/**
		 * This variation stores only the leaf values permanently. Aggregated values are cached.
		 */
		FLEX_CALC,
		
		/**
		 * This cube extends FLEX_CALC with pre-caching (key patch caching) when aggregated cell
		 * calculation exceeds a given threshold - buildCacheForPathsTimeout (by default 1000 msec) 
		 */
		PRE_CACHE
	}
	
	/**
	 * Returns the list of dimensions.
	 * @return the dimensions
	 */
	public abstract Collection<IDimension> getDimensions();

	/**
	 * Create a new dimension.
	 * @param key
	 * @return
	 */
	public abstract IDimension createDimension(String key);

	/**
	 * Returns the dimension with the specified key. If the dimension does not exist, 
	 * an IllegalArgumentException is thrown. 
	 * @param key
	 * @return
	 */
	public abstract IDimension getDimension(String key);

	/**
	 * Returns true if the specified dimension exists.
	 * @param key
	 * @return
	 */
	public abstract boolean containsDimension(String key);

	/**
	 * @return the cubes
	 */
	public abstract Collection<ICube> getCubes();

	/**
	 * Create a new cube with the default implementation.
	 * @param key
	 * @return
	 */
	public abstract ICube createCube(String key, IDimension[] dimensions,
			IMeasure[] measures);

	/**
	 * Create a new cube with the specified implementation type.
	 * @param key
	 * @return
	 */
	public abstract ICube createCube(String key, IDimension[] dimensions, IMeasure[] measures, CubeType type);

	
	/**
	 * Returns the cube with the specified key. If the cube does not exist, 
	 * an IllegalArgumentException is thrown. 
	 * @param key
	 * @return
	 */
	public abstract ICube getCube(String key);

	/**
	 * Returns true if the specified cube exists.
	 * @param key
	 * @return
	 */
	public abstract boolean containsCube(String key);

	/**
	 * @return the measures
	 */
	public abstract Collection<IMeasure> getMeasures();

	/**
	 * Create a new cube.
	 * @param key
	 * @return
	 */
	public abstract IMeasure createMeasure(String key);

	/**
	 * Returns the measure with the specified key. If the measure does not exist, 
	 * an IllegalArgumentException is thrown. 
	 * @param key
	 * @return
	 */
	public abstract IMeasure getMeasure(String key);

	/**
	 * Returns true if the specified measure exists.
	 * @param key
	 * @return
	 */
	public abstract boolean containsMeasure(String key);

	/**
	 * Persist the DataPool. 
	 * @throws StorageException
	 */
	public abstract void save() throws StorageException;

	/**
	 * Delete the DataPool (both from memory and the underlying storage).
	 * @throws StorageException
	 */
	public abstract void delete() throws StorageException;

	/**
	 * Returns the DimensionElement specified in the id. The id is created
	 * from IDimensionElement.getId(). Sample:
	 * <p>[GEO:EMEA/Germany]
	 * 
	 * @param id
	 * @return
	 */
	public abstract IDimensionElement parseDimensionElementId(String id);
	
}