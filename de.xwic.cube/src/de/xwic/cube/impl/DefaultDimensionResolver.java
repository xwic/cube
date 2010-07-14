/**
 * 
 */
package de.xwic.cube.impl;

import java.io.Serializable;

import de.xwic.cube.IDimensionResolver;
import de.xwic.cube.Key;

/**
 * @author JBORNEMA
 *
 */
public class DefaultDimensionResolver implements IDimensionResolver, Serializable {

	private static final long serialVersionUID = 7347660598177270783L;

	protected Cube cube;

	public DefaultDimensionResolver(Cube cube) {
		this.cube = cube;
	}
	
	/* (non-Javadoc)
	 * @see de.xwic.cube.IDimensionResolver#isSubKey(de.xwic.cube.Key, de.xwic.cube.Key, de.xwic.cube.impl.Cell)
	 */
	public boolean isSubKey(Key parentKey, Key rawKey) {
		return parentKey.isSubKey(rawKey, cube.dimensionBehavior);
	}

	/* (non-Javadoc)
	 * @see de.xwic.cube.IDimensionResolver#adjustKey(de.xwic.cube.Key, de.xwic.cube.Key)
	 */
	public boolean adjustKey(Key parentKey, Key rawKey) {
		return false;
	}
}
