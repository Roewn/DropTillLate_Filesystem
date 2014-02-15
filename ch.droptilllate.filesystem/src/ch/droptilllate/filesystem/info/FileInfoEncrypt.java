/**
 * 
 */
package ch.droptilllate.filesystem.info;


/**
 * @author Roewn
 *
 */
public class FileInfoEncrypt extends FileInfo
{
	private String fullPlainFilePath;
	
	/**
	 * Constructor for encrypting a new plain file.
	 * @param fileID Unique id of the File
	 * @param fullPlainFilePath This is the path to the plain file including filename and extension (Example C:\\temp\\test.txt)
	 * @param destShareRelationPath Path to the directory (directory of the share relation) where the file has to be encrypted in a new container.
	 */
	public FileInfoEncrypt (int fileID, String fullPlainFilePath, String destShareRelationPath) {
		// Set the container ID to 0;
		super(fileID, new ContainerInfo(0, destShareRelationPath));
		this.fullPlainFilePath = fullPlainFilePath;
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
