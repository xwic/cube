/**
 * 
 */
package de.xwic.cube;

import java.io.Serializable;

/**
 * @author jbornema
 *
 */
public interface ICellProvider extends Serializable {

	public ICell createCell(Key key, int measureSize);
}
