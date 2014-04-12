/**
 * 
 */
package ch.droptilllate.keyfile.error;

/**
 * @author Roewn
 *
 */
public enum KeyFileError
{

	NONE("No error"),
	UNKNOWN("Unkown exceptions"),
	INVALID_KEY("Invalid key"),
	
	IO_EXCEPTION("IO Exception"), 
	
	FILE_NOT_FOUND("keyfile not found"),
	
	FILE_INVALID_PATH("Path argument for the keyfile is invalid"),
	
	EMPTY_KEYRELATION("Key relation is empty, no share relation found in key relation");
	
	private String error;
	private String message;

	private KeyFileError(String error) {
		this.error = error;
	}
	
	public String getError(){
		return this.error;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	
	public String toString(){
		return error +": "+ message;
	}
	
}
