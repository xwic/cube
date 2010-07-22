/**
 * 
 */
package de.xwic.cube.impl;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;

import de.xwic.cube.IUserObject;

/**
 * @author jbornema
 *
 */
public class CellExt extends Cell implements IUserObject {

	private static final long serialVersionUID = 1L;
	
	protected Serializable userObject = null;
	
	/**
	 * 
	 */
	public CellExt() {
	}

	/**
	 * @param measureSize
	 */
	public CellExt(int measureSize) {
		super(measureSize);
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
	public void addUserObjects(Serializable userObject) {
		userObject = Helper.addObjects(this.userObject, userObject);
	}
	
	@Override
	public boolean isEmpty() {
		return userObject == null && super.isEmpty();
	}
	
	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		super.readExternal(in);
		userObject = (Serializable)in.readObject();
	}
	
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		out.writeObject(userObject);
	}
}
