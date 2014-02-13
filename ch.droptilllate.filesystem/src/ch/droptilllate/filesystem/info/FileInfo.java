package ch.droptilllate.filesystem.info;

import ch.droptilllate.filesystem.api.FileError;


public class FileInfo implements Comparable<FileInfo>
{

	private int fileID;
	private ContainerInfo containerInfo;

	private long size;
	private long timeStamp;
	private FileError error;

	/**
	 * This constructor should only be used for listing the content of a container.
	 * 
	 * @param fileID Unique id of the File.
	 */
	public FileInfo(int fileID)
	{
		this.fileID = fileID;
		this.error = FileError.NONE;
	}

	/**
	 * This constructor has to be used when deleting a file.
	 * 
	 * @param fileID Unique id of the File.
	 * @param containerInfo Info of the Container which holds the file (For deleting a file also the container id is needed)
	 */
	public FileInfo(int fileID, ContainerInfo containerInfo)
	{
		this(fileID);
		this.containerInfo = containerInfo;		
	}

	/**
	 * @return the fileID -> Unique id of the File
	 */
	public int getFileID() {
		return fileID;
	}

	/**
	 * @param fileID the unique id of the File to set
	 */
	public void setFileID(int fileID) {
		this.fileID = fileID;
	}

	/**
	 * @return the containerInfo -> Encrypt: Target container for file encryption. Decrypt: Source container of encrypted file. Move: New
	 *         target container for file encryption.
	 */
	public ContainerInfo getContainerInfo() {
		return containerInfo;
	}

	/**
	 * @param containerInfo the containerInfo to set Encrypt: Target container for file encryption. Decrypt: Source container of encrypted
	 *            file. Move: New target container for file encryption.
	 */
	public void setContainerInfo(ContainerInfo containerInfo) {
		this.containerInfo = containerInfo;
	}


	/**
	 * @return the timeStamp
	 */
	public long getTimeStamp() {
		return timeStamp;
	}

	/**
	 * @param timeStamp the timeStamp to set
	 */
	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}

	/**
	 * @return the error
	 */
	public FileError getError() {
		return error;
	}
	
	public String getErrorMessage() {
		return error.getMessage();
	}

	/**
	 * @param error the error to set
	 */
	public void setError(FileError error) {
		this.error = error;
	}
	
	/**
	 * @param error the error to set
	 */
	public void setError(FileError error, String msg) {
		this.error = error;
		error.setMessage(msg);
	}

	/**
	 * @return the size in bytes
	 */
	public long getSize() {
		return size;
	}

	/**
	 * @param size the size to set in bytes
	 */
	public void setSize(long size) {
		this.size = size;
	}

	@Override
	public int compareTo(FileInfo o) {
		if (this.size > o.size)
		{
			return 1;
		}
		if (this.size < o.size)
		{
			return -1;
		}
		// if size is the same, check if it is also to same file
		if (this.fileID > o.fileID)
		{
			return 1;
		}
		if (this.fileID < o.fileID)
		{
			return -1;
		}
		return 0;
	}


}
