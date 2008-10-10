/**
 * 
 */
package de.xwic.cube;


/**
 * @author Florian Lippisch
 */
public interface IDataPoolManager {

	/**
	 * Create a new DataPool.
	 * @return
	 */
	public abstract IDataPool createDataPool(String key);

}