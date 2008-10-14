/**
 * 
 */
package de.xwic.cube.webui.util;

/**
 * @author Florian Lippisch
 */
public class TableCell {

	private String content = null;
	
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
	public void setContent(String content) {
		this.content = content;
	}
	
}
