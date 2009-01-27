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
 */
public class FilterInfoFunction extends AbstractFunction {

	public final static String FUNCTION_NAME = "xcFilterInfo";
	
	/**
	 * @param config
	 * @param workbook
	 * @param log
	 * @param dataPool
	 * @param filters
	 */
	public FilterInfoFunction(Properties config, HSSFWorkbook workbook, ISimpleLog log, IDataPool dataPool, List<IDimensionElement> filters) {
		super(config, workbook, log, dataPool, filters);
	}

	/* (non-Javadoc)
	 * @see de.xwic.cube.xlsbridge.AbstractFunction#executeFunction(java.util.List, org.apache.poi.hssf.usermodel.HSSFSheet, org.apache.poi.hssf.usermodel.HSSFRow, org.apache.poi.hssf.usermodel.HSSFCell)
	 */
	@Override
	public void executeFunction(Match match, HSSFSheet sheet, HSSFRow row, HSSFCell cell) {

		String info;
		if (match.args.size() > 0) {
			info = "Unknown Dimension";
			String dimKey = match.args.get(0);
			for (IDimensionElement de : filters) {
				if (de.getDimension().getKey().equals(dimKey)) {
					if (de.getDepth() == 0) {
						info = "- All -";
					} else {
						info = de.getPath();
					}
					break;
				}
			}
		} else {
			info = "";
			for (IDimensionElement de : filters) {
				info = info + de.getID();
			}
		}
		cell.setCellFormula(match.prefix + "\"" + info + "\"" + match.suffix);
		
		

	}

}
