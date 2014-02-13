package ch.droptilllate.filesystem.info;

import ch.droptilllate.filesystem.commons.Constants;

/**
 * Class which holds all informations about a container. 
 * The id of a container can be set by the user or it can be left empty, if the system has to assign a new container.
 * @author Roewn
 * 
 */
public class ContainerInfo implements Comparable<ContainerInfo>
{
	private int containerID;
	private String containerPath;
	private long estimatedContainerSize;

	/**
	 * Creates an ContainerInfo and sets the full path of the Container (Parent container path + containerID + Container extension).
	 * The Container id as well as the parent container path will be set automatically
	 * Example: "C:\\Temp\\Share1\\342657.tilllate"
	 * @param fullContainerPath -> Parent container path + containerID + Container extension.
	 */
	public ContainerInfo(String fullContainerPath)
	{
		setFullContainerPath(fullContainerPath);		
	}

	/**
	 * Creates an ContainerInfo and sets the parent path of the Container, as well as the Container id
	 * If the Container id is unknown and the system has to provide a new number, set the containerID = 0.
	 * ParentContainerPath Example: "C:\\Temp\\Share1\\"
	 * @param containerID Id of the container or 0 if unknown.
	 * @param parentContainerPath Directory which holds the container.
	 */
	public ContainerInfo(int containerID, String parentContainerPath)
	{
		this.containerID = containerID;
		setParentContainerPath(parentContainerPath);		
	}

	/**
	 * @return the containerID
	 */
	public int getContainerID() {
		return containerID;
	}

	/**
	 * @param containerID the containerID to set
	 */
	public void setContainerID(int containerID) {
		this.containerID = containerID;
	}

	/**
	 * ParentContainerPath Example: "C:\\Temp\\Share1\\"
	 * @return the parentContainerPath -> Directory which holds the container.
	 */
	public String getParentContainerPath() {
		return containerPath;
	}

	/**
	 * ParentContainerPath Example: "C:\\Temp\\Share1\\"
	 * @param parentContainerPath the containerPath to set -> Directory which holds the container.
	 */
	public void setParentContainerPath(String parentContainerPath) {	
		this.containerPath = InfoHelper.checkPath(parentContainerPath);
	}

	/**
	 * Example: "C:\\Temp\\Share1\\342657.tilllate"
	 * @param path fullContainerPath -> Parent container path + containerID + Container extension.
	 */
	public void setFullContainerPath(String path) {
		try
		{
			this.containerPath = path.substring(0, path.lastIndexOf(InfoHelper.getDirLimiter()));
			this.containerID = Integer.parseInt((path.substring(path.lastIndexOf(InfoHelper.getDirLimiter()) + InfoHelper.getOffset(),
					path.lastIndexOf(Constants.EXT_LIMITER))));
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}

	}

	/**
	 * Example: "C:\\Temp\\Share1\\342657.tilllate"
	 * @return fullContainerPath -> Parent container path + containerID + Container extension.
	 */
	public String getFullContainerPath() {
		return InfoHelper.createFullPath(this.containerPath, Integer.toString(this.containerID), Constants.CONTAINER_EXTENTION);
	}

	/**
	 * @return the estimatedContainerSize
	 */
	public long getEstimatedContainerSize() {
		return estimatedContainerSize;
	}

	/**
	 * @param estimatedContainerSize the estimatedContainerSize to set
	 */
	public void setEstimatedContainerSize(long estimatedContainerSize) {
		this.estimatedContainerSize = estimatedContainerSize;
	}

	@Override
	public boolean equals(Object other) {
		if (other == null)
		{
			return false;
		}

		if (this.getClass() != other.getClass())
		{
			return false;
		}

		if (this.containerID != ((ContainerInfo) other).containerID)
		{
			return false;
		}

		if (!this.containerPath.equals(((ContainerInfo) other).containerPath))
		{
			return false;
		}
		return true;
	}

	@Override
	/**
	 * Sorts the ContainerInfos regarding their size from low to high
	 */
	public int compareTo(ContainerInfo o) {
		if (this.estimatedContainerSize > o.estimatedContainerSize)
		{
			return 1;
		}
		if (this.estimatedContainerSize < o.estimatedContainerSize)
		{
			return -1;
		}
		// if size is the same, check if it is also to same container
		return this.getFullContainerPath().compareTo((o.getFullContainerPath()));
	}

	@Override
	public String toString() {
		return getFullContainerPath() + ", Size: " + Long.toString(estimatedContainerSize / 1024) + "KB";
	}
	
}
