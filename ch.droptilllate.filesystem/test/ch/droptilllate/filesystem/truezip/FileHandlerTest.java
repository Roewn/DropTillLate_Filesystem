package ch.droptilllate.filesystem.truezip;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import ch.droptilllate.filesystem.commons.Constants;
import ch.droptilllate.filesystem.commons.Timer;
import ch.droptilllate.filesystem.helper.TestHelper;
import ch.droptilllate.filesystem.info.ContainerInfo;
import ch.droptilllate.filesystem.info.FileInfo;
import ch.droptilllate.filesystem.info.FileInfoDecrypt;
import ch.droptilllate.filesystem.info.FileInfoEncrypt;
import ch.droptilllate.filesystem.info.FileInfoMove;
import ch.droptilllate.filesystem.info.InfoHelper;
import ch.droptilllate.filesystem.io.FileException;
import ch.droptilllate.filesystem.io.IFile;
import ch.droptilllate.filesystem.io.IShareRelation;
import ch.droptilllate.filesystem.io.ShareRelationHandler;
import de.schlichtherle.truezip.file.TArchiveDetector;
import de.schlichtherle.truezip.file.TConfig;

public class FileHandlerTest
{
	private IFile iFile = new FileHandler();
	private IShareRelation iShareRelation = new ShareRelationHandler(); 
	
	private File textFile;
	private String filenameTextFile = "test.txt";
	private String contentTextFile = "This is a test File";
	
	//TODO Add testcase for different src and dest keys for moving files
	
	 @Rule public TestName name = new TestName();

	/**
	 * Constructor
	 */
	public FileHandlerTest()
	{
		// initalize the config
				TConfig config = TConfig.get();
				// Configure custom application file format.
				TArchiveDetector tad = KeyManager.getArchiveDetector(Constants.CONTAINER_EXTENTION, Constants.TEST_PASSWORD_1.toCharArray());
				config.setArchiveDetector(tad);
	}

	@Test
	public void testAddAndExtractTextFile() {
		System.out.println(Constants.TESTCASE_LIMITER);
		System.out.println(this.getClass().getSimpleName()+": " + name.getMethodName());
		int id = 1234;
		int contId = 9999;
		// Create FileInfo
		FileInfoEncrypt fie = new FileInfoEncrypt(id, textFile.getAbsolutePath(), TestHelper.getTestDir());
		fie.getContainerInfo().setContainerID(contId);
		// Adding File
		System.out.println(Constants.CONSOLE_LIMITER);
		System.out.println("Adding the text file");
		Timer.start();
		try {
						
			iFile.encryptFile(fie, Constants.TEST_PASSWORD_1);
		} catch (FileException e)
		{
			System.out.println(e.getMessage());
		}
		Timer.stop(true);
		iFile.umountFileSystem();
		System.out.println("Size: " + (textFile.length() / 1024) + "kb");
		assertTrue(iFile.isFileInContainer(fie));

		// Create FileInfo
		FileInfoDecrypt fid = new FileInfoDecrypt(id, InfoHelper.checkFileExt(filenameTextFile), TestHelper.getTestDir(),
				TestHelper.getTestDir(), contId);
		// Extract File
		System.out.println();
		System.out.println("Extracting the text file");
		Timer.start();
		try
		{			
			iFile.decryptFile(fid, Constants.TEST_PASSWORD_2);
		} catch (FileException e)
		{
			System.out.println(e.getMessage());
		}
		Timer.stop(true);
		System.out.println("Size: " + (textFile.length() / 1024) + "kb");
		assertTrue(TestHelper.getTextFileContent(textFile).equals(TestHelper.getTextFileContent(new File(fid.getFullTmpFilePath()))));
		iFile.umountFileSystem();

	}

	@Test
	public void deleteFile() {
		System.out.println(Constants.TESTCASE_LIMITER);
		System.out.println(this.getClass().getSimpleName()+": " + name.getMethodName());
		int id = 1234;
		int contId = 9999;
		String deleteDir = TestHelper.getTestDir()+InfoHelper.getDirLimiter()+"dirToDelete";
		System.out.println(Constants.CONSOLE_LIMITER);
		System.out.println("Deleting the text file");
		// Create FileInfo
		FileInfoEncrypt fie = new FileInfoEncrypt(id, textFile.getAbsolutePath(), deleteDir);
		fie.getContainerInfo().setContainerID(contId);
		// Create the directories
		iShareRelation.checkIfDirectoryExists(fie.getContainerInfo().getParentContainerPath());
		
		// Adding File
		try
		{
			iFile.encryptFile(fie, Constants.TEST_PASSWORD_1);
		} catch (FileException e)
		{
			System.out.println(e.getMessage());
		}
		iFile.umountFileSystem();
		assertTrue(iFile.isFileInContainer(fie));

		// Create FileInfo to delete
		FileInfo fi = new FileInfo(id, new ContainerInfo(contId, deleteDir));
		try
		{
			iFile.deleteFile(fi, Constants.TEST_PASSWORD_1);
		} catch (FileException e)
		{
			System.out.println(e.getMessage());
		}
		iFile.umountFileSystem();
		assertFalse(iFile.isFileInContainer(fie));
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
		File shareDir1 = new File(TestHelper.getTestDir(), "share1");
		shareDir1.mkdir();
		File shareDir2 = new File(TestHelper.getTestDir(), "share2");
		shareDir2.mkdir();
		
		// Create FileInfo for adding to share1
		FileInfoEncrypt fie = new FileInfoEncrypt(id, textFile.getAbsolutePath(), shareDir1.getAbsolutePath());
		fie.getContainerInfo().setContainerID(contId);
		// Adding File
		try
		{
			iFile.encryptFile(fie, Constants.TEST_PASSWORD_1);
		} catch (FileException e)
		{
			System.out.println(e.getMessage());
		}
		iFile.umountFileSystem();
		assertTrue(iFile.isFileInContainer(fie));

		
		// Create FileInfo to move the file to share2
		FileInfoMove fim = new FileInfoMove(id, textFile.length(), shareDir1.getAbsolutePath(), contId, shareDir2.getAbsolutePath());
		fim.getContainerInfo().setContainerID(contId);
		try
		{
			iFile.moveFile(fim, Constants.TEST_PASSWORD_1);
		} catch (FileException e)
		{
			System.out.println(e.getMessage());
		}
		iFile.umountFileSystem();
		//file should not be longer in the source container
		assertFalse(iFile.isFileInContainer(fie));
		//check if its in the dest container
		assertTrue(iFile.isFileInContainer(fim));
	}

	@Before
	public void befor()
	{
		// create DIR
		TestHelper.setupTestDir();
		// create a new textfile
		textFile = TestHelper.createTextFile(filenameTextFile, contentTextFile);
	}

	@After
	public void after()
	{
		TestHelper.cleanTestDir();
	}

}
