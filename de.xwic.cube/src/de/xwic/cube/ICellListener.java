/**
 * 
 */
package de.xwic.cube;

/**
 * @author Oleksiy Samokhvalov
 * 
 */
public interface ICellListener {
	/**
	 * Called on every cell. If it returns false, the further cell processing is
	 * terminated.
	 * 
	 * @param key
	 * @param cell
	 * @return
	 */
	boolean onCell(Key key, ICell cell);
}
