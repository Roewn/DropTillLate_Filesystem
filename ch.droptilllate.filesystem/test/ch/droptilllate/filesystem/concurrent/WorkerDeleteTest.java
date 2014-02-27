package ch.droptilllate.filesystem.concurrent;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import ch.droptilllate.filesystem.api.FileError;
import ch.droptilllate.filesystem.commons.Constants;
import ch.droptilllate.filesystem.helper.TestHelper;
import ch.droptilllate.filesystem.info.FileInfo;
import ch.droptilllate.filesystem.info.FileInfoEncrypt;
import ch.droptilllate.filesystem.io.IFile;
import ch.droptilllate.filesystem.security.KeyRelation;
import ch.droptilllate.filesystem.truezip.FileHandler;
import ch.droptilllate.filesystem.truezip.KeyManager1;
import de.schlichtherle.truezip.file.TArchiveDetector;
import de.schlichtherle.truezip.file.TConfig;

public class WorkerDeleteTest
{
	private IFile iFile = new FileHandler();
	private KeyRelation kr1 = null;

	private File textFile;
	private String filenameTextFile = "test.txt";
	private String contentTextFile = "This is a test File";

	private String key1 = Constants.TEST_PASSWORD_1;

	@Rule
	public TestName name = new TestName();

	// TODO Remove TConfig from constructor and encrypt files by the encrypt worker

	/**
	 * Constructor
	 */
	public WorkerDeleteTest()
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
		// Create FileInfo
		FileInfoEncrypt fie = new FileInfoEncrypt(id, textFile.getAbsolutePath(), TestHelper.getTestDir());
		fie.getContainerInfo().setContainerID(contId);
		// Add the text file
		Thread thread1 = new Thread(new WorkerEncrypt(fie, key1));
		thread1.start();
		try
		{
			thread1.join();
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		iFile.unmountFileSystem();

		// delete the file
		FileInfo fi = new FileInfo(id, fie.getContainerInfo());
		Thread thread = new Thread(new WorkerDelete(fi, key1));
		thread.start();
		try
		{
			thread.join();
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}

		iFile.unmountFileSystem();
		assertFalse(iFile.checkFile(fie, key1));
	}

	@Test
	public void testException()
	{
		System.out.println(Constants.TESTCASE_LIMITER);
		System.out.println(this.getClass().getSimpleName() + ": " + name.getMethodName());

		int id = 1234;
		int contId = 9999;

		// Create FileInfo
		FileInfoEncrypt fie = new FileInfoEncrypt(id, textFile.getAbsolutePath(), TestHelper.getTestDir());
		fie.getContainerInfo().setContainerID(contId);
		// Add the text file
		Thread thread1 = new Thread(new WorkerEncrypt(fie, key1));
		thread1.start();
		try
		{
			thread1.join();
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		iFile.unmountFileSystem();

		// delete the file
		FileInfo fi = new FileInfo(id + 1, fie.getContainerInfo());
		Thread thread = new Thread(new WorkerDelete(fi, key1));
		thread.start();
		try
		{
			thread.join();
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}

		assertTrue(fi.getError() == FileError.SRC_FILE_NOT_FOUND);
		iFile.unmountFileSystem();

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
