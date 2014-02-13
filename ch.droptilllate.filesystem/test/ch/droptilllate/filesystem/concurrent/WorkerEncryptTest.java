package ch.droptilllate.filesystem.concurrent;

import static org.junit.Assert.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import jdk.internal.jfr.events.FileWriteEvent;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import ch.droptilllate.filesystem.api.FileError;
import ch.droptilllate.filesystem.commons.Constants;
import ch.droptilllate.filesystem.info.FileInfoEncrypt;
import ch.droptilllate.filesystem.io.FileException;
import ch.droptilllate.filesystem.io.FileOperator;
import ch.droptilllate.filesystem.security.KeyManager;
import de.schlichtherle.truezip.file.TArchiveDetector;
import de.schlichtherle.truezip.file.TConfig;
import de.schlichtherle.truezip.file.TFile;

public class WorkerEncryptTest
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
	public WorkerEncryptTest()
	{
		// initalize the config
		TConfig config = TConfig.get();
		// Configure custom application file format.
		TArchiveDetector tad = KeyManager.getArchiveDetector(Constants.CONTAINER_EXTENTION, Constants.PASSWORD.toCharArray());
		config.setArchiveDetector(tad);
	}

	@Test
	public void testThread()
	{
		System.out.println(Constants.TESTCASE_LIMITER);
		System.out.println("\u001B[32m"+this.getClass().getSimpleName()+": " + name.getMethodName());
		
		int id = 1234;
		int contId = 9999;
		// Create FileInfo
		FileInfoEncrypt fie = new FileInfoEncrypt(id, textFile.getAbsolutePath(), testDir.getAbsolutePath());
		fie.getContainerInfo().setContainerID(contId);
		Thread thread = new Thread(new WorkerEncrypt(fie));
		thread.start();
		try
		{
			thread.join();
		} catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue(FileOperator.isFileInContainer(fie));
		FileOperator.umountFileSystem();

		
	}
	
	@Test
	public void testException()
	{
		System.out.println(Constants.TESTCASE_LIMITER);
		System.out.println(this.getClass().getSimpleName()+": " + name.getMethodName());
		
		int id = 1234;
		int contId = 9999;
		// Create FileInfo
		FileInfoEncrypt fie = new FileInfoEncrypt(id, textFile.getAbsolutePath()+"bla", testDir.getAbsolutePath());
		fie.getContainerInfo().setContainerID(contId);
		Thread thread = new Thread( new WorkerEncrypt(fie));
		thread.start();
		
		try
		{
			thread.join();
		} 
			
		catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertFalse(FileOperator.isFileInContainer(fie));
		assertTrue(fie.getError() == FileError.SRC_FILE_NOT_FOUND);
		FileOperator.umountFileSystem();

	}

	@Before
	public void befor()
	{
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
	public void after()
	{
		try
		{
			TFile.rm_r(new TFile(testDir.getAbsolutePath()));
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
