/**
 * 
 */
package de.xwic.cube;

/**
 * A function implementation for a measure.
 * @author Florian Lippisch
 */
public interface IMeasureFunction {

	/**
	 * @param measure 
	 * @param cell 
	 * @param key 
	 * @return
	 */
	Double computeValue(ICube cube, Key key, ICell cell, IMeasure measure);

}
