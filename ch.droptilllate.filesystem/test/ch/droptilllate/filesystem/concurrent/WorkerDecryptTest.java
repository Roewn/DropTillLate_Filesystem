package ch.droptilllate.filesystem.concurrent;

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
import ch.droptilllate.filesystem.info.FileInfoDecrypt;
import ch.droptilllate.filesystem.info.FileInfoEncrypt;
import ch.droptilllate.filesystem.io.IFile;
import ch.droptilllate.filesystem.security.KeyRelation;
import ch.droptilllate.filesystem.truezip.FileHandler;

public class WorkerDecryptTest
{
	private IFile iFile = new FileHandler();
	private KeyRelation kr1 = null;

	private File textFile;
	private String filenameTextFile = "test.txt";
	private String contentTextFile = "This is a test File";

	private String key1 = Constants.TEST_PASSWORD_1;
	private String key2 = Constants.TEST_PASSWORD_2;

	@Rule
	public TestName name = new TestName();

	/**
	 * Constructor
	 */
	public WorkerDecryptTest()
	{
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
		iFile.umountFileSystem();

		// Extract the file
		FileInfoDecrypt fid = new FileInfoDecrypt(id, "txt", TestHelper.getExtractDir(), TestHelper.getTestDir(), contId);
		Thread thread = new Thread(new WorkerDecrypt(fid, key1));
		thread.start();
		try
		{
			thread.join();
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		iFile.umountFileSystem();
		
		assertTrue(TestHelper.getTextFileContent(textFile).equals(TestHelper.getTextFileContent(new File(fid.getFullTmpFilePath()))));
		
	}

	@Test
	public void testNotFoundException()
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
		iFile.umountFileSystem();

		// Extract the file
		FileInfoDecrypt fid = new FileInfoDecrypt(id + 1, "txt", TestHelper.getExtractDir(), TestHelper.getTestDir(), contId);
		Thread thread = new Thread(new WorkerDecrypt(fid, key1));
		thread.start();
		try
		{
			thread.join();
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		iFile.umountFileSystem();
		
		assertTrue(fid.getError() == FileError.SRC_FILE_NOT_FOUND);
		
	}
	

	@Before
	public void befor()
	{
		// create DIR
		TestHelper.setupTestDir();
		// create a new textfile
		textFile = TestHelper.createTextFile(filenameTextFile, contentTextFile);
		// create key relation
		kr1 = new KeyRelation();
		kr1.addKeyOfShareRelation(TestHelper.getTestDir(), Constants.TEST_PASSWORD_1);
	}

	@After
	public void after()
	{
		TestHelper.cleanTestDir();
	}

}
