// $codepro.audit.disable tooManyViolations
package ch.droptilllate.filesystem.concurrent;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import ch.droptilllate.filesystem.error.FileError;
import ch.droptilllate.filesystem.error.FileException;
import ch.droptilllate.filesystem.helper.TestHelper;
import ch.droptilllate.filesystem.info.FileInfoEncrypt;
import ch.droptilllate.filesystem.info.FileInfoMove;
import ch.droptilllate.filesystem.io.IFile;
import ch.droptilllate.filesystem.preferences.Constants;
import ch.droptilllate.filesystem.preferences.Options;
import ch.droptilllate.filesystem.truezip.FileHandler;
import ch.droptilllate.security.truezip.KeyManager1;
import de.schlichtherle.truezip.file.TArchiveDetector;
import de.schlichtherle.truezip.file.TConfig;

public class WorkerMoveTest
{
	private Options options;
	
	private IFile iFile = new FileHandler();
	
	private int shareRelationID = 4444;

	private File textFile;
	private String filenameTextFile = "test.txt";
	private String contentTextFile = "This is a test File";

	private String key1 = Constants.TEST_PASSWORD_1;
	private String key2 = Constants.TEST_PASSWORD_2;

	// TODO Add testcase for different src and dest keys

	@Rule
	public TestName name = new TestName();

	/**
	 * Constructor
	 */
	public WorkerMoveTest()
	{
		// initalize the config
		TConfig config = TConfig.get();
		// Configure custom application file format.
		TArchiveDetector tad = KeyManager1.getArchiveDetector(Constants.CONTAINER_EXTENTION, Constants.TEST_PASSWORD_1.toCharArray());
		config.setArchiveDetector(tad);
	}

	@Test
	public void testThread()
	{
		System.out.println(Constants.TESTCASE_LIMITER);
		System.out.println(this.getClass().getSimpleName() + ": " + name.getMethodName());

		int id = 1234;
		int contId = 9999;
		int shareRelationID2 = 5555;
		// Create share directory
		File shareDir = new File(TestHelper.getTestDir(), "share");
		shareDir.mkdir();
		// Create FileInfo
		FileInfoEncrypt fie = new FileInfoEncrypt(id, textFile.getAbsolutePath(), shareRelationID);
		fie.getContainerInfo().setContainerID(contId);
		// Add the text file
		try
		{
			iFile.encryptFile(fie, key1);
		} catch (FileException e1)
		{
			e1.printStackTrace();
		}
		iFile.unmountFileSystem();

		// move the file
		FileInfoMove fim = new FileInfoMove(id, fie.getSize(), fie.getContainerInfo().getShareRelationID(), contId,
				shareRelationID2);
		fim.getDestContainerInfo().setContainerID(contId);
		Thread thread = new Thread(new WorkerMove(fim, key1, key2));
		thread.start();
		try
		{
			thread.join();
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}

		iFile.unmountFileSystem();
		// file should not be longer in the source container
		assertFalse(iFile.isFileInContainer(fie, key1));
		iFile.unmountFileSystem();
		// check if its in the dest container
		assertTrue(iFile.isFileInContainer(fim, key2));
	}

	@Test
	public void testException()
	{
		System.out.println(Constants.TESTCASE_LIMITER);
		System.out.println(this.getClass().getSimpleName() + ": " + name.getMethodName());

		int id = 1234;
		int contId = 9999;
		int shareRelationID2 = 5555;
		// Create share directory
		File shareDir = new File(TestHelper.getTestDir(), "share");
		shareDir.mkdir();
		// Create FileInfo
		FileInfoEncrypt fie = new FileInfoEncrypt(id, textFile.getAbsolutePath(), shareRelationID);
		fie.getContainerInfo().setContainerID(contId);
		// Add the text file
		try
		{
			iFile.encryptFile(fie, key1);
		} catch (FileException e1)
		{
			e1.printStackTrace();
		}
		iFile.unmountFileSystem();

		// move the file
		FileInfoMove fim = new FileInfoMove(id + 1, fie.getSize(), fie.getContainerInfo().getShareRelationID(), contId,
				shareRelationID2);
		fim.getDestContainerInfo().setContainerID(contId);
		Thread thread = new Thread(new WorkerMove(fim, key1, key2));
		thread.start();
		try
		{
			thread.join();
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}

		assertTrue(fim.getError() == FileError.SRC_FILE_NOT_FOUND);
		iFile.unmountFileSystem();

	}

	@Before
	public void befor()
	{
		// create DIR
		TestHelper.setupTestDir();
		// create a new textfile
		textFile = TestHelper.createTextFile(filenameTextFile, contentTextFile);
		// set options
		options = Options.getInstance();
		options.setDroptilllatePath(TestHelper.getTestDir());
		options.setTempPath(TestHelper.getExtractDir());
	}

	@After
	public void after()
	{
		TestHelper.cleanTestDir();
	}

}
