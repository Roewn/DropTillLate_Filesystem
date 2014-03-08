/**
 * 
 */
package ch.droptilllate.filesystem.error;


/**
 * @author Roewn
 * 
 */
@SuppressWarnings("serial")
public class FileException extends Exception
{
	private FileError error;
	
	public FileException(String msg)
	{
		super(msg);
	}

	public FileException(FileError error, String msg)
	{
		super(msg);
		this.error = error;
		this.error.setMessage(msg);
	}

	public FileException(String path, String msg)
	{
		super(msg + ": " + path);
	}

	public String getMessage() {
		return super.getMessage();
	}

	/**
	 * @return the error enumerator (FileError)
	 */
	public FileError getError() {
		return error;
	}

	/**
	 * @param error the error enumerator to set (FileError)
	 */
	public void setError(FileError error) {
		this.error = error;
	}
	

}
