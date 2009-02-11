/**
 * 
 */
package de.xwic.cube;

import java.util.List;

/**
 * Used to query one or more values from the cube. While the Key object is
 * used to store and retrieve single values, the query is able to do retrieve
 * data based on more complex criteria. 
 * @author lippisch
 */
public interface IQuery {

	/**
	 * Clear all selections.
	 */
	public void clear();
	
	/**
	 * Clear the specified dimension.
	 * @param dimension
	 */
	public void clear(IDimension dimension);
	
	/**
	 * Selects the specified elements.
	 * @param elements
	 */
	public void selectDimensionElements(IDimensionElement... elements);
	
	/**
	 * Creates the list of keys that result in the specified selections.
	 * @return
	 */
	public List<Key> createKeys();
	
}
