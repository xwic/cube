/**
 * 
 */
package de.xwic.cube.functions;

import java.io.Serializable;

import de.xwic.cube.ICell;
import de.xwic.cube.ICube;
import de.xwic.cube.IMeasure;
import de.xwic.cube.IMeasureFunction;
import de.xwic.cube.IUserObject;
import de.xwic.cube.Key;

/**
 * @author jbornema
 *
 */
public class CountFunction implements IMeasureFunction, Serializable {

	private static final long serialVersionUID = 1L;

	/* (non-Javadoc)
	 * @see de.xwic.cube.IMeasureFunction#computeValue(de.xwic.cube.ICube, de.xwic.cube.Key, de.xwic.cube.ICell, de.xwic.cube.IMeasure)
	 */
	@Override
	public Double computeValue(ICube cube, Key key, ICell cell, IMeasure measure) {
		IUserObject uo = IUserObject.Helper.getFirstUserObject(cell, key);
		if (uo != null) {
			Double size = IUserObject.Helper.getSize(uo.getUserObject());
			return size;
		}
		return null;
	}

}
