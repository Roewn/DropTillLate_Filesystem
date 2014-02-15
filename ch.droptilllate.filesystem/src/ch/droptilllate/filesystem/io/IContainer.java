package ch.droptilllate.filesystem.io;

import java.util.List;

import ch.droptilllate.filesystem.info.ContainerInfo;
import ch.droptilllate.filesystem.info.FileInfo;

public interface IContainer
{

	/**
	 * Removes all empty containers from the filesystem.
	 * 
	 * @param fileInfoList List of the deleted Fileinfos which contain the containers to check
	 */
	void removeEmptyContainers(List<FileInfo> fileInfoList);
	
	/**
	 * Lists all files in the passed container and returns a list of contained FileInfos.
	 * 
	 * @param containerInfo Info of the container to check
	 * @param key related key for the passed container
	 * @return List off all files contained by this container
	 */
	List<FileInfo> listContainerContent(ContainerInfo containerInfo, String key) throws FileException;
	
	/**
	 * Lists all files in the passed container and returns a list of contained FileInfos.
	 * 
	 * @param containerInfo Info of the container to check
	 * @return List off all files contained by this container
	 */
	List<FileInfo> listContainerContent(ContainerInfo containerInfo) throws FileException;
	
	/**
	 * Checks if the Container is empty, if this is the case it will be removed.
	 * 
	 * @param containerInfo Info of the container to check
	 * @throws FileException Throw when container is corrupt.
	 */
	void checkForEmptyContainer(ContainerInfo containerInfo) throws FileException;
	
}