/**
 * 
 */
package de.xwic.cube.impl;

import java.io.Serializable;

import de.xwic.cube.IIdentifyable;

/**
 * Base class for all objects that have a key.
 * @author Florian Lippisch
 */
public abstract class Identifyable implements IIdentifyable, Serializable {

	protected String key = null;
	protected String title = null;

	/**
	 * @param key
	 */
	public Identifyable(String key) {
		super();
		this.key = key;
	}

	/* (non-Javadoc)
	 * @see de.xwic.cube.IIdentifyable#getKey()
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @param key the key to set
	 */
	protected void setKey(String key) {
		this.key = key;
	}

	/* (non-Javadoc)
	 * @see de.xwic.cube.IIdentifyable#getTitle()
	 */
	public String getTitle() {
		return title;
	}

	/* (non-Javadoc)
	 * @see de.xwic.cube.IIdentifyable#setTitle(java.lang.String)
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getClass().getSimpleName() + ":" + getKey();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
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
		final Identifyable other = (Identifyable) obj;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		return true;
	}
	
	
}
