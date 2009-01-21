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
public class PercentageValueFormat implements IValueFormat {

	private NumberFormat nf;
	
	/**
	 * Create a percentage value with custom locale.
	 * @param locale
	 */
	public PercentageValueFormat(Locale locale) {
		nf = NumberFormat.getNumberInstance(locale);
		nf.setMaximumFractionDigits(2);
		nf.setMinimumFractionDigits(0);
	}
	
	/* (non-Javadoc)
	 * @see de.xwic.cube.IValueFormat#format(java.lang.Double)
	 */
	public String format(Double value) {
		if (value != null) {
			return "<nobr>" + nf.format(value.doubleValue() * 100) + " %</nobr>";
		}
		return null;
	}

}
