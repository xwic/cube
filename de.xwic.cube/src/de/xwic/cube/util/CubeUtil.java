/**
 * $Id: $
 *
 * Copyright (c) 2008 Network Appliance.
 * All rights reserved.

 * de.xwic.cube.util.CubeUtil.java
 * Created on Feb 11, 2009
 * 
 * @author JBORNEMA
 */
package de.xwic.cube.util;

import java.util.HashMap;
import java.util.Map;

import de.xwic.cube.IDataPool;
import de.xwic.cube.IDimension;
import de.xwic.cube.IDimensionElement;

/**
 * Created on Feb 11, 2009
 * 
 * @author JBORNEMA
 */

public class CubeUtil {

	protected IDataPool dataPool;
	protected boolean autoCreateDimensionElement;

	protected Map<String, IDimensionElement> parsedDimensionElements = new HashMap<String, IDimensionElement>();

	/**
	 * Default constructor.
	 */
	public CubeUtil() {

	}

	/**
	 * Use this dataPool.
	 * 
	 * @param dataPool
	 */
	public CubeUtil(IDataPool dataPool) {
		setDataPool(dataPool);
	}

	/**
	 * @return the dataPool
	 */
	public IDataPool getDataPool() {
		return dataPool;
	}

	/**
	 * @param dataPool
	 *            the dataPool to set
	 */
	public void setDataPool(IDataPool dataPool) {
		this.dataPool = dataPool;
	}

	/**
	 * @return the autoCreateDimensionElement
	 */
	public boolean isAutoCreateDimensionElement() {
		return autoCreateDimensionElement;
	}

	/**
	 * @param autoCreateDimensionElement
	 *            the autoCreateDimensionElement to set
	 */
	public void setAutoCreateDimensionElement(boolean autoCreateDimensionElement) {
		this.autoCreateDimensionElement = autoCreateDimensionElement;
	}

	/**
	 * Returns the DimensionElement specified in the id. If
	 * autoCreateDimensionElement is true missing dimension elements are
	 * created. This call caches parsed dimension elements in
	 * parsedDimensionElements. The id is created from
	 * IDimensionElement.getId(). Sample:
	 * <p>
	 * [GEO:EMEA/Germany]
	 * 
	 * @param id
	 * @return
	 */
	public IDimensionElement parseDimensionElementId(String id) {

		IDimensionElement cached = parsedDimensionElements.get(id);
		if (cached != null) {
			return cached;
		}

		/**
		 * Copied from DataPool.parseDimensionElementId(String)
		 */
		int start = id.indexOf('[');
		if (start == -1) {
			throw new IllegalArgumentException("Missing starting [");
		}
		int end = id.indexOf(']', start);
		if (end == -1) {
			throw new IllegalArgumentException("Missing ending ]");
		}
		String part = id.substring(start + 1, end);
		int idxDimSep = part.indexOf(':');
		String elmKeys;
		IDimension dimension;
		// does the key contain a dimension key?
		if (idxDimSep == -1) { // no key given
			throw new IllegalArgumentException("No dimension key found");
		}

		String dimKey = part.substring(0, idxDimSep);
		elmKeys = part.substring(idxDimSep + 1);
		dimension = dataPool.getDimension(dimKey);
		IDimensionElement element = dimension;
		if (!"*".equals(elmKeys)) { // specific key given.
			int idxPathSep;
			int idxPathStart = 0;
			do {
				idxPathSep = elmKeys.indexOf('/', idxPathStart);
				String elmKey;
				if (idxPathSep == -1) {
					elmKey = elmKeys.substring(idxPathStart);
				} else {
					elmKey = elmKeys.substring(idxPathStart, idxPathSep);
				}
				if (!autoCreateDimensionElement
						|| element.containsDimensionElement(elmKey)) {
					element = element.getDimensionElement(elmKey);
				} else {
					// create element
					element = element.createDimensionElement(elmKey);
				}
				idxPathStart = idxPathSep + 1;
			} while (idxPathSep != -1);
		}

		/**
		 * Copied end
		 */

		parsedDimensionElements.put(id, element);

		return element;

	}

	/**
	 * Parse a dimension path.
	 * @param path
	 * @param dimension
	 * @return
	 */
	public IDimensionElement parseDimensionPath(String path, IDimension dimension) {
		if (path == null) {
			return dimension;
		}

		IDimensionElement element = dimension;
		int idxPathSep;
		int idxPathStart = 0;
		do {
			idxPathSep = path.indexOf('/', idxPathStart);
			String elmKey;
			if (idxPathSep == -1) {
				elmKey = path.substring(idxPathStart);
			} else {
				elmKey = path.substring(idxPathStart, idxPathSep);
			}
			if (!autoCreateDimensionElement
					|| element.containsDimensionElement(elmKey)) {
				element = element.getDimensionElement(elmKey);
			} else {
				// create element
				element = element.createDimensionElement(elmKey);
			}
			idxPathStart = idxPathSep + 1;
		} while (idxPathSep != -1);

		return element;
	}

}