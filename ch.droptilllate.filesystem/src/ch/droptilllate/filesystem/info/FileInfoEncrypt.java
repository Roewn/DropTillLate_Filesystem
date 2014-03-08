/**
 * 
 */
package ch.droptilllate.filesystem.info;

import ch.droptilllate.filesystem.preferences.Options;


/**
 * @author Roewn
 *
 */
public class FileInfoEncrypt extends FileInfo
{
	private String fullPlainFilePath;
	
	/**
	 * Constructor for encrypting a new plain file (Create).
	 * @param fileID Unique id of the File
	 * @param fullPlainFilePath This is the path to the plain file including filename and extension (Example C:\\somewhere\\test.txt)
	 * @param destShareRelationID share relation which holds the container.
	 */
	public FileInfoEncrypt (int fileID, String fullPlainFilePath, int destShareRelationID) {
		// Set the container ID to 0 so a new one gets assigned;
		super(fileID, new ContainerInfo(0, destShareRelationID));
		this.fullPlainFilePath = fullPlainFilePath;
	}
	
	/**
	 * Constructor for encrypting an existing File in the temp dir (Update), the value of Options.getTempPath() is the root directory.
	 * @param fileID Unique id of the File
	 * @param destShareRelationId share relation which holds the container.
	 * @param int destContainerId Id of the container in which the file has to get updated
	 */
	public FileInfoEncrypt (int fileID, int destShareRelationId, int destContainerId, String fileExtension) {
		// Set the container ID;
		super(fileID, new ContainerInfo(destContainerId, destShareRelationId));
		this.fullPlainFilePath = InfoHelper.createFullPath(Options.getInstance().getTempPath(), Integer.toString(fileID), fileExtension);
	}

	/**
	 * @return the fullPlainFilePath
	 */
	public synchronized String getFullPlainFilePath() {
		return fullPlainFilePath;
	}

	/**
	 * @param fullPlainFilePath the fullPlainFilePath to set
	 */
	public synchronized void setFullPlainFilePath(String fullPlainFilePath) {
		this.fullPlainFilePath = fullPlainFilePath;
	}	

}
