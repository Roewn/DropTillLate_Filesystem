/**
 * 
 */
package ch.droptilllate.filesystem.concurrent;

import ch.droptilllate.filesystem.commons.Constants;
import ch.droptilllate.filesystem.commons.OsUtils;
import ch.droptilllate.filesystem.info.FileInfoDecrypt;
import ch.droptilllate.filesystem.info.FileInfoEncrypt;
import ch.droptilllate.filesystem.info.InfoHelper;
import ch.droptilllate.filesystem.io.FileException;
import ch.droptilllate.filesystem.io.FileOperator;

/**
 * @author Roewn
 *
 */
public class WorkerDecrypt implements Runnable
{
	private FileInfoDecrypt fileInfo;

    public WorkerDecrypt(FileInfoDecrypt fileInfo){
        this.fileInfo= fileInfo;
    }

    @Override
    public void run() {
        System.out.println("Thread started: " +Thread.currentThread().getName() + " -> File: " + fileInfo.getContainerInfo().getFullContainerPath()+InfoHelper.getDirLimiter()+fileInfo.getFileID());
        try
		{
			FileOperator.extractFile(fileInfo);
		} catch (FileException e)
		{
			System.err.println(e.getError());
			fileInfo.setError(e.getError());
		}
    }


}
