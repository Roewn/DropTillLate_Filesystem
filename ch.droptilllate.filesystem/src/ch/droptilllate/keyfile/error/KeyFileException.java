package ch.droptilllate.keyfile.error;


@SuppressWarnings("serial")
public class KeyFileException extends Exception
{
	private KeyFileError error;

	public KeyFileException(String msg)
	{
		super(msg);
	}

	public KeyFileException(KeyFileError error, String msg)
	{
		super(msg);
		this.error = error;
		this.error.setMessage(msg);
	}

	public KeyFileException(String path, String msg)
	{
		super(msg + ": " + path);
	}

	public String getMessage()
	{
		return super.getMessage();
	}

	/**
	 * @return the error enumerator (KeyFileError)
	 */
	public KeyFileError getError()
	{
		return error;
	}

	/**
	 * @param error the error enumerator to set (KeyFileError)
	 */
	public void setError(KeyFileError error)
	{
		this.error = error;
	}

}
