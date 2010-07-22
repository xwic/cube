/**
 * $Id: $
 * 
 * @author JBORNEMA
 */
package de.xwic.cube;



/**
 * @author JBORNEMA
 */

public interface ICellLoader {

	/**
	 * Implements customer updates on cell based on action and parameter.
	 * In addition the cube and key of given cell is available.
	 * @param cube
	 * @param key
	 * @param cell
	 * @param action
	 * @param parameter
	 */
	 void updateCell(ICube cube, Key key, ICell cell, Object action, Object parameter);

}
