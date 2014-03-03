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
	private boolean expanded;
	/**
	 * @param content
	 */
	TableCell() {
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
		if(content == null || content.equals("")){
			return "&nbsp;";
		}
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
	 * @param b
	 */
	public void setTitle(boolean b) {
		this.title = b;
	}

	/**
	 * @return
	 */
	public boolean isTitle() {
		return this.title;
	}
	
	/**
	 * @return
	 */
	public int getIndex() {
		return index;
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
	public TableRow getParent() {
		return parent;
	}
	
	/**
	 * @param parent
	 */
	void setParent(TableRow parent) {
		this.parent = parent;
	}
	
	/**
	 * @param elementId
	 */
	public void setElementId(String elementId) {
		this.elementId = elementId;
	}
	
	/**
	 * @return
	 */
	public String getElementId() {
		return elementId;
	}
	
	/**
	 * @param expandable
	 */
	public void setExpandable(boolean expandable) {
		this.expandable = expandable;
	}
	/**
	 * @return
	 */
	public boolean isExpandable() {
		return expandable;
	}

	/**
	 * @param group
	 */
	public void setGroup(String group) {
		this.group = group;
	}
	
	/**
	 * @return
	 */
	public String getGroup(){
		return this.group;
	}
	
	/**
	 * @param expanded
	 */
	public void setExpanded(boolean expanded) {
		this.expanded = expanded;
	}
	
	/**
	 * @return
	 */
	public boolean isExpanded() {
		return expanded;
	}
}
