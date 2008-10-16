/**
 * 
 */
package de.xwic.cube.webui.util;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class to render a HTML table.
 * @author Florian Lippisch
 */
public class TableRenderer {

	private List<Object> columnData = new ArrayList<Object>();
	private List<Object> rowData = new ArrayList<Object>();
	
	private List<List<TableCell>> rows = new ArrayList<List<TableCell>>();
	
	private String cssClass = "";
	
	public void render(PrintWriter out) {
		
		out.println("<TABLE cellspacing=0 cellpadding=0 class=\"" + cssClass + "\">");
		for (List<TableCell> row : rows) {
			out.println("<TR>");
			int skip = 0;
			for (TableCell cell : row) {
				if (skip > 0) {
					skip--;
				} else {
					out.print("<TD");
					if (cell.getCssClass() != null) {
						out.print(" class=\"" + cell.getCssClass() + "\"");
					}
					if (cell.getColSpan() > 1) {
						out.print(" colspan=\"" + cell.getColSpan() + "\"");
					}
					out.print(">");
					out.print(cell.getContent() != null ? cell.getContent() : "&nbsp;");
					out.print("</TD>");
					skip = cell.getColSpan() - 1;
				}
			}
			out.println("</TR>");
		}
		out.println("</TABLE>");
	}

	/**
	 * @param i
	 * @param j
	 */
	public void initSize(int rowCount, int colCount) {
		
		rows.clear();
		rowData.clear();
		columnData.clear();
		for (int i = 0; i < rowCount; i++) {
			List<TableCell> cells = new ArrayList<TableCell>();
			rows.add(cells);
			for (int c = 0; c < colCount; c++) {
				cells.add(new TableCell());
			}
			rowData.add(null);
		}
		
		for (int c = 0; c < colCount; c++) {
			columnData.add(null);
		}
		
	}
	
	/**
	 * Returns the cell at the specified position.
	 * @param row
	 * @param col
	 * @return
	 */
	public TableCell getCell(int row, int col) {
		if (rows.size() > row) {
			List<TableCell> cells = rows.get(row);
			if (cells.size() > col) {
				return cells.get(col);
			}
			throw new IndexOutOfBoundsException("Column index out of range. (" + col + " of " + cells.size() + ")");
		}
		throw new IndexOutOfBoundsException("Row index out of range.");
	}
	
	/**
	 * Set a column ID.
	 * @param col
	 * @param data
	 */
	public void setColumnData(int col, Object data) {
		if (columnData.size() < col) {
			throw new IndexOutOfBoundsException("Column index out of range");
		}
		columnData.set(col, data);
	}
	
	/**
	 * Set the row id.
	 * @param row
	 * @param data
	 */
	public void setRowData(int row, Object data) {
		if (rowData.size() < row) {
			throw new IndexOutOfBoundsException("Row index out of range");
		}
		rowData.set(row, data);
	}
	
	/**
	 * Returns the custom Id for that column.
	 * @param col
	 * @return
	 */
	public Object getColumnData(int col) {
		return columnData.get(col);
	}
	
	/**
	 * Returns the custom Id for that row.
	 * @param row
	 * @return
	 */
	public Object getRowData(int row) {
		return rowData.get(row);
	}

	/**
	 * @return the cssClass
	 */
	public String getCssClass() {
		return cssClass;
	}

	/**
	 * @param cssClass the cssClass to set
	 */
	public void setCssClass(String cssClass) {
		this.cssClass = cssClass;
	}
	
}
