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

import de.xwic.cube.ICube;
import de.xwic.cube.IDataPool;
import de.xwic.cube.IDimensionElement;
import de.xwic.cube.IMeasure;
import de.xwic.cube.Key;

/**
 * Used to retrieve a single numeric value from the datapool.
 * Arguments: Key, [Measure], [CubeKey]
 * Sample:
 * =xcValue("[Name:*][GEO:EMEA/Germany]")
 * 
 * @author lippisch
 */
public class XCValueFunction extends AbstractFunction {

	public final static String FUNCTION_NAME = "xcValue";

	/**
	 * @param config
	 * @param workbook
	 * @param log
	 * @param dataPool 
	 */
	public XCValueFunction(Properties config, HSSFWorkbook workbook, ISimpleLog log, IDataPool dataPool, List<IDimensionElement> filters) {
		super(config, workbook, log, dataPool, filters);
	}
	
	/* (non-Javadoc)
	 * @see de.xwic.cube.xlsbridge.AbstractFunction#executeFunction(java.util.List, org.apache.poi.hssf.usermodel.HSSFSheet, org.apache.poi.hssf.usermodel.HSSFRow, org.apache.poi.hssf.usermodel.HSSFCell)
	 */
	@Override
	public void executeFunction(Match match, HSSFSheet sheet, HSSFRow row, HSSFCell cell) {
		
		if (match.args.size() < 1) {
			log.log("Error: xcValue requires 1 argument at least.");
		} else {
			String sKey = match.args.get(0);
			String sMeasure = (match.args.size() > 1) ? match.args.get(1) : config.getProperty("defaultMeasure");
			String sCube = match.args.size() > 3 ? match.args.get(3) : config.getProperty("defaultCube");
			
			if (sCube == null) {
				log.log("Error: xcValue does not contain a cube name and no default cube is defined. (row = " + row.getRowNum() + ")");
			} else if (sMeasure == null) {
				log.log("Error: xcValue does not contain a measure name and no default measure is defined. (row = " + row.getRowNum() + ")");
			} else {
				try {
					ICube cube = dataPool.getCube(sCube);
					IMeasure measure = dataPool.getMeasure(sMeasure);
					Key key = cube.createKey(sKey);
					
					// apply filter
					for (IDimensionElement fe : filters) {
						if (cube.getDimensions().contains(fe.getDimension())) {
							int idx = cube.getDimensionIndex(fe.getDimension());
							IDimensionElement elm = key.getDimensionElement(idx);
							if (elm.getDepth() == 0 && fe.getDepth() != 0) {
								key.setDimensionElement(idx, fe);
							}
						}
					}
					
					Double value = cube.getCellValue(key, measure);
					if (value == null) {
						cell.setCellFormula("\"\"");
					} else {
						
						if (config.getProperty("divide") != null) {
							double divide = Double.parseDouble(config.getProperty("divide"));
							value = new Double(value.doubleValue() / divide);
						}
						cell.setCellFormula(match.prefix + value.toString() + match.suffix);
						
					}
				} catch (Exception e) {
					log.log("Error reading value: " + e);
					cell.setCellFormula(match .prefix + "\"" + e.toString() + "\"" + match.suffix);
				}
			}
			
			//cell.setCellFormula("12345.0");
		}

		
	}
	
	
}
