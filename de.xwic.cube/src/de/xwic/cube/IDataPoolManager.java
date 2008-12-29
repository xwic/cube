/**
 * 
 */
package de.xwic.cube;

import java.util.List;


/**
 * @author Florian Lippisch
 */
public interface IDataPoolManager {

	/**
	 * Create a new DataPool.
	 * @return
	 */
	public abstract IDataPool createDataPool(String key);

	/**
	 * Release the DataPool from memory. The DataPoolManager will 
	 * then clear any references to the object so that the garbage collector
	 * can free up the memory - which will only work if no more references
	 * exist...
	 * @param pool
	 */
	public abstract void releaseDataPool(IDataPool pool);
	
	/**
	 * Load a pool from the storage. 
	 * @param key
	 * @return
	 * @throws StorageException 
	 */
	public abstract IDataPool getDataPool(String key) throws StorageException;

	/**
	 * Returns true if a DataPool with that key exists.
	 * @param key
	 * @return
	 * @throws StorageException 
	 */
	public abstract boolean containsDataPool(String key) throws StorageException;
	
	/**
	 * List the available DataPool keys.
	 * @return
	 * @throws StorageException
	 */
	public abstract List<String> listDataPoolKeys() throws StorageException;
}