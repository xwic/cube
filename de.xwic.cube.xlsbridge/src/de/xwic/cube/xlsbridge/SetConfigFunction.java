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
 * @author lippisch
 *
 */
public class SetConfigFunction extends AbstractFunction {

	public final static String FUNCTION_NAME = "xcSetConfig";
	
	/**
	 * @param config
	 * @param workbook
	 * @param log
	 * @param dataPool
	 * @param filters
	 */
	public SetConfigFunction(Properties config, HSSFWorkbook workbook,	ISimpleLog log, IDataPool dataPool, List<IDimensionElement> filters) {
		super(config, workbook, log, dataPool, filters);
		
	}

	/* (non-Javadoc)
	 * @see de.xwic.cube.xlsbridge.AbstractFunction#executeFunction(java.util.List, org.apache.poi.hssf.usermodel.HSSFSheet, org.apache.poi.hssf.usermodel.HSSFRow, org.apache.poi.hssf.usermodel.HSSFCell)
	 */
	@Override
	public void executeFunction(Match match, HSSFSheet sheet, HSSFRow row, HSSFCell cell) {

		String key = null;
		for (int i = 0; i < match.args.size(); i++) {
			if (key == null) {
				key = match.args.get(i);
			} else {
				config.setProperty(key, match.args.get(i));
				key = null;
			}
		}
		cell.setCellFormula("\"\""); // clear formula

	}

}
