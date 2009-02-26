/**
 * 
 */
package de.xwic.cube;

import de.xwic.cube.impl.DataPoolManager;

/**
 * @author Florian Lippisch
 */
public abstract class DataPoolManagerFactory {

	/**
	 * R
	 * @return
	 */
	public static IDataPoolManager createDataPoolManager(IDataPoolStorageProvider storageProvider) {
		return new DataPoolManager(storageProvider);
	}
	
}
