package de.xwic.cube.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.xwic.cube.IDataPool;
import de.xwic.cube.IDataPoolManager;
import de.xwic.cube.IDataPoolStorageProvider;
import de.xwic.cube.StorageException;

/**
 * Manages all pool instances.
 * 
 * @author Florian Lippisch
 */
public class DataPoolManager implements IDataPoolManager {

	private String id = null;
	private Map<String, IDataPool> poolMap = new HashMap<String, IDataPool>();
	private final IDataPoolStorageProvider storageProvider;
	
	
	
	/**
	 * @param storageProvider
	 */
	public DataPoolManager(IDataPoolStorageProvider storageProvider) {
		this.storageProvider = storageProvider;
	}



	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		final DataPoolManager other = (DataPoolManager) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}



	/* (non-Javadoc)
	 * @see de.xwic.cube.IDataPoolManager#createDataPool(java.lang.String)
	 */
	public IDataPool createDataPool(String key) {
		if (poolMap.containsKey(key)) {
			throw new IllegalArgumentException("A DataPool with that key already exists. (" + key + ")");
		}
		IDataPool pool = new DataPool(this, key);
		poolMap.put(key, pool);
		return pool;
	}



	/**
	 * @param dataPool
	 * @throws StorageException 
	 */
	void saveDataPool(IDataPool dataPool) throws StorageException {
		storageProvider.saveDataPool(dataPool);
	}



	/* (non-Javadoc)
	 * @see de.xwic.cube.IDataPoolManager#getDataPool(java.lang.String)
	 */
	public synchronized IDataPool getDataPool(String key) throws StorageException {
		if (poolMap.containsKey(key)) {
			return poolMap.get(key);
		} else {
			if (storageProvider.containsDataPool(key)) {
				DataPool pool = (DataPool)storageProvider.loadDataPool(key);
				if (pool == null) {
					throw new StorageException("StorageProvider.loadDataPool('" + key + "') returned NULL."); 
				}
				pool.setDataPoolManager(this);
				poolMap.put(key, pool);
				return pool;
			}
		}
		throw new IllegalArgumentException("A DataPool with that key does not exist.");
	}



	/* (non-Javadoc)
	 * @see de.xwic.cube.IDataPoolManager#releaseDataPool(de.xwic.cube.IDataPool)
	 */
	public void releaseDataPool(IDataPool pool) {
		try {
			pool.close();
		} catch (StorageException e) {
			throw new IllegalStateException("Error closing dataPool");
		}
		poolMap.remove(pool.getKey());
	}



	/**
	 * @param dataPool
	 */
	void deleteDataPool(IDataPool dataPool) {
		
		releaseDataPool(dataPool);
		storageProvider.deleteDataPool(dataPool.getKey());
		
	}
	
	/* (non-Javadoc)
	 * @see de.xwic.cube.IDataPoolManager#containsDataPool(java.lang.String)
	 */
	public boolean containsDataPool(String key) throws StorageException {
		return poolMap.containsKey(key) || storageProvider.containsDataPool(key);
	}

	/* (non-Javadoc)
	 * @see de.xwic.cube.IDataPoolManager#isDataPoolLoaded(java.lang.String)
	 */
	public boolean isDataPoolLoaded(String key) {
		return poolMap.containsKey(key);
	}
	
	/* (non-Javadoc)
	 * @see de.xwic.cube.IDataPoolManager#listDataPoolKeys()
	 */
	public List<String> listDataPoolKeys() throws StorageException {
		List<String> keys = storageProvider.listDataPools();
		for (String loadedKey : poolMap.keySet()) {
			if (!keys.contains(loadedKey)) {
				keys.add(loadedKey);
			}
		}
		return keys;
	}



	/**
	 * @return the storageProvider
	 */
	public IDataPoolStorageProvider getStorageProvider() {
		return storageProvider;
	}
	
}
