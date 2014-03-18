package ch.droptilllate.filesystem.info;

import ch.droptilllate.filesystem.commons.OsUtils;
import ch.droptilllate.filesystem.error.FileError;
import ch.droptilllate.filesystem.error.FileException;
import ch.droptilllate.filesystem.preferences.Constants;
import ch.droptilllate.filesystem.preferences.Options;

public class InfoHelper
{
	private static String dirLimiter = Constants.DIR_LIMITER_WIN;
	private static int offset = 1;

	static
	{
		// Set OS specific values
		if (!OsUtils.isWindows())
		{
			dirLimiter = Constants.DIR_LIMITER_MAC;
			offset = 0;
		}
	}

	/**
	 * Concatenates the provides Strings to a full path string using the os specific limiters.
	 * 
	 * @param path Parent path of the file
	 * @param fileName Name of the file
	 * @param fileExtension Extension of the file
	 * @return Full file path (Example: "C:\\Temp\\3425.txt")
	 */
	public synchronized static String createFullPath(String path, String fileName, String fileExtension)
	{
		return path + dirLimiter + fileName + Constants.EXT_LIMITER + fileExtension;
	}
	
	/**
	 * Concatenates the provided integer to the full container path using the os specific limiters and the value of Options.getDropboxPath as root directory.
	 *
	 * @param shareRelationId Id of the share relation containing the container.
	 * @param containerID Id of the container
	 * @return full sharerelation path. Example for full sharerelation path: "C:\\dropbox\\droptillate\\9999999"
	 */
	public synchronized static String createFullSharerelationPath(int shareRelationId, int containerID)
	{
		return Options.getInstance().getDroptilllatePath() + dirLimiter + shareRelationId;
	}
	
	/**
	 * Concatenates the provided integer to the full container path using the os specific limiters and the value of Options.getDropboxPath as root directory.
	 *
	 * @param shareRelationId Id of the share relation containing the container.
	 * @param containerID Id of the container
	 * @return full container path. Example for full container path: "C:\\Temp\\Share1\\342657.tilllate"
	 */
	public synchronized static String createFullContainerPath(int shareRelationId, int containerID)
	{
		return Options.getInstance().getDroptilllatePath() + dirLimiter + shareRelationId + dirLimiter + containerID + Constants.EXT_LIMITER + Constants.CONTAINER_EXTENTION;
	}
	
	

	/**
	 * Removes the limiters at the end of a path string
	 * 
	 * @param path path of a directory
	 * @return path to the directory without endings limiters
	 */
	public synchronized static String checkPath(String path)
	{
		if ((path.lastIndexOf(dirLimiter) + 1) == path.length())
		{
			return path.substring(0, path.lastIndexOf(dirLimiter));
		} else
		{
			return path;
		}
	}

	/**
	 * Removes the dot from the passed file extension
	 * 
	 * @param ext extension of the file (Example .txt)
	 * @return Extension without the leading dot (Example: txt)
	 */
	public synchronized static String checkFileExt(String ext)
	{
		if (ext.contains(Constants.EXT_LIMITER))
		{
			return ext.substring(ext.lastIndexOf(Constants.EXT_LIMITER) + 1, ext.length());
		} else
		{
			return ext;
		}
	}

	/**
	 * Takes the full container path and extracts the containerID. Example for full container path: "C:\\Temp\\Share1\\342657.tilllate"
	 * 
	 * @param path full container path.
	 * @return containerId included in the path string.
	 * @throws FileException if the id could not be fetched.
	 */
	public synchronized static int extractContainerID(String path) throws FileException
	{
		// check for valid path
		checkContainerPath(path);
		// get the container from the path
		String container = path.substring(path.lastIndexOf(getDirLimiter()) + 1, path.lastIndexOf(Constants.EXT_LIMITER));
		// convert to int and return value
		try
		{
			return Integer.parseInt(container);
		} catch (NumberFormatException e)
		{
			throw new FileException(FileError.CONT_ID_NOT_PARSABLE, e.getMessage());
		}
	}

	/**
	 * Takes the full container path and extracts the containerID. Example for full container path: "C:\\Temp\\Share1\\342657.tilllate"
	 * 
	 * @param path full container path.
	 * @return containerId included in the path string.
	 * @throws FileException if the id could not be fetched.
	 */
	public synchronized static int extractShareRelationID(String path) throws FileException
	{
		// check for valid path
		checkContainerPath(path);
		// get the container from the path
		String[] pathElements = null;
		// this is neccessary for the split function to work properly
		String regex = Constants.DIR_LIMITER_WIN + Constants.DIR_LIMITER_WIN;
		if (!OsUtils.isWindows())
		{
			regex = Constants.DIR_LIMITER_MAC;
		}
		try
		{
			pathElements = path.split(regex);
		} catch (Exception e1)
		{
			throw new FileException(FileError.SHARE_PATH_SPLIT_ERROR, e1.getMessage());
		}
		String shareRealation = pathElements[pathElements.length - 2];
		// convert to int and return value
		try
		{
			return Integer.parseInt(shareRealation);
		} catch (NumberFormatException e)
		{
			throw new FileException(FileError.SHARE_ID_NOT_PARSABLE, e.getMessage());
		}
	}

	/**
	 * @return the dirLimiter
	 */
	public synchronized static String getDirLimiter()
	{
		return dirLimiter;
	}

	/**
	 * @return the offset
	 */
	public synchronized static int getOffset()
	{
		return offset;
	}

	/**
	 * Takes the full container path and checks if it is valid, if not, an exception is thrown.
	 * 
	 * @param path full container path, Example for full container path: "C:\\Temp\\Share1\\342657.tilllate".
	 * @throws FileException if path is invalid.
	 */
	private static void checkContainerPath(String path) throws FileException
	{
		if (path != null && path.length() > 0)
		{
			if (path.indexOf(Constants.CONTAINER_EXTENTION) > 0)
			{
				return;
			}
		}
		// if path is wrong throw exception
		throw new FileException(FileError.CONT_WRONG_PATH, path);
	}

}
