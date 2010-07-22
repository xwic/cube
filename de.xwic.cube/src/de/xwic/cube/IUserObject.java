/**
 * 
 */
package de.xwic.cube;

import java.io.Serializable;
import java.util.Collection;
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
	public final static class Helper {
		/**
		 * Returns the first object instanceof IUserObject
		 * @param objects
		 * @return
		 */
		public static IUserObject getFirstUserObject(Object... objects) {
			for (Object object : objects) {
				if (object instanceof IUserObject) {
					return (IUserObject)object;
				}
			}
			return null;
		}
		/**
		 * If userObject is instanceof Collection<?> it returns the size() otherwise userObject != null ? 1 : 0
		 * @param userObject
		 * @return
		 */
		public static Double getSize(Serializable userObject) {
			if (userObject instanceof Collection<?>) {
				return (double)((Collection<?>)userObject).size();
			}
			return userObject != null ? 1d : null;
		}
		/**
		 * Adds userObject to userObjects and return a Set if more objects exists or just userObject if it's the only one.
		 * @param userObjects
		 * @param userObject
		 * @return
		 */
		@SuppressWarnings("unchecked")
		public static Serializable addObjects(Serializable userObjects, Serializable userObject) {
			HashSet<Object> newObjects = null;
			if (userObject instanceof Collection<?>) {
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
