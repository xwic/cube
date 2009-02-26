/**
 * 
 */
package de.xwic.cube.xlsbridge;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import de.xwic.cube.IDataPool;
import de.xwic.cube.IDimensionElement;

/**
 * This class is used to fill an existing XLS Template with data from a datapool.
 * 
 * @author lippisch
 */
public class CubeToExcel implements ISimpleLog {

	private final IDataPool dataPool;
	private StringBuilder log = new StringBuilder();
	private HSSFWorkbook wb;

	private Properties config = new Properties();

	private Map<String, AbstractFunction> functions = new HashMap<String, AbstractFunction>();
	private List<IDimensionElement> filters = new ArrayList<IDimensionElement>();
	
	/**
	 * Construct a new instance.
	 * @param dataPool
	 */
	public CubeToExcel(IDataPool dataPool) {
		this.dataPool = dataPool;
		
	}

	/**
	 * Create the Workbook from the template.
	 * @param template
	 * @return
	 * @throws IOException
	 */
	public HSSFWorkbook createWorkbook(InputStream template) throws IOException {
		
		log("Reading Template..");
		POIFSFileSystem fs = new POIFSFileSystem(template);
		wb = new HSSFWorkbook(fs);

		readConfig();
		
		// initialize functions.
		functions.clear();
		functions.put(XCValueFunction.FUNCTION_NAME, new XCValueFunction(config, wb, this, dataPool, filters));
		functions.put(FilterInfoFunction.FUNCTION_NAME, new FilterInfoFunction(config, wb, this, dataPool, filters));
		functions.put(SetConfigFunction.FUNCTION_NAME, new SetConfigFunction(config, wb, this, dataPool, filters));
		
		// process all sheets
		int num = wb.getNumberOfSheets();
		for (int i = 0; i < num; i++) {
			HSSFSheet sheet = wb.getSheetAt(i);
			fillSheet(sheet);
		}
		
		
		log("Finished.");
		
		return wb;
		
	}

	/**
	 * @param sheet
	 */
	private void fillSheet(HSSFSheet sheet) {

		int lastRow = sheet.getLastRowNum();

		for (int i = 0; i <= lastRow; i++) {
			
			HSSFRow row = sheet.getRow(i);
			if (row != null) {
				for (Iterator<?> it = row.cellIterator(); it.hasNext(); ) {
					HSSFCell cell = (HSSFCell)it.next();
					if (cell != null) {
						if (cell.getCellType() == HSSFCell.CELL_TYPE_FORMULA) {
							// only consider formulas
							
							String formula = cell.getCellFormula();
							
							log(formula);
							if (formula != null) {
								
								Match match = getFunctionArgs(formula);
								while (match != null) {
									//log("Identified function: " + args.get(0));
									AbstractFunction func = functions.get(match.functionName);
									if (func != null) {
										func.executeFunction(match, sheet, row, cell);
									} else {
										log("Related function not found: " + match.functionName);
									}
									formula = cell.getCellFormula();
									match = getFunctionArgs(formula);
								}
								
							}
							
						}
					}
				}
			}
			
		}
		
		
	}

	/**
	 * @param formula
	 * @return
	 */
	private Match getFunctionArgs(String formula) {
		
		for (String funcName : functions.keySet()) {
			int idx = formula.indexOf(funcName);
			if (idx != -1) {
				Match match = new Match();
				match.functionName = funcName;
				int start = idx + funcName.length();
				StringBuilder sb = new StringBuilder();
				boolean started = false;
				boolean isArg = false;
				for (int i = start; i < formula.length(); i++) {
					char c = formula.charAt(i);
					if (!started) {
						if (c == '(') {
							started = true;
						}
					} else {
						if (c == '"') {
							if (isArg) {
								match.args.add(sb.toString());
								sb.setLength(0);
								isArg = false;
							} else {
								isArg = true;
							}
						} else if (isArg) {
							sb.append(c);
						} else if (c == ')') { // end
							match.suffix = formula.substring(i + 1);
							break;
						}
					}
				}
				match.prefix = formula.substring(0, idx);
				if (match.prefix.startsWith("_xlfn.IFERROR(")) {
					match.prefix = match.prefix.substring(14);
					if (match.suffix.length() != 0) {
						int lastComma = match.suffix.lastIndexOf(',');
						if (lastComma != -1) {
							match.suffix = match.suffix.substring(0, lastComma);
						}
					}
				}
				return match;
			}
		}
		return null;
	}

	/**
	 * 
	 */
	private void readConfig() {

		HSSFSheet cfgSheet = wb.getSheet("config");
		if (cfgSheet != null) {
			log("Reading configuration...");
			
			boolean started = false;
			for (int i = 0; i <= cfgSheet.getLastRowNum(); i++) {
				HSSFRow row = cfgSheet.getRow(i);
				if (row != null) {
					String key = getString(row, 0);
					if (started) {
						if (key != null && key.length() != 0) {
							String obj = getString(row, 1);
							config.setProperty(key, obj);
							log(key + "=" + obj);
						}
					} else if ("Key".equals(key)) {
						started = true;
					}
				}
			}
			
			log("Removing Config Sheet");
			wb.removeSheetAt(wb.getSheetIndex("config"));
			
			
		} else {
			log("No config sheet found.");
		}
		
		
	}

	/**
	 * Returns the value in the specified cell as a string.
	 * @param row
	 * @param col
	 * @return
	 */
	private String getString(HSSFRow row, int col) {
		Object o = getObject(row, col);
		if (o != null) {
			return o.toString();
		}
		return null;
	}
	
	/**
	 * @param row
	 * @param i
	 * @return
	 */
	private Object getObject(HSSFRow row, int col) {
		
		if (row != null) {
			HSSFCell cell = row.getCell(col);
			if (cell != null) {
				switch (cell.getCellType()) {
				case HSSFCell.CELL_TYPE_STRING:
					return cell.getRichStringCellValue().getString();
				case HSSFCell.CELL_TYPE_NUMERIC:
					return new Double(cell.getNumericCellValue());
				}
			}
		}

		return null;
	}

	/**
	 * Simple log implementation.
	 */
	public void log(String message) {
		log.append(message).append("\n");
	}
	
	/**
	 * Returns the log.
	 * @return
	 */
	public String getLog() {
		return log.toString();
	}

	/**
	 * @return the filters
	 */
	public List<IDimensionElement> getFilters() {
		return filters;
	}

	/**
	 * @param filters the filters to set
	 */
	public void setFilters(List<IDimensionElement> filters) {
		this.filters = filters;
	}
	
}
