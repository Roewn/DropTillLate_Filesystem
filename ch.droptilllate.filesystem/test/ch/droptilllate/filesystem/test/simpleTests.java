package ch.droptilllate.filesystem.test;

import java.awt.TextField;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchEvent.Modifier;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.sun.org.apache.bcel.internal.generic.IFLE;

import ch.droptilllate.filesystem.error.FileException;
import ch.droptilllate.filesystem.helper.TestHelper;
import ch.droptilllate.filesystem.info.ContainerInfo;
import ch.droptilllate.filesystem.info.FileInfo;
import ch.droptilllate.filesystem.info.FileInfoDecrypt;
import ch.droptilllate.filesystem.info.FileInfoEncrypt;
import ch.droptilllate.filesystem.info.FileInfoMove;
import ch.droptilllate.filesystem.io.IContainer;
import ch.droptilllate.filesystem.io.IFile;
import ch.droptilllate.filesystem.preferences.Constants;
import ch.droptilllate.filesystem.preferences.Options;
import ch.droptilllate.filesystem.truezip.ContainerHandler;
import ch.droptilllate.filesystem.truezip.FileHandler;
import ch.droptilllate.filesystem.truezip.KeyManager1;
import de.schlichtherle.truezip.file.TArchiveDetector;
import de.schlichtherle.truezip.file.TConfig;
import de.schlichtherle.truezip.file.TFile;
import de.schlichtherle.truezip.nio.file.TPath;

public class simpleTests
{
	private static IFile iFile = new FileHandler();
	static String testPath = "C:\\01_Development\\IndividualProject\\testfiles\\";
	static String extractPath = testPath + "extract\\";
	static FileInfoDecrypt fid;
	static Thread t1;
	static Thread t2;
	static int share1 = 4444;

	static File textFile;
	static File shareDir1;

	public static void main(String[] args)
	{
		// set options
		Options options = Options.getInstance();
		options.setDroptilllatePath(TestHelper.getTestDir());
		options.setTempPath(TestHelper.getExtractDir());

		textFile = new File(testPath + "test.txt");

		// encrypt();
		iFile.unmountFileSystem();

		// decrypt();
		iFile.unmountFileSystem();

		// move();
		iFile.unmountFileSystem();

		getFileSize();
	}

	private static void encrypt()
	{
		FileInfoEncrypt fie = new FileInfoEncrypt(1234, textFile.getAbsolutePath(), share1);
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
		// String dir = shareDir1.getAbsolutePath();
		String dir = testPath;
		// Create FileInfo
		fid = new FileInfoDecrypt(1234, "txt", share1, 9999);
		// Extract File

		try
		{
			iFile.decryptFile(fid, Constants.TEST_PASSWORD_1);
		} catch (FileException e)
		{

			System.err.println(e.getError());
		}
	}

	private static void move()
	{
		int share2 = 5555;
		FileInfoMove fim = new FileInfoMove(1234, textFile.length(), share1, 9999, share2);
		fim.getDestContainerInfo().setContainerID(8888);
		try
		{
			iFile.moveFile(fim, Constants.TEST_PASSWORD_1, Constants.TEST_PASSWORD_2);
		} catch (FileException e)
		{
			System.err.println(e.getError());
		}
	}

	private static void getFileSize()
	{
		FileInfo fi = new FileInfo(1234, new ContainerInfo(9999, share1));

		IFile ifile = new FileHandler();
		List<FileInfo> fil = new ArrayList<FileInfo>();
		fil.add(fi);
		ifile.listFileAssignment(fil, Constants.TEST_PASSWORD_1);

		TFile file = new TFile(fi.getContainerInfo().getContainerPath(), Integer.toString(fi.getFileID()));
		System.out.println(file.getAbsolutePath());
		try
		{
			TPath path = new TPath(file);
			System.out.println("Size: " + Files.size(path));
			System.out.println("Size: " + file.length());

		} catch (IOException e)
		{
			e.printStackTrace();
		}

	}

}
