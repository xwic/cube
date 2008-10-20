/**
 * 
 */
package de.xwic.cube.impl;

import java.io.Serializable;

import de.xwic.cube.IMeasure;
import de.xwic.cube.IMeasureFunction;
import de.xwic.cube.IValueFormatProvider;
import de.xwic.cube.formatter.DefaultValueFormatProvider;

/**
 * @author Florian Lippisch
 */
public class Measure extends Identifyable implements IMeasure, Serializable {

	private static final long serialVersionUID = 4379759311293100433L;
	private final DataPool dataPool;

	private IMeasureFunction function = null;
	private IValueFormatProvider formatProvider = new DefaultValueFormatProvider();
	
	/**
	 * @param dataPool 
	 * @param key
	 */
	public Measure(DataPool dataPool, String key) {
		super(key);
		this.dataPool = dataPool;
	}

	/* (non-Javadoc)
	 * @see de.xwic.cube.IMeasure#remove()
	 */
	public void remove() {
		dataPool.removeMeasure(this);
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
