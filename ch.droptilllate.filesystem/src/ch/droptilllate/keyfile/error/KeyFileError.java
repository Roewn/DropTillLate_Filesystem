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
	FILE_INVALID_PATH("Keyfile path argument for the keyfile is invalid"),
	FILE_WRITE_EXCEPTION("An error occurred while writing into the keyfile"),
	FILE_READ_EXCEPTION("An error occurred while reading from the keyfile"),
	
	LINE_SPLIT_ERROR("Could not split the read keyfile line to extract the shareRelationID and the related key "),
	LINE_FETCH_ERROR("Read line from keyfile seems to be corrupt, wrong format or entries"),
	LINE_WRITE_ERROR("ShareRelation or related key can not be written to the keyfile, wrong format or entries"),
	SHARE_ID_NOT_PARSABLE("ShareRelationId could not be pared to integer"),
	
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
