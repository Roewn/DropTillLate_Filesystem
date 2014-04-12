// $codepro.audit.disable com.instantiations.assist.eclipse.analysis.audit.rule.effectivejava.obeyEqualsContract.obeyGeneralContractOfEquals
package ch.droptilllate.filesystem.info;

import ch.droptilllate.filesystem.commons.OsHelper;
import ch.droptilllate.filesystem.error.FileException;


/**
 * Class which holds all informations about a container and the related share relation. 
 * The id of a container can be set by the user or it can be left empty, if the system has to assign a new container the id is set to 0.
 * @author Roewn
 * 
 */
public class ContainerInfo implements Comparable<ContainerInfo>
{
	private int containerID;
	private int shareRelationID;
	private long estimatedContainerSize;

	/**
	 * Creates an ContainerInfo and sets the full path of the Container.
	 * The Container id as well as the parent container path will be set automatically
	 * Example: "C:\\Temp\\Share1\\342657.tilllate"
	 * @param containerPath -> Dropbox path + shareRealtionID + containerID + Container extension.
	 * @throws FileException if the container or sharerelation id could not be fetched.
	 */
	public ContainerInfo(String containerPath) throws FileException
	{
		setContainerPath(containerPath);		
	}

	/**
	 * Creates an ContainerInfo and sets the share relation id, as well as the Container id
	 * If the Container id is unknown and the system has to provide a new number, it sets the containerID = 0.
	 * @param containerID Id of the container or 0 if unknown.
	 * @param shareRelationID share relation which holds the container.
	 */
	public ContainerInfo(int containerID, int shareRelationID)
	{
		this.containerID = containerID;
		this.shareRelationID = shareRelationID;	
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
	 * ShareRelationId which holds the container
	 * @return the ShareRelationID.
	 */
	public int getShareRelationID() {
		return shareRelationID;
	}

	/**
	 * ShareRelationId which holds the container
	 * @param ShareRelationID the shareRelationID to set.
	 */
	public void setShareRelationID(int ShareRelationID) {	
		this.shareRelationID = ShareRelationID;
	}

	/**
	 * Takes the full container path and sets the shareRelationId and the containerID
	 * Example: "C:\\Temp\\Share1\\342657.tilllate"
	 * @param containerPath -> Dropbox path + shareRealtionID + containerID + Container extension.
	 * @throws FileException if the container or sharerelation id could not be fetched.
	 */
	public void setContainerPath(String containerPath) throws FileException
	{
		
			this.shareRelationID = OsHelper.extractShareRelationID(containerPath);
			this.containerID = OsHelper.extractContainerID(containerPath);
	}
	
	/**
	 * ShareRelationpath which holds the container. 
	 * @return the full sharerelation path. Example for full sharerelation path: "C:\\dropbox\\droptillate\\9999999".
	 */
	public String getShareRelationPath() {
		return OsHelper.createFullSharerelationPath(this.shareRelationID, this.containerID);
	}

	/**
	 * Example: "C:\\Temp\\Share1\\342657.tilllate"
	 * @return containerPath -> Parent container path + containerID + Container extension.
	 */
	public String getContainerPath() {
		return OsHelper.createFullContainerPath(this.shareRelationID, this.containerID);
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

		if (this.shareRelationID != ((ContainerInfo) other).shareRelationID)
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
	       result = 31*result + this.shareRelationID;
	      
	       return result;
	   }
	
}
