package ch.droptilllate.filesystem.io;

import static org.junit.Assert.*;

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
import ch.droptilllate.filesystem.info.InfoHelper;
import ch.droptilllate.filesystem.security.KeyRelation;
import ch.droptilllate.filesystem.truezip.FileHandler;
import ch.droptilllate.filesystem.truezip.KeyManager;
import de.schlichtherle.truezip.file.TArchiveDetector;
import de.schlichtherle.truezip.file.TConfig;

public class ShareRelationHandlerTest
{
	private ShareRelationHandler srh = new ShareRelationHandler();
	private IFile iFile = new FileHandler();
	
	private File textFile;
	private String filenameTextFile = "test.txt";
	private String contentTextFile = "This is a test File";
	
	@Rule public TestName name = new TestName();
	
	/**
	 * Constructor
	 */
	public ShareRelationHandlerTest()
	{
		// initalize the config
				TConfig config = TConfig.get();
				// Configure custom application file format.
				TArchiveDetector tad = KeyManager.getArchiveDetector(Constants.CONTAINER_EXTENTION, Constants.TEST_PASSWORD_1.toCharArray());
				config.setArchiveDetector(tad);
	}

	@Test
	public void testListFilesInShareRelation()
	{
		System.out.println(Constants.TESTCASE_LIMITER);
		System.out.println(this.getClass().getSimpleName()+": " + name.getMethodName());
		int id = 1234;
		int contId = 9999;
		String deleteDir = TestHelper.getTestDir()+InfoHelper.getDirLimiter();
		System.out.println(Constants.CONSOLE_LIMITER);
		System.out.println("Deleting the text file");
		// Create FileInfo
		FileInfoEncrypt fie = new FileInfoEncrypt(id, textFile.getAbsolutePath(), deleteDir);
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
				
		List<FileInfo> fil = srh.getFilesOfShareRelation(fie.getContainerInfo().getParentContainerPath(), Constants.TEST_PASSWORD_1);
		assertTrue(fil.size() == 1);
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
