package de.xwic.cube.webui.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import de.xwic.cube.webui.viewer.ContentInfo;
import de.xwic.cube.webui.viewer.INavigationElementProvider;
import de.xwic.cube.webui.viewer.INavigationProvider;

/**
 * @author bogdan
 *
 */
public class TableRow implements Iterable<TableCell>{
	private final List<TableCell> cells;
	private int level;
	private int index;
	private String tableRowType;
	
	private Table parent;
	
	private ContentInfo data;
	
	/**
	 * 
	 */
	TableRow() {
		cells = new ArrayList<TableCell>();
	}
	
	/**
	 * @return
	 */
	public List<TableCell> getCells() {
		List<TableCell> cells = this.cells;
		if(!parent.isExpandLeft()){
			cells = new ArrayList<TableCell>(this.cells);
			//revers the whole list
			Collections.reverse(cells);
			//but keep the first column in place
			cells = cells.subList(0, cells.size()-1);
			cells.add(0, this.cells.get(0));
		}
		return cells;
	}

	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<TableCell> iterator() {
		return getCells().iterator();
	}
	
	/**
	 * @param cell
	 */
	public void addCell(TableCell cell){
		this.cells.add(cell);
		cell.setParent(this);
		cell.setIndex(cells.size()-1);
	}
	/**
	 * @param cell
	 * @param at
	 */
	public void addCellAt(TableCell cell, int at){
		this.cells.add(at, cell);
		cell.setParent(this);
		cell.setIndex(at);
	}
	
	/**
	 * @param cells
	 */
	public void addAllCells(Collection<TableCell> cells){
		for(TableCell cell : cells){
			this.addCell(cell);
		}
	}
	/**
	 * @return
	 */
	public int size(){
		return this.cells.size();
	}
	/**
	 * @param cellPos
	 * @return
	 */
	public TableCell getCell(int cellPos){
		if(cellPos >=cells.size()){
			return null;
		}
		return this.cells.get(cellPos);
	}
	
	/**
	 * @param data
	 */
	public void setData(ContentInfo data) {
		this.data = data;
	}
	
	/**
	 * @return
	 */
	public ContentInfo getData() {
		return data;
	}
	
	/**
	 * @param level
	 */
	public void setLevel(int level) {
		this.level = level;
	}
	
	/**
	 * @return
	 */
	public int getLevel() {
		return level;
	}
	
	/**
	 * @param parent
	 */
	void setParent(Table parent) {
		this.parent = parent;
	}
	
	/**
	 * @return
	 */
	public Table getParent() {
		return parent;
	}
	
	/**
	 * @param index
	 */
	void setIndex(int index) {
		this.index = index;
	}
	
	/**
	 * @return
	 */
	public int getIndex(){
		if(parent == null){
			return 0;
		}
		return this.index;
	}

	public String getTableRowType() {
		return (StringUtils.isEmpty(tableRowType))?INavigationProvider.NavigationProviderTypes.NORMAL.name():tableRowType;
	}

	public void setTableRowType(String tableRowType) {
		this.tableRowType = tableRowType;
	}


	
	
}
