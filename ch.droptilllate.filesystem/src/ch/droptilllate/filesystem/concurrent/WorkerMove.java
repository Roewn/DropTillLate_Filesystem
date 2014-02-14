/**
 * 
 */
package ch.droptilllate.filesystem.concurrent;

import ch.droptilllate.filesystem.info.FileInfoMove;
import ch.droptilllate.filesystem.info.InfoHelper;
import ch.droptilllate.filesystem.io.FileException;
import ch.droptilllate.filesystem.io.FileOperator;

/**
 * @author Roewn
 *
 */
public class WorkerMove implements Runnable
{
	private FileInfoMove fileInfo;

    public WorkerMove(FileInfoMove fileInfo){
        this.fileInfo= fileInfo;
    }

    @Override
    public void run() {
        System.out.println("Thread started: " +Thread.currentThread().getName() + " -> File: " + fileInfo.getContainerInfo().getFullContainerPath()+InfoHelper.getDirLimiter()+fileInfo.getFileID());
        try
		{
			FileOperator.moveFile(fileInfo);
		} catch (FileException e)
		{
			System.err.println(e.getError());
			fileInfo.setError(e.getError());
		}
    }


}
