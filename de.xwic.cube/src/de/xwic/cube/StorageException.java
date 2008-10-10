/**
 * 
 */
package de.xwic.cube;

/**
 * @author Florian Lippisch
 */
public class StorageException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1064934781148995852L;

	/**
	 * 
	 */
	public StorageException() {
		super();
	}

	/**
	 * @param message
	 * @param cause
	 */
	public StorageException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public StorageException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public StorageException(Throwable cause) {
		super(cause);
	}

	
	
}
