/**
 * 
 */
package de.xwic.cube;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Represents a pointer to a cell in a cube.
 * @author Florian Lippisch
 */
public class Key implements Serializable {

	private static final long serialVersionUID = -3558184977558178383L;
	private IDimensionElement[] elementKeys;
	private boolean modifyable = false;
	private ICube cube = null;
	
	/**
	 * @param elementKeys
	 */
	public Key(ICube cube, IDimensionElement[] elementKeys) {
		super();
		this.cube = cube;
		this.elementKeys = elementKeys;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(elementKeys);
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Key other = (Key) obj;
		if (!Arrays.equals(elementKeys, other.elementKeys))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (IDimensionElement elm : elementKeys) {
			sb.append(elm.getID());
		}
		return sb.toString();
	}

	/**
	 * @return the elementKeys
	 */
	public Key clone() {
		IDimensionElement[] cloneKeys = new IDimensionElement[elementKeys.length];
		System.arraycopy(elementKeys, 0, cloneKeys, 0, elementKeys.length);
		Key clone = new Key(cube, cloneKeys);
		clone.modifyable = this.modifyable;
		return clone;
	}

	/**
	 * @return the modifyable
	 */
	public boolean isModifyable() {
		return modifyable;
	}

	/**
	 * Returns true if this key is based upon leafs only.
	 * @return
	 */
	public boolean containsLeafsOnly() {
		for (IDimensionElement elm : elementKeys) {
			if (!elm.isLeaf()) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Change the key. The index of the dimension element is determined
	 * automatically.
	 * @param element
	 */
	public void setDimensionElement(IDimensionElement element) {
		int idx = cube.getDimensionIndex(element.getDimension());
		setDimensionElement(idx, element);
	}
	
	/**
	 * Change the key.
	 * @param idx
	 * @param element
	 */
	public void setDimensionElement(int idx, IDimensionElement element) {
		if (!modifyable) {
			throw new IllegalStateException("The key is unmodifyable!");
		}
		elementKeys[idx] = element;
	}

	/**
	 * @param modifyable the modifyable to set
	 */
	public void setModifyable(boolean modifyable) {
		this.modifyable = modifyable;
	}

	/**
	 * @param idx
	 * @return
	 */
	public IDimensionElement getDimensionElement(int idx) {
		return elementKeys[idx];
	}

	/**
	 * Returns a collection of all DimensionElements in this key.
	 * @return
	 */
	public Collection<IDimensionElement> getDimensionElements() {
		List<IDimensionElement> elements = new ArrayList<IDimensionElement>();
		for (IDimensionElement e : elementKeys) {
			elements.add(e);
		}
		return elements;
	}
	
}
