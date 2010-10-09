/**
 * 
 */
package de.xwic.cube.impl;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import de.xwic.cube.ICell;
import de.xwic.cube.IKeyProvider;
import de.xwic.cube.Key;

/**
 * @author lippisch
 *
 */
public class MapCellStore implements ICellStore, Serializable {

	protected Map<Key, ICell> data;
	private final int dimensionSize;

	
	/**
	 * Constructor with a default initial capacity of 500.
	 */
	public MapCellStore(int dimensionSize) {
		this(dimensionSize, 500);
	}

	/**
	 * Constructor.
	 * @param initialCapacity
	 */
	public MapCellStore(int dimensionSize, int initialCapacity) {
		data = new HashMap<Key, ICell>(initialCapacity);
		this.dimensionSize = dimensionSize;
	}
	
	/* (non-Javadoc)
	 * @see de.xwic.cube.impl.ICellStore#get(de.xwic.cube.Key)
	 */
	@Override
	public ICell get(Key key) {
		return data.get(key);
	}

	/* (non-Javadoc)
	 * @see de.xwic.cube.impl.ICellStore#put(de.xwic.cube.Key, de.xwic.cube.ICell)
	 */
	@Override
	public void put(Key key, ICell cell) {
		data.put(key, cell);
	}

	/* (non-Javadoc)
	 * @see de.xwic.cube.impl.ICellStore#remove(de.xwic.cube.Key)
	 */
	@Override
	public void remove(Key key) {
		data.remove(key);
	}
	
	/* (non-Javadoc)
	 * @see de.xwic.cube.impl.ICellStore#clear()
	 */
	@Override
	public void clear() {
		data.clear();
	}
	
	/* (non-Javadoc)
	 * @see de.xwic.cube.impl.ICellStore#getKeyIterator()
	 */
	@Override
	public Iterator<Key> getKeyIterator() {
		return data.keySet().iterator();
	}
	
	/* (non-Javadoc)
	 * @see de.xwic.cube.impl.ICellStore#size()
	 */
	@Override
	public int size() {
		return data.size();
	}
	
	/* (non-Javadoc)
	 * @see de.xwic.cube.impl.ICellStore#restore(java.io.ObjectInput, de.xwic.cube.IKeyProvider)
	 */
	@Override
	public void restore(ObjectInput in, IKeyProvider keyProvider) throws IOException, ClassNotFoundException {

		int size = in.readInt();

		data = new HashMap<Key, ICell>(size);
		
		for (int i = 0; i < size; i++) {
			Key key = keyProvider.createNewKey(null);
			key.readObject(in, dimensionSize);
			Cell cell = (Cell)in.readObject();
			data.put(key, cell);
		}
		
	}
	
	/* (non-Javadoc)
	 * @see de.xwic.cube.impl.ICellStore#serialize(java.io.ObjectOutput)
	 */
	@Override
	public void serialize(ObjectOutput out) throws IOException {
		
		out.writeInt(data.size());
		for(Entry<Key, ICell> entry: data.entrySet()) {
			
			entry.getKey().writeObject(out);
			out.writeObject(entry.getValue());
			
		}
		
	}

	/**
	 * @return
	 * @see java.util.Map#entrySet()
	 */
	public Set<Entry<Key, ICell>> entrySet() {
		return data.entrySet();
	}
	
}
