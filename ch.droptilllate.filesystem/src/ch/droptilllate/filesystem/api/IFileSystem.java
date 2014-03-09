/**
 * 
 */
package ch.droptilllate.filesystem.api;

import java.util.HashMap;
import java.util.List;

import ch.droptilllate.filesystem.info.FileInfo;
import ch.droptilllate.filesystem.info.FileInfoDecrypt;
import ch.droptilllate.filesystem.info.FileInfoEncrypt;
import ch.droptilllate.filesystem.info.FileInfoMove;
import ch.droptilllate.filesystem.security.KeyRelation;

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
	 * @param keyRelation Map of all shareRelations of the passed files and the related encryption key
	 * @return summary with list of successful and unsuccessful FileInfos
	 */
	FileHandlingSummary encryptFiles(List<FileInfoEncrypt> fileInfoList, KeyRelation keyRelation);

	/**
	 * Loads and decrypts the Files of the list
	 * 
	 * @param fileList list of FileInfos to load to the temporary directory
	 * @param keyRelation Map of all shareRelations of the passed files and the related encryption, decryption key
	 * @return summary with list of successful and unsuccessful FileInfos
	 */
	FileHandlingSummary decryptFiles(List<FileInfoDecrypt> fileInfoList, KeyRelation keyRelation);

	/**
	 * Removes the Files of the list from their containers
	 * 
	 * @param fileList list of FileInfos to remove (note: Container ID is mandatory)
	 * @param keyRelation Map of all shareRelations of the passed files and the related key
	 * @return summary with list of successful and unsuccessful FileInfos
	 */
	FileHandlingSummary deleteFiles(List<FileInfo> fileInfoList, KeyRelation keyRelation);

	/**
	 * Moves the Files of the list from the source Container to the Target Container.
	 * 
	 * @param fileList list of FileInfos to remove (note: source Container ID and fileSize is mandatory)
	 * @param keyRelation Map of all shareRelations of the passed files and the related move keys
	 * @return summary with list of successful and unsuccessful FileInfos
	 */
	FileHandlingSummary moveFiles(List<FileInfoMove> fileInfoList, KeyRelation keyRelation);
	
	/**
	 * Lists all encrypted files per passed directories (contained in the key relation) and returns a list of their FileInfos.
	 * 
	 * @param keyRelation Map of all shareRelations of the passed files and the related key
	 * @return Map off all encrypted files contained per directory (where the share relation is the key)
	 */
	HashMap<Integer, List<FileInfo>> getFilesPerRelation(KeyRelation keyRelation);
	
	/**
	 * Stores and encrypted the passed xml file in the share relation and encrypts it with the passed key.
	 * The Container ID has to be passed in the FileInfo.
	 * @param fileInfo File Info for the xml file to encrypt
	 * @param key Key of the shareRelation where the xml gets encrypted
	 * @return FileInfoEncrypt and the resulting error, if no error occurred than fileInfo.getError() == FileError.NONE
	 */
	FileInfoEncrypt storeFileStructure(FileInfoEncrypt fileInfo, String key);
	
	/**
	 * Loads and decrypted the passed xml file from the passed container, using the passed key.
	 * @param fileInfo File Info for the xml file to decrypt
	 * @param key Key of the shareRelation where the xml is encrypted
	 * @return FileInfoDecrypt and the resulting error, if no error occurred than fileInfo.getError() == FileError.NONE
	 */
	FileInfoDecrypt loadFileStructure(FileInfoDecrypt fileInfo, String key);

}
