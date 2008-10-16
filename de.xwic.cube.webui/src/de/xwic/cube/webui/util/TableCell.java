/**
 * 
 */
package de.xwic.cube.webui.util;

/**
 * @author Florian Lippisch
 */
public class TableCell {

	private String content = null;
	private String cssClass = null;
	private int colSpan = 1;
	
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
	
}
