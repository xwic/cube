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

	private int minFractionDigits = 2;
	private int maxFractionDigits = 2;
	
	
	/**
	 * 
	 */
	public DefaultValueFormatProvider() {
		super();
	}

	/**
	 * @param fractionDigits Default is 2
	 */
	public DefaultValueFormatProvider(int minFractionDigits) {
		super();
		this.minFractionDigits = minFractionDigits;
	}

	
	
	/**
	 * @param minFractionDigits Default is 2
	 * @param maxFractionDigits Default is 2
	 */
	public DefaultValueFormatProvider(int minFractionDigits, int maxFractionDigits) {
		super();
		this.minFractionDigits = minFractionDigits;
		this.maxFractionDigits = maxFractionDigits;
	}

	/* (non-Javadoc)
	 * @see de.xwic.cube.IValueFormatProvider#createValueFormat(java.util.Locale)
	 */
	public IValueFormat createValueFormat(Locale locale) {
		return new DefaultValueFormat(locale, minFractionDigits, maxFractionDigits);
	}

}
