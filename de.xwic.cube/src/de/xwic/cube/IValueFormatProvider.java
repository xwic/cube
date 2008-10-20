/**
 * 
 */
package de.xwic.cube;

import java.io.Serializable;
import java.util.Locale;

/**
 * Creates a ValueFormat.
 * @author Florian Lippisch
 */
public interface IValueFormatProvider extends Serializable {

	/**
	 * Create a new ValueFormat.
	 * @param locale
	 * @return
	 */
	public IValueFormat createValueFormat(Locale locale);
	
}
