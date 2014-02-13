/**
 * 
 */
package ch.droptilllate.filesystem.api;

import java.util.List;

import ch.droptilllate.filesystem.info.FileInfo;
import ch.droptilllate.filesystem.info.FileInfoDecrypt;
import ch.droptilllate.filesystem.info.FileInfoEncrypt;
import ch.droptilllate.filesystem.info.FileInfoMove;

/**
 * @author Rene Amrhein
 * 
 */
public interface IFileSystem
{

	/**
	 * Adds the the files from the list to an encrypted container
	 * 
	 * @param fileList list of FileInfos to encrypt
	 * @return summary with list of successful and unsuccessful FileInfos
	 */
	FileHandlingSummary encryptFiles(List<FileInfoEncrypt> fileInfoList);

	/**
	 * Loads and decrypts the Files of the list
	 * 
	 * @param fileList list of FileInfos to load to the temporary directory
	 * @return summary with list of successful and unsuccessful FileInfos
	 */
	FileHandlingSummary decryptFiles(List<FileInfoDecrypt> fileInfoList);

	/**
	 * Removes the Files of the list from their containers
	 * 
	 * @param fileList list of FileInfos to remove (note: Container ID is mandatory)
	 * @return summary with list of successful and unsuccessful FileInfos
	 */
	FileHandlingSummary deleteFiles(List<FileInfo> fileInfoList);

	/**
	 * Moves the Files of the list from the source Container to the Target Container.
	 * 
	 * @param fileList list of FileInfos to remove (note: source Container ID and fileSize is mandatory)
	 * @return summary with list of successful and unsuccessful FileInfos
	 */
	FileHandlingSummary moveFiles(List<FileInfoMove> fileInfoList);

}
