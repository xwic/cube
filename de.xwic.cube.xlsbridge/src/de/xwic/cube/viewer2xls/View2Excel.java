/**
 * 
 */
package de.xwic.cube.viewer2xls;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;

import de.xwic.cube.webui.util.TableCell;
import de.xwic.cube.webui.util.TableRenderer;
import de.xwic.cube.webui.viewer.CubeViewer;

/**
 * @author lippisch
 */
public class View2Excel {
	
	/**
	 * Render a CubeViewer to the specified sheet & location.
	 * @param viewer
	 * @param sheet
	 * @param row
	 * @param cell
	 * @param addRows
	 * @param percentageStyle
	 */
	public static void renderViewerToSheet(CubeViewer viewer, HSSFSheet sheet, HSSFRow row, HSSFCell cell, boolean addRows, HSSFCellStyle percentageStyle) {
		
		if (percentageStyle == null) {
			percentageStyle = sheet.getWorkbook().createCellStyle();
			percentageStyle.setDataFormat((short)10);
		}
		
		TableRenderer table = viewer.renderTable();
		
		if (addRows) {
			sheet.shiftRows(row.getRowNum() + 1, sheet.getLastRowNum(), table.getRowCount());
		}
		
		int xlRowNumStart = row.getRowNum();
		int xlColNumStart = cell.getColumnIndex();
		
		for (int rowNum = 0; rowNum < table.getRowCount(); rowNum++) {
			
			int xlRowNum = xlRowNumStart + rowNum;
			HSSFRow xlRow = sheet.getRow(xlRowNum);
			if (xlRow == null) {
				xlRow = sheet.createRow(xlRowNum);
			}

			for (int colNum = 0; colNum < table.getColCount(); colNum++) {
				
				int xlColNum = xlColNumStart + colNum;
				TableCell tblCell = table.getCell(rowNum, colNum);
				
				String content = tblCell.getContent();
				HSSFCell xlCell = xlRow.getCell(xlColNum);
				if (content != null) {
					if (xlCell == null) {
						xlCell = xlRow.createCell(xlColNum);
					}

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
					// only change cells that already exist.
					if (xlCell != null) {
						xlRow.removeCell(xlCell);
						//xlCell.setCellValue(new HSSFRichTextString(""));
						//xlCell.setCellType(HSSFCell.CELL_TYPE_STRING);
					}
				}
				
			}
			
		}

		
	}

}
