/**
 * 
 */
package ch.droptilllate.filesystem.truezip;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ch.droptilllate.filesystem.api.FileError;
import ch.droptilllate.filesystem.commons.Constants;
import ch.droptilllate.filesystem.info.ContainerInfo;
import ch.droptilllate.filesystem.info.FileInfo;
import ch.droptilllate.filesystem.io.FileException;
import ch.droptilllate.filesystem.io.IContainer;
import de.schlichtherle.truezip.file.TConfig;
import de.schlichtherle.truezip.file.TFile;

/**
 * @author Roewn
 * 
 */
public class ContainerHandler implements IContainer
{

	/**
	 * Removes all empty containers from the filesystem.
	 * 
	 * @param fileInfoList List of the deleted Fileinfos which contain the containers to check
	 */
	public synchronized void removeEmptyContainers(List<FileInfo> fileInfoList)
	{
		Set<ContainerInfo> contSet = new HashSet<ContainerInfo>();
		// get all containerInfos to check
		for (FileInfo fi : fileInfoList)
		{
			if (fi.getError() == FileError.NONE)
			{
				contSet.add(fi.getContainerInfo());
			}
		}
		// remove empty containers
		for (ContainerInfo contInfo : contSet)
		{
			try
			{
				checkForEmptyContainer(contInfo);
			} catch (FileException e)
			{
				System.err.println("Could not delete empty container: " + contInfo);
			}
		}

	}

	/**
	 * Lists all files in the passed container and returns a list of contained FileInfos.
	 * 
	 * @param containerInfo Info of the container to check
	 * @param key related key for the passed file
	 * @return List off all files contained by this container
	 */
	@Override
	public synchronized List<FileInfo> listContainerContent(ContainerInfo containerInfo, String key) throws FileException
	{
		try (TConfig config = TConfig.push())
		{
			return listContainerContent(containerInfo);
		}
	}

	/**
	 * Lists all files in the passed container and returns a list of contained FileInfos.
	 * 
	 * @param containerInfo Info of the container to check
	 * @return List off all files contained by this container
	 */
	@Override
	public List<FileInfo> listContainerContent(ContainerInfo containerInfo) throws FileException
	{
			ArrayList<FileInfo> fileInfoList = new ArrayList<FileInfo>();
			TFile containerFile = new TFile(containerInfo.getContainerPath());
			TFile[] fileList = containerFile.listFiles();

			if (fileList != null)
			{
				for (TFile file : fileList)
				{
					try
					{
						fileInfoList.add(new FileInfo(Integer.parseInt(file.getName()), containerInfo));
					} catch (NumberFormatException e)
					{
						System.err.println(e.getMessage());
						throw new FileException(FileError.FILENAME_NOT_PARSABLE, e.getMessage());
					}
				}
			}
			else {
				throw new FileException(FileError.CONT_NO_CONTENT, "ContainerInfo: "+containerInfo.getContainerPath());
			}
			return fileInfoList;
	}

	/**
	 * Checks if the Container is empty, if this is the case it will be removed.
	 * 
	 * @param containerInfo Info of the container to check
	 * @throws FileException Throw when container is corrupt.
	 */
	public synchronized void checkForEmptyContainer(ContainerInfo containerInfo) throws FileException
	{
		checkIfContainerExists(containerInfo, FileError.CONT_NOT_FOUND);

		// if the container is empty ..
		if (listContainerContent(containerInfo).size() <= 0)
		{
			// .. delete the container
			deleteContainer(containerInfo);
			System.out.println(Constants.CONSOLE_LIMITER);
			System.out.println("Empty Container deleted: " + containerInfo.getContainerPath());
		}
	}

	/**
	 * Removes the passed Container and all included files recursively
	 * 
	 * @param containerInfo of the container to delete
	 * @return true if delete was successful
	 * @throws FileException Throw when container is corrupt.
	 */
	public synchronized boolean deleteContainer(ContainerInfo containerInfo) throws FileException
	{
		try
		{
			TFile cont = new TFile(containerInfo.getContainerPath());
			checkIfContainerExists(cont, FileError.CONT_NOT_FOUND);
			TFile.rm_r(cont);
		} catch (IOException e)
		{
			throw new FileException(FileError.IO_EXCEPTION, e.getMessage());
		}
		return true;
	}

	/**
	 * Checks if the Container exists and trows the passed FileError if not.
	 * 
	 * @param file File to check
	 * @param fileError FileError which is thrown by the exception.
	 * @throws FileException Throw when container is corrupt.
	 */
	private synchronized void checkIfContainerExists(File file, FileError fileError) throws FileException
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
	private synchronized void checkIfContainerExists(ContainerInfo containerInfo, FileError fileError) throws FileException
	{
		TFile cont = new TFile(containerInfo.getContainerPath());
		checkIfContainerExists(cont, fileError);
	}

}
