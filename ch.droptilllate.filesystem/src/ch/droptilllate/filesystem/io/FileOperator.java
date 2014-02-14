package ch.droptilllate.filesystem.io;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;

import ch.droptilllate.filesystem.api.FileError;
import ch.droptilllate.filesystem.commons.Constants;
import ch.droptilllate.filesystem.info.ContainerInfo;
import ch.droptilllate.filesystem.info.FileInfo;
import ch.droptilllate.filesystem.info.FileInfoDecrypt;
import ch.droptilllate.filesystem.info.FileInfoEncrypt;
import ch.droptilllate.filesystem.info.FileInfoMove;
import de.schlichtherle.truezip.file.TFile;
import de.schlichtherle.truezip.file.TVFS;
import de.schlichtherle.truezip.fs.FsSyncException;

public class FileOperator
{
	// TODO when moving files to another archive use the RDC methode

	/**
	 * Adds and encrypts the specific File form the plain file path to the container contained in the fileInfo.
	 * 
	 * @param fileInfo Description of the file and the container.
	 * @throws FileException Throw when file could not be added.
	 */
	public static void addFile(FileInfoEncrypt fileInfo) throws FileException
	{
		try
		{
			TFile src = new TFile(fileInfo.getFullPlainFilePath());
			checkIfFileExists(src, FileError.SRC_FILE_NOT_FOUND);

			TFile dst = new TFile(fileInfo.getContainerInfo().getFullContainerPath(), Integer.toString(fileInfo.getFileID()));

			// check if it is a File and not an directory
			if (src.isDirectory())
			{
				throw new FileException(FileError.SRC_FILE_IS_A_DIR, src.getAbsolutePath());
			} else
			{

				src.cp(dst);

			}
		} catch (IOException e)
		{
			throw new FileException(FileError.IO_EXCEPTION, e.getMessage());
		} catch (FileException e)
		{
			throw e;
		} catch (Exception e)
		{
			throw new FileException(FileError.UNKNOWN, e.getMessage());
		}
	}

	/**
	 * Loads and encrypts the specific file from the container to the plain file path contained in the fileInfo.
	 * 
	 * @param fileInfo Description of the file and the container.
	 * @return the updated FileInfo with the plain file path and the file extension.
	 * @throws FileException Throw when file could not be loaded.
	 */
	public static void extractFile(FileInfoDecrypt fileInfo) throws FileException
	{
		try
		{
			TFile src = new TFile(fileInfo.getContainerInfo().getFullContainerPath(), Integer.toString(fileInfo.getFileID()));
			checkIfFileExists(src, FileError.SRC_FILE_NOT_FOUND);

			DirectoryOperator.checkIfDirectoryExists(fileInfo.getTempDirPath());
			TFile dst = new TFile(fileInfo.getTempDirPath(), fileInfo.getFileID() + Constants.EXT_LIMITER + fileInfo.getFileExtension());

			src.cp(dst);
			checkIfFileExists(dst, FileError.EXTRACTED_FILE_NOT_FOUND);

		} catch (IOException e)
		{
			throw new FileException(FileError.IO_EXCEPTION, e.getMessage());
		} catch (FileException e)
		{
			throw e;
		} catch (Exception e)
		{
			throw new FileException(FileError.UNKNOWN, e.getMessage());
		}
	}

	/**
	 * Removes the passed file from its container.
	 * 
	 * @param fileInfo Info of the file including container id
	 * @throws FileException Throw when file could not be deleted.
	 */
	public static void deleteFile(FileInfo fileInfo) throws FileException
	{
		// TODO delete dir if it contains no more containers
		try
		{
			TFile src = new TFile(fileInfo.getContainerInfo().getFullContainerPath(), Integer.toString(fileInfo.getFileID()));
			checkIfFileExists(src, FileError.SRC_FILE_NOT_FOUND);
			ContainerOperator.checkForEmptyContainer(fileInfo.getContainerInfo());

			src.rm();

		} catch (IOException e)
		{
			throw new FileException(FileError.IO_EXCEPTION, e.getMessage());
		} catch (FileException e)
		{
			throw e;
		} catch (Exception e)
		{
			throw new FileException(FileError.UNKNOWN, e.getMessage());
		}
	}

	/**
	 * Moves the file from the src to the dest Container
	 * 
	 * @param fileinfo Info of the file including source and destination container
	 * @throws FileException Throw when file could not be moved.
	 */
	public static void moveFile(FileInfoMove fileInfo) throws FileException
	{
		try
		{
			// TODO Check if the whole container can be moved instead of reencrypt the files (Performance check!)
			TFile src = new TFile(fileInfo.getSrcContainerInfo().getFullContainerPath(), Integer.toString(fileInfo.getFileID()));
			checkIfFileExists(src, FileError.SRC_FILE_NOT_FOUND);

			DirectoryOperator.checkIfDirectoryExists(fileInfo.getDestContainerInfo().getParentContainerPath());
			TFile dst = new TFile(fileInfo.getDestContainerInfo().getFullContainerPath(), Integer.toString(fileInfo.getFileID()));

			src.mv(dst);

		} catch (IOException e)
		{
			throw new FileException(FileError.IO_EXCEPTION, e.getMessage());
		} catch (FileException e)
		{
			throw e;
		} catch (Exception e)
		{
			throw new FileException(FileError.UNKNOWN, e.getMessage());
		}
	}

	/**
	 * Checks if the File still exists in this Container
	 * 
	 * @param fileInfo Info of the File to check (including container id)
	 * @return true when file still exists
	 */
	public synchronized static boolean isFileInContainer(FileInfo fileInfo)
	{
		try
		{
			for (FileInfo file : ContainerOperator.listContainerContent(fileInfo.getContainerInfo()))
			{
				if (fileInfo.getFileID() == file.getFileID())
				{
					return true;
				}
			}
			return false;

		} catch (Exception e)
		{
			return false;
		}

	}

	/**
	 * List the Containers of each passed fileinfo and list all the file in those containers to the console
	 * 
	 * @param fileInfos List of all fileInfos
	 */
	public synchronized static void listFileAssignment(List<? extends FileInfo> fileInfos)
	{
		HashSet<ContainerInfo> containerInfos = new HashSet<ContainerInfo>();
		for (FileInfo fileInfo : fileInfos)
		{
			containerInfos.add(fileInfo.getContainerInfo());
		}
		for (ContainerInfo contInfo : containerInfos)
		{
			List<FileInfo> fileList = ContainerOperator.listContainerContent(contInfo);
			System.out.println(Constants.CONSOLE_LIMITER);
			System.out.println("Container: " + contInfo.getContainerID());
			try
			{
				for (FileInfo file : fileList)
				{
					System.out.println("-> " + file.getFileID());
				}
			} catch (Exception e1)
			{
			}
		}
	}

	/**
	 * Commits all pending changes for all (nested) archive files to their respective parent file system, closes their associated target
	 * archive file in order to allow access by third parties (e.g. other processes), cleans up any temporary allocated resources (e.g.
	 * temporary files) and purges any cached data.
	 */
	public static void umountFileSystem()
	{
		try
		{
			TVFS.umount();
		} catch (FsSyncException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Checks if the file exists and trows the passed FileError if not.
	 * 
	 * @param file File to check
	 * @param fileError FileError which is thrown by the exception.
	 * @throws FileException Throw when file is corrupt.
	 */
	private synchronized static void checkIfFileExists(File file, FileError fileError) throws FileException
	{
		if (!file.exists())
		{
			throw new FileException(fileError, file.getAbsolutePath());
		}

	}
}