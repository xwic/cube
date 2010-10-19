/**
 * 
 */
package de.xwic.cube.impl;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;

import de.xwic.cube.ICell;
import de.xwic.cube.IDimensionElement;
import de.xwic.cube.IKeyProvider;
import de.xwic.cube.Key;

/**
 * Base element within an IndexedDataTable.
 * @author lippisch
 *
 */
public class IndexedData implements Comparable<IndexedData>, Serializable {

	private Key key;
	private ICell cell;
	
	private int[][] nextEntry = new int[0][0];
	
	/**
	 * Constructor - only use for serialization.
	 */
	public IndexedData() { // used for serialization
		
	}
	
	/**
	 * @param key
	 * @param cell
	 */
	public IndexedData(Key key, ICell cell) {
		super();
		this.key = key;
		this.cell = cell;
	}

	/**
	 * @return the key
	 */
	public Key getKey() {
		return key;
	}
	
	/**
	 * @return the cell
	 */
	public ICell getCell() {
		return cell;
	}

	/**
	 * Compare the keys against each other. It is assumed that
	 * all elements come from the same cube, therefore the keys
	 * do all have the same dimension order. Each dimension is compared
	 * to each other.
	 */
	@Override
	public int compareTo(IndexedData o) {
		
		// compare the keys
		int max = key.getDimensionCount();
		for (int dimIdx = 0; dimIdx < max; dimIdx++) {
			
			IDimensionElement elm1 = key.getDimensionElement(dimIdx);
			IDimensionElement elm2 = o.key.getDimensionElement(dimIdx);
			
			String s1 = elm1.getPath();
			String s2 = elm2.getPath();
			
			int result = s1.compareTo(s2);
			if (result != 0) {
				return result; // exit if its not equal
			}
			
		}
		return 0; // must be equal
	}

	/**
	 * @param cell the cell to set
	 */
	public void setCell(ICell cell) {
		this.cell = cell;
	}

	/**
	 * @return the nextEntry
	 */
	public int[][] getNextEntry() {
		return nextEntry;
	}

	/**
	 * @param nextEntry the nextEntry to set
	 */
	public void setNextEntry(int[][] nextEntry) {
		this.nextEntry = nextEntry;
	}
	
	/**
	 * Write to stream.
	 * @param out
	 * @throws IOException
	 */
	public void serialize(ObjectOutput out) throws IOException {
		
		key.writeObject(out);
		out.writeObject(cell);
		// write array
		out.writeInt(nextEntry.length);
		for (int i = 0; i < nextEntry.length; i++) {
			int[] sa = nextEntry[i];
			out.writeInt(sa.length);
			for (int a = 0; a < sa.length; a++) {
				out.writeInt(sa[a]);
			}
		}
			
		
	}
	/**
	 * Restore from stream.
	 * @param in
	 * @param keyProvider
	 * @param dimensionCount
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public void restore(ObjectInput in, IKeyProvider keyProvider, int dimensionCount) throws IOException, ClassNotFoundException {
		
		key = keyProvider.createNewKey(null);
		key.readObject(in, dimensionCount);
		cell = (ICell)in.readObject();
		// read array
		int baseSize = in.readInt();
		nextEntry = new int[baseSize][0];
		for (int i = 0; i < baseSize; i++) {
			int subSize = in.readInt();
			int[] subArray = new int[subSize];
			for (int a = 0; a < subSize; a++) {
				subArray[a] = in.readInt();
			}
			nextEntry[i] = subArray;
		}
		
	}
}
