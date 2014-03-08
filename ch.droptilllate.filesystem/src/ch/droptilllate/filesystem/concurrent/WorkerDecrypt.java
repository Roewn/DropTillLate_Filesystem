/**
 * 
 */
package ch.droptilllate.filesystem.concurrent;

import ch.droptilllate.filesystem.error.FileException;
import ch.droptilllate.filesystem.info.FileInfoDecrypt;
import ch.droptilllate.filesystem.info.InfoHelper;
import ch.droptilllate.filesystem.io.IFile;
import ch.droptilllate.filesystem.truezip.FileHandler;

/**
 * @author Roewn
 * 
 */
public class WorkerDecrypt implements Runnable
{
	private IFile iFile = new FileHandler();
	private FileInfoDecrypt fileInfo;
	private String key;


	public WorkerDecrypt(FileInfoDecrypt fileInfo, String key){
        this.fileInfo= fileInfo;
        this.key = key;
	}

	@Override
	public void run()
	{
		
			System.out.println("Thread started: " + Thread.currentThread().getName() + " -> File: "
					+ fileInfo.getContainerInfo().getContainerPath() + InfoHelper.getDirLimiter() + fileInfo.getFileID());
			try
			{
				iFile.decryptFile(fileInfo, this.key);
			} catch (FileException e)
			{
				
				System.err.println(e.getError());
				fileInfo.setError(e.getError());
				
			}
		
	}

}
