/**
 * 
 */
package de.xwic.cube.impl;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Iterator;

import de.xwic.cube.ICell;
import de.xwic.cube.IKeyProvider;
import de.xwic.cube.Key;

/**
 * Defines the storage of cube cells.
 * @author lippisch
 */
public interface ICellStore {

	/**
	 * Put an element into the cell store;
	 */
	public void put(Key key, ICell cell);
	
	/**
	 * Retrieve an element from the cell store;
	 * @param key
	 * @return
	 */
	public ICell get(Key key);

	/**
	 * Remove an element from the store.
	 * @param key
	 */
	public void remove(Key key);

	/**
	 * Remove all elements.
	 */
	public void clear();
	
	/**
	 * Returns an iterator over all keys.
	 * @return
	 */
	public Iterator<Key> getKeyIterator();

	/**
	 * Returns the number of elements in the store.
	 * @return
	 */
	public int size();
	
	/**
	 * Custom Serialization.
	 * @param out
	 * @throws IOException
	 */
	public void serialize(ObjectOutput out) throws IOException;

	/**
	 * Restore during serialization.
	 * @param in
	 * @param keyProvider 
	 * @throws IOException
	 * @throws ClassNotFoundException 
	 */
	public void restore(ObjectInput in, IKeyProvider keyProvider) throws IOException, ClassNotFoundException;
	
}
