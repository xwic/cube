/**
 * 
 */
package de.xwic.cube;

import java.text.ParseException;
import java.util.Collection;

/**
 * The cube contains a data structure based on a fixed number of dimensions. Data can 
 * be added into the cube, where they are automatically aggregated.
 * 
 * @author Florian Lippisch
 */
public interface ICube extends IIdentifyable {

	/**
	 * Returns the list of collections in this cube.
	 * @return
	 */
	public abstract Collection<IDimension> getDimensions();

	/**
	 * Returns the list of measures.
	 * @return
	 */
	public abstract Collection<IMeasure> getMeasures();

	/**
	 * Delete the cube.
	 */
	public abstract void remove();

	/**
	 * Create a Key from a string notation. The key string must contain a [] pair for
	 * each dimension in the cube. If the dimension key is not present in the key pair, 
	 * the dimension at the index position is assumed.
	 * </p>
	 * Some examples:
	 * <ul>
	 * <li>[GEO:EMEA/Germany][Time:2009/Q1/Apr]
	 * </ul>
	 * 
	 * @param key
	 * @return
	 * @throws ParseException 
	 */
	public abstract Key createKey(String key);
	
	/**
	 * Returns the cell for the specified key.
	 * @param key
	 * @return
	 */
	public abstract ICell getCell(Key key);

	/**
	 * Write the value into the cube.
	 * NOTE: This method is not thread safe and must be synchronized externally
	 * if the cube is modified in different threads.
	 * @return the number of cells modified.
	 * @param key
	 * @param measure
	 * @param value
	 */
	public abstract int setCellValue(Key key, IMeasure measure, double value);

	/**
	 * Returns the value in a cell or NULL if the cell contains no value.
	 * @param key
	 * @param measure
	 * @return
	 */
	public abstract Double getCellValue(Key key, IMeasure measure);
	
	/**
	 * Returns the value in a cell or NULL if the cell contains no value.
	 * @param key
	 * @param measure
	 * @return
	 */
	public abstract Double getCellValue(String keyString, IMeasure measure);

	/**
	 * Empties all values in the cube.
	 */
	public abstract void reset();

	/**
	 * Returns the key index position for this dimension.
	 * @param dimVert
	 * @return
	 */
	public abstract int getDimensionIndex(IDimension dimVert);

}