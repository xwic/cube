/**
 * 
 */
package de.xwic.cube.xlsbridge;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;

/**
 * @author lippisch
 *
 */
public class SetConfigFunction extends AbstractFunction {

	public final static String FUNCTION_NAME = "xcSetConfig";
	
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
