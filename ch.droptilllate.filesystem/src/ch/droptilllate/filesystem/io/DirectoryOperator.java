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

/**
 * @author Roewn
 * 
 */
public class DirectoryOperator
{
	/**
	 * Checks if the directory of the passed path exists, if not, it gets created.
	 * 
	 * @param path path of the directory
	 * @return true if the directory already existed, false if a new directory was created
	 */
	public synchronized static boolean checkIfDirectoryExists(String path)
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

	/**
	 * Lists all encrypted files in the passed directory and returns a list of the their FileInfos.
	 * 
	 * @param path path of the directory which contains the files
	 * @return List off all encrypted files contained by this directory
	 */
	public synchronized static List<FileInfo> getAllEncryptedFilesInDir(String path)
	{
		ArrayList<FileInfo> fileInfoList = new ArrayList<FileInfo>();
		// get all containers in this directory
		List<File> containerList= getContainersInDir(path);
		// for every container, add all contained files to the resultList
		for (File cont: containerList) {
			// get list of fileInfos of the current container
			List<FileInfo> currentContContent = ContainerOperator.listContainerContent(new ContainerInfo(cont.getAbsolutePath()));
			fileInfoList.addAll(currentContContent);
		}		
		return fileInfoList;
	}

	/**
	 * 
	 * @param path
	 * @return
	 */
	public synchronized static List<File> getContainersInDir(String path)
	{
		// get all containers in this directory
		File directory = new File(path);
		File[] containerList = directory.listFiles(new FilenameFilter()
		{
			@Override
			public boolean accept(File dir, String name)
			{
				// TODO Check if it is really an archive and maybe put to another class
				return name.endsWith("." + Constants.CONTAINER_EXTENTION);
			}
		});
		return Arrays.asList(containerList);
	}

}
