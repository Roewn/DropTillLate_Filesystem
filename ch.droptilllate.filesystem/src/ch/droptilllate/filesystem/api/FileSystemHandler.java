package ch.droptilllate.filesystem.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import ch.droptilllate.filesystem.concurrent.WorkerDecrypt;
import ch.droptilllate.filesystem.concurrent.WorkerDelete;
import ch.droptilllate.filesystem.concurrent.WorkerEncrypt;
import ch.droptilllate.filesystem.concurrent.WorkerListFiles;
import ch.droptilllate.filesystem.concurrent.WorkerMove;
import ch.droptilllate.filesystem.error.FileError;
import ch.droptilllate.filesystem.error.FileException;
import ch.droptilllate.filesystem.info.FileInfo;
import ch.droptilllate.filesystem.info.FileInfoDecrypt;
import ch.droptilllate.filesystem.info.FileInfoEncrypt;
import ch.droptilllate.filesystem.info.FileInfoMove;
import ch.droptilllate.filesystem.io.ContainerManager;
import ch.droptilllate.filesystem.io.IContainer;
import ch.droptilllate.filesystem.io.IFile;
import ch.droptilllate.filesystem.preferences.Constants;
import ch.droptilllate.filesystem.preferences.Options;
import ch.droptilllate.filesystem.truezip.ContainerHandler;
import ch.droptilllate.filesystem.truezip.FileHandler;
import ch.droptilllate.security.commons.KeyRelation;

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
	
	@SuppressWarnings("unused")
	private FileSystemHandler(){}

	public FileSystemHandler(String droptilllatePath, String tempPath)
	{
		// initialise options
		Options options = Options.getInstance();
		options.setDroptilllatePath(droptilllatePath);
		options.setTempPath(tempPath);
		
		containerManager = ContainerManager.getInstance();
		
		

		// initialise Worker pool
		cores = Runtime.getRuntime().availableProcessors();
		
	}

	@Override
	public FileHandlingSummary encryptFiles(List<FileInfoEncrypt> fileInfoList, KeyRelation keyRelation)
	{
		ExecutorService executor = Executors.newFixedThreadPool(cores);
		printStartToConsole("encryptFiles", cores);

		// Get the ContainerInfo with the assigned ContainerID of every File updated
		containerManager.assignContainerID(fileInfoList);

		System.out.println(Constants.CONSOLE_LIMITER);
		// Encrypt the Files
		for (FileInfoEncrypt fileInfo : fileInfoList)
		{
			// Get key for the current share relation
			String key = getKey(fileInfo, keyRelation);
			// if no error occurred during the container assignment perform the operation
			if (fileInfo.getError() == FileError.NONE)
			{
				Runnable worker = new WorkerEncrypt(fileInfo, key);
				executor.execute(worker);
			}
		}
		waitExecutor(executor);

		System.out.println("Finished all threads");

		iFile.unmountFileSystem();

		// Initialise the summary
		FileHandlingSummary fileHandSummary = new FileHandlingSummary(fileInfoList);
		return fileHandSummary;
	}

	@Override
	public FileHandlingSummary decryptFiles(List<FileInfoDecrypt> fileInfoList, KeyRelation keyRelation)
	{
		ExecutorService executor = Executors.newFixedThreadPool(cores);
		printStartToConsole("encryptFiles", cores);

		System.out.println(Constants.CONSOLE_LIMITER);
		// Encrypt the Files
		for (FileInfoDecrypt fileInfo : fileInfoList)
		{
			// Get key for the current share relation
			String key = getKey(fileInfo, keyRelation);
			// if no error occurred during the container assignment perform the operation
			if (fileInfo.getError() == FileError.NONE)
			{
				Runnable worker = new WorkerDecrypt(fileInfo, key);
				executor.execute(worker);
			}
		}
		waitExecutor(executor);

		System.out.println("Finished all threads");

		iFile.unmountFileSystem();

		// Initialise the summary
		FileHandlingSummary fileHandSummary = new FileHandlingSummary(fileInfoList);
		return fileHandSummary;
	}

	@Override
	public FileHandlingSummary deleteFiles(List<FileInfo> fileInfoList, KeyRelation keyRelation)
	{
		ExecutorService executor = Executors.newFixedThreadPool(cores);
		printStartToConsole("deleteFiles", cores);

		System.out.println(Constants.CONSOLE_LIMITER);
		// Delete the Files
		for (FileInfo fileInfo : fileInfoList)
		{
			// Get key for the current share relation
			String key = getKey(fileInfo, keyRelation);
			// if no error occurred during the container assignment perform the operation
			if (fileInfo.getError() == FileError.NONE)
			{
				Runnable worker = new WorkerDelete(fileInfo, key);
				executor.execute(worker);
			}

		}
		waitExecutor(executor);

		System.out.println("Finished all threads");
		iFile.unmountFileSystem();
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
		printStartToConsole("moveFiles", cores);

		// Get the ContainerInfo with the assigned ContainerID of every File updated
		containerManager.assignContainerID(fileInfoList);

		System.out.println(Constants.CONSOLE_LIMITER);
		// Move the Files
		for (FileInfoMove fileInfo : fileInfoList)
		{

			// Get key for the destination share relation
			String dstKey = getKey(fileInfo, keyRelation);
			// Get key for the source share relation
			String srcKey = getKey(fileInfo.getSrcContainerInfo().getShareRelationID(), fileInfo, keyRelation);
			// if no error occurred during the container assignment perform the operation
			if (fileInfo.getError() == FileError.NONE)
			{
				Runnable worker = new WorkerMove(fileInfo, srcKey, dstKey);
				executor.execute(worker);
			}
		}
		waitExecutor(executor);

		System.out.println("Finished all threads");
		iFile.unmountFileSystem();

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
	public HashMap<Integer, List<FileInfo>> getFilesPerRelation(KeyRelation keyRelation)
	{
		ExecutorService executor = Executors.newFixedThreadPool(cores);
		printStartToConsole("getFilesPerRelation", cores);

		// create a list to hold the Future object associated with Callable
		List<Future<List<FileInfo>>> workerList = new ArrayList<Future<List<FileInfo>>>();
		// create result map
		HashMap<Integer, List<FileInfo>> resultMap = new HashMap<Integer, List<FileInfo>>();
		Set<Integer> shareRelationSet = keyRelation.getKeyShareMap().keySet();
		// Start the workers for all share relations
		for (int shareRelation : shareRelationSet)
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
				if (resultList != null && resultList.size() > 0)
				{
					resultMap.put(resultList.get(0).getContainerInfo().getShareRelationID(), resultList);
				}
			} catch (InterruptedException | ExecutionException e)
			{
				System.err.println("Worker List files interrupted");
				System.err.println(e.getMessage());
			} catch (NullPointerException e)
			{
				e.printStackTrace();
			}
		}
		waitExecutor(executor);

		System.out.println("Finished all threads");
		iFile.unmountFileSystem();
		return resultMap;
	}

	@Override
	public FileInfoEncrypt storeFileStructure(FileInfoEncrypt fileInfo, String key)
	{
		printStartToConsole("storeFileStructure", 1);
		try
		{
			iFile.encryptFile(fileInfo, key);
		} catch (FileException e)
		{
			System.err.println(e.getError());
			fileInfo.setError(e.getError());
		}
		iFile.unmountFileSystem();
		return fileInfo;
	}

	@Override
	public FileInfoDecrypt loadFileStructure(FileInfoDecrypt fileInfo, String key)
	{
		printStartToConsole("loadFileStructure", 1);
		try
		{
			iFile.decryptFile(fileInfo, key);
		} catch (FileException e)
		{
			System.err.println(e.getError());
			fileInfo.setError(e.getError());
		}
		iFile.unmountFileSystem();
		return fileInfo;
	}

	/**
	 * Gets the key for the current file info from the passed key relation. If the share relation is not contained in the key relation, a
	 * error gets set to the FileInfo. If the FileInfo is a FileInfoMove, the destination key gets returned
	 * 
	 * @param fileInfo current file info
	 * @param keyRelation relation of all keys
	 * @return key for the passed file info. If the FileInfo is a FileInfoMove, the destination key gets returned.
	 */
	private String getKey(FileInfo fileInfo, KeyRelation keyRelation)
	{
		return getKey(fileInfo.getContainerInfo().getShareRelationID(), fileInfo, keyRelation);
	}

	/**
	 * Gets the key for the current file info from the passed key relation. If the share relation is not contained in the key relation, a
	 * error gets set to the FileInfo. If the FileInfo is a FileInfoMove, the destination key gets returned
	 * 
	 * @param shareRelationID id of the share relation to determine the key
	 * @param fileInfo current file info
	 * @param keyRelation relation of all keys
	 * @return key for the passed file info. If the FileInfo is a FileInfoMove, the destination key gets returned.
	 */
	private String getKey(int shareRelationID, FileInfo fileInfo, KeyRelation keyRelation)
	{
		// Get key for the current share relation
		String key = keyRelation.getKeyOfShareRelation(shareRelationID);
		// Generate Error if share relation not found in key relation
		if (key == null)
		{
			fileInfo.setError(FileError.SHARE_NOT_FOUND, Integer.toString(shareRelationID));
		}
		return key;
	}

	/**
	 * Closes the executor and waits until all threads are finished
	 * 
	 * @param executor to wait for
	 */
	private void waitExecutor(ExecutorService executor)
	{
		// make the executor accept no new threads and finish all existing threads
		executor.shutdown();
		// Wait until all threads are finish
		while (!executor.isTerminated())
		{
		}
	}

	private void printStartToConsole(String methode, int cores)
	{

		System.out.println(Constants.CONSOLE_LIMITER);
		System.out.println(methode);
		System.out.println("Prozessor Threads: " + cores);
		System.out.println(Constants.CONSOLE_LIMITER);
	}

}
