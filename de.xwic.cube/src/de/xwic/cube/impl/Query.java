/**
 * 
 */
package de.xwic.cube.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.xwic.cube.ICube;
import de.xwic.cube.IDimension;
import de.xwic.cube.IDimensionElement;
import de.xwic.cube.IQuery;
import de.xwic.cube.Key;

/**
 * Implementation of the IQuery interface, used to retrieve data from the cube.
 * 
 * The current state of the implementation allows only to retrieve one value from
 * multiple dimensions, so it basically does an automated aggregation if multiple
 * dimension elements are specified.
 * 
 * The future purpose of this object should be to allow the retrieval of multiple
 * values.
 * 
 * @author lippisch
 */
public class Query implements IQuery {

	private final ICube cube;
	private Map<IDimension, Set<IDimensionElement>> selection = new HashMap<IDimension, Set<IDimensionElement>>();
	private IDimension[] dimensions;
	
	/**
	 * @param cube
	 */
	public Query(ICube cube) {
		this.cube = cube;
		dimensions = new IDimension[cube.getDimensions().size()];
		dimensions = cube.getDimensions().toArray(dimensions);
	}

	/**
	 * @param cube
	 * @param query
	 */
	public Query(ICube cube, String query) {
		this.cube = cube;
		dimensions = new IDimension[cube.getDimensions().size()];
		dimensions = cube.getDimensions().toArray(dimensions);
		
	}

	/* (non-Javadoc)
	 * @see de.xwic.cube.IQuery#clear()
	 */
	public void clear() {
		selection.clear();
	}

	/* (non-Javadoc)
	 * @see de.xwic.cube.IQuery#clear(de.xwic.cube.IDimension)
	 */
	public void clear(IDimension dimension) {
		selection.remove(dimension);
	}

	/* (non-Javadoc)
	 * @see de.xwic.cube.IQuery#createKeys()
	 */
	public List<Key> createKeys() {
		
		List<Key> keys = new ArrayList<Key>();
		Key baseKey = cube.createKey("");
		
		if (selection.isEmpty()) {
			keys.add(baseKey);
		} else {
	 		// determine size
			int dimIdx = 0;
			buildKeys(baseKey, keys, dimIdx);
		}
		 
		return keys;
	}

	/**
	 * @param keys
	 * @param idx
	 * @param dimIdx
	 */
	private void buildKeys(Key baseKey, List<Key> keys, int dimIdx) {
		
		if (dimIdx + 1 > dimensions.length) {
			keys.add(baseKey.clone());
			return;
		}
		
		IDimension dim = dimensions[dimIdx];
		Set<IDimensionElement> elList = selection.get(dim);
		if (elList != null && !elList.isEmpty()) {
			for (IDimensionElement elm : elList) {
				baseKey.setDimensionElement(dimIdx, elm);
				buildKeys(baseKey, keys, dimIdx + 1);
			}
		} else {
			buildKeys(baseKey, keys, dimIdx + 1);
		}
		
	}

	/* (non-Javadoc)
	 * @see de.xwic.cube.IQuery#selectDimensionElements(de.xwic.cube.IDimensionElement[][])
	 */
	public void selectDimensionElements(IDimensionElement... elements) {
		
		for (IDimensionElement elm : elements) {
			Set<IDimensionElement> el = selection.get(elm.getDimension());
			if (el == null) {
				el = new HashSet<IDimensionElement>();
				selection.put(elm.getDimension(), el);
			}
			el.add(elm);
		}
		
	}
	
	/* (non-Javadoc)
	 * @see de.xwic.cube.IQuery#getSelectedDimensionElements(de.xwic.cube.IDimension)
	 */
	public Set<IDimensionElement> getSelectedDimensionElements(IDimension dimension) {
		return selection.get(dimension);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public IQuery clone() {
		Query clone = new Query(cube);
		clone.selection = new HashMap<IDimension, Set<IDimensionElement>>(selection);
		return clone;
	}

	/* (non-Javadoc)
	 * @see de.xwic.cube.IQuery#getSelectedDimensions()
	 */
	@Override
	public Set<IDimension> getSelectedDimensions() {
		return selection.keySet();
	}

}
