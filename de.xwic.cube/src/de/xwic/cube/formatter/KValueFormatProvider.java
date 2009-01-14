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
public class KValueFormatProvider implements IValueFormatProvider {

	private int minFractionDigits = 0;
	private int maxFractionDigits = 0;
	
	
	/**
	 * 
	 */
	public KValueFormatProvider() {
		super();
	}

	/**
	 * @param fractionDigits Default is 0
	 */
	public KValueFormatProvider(int minFractionDigits) {
		super();
		this.minFractionDigits = minFractionDigits;
	}

	
	
	/**
	 * @param minFractionDigits Default is 0
	 * @param maxFractionDigits Default is 0
	 */
	public KValueFormatProvider(int minFractionDigits, int maxFractionDigits) {
		super();
		this.minFractionDigits = minFractionDigits;
		this.maxFractionDigits = maxFractionDigits;
	}

	/* (non-Javadoc)
	 * @see de.xwic.cube.IValueFormatProvider#createValueFormat(java.util.Locale)
	 */
	public IValueFormat createValueFormat(Locale locale) {
		return new KValueFormat(locale, minFractionDigits, maxFractionDigits);
	}

}
