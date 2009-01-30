/**
 * 
 */
package de.xwic.cube.xlsbridge;

import java.util.ArrayList;
import java.util.List;

/**
 * Used to identify a function that has been found.
 * @author lippisch
 */
public class Match {

	String functionName = null;
	List<String> args = new ArrayList<String>();
	
	String prefix = null;
	String suffix = "";
	
}
