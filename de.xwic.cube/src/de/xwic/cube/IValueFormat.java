/**
 * 
 */
package de.xwic.cube;

/**
 * Formats cell values.
 * @author Florian Lippisch
 */
public interface IValueFormat {

	/**
	 * Format the specified value.
	 * @param value
	 * @return
	 */
	public String format(Double value);
	
}
