package ch.droptilllate.filesystem.io;

import java.util.List;

import ch.droptilllate.filesystem.error.FileException;
import ch.droptilllate.filesystem.info.FileInfo;
import ch.droptilllate.filesystem.info.FileInfoDecrypt;
import ch.droptilllate.filesystem.info.FileInfoEncrypt;
import ch.droptilllate.filesystem.info.FileInfoMove;

public interface IFile
{

	/**
	 * Adds and encrypts the specific File form the plain file path to the container contained in the fileInfo.
	 * 
	 * @param fileInfo Description of the file and the container.
	 * @param key related key for the passed file
	 * @throws FileException Throw when file could not be added.
	 */
	public abstract void encryptFile(FileInfoEncrypt fileInfo, String key) throws FileException;

	/**
	 * Loads and encrypts the specific file from the container to the plain file path contained in the fileInfo.
	 * 
	 * @param fileInfo Description of the file and the container.
	 * @param key related key for the passed file
	 * @return the updated FileInfo with the plain file path and the file extension.
	 * @throws FileException Throw when file could not be loaded.
	 */
	public abstract void decryptFile(FileInfoDecrypt fileInfo, String key) throws FileException;

	/**
	 * Removes the passed file from its container.
	 * 
	 * @param fileInfo Info of the file including container id
	 * @param key related key for the passed file
	 * @throws FileException Throw when file could not be deleted.
	 */
	public abstract void deleteFile(FileInfo fileInfo, String key) throws FileException;

	/**
	 * Moves the file from the src to the dest Container
	 * 
	 * @param fileinfo Info of the file including source and destination container
	 * @param srcKey related key for the passed source file
	 * @param dstKey new key for the destination of the passed file
	 * @throws FileException Throw when file could not be moved.
	 */
	public abstract void moveFile(FileInfoMove fileInfo, String srcKey, String dstKey) throws FileException;
	
	/**
	 * List the Containers of each passed fileinfo and list all the file in those containers to the console
	 * 
	 * @param fileInfos List of all fileInfos
	 * @param key related key for the passed file
	 */
	void listFileAssignment(List<? extends FileInfo> fileInfos, String key);
	
	/**
	 * Checks if the File still exists in this Container
	 * 
	 * @param fileInfo Info of the File to check (including container id)
	 * @return true when file exists
	 */
	boolean checkFile(FileInfo fileInfo, String key);

	/**
	 * Commits all pending changes for all (nested) archive files to their respective parent file system, closes their associated target
	 * archive file in order to allow access by third parties (e.g. other processes), cleans up any temporary allocated resources (e.g.
	 * temporary files) and purges any cached data.
	 */
	public abstract void unmountFileSystem();

}