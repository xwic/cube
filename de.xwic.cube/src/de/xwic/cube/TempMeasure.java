/**
 * 
 */
package de.xwic.cube;

import java.io.Serializable;

import de.xwic.cube.IMeasure;
import de.xwic.cube.IMeasureFunction;
import de.xwic.cube.IValueFormatProvider;
import de.xwic.cube.formatter.DefaultValueFormatProvider;
import de.xwic.cube.impl.Identifyable;

/**
 * This measure can be used to query data from a cube without the need to 
 * register the measure on the cube.
 * @author Florian Lippisch
 */
public class TempMeasure extends Identifyable implements IMeasure, Serializable {

	private static final long serialVersionUID = 4379759311293100433L;

	private IMeasureFunction function = null;
	private IValueFormatProvider formatProvider = new DefaultValueFormatProvider();
	
	/**
	 * @param dataPool 
	 * @param key
	 */
	public TempMeasure(String key, IMeasureFunction function) {
		super(key);
		this.function = function;
	}

	/**
	 * @param dataPool 
	 * @param key
	 */
	public TempMeasure(String key, IMeasureFunction function, IValueFormatProvider formatProvider) {
		super(key);
		this.function = function;
		this.formatProvider = formatProvider;
	}

	/* (non-Javadoc)
	 * @see de.xwic.cube.IMeasure#remove()
	 */
	public void remove() {
		
	}

	/**
	 * @return the function
	 */
	public IMeasureFunction getFunction() {
		return function;
	}

	/**
	 * @param function the function to set
	 */
	public void setFunction(IMeasureFunction function) {
		this.function = function;
	}

	/* (non-Javadoc)
	 * @see de.xwic.cube.IMeasure#isFunction()
	 */
	public boolean isFunction() {
		return function != null;
	}

	/**
	 * @return the formatProvider
	 */
	public IValueFormatProvider getValueFormatProvider() {
		return formatProvider;
	}

	/**
	 * @param formatProvider the formatProvider to set
	 */
	public void setValueFormatProvider(IValueFormatProvider formatProvider) {
		this.formatProvider = formatProvider;
	}

}
