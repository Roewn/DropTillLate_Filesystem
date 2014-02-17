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
	private String shareRelationPath;
	private long estimatedContainerSize;

	/**
	 * Creates an ContainerInfo and sets the full path of the Container (Parent container path + containerID + Container extension).
	 * The Container id as well as the parent container path will be set automatically
	 * Example: "C:\\Temp\\Share1\\342657.tilllate"
	 * @param containerPath -> Parent container path + containerID + Container extension.
	 */
	public ContainerInfo(String containerPath)
	{
		setContainerPath(containerPath);		
	}

	/**
	 * Creates an ContainerInfo and sets the parent path of the Container, as well as the Container id
	 * If the Container id is unknown and the system has to provide a new number, set the containerID = 0.
	 * shareRelationPath Example: "C:\\Temp\\Share1\\"
	 * @param containerID Id of the container or 0 if unknown.
	 * @param shareRelationPath Directory which holds the container Example: "C:\\Temp\\Share1".
	 */
	public ContainerInfo(int containerID, String shareRelationPath)
	{
		this.containerID = containerID;
		setShareRelationPath(shareRelationPath);		
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
	 * ShareRelationPath Example: "C:\\Temp\\Share1"
	 * @return the ShareRelationPath -> Directory which holds the container.
	 */
	public String getShareRelationPath() {
		return shareRelationPath;
	}

	/**
	 * ShareRelationPath Example: "C:\\Temp\\Share1"
	 * @param ShareRelationPath the shareRelationPath to set -> Directory which holds the container.
	 */
	public void setShareRelationPath(String ShareRelationPath) {	
		this.shareRelationPath = InfoHelper.checkPath(ShareRelationPath);
	}

	/**
	 * Example: "C:\\Temp\\Share1\\342657.tilllate"
	 * @param containerPath containerPath -> shareRelationPath + containerID + Container extension.
	 */
	public void setContainerPath(String containerPath) {
		try
		{
			this.shareRelationPath = containerPath.substring(0, containerPath.lastIndexOf(InfoHelper.getDirLimiter()));
			this.containerID = Integer.parseInt((containerPath.substring(containerPath.lastIndexOf(InfoHelper.getDirLimiter()) + InfoHelper.getOffset(),
					containerPath.lastIndexOf(Constants.EXT_LIMITER))));
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}

	}

	/**
	 * Example: "C:\\Temp\\Share1\\342657.tilllate"
	 * @return containerPath -> Parent container path + containerID + Container extension.
	 */
	public String getContainerPath() {
		return InfoHelper.createFullPath(this.shareRelationPath, Integer.toString(this.containerID), Constants.CONTAINER_EXTENTION);
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

		if (!this.shareRelationPath.equals(((ContainerInfo) other).shareRelationPath))
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
		return this.getContainerPath().compareTo((o.getContainerPath()));
	}

	@Override
	public String toString() {
		return getContainerPath() + ", Size: " + Long.toString(estimatedContainerSize / 1024) + "KB";
	}
	
	 @Override
	 public int hashCode(){
	       int result = 0;
	       result = 31*result + this.containerID;
	       result = 31*result + (this.shareRelationPath !=null ? this.shareRelationPath.hashCode() : 0);
	      
	       return result;
	   }
	
}
