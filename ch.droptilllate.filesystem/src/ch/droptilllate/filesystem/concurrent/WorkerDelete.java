/**
 * 
 */
package ch.droptilllate.filesystem.concurrent;

import ch.droptilllate.filesystem.commons.OsHelper;
import ch.droptilllate.filesystem.error.FileException;
import ch.droptilllate.filesystem.info.FileInfo;
import ch.droptilllate.filesystem.io.IFile;
import ch.droptilllate.filesystem.truezip.FileHandler;

/**
 * @author Roewn
 *
 */
public class WorkerDelete implements Runnable
{
	private IFile iFile = new FileHandler();
	private FileInfo fileInfo;
	private String key;

    public WorkerDelete(FileInfo fileInfo, String key){
        this.fileInfo= fileInfo;
        this.key = key;
    }

    @Override
    public void run() {
        System.out.println("Thread started: " +Thread.currentThread().getName() + " -> File: " + fileInfo.getContainerInfo().getContainerPath()+OsHelper.getDirLimiter()+fileInfo.getFileID());
        try
		{
			iFile.deleteFile(fileInfo, key);
		} catch (FileException e)
		{
			System.err.println(e.getError());
			fileInfo.setError(e.getError());
		}
    }


}
