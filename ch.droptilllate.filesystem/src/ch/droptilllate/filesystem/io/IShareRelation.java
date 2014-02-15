package ch.droptilllate.filesystem.io;

import java.io.File;
import java.util.List;

import ch.droptilllate.filesystem.info.FileInfo;

public interface IShareRelation
{
	// TODO Throw proper FileExceptions

	/**
	 * Checks if the directory of the passed path exists, if not, it gets created.
	 * 
	 * @param path path of the directory
	 * @return true if the directory already existed, false if a new directory was created
	 */
	public abstract boolean checkIfDirectoryExists(String path);

	/**
	 * Lists all encrypted files in the passed directory and returns a list of the their FileInfos.
	 * 
	 * @param path path of the directory which contains the files
	 * @param key related key for the passed file
	 * @return List off all encrypted files contained by this directory
	 */
	public abstract List<FileInfo> getFilesOfShareRelation(String path, String key);

	/**
	 * Lists all contaierns in the passed directory and returns a list of files.
	 * @param path path of the directory which contains the containers
	 * @return List off all containers contained by this directory
	 */
	public abstract List<File> getContainersOfShareRelation(String path);

}