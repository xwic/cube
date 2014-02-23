/**
 * 
 */
package de.xwic.cube.webui.util;

import de.xwic.cube.webui.viewer.ContentInfo;

/**
 * @author Florian Lippisch
 */
public class TableCell {
	
	private String content = "";
	private String cssClass = "";
	private String action = "";
	private String actionParam = "";
	private int colSpan = 1;
	private int level;
	private ContentInfo data;
	private boolean title;
	private int index = 0;
	private TableRow parent;
	public String elementId;
	private boolean expandable;
	private String group;
	/**
	 * @param content
	 */
	public TableCell() {
		super();
	}

	/**
	 * @param content2
	 */
	public TableCell(String content) {
		this.content = content;
	}

	/**
	 * @return the content
	 */
	public String getContent() {
		return content;
	}

	/**
	 * @param content the content to set
	 */
	public TableCell setContent(String content) {
		this.content = content;
		return this;
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
	public TableCell setCssClass(String cssClass) {
		this.cssClass = cssClass;
		return this;
	}

	/**
	 * @return the colSpan
	 */
	public int getColSpan() {
		return colSpan;
	}

	/**
	 * @param colSpan the colSpan to set
	 */
	public TableCell setColSpan(int colSpan) {
		this.colSpan = colSpan;
		return this;
	}

	/**
	 * @return the action
	 */
	public String getAction() {
		return action;
	}

	/**
	 * @param action the action to set
	 */
	public void setAction(String action) {
		this.action = action;
	}

	/**
	 * @return the actionParam
	 */
	public String getActionParam() {
		return actionParam;
	}

	/**
	 * @param actionParam the actionParam to set
	 */
	public void setActionParam(String actionParam) {
		this.actionParam = actionParam;
	}
	
	public void setLevel(int level) {
		this.level = level;	
	}
	public int getLevel() {
		return level;
	}
	
	public void setData(ContentInfo data) {
		this.data = data;
	}
	public ContentInfo getData() {
		return data;
	}

	public void setTitle(boolean b) {
		this.title = b;
	}

	public boolean isTitle() {
		return this.title;
	}
	
	public int getIndex() {
		return index;
	}
	
	void setIndex(int index) {
		this.index = index;
	}
	
	public TableRow getParent() {
		return parent;
	}
	
	void setParent(TableRow parent) {
		this.parent = parent;
	}
	
	public void setElementId(String elementId) {
		this.elementId = elementId;
	}
	
	public String getElementId() {
		return elementId;
	}
	
	public void setExpandable(boolean expandable) {
		this.expandable = expandable;
	}
	public boolean isExpandable() {
		return expandable;
	}

	public void setGroup(String group) {
		this.group = group;
	}
	
	public String getGroup(){
		return this.group;
	}
}
