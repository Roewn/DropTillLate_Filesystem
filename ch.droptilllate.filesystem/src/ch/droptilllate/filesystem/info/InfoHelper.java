package ch.droptilllate.filesystem.info;

import ch.droptilllate.filesystem.commons.Constants;
import ch.droptilllate.filesystem.commons.OsUtils;

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
	 * @param path Parent path of the file
	 * @param fileName Name of the file
	 * @param fileExtension Extension of the file
	 * @return Full file path (Example: "C:\\Temp\\3425.txt")
	 */
	public static String createFullPath(String path, String fileName, String fileExtension) {
		return path +dirLimiter+ fileName + Constants.EXT_LIMITER + fileExtension;
	}
	
	/**
	 * Removes the limiters at the end of a path string
	 * @param path path of a directory
	 * @return path to the directory without endings limiters
	 */
	public static String checkPath(String path) {
		if ((path.lastIndexOf(dirLimiter) + 1) == path.length()) {
			return path.substring(0, path.lastIndexOf(dirLimiter));
		} else {
			return path;
		}
	}
	
	/**
	 * Removes the dot from the passed file extension
	 * @param ext extension of the file (Example .txt)
	 * @return Extension without the leading dot (Example: txt)
	 */
	public static String checkFileExt(String ext) {
		if (ext.contains(Constants.EXT_LIMITER)) {
			return ext.substring(ext.lastIndexOf(Constants.EXT_LIMITER)+1, ext.length());
		} else {
			return ext;
		}
	}
	
	/**
	 * @return the dirLimiter
	 */
	public static String getDirLimiter() {
		return dirLimiter;
	}

	/**
	 * @return the offset
	 */
	public static int getOffset() {
		return offset;
	}


	
	
	
	

}
