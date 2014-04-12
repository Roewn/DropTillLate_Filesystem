/**
 * 
 */
package ch.droptilllate.filesystem.preferences;

import ch.droptilllate.filesystem.commons.OsHelper;


/**
 * @author Roewn
 *
 */
public class Options
{
	private String DroptilllatePath;
	private String tempPath;
	
	private static Options instance = null;

	protected Options()
	{
		// Exists only to defeat instantiation.
	}

	public static Options getInstance()
	{
		if (instance == null)
		{
			instance = new Options();
		}
		return instance;
	}

	/**
	 * Gets the Droptilllate path which includes the droptilllate folder.
	 * Example for Droptilllate path: "C:\\dropbbox\\droptilllate
	 * @return the DroptilllatePath
	 */
	public String getDroptilllatePath()
	{
		return this.DroptilllatePath;
	}

	/**	
	 * Sets the Droptilllate path which includes the droptilllate folder, the directory limiter at the end of the string are being removed.
	 * Example for Droptilllate path: "C:\\dropbbox\\droptilllate
	 * @param DroptilllatePath the DroptilllatePath to set
	 */
	public void setDroptilllatePath(String DroptilllatePath)
	{
		this.DroptilllatePath = OsHelper.checkPath(DroptilllatePath);
	}

	/**
	 * * Gets the path to the directory for the temporary files.
	 * Example for temp path: "C:\\droptillate\\tempfiles
	 * @return the tempPath
	 */
	public String getTempPath()
	{
		return tempPath;
	}

	/**
	 * Sets the path to the directory for the temporary files, the directory limiter at the end of the string are being removed.
	 * Example for temp path: "C:\\droptillate\\tempfiles
	 * @param tempPath the tempPath to set
	 */
	public void setTempPath(String tempPath)
	{
		this.tempPath = OsHelper.checkPath(tempPath);
	}
	
	

}
