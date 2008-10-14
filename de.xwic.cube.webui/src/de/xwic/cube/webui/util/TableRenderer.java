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

	private List<List<TableCell>> rows = new ArrayList<List<TableCell>>();
	
	private int row = 0;
	private int col = 0;
	
	public void render(PrintWriter out) {
		
		out.println("<TABLE border=1>");
		for (List<TableCell> row : rows) {
			out.println("<TR>");
			for (TableCell cell : row) {
				out.print("<TD>");
				out.print(cell.getContent() != null ? cell.getContent() : "");
				out.print("</TD>");
			}
			out.println("</TR>");
		}
		out.println("</TABLE>");
	}

	/**
	 * @param string
	 */
	public void append(String content) {
	
		while (rows.size() <= row) {
			rows.add(new ArrayList<TableCell>());
		}
		List<TableCell> cells = rows.get(row);
		cells.add(new TableCell(content));
	}

	/**
	 * 
	 */
	public void newRow() {
		row++;
		col = 0;
		
	}
	
}
