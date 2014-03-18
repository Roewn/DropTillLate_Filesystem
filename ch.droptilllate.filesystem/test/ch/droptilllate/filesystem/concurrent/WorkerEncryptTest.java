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
import ch.droptilllate.filesystem.helper.TestHelper;
import ch.droptilllate.filesystem.info.FileInfoEncrypt;
import ch.droptilllate.filesystem.io.IFile;
import ch.droptilllate.filesystem.preferences.Constants;
import ch.droptilllate.filesystem.preferences.Options;
import ch.droptilllate.filesystem.truezip.FileHandler;

public class WorkerEncryptTest
{
	
	private Options options;
	private IFile iFile = new FileHandler();

	private File textFile;
	private String filenameTextFile = "test.txt";
	private String contentTextFile = "This is a test File";

	private String key1 = Constants.TEST_PASSWORD_1;

	@Rule
	public TestName name = new TestName();

	/**
	 * Constructor
	 */
	public WorkerEncryptTest()
	{
	}

	@Test
	public void testThread()
	{
		System.out.println(Constants.TESTCASE_LIMITER);
		System.out.println(this.getClass().getSimpleName() + ": " + name.getMethodName());

		int id = 1234;
		int contId = 9999;
		int shareRelationID = 4444;
		// Create FileInfo
		FileInfoEncrypt fie = new FileInfoEncrypt(id, textFile.getAbsolutePath(), shareRelationID);
		fie.getContainerInfo().setContainerID(contId);
		Thread thread = new Thread(new WorkerEncrypt(fie, key1));
		thread.start();
		try
		{
			thread.join();
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		assertTrue(iFile.isFileInContainer(fie, key1));
		iFile.unmountFileSystem();
	}

	@Test
	public void testException()
	{
		System.out.println(Constants.TESTCASE_LIMITER);
		System.out.println(this.getClass().getSimpleName() + ": " + name.getMethodName());

		int id = 1234;
		int contId = 9999;
		int shareRelationID = 4444;
		// Create FileInfo
		FileInfoEncrypt fie = new FileInfoEncrypt(id, textFile.getAbsolutePath() + "bla", shareRelationID);
		fie.getContainerInfo().setContainerID(contId);
		Thread thread = new Thread(new WorkerEncrypt(fie, key1));
		thread.start();

		try
		{
			thread.join();
		}

		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		iFile.unmountFileSystem();
		assertTrue(fie.getError() == FileError.SRC_FILE_NOT_FOUND);
		assertFalse(iFile.isFileInContainer(fie, key1));

		

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
