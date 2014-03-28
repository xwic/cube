/**
 * 
 */
package de.xwic.cube.webui.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Utility class to render a HTML table.
 * 
 * @author Florian Lippisch
 */
public class Table implements Iterable<TableRow>{

	private final List<Object> columnData = new ArrayList<Object>();
	private final List<Object> rowData = new ArrayList<Object>();

	private final List<Integer> colWidth = new ArrayList<Integer>();

	private final List<TableRow> rows = new ArrayList<TableRow>();

	private final List<TableRow> headRows = new ArrayList<TableRow>();
	
	private int rowCount;
	private int colCount;

	private int maxRowLevel = 0;
	private int maxColLevel = 0;

	private boolean expandDown;

	private boolean expandLeft;
	
	

	/**
	 * @param baseControl
	 */
	public Table() {
		super();
		this.expandDown = true;
		this.expandLeft = false;
		
	}
	/**
	 * @param i
	 * @param j
	 */
	public void initSize(int rowCount, int colCount) {

		this.rowCount = rowCount;
		this.colCount = colCount;

		rows.clear();
		for (int i = 0; i < rowCount; i++) {
			List<TableCell> cells = new ArrayList<TableCell>();
			for (int c = 0; c < colCount; c++) {
				cells.add(new TableCell());
			}

			TableRow row = new TableRow();
			row.setParent(this);
			row.addAllCells(cells);
			rows.add(row);
			rowData.add(null);
		}

		for (int c = 0; c < colCount; c++) {
			columnData.add(null);
			colWidth.add(null);
		}

	}

	/**
	 * Returns the cell at the specified position.
	 * 
	 * @param row
	 * @param col
	 * @return
	 */
	public TableCell getCell(int row, int col) {
		if(headRows.size()>row){
			TableRow cells = headRows.get(row);
			if (cells.size() > col) {
				return cells.getCell(col);
			}
			throw new IndexOutOfBoundsException("Column index out of range. ("
					+ col + " of " + cells.size() + ")");
		}
		row = row - headRows.size();//set the correct index
		if (rows.size() > row) {
			TableRow cells = rows.get(row);
			if (cells.size() > col) {
				return cells.getCell(col);
			}
			throw new IndexOutOfBoundsException("Column index out of range. ("
					+ col + " of " + cells.size() + ")");
		}
		throw new IndexOutOfBoundsException("Row index out of range.");
	}

	/**
	 * Set a column ID.
	 * 
	 * @param col
	 * @param data
	 */
	public void setColumnData(int col, Object data) {
		if (columnData.size() <= col) {
			throw new IndexOutOfBoundsException("Column index out of range");
		}
		columnData.set(col, data);
	}

	/**
	 * Set the column width.
	 * 
	 * @param col
	 * @param width
	 */
	public void setColumnWidth(int col, Integer width) {
		colWidth.set(col, width);
	}

	/**
	 * Set the row id.
	 * 
	 * @param row
	 * @param data
	 */
	public void setRowData(int row, Object data) {
		if (rowData.size() <= row) {
			throw new IndexOutOfBoundsException("Row index out of range");
		}
		rowData.set(row, data);
	}

	/**
	 * Returns the custom Id for that column.
	 * 
	 * @param col
	 * @return
	 */
	public Object getColumnData(int col) {
		return columnData.get(col);
	}

	/**
	 * Returns the custom Id for that row.
	 * 
	 * @param row
	 * @return
	 */
	public Object getRowData(int row) {
		return rowData.get(row);
	}

	/**
	 * @return the rowCount
	 */
	public int getRowCount() {
		return rowCount;
	}

	/**
	 * @return the colCount
	 */
	public int getColCount() {
		return colCount;
	}

	public List<TableRow> getRows() {
		List<TableRow> rows = this.rows;
		if(expandDown){
			rows = new ArrayList<TableRow>(this.rows);
			Collections.reverse(rows);
		}
		return rows;
	}

	/**
	 * @return
	 */
	public int getMaxRowLevel() {
		return maxRowLevel;
	}

	/**
	 * @param maxRowLevel
	 */
	public void setMaxRowLevel(int maxRowLevel) {
		this.maxRowLevel = maxRowLevel;
	}

	/**
	 * @return
	 */
	public int getMaxColLevel() {
		return maxColLevel;
	}

	/**
	 * @param maxColLevel
	 */
	public void setMaxColLevel(int maxColLevel) {
		this.maxColLevel = maxColLevel;
	}

	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<TableRow> iterator() {
		return getRows().iterator();
	}
	
	/**
	 * @param row
	 */
	public void addRow(TableRow row){
		this.rows.add(row);
		row.setIndex(rows.size()-1);
		row.setParent(this);
	}
	/**
	 * @param row
	 * @param at
	 */
	public void addRowAt(TableRow row, int at){
		this.rows.add(at, row);
		row.setIndex(at);
		row.setParent(this);
	}

	/**
	 * @param rowCount
	 * @param colCount
	 */
	public void initHead(int rowCount, int colCount) {
		rowData.clear();
		for (int i = 0; i < rowCount; i++) {
			List<TableCell> cells = new ArrayList<TableCell>();
			for (int c = 0; c < colCount; c++) {
				cells.add(new TableCell());
			}

			TableRow row = new TableRow();
			row.setParent(this);
			row.addAllCells(cells);
			headRows.add(row);
			rowData.add(null);
		}

	}
	
	
	/**
	 * @return
	 */
	public List<TableRow> getHeadRows() {
		return headRows;
	}

	/**
	 * @param down
	 * @param left
	 */
	public void setExpandDirections(boolean down, boolean left) {
		this.expandDown = down;
		this.expandLeft = left;
	}
	
	/**
	 * @return
	 */
	boolean isExpandDown() {
		return expandDown;
	}
	
	/**
	 * @return
	 */
	boolean isExpandLeft() {
		return expandLeft;
	}
}
