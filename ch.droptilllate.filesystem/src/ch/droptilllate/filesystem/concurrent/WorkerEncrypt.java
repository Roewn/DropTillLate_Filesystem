/**
 * 
 */
package ch.droptilllate.filesystem.concurrent;

import ch.droptilllate.filesystem.info.FileInfoEncrypt;
import ch.droptilllate.filesystem.io.FileException;
import ch.droptilllate.filesystem.io.FileOperator;

/**
 * @author Roewn
 *
 */
public class WorkerEncrypt implements Runnable
{
	private FileInfoEncrypt fileInfo;

    public WorkerEncrypt(FileInfoEncrypt fileInfo){
        this.fileInfo= fileInfo;
    }

    @Override
    public void run() {
        System.out.println("Thread started: " +Thread.currentThread().getName() + " -> File: " + fileInfo.getFullPlainFilePath());
        try
		{
			FileOperator.addFile(fileInfo);
		} catch (FileException e)
		{
			System.err.println(e.getError());
			fileInfo.setError(e.getError());
		}
    }


}
