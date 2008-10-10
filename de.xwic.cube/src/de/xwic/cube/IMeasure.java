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

}