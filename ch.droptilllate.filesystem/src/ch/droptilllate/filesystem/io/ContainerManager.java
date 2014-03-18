package ch.droptilllate.filesystem.io;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import ch.droptilllate.filesystem.error.FileError;
import ch.droptilllate.filesystem.error.FileException;
import ch.droptilllate.filesystem.info.ContainerInfo;
import ch.droptilllate.filesystem.info.FileInfo;
import ch.droptilllate.filesystem.info.FileInfoEncrypt;
import ch.droptilllate.filesystem.info.InfoHelper;
import ch.droptilllate.filesystem.preferences.Constants;
import ch.droptilllate.filesystem.preferences.Options;

public class ContainerManager
{
	private IShareRelation iShareRelation = new ShareRelationHandler();

	private static ContainerManager instance = null;
	private long maxContainerSize = Constants.MAX_CONT_SIZE;

	protected ContainerManager()
	{
		// Exists only to defeat instantiation.
	}

	public static ContainerManager getInstance()
	{
		if (instance == null)
		{
			instance = new ContainerManager();
		}
		return instance;
	}

	/**
	 * Takes the passed list of FileInfos and assigns them to suitable containers regarding their size. If necessary, new containers will be
	 * assigned.
	 * 
	 * @param fileInfoList The list of all FileInfos for which the Containers shall be determined, note: the id will be written directly
	 *            into the list passed as reference
	 */
	public void assignContainerID(List<? extends FileInfo> fileInfoList)
	{
		ContainerInfo actFileContInfo;

		// generate the ContainerInfo entries for every ShareRelation
		HashMap<Integer, TreeSet<ContainerInfo>> shareRelationsMap = getContainersPerShareRelation(fileInfoList);

		// Sort the fileInfos from big to small size
		// The set of ContainerInfos is order from small to big size
		Collections.sort(fileInfoList, Collections.reverseOrder());

		// TODO keep in mind that if a file which has already an container can
		// be to big and has to be moved to another Container

		// Update the ContainerInfo for every fileInfo which has no entry
		for (FileInfo fileInfo : fileInfoList)
		{
			if (fileInfo.getError() == FileError.NONE)
			{
				actFileContInfo = fileInfo.getContainerInfo();
				// get the Mapkey for the current share relation
				int shareRelationKey = actFileContInfo.getShareRelationID();
				// while no ContainerID for this file exists do ...
				if (actFileContInfo.getContainerID() <= 0)
				{
					// Store the size of the current File
					long actFileSize = fileInfo.getSize();
					// get the sorted set of all Containers in this ShareRelation
					TreeSet<ContainerInfo> contInfoSet = shareRelationsMap.get(shareRelationKey);

					// DEBUG
					// System.out.println("TreeSet\n"+ contInfoSet);

					// check if there is at least one ContainerInfo in the set
					if (!contInfoSet.isEmpty())
					{
						// get the smallest container from this ShareRelation
						ContainerInfo smallestContInfoInRelation = contInfoSet.first();
						// check if there is enough free space in the Container and ..
						// its not the reserved container id for storing the filesystem structure
						if ((maxContainerSize - smallestContInfoInRelation.getEstimatedContainerSize()) > actFileSize)
						{
							// first remove the ContInfo from the set (needed for update the SET)
							contInfoSet.remove(smallestContInfoInRelation);
							// Update the Size of the assign container
							smallestContInfoInRelation.setEstimatedContainerSize(smallestContInfoInRelation.getEstimatedContainerSize()
									+ actFileSize);
							// update the set with the new ContainerInfo so the ordering by size is still OK
							contInfoSet.add(smallestContInfoInRelation);
							// assign this ContainerInfo to the actual ContainerInfo
							actFileContInfo = smallestContInfoInRelation;
							// DEBUG
							// system.out.println("TreeSet\n"+ contInfoSet);
						}
					}
					// When the smallest Container han't1 enough space, assign a new one
					if (actFileContInfo.getContainerID() <= 0)
					{
						// Pass the relation ContainerInfos to generate a new ContainerInfo with a random number.
						actFileContInfo = generateNewContainerID(contInfoSet, shareRelationKey);
						// Add the new ContInfo to the Map and to the file Info and set the estimated size
						actFileContInfo.setEstimatedContainerSize(actFileSize);
						contInfoSet.add(actFileContInfo);
					}
					// Set the updated ContainerInfo for the File
					fileInfo.setContainerInfo(actFileContInfo);
				} else
				{
					// TODO also implement a update of the container Size when the container is already known, dont forget to count the size
					// of
					// the existing file in the container
					// Add a new asser statement in testExistingContainerID()

				}
			}
		}
		System.out.println(Constants.CONSOLE_LIMITER);
		System.out.println("Containers for the files assigned: assignContainerID()");

	}

	/**
	 * Lists all ContainerInfo Objects per ShareRelations in a HashMap where the path is the key and the value is a sorted TreeSet (by
	 * Container-Size).
	 * 
	 * @param fileInfoList The list of all FileInfos for which the Containers shall be determined
	 * @return Sorted Set of ContainerInfos in a Map where the ShareRelation is the key
	 */
	private HashMap<Integer, TreeSet<ContainerInfo>> getContainersPerShareRelation(List<? extends FileInfo> fileInfoList)
	{
		HashMap<Integer, TreeSet<ContainerInfo>> shareRelationsMap = new HashMap<Integer, TreeSet<ContainerInfo>>();
		ContainerInfo actFileContInfo;

		System.out.println(Constants.CONSOLE_LIMITER);
		System.out.println("Fetching existing Containers per share relations ...");

		for (FileInfo fileInfo : fileInfoList)
		{

			try
			{
				// Get file size if the info does not contain it
				checkFileSize(fileInfo);

				// get the containerInfo
				actFileContInfo = fileInfo.getContainerInfo();
				// create a new entry for every share relation if it's nor already there
				if (!shareRelationsMap.containsKey(actFileContInfo.getShareRelationID()))
				{
					// TODO Maybe check if the parent folder of droptilllate is correct
					shareRelationsMap.put(actFileContInfo.getShareRelationID(), new TreeSet<ContainerInfo>());

					String shareRelationPath = Options.getInstance().getDroptilllatePath() + InfoHelper.getDirLimiter()
							+ actFileContInfo.getShareRelationID();
					// If folder already exists, get all containers and update the Map with these infos
					if (iShareRelation.checkIfDirectoryExists(shareRelationPath))
					{
						// get all containers in this share relation
						List<File> containerList = iShareRelation.getContainersOfShareRelation(actFileContInfo.getShareRelationID());
						// Check if there is at least a container in the share relation
						if (containerList != null && containerList.size() > 0)
						{
							TreeSet<ContainerInfo> contInfoSet = new TreeSet<ContainerInfo>();
							// get all FileInfos for the containers in these path
							for (File file : containerList)
							{
								// get the full path of the container file
								String filePath = file.getAbsolutePath();
								// check if the filePath points to the container which contains the filesystem structure ...
								// ... this container shall not be added to the set
								if (InfoHelper.extractContainerID(filePath) != Constants.STRUCT_CONT_ID)
								{
									ContainerInfo contInfo = new ContainerInfo(file.getAbsolutePath());
									// get size of every container
									contInfo.setEstimatedContainerSize(file.length());
									// add the ContainerInfo to the Set
									contInfoSet.add(contInfo);
								}
							}
							// Update the map with the all containers per share relation
							shareRelationsMap.put(actFileContInfo.getShareRelationID(), contInfoSet);
						}
					}
				}
			} catch (FileException e)
			{
				System.err.println(e.getError());
				System.out.println();
				fileInfo.setError(e.getError());
			} catch (Exception e)
			{
				System.err.println(e.getMessage());
				System.out.println();
				fileInfo.setError(FileError.UNKNOWN, e.getMessage());
			}
		}

		System.out.println("Existing Containers per share relations verified: ");
		System.out.println(shareRelationsMap);

		return shareRelationsMap;
	}

	/**
	 * Generate a new random ContainerId for the passed ShareRelation.
	 * 
	 * @param contInfoSet Set of the existing ContainerInfo for this ShareRelation
	 * @param shareRelationPath this is the path of the current ShareRelation (root directory of the ContainerFiles)
	 * @return ContainerInfo with the generated ID
	 */
	private ContainerInfo generateNewContainerID(Set<ContainerInfo> contInfoSet, int shareRelationID)
	{
		Set<Integer> contIdSet = new LinkedHashSet<Integer>();
		Integer newID = 0;

		// fill all existing containerID of the share relation in a separate set
		for (ContainerInfo contInfo : contInfoSet)
		{
			contIdSet.add(contInfo.getContainerID());
			// if path is empty, get it from containerInfo
			if (shareRelationID == 0)
			{
				shareRelationID = contInfo.getShareRelationID();
			}
		}

		// Remember the size of the set plus 1, because 1 new id shall be generated
		int oldIdCount = contIdSet.size() + 1;

		Random rnd = new Random();
		while (contIdSet.size() < oldIdCount)
		{
			newID = (Constants.STRUCT_CONT_ID + 1) + rnd.nextInt(Constants.MAX_RND);
			// As we're adding to a set, this will automatically do a containment check
			contIdSet.add(newID);
		}

		// TODO Error handling if no id can be generated

		// returns the new ContainerInfo
		return new ContainerInfo(newID, shareRelationID);
	}

	/**
	 * Checks if the info has a size set, if not, the size of the source file will be added
	 * 
	 * @param fileInfo Info of the file
	 * @throws FileException Throw when file size could not be estimated.
	 */
	private void checkFileSize(FileInfo fileInfo) throws FileException
	{
		if (fileInfo.getSize() <= 0)
		{
			File file = new File(((FileInfoEncrypt) fileInfo).getFullPlainFilePath());
			if (file.exists())
			{
				fileInfo.setSize(file.length());
			} else
			{
				throw new FileException(FileError.SRC_FILE_NOT_FOUND, file.getAbsolutePath());
			}
		}
	}

	/**
	 * @return the maxContainerSize
	 */
	public long getMaxContainerSize()
	{
		return maxContainerSize;
	}

	/**
	 * @param maxContainerSize the maxContainerSize to set
	 */
	public void setMaxContainerSize(long maxContainerSize)
	{
		this.maxContainerSize = maxContainerSize;
	}

}
