/**
 * 
 */
package de.xwic.cube.xlsbridge;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;

import de.xwic.cube.IDimensionElement;

/**
 * @author lippisch
 */
public class FilterInfoFunction extends AbstractFunction {

	public final static String FUNCTION_NAME = "xcFilterInfo";
	
	/* (non-Javadoc)
	 * @see de.xwic.cube.xlsbridge.AbstractFunction#executeFunction(java.util.List, org.apache.poi.hssf.usermodel.HSSFSheet, org.apache.poi.hssf.usermodel.HSSFRow, org.apache.poi.hssf.usermodel.HSSFCell)
	 */
	@Override
	public void executeFunction(Match match, HSSFSheet sheet, HSSFRow row, HSSFCell cell) {

		String info;
		if (match.args.size() > 0) {
			info = null;
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
			
			if (info == null) {
				if (dataPool.containsDimension(dimKey)) {
					info = "- All -";
				} else {
					info = "Specified Dimension does not exist.";
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
