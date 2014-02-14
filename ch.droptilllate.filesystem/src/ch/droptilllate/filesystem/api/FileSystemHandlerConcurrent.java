package ch.droptilllate.filesystem.api;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ch.droptilllate.filesystem.commons.Constants;
import ch.droptilllate.filesystem.concurrent.WorkerDecrypt;
import ch.droptilllate.filesystem.concurrent.WorkerDelete;
import ch.droptilllate.filesystem.concurrent.WorkerEncrypt;
import ch.droptilllate.filesystem.concurrent.WorkerMove;
import ch.droptilllate.filesystem.info.FileInfo;
import ch.droptilllate.filesystem.info.FileInfoDecrypt;
import ch.droptilllate.filesystem.info.FileInfoEncrypt;
import ch.droptilllate.filesystem.info.FileInfoMove;
import ch.droptilllate.filesystem.io.ContainerManager;
import ch.droptilllate.filesystem.io.ContainerOperator;
import ch.droptilllate.filesystem.io.FileOperator;
import ch.droptilllate.filesystem.security.KeyManager;
import de.schlichtherle.truezip.file.TArchiveDetector;
import de.schlichtherle.truezip.file.TConfig;

/**
 * This class extends the IFileSystem interface and operates on the filesystem concurrently (multithreading). For every operation of a file,
 * a new thread gets started out of a worker pool.
 * 
 * @author Roewn
 * 
 */
public class FileSystemHandlerConcurrent implements IFileSystem
{
	private ContainerManager containerManager;
	private int cores;

	public FileSystemHandlerConcurrent()
	{
		containerManager = ContainerManager.getInstance();

		// TODO Initialise password for every single container
		// Initialise the config
		TConfig config = TConfig.get();
		// Configure custom application file format.
		TArchiveDetector tad = KeyManager.getArchiveDetector(Constants.CONTAINER_EXTENTION, Constants.PASSWORD.toCharArray());
		config.setArchiveDetector(tad);

		// initialise Worker pool
		cores = Runtime.getRuntime().availableProcessors();
	}

	@Override
	public FileHandlingSummary encryptFiles(List<FileInfoEncrypt> fileInfoList)
	{
		ExecutorService executor = Executors.newFixedThreadPool(cores);
		System.out.println(Constants.CONSOLE_LIMITER);
		System.out.println("Prozessor Threads: " + cores);

		// Get the ContainerInfo with the assigned ContainerID of every File updated
		containerManager.assignContainerID(fileInfoList);

		System.out.println(Constants.CONSOLE_LIMITER);
		// Encrypt the Files
		for (FileInfoEncrypt fileInfo : fileInfoList)
		{
			// if no error occurred during the container assignment perform the operation
			if (fileInfo.getError() == FileError.NONE)
			{
				Runnable worker = new WorkerEncrypt(fileInfo);
				executor.execute(worker);
			}

		}

		// This will make the executor accept no new threads
		// and finish all existing threads in the queue
		executor.shutdown();
		// Wait until all threads are finish
		while (!executor.isTerminated())
		{
		}

		System.out.println("Finished all threads");

		FileOperator.umountFileSystem();

		// Initialise the summary
		FileHandlingSummary fileHandSummary = new FileHandlingSummary(fileInfoList);
		return fileHandSummary;
	}

	@Override
	public FileHandlingSummary decryptFiles(List<FileInfoDecrypt> fileInfoList)
	{
		ExecutorService executor = Executors.newFixedThreadPool(cores);
		System.out.println(Constants.CONSOLE_LIMITER);
		System.out.println("Prozessor Threads: " + cores);

		System.out.println(Constants.CONSOLE_LIMITER);
		// Encrypt the Files
		for (FileInfoDecrypt fileInfo : fileInfoList)
		{
			// if no error occurred during the container assignment perform the operation
			if (fileInfo.getError() == FileError.NONE)
			{
				Runnable worker = new WorkerDecrypt(fileInfo);
				executor.execute(worker);
			}
		}
		// This will make the executor accept no new threads
		// and finish all existing threads in the queue
		executor.shutdown();
		// Wait until all threads are finish
		while (!executor.isTerminated())
		{
		}

		System.out.println("Finished all threads");

		FileOperator.umountFileSystem();

		// Initialise the summary
		FileHandlingSummary fileHandSummary = new FileHandlingSummary(fileInfoList);
		return fileHandSummary;
	}

	@Override
	public FileHandlingSummary deleteFiles(List<FileInfo> fileInfoList)
	{
		ExecutorService executor = Executors.newFixedThreadPool(cores);
		System.out.println(Constants.CONSOLE_LIMITER);
		System.out.println("Prozessor Threads: " + cores);

		System.out.println(Constants.CONSOLE_LIMITER);
		// Delete the Files
		for (FileInfo fileInfo : fileInfoList)
		{
			// if no error occurred during the container assignment perform the operation
			if (fileInfo.getError() == FileError.NONE)
			{
				Runnable worker = new WorkerDelete(fileInfo);
				executor.execute(worker);
			}

		}
		// This will make the executor accept no new threads
		// and finish all existing threads in the queue
		executor.shutdown();
		// Wait until all threads are finish
		while (!executor.isTerminated())
		{
		}

		System.out.println("Finished all threads");
		FileOperator.umountFileSystem();
		// Remove all empty containers from the fileSystem
		ContainerOperator.removeEmptyContainers(fileInfoList);
		// Initialise the summary
		FileHandlingSummary fileHandSummary = new FileHandlingSummary(fileInfoList);
		return fileHandSummary;
	}

	@Override
	public FileHandlingSummary moveFiles(List<FileInfoMove> fileInfoList)
	{
		ExecutorService executor = Executors.newFixedThreadPool(cores);
		System.out.println(Constants.CONSOLE_LIMITER);
		System.out.println("Prozessor Threads: " + cores);

		// Get the ContainerInfo with the assigned ContainerID of every File updated
		containerManager.assignContainerID(fileInfoList);

		System.out.println(Constants.CONSOLE_LIMITER);
		// Move the Files
		for (FileInfoMove fileInfo : fileInfoList)
		{
			// if no error occurred during the container assignment perform the operation
			if (fileInfo.getError() == FileError.NONE)
			{
				Runnable worker = new WorkerMove(fileInfo);
				executor.execute(worker);
			}
		}
		// This will make the executor accept no new threads
		// and finish all existing threads in the queue
		executor.shutdown();
		// Wait until all threads are finish
		while (!executor.isTerminated())
		{
		}

		System.out.println("Finished all threads");
		FileOperator.umountFileSystem();
		
		// create FileInfo list of the src files
		List<FileInfo> fiSrcList = new ArrayList<FileInfo>();
		for (FileInfoMove fim :fileInfoList) {
			fiSrcList.add(new FileInfo(fim.getFileID(),fim.getSrcContainerInfo()));
		}
		// Remove all empty containers from the fileSystem
		ContainerOperator.removeEmptyContainers(fiSrcList);

		// Initialise the summary
		FileHandlingSummary fileHandSummary = new FileHandlingSummary(fileInfoList);
		return fileHandSummary;
	}

}
