/**
 * 
 */
package ch.droptilllate.filesystem.info;


/**
 * @author Roewn
 *
 */
public class FileInfoMove extends FileInfo
{
	ContainerInfo srcContainerInfo;
	
	/**
	 * Constructor for moving an existing file from a container to a new share relation.
	 * The target container info does just pass the path of the share relation, the container id will be assigned by the Container Manager.
	 * @param fileID Unique id of the File
	 * @param fileSize Size of the file to move
	 * @param srcShareRelationID share relation which holds the container.
	 * @param srcContainerID Id of the container which contains the source file. 
	 * @param destShareRelationID share relation where the file has to be moved in a new container.
	 */
	public FileInfoMove (int fileID, long fileSize, int srcShareRelationID, int srcContainerID, int destShareRelationID){
		super(fileID, new ContainerInfo(0, destShareRelationID));
		super.setSize(fileSize);
		srcContainerInfo = new ContainerInfo(srcContainerID, srcShareRelationID);
	}

	/**
	 * @return the source ContainerInfo
	 */
	public synchronized ContainerInfo getSrcContainerInfo() {
		return srcContainerInfo;
	}

	/**
	 * @param srcContainerInfo the source ContainerInfo to set
	 */
	public synchronized void setSrcContainerInfo(ContainerInfo srcContainerInfo) {
		this.srcContainerInfo = srcContainerInfo;
	}
	
	/**
	 * @return the destination ContainerInfo
	 */
	public synchronized ContainerInfo getDestContainerInfo() {
		return super.getContainerInfo();
	}

	/**
	 * @param destContainerInfo the destination ContainerInfo to set
	 */
	public synchronized void setDestContainerInfo(ContainerInfo destContainerInfo) {
		super.setContainerInfo(destContainerInfo);
	}
	

}
