package ch.droptilllate.filesystem.api;

import java.util.ArrayList;
import java.util.List;

import ch.droptilllate.filesystem.error.FileError;
import ch.droptilllate.filesystem.info.FileInfo;

/**
 * @author Roewn Maintains a List of all FileInfos with problems during handling.
 */
public class FileHandlingSummary
{
	private List<FileInfo> fileInfoErrorList;
	private List<FileInfo> fileInfoSuccessList;

	/**
	 * Creates a Summary of which files where successful and which had an error.
	 */
	public FileHandlingSummary()
	{
		fileInfoErrorList = new ArrayList<FileInfo>();
		fileInfoSuccessList = new ArrayList<FileInfo>();
		
	}
	
	/**
	 * Creates a Summary of which files where successful and which had an error.
	 * This Constructor assigns the filesinfos to the right list according to their FileError.
	 * @param fileInfoList list of Fileinfos to assign
	 */
	public FileHandlingSummary(List<? extends FileInfo> fileInfoList)
	{
		this();
		assignFileInfos(fileInfoList);		
	}

	/**
	 * @return the fileErrorCount
	 */
	public int getFileErrorCount() {
		return fileInfoErrorList.size();
	}

	/**
	 * Adds a new entry to the FileInfoError List.
	 * 
	 * @param fileInfo FileInfo which throw an Exception during an Operation
	 */
	public void addFileInfoError(FileInfo fileInfo) {
		fileInfoErrorList.add(fileInfo);
	}

	/**
	 * @return the fileInfoErrorList
	 */
	public List<FileInfo> getFileInfoErrorList() {
		return fileInfoErrorList;
	}

	/**
	 * @return the fileSuccessCount
	 */
	public int getFileSuccessCount() {
		return fileInfoSuccessList.size();
	}

	/**
	 * Adds a new entry to the FileInfoError List.
	 * 
	 * @param fileInfo FileInfo which throw an Exception during an Operation
	 */
	public void addFileInfoSuccess(FileInfo fileInfo) {
		fileInfoSuccessList.add(fileInfo);
	}

	/**
	 * @return the fileInfoSuccessList
	 */
	public List<FileInfo> getFileInfoSuccessList() {
		return fileInfoSuccessList;
	}

	/**
	 * @param fileInfoSuccessList the fileInfoSuccessList to set
	 */
	public void setFileInfoSuccessList(List<FileInfo> fileInfoSuccessList) {
		this.fileInfoSuccessList = fileInfoSuccessList;
	}
	
	/**
	 * Assigns the files infos to either the success or error list of the summary
	 * @param fileInfoList
	 */
	private void assignFileInfos(List<? extends FileInfo> fileInfoList) {
		for (FileInfo fi : fileInfoList) {
			if (fi.getError() == FileError.NONE) {
				addFileInfoSuccess(fi);
			} else {
				addFileInfoError(fi);
			}
			
	
		}
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Success List:");
		sb.append(System.getProperty("line.separator"));
		for (FileInfo fi : fileInfoSuccessList){
			sb.append(fi.getFileID()+" -> Created in: "+fi.getContainerInfo());
			sb.append(System.getProperty("line.separator"));
			
		}
		sb.append("Error List:");
		sb.append(System.getProperty("line.separator"));
		for (FileInfo fi : fileInfoErrorList){
			sb.append(fi.getFileID() +" -> Error: "+fi.getError());
			sb.append(System.getProperty("line.separator"));			
		}
		
		return sb.toString();
	}

}
