package ch.droptilllate.filesystem.test;

import ch.droptilllate.filesystem.commons.Constants;
import ch.droptilllate.filesystem.info.FileInfoDecrypt;
import ch.droptilllate.filesystem.info.FileInfoEncrypt;
import ch.droptilllate.filesystem.io.FileException;
import ch.droptilllate.filesystem.io.IFile;
import ch.droptilllate.filesystem.truezip.FileHandler;
import ch.droptilllate.filesystem.truezip.KeyManager;
import de.schlichtherle.truezip.file.TConfig;

public class pieceTests
{
	private static IFile iFile = new FileHandler();
	static String testPath = "C:\\01_Development\\IndividualProject\\testfiles\\";
	static String extractPath = testPath + "extract\\";
	static FileInfoDecrypt fid;
	static Thread t1;
	static Thread t2;

	public static void main(String[] args)
	{
		System.out.println(TConfig.get().getArchiveDetector());
//		encrypt();

		System.out.println(TConfig.get().getArchiveDetector());
		System.out.println(KeyManager.getArchiveDetector(Constants.CONTAINER_EXTENTION, Constants.TEST_PASSWORD_1.toCharArray()));

		decrypt();
	}

	private static void encrypt()
	{
		FileInfoEncrypt fie = new FileInfoEncrypt(1234, testPath + "test.txt", testPath);
		fie.getContainerInfo().setContainerID(9999);
		try
		{
			iFile.encryptFile(fie, Constants.TEST_PASSWORD_1);
		} catch (FileException e)
		{
			System.out.println(e.getMessage());
		}
	}

	private static void decrypt()
	{
		// Create FileInfo
		fid = new FileInfoDecrypt(1234, "txt", testPath, testPath, 9999);
		// Extract File
		
		System.out.println("PW2");
		try
		{
			iFile.decryptFile(fid, Constants.TEST_PASSWORD_1);
		} catch (FileException e)
		{
			System.err.println(e.getError());
		}
	

		System.out.println("PW1");
		try
		{
			iFile.decryptFile(fid, "testest");
		} catch (FileException e)
		{
			System.err.println(e.getError());
		}
	}

		

}
