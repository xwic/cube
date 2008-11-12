/**
 * 
 */
package de.xwic.cube.formatter;

import java.text.NumberFormat;
import java.util.Locale;

import de.xwic.cube.IValueFormat;

/**
 * @author Florian Lippisch
 */
public class DefaultValueFormat implements IValueFormat {

	private NumberFormat nf;
	
	/**
	 * Uses a default NumberFormat with 2 fraction digits and
	 * grouping. 
	 */
	public DefaultValueFormat(Locale locale, int minFractionDigits, int maxFractionDigits) {
		nf = NumberFormat.getInstance(locale);
		nf.setGroupingUsed(true);
		nf.setMinimumFractionDigits(minFractionDigits);
		nf.setMaximumFractionDigits(maxFractionDigits);
	}
	
	/**
	 * Create a format with custom NumberFormat.
	 * @param nf
	 */
	public DefaultValueFormat(NumberFormat nf) {
		this.nf = nf;
	}
	
	/* (non-Javadoc)
	 * @see de.xwic.cube.IValueFormat#format(java.lang.Double)
	 */
	public String format(Double value) {
		if (value != null) {
			return nf.format(value.doubleValue());
		}
		return null;
	}

}
