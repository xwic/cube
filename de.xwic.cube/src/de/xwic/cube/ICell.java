/**
 * 
 */
package de.xwic.cube;

/**
 * A cell contains the value for a specific key in a cube.
 * @author Florian Lippisch
 */
public interface ICell {

	public final static ICell EMPTY = new ICell() {

		/*(non-Javadoc)
		 * @see de.xwic.cube.ICell#getValue(int)
		 */
		public Double getValue(int measureIndex) {
			return null;
		}
		
		/* (non-Javadoc)
		 * @see de.xwic.cube.ICell#setValue(int, java.lang.Double)
		 */
		public void setValue(int arg0, Double arg1) {
		}

		/*(non-Javadoc)
		 * @see de.xwic.cube.ICell#isEmpty()
		 */
		public boolean isEmpty() {
			return true;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (obj == null) {
				return false;
			}
			if (obj.getClass() != getClass()) {
				return false;
			}
			return ((ICell)obj).isEmpty();
		}
	};
	
	/**
	 * Returns the value in the cell of the specified measure.
	 * @param measure
	 * @return
	 */
	public abstract Double getValue(int measureIndex);

	/**
	 * Set the value in the cell of the specified measure.
	 * @param measureIndex
	 * @param value
	 */
	public void setValue(int measureIndex, Double value);
	
	/**
	 * Returns true if the cell is empty.
	 * @return
	 */
	public boolean isEmpty();

}