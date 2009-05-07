/**
 * $Id: $
 *
 * Copyright (c) 2009 NetApp.
 * All rights reserved.

 * de.xwic.cube.IMeasureLoader.java
 * Created on Apr 22, 2009
 * 
 * @author JBORNEMA
 */
package de.xwic.cube;



/**
 * Created on Apr 22, 2009
 * @author JBORNEMA
 */

public interface IMeasureLoader extends ICubeListener {

	/**
	 * @param fromLoader
	 */
	void configure(IMeasureLoader fromLoader);

	/**
	 * 
	 */
	void clear();

	/**
	 * @param objectFocus
	 */
	void setObjectFocus(Object objectFocus);

	/**
	 * @param measureIndex
	 */
	void setMeasureIndex(int measureIndex);

	/**
	 * @return
	 */
	int getMeasureIndex();
	
	/**
	 * Returns true if existing cube sum aggregation is extended.
	 * @return
	 */
	boolean isExtension();

	/**
	 * Returns true if measure and value is accepted for loading.
	 * @param cube
	 * @param key
	 * @param measure
	 * @param value
	 * @return
	 */
	boolean accept(ICube cube, Key key, IMeasure measure, Double value);

}
