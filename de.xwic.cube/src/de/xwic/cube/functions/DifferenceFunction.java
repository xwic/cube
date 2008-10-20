/**
 * 
 */
package de.xwic.cube.functions;

import java.io.Serializable;

import de.xwic.cube.ICell;
import de.xwic.cube.IMeasure;
import de.xwic.cube.IMeasureFunction;
import de.xwic.cube.Key;

/**
 * This function computes the difference between two other measures. The
 * result may either be returned as absolute or percentage to measure B.
 * @author Florian Lippisch
 */
public class DifferenceFunction implements IMeasureFunction, Serializable {
	
	private IMeasure measureA;
	private IMeasure measureB;
	private boolean asPercent = false;

	/**
	 * @param measureA
	 * @param measureB
	 * @param asPercent
	 */
	public DifferenceFunction(IMeasure measureA, IMeasure measureB, boolean asPercent) {
		super();
		this.measureA = measureA;
		this.measureB = measureB;
		this.asPercent = asPercent;
	}

	/**
	 * @param measureA
	 * @param measureB
	 */
	public DifferenceFunction(IMeasure measureA, IMeasure measureB) {
		super();
		this.measureA = measureA;
		this.measureB = measureB;
	}

	/* (non-Javadoc)
	 * @see de.xwic.cube.IMeasureFunction#computeValue(de.xwic.cube.Key, de.xwic.cube.ICell, de.xwic.cube.IMeasure)
	 */
	public Double computeValue(Key key, ICell cell, IMeasure measure) {
		if (cell != null) {
			Double valA = cell.getValue(measureA);
			Double valB = cell.getValue(measureB);
			if (valA == null) {
				valA = 0.0;
			} else if (valB == null) {
				valB = 0.0;
			}
			double diff = valA - valB;
			if (asPercent) {
				if (valB == 0.0) {
					return null;
				}
				return (diff / valB);
			}
			return diff;
		}
		return null;
	}

	/**
	 * @return the asPercent
	 */
	public boolean isAsPercent() {
		return asPercent;
	}

	/**
	 * @param asPercent the asPercent to set
	 */
	public void setAsPercent(boolean asPercent) {
		this.asPercent = asPercent;
	}

}
