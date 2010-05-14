/**
 * 
 */
package de.xwic.cube;

/**
 * Defines a list or hierarchy of DimensionElement's that specify a
 * cell in a cube. 
 * @author Florian Lippisch
 */
public interface IDimension extends IDimensionElement {

	/**
	 * Remove the dimension from the DataPool. A dimension can only be 
	 * removed from the DataPool if it is not used in a cube. If the
	 * dimension contains DimensionElements, they are removed as well.
	 */
	public abstract void remove();
	
	/**
	 * Remove all dimension elements belonging to this dimension.
	 * As the remove() only an unused dimension can be cleared.
	 */
	public abstract void removeDimensionElements();

	/* (non-Javadoc)
	 * @see de.xwic.cube.DimensionElement#getDimension()
	 */
	public abstract IDimension getDimension();

	/* (non-Javadoc)
	 * @see de.xwic.cube.DimensionElement#getID()
	 */
	public abstract String getID();
	
	/**
	 * Returns true if the dimension is sealed. A sealed dimension can not be changed, so
	 * that creation, deletion or moving of elements will raise an IllegalStateException.
	 * @return
	 */
	public boolean isSealed();

	/**
	 * Set the sealed flag. A sealed dimension can not be changed, so
	 * that creation, deletion or moving of elements will raise an IllegalStateException.
	 * @param sealed
	 */
	public void setSealed(boolean sealed);
	
}