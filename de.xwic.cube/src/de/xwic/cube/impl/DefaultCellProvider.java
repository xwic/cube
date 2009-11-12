/**
 * 
 */
package de.xwic.cube.impl;

import de.xwic.cube.ICell;
import de.xwic.cube.ICellProvider;
import de.xwic.cube.Key;

/**
 * @author jbornema
 *
 */
public class DefaultCellProvider implements ICellProvider {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/* (non-Javadoc)
	 * @see de.xwic.cube.ICellProvider#createCell(int)
	 */
	public ICell createCell(Key key, int measureSize) {
		return new Cell(measureSize);
	}

}
