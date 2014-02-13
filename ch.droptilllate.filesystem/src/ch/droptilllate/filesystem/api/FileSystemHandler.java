/**
 * 
 */
package ch.droptilllate.filesystem.api;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ch.droptilllate.filesystem.commons.Constants;
import ch.droptilllate.filesystem.commons.Timer;
import ch.droptilllate.filesystem.concurrent.WorkerEncrypt;
import ch.droptilllate.filesystem.info.FileInfo;
import ch.droptilllate.filesystem.info.FileInfoDecrypt;
import ch.droptilllate.filesystem.info.FileInfoEncrypt;
import ch.droptilllate.filesystem.info.FileInfoMove;
import ch.droptilllate.filesystem.io.ContainerManager;
import ch.droptilllate.filesystem.io.FileException;
import ch.droptilllate.filesystem.io.FileOperator;
import ch.droptilllate.filesystem.security.KeyManager;
import de.schlichtherle.truezip.file.TArchiveDetector;
import de.schlichtherle.truezip.file.TConfig;

/**
 * @author Roewn
 * 
 */
public class FileSystemHandler implements IFileSystem
{
	private ContainerManager containerManager;

	public FileSystemHandler()
	{
		containerManager = ContainerManager.getInstance();

		// TODO Initialise password for every single container
		// Initialise the config
		TConfig config = TConfig.get();
		// Configure custom application file format.
		TArchiveDetector tad = KeyManager.getArchiveDetector(Constants.CONTAINER_EXTENTION, Constants.PASSWORD.toCharArray());
		config.setArchiveDetector(tad);
	}

	@Override
	public FileHandlingSummary encryptFiles(List<FileInfoEncrypt> fileInfoList)
	{
		// Initialise the summary
		FileHandlingSummary fileHandSummary = new FileHandlingSummary();
		// Get the ContainerInfo with the assigned ContainerID of every File updated
		containerManager.assignContainerID(fileInfoList);
		// Encrypt the Files
		for (FileInfoEncrypt fileInfo : fileInfoList)
		{
			try
			{
				// if no error occurred during the container assignment perform the operation
				if (fileInfo.getError() == FileError.NONE)
				{
					// TODO Remove debug options
					// System.out.print("CREATE;" + fileInfo.getFileID());
					// Timer.start();
					FileOperator.addFile(fileInfo);
					// System.out.println(";" + Timer.stop(false));
					fileHandSummary.addFileInfoSuccess(fileInfo);
				} else
				{
					fileHandSummary.addFileInfoError(fileInfo);
				}
			} catch (FileException e)
			{
				System.err.println(e.getError());
				fileInfo.setError(e.getError());
				fileHandSummary.addFileInfoError(fileInfo);
			} catch (Exception e)
			{
				System.err.println(e.getMessage());
				fileInfo.setError(FileError.UNKNOWN, e.getMessage());
				fileHandSummary.addFileInfoError(fileInfo);
			}
		}
		FileOperator.umountFileSystem();
		return fileHandSummary;
	}

	@Override
	public FileHandlingSummary decryptFiles(List<FileInfoDecrypt> fileInfoList)
	{
		// Encrypt the Files
		for (FileInfoDecrypt fileInfo : fileInfoList)
		{
			try
			{
				// if no error occurred during the container assignment perform the operation
				if (fileInfo.getError() == FileError.NONE)
				{
					// TODO Remove debug options
					// System.out.print("EXTRACT;" + fileInfo.getFileID());
					// Timer.start();
					FileOperator.extractFile(fileInfo);
					// System.out.println(";" + Timer.stop(false));
				}
			} catch (FileException e)
			{
				System.err.println(e.getError());
				fileInfo.setError(e.getError());
			}
		}
		FileOperator.umountFileSystem();

		// Initialise the summary
		FileHandlingSummary fileHandSummary = new FileHandlingSummary(fileInfoList);
		return fileHandSummary;
	}

	@Override
	public FileHandlingSummary deleteFiles(List<FileInfo> fileInfoList)
	{
		// Delete the Files
		for (FileInfo fileInfo : fileInfoList)
		{
			try
			{
				// if no error occurred during the container assignment perform the operation
				if (fileInfo.getError() == FileError.NONE)
				{
					FileOperator.deleteFile(fileInfo);
				}
			} catch (FileException e)
			{
				System.err.println(e.getError());
				fileInfo.setError(e.getError());
			}
		}
		FileOperator.umountFileSystem();

		// Initialise the summary
		FileHandlingSummary fileHandSummary = new FileHandlingSummary(fileInfoList);
		return fileHandSummary;
	}

	@Override
	public FileHandlingSummary moveFiles(List<FileInfoMove> fileInfoList)
	{
		// Get the ContainerInfo with the assigned ContainerID of every File updated
		containerManager.assignContainerID(fileInfoList);
		// Move the Files
		for (FileInfoMove fileInfo : fileInfoList)
		{
			try
			{
				// if no error occurred during the container assignment perform the operation
				if (fileInfo.getError() == FileError.NONE)
				{
					FileOperator.moveFile(fileInfo);
				} 
			} catch (FileException e)
			{
				System.err.println(e.getError());
				fileInfo.setError(e.getError());
			} 
		}
		FileOperator.umountFileSystem();

		// Initialise the summary
		FileHandlingSummary fileHandSummary = new FileHandlingSummary(fileInfoList);
		return fileHandSummary;
	}

}
