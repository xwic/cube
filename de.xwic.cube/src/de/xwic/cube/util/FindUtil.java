/**
 * $Id: $
 *
 * Copyright (c) 2009 NetApp.
 * All rights reserved.

 * de.xwic.cube.util.FindUtil.java
 * Created on Jun 19, 2009
 * 
 * @author JBORNEMA
 */
package de.xwic.cube.util;

import java.util.LinkedHashMap;
import java.util.Map;

import de.xwic.cube.ICell;
import de.xwic.cube.ICube;
import de.xwic.cube.IDimensionElement;
import de.xwic.cube.Key;

/**
 * Created on Jun 19, 2009
 * @author JBORNEMA
 */

public class FindUtil {

	/**
	 * Returns all IDimensionElement leafs that have a value != null for specified measure with its value.
	 * @param cube
	 * @param parent
	 * @param measureIndex
	 * @param key
	 * @return
	 */
	public static Map<IDimensionElement, Double> findLeafs(ICube cube, IDimensionElement parent, int measureIndex, Key key) {
		return findLeafs(cube, parent, measureIndex, cube.getDimensionIndex(parent.getDimension()), new LinkedHashMap<IDimensionElement, Double>(), key);
	}

	/**
	 * @param cube
	 * @param element
	 * @param measureIndex
	 * @param dimensionIndex
	 * @param map
	 * @param key
	 * @return
	 */
	protected static Map<IDimensionElement, Double> findLeafs(ICube cube, 
		IDimensionElement element, int measureIndex, int dimensionIndex, Map<IDimensionElement, Double> map, Key key) {
		
		if (element.isLeaf()) {
			key.setDimensionElement(dimensionIndex, element);
			ICell cell = cube.getCell(key);
			if (cell != null && cell.getValue(measureIndex) != null) {
				map.put(element, cell.getValue(measureIndex));
			}
			return map;
		}
		for (IDimensionElement child : element.getDimensionElements()) {
			findLeafs(cube, child, measureIndex, dimensionIndex, map, key);
		}
		return map;
	}
}
