/**
 * 
 */
package de.xwic.cube.xlsbridge;

import java.util.List;
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
	
	protected final Properties config;
	protected final HSSFWorkbook workbook;
	protected final ISimpleLog log;
	protected final IDataPool dataPool;
	protected final List<IDimensionElement> filters;
	
	/**
	 * @param config
	 * @param workbook
	 * @param dataPool 
	 */
	public AbstractFunction(Properties config, HSSFWorkbook workbook, ISimpleLog log, IDataPool dataPool, List<IDimensionElement> filters) {
		super();
		this.config = config;
		this.workbook = workbook;
		this.log = log;
		this.dataPool = dataPool;
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
	
}
