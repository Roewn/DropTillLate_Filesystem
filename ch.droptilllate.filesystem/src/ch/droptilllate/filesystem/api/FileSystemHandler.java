package ch.droptilllate.filesystem.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import ch.droptilllate.filesystem.commons.Constants;
import ch.droptilllate.filesystem.concurrent.WorkerDecrypt;
import ch.droptilllate.filesystem.concurrent.WorkerDelete;
import ch.droptilllate.filesystem.concurrent.WorkerEncrypt;
import ch.droptilllate.filesystem.concurrent.WorkerListFiles;
import ch.droptilllate.filesystem.concurrent.WorkerMove;
import ch.droptilllate.filesystem.info.FileInfo;
import ch.droptilllate.filesystem.info.FileInfoDecrypt;
import ch.droptilllate.filesystem.info.FileInfoEncrypt;
import ch.droptilllate.filesystem.info.FileInfoMove;
import ch.droptilllate.filesystem.io.ContainerManager;
import ch.droptilllate.filesystem.io.IContainer;
import ch.droptilllate.filesystem.io.IFile;
import ch.droptilllate.filesystem.security.KeyRelation;
import ch.droptilllate.filesystem.truezip.ContainerHandler;
import ch.droptilllate.filesystem.truezip.FileHandler;

/**
 * This class extends the IFileSystem interface and operates on the filesystem concurrently (multithreading). For every operation of a file,
 * a new thread gets started out of a worker pool.
 * 
 * @author Roewn
 * 
 */
public class FileSystemHandler implements IFileSystem
{
	private IContainer iContainer = new ContainerHandler();
	private IFile iFile = new FileHandler();

	private ContainerManager containerManager;
	private int cores;

	public FileSystemHandler()
	{
		containerManager = ContainerManager.getInstance();

		// initialise Worker pool
		cores = Runtime.getRuntime().availableProcessors();
	}

	@Override
	public FileHandlingSummary encryptFiles(List<FileInfoEncrypt> fileInfoList, KeyRelation keyRelation)
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
			// Get key for the current share relation
			String key = keyRelation.getKeyOfShareRelation(fileInfo.getContainerInfo().getParentContainerPath());
			// Generate Error if share relation not found in key relation
			if (key == null)
			{
				fileInfo.setError(FileError.SHARERELATION_NOT_FOUND, fileInfo.getContainerInfo().getParentContainerPath());
			}
			// if no error occurred during the container assignment perform the operation
			if (fileInfo.getError() == FileError.NONE)
			{
				Runnable worker = new WorkerEncrypt(fileInfo, key);
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

		iFile.umountFileSystem();

		// Initialise the summary
		FileHandlingSummary fileHandSummary = new FileHandlingSummary(fileInfoList);
		return fileHandSummary;
	}

	@Override
	public FileHandlingSummary decryptFiles(List<FileInfoDecrypt> fileInfoList, KeyRelation keyRelation)
	{
		ExecutorService executor = Executors.newFixedThreadPool(cores);
		System.out.println(Constants.CONSOLE_LIMITER);
		System.out.println("Prozessor Threads: " + cores);

		System.out.println(Constants.CONSOLE_LIMITER);
		// Encrypt the Files
		for (FileInfoDecrypt fileInfo : fileInfoList)
		{
			// Get key for the current share relation
			String key = keyRelation.getKeyOfShareRelation(fileInfo.getContainerInfo().getParentContainerPath());
			// Generate Error if share relation not found in key relation
			if (key == null)
			{
				fileInfo.setError(FileError.SHARERELATION_NOT_FOUND, fileInfo.getContainerInfo().getParentContainerPath());
			}
			// if no error occurred during the container assignment perform the operation
			if (fileInfo.getError() == FileError.NONE)
			{
				Runnable worker = new WorkerDecrypt(fileInfo, key);
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

		iFile.umountFileSystem();

		// Initialise the summary
		FileHandlingSummary fileHandSummary = new FileHandlingSummary(fileInfoList);
		return fileHandSummary;
	}

	@Override
	public FileHandlingSummary deleteFiles(List<FileInfo> fileInfoList, KeyRelation keyRelation)
	{
		ExecutorService executor = Executors.newFixedThreadPool(cores);
		System.out.println(Constants.CONSOLE_LIMITER);
		System.out.println("Prozessor Threads: " + cores);

		System.out.println(Constants.CONSOLE_LIMITER);
		// Delete the Files
		for (FileInfo fileInfo : fileInfoList)
		{
			// Get key for the current share relation
			String key = keyRelation.getKeyOfShareRelation(fileInfo.getContainerInfo().getParentContainerPath());
			// Generate Error if share relation not found in key relation
			if (key == null)
			{
				fileInfo.setError(FileError.SHARERELATION_NOT_FOUND, fileInfo.getContainerInfo().getParentContainerPath());
			}
			// if no error occurred during the container assignment perform the operation
			if (fileInfo.getError() == FileError.NONE)
			{
				Runnable worker = new WorkerDelete(fileInfo, key);
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
		iFile.umountFileSystem();
		// Remove all empty containers from the fileSystem
		iContainer.removeEmptyContainers(fileInfoList);
		// Initialise the summary
		FileHandlingSummary fileHandSummary = new FileHandlingSummary(fileInfoList);
		return fileHandSummary;
	}

	@Override
	public FileHandlingSummary moveFiles(List<FileInfoMove> fileInfoList, KeyRelation keyRelation)
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
			// Get key for the current share relation
			String key = keyRelation.getKeyOfShareRelation(fileInfo.getContainerInfo().getParentContainerPath());
			// Generate Error if share relation not found in key relation
			if (key == null)
			{
				fileInfo.setError(FileError.SHARERELATION_NOT_FOUND, fileInfo.getContainerInfo().getParentContainerPath());
			}
			// if no error occurred during the container assignment perform the operation
			if (fileInfo.getError() == FileError.NONE)
			{
				Runnable worker = new WorkerMove(fileInfo, key);
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
		iFile.umountFileSystem();

		// create FileInfo list of the src files
		List<FileInfo> fiSrcList = new ArrayList<FileInfo>();
		for (FileInfoMove fim : fileInfoList)
		{
			fiSrcList.add(new FileInfo(fim.getFileID(), fim.getSrcContainerInfo()));
		}
		// Remove all empty containers from the fileSystem
		iContainer.removeEmptyContainers(fiSrcList);

		// Initialise the summary
		FileHandlingSummary fileHandSummary = new FileHandlingSummary(fileInfoList);
		return fileHandSummary;
	}

	@Override
	public HashMap<String, List<FileInfo>> getFilesPerRelation(KeyRelation keyRelation)
	{
		ExecutorService executor = Executors.newFixedThreadPool(cores);
		System.out.println(Constants.CONSOLE_LIMITER);
		System.out.println("Prozessor Threads: " + cores);
		System.out.println(Constants.CONSOLE_LIMITER);

		// create a list to hold the Future object associated with Callable
		List<Future<List<FileInfo>>> workerList = new ArrayList<Future<List<FileInfo>>>();
		// create result map
		HashMap<String, List<FileInfo>> resultMap = new HashMap<String, List<FileInfo>>();
		Set<String> shareRelationSet = keyRelation.getKeyShareMap().keySet();
		// Start the workers for all share relations
		for (String shareRelation : shareRelationSet)
		{
			// Get key for the current share relation
			String key = keyRelation.getKeyOfShareRelation(shareRelation);
			// Get all files of this share relation
			Future<List<FileInfo>> worker;
			worker = executor.submit(new WorkerListFiles(shareRelation, key));
			workerList.add(worker);
		}
		// wait for the result of every share relation
		for (Future<List<FileInfo>> worker : workerList)
		{
			try
			{
				List<FileInfo> resultList = worker.get();
				if (resultList.size() > 0)
				{
					resultMap.put(resultList.get(0).getContainerInfo().getParentContainerPath(), resultList);
				}
			} catch (InterruptedException | ExecutionException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
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
		iFile.umountFileSystem();
		return resultMap;
	}

}
