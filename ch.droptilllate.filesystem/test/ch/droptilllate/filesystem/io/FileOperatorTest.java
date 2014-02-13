package ch.droptilllate.filesystem.io;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import ch.droptilllate.filesystem.commons.Constants;
import ch.droptilllate.filesystem.commons.Timer;
import ch.droptilllate.filesystem.info.ContainerInfo;
import ch.droptilllate.filesystem.info.FileInfo;
import ch.droptilllate.filesystem.info.FileInfoDecrypt;
import ch.droptilllate.filesystem.info.FileInfoEncrypt;
import ch.droptilllate.filesystem.info.FileInfoMove;
import ch.droptilllate.filesystem.info.InfoHelper;
import ch.droptilllate.filesystem.security.KeyManager;
import de.schlichtherle.truezip.file.TArchiveDetector;
import de.schlichtherle.truezip.file.TConfig;
import de.schlichtherle.truezip.file.TFile;

public class FileOperatorTest
{
	private static String workingDir = System.getProperty("user.dir");
	private static File testDir;

	private File textFile;
	private String filenameTextFile = "test.txt";
	private String contentTextFile = "This is a test File";
	
	 @Rule public TestName name = new TestName();

	/**
	 * Constructor
	 */
	public FileOperatorTest()
	{
		
		// initalize the config
		TConfig config = TConfig.get();
		// Configure custom application file format.
		TArchiveDetector tad = KeyManager.getArchiveDetector(Constants.CONTAINER_EXTENTION, Constants.PASSWORD.toCharArray());
		config.setArchiveDetector(tad);
	}

	@Test
	public void testAddAndExtractTextFile() {
		System.out.println(Constants.TESTCASE_LIMITER);
		System.out.println(this.getClass().getSimpleName()+": " + name.getMethodName());
		int id = 1234;
		int contId = 9999;
		// Create FileInfo
		FileInfoEncrypt fie = new FileInfoEncrypt(id, textFile.getAbsolutePath(), testDir.getAbsolutePath());
		fie.getContainerInfo().setContainerID(contId);
		// Adding File
		System.out.println(Constants.CONSOLE_LIMITER);
		System.out.println("Adding the text file");
		Timer.start();
		try
		{
			FileOperator.addFile(fie);
		} catch (FileException e)
		{
			System.out.println(e.getMessage());
		}
		Timer.stop(true);
		FileOperator.umountFileSystem();
		System.out.println("Size: " + (textFile.length() / 1024) + "kb");
		assertTrue(FileOperator.isFileInContainer(fie));

		// Create FileInfo
		FileInfoDecrypt fid = new FileInfoDecrypt(id, InfoHelper.checkFileExt(filenameTextFile), testDir.getAbsolutePath(),
				testDir.getAbsolutePath(), contId);
		// Extract File
		System.out.println();
		System.out.println("Extracting the text file");
		Timer.start();
		try
		{
			FileOperator.extractFile(fid);
		} catch (FileException e)
		{
			System.out.println(e.getMessage());
		}
		Timer.stop(true);
		System.out.println("Size: " + (textFile.length() / 1024) + "kb");
		assertTrue(getTextFileContent(textFile).equals(getTextFileContent(new File(fid.getFullTmpFilePath()))));
		FileOperator.umountFileSystem();

	}

	@Test
	public void deleteFile() {
		System.out.println(Constants.TESTCASE_LIMITER);
		System.out.println(this.getClass().getSimpleName()+": " + name.getMethodName());
		int id = 1234;
		int contId = 9999;
		System.out.println(Constants.CONSOLE_LIMITER);
		System.out.println("Deleting the text file");
		// Create FileInfo
		FileInfoEncrypt fie = new FileInfoEncrypt(id, textFile.getAbsolutePath(), testDir.getAbsolutePath());
		fie.getContainerInfo().setContainerID(contId);
		// Adding File
		try
		{
			FileOperator.addFile(fie);
		} catch (FileException e)
		{
			System.out.println(e.getMessage());
		}
		FileOperator.umountFileSystem();
		assertTrue(FileOperator.isFileInContainer(fie));

		// Create FileInfo to delete
		FileInfo fi = new FileInfo(id, new ContainerInfo(contId, testDir.getAbsolutePath()));
		try
		{
			FileOperator.deleteFile(fi);
		} catch (FileException e)
		{
			System.out.println(e.getMessage());
		}
		FileOperator.umountFileSystem();
		assertFalse(FileOperator.isFileInContainer(fie));

	}

	@Test
	public void moveFile() {
		System.out.println(Constants.TESTCASE_LIMITER);
		System.out.println(this.getClass().getSimpleName()+": " + name.getMethodName());
		int id = 1234;
		int contId = 9999;
		System.out.println(Constants.CONSOLE_LIMITER);
		System.out.println("Moving the text file");
		// Create two share directories
		// create DIR
		File shareDir1 = new File(testDir, "share1");
		shareDir1.mkdir();
		File shareDir2 = new File(testDir, "share2");
		shareDir2.mkdir();
		
		// Create FileInfo for adding to share1
		FileInfoEncrypt fie = new FileInfoEncrypt(id, textFile.getAbsolutePath(), shareDir1.getAbsolutePath());
		fie.getContainerInfo().setContainerID(contId);
		// Adding File
		try
		{
			FileOperator.addFile(fie);
		} catch (FileException e)
		{
			System.out.println(e.getMessage());
		}
		FileOperator.umountFileSystem();
		assertTrue(FileOperator.isFileInContainer(fie));

		
		// Create FileInfo to move the file to share2
		FileInfoMove fim = new FileInfoMove(id, textFile.length(), shareDir1.getAbsolutePath(), contId, shareDir2.getAbsolutePath());
		fim.getContainerInfo().setContainerID(contId);
		try
		{
			FileOperator.moveFile(fim);
		} catch (FileException e)
		{
			System.out.println(e.getMessage());
		}
		FileOperator.umountFileSystem();
		//file should not be longer in the source container
		assertFalse(FileOperator.isFileInContainer(fie));
		//check if its in the dest container
		assertTrue(FileOperator.isFileInContainer(fim));
	}

	@Before
	public void befor() {
		// create a new textfile
		try
		{
			// create DIR
			testDir = new File(workingDir, "tmpTest");
			testDir.mkdir();
			// create Test File
			textFile = new File(testDir.getAbsolutePath(), filenameTextFile);
			BufferedWriter writer = new BufferedWriter(new FileWriter(textFile));
			writer.write(contentTextFile);

			// close writer
			writer.close();
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@After
	public void after() {
		try
		{
			TFile.rm_r(new TFile(testDir.getAbsolutePath()));
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private String getTextFileContent(File file) {
		String thisLine;
		StringBuilder sb = null;
		try
		{
			FileInputStream fin = new FileInputStream(file);
			BufferedReader myInput = new BufferedReader(new InputStreamReader(fin));
			sb = new StringBuilder();
			while ((thisLine = myInput.readLine()) != null)
			{
				sb.append(thisLine);
			}
			myInput.close();
		} catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sb.toString();
	}

}
