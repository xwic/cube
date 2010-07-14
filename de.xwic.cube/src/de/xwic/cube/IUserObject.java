/**
 * 
 */
package de.xwic.cube;

import java.io.Serializable;
import java.util.HashSet;

/**
 * @author JBORNEMA
 *
 */
public interface IUserObject {

	/**
	 * IUserObject helper class to have a set or single object representing a group.
	 * @author JBORNEMA
	 *
	 */
	public static class ObjectsHelper {
		@SuppressWarnings("unchecked")
		public static Serializable addObjects(Serializable userObjects, Serializable userObject) {
			HashSet<Object> newObjects = null;
			if (userObject instanceof HashSet<?>) {
				newObjects = (HashSet<Object>)userObject;
			}
			if (userObjects == null) {
				if (newObjects != null) {
					return (Serializable)newObjects.clone();
				}
				return userObject;
			}
			HashSet<Object> objects = null;
			if (userObjects instanceof HashSet<?>) {
				objects = (HashSet<Object>)userObjects;
			} else {
				if (userObjects.equals(userObject)) {
					return userObjects;
				}
				objects = new HashSet<Object>(newObjects == null ? 2 : newObjects.size() + 1);
				objects.add((Object)userObjects);
			}
			
			if (newObjects != null) {
				objects.addAll(newObjects);
			} else {
				objects.add(userObject);
			}
			
			return objects;
		}
	}
	
	/**
	 * @return the userObject
	 */
	Serializable getUserObject();
	
	/**
	 * Sets the userObject
	 * @param userObject
	 */
	void setUserObject(Serializable userObject);
	
	/**
	 * Sets or adds the userObject using ObjectsHelper.addObjects(this.userObject, userObject).
	 * @param userObject
	 */
	void addUserObjects(Serializable userObject);
}
