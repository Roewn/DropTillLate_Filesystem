package ch.droptilllate.filesystem.concurrent;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import ch.droptilllate.filesystem.commons.Constants;
import ch.droptilllate.filesystem.helper.TestHelper;
import ch.droptilllate.filesystem.info.FileInfo;
import ch.droptilllate.filesystem.info.FileInfoEncrypt;
import ch.droptilllate.filesystem.io.FileException;
import ch.droptilllate.filesystem.io.IFile;
import ch.droptilllate.filesystem.truezip.FileHandler;

public class WorkerListFilesTest
{

	private IFile iFile = new FileHandler();

	private File textFile;
	private String filenameTextFile = "test.txt";
	private String contentTextFile = "This is a test File";

	private String key1 = Constants.TEST_PASSWORD_1;
	private String key2 = Constants.TEST_PASSWORD_2;

	// TODO Add testcase for different src and dest keys

	@Rule
	public TestName name = new TestName();

	@Test
	public void testThread()
	{
		System.out.println(Constants.TESTCASE_LIMITER);
		System.out.println(this.getClass().getSimpleName() + ": " + name.getMethodName());

		int id1 = 1111;
		int contId1 = 8888;
		int id2 = 2222;
		int contId2 = 9999;		
		
		// Create FileInfo
		FileInfoEncrypt fie1 = new FileInfoEncrypt(id1, textFile.getAbsolutePath(), TestHelper.getTestDir());
		fie1.getContainerInfo().setContainerID(contId1);
		// Create FileInfo
		FileInfoEncrypt fie2 = new FileInfoEncrypt(id2, textFile.getAbsolutePath(), TestHelper.getTestDir());
		fie2.getContainerInfo().setContainerID(contId2);
		// Add the text file
		try
		{
			iFile.encryptFile(fie1, key1);
			iFile.encryptFile(fie2, key1);
		} catch (FileException e1)
		{
			e1.printStackTrace();
		}
		iFile.unmountFileSystem();

		// list the files
		ExecutorService executor = Executors.newFixedThreadPool(1);
		Future<List<FileInfo>> worker;
		worker = executor.submit(new WorkerListFiles(TestHelper.getTestDir(), key1));

		List<FileInfo> resultList = null;
		try
		{
			resultList = worker.get();
		} catch (InterruptedException | ExecutionException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		iFile.unmountFileSystem();
		
		assertTrue(resultList.size() == 2);
		assertTrue(resultList.contains(fie1));
		assertTrue(resultList.contains(fie2));
	}

	@Test
	public void testException()
	{

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
