/**
 * 
 */
package ch.droptilllate.filesystem.concurrent;

import ch.droptilllate.filesystem.info.FileInfoMove;
import ch.droptilllate.filesystem.info.InfoHelper;
import ch.droptilllate.filesystem.io.FileException;
import ch.droptilllate.filesystem.io.IFile;
import ch.droptilllate.filesystem.truezip.FileHandler;

/**
 * @author Roewn
 *
 */
public class WorkerMove implements Runnable
{
	private IFile iFile = new FileHandler();
	private FileInfoMove fileInfo;
	private String srcKey;
	private String dstKey;

    public WorkerMove(FileInfoMove fileInfo, String srcKey, String dstKey){
        this.fileInfo= fileInfo;
        this.srcKey = srcKey;
        this.dstKey = dstKey;        
    }

    @Override
    public void run() {
        System.out.println("Thread started: " +Thread.currentThread().getName() + " -> File: " + fileInfo.getContainerInfo().getContainerPath()+InfoHelper.getDirLimiter()+fileInfo.getFileID());
        try
		{
			iFile.moveFile(fileInfo, srcKey, dstKey);
		} catch (FileException e)
		{
			System.err.println(e.getError());
			fileInfo.setError(e.getError());
		}
    }
}
