/**
 * 
 */
package de.xwic.cube;

import java.io.Serializable;

/**
 * @author jbornema
 *
 */
public interface IKeyProvider extends Serializable {

	/**
	 * Creates a new Key. Used by Cube.createNewKey().
	 * @param elements
	 * @return
	 */
	public Key createNewKey(IDimensionElement[] elements);
}
