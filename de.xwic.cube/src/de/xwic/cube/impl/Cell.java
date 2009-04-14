/**
 * 
 */
package de.xwic.cube.impl;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import de.xwic.cube.ICell;

/**
 * Represents a cell inside of the cube. A cell contains a value per measure.
 * 
 * @author Florian Lippisch
 */
public class Cell implements ICell, Externalizable {

	private static final long serialVersionUID = -4297789024853482650L;
	
	public Double[] values;
	
	public Cell() {
		
	}
	
	Cell(int measureSize) {
		values = new Double[measureSize];
	}
	
	/* (non-Javadoc)
	 * @see de.xwic.cube.ICell#getValue(de.xwic.cube.IMeasure)
	 */
	public Double getValue(int measureIndex) {
		// FIXME Functions always return null, why does values contains that index? Could be used for caching?
		// Function measureIndex used by DummyFunction...
		return values[measureIndex];
	}
	
	/*
	 * Change the value in this cell.
	 */
	public void setValue(int measureIndex, Double value) {
		values[measureIndex] = value;
	}
	
	/**
	 * Returns true if the cell is empty.
	 * @return
	 */
	public boolean isEmpty() {
		for (Double d : values) {
			if (d != null) {
				return false;
			}
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see java.io.Externalizable#readExternal(java.io.ObjectInput)
	 */
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		
		int size = in.readInt();
		values = new Double[size];
		for (int i = 0; i < size; i++) {
			if (in.readBoolean()) {
				values[i] = new Double(in.readDouble());
			} else {
				values[i] = null;
			}
		}
		
	}

	/* (non-Javadoc)
	 * @see java.io.Externalizable#writeExternal(java.io.ObjectOutput)
	 */
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeInt(values.length);
		for (Double d : values) {
			if (d != null) {
				out.writeBoolean(true);
				out.writeDouble(d);
			} else {
				out.writeBoolean(false);
			}
		}
		
	}
	
}
