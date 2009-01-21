/**
 * 
 */
package de.xwic.cube.storage.impl;

import java.util.List;

import de.xwic.cube.IDataPool;
import de.xwic.cube.IDataPoolStorageProvider;
import de.xwic.cube.StorageException;

/**
 * @author lippisch
 *
 */
public class JDBCDataPoolStorageProvider implements IDataPoolStorageProvider {

	/* (non-Javadoc)
	 * @see de.xwic.cube.IDataPoolStorageProvider#containsDataPool(java.lang.String)
	 */
	public boolean containsDataPool(String key) throws StorageException {
		return false;
	}

	/* (non-Javadoc)
	 * @see de.xwic.cube.IDataPoolStorageProvider#deleteDataPool(java.lang.String)
	 */
	public void deleteDataPool(String key) {

	}

	/* (non-Javadoc)
	 * @see de.xwic.cube.IDataPoolStorageProvider#listDataPools()
	 */
	public List<String> listDataPools() throws StorageException {
		return null;
	}

	/* (non-Javadoc)
	 * @see de.xwic.cube.IDataPoolStorageProvider#loadDataPool(java.lang.String)
	 */
	public IDataPool loadDataPool(String key) throws StorageException {
		return null;
	}

	/* (non-Javadoc)
	 * @see de.xwic.cube.IDataPoolStorageProvider#saveDataPool(de.xwic.cube.IDataPool)
	 */
	public void saveDataPool(IDataPool dataPool) throws StorageException {

	}

}
