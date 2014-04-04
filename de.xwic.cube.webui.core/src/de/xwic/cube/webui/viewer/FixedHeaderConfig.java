package de.xwic.cube.webui.viewer;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author bogdan
 *
 */
public class FixedHeaderConfig {
	
	private String width = "100%";
	private String height = "100%";
	private String themeClass = "fht-default";
	private boolean borderColapse = true;
	private int fixedColumns = 1;
	private boolean fixedColumn = true;
	private boolean sortable = false;
	private boolean autoShow = true;
	private boolean footer = false;
	private boolean cloneHeadToFoot = false;
	private int columnWidth = 86;
	private int firstColumnWidth = 276;
	private int heightOffset = 300;
	private int widthOffset = -15;
	
	/**
	 * 
	 */
	public FixedHeaderConfig() {
	}

	/**
	 * @return
	 */
	public String getWidth() {
		return width;
	}

	/**
	 * @param width
	 */
	public void setWidth(String width) {
		this.width = width;
	}

	/**
	 * @return
	 */
	public String getHeight() {
		return height;
	}

	/**
	 * @param height
	 */
	public void setHeight(String height) {
		this.height = height;
	}

	/**
	 * @return
	 */
	public String getThemeClass() {
		return themeClass;
	}

	/**
	 * @param themeClass
	 */
	public void setThemeClass(String themeClass) {
		this.themeClass = themeClass;
	}

	/**
	 * @return
	 */
	public boolean isBorderColapse() {
		return borderColapse;
	}

	/**
	 * @param borderColapse
	 */
	public void setBorderColapse(boolean borderColapse) {
		this.borderColapse = borderColapse;
	}

	/**
	 * @return
	 */
	public int getFixedColumns() {
		return fixedColumns;
	}

	/**
	 * @param fixedColumns
	 */
	public void setFixedColumns(int fixedColumns) {
		this.fixedColumns = fixedColumns;
	}

	/**
	 * @return
	 */
	public boolean isFixedColumn() {
		return fixedColumn;
	}

	/**
	 * @param fixedColumn
	 */
	public void setFixedColumn(boolean fixedColumn) {
		this.fixedColumn = fixedColumn;
	}

	/**
	 * @return
	 */
	public boolean isSortable() {
		return sortable;
	}

	/**
	 * @param sortable
	 */
	public void setSortable(boolean sortable) {
		this.sortable = sortable;
	}

	/**
	 * @return
	 */
	public boolean isAutoShow() {
		return autoShow;
	}

	/**
	 * @param autoShow
	 */
	public void setAutoShow(boolean autoShow) {
		this.autoShow = autoShow;
	}

	/**
	 * @return
	 */
	public boolean isFooter() {
		return footer;
	}

	/**
	 * @param footer
	 */
	public void setFooter(boolean footer) {
		this.footer = footer;
	}

	/**
	 * @return
	 */
	public boolean isCloneHeadToFoot() {
		return cloneHeadToFoot;
	}

	/**
	 * @param cloneHeadToFoot
	 */
	public void setCloneHeadToFoot(boolean cloneHeadToFoot) {
		this.cloneHeadToFoot = cloneHeadToFoot;
	}

	/**
	 * @return
	 */
	public int getColumnWidth() {
		return columnWidth;
	}

	/**
	 * @param columnWidth
	 */
	public void setColumnWidth(int columnWidth) {
		this.columnWidth = columnWidth;
	}

	/**
	 * @return
	 */
	public int getFirstColumnWidth() {
		return firstColumnWidth;
	}

	/**
	 * @param firstColumnWidth
	 */
	public void setFirstColumnWidth(int firstColumnWidth) {
		this.firstColumnWidth = firstColumnWidth;
	}
	
	/**
	 * @return
	 */
	public int getHeightOffset() {
		return heightOffset;
	}
	
	/**
	 * @param heightOffset
	 */
	public void setHeightOffset(int heightOffset) {
		this.heightOffset = heightOffset;
	}
	
	/**
	 * @return
	 */
	public int getWidthOffset() {
		return widthOffset;
	}
	
	/**
	 * @param widthOffset
	 */
	public void setWidthOffset(int widthOffset) {
		this.widthOffset = widthOffset;
	}

	protected JSONObject toJSONObject() throws JSONException{
		JSONObject object = new JSONObject();
		object.put("width", getWidth());
		object.put("themeClass", getThemeClass());
		object.put("borderCollapse", isBorderColapse());
		object.put("fixedColumns", getFixedColumns());
		object.put("fixedColumn", getFixedColumns());
		object.put("sortable", isSortable());
		object.put("autoShow", isAutoShow());
		object.put("footer", isFooter());
		object.put("cloneHeadToFoot", isCloneHeadToFoot());
		object.put("columnWidth", getColumnWidth());
		object.put("firstColumnWidth", getFirstColumnWidth());
		object.put("heightOffset", getHeightOffset());
		object.put("widthOffset", getWidthOffset());
		return object;
	}

	
	
}
