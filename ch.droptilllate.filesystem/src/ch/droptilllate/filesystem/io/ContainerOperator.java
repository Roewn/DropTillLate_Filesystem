/**
 * 
 */
package ch.droptilllate.filesystem.io;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ch.droptilllate.filesystem.api.FileError;
import ch.droptilllate.filesystem.commons.Constants;
import ch.droptilllate.filesystem.info.ContainerInfo;
import ch.droptilllate.filesystem.info.FileInfo;
import de.schlichtherle.truezip.file.TFile;

/**
 * @author Roewn
 * 
 */
public class ContainerOperator
{
	/**
	 * Checks if the Container is empty, if this is the case it will be removed.
	 * @param containerInfo Info of the container to check
	 * @throws FileException Throw when container is corrupt.
	 */
	public synchronized static void checkForEmptyContainer(ContainerInfo containerInfo) throws FileException
	{
		checkIfContainerExists(containerInfo, FileError.CONT_NOT_FOUND);
		// if the container is empty ..
		if (listContainerContent(containerInfo).size() <= 0)
		{
			// .. delete the container
			deleteContainer(containerInfo);
			System.out.println(Constants.CONSOLE_LIMITER);
			System.out.println("Empty Container deleted: "+ containerInfo.getFullContainerPath());
		}
	}

	/**
	 * Removes the passed Container and all included files recursively
	 * 
	 * @param containerInfo of the container to delete
	 * @return true if delete was successful
	 */
	public synchronized static boolean deleteContainer(ContainerInfo containerInfo) throws FileException
	{
		try
		{
			TFile cont = new TFile(containerInfo.getFullContainerPath());
			checkIfContainerExists(cont, FileError.CONT_NOT_FOUND);
			TFile.rm_r(cont);
		} catch (IOException e)
		{
			throw new FileException(FileError.IO_EXCEPTION, e.getMessage());
		}
		return true;
	}

	/**
	 * Lists all files in the passed container and returns a list of contained FileInfos.
	 * 
	 * @param containerInfo Info of the container to check
	 * @return List off all files contained by this container
	 */
	public synchronized static List<FileInfo> listContainerContent(ContainerInfo containerInfo)
	{
		TFile containerFile = new TFile(containerInfo.getFullContainerPath());
		TFile[] fileList = containerFile.listFiles();
		ArrayList<FileInfo> fileInfoList = new ArrayList<FileInfo>();
		try
		{
			for (TFile file : fileList)
			{
				// TODO add error handling
				fileInfoList.add(new FileInfo(Integer.parseInt(file.getName())));
			}
		} catch (Exception e)
		{
			return null;
		}
		return fileInfoList;
	}

	/**
	 * Checks if the Container exists and trows the passed FileError if not.
	 * 
	 * @param file File to check
	 * @param fileError FileError which is thrown by the exception.
	 * @throws FileException Throw when container is corrupt.
	 */
	private synchronized static void checkIfContainerExists(File file, FileError fileError) throws FileException
	{
		if (!file.exists())
		{
			throw new FileException(fileError, file.getAbsolutePath());
		}
	}

	/**
	 * Checks if the Container exists and trows the passed FileError if not.
	 * 
	 * @param ContainerInfo Info of the Container to check
	 * @param fileError FileError which is thrown by the exception.
	 * @throws FileException Throw when container is corrupt.
	 */
	private synchronized static void checkIfContainerExists(ContainerInfo containerInfo, FileError fileError) throws FileException
	{
		TFile cont = new TFile(containerInfo.getFullContainerPath());
		checkIfContainerExists(cont, fileError);
	}
}
