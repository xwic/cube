/**
 * 
 */
package de.xwic.cube.xlsbridge;

import java.util.Collection;
import java.util.Properties;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import de.xwic.cube.IDataPool;
import de.xwic.cube.IDimensionElement;

/**
 * Used to retrieve a single numeric value from the datapool.
 * Arguments: Key, [Measure], [CubeKey]
 * Sample:
 * =xcValue("[Name:*][GEO:EMEA/Germany]")
 * 
 * @author lippisch
 */
public abstract class AbstractFunction {
	
	protected Properties config = null;
	protected HSSFWorkbook workbook = null;
	protected ISimpleLog log;
	protected IDataPool dataPool;
	protected Collection<IDimensionElement> filters = null;

	/**
	 * @param config
	 * @param workbook
	 * @param dataPool 
	 */
	public void initialize(ISimpleLog log, IDataPool dataPool, Properties config, HSSFWorkbook workbook, Collection<IDimensionElement> filters) {
		this.log = log;
		this.dataPool = dataPool;
		this.config = config;
		this.workbook = workbook;
		this.filters = filters;
	}

	
	/**
	 * Execute the function.
	 * @param match
	 * @param sheet
	 * @param row
	 * @param cell
	 */
	public abstract void executeFunction(Match match, HSSFSheet sheet, HSSFRow row, HSSFCell cell);
	

	/**
	 * Replace the function with a custom value.
	 * @param match
	 * @param cell
	 * @param value
	 */
	protected void replaceData(Match match, HSSFCell cell, Object value) {
		
		if (value instanceof Number) {
			cell.setCellFormula(match.prefix + "" + value + "" + match.suffix);
		} else {
			cell.setCellFormula(match.prefix + "\"" + value + "\"" + match.suffix);
		}
		
	}
	
}
