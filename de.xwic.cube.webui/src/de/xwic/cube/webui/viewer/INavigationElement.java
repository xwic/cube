/**
 * 
 */
package de.xwic.cube.webui.viewer;


/**
 * @author Florian Lippisch
 */
public interface INavigationElement extends INavigationElementProvider {

	/**
	 * Returns an object that is used to generate a cursor to display the right value.
	 * @return
	 */
	public abstract ContentInfo getContentInfo();

	/**
	 * Returns an id used for expand/collapse identification.
	 * @return
	 */
	public abstract String getElementId();
	
	/**
	 * Returns true if the element is expandable.
	 * @return
	 */
	public abstract boolean isExpandable();
	
	/**
	 * Returns the number cells this element spans over.
	 * @return
	 */
	public abstract int getSpan();
	
	/**
	 * Returns the title of the element.
	 * @return
	 */
	public abstract String getTitle();
	
	/**
	 * If true, no total row is displays for this elements if its child elements are displayed. 
	 * @return
	 */
	public abstract boolean hideTotal();
}
