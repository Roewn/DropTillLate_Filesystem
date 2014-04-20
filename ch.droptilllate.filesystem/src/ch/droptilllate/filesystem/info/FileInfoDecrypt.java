/**
 * 
 */
package ch.droptilllate.filesystem.info;

import java.io.File;

import ch.droptilllate.filesystem.commons.OsHelper;
import ch.droptilllate.filesystem.preferences.Constants;
import ch.droptilllate.filesystem.preferences.Options;


/**
 * @author Roewn
 *
 */
public class FileInfoDecrypt extends FileInfo
{	
	
	private String fileExtension;
	private String absExtractPath;
	
	/**
	 * Constructor for decrypting an encrypted file from the passed container to the temp directory.
	 * @param fileID Unique id of the File
	 * @param fileExtension The file extension of the plain file (this will be added to the file id when decrypted to the temp directory)
	 * @param shareRelationID share relation which holds the container.
	 * @param containerID Id of the container which contains the file. 
	 */
	public FileInfoDecrypt(int fileID, String fileExtension, int shareRelationID, int containerID) {
		super(fileID, new ContainerInfo(containerID, shareRelationID));
		setFileExtension(fileExtension);
	}
	
	/**
	 * Constructor for decrypting an encrypted file from the passed container to the temp directory.
	 * @param fileID Unique id of the File
	 * @param shareRelationID share relation which holds the container.
	 * @param containerID Id of the container which contains the file. 
	 * @param absExtractPath The full path of the file after the decryption (place where it gets decrypted), Example: C:\\Extract\\3425.txt
	 */
	public FileInfoDecrypt(int fileID, int shareRelationID, int containerID, String  absExtractPath) {
		super(fileID, new ContainerInfo(containerID, shareRelationID));
		this.absExtractPath = absExtractPath;
	}	

	/**
	 * @return the tempDirPath
	 */
	public synchronized String getTempDirPath() {
		return Options.getInstance().getTempPath();
	}

	/**
	 * @return the fileExtension
	 */
	public synchronized String getFileExtension() {
		return fileExtension;
	}

	/**
	 * @param fileExtension the fileExtension to set
	 */
	public synchronized void setFileExtension(String fileExtension) {
		this.fileExtension = OsHelper.checkFileExt(fileExtension);
	}	
	
	/**
	 * Example: "C:\\Temp\\3425.txt"
	 * @return fullFilePath -> Location of the decrypted File (temp dir path + file Id + File extension)
	 */
	public synchronized String getFullTmpFilePath() {
		return OsHelper.createFullPath(getTempDirPath(), Integer.toString(super.getFileID()), this.fileExtension);
	}
	
	/**
	 * Returns the filename including the extension (without path)
	 * Example: "3425.txt"
	 * @return
	 */
	public synchronized String getPlainFileName() {
		if (isToExtract()) return new File(absExtractPath).getName();
		return super.getFileID() + Constants.EXT_LIMITER + this.fileExtension;
	}	
	
	
	/**
	 * Returns the target filename including the path after if a file gets extracted (not the same as the temp directory path)
	 * @return The full path of the file after the decryption (place where it gets decrypted), Example: C:\\Extract\\3425.txt
	 */
	public String getAbsExtractPath()
	{
		return absExtractPath;
	}

	/**
	 * Sets the target filename including the path after if a file gets extracted (not the same as the temp directory path)
	 * @param absExtractPath The full path of the file after the decryption (place where it gets decrypted), Example: C:\\Extract\\3425.txt
	 */
	public void setAbsExtractPath(String absExtractPath)
	{
		this.absExtractPath = absExtractPath;
	}

	/**
	 * Returns if the file gets extracted to a given directory (not into the temp directory)
	 * @return true if the file gets extracted
	 */
	public synchronized boolean isToExtract() {
		if (absExtractPath == null || absExtractPath.length() < 1) return false;
		return true;
	}

}
