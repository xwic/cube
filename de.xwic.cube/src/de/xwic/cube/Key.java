/**
 * 
 */
package de.xwic.cube;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
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
	protected IDimensionElement[] elementKeys;
	protected transient int hashCode = 0; 
	
	/**
	 * @param elementKeys
	 */
	public Key(IDimensionElement[] elementKeys) {
		super();
		this.elementKeys = elementKeys;
	}
	
	/**
	 * @param key
	 */
	public Key(Key key) {
		elementKeys = new IDimensionElement[key.elementKeys.length];
		System.arraycopy(key.elementKeys, 0, elementKeys, 0, key.elementKeys.length);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		if (hashCode != 0) {
			return hashCode;
		}
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(elementKeys);
		hashCode = result;
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
		Key clone = new Key(cloneKeys);
		return clone;
	}
	
	
	/**
	 * Returns true if this key is based upon leafs only.
	 * @deprecated - use isLeaf() instead.x
	 * @return
	 */
	public boolean containsLeafsOnly() {
		return isLeaf();
	}
	
	/**
	 * Returns true if this key is based upon leafs only.
	 * @return
	 */
	public boolean isLeaf() {
		for (IDimensionElement elm : elementKeys) {
			if (!elm.isLeaf()) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Change the key.
	 * @param idx
	 * @param element
	 */
	public void setDimensionElement(int idx, IDimensionElement element) {
		elementKeys[idx] = element;
		hashCode = 0;
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
	
	/**
	 * Returns true if the key's elements match these
	 * @param key
	 * @return
	 */
	public boolean isSubKey(Key key) {
		for (int i = 0; i < elementKeys.length; i++) {
			if (!isSubKeyElement(key, i)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks if the key exists in the cube. A key might not exist after some
	 * dimension elements have been removed.
	 * 
	 * @return
	 */
	public boolean exists() {
		for(IDimensionElement el: elementKeys) {
			if(el.getParent() != null) {
				if(!el.getParent().containsDimensionElement(el.getKey())) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Returns true if the element at idx matches, invoked by @see isSubeKey(Key)
	 * @param key
	 * @param idx
	 * @return
	 */
	public boolean isSubKeyElement(Key key, int idx) {
		return elementKeys[idx].equals(key.elementKeys[idx]) || elementKeys[idx].isChild(key.elementKeys[idx]);
	}

	/**
	 * Writes the object to the ObjectOutput
	 * @param out
	 * @throws IOException 
	 */
	public void writeObject(ObjectOutput out) throws IOException {
		for (IDimensionElement elm : elementKeys) {
			out.writeObject(elm);
		}
	}

	/**
	 * Reads the object data from the ObjectInput
	 * @param in
	 * @param dimSize
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	public void readObject(ObjectInput in, int dimSize) throws ClassNotFoundException, IOException {
		elementKeys = new IDimensionElement[dimSize];
		for (int dIdx = 0; dIdx < dimSize; dIdx++) {
			elementKeys[dIdx] = (IDimensionElement)in.readObject();
		}
	}
}
