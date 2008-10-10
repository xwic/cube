package de.xwic.cube.impl;

import java.util.HashMap;
import java.util.Map;

import de.xwic.cube.IDataPool;
import de.xwic.cube.IDataPoolManager;

/**
 * Manages all pool instances.
 * 
 * @author Florian Lippisch
 */
public class DataPoolManager implements IDataPoolManager {

	private String id = null;
	private Map<String, DataPool> poolMap = new HashMap<String, DataPool>();
	
	
	
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
		DataPool pool = new DataPool(this, key);
		poolMap.put(key, pool);
		return pool;
	}



	/**
	 * @param dataPool
	 */
	public void saveDataPool(DataPool dataPool) {
		// TODO Auto-generated method stub
		
	}
	
}
