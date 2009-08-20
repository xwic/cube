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
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import de.xwic.cube.IDataPool;
import de.xwic.cube.IDimensionElement;
import de.xwic.cube.webui.util.TableCell;
import de.xwic.cube.webui.util.TableRenderer;
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

				TableRenderer table = viewer.renderTable();
				
				if (addRows) {
					sheet.shiftRows(row.getRowNum() + 1, sheet.getLastRowNum(), table.getRowCount());
				}
				
				int xlRowNumStart = row.getRowNum();
				int xlColNumStart = cell.getCellNum();
				
				for (int rowNum = 0; rowNum < table.getRowCount(); rowNum++) {
					
					int xlRowNum = xlRowNumStart + rowNum;
					HSSFRow xlRow = sheet.getRow(xlRowNum);
					if (xlRow == null) {
						xlRow = sheet.createRow(xlRowNum);
					}

					for (int colNum = 0; colNum < table.getColCount(); colNum++) {
						
						short xlColNum = (short)(xlColNumStart + colNum);
						TableCell tblCell = table.getCell(rowNum, colNum);
						
						HSSFCell xlCell = xlRow.getCell(xlColNum);
						if (xlCell == null) {
							xlCell = xlRow.createCell((short)xlColNum);
						}
						
						String content = tblCell.getContent();
						if (content != null) {
							// strip HTML tags
							content = content.replaceAll("<[^>]*>", "");
							// is Numeric?
							Double numValue = null;
							boolean percentage = false;
							String tmp = content.replaceAll(",", "");
							
							if (tmp.endsWith("%")) {
								percentage = true;
								tmp = tmp.substring(0, tmp.length() - 1).trim();
							}
							
							if (tmp.matches("[-+]?[0-9]*\\.?[0-9]+")) { // is a number
								
								try {
									double dbl = Double.parseDouble(tmp);
									if (percentage) {
										dbl = dbl / 100;
									}
									numValue = dbl;
								} catch (Exception e) {
									// do nothing -> its not a number...
								}
							}
							if (numValue != null) {
								xlCell.setCellValue(numValue.doubleValue());
								if (percentage) {
									xlCell.setCellStyle(percentageStyle);
								}
							} else {
								xlCell.setCellValue(new HSSFRichTextString(content));
							}
						} else {
							xlCell.setCellValue(new HSSFRichTextString(""));
							xlCell.setCellType(HSSFCell.CELL_TYPE_STRING);
						}
						
					}
					
				}
				
				
				
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
