/**
 * 
 */
package de.xwic.cube;

/**
 * @author Florian Lippisch
 */
public interface IMeasure extends IIdentifyable {

	/**
	 * Remove the measure from the DataPool. A measure can only be 
	 * removed from the DataPool if it is not used in a cube. 
	 */
	public abstract void remove();
	
	/**
	 * Returns true if this measure is based on a function. Functional
	 * measures do not store data in the cube - instead a function 
	 * implementation is calculating the value for a cell based on the
	 * other measures.
	 * @return
	 */
	public boolean isFunction();

	/**
	 * Set the function for the measure.
	 * @see isFunction()
	 * @param function
	 */
	public void setFunction(IMeasureFunction function);
	
	/**
	 * Returns the assigned function.
	 * @return
	 */
	public IMeasureFunction getFunction();
	
	/**
	 * Returns the format provider used to create strings out of the values.
	 * @return the format
	 */
	public IValueFormatProvider getValueFormatProvider();

	/**
	 * @param format the format to set
	 */
	public void setValueFormatProvider(IValueFormatProvider format);
	
}