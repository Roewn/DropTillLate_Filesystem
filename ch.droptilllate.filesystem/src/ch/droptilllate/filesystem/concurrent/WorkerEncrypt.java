/**
 * 
 */
package ch.droptilllate.filesystem.concurrent;

import ch.droptilllate.filesystem.info.FileInfoEncrypt;
import ch.droptilllate.filesystem.io.FileException;
import ch.droptilllate.filesystem.io.IFile;
import ch.droptilllate.filesystem.truezip.FileHandler;

/**
 * @author Roewn
 *
 */
public class WorkerEncrypt implements Runnable
{
	private IFile iFile = new FileHandler();
	private FileInfoEncrypt fileInfo;
	private String key;

    public WorkerEncrypt(FileInfoEncrypt fileInfo, String key){
        this.fileInfo= fileInfo;
        this.key = key;
    }

    @Override
    public void run() {
        System.out.println("Thread started: " +Thread.currentThread().getName() + " -> File: " + fileInfo.getFullPlainFilePath());
        try
		{
			iFile.encryptFile(fileInfo, this.key);
		} catch (FileException e)
		{
			System.err.println(e.getError());
			fileInfo.setError(e.getError());
		}
    }


}
