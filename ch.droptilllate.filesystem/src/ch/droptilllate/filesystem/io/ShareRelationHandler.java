/**
 * 
 */
package ch.droptilllate.filesystem.io;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ch.droptilllate.filesystem.commons.Constants;
import ch.droptilllate.filesystem.info.ContainerInfo;
import ch.droptilllate.filesystem.info.FileInfo;
import ch.droptilllate.filesystem.truezip.ContainerHandler;

/**
 * @author Roewn
 * 
 */
public class ShareRelationHandler implements IShareRelation
{
	private IContainer iContainer = new ContainerHandler();

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.droptilllate.filesystem.io.IShareRelation#checkIfDirectoryExists(java.lang.String)
	 */
	@Override
	public synchronized boolean checkIfDirectoryExists(String path)
	{
		// create the dir file
		File directory = new File(path);
		// Create folder of share relation if it does not exists
		if (!directory.exists())
		{
			directory.mkdirs();
			System.out.println(Constants.CONSOLE_LIMITER);
			System.out.println("Directory created: " + directory.getAbsolutePath());
			return false;
		}
		return true;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.droptilllate.filesystem.io.IShareRelation#getAllEncryptedFilesInDir(java.lang.String)
	 */
	@Override
	public synchronized List<FileInfo> getFilesOfShareRelation(String path, String key)
	{
		System.out.println(Constants.CONSOLE_LIMITER);
		System.out.println("Get files of share relation: " + path);

		ArrayList<FileInfo> fileInfoList = new ArrayList<FileInfo>();
		// get all containers in this directory
		List<File> containerList = getContainersOfShareRelation(path);
		// Check if there is at least a contaienr in the share relation
		if (containerList != null && containerList.size() > 0)
		{
			// for every container, add all contained files to the resultList
			for (File cont : containerList)
			{
				// get list of fileInfos of the current container
				List<FileInfo> currentContContent = null;
				try
				{
					currentContContent = iContainer.listContainerContent(new ContainerInfo(cont.getAbsolutePath()), key);

					if (currentContContent != null)
					{
						fileInfoList.addAll(currentContContent);
					}
				} catch (FileException e)
				{
					System.err.println(e.getError());
				}

			}
		}
		return fileInfoList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.droptilllate.filesystem.io.IShareRelation#getContainersInDir(java.lang.String)
	 */
	@Override
	public synchronized List<File> getContainersOfShareRelation(String path)
	{
		// get all containers in this directory
		File directory = new File(path);
		File[] containerList = directory.listFiles(new FilenameFilter()
		{
			@Override
			public boolean accept(File dir, String name)
			{
				return name.endsWith("." + Constants.CONTAINER_EXTENTION);
			}
		});
		return Arrays.asList(containerList);
	}

}
