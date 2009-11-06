/**
 * 
 */
package de.xwic.cube;

import java.util.Comparator;
import java.util.List;

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
	public abstract List<IDimensionElement> getDimensionElements();

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
	 * Builds the ID of an element. The ID contains the dimension key and 
	 * the path of all elements.
	 * Sample: 
	 * <li>[GEO:EMEA/Germany]
	 * <li>[Time:2009/Q1/Jan]
	 * @return
	 */
	public abstract String getID();
	
	/**
	 * Returns the path to this dimension element.
	 * Samples:
	 * <li>EMEA/Germany
	 * <li>2009/Q1/Jan
	 * @return
	 */
	public abstract String getPath();

	/**
	 * Returns the index position within the parent's dimension element collection.
	 * @return
	 */
	public abstract int getIndex();
	
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

	/**
	 * Returns the element specified in this path. The keys in the path
	 * are separated by a slash.
	 * @param path
	 * @return
	 */
	public IDimensionElement parsePath(String path);

	/**
	 * Change the child elements index position.
	 * @param childElement
	 * @param newIndex
	 */
	public void reindex(IDimensionElement childElement, int newIndex);
	
	/**
	 * Returns the depth of this element. The IDimension returns 0, the dimensions
	 * child 1 and so on..
	 * @return
	 */
	public int getDepth();

	/**
	 * Returns true if the specified element is a child of this element. This includes
	 * childs in any depth. 
	 * @param dimensionElement
	 * @return
	 */
	public boolean isChild(IDimensionElement dimensionElement);

	/**
	 * Sort elements using given comparator.
	 * @param comparator 
	 */
	public void sortDimensionElements(Comparator<IDimensionElement> comparator);
	
	/**
	 * Sort elements by key. 
	 */
	public void sortDimensionElements();
}