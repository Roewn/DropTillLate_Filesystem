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
	UNKNOWN("Unkown exceptions");
	
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
