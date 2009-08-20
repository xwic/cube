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

	public String functionName = null;
	public List<String> args = new ArrayList<String>();
	
	public String prefix = null;
	public String suffix = "";
	
}
