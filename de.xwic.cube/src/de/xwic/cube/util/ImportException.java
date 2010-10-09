/**
 * 
 */
package de.xwic.cube.util;

/**
 * @author lippisch
 *
 */
public class ImportException extends Exception {

	/**
	 * 
	 */
	public ImportException() {
	}

	/**
	 * @param message
	 */
	public ImportException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public ImportException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public ImportException(String message, Throwable cause) {
		super(message, cause);
	}

}
