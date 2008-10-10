/**
 * 
 */
package de.xwic.cube;

import java.util.List;

/**
 * Used to save and load a DataPool.
 * @author Florian Lippisch
 */
public interface IDataPoolStorageProvider {

	/**
	 * Stores the specified dataPool.
	 * @param dataPool
	 */
	public abstract void saveDataPool(IDataPool dataPool) throws StorageException ;
	
	/**
	 * Load the DataPool with the specified key.
	 * @param key
	 * @return
	 * @throws StorageException
	 */
	public abstract IDataPool loadDataPool(String key) throws StorageException;
	
	/**
	 * Returns a list of all DataPools that are stored. 
	 * @return
	 * @throws StorageException
	 */
	public abstract List<String> listDataPools() throws StorageException;
	
	/**
	 * Returns true if a DataPool with the specified key is stored.
	 * @param key
	 * @return
	 * @throws StorageException
	 */
	public abstract boolean containsDataPool(String key) throws StorageException;

	/**
	 * @param key
	 */
	public abstract void deleteDataPool(String key);
	
}
