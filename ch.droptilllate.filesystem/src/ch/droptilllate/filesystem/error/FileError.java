/**
 * 
 */
package ch.droptilllate.filesystem.error;

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
	
	FILENAME_NOT_PARSABLE("Can not parse the filename"),
	
	// share realation
	SHARE_NOT_FOUND("Share relation not found in key relation"),
	SHARE_ID_NOT_PARSABLE("Could not parse shareRelation id from string"),
	SHARE_PATH_SPLIT_ERROR("Could not split the passed path string to extract the share relation"),
	
	// container
	CONT_ID_NOT_PARSABLE("Could not parse container id from string"),
	CONT_WRONG_PATH("Invalid container path"),
	CONT_WRONG_ID("ContainerInfo contains a wrong container id"),
	CONT_NO_CONTENT("Could not list the content of the container"),
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
