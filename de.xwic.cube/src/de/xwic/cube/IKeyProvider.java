/**
 * 
 */
package de.xwic.cube;

/**
 * @author jbornema
 *
 */
public interface IKeyProvider {

	/**
	 * Creates a new Key. Used by Cube.createNewKey().
	 * @param elements
	 * @return
	 */
	public Key createNewKey(IDimensionElement[] elements);
}
