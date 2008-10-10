/**
 * 
 */
package de.xwic.cube;

/**
 * A cell contains the value for a specific key in a cube.
 * @author Florian Lippisch
 */
public interface ICell {

	/**
	 * Returns the value in the cell of the specified measure.
	 * @param measure
	 * @return
	 */
	public abstract Double getValue(IMeasure measure);

}