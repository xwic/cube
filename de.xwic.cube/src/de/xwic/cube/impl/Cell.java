/**
 * 
 */
package de.xwic.cube.impl;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;

import de.xwic.cube.ICell;

/**
 * Represents a cell inside of the cube. A cell contains a value per measure.
 * 
 * @author Florian Lippisch
 */
public class Cell implements ICell, Externalizable {

	private static final long serialVersionUID = -4297789024853482650L;

	private double[] values;
	
	public Cell() {
		
	}
	
	Cell(int measureSize) {
		values = new double[measureSize];
		Arrays.fill(values, Double.NaN);
	}
	
	/* (non-Javadoc)
	 * @see de.xwic.cube.ICell#getValue(de.xwic.cube.IMeasure)
	 */
	public Double getValue(int measureIndex) {
		// FIXME Functions always return null, why does values contains that index? Could be used for caching?
		// Function measureIndex used by DummyFunction...
		double value = values[measureIndex];
		return Double.isNaN(value) ? null : value;
	}
	
	/*
	 * Change the value in this cell.
	 */
	public void setValue(int measureIndex, Double value) {
		values[measureIndex] = value != null ? value : Double.NaN;
	}
	
	/**
	 * Returns true if the cell is empty.
	 * @return
	 */
	public boolean isEmpty() {
		for (double d : values) {
			if (!Double.isNaN(d)) {
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
		values = new double[size];
		for (int i = 0; i < size; i++) {
			if (in.readBoolean()) {
				values[i] = in.readDouble();
			} else {
				values[i] = Double.NaN;
			}
		}
	}

	/* (non-Javadoc)
	 * @see java.io.Externalizable#writeExternal(java.io.ObjectOutput)
	 */
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeInt(values.length);
		for (double d : values) {
			if (!Double.isNaN(d)) {
				out.writeBoolean(true);
				out.writeDouble(d);
			} else {
				out.writeBoolean(false);
			}
		}
		
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return Arrays.toString(values);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(values);
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
		Cell other = (Cell) obj;
		if (!Arrays.equals(values, other.values))
			return false;
		return true;
	}
	
	
}
