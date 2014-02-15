/**
 * 
 */
package ch.droptilllate.filesystem.api;

/**
 * @author Roewn
 * 
 */
public enum FileError
{
	NONE("No error"),
	UNKNOWN("Unkown exceptions"), 
	
	IO_EXCEPTION("TrueZIP IO Exception"), 
	
	SRC_FILE_NOT_FOUND("Source file not found"), 
	SRC_FILE_IS_A_DIR("Source file is a directory"), 
	
	DEST_FILE_NOT_FOUND("Destination file not found"),
	EXTRACTED_FILE_NOT_FOUND("Extracted file not found in temp directory"),
	
	INVALID_KEY("Invalid key"),
	SHARERELATION_NOT_FOUND("Share relation not found in key relation"),
	
	CONT_NOT_FOUND("Container not found");
	
	private String error;
	private String message;


	private FileError(String error) {
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
