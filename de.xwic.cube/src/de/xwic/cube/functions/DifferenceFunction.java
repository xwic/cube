/**
 * 
 */
package de.xwic.cube.functions;

import java.io.Serializable;

import de.xwic.cube.ICell;
import de.xwic.cube.ICube;
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
	private int measureIndexA = -1;
	private int measureIndexB = -1;
	private boolean asPercent = false;
	private boolean negate = false;

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
	public DifferenceFunction(ICube cube, IMeasure measureA, IMeasure measureB) {
		super();
		this.measureA = measureA;
		this.measureB = measureB;
	}

	/* (non-Javadoc)
	 * @see de.xwic.cube.IMeasureFunction#computeValue(de.xwic.cube.Key, de.xwic.cube.ICell, de.xwic.cube.IMeasure)
	 */
	public Double computeValue(ICube cube, Key key, ICell cell, IMeasure measure) {
		
		// cache the measure index on first use. This does not
		// need to be synchronized because if it would realy
		// happen that an initial call is done in two ore more threads,
		// the result would still be the same.
		
		if (measureIndexA == -1) {
			measureIndexA = cube.getMeasureIndex(measureA);
			measureIndexB = cube.getMeasureIndex(measureB);
		}
		
		if (cell != null) {
			Double valA = cell.getValue(measureIndexA);
			Double valB = cell.getValue(measureIndexB);
			if (valA == null) {
				valA = 0.0;
			} 
			if (valB == null) {
				valB = 0.0;
			}
			double diff = valA - valB;
			if (negate) {
				diff = -diff;
			}
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

	/**
	 * @return the negate
	 */
	public boolean isNegate() {
		return negate;
	}

	/**
	 * @param negate the negate to set
	 */
	public void setNegate(boolean negate) {
		this.negate = negate;
	}

}
