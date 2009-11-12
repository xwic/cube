/**
 * 
 */
package de.xwic.cube.impl;

import de.xwic.cube.IDimensionElement;
import de.xwic.cube.Key;
import de.xwic.cube.IKeyProvider;

/**
 * @author jbornema
 *
 */
public class DefaultKeyProvider implements IKeyProvider {

	/* (non-Javadoc)
	 * @see de.xwic.cube.IKeyProvider#createNewKey(de.xwic.cube.IDimensionElement[])
	 */
	public Key createNewKey(IDimensionElement[] elements) {
		return new Key(elements);
	}

}
