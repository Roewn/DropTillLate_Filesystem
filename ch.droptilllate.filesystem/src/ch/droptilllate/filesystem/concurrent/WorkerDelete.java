/**
 * 
 */
package ch.droptilllate.filesystem.concurrent;

import ch.droptilllate.filesystem.info.FileInfo;
import ch.droptilllate.filesystem.info.InfoHelper;
import ch.droptilllate.filesystem.io.FileException;
import ch.droptilllate.filesystem.io.FileOperator;

/**
 * @author Roewn
 *
 */
public class WorkerDelete implements Runnable
{
	private FileInfo fileInfo;

    public WorkerDelete(FileInfo fileInfo){
        this.fileInfo= fileInfo;
    }

    @Override
    public void run() {
        System.out.println("Thread started: " +Thread.currentThread().getName() + " -> File: " + fileInfo.getContainerInfo().getFullContainerPath()+InfoHelper.getDirLimiter()+fileInfo.getFileID());
        try
		{
			FileOperator.deleteFile(fileInfo);
		} catch (FileException e)
		{
			System.err.println(e.getError());
			fileInfo.setError(e.getError());
		}
    }


}
