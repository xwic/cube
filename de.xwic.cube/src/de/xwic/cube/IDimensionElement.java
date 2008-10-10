/**
 * 
 */
package de.xwic.cube;

import java.util.Collection;

/**
 * @author Florian Lippisch
 */
public interface IDimensionElement extends IIdentifyable {

	/**
	 * Create a new DimensionElement.
	 * @param key
	 * @return
	 */
	public abstract IDimensionElement createDimensionElement(String key);

	/**
	 * Returns true if an element with that key exists.
	 * @param key
	 * @return
	 */
	public abstract boolean containsDimensionElement(String key);

	/**
	 * Returns the element with the specified key. If no element with that
	 * key exists, an IllegalArgumentException is thrown.
	 * @param key
	 * @return
	 */
	public abstract IDimensionElement getDimensionElement(String key);

	/**
	 * Returns the list of DimensionElements.
	 * @return
	 */
	public abstract Collection<IDimensionElement> getDimensionElements();

	/**
	 * Remove the element from the parent element/dimension. A DimensionElement can only be 
	 * removed from the DataPool if the dimension is not used in a cube.
	 */
	public abstract void remove();

	/**
	 * Returns the Dimension this DimensionElement belongs to.
	 * @return the dimension
	 */
	public abstract IDimension getDimension();

	/**
	 * Returns the parent element.
	 * @return the parent
	 */
	public abstract IDimensionElement getParent();

	/**
	 * Builds the ID of an element.
	 * @return
	 */
	public abstract String getID();

	/**
	 * Returns the total number of child elements in this dimension/element.
	 * @return
	 */
	public abstract int totalSize();
	
	/**
	 * Returns true if this element contains no childs.
	 * @return
	 */
	public abstract boolean isLeaf();

	/**
	 * @return the weight
	 */
	public double getWeight();

	/**
	 * The weight of an dimension element is used to splash values
	 * down a hierachy.
	 * @param weight the weight to set
	 */
	public void setWeight(double weight);

	/**
	 * @return
	 */
	public abstract double getElementsTotalWeight();

}