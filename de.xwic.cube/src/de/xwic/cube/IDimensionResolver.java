/**
 * 
 */
package de.xwic.cube;


/**
 * @author JBORNEMA
 *
 */
public interface IDimensionResolver {

	/**
	 * Like parentKey.isSubKey(childKey) returns true if it is a sub key, but can implement special logic. 
	 * @param parentKey
	 * @param childKey
	 * @return
	 */
	boolean isSubKey(Key parentKey, Key childKey);

	/**
	 * Before aggregation takes place the parentKey might be adjusted.
     * This special logic must be in sync with isSubKey method.
     */
	boolean adjustKey(Key parentKey, Key rawKey);

}
