/**
 * 
 */
package de.xwic.cube;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;



/**
 * @author JBORNEMA
 *
 */
public class KeyExt extends Key implements IUserObject {

	private static final long serialVersionUID = 796614174206092706L;
	
	protected Serializable userObject;

	/**
	 * @param elementKeys
	 */
	public KeyExt(IDimensionElement[] elementKeys) {
		super(elementKeys);
	}

	/**
	 * @param key
	 */
	public KeyExt(Key key) {
		super(key);
	}

	/**
	 * Constructor with userObject
	 * @param elementKeys
	 * @param userObject
	 */
	public KeyExt(IDimensionElement[] elementKeys, Serializable userObject) {
		super(elementKeys);
		this.userObject = userObject;
	}

	/**
	 * Constructor with userObject
	 * @param key
	 * @param userObject
	 */
	public KeyExt(Key key, Serializable userObject) {
		super(key);
		this.userObject = userObject;
	}

	/**
	 * @return the userObject
	 */
	public Serializable getUserObject() {
		return userObject;
	}

	/**
	 * @param userObject the userObject to set
	 */
	public void setUserObject(Serializable userObject) {
		this.userObject = userObject;
	}
	
	@Override
	public Key clone() {
		IDimensionElement[] cloneKeys = new IDimensionElement[elementKeys.length];
		System.arraycopy(elementKeys, 0, cloneKeys, 0, elementKeys.length);
		KeyExt clone = new KeyExt(cloneKeys, userObject);
		return clone;
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
		int result = super.hashCode();
		result = prime * result
				+ ((userObject == null) ? 0 : userObject.hashCode());
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
		if (!super.equals(obj))
			return false;
		KeyExt other = (KeyExt) obj;
		if (userObject == null) {
			if (other.userObject != null)
				return false;
		} else if (!userObject.equals(other.userObject))
			return false;
		return true;
	}
	
	/* (non-Javadoc)
	 * @see de.xwic.cube.Key#readObject(java.io.ObjectInput, int)
	 */
	@Override
	public void readObject(ObjectInput in, int dimSize)	throws ClassNotFoundException, IOException {
		super.readObject(in, dimSize);
		userObject = (Serializable)in.readObject();
	}
	
	/* (non-Javadoc)
	 * @see de.xwic.cube.Key#writeObject(java.io.ObjectOutput)
	 */
	@Override
	public void writeObject(ObjectOutput out) throws IOException {
		super.writeObject(out);
		out.writeObject(userObject);
	}

	@Override
	public void addUserObjects(Serializable userObject) {
		userObject = ObjectsHelper.addObjects(this.userObject, userObject);
	}
}
