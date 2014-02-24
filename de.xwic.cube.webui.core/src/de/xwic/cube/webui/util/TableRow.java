package de.xwic.cube.webui.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import de.xwic.cube.webui.viewer.ContentInfo;

/**
 * @author bogdan
 *
 */
public class TableRow implements Iterable<TableCell>{
	private final List<TableCell> cells;
	private String cssClass = "";
	private int level;
	private int index;
	
	private Table parent;
	
	private ContentInfo data;
	
	/**
	 * 
	 */
	public TableRow() {
		cells = new ArrayList<TableCell>();
	}
	
	/**
	 * @return
	 */
	public List<TableCell> getCells() {
		return cells;
	}

	@Override
	public Iterator<TableCell> iterator() {
		return cells.iterator();
	}
	
	public void addCell(TableCell cell){
		this.cells.add(cell);
		cell.setParent(this);
		cell.setIndex(cells.size()-1);
	}
	public void addCellAt(TableCell cell, int at){
		this.cells.add(at, cell);
		cell.setParent(this);
		cell.setIndex(at);
	}
	
	public void addAllCells(Collection<TableCell> cells){
		for(TableCell cell : cells){
			this.addCell(cell);
		}
	}
	public int size(){
		return this.cells.size();
	}
	public TableCell getCell(int cellPos){
		if(cellPos >=cells.size()){
			return null;
		}
		return this.cells.get(cellPos);
	}
	
	public void setCssClass(String cssClass){
		this.cssClass = cssClass;
	}
	
	public void setData(ContentInfo data) {
		this.data = data;
	}
	
	public ContentInfo getData() {
		return data;
	}
	
	public void setLevel(int level) {
		this.level = level;
	}
	
	public int getLevel() {
		return level;
	}
	
	void setParent(Table parent) {
		this.parent = parent;
	}
	
	public Table getParent() {
		return parent;
	}
	
	void setIndex(int index) {
		this.index = index;
	}
	
	public int getIndex(){
		if(parent == null){
			return 0;
		}
		return this.index;
	}
}
