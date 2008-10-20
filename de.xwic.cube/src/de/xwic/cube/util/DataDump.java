/**
 * 
 */
package de.xwic.cube.util;

import java.io.PrintStream;
import java.text.NumberFormat;
import java.util.Locale;

import de.xwic.cube.ICube;
import de.xwic.cube.IDimension;
import de.xwic.cube.IDimensionElement;
import de.xwic.cube.IMeasure;
import de.xwic.cube.IValueFormat;

/**
 * @author Florian Lippisch
 */
public class DataDump {

	/**
	 * Print the Dimension Structure.
	 * @param out
	 * @param dimension
	 */
	public static void printStructure(PrintStream out, IDimension dimension) {
		
		out.println(dimension.getID());
		for (IDimensionElement element : dimension.getDimensionElements()) {
			printStructure(1, out, element);
		}
		
	}

	/**
	 * @param i
	 * @param out
	 * @param element
	 */
	private static void printStructure(int level, PrintStream out, IDimensionElement element) {
		
		StringBuilder sbSpaces = new StringBuilder();
		for (int i = 0; i < level; i++) {
			sbSpaces.append("  ");
		}
		
		out.println(sbSpaces + element.getKey());
		for (IDimensionElement elm : element.getDimensionElements()) {
			printStructure(level + 1, out, elm);
		}
		
	}

	public static void printValues(PrintStream out, ICube cube, IDimension vertical, IDimension horizontal, IMeasure measure) {
		
		// print header
		out.print(space(20));
		for (IDimensionElement elm : horizontal.getDimensionElements()) {
			out.print(fixedString(elm.getKey(), 10));
			out.print(" ");
		}
		out.print(fixedString("Total", 10));
		out.println("");
		
		printValueLine(out, cube, vertical, horizontal, 0, measure);
		
		//out.print(fixedString("Total", 20));

	}
	
	
	/**
	 * @param out
	 * @param nf 
	 * @param cube
	 * @param measure 
	 * @param verticals
	 * @param horizontals
	 * @param i
	 * @param key
	 */
	private static void printValueLine(PrintStream out, ICube cube, IDimensionElement verticalElm, IDimension horizontal, int idx, IMeasure measure) {
		
		out.print(space(idx * 2));
		out.print(fixedString(verticalElm.getKey(), 20 - (idx * 2)));
		
		IValueFormat nf = measure.getValueFormatProvider().createValueFormat(Locale.getDefault());
		
		for (IDimensionElement elm : horizontal.getDimensionElements()) {
			
			Double value = cube.getCellValue(verticalElm.getID() + elm.getID(), measure);
			out.print(fixedString(value != null ? nf.format(value.doubleValue()) : "", 10));
			out.print(" ");
		}
		Double value = cube.getCellValue(verticalElm.getID(), measure);
		out.print(fixedString(value != null ? nf.format(value.doubleValue()) : "", 10));
		out.print(" ");
		out.println();
		
		if (!verticalElm.isLeaf()) {
			idx++;
			for (IDimensionElement elm : verticalElm.getDimensionElements()) {
				printValueLine(out, cube, elm, horizontal, idx, measure);
			}
		}
		
	}

	private static String space(int size) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < size; i++) {
			sb.append(' ');
		}
		return sb.toString();
	}
	
	private static String fixedString(String s, int size) {
		if (s.length() < size) {
			return s + space(size - s.length());
		} else {
			return s.substring(0, size);
		}
	}
}
