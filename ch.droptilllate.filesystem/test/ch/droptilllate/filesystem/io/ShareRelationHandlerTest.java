package ch.droptilllate.filesystem.io;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import ch.droptilllate.filesystem.commons.Constants;
import ch.droptilllate.filesystem.helper.TestHelper;
import ch.droptilllate.filesystem.info.FileInfo;
import ch.droptilllate.filesystem.info.FileInfoEncrypt;
import ch.droptilllate.filesystem.truezip.FileHandler;

public class ShareRelationHandlerTest
{
	private ShareRelationHandler srh = new ShareRelationHandler();
	private IFile iFile = new FileHandler();
	
	private File textFile;
	private String filenameTextFile = "test.txt";
	private String contentTextFile = "This is a test File";
	
	private String key1 = Constants.TEST_PASSWORD_1;
	private String key2 = Constants.TEST_PASSWORD_2;
	
	@Rule public TestName name = new TestName();
	
	/**
	 * Constructor
	 */
	public ShareRelationHandlerTest()
	{
	}

	@Test
	public void testListFilesInShareRelation()
	{
		System.out.println(Constants.TESTCASE_LIMITER);
		System.out.println(this.getClass().getSimpleName()+": " + name.getMethodName());
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
		} catch (FileException e)
		{
			System.out.println(e.getMessage());
		}
		iFile.unmountFileSystem();
		assertTrue(iFile.checkFile(fie1, key1));
		assertTrue(iFile.checkFile(fie2, key1));
				
		List<FileInfo> fil = srh.getFilesOfShareRelation(TestHelper.getTestDir(), key1);
		assertTrue(fil.size() == 2);	
		assertTrue(fil.contains(fie1));
		assertTrue(fil.contains(fie2));
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
