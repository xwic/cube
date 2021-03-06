/**
 * 
 */
package de.xwic.cube;

import java.io.PrintStream;
import java.util.Collection;
import java.util.List;

import de.xwic.cube.IDataPool.CubeType;

/**
 * The cube contains a data structure based on a fixed number of dimensions. Data can 
 * be added into the cube, where they are automatically aggregated.
 * 
 * @author Florian Lippisch
 */
public interface ICube extends IIdentifyable {

	/**
	 * Returns the list of collections in this cube.
	 * @return
	 */
	public abstract Collection<IDimension> getDimensions();

	/**
	 * Returns the list of measures.
	 * @return
	 */
	public abstract Collection<IMeasure> getMeasures();

	/**
	 * Delete the cube.
	 */
	public abstract void remove();

	/**
	 * Create a Key from a list of dimension elements. Each specified element is placed into the key
	 * at its designated position. If a element is specified that is not used in the cube, an exception 
	 * is thrown.
	 * The elements do not have to be in a special order nor do you need to specify all elements used 
	 * in that cube.
	 *  
	 * @param elements
	 * @return
	 */
	public abstract Key createKey(IDimensionElement... elements);
	
	/**
	 * Create a Key from a string notation. The key string must contain a [] pair for
	 * each dimension in the cube. If the dimension key is not present in the key pair, 
	 * the dimension at the index position is assumed.
	 * </p>
	 * Some examples:
	 * <ul>
	 * <li>[GEO:EMEA/Germany][Time:2009/Q1/Apr]
	 * </ul>
	 * 
	 * @param key
	 * @return
	 * @throws IllegalArgumentException 
	 */
	public abstract Key createKey(String key);
	
	/**
	 * Create a Key from another Key object that has been created for another
	 * Cube. The new Key will be pre-filled by all dimension elements that 
	 * exist in the current cube. Non-existing dimensions are ignored.
	 * @param foreignKey
	 * @return
	 */
	public abstract Key createKey(Key foreignKey);
	
	/**
	 * Create a new Key. 
	 * @return
	 */
	public Key createKey();


	/**
	 * Create an empty query.
	 * @return
	 */
	public abstract IQuery createQuery();
	
	/**
	 * Create a new query.
	 * @param query
	 * @return
	 */
	public abstract IQuery createQuery(String query);
	
	/**
	 * Create a query for the specified key.
	 * @param key
	 * @return
	 */
	public abstract IQuery createQuery(Key key);
	
	/**
	 * Create a query for the specified query.
	 * @param query
	 * @return
	 */
	public IQuery createQuery(IQuery query);
	
	/**
	 * Returns the cell for the specified key (null if cell doesn't exist).
	 * @param key
	 * @return
	 */
	public abstract ICell getCell(Key key);

	
	/**
	 * Returns the cell for specified key. Non existing cell is created
	 * when createNew flag is set.
	 * @param key
	 * @param createNew
	 * @return
	 */
	public abstract ICell getCell(Key key, boolean createNew);
	
	/**
	 * Returns the DataPool this cube belongs too.
	 * @return
	 */
	public abstract IDataPool getDataPool();
	
	/**
	 * Notify the cube that a large number of data will be
	 * written. This helps the cube to optimize write
	 * performance.
	 */
	public abstract void beginMassUpdate();
	
	/**
	 * Notify the cube that writing of data is now completed so
	 * that the cube can perform operations to enhance read
	 * performance like indexing. 
	 */
	public abstract void massUpdateFinished();
	
	/**
	 * Add a value to the existing value in the specified cell.
	 * @param key
	 * @param measure
	 * @param value
	 * @return
	 */
	public int addCellValue(Key key, IMeasure measure, double value);
	
	/**
	 * Write the value into the cube.
	 * NOTE: This method is not thread safe and must be synchronized externally
	 * if the cube is modified in different threads.
	 * @return the number of cells modified.
	 * @param key
	 * @param measure
	 * @param value
	 */
	public abstract int setCellValue(Key key, IMeasure measure, double value);

	/**
	 * Returns the value in a cell or NULL if the cell contains no value.
	 * @param key
	 * @param measure
	 * @return
	 */
	public abstract Double getCellValue(Key key, IMeasure measure);
	
	/**
	 * Returns the value defined by the query.
	 * @param query
	 * @param measure
	 * @return
	 */
	public abstract Double getCellValue(IQuery query, IMeasure measure);
	
	/**
	 * Returns the value in a cell or NULL if the cell contains no value.
	 * Calls createKey(keyString) to create a Key that might throws an IllegalArgumentException.
	 * @param key
	 * @param measure
	 * @return
	 */
	public abstract Double getCellValue(String keyString, IMeasure measure);

	/**
	 * Empties all values in the cube.
	 * @deprecated - use clear()
	 */
	public abstract void reset();

	/**
	 * Empty all values in the cube.
	 */
	public abstract void clear();
	
	/**
	 * Empty all values for the specified measure.
	 * @param measure
	 */
	public abstract void clear(IMeasure measure);
	
	/**
	 * Empty all values for the specified measure and key.
	 * @param measure
	 * @param key
	 */
	public abstract void clear(IMeasure measure, Key key);

	/**
	 * Empty all values for the specified key and all measures.
	 * @param measure
	 * @param key
	 */
	public abstract void clear(Key key);

	/**
	 * Returns the key index position for this dimension.
	 * @param dimVert
	 * @return
	 */
	public abstract int getDimensionIndex(IDimension dimVert);
	
	/**
	 * Returns the number of filled cells in this cube.
	 * @return
	 */
	public int getSize();

	/**
	 * Returns the measure index.
	 * @param measure
	 * @return
	 */
	public int getMeasureIndex(IMeasure measure);
	
	/**
	 * Iterates through the cells.
	 * 
	 * @param listener
	 */
	public void forEachCell(ICellListener listener);

	/**
	 * Print cache details to the stream. The details
	 * are a list of keys in the cache, including the hit count
	 * and leaf count. Format:
	 * 
	 * [score]; [hit count]; [leaf count]; [unused count]; [key]
	 * 
	 * @param out
	 */
	public void printCacheProfile(PrintStream out);

	/**
	 * Returns list of ICubeListener.
	 * Use list to add or remove listeners.
	 * Never returns null.
	 * List is serialized.
	 * @return
	 */
	public List<ICubeListener> getCubeListeners();
	
	/**
	 * Sets this cube in place for the old cube (used for fast cube update without downtime)
	 * @param oldCube
	 */
	public void replace(ICube oldCube);

	/**
	 * @return the allowSplash
	 */
	public boolean isAllowSplash();

	/**
	 * @param allowSplash the allowSplash to set
	 */
	public void setAllowSplash(boolean allowSplash);
	
	/**
	 * Returns the CubeType defined in IDataPool.
	 * @return
	 */
	public CubeType getCubeType();
	
	/**
	 * Change the behavior of a dimension. This should be changed only when the cube is empty.
	 * @param dimension
	 * @param behavior
	 */
	public void setDimensionBehavior(IDimension dimension, DimensionBehavior behavior);
	
	/**
	 * Returns the behavior associated with the specified dimension. 
	 * @param dimension
	 * @return
	 */
	public DimensionBehavior getDimensionBehaivior(IDimension dimension);

	/**
	 * INTERNAL - Closes the cube and the underlying resources. This method
	 * is invoked by the DataPool when the pool is closed. 
	 * @throws StorageException 
	 */
	public abstract void close() throws StorageException;
	
}