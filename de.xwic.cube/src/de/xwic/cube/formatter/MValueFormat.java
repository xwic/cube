/**
 * 
 */
package de.xwic.cube.formatter;

import java.text.NumberFormat;
import java.util.Locale;

import de.xwic.cube.IValueFormat;

/**
 * Displays the value in millions. 
 * Sample:
 * 1.234.000,50 = 1
 * 122.600.000,00 = 123
 * @author Florian Lippisch
 */
public class MValueFormat implements IValueFormat {

	private NumberFormat nf;
	
	/**
	 * Uses a default NumberFormat with 2 fraction digits and
	 * grouping. 
	 */
	public MValueFormat(Locale locale, int minFractionDigits, int maxFractionDigits) {
		nf = NumberFormat.getInstance(locale);
		nf.setGroupingUsed(true);
		nf.setMinimumFractionDigits(minFractionDigits);
		nf.setMaximumFractionDigits(maxFractionDigits);
	}
	
	/**
	 * Create a format with custom NumberFormat.
	 * @param nf
	 */
	public MValueFormat(NumberFormat nf) {
		this.nf = nf;
	}
	
	/* (non-Javadoc)
	 * @see de.xwic.cube.IValueFormat#format(java.lang.Double)
	 */
	public String format(Double value) {
		if (value != null) {
			return nf.format(value.doubleValue() / 1000000.0d);
		}
		return null;
	}

}
