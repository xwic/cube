/**
 * 
 */
package de.xwic.cube.viewer2xls;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import de.xwic.cube.IDataPool;
import de.xwic.cube.IDimensionElement;
import de.xwic.cube.webui.viewer.CubeViewer;
import de.xwic.cube.xlsbridge.AbstractFunction;
import de.xwic.cube.xlsbridge.ISimpleLog;
import de.xwic.cube.xlsbridge.Match;

/**
 * Render a CubeViewer (as is) into the sheet.
 * @author lippisch
 */
public class InsertViewerFunction extends AbstractFunction {

	public final static String FUNCTION_NAME = "xcInsertViewer";
	
	private Map<String, CubeViewer> viewerMap = new HashMap<String, CubeViewer>();

	private HSSFCellStyle percentageStyle;
	
	/**
	 * Add a viewer.
	 * @param key
	 * @param viewer
	 */
	public void addViewer(String key, CubeViewer viewer) {
		viewerMap.put(key, viewer);
	}
	
	/* (non-Javadoc)
	 * @see de.xwic.cube.xlsbridge.AbstractFunction#initialize(de.xwic.cube.xlsbridge.ISimpleLog, de.xwic.cube.IDataPool, java.util.Properties, org.apache.poi.hssf.usermodel.HSSFWorkbook, java.util.Collection)
	 */
	@Override
	public void initialize(ISimpleLog log, IDataPool dataPool, Properties config, HSSFWorkbook workbook, Collection<IDimensionElement> filters) {
		super.initialize(log, dataPool, config, workbook, filters);
		
		percentageStyle = workbook.createCellStyle();
		percentageStyle.setDataFormat((short)10); 
		
	}
	
	/* (non-Javadoc)
	 * @see de.xwic.cube.xlsbridge.AbstractFunction#executeFunction(de.xwic.cube.xlsbridge.Match, org.apache.poi.hssf.usermodel.HSSFSheet, org.apache.poi.hssf.usermodel.HSSFRow, org.apache.poi.hssf.usermodel.HSSFCell)
	 */
	@Override
	public void executeFunction(Match match, HSSFSheet sheet, HSSFRow row, HSSFCell cell) {

		if (match.args.size() > 0) {
			
			CubeViewer viewer = viewerMap.get(match.args.get(0));
			if (viewer != null) {
				
				boolean addRows = false; 
				if (match.args.size() > 1) {
					addRows = "INSERTROWS".equalsIgnoreCase(match.args.get(1));
				}

				View2Excel.renderViewerToSheet(viewer, sheet, row, cell, addRows, percentageStyle);
				
				
			} else {
				replaceData(match, cell, "A viewer with the key '" + match.args.get(0) + "' does not exist. Valid keys: " + viewerMap.keySet().toString());	
			}
			
		} else {
			replaceData(match, cell, "No viewer specified. Valid keys: " + viewerMap.keySet().toString());
		}
		
		cell.setCellFormula("\"\""); // clear formula
		
	}

	/**
	 * @return the viewerMap
	 */
	public Map<String, CubeViewer> getViewerMap() {
		return viewerMap;
	}

	/**
	 * @param viewerMap the viewerMap to set
	 */
	public void setViewerMap(Map<String, CubeViewer> viewerMap) {
		this.viewerMap = viewerMap;
	}

}
