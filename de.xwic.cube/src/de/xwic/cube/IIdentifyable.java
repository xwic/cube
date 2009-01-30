/**
 * 
 */
package de.xwic.cube;

/**
 * @author Florian Lippisch
 */
public interface IIdentifyable {

	/**
	 * @return the key
	 */
	public abstract String getKey();

	/**
	 * @return the title
	 */
	public abstract String getTitle();

	/**
	 * @param title the title to set
	 */
	public abstract void setTitle(String title);

}