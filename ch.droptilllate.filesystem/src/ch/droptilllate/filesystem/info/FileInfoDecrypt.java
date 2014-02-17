/**
 * 
 */
package ch.droptilllate.filesystem.info;

import ch.droptilllate.filesystem.commons.Constants;


/**
 * @author Roewn
 *
 */
public class FileInfoDecrypt extends FileInfo
{	
	
	private String tempDirPath;
	private String fileExtension;
	
	/**
	 * Constructor for decrypting an encrypted file from the passed container to the temp directory.
	 * @param fileID Unique id of the File
	 * @param fileExtension The file extension of the plain file (this will be added to the file id when decrypted to the temp directory)
	 * @param tempDirPath Path to the temp directory, where the file gets decrypted 
	 * @param shareRelationPath Path to the directory (directory of the share relation) which holds the container of the encrypted file.
	 * @param containerID Id of the container which contains the file. 
	 */
	public FileInfoDecrypt(int fileID, String fileExtension, String tempDirPath, String shareRelationPath, int containerID) {
		super(fileID, new ContainerInfo(containerID, shareRelationPath));
		setTempDirPath(tempDirPath);
		setFileExtension(fileExtension);
	}

	/**
	 * @return the tempDirPath
	 */
	public synchronized String getTempDirPath() {
		return tempDirPath;
	}

	/**
	 * @param tempDirPath the tempDirPath to set
	 */
	public synchronized void setTempDirPath(String tempDirPath) {
		this.tempDirPath = InfoHelper.checkPath(tempDirPath);
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
		this.fileExtension = InfoHelper.checkFileExt(fileExtension);
	}	
	
	/**
	 * Example: "C:\\Temp\\3425.txt"
	 * @return fullFilePath -> Location of the decrypted File (temp dir path + file Id + File extension)
	 */
	public synchronized String getFullTmpFilePath() {
		return InfoHelper.createFullPath(this.tempDirPath, Integer.toString(super.getFileID()), this.fileExtension);
	}
	
	/**
	 * Returns the filename including the extension (without path)
	 * Example: "3425.txt"
	 * @return
	 */
	public synchronized String getPlainFileName() {
		return super.getFileID() + Constants.EXT_LIMITER + this.fileExtension;
	}

}
