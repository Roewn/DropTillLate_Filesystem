package ch.droptilllate.filesystem.test;

import java.awt.TextField;
import java.io.File;

import com.sun.org.apache.bcel.internal.generic.IFLE;

import ch.droptilllate.filesystem.commons.Constants;
import ch.droptilllate.filesystem.helper.TestHelper;
import ch.droptilllate.filesystem.info.FileInfoDecrypt;
import ch.droptilllate.filesystem.info.FileInfoEncrypt;
import ch.droptilllate.filesystem.info.FileInfoMove;
import ch.droptilllate.filesystem.io.FileException;
import ch.droptilllate.filesystem.io.IFile;
import ch.droptilllate.filesystem.truezip.FileHandler;
import ch.droptilllate.filesystem.truezip.KeyManager;
import de.schlichtherle.truezip.file.TConfig;

public class simpleTests
{
	private static IFile iFile = new FileHandler();
	static String testPath = "C:\\01_Development\\IndividualProject\\testfiles\\";
	static String extractPath = testPath + "extract\\";
	static FileInfoDecrypt fid;
	static Thread t1;
	static Thread t2;

	static File textFile;
	static File shareDir1;
	
	public static void main(String[] args)
	{
		shareDir1 = new File(testPath, "share1");
		shareDir1.mkdir();
		textFile = new File(testPath + "test.txt");
		
//		encrypt();
		iFile.unmountFileSystem();
		
		decrypt();
		iFile.unmountFileSystem();
		
//		move();
		iFile.unmountFileSystem();
	}

	private static void encrypt()
	{
		FileInfoEncrypt fie = new FileInfoEncrypt(1234, textFile.getAbsolutePath(), testPath);
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
//		String dir = shareDir1.getAbsolutePath();
		String dir = testPath;
		// Create FileInfo
		fid = new FileInfoDecrypt(1234, "txt", dir, dir, 9999);
		// Extract File
	

		try
		{
			iFile.decryptFile(fid, Constants.TEST_PASSWORD_2);
		} catch (FileException e)
		{
			
			
			System.err.println(e.getError());
		}
		
	}
	
	private static void move() {		
		
		FileInfoMove fim = new FileInfoMove(1234, textFile.length(),testPath, 9999, shareDir1.getAbsolutePath());
		fim.getDestContainerInfo().setContainerID(8888);
		try
		{
			iFile.moveFile(fim, Constants.TEST_PASSWORD_1, Constants.TEST_PASSWORD_2);
		} catch (FileException e)
		{
			System.err.println(e.getError());
		}
		
		
		
	}

		

}
