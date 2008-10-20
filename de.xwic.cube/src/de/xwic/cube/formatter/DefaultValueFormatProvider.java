/**
 * 
 */
package de.xwic.cube.formatter;

import java.util.Locale;

import de.xwic.cube.IValueFormat;
import de.xwic.cube.IValueFormatProvider;

/**
 * @author Florian Lippisch
 */
public class DefaultValueFormatProvider implements IValueFormatProvider {

	/* (non-Javadoc)
	 * @see de.xwic.cube.IValueFormatProvider#createValueFormat(java.util.Locale)
	 */
	public IValueFormat createValueFormat(Locale locale) {
		return new DefaultValueFormat(locale);
	}

}
