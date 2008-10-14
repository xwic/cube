/**
 * 
 */
package de.xwic.cube.webui.viewer;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.xwic.cube.ICube;
import de.xwic.cube.IDimension;
import de.xwic.cube.IMeasure;
import de.xwic.cube.Key;

/**
 * Model that controls the CubeViewer.
 * 
 * @author Florian Lippisch
 */
public class CubeViewerModel {

	private ICube cube = null;

	private IMeasure measure = null;
	private List<INavigationProvider> rowProvider = new ArrayList<INavigationProvider>();
	private List<INavigationProvider> columnProvider = new ArrayList<INavigationProvider>();
	
	private NumberFormat numberFormat;
	
	public CubeViewerModel(Locale locale) {
		numberFormat = NumberFormat.getNumberInstance(locale);
		numberFormat.setMinimumFractionDigits(2);
		numberFormat.setMaximumFractionDigits(2);
	}
	
	
	/**
	 * @return the cube
	 */
	public ICube getCube() {
		return cube;
	}

	/**
	 * @param cube the cube to set
	 */
	public void setCube(ICube cube) {
		this.cube = cube;
	}

	/**
	 * @return the measure
	 */
	public IMeasure getMeasure() {
		return measure;
	}

	/**
	 * @param measure the measure to set
	 */
	public void setMeasure(IMeasure measure) {
		this.measure = measure;
	}

	/**
	 * @return the verticals
	 */
	public List<INavigationProvider> getRowProvider() {
		return rowProvider;
	}

	/**
	 * @return the horizontals
	 */
	public List<INavigationProvider> getColumnProvider() {
		return columnProvider;
	}

	/**
	 * @return
	 */
	public Key getTotalKey() {
		return cube.createKey("");
	}


	/**
	 * @return the numberFormat
	 */
	public NumberFormat getNumberFormat() {
		return numberFormat;
	}


	/**
	 * @param numberFormat the numberFormat to set
	 */
	public void setNumberFormat(NumberFormat numberFormat) {
		this.numberFormat = numberFormat;
	}


	/**
	 * @param navigationProvider
	 */
	public void addColumnNavigationProvider(INavigationProvider navigationProvider) {
		columnProvider.add(navigationProvider);
	}

	/**
	 * @param vertical
	 * @param dimension
	 */
	public void addRowNavigationProvider(INavigationProvider navigationProvider) {
		rowProvider.add(navigationProvider);
	}


	/**
	 * @return
	 */
	public Key createCursor() {
		// later: inject filter 
		Key key = cube.createKey("");
		key.setModifyable(true);
		return key;
	}


	/**
	 * @param dimVert
	 * @return
	 */
	public int getDimensionIndex(IDimension dimVert) {
		return cube.getDimensionIndex(dimVert);
	}
	
}
