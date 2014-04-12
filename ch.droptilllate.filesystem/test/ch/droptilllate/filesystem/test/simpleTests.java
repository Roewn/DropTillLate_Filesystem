package ch.droptilllate.filesystem.test;


import java.io.File;
import java.io.IOException;


import java.nio.file.Files;






import java.util.ArrayList;
import java.util.List;





import ch.droptilllate.filesystem.error.FileException;
import ch.droptilllate.filesystem.helper.TestHelper;
import ch.droptilllate.filesystem.info.ContainerInfo;
import ch.droptilllate.filesystem.info.FileInfo;
import ch.droptilllate.filesystem.info.FileInfoDecrypt;
import ch.droptilllate.filesystem.info.FileInfoEncrypt;
import ch.droptilllate.filesystem.info.FileInfoMove;
import ch.droptilllate.filesystem.io.IFile;
import ch.droptilllate.filesystem.preferences.Constants;
import ch.droptilllate.filesystem.preferences.Options;
import ch.droptilllate.filesystem.truezip.FileHandler;



import ch.droptilllate.keyfile.io.KeyFile;
import ch.droptilllate.security.commons.KeyRelation;
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
//		Options options = Options.getInstance();
//		options.setDroptilllatePath(TestHelper.getTestDir());
//		options.setTempPath(TestHelper.getExtractDir());

		textFile = new File(testPath + "test.txt");

//		// encrypt();
//		iFile.unmountFileSystem();
//
//		// decrypt();
//		iFile.unmountFileSystem();
//
//		// move();
//		iFile.unmountFileSystem();
//		getFileSize();
		
		storeKeyFile();
		loadKeyFile();
		
		
				
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
	
	private static void storeKeyFile(){
		KeyRelation kr = new KeyRelation();
		kr.addKeyOfShareRelation(2, "share3erwe");
		kr.addKeyOfShareRelation(10, "share4werwer");
		String path = testPath + "keyfile";
		try
		{
			KeyFile.store(path, "anus", kr);
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void loadKeyFile(){
		KeyRelation kr = new KeyRelation();
		String path = testPath + "keyfile";
		try
		{
			KeyFile.load(path, "penis");
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
