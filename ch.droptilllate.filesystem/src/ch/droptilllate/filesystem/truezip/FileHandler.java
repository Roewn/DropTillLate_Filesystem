package ch.droptilllate.filesystem.truezip;

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
import ch.droptilllate.filesystem.info.InfoHelper;
import ch.droptilllate.filesystem.io.FileException;
import ch.droptilllate.filesystem.io.IContainer;
import ch.droptilllate.filesystem.io.IFile;
import de.schlichtherle.truezip.file.TArchiveDetector;
import de.schlichtherle.truezip.file.TConfig;
import de.schlichtherle.truezip.file.TFile;
import de.schlichtherle.truezip.file.TVFS;
import de.schlichtherle.truezip.fs.FsSyncException;

public class FileHandler implements IFile
{
	private IContainer iContainer = new ContainerHandler();

	public FileHandler()
	{
		// TODO initalize the tillate archive detector globally

		// initalize the config
		TConfig config = TConfig.get();
		// Configure custom application file format.
		TArchiveDetector tad = KeyManager.getArchiveDetector(Constants.CONTAINER_EXTENTION);
		config.setArchiveDetector(tad);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.droptilllate.filesystem.io.IFile#encryptFile(ch.droptilllate.filesystem.info.FileInfoEncrypt, java.lang.String)
	 */
	@Override
	public synchronized void encryptFile(FileInfoEncrypt fileInfo, String key) throws FileException
	{

		TConfig config = TConfig.push();
		try
		{
			// Set the password for the current operation
			config.setArchiveDetector(KeyManager.getArchiveDetector(key.toCharArray()));

			TFile src = new TFile(fileInfo.getFullPlainFilePath());
			checkIfFileExists(src, FileError.SRC_FILE_NOT_FOUND);

			TFile dst = new TFile(fileInfo.getContainerInfo().getContainerPath(), Integer.toString(fileInfo.getFileID()));

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
		} finally
		{
			// Pop the current configuration off the inheritable thread local stack,
			// thereby reverting to the old default archive detector.
			config.close();			
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.droptilllate.filesystem.io.IFile#decryptFile(ch.droptilllate.filesystem.info.FileInfoDecrypt, java.lang.String)
	 */
	@Override
	public synchronized void decryptFile(FileInfoDecrypt fileInfo, String key) throws FileException
	{
		TConfig config = TConfig.push();
		try
		{
			// Set the password for the current operation
			config.setArchiveDetector(KeyManager.getArchiveDetector(key.toCharArray()));
			System.out.println(TConfig.get().getArchiveDetector());

			TFile src = new TFile(fileInfo.getContainerInfo().getContainerPath(), Integer.toString(fileInfo.getFileID()));

			createDir(fileInfo.getTempDirPath());
			TFile dst = new TFile(fileInfo.getTempDirPath(), fileInfo.getFileID() + Constants.EXT_LIMITER + fileInfo.getFileExtension());

			src.cp(dst);
			checkIfFileExists(dst, FileError.EXTRACTED_FILE_NOT_FOUND);

		} catch (IOException e)
		{
			if (e.getMessage().contains(Constants.EXC_IDENTIFIER_KEY))
			{
				throw new FileException(FileError.INVALID_KEY, e.getCause().toString());
			} else if (isFileNotFoundException(e))
			{
				throw new FileException(FileError.SRC_FILE_NOT_FOUND, fileInfo.getContainerInfo().getContainerPath()
						+ InfoHelper.getDirLimiter() + fileInfo.getFileID());
			} else
			{
				throw new FileException(FileError.IO_EXCEPTION, e.getMessage());
			}
		} catch (FileException e)
		{
			throw e;
		} catch (Exception e)
		{
			throw new FileException(FileError.UNKNOWN, e.getMessage());
		} finally
		{
			// Pop the current configuration off the inheritable thread local stack,
			// thereby reverting to the old default archive detector.
			config.close();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.droptilllate.filesystem.io.IFile#deleteFile(ch.droptilllate.filesystem.info.FileInfo)
	 */
	@Override
	public synchronized void deleteFile(FileInfo fileInfo, String key) throws FileException
	{
		// TODO delete dir if it contains no more containers
		TConfig config = TConfig.push();
		try
		{
			// Set the password for the current operation
			config.setArchiveDetector(KeyManager.getArchiveDetector(key.toCharArray()));

			TFile src = new TFile(fileInfo.getContainerInfo().getContainerPath(), Integer.toString(fileInfo.getFileID()));
			checkIfFileExists(src, FileError.SRC_FILE_NOT_FOUND);
			iContainer.checkForEmptyContainer(fileInfo.getContainerInfo());

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
		} finally
		{
			// Pop the current configuration off the inheritable thread local stack,
			// thereby reverting to the old default archive detector.
			config.close();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.droptilllate.filesystem.io.IFile#moveFile(ch.droptilllate.filesystem.info.FileInfoMove)
	 */
	@Override
	public synchronized void moveFile(FileInfoMove fileInfo, String srcKey, String dstKey) throws FileException
	{
		// TODO when moving files to another archive use the RDC methode

		TConfig config = TConfig.push();
		try
		{
			// Set the password for the current operation (Archive detector of the destination)
			config.setArchiveDetector(KeyManager.getArchiveDetector(dstKey.toCharArray()));

			// create the source file and pass the source archive detector with the source key
			TFile src = new TFile(fileInfo.getSrcContainerInfo().getContainerPath(), Integer.toString(fileInfo.getFileID()),
					KeyManager.getArchiveDetector(srcKey.toCharArray()));

			createDir(fileInfo.getDestContainerInfo().getShareRelationPath());
			TFile dst = new TFile(fileInfo.getDestContainerInfo().getContainerPath(), Integer.toString(fileInfo.getFileID()));

			src.mv(dst);

		} catch (IOException e)
		{
			if (e.getMessage().contains(Constants.EXC_IDENTIFIER_KEY))
			{
				throw new FileException(FileError.INVALID_KEY, e.getCause().toString());
			} else if (isFileNotFoundException(e))
			{
				throw new FileException(FileError.SRC_FILE_NOT_FOUND, fileInfo.getSrcContainerInfo().getContainerPath()
						+ InfoHelper.getDirLimiter() + fileInfo.getFileID());
			} else
			{
				throw new FileException(FileError.IO_EXCEPTION, e.getMessage());
			}
		} catch (Exception e)
		{
			throw new FileException(FileError.UNKNOWN, e.getMessage());
		} finally
		{
			// Pop the current configuration off the inheritable thread local stack,
			// thereby reverting to the old default archive detector.
			config.close();
		}

	}

	/**
	 * Checks if the File still exists in this Container
	 * 
	 * @param fileInfo Info of the File to check (including container id)
	 * @return true when file still exists
	 */
	public synchronized boolean checkFile(FileInfo fileInfo, String key)
	{
		TConfig config = TConfig.push();
		try
		{
			// Set the password for the current operation
			config.setArchiveDetector(KeyManager.getArchiveDetector(key.toCharArray()));

			for (FileInfo file : iContainer.listContainerContent(fileInfo.getContainerInfo()))
			{
				if (fileInfo.getFileID() == file.getFileID())
				{
					return true;
				}
			}

		} catch (FileException e)
		{
			System.err.println(e.getError());
		} finally
		{
			// Pop the current configuration off the inheritable thread local stack,
			// thereby reverting to the old default archive detector.
			config.close();
		}
		return false;

	}

	/**
	 * List the Containers of each passed fileinfo and list all the file in those containers to the console
	 * 
	 * @param fileInfos List of all fileInfos
	 */
	public synchronized void listFileAssignment(List<? extends FileInfo> fileInfos)
	{
		HashSet<ContainerInfo> containerInfos = new HashSet<ContainerInfo>();
		for (FileInfo fileInfo : fileInfos)
		{
			containerInfos.add(fileInfo.getContainerInfo());
		}
		for (ContainerInfo contInfo : containerInfos)
		{
			List<FileInfo> fileList = null;
			try
			{
				fileList = iContainer.listContainerContent(contInfo);
			} catch (FileException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.droptilllate.filesystem.io.IFile#umountFileSystem()
	 */
	@Override
	public synchronized void unmountFileSystem()
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
	private synchronized void checkIfFileExists(File file, FileError fileError) throws FileException
	{
		if (!file.exists())
		{
			throw new FileException(fileError, file.getAbsolutePath());
		}

	}

	/**
	 * Checks if the directory of the passed path exists, if not, it gets created.
	 * 
	 * @param path path of the directory
	 * @return true if the directory already existed, false if a new directory was created
	 */
	private synchronized void createDir(String path)
	{
		// create the dir file
		File directory = new File(path);
		// Create folder of share relation if it does not exists
		if (!directory.exists())
		{
			directory.mkdirs();
			System.out.println(Constants.CONSOLE_LIMITER);
			System.out.println("Directory created: " + directory.getAbsolutePath());
		}
	}

	private synchronized boolean isFileNotFoundException(Exception e)
	{
		if (e.getMessage().contains(Constants.EXC_IDENTIFIER_NOT_FOUND) || e.getMessage().contains(Constants.EXC_IDENTIFIER_MISSING))
		{
			return true;
		}
		return false;
	}
}