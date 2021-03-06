// $codepro.audit.disable tooManyViolations
package ch.droptilllate.filesystem.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import ch.droptilllate.filesystem.helper.TestHelper;
import ch.droptilllate.filesystem.info.FileInfoEncrypt;
import ch.droptilllate.filesystem.preferences.Constants;
import ch.droptilllate.security.truezip.KeyManager1;
import de.schlichtherle.truezip.file.TArchiveDetector;
import de.schlichtherle.truezip.file.TConfig;

public class ContainerManagerTest
{
	private ContainerManager containerManager;

	private File textFile;
	private File textFileBigger;
	private String filenameTextFile = "test.txt";
	private String filenameTextFileBigger = "testBigger.txt";
	private String contentTextFile = "This is a test File";
	
	 @Rule public TestName name = new TestName();

	public ContainerManagerTest()
	{
		containerManager = ContainerManager.getInstance();		

		// initalize the config
		TConfig config = TConfig.get();
		// Configure custom application file format.
		TArchiveDetector tad = KeyManager1.getArchiveDetector(Constants.CONTAINER_EXTENTION, Constants.TEST_PASSWORD_1.toCharArray());
		config.setArchiveDetector(tad);
	}

	@Test
	public void testNewContainerAssignment() {
		System.out.println(Constants.TESTCASE_LIMITER);
		System.out.println(this.getClass().getSimpleName()+": " + name.getMethodName());
		int id = 2222;
		int shareRelationID = 4444;
		// Create FileInfo
		FileInfoEncrypt fie = new FileInfoEncrypt(id, textFile.getAbsolutePath(), shareRelationID);
		
		List<FileInfoEncrypt> fileInfoList = new ArrayList<FileInfoEncrypt>();
		fileInfoList.add(fie);
		// get the gerated File Infos
		containerManager.assignContainerID(fileInfoList);
		assertFalse(fileInfoList.isEmpty()); // not Empty
		// check Container ID
		assertTrue(fileInfoList.get(0).getContainerInfo().getContainerID() >= Constants.STRUCT_CONT_ID);
		// Check Path
		assertEquals(fie.getContainerInfo().getShareRelationID(), fileInfoList.get(0).getContainerInfo()
				.getShareRelationID());
		// Check Size
		assertTrue(fileInfoList.get(0).getContainerInfo().getEstimatedContainerSize() == textFile.length());
	}

	@Test
	public void testExistingContainerID() {
		System.out.println(Constants.TESTCASE_LIMITER);
		System.out.println(this.getClass().getSimpleName()+": " + name.getMethodName());
		int id = 2222;
		int shareRelationID = 4444;
		int contId = 9999;
		// Create FileInfo
		FileInfoEncrypt fie = new FileInfoEncrypt(id, textFile.getAbsolutePath(), shareRelationID);
		fie.getContainerInfo().setContainerID(contId);
		
		List<FileInfoEncrypt> fileInfoList = new ArrayList<FileInfoEncrypt>();
		fileInfoList.add(fie);
		// get the gerated File Infos
		containerManager.assignContainerID(fileInfoList);
		assertFalse(fileInfoList.isEmpty()); // not Empty
		// check Container ID
		assertTrue(fileInfoList.get(0).getContainerInfo().getContainerID() == contId);
		// Check Path
		assertEquals(fie.getContainerInfo().getShareRelationID(), fileInfoList.get(0).getContainerInfo()
				.getShareRelationID());
		// Check Size
		// assertTrue(fileInfoList.get(0).getContainerInfo().getEstimatedContainerSize() == textFile.length());
	}

	@Test
	public void testContainerAssignmentOfMultipleFiles() {	
		System.out.println(Constants.TESTCASE_LIMITER);
		System.out.println(this.getClass().getSimpleName()+": " + name.getMethodName());
		int id1 = 1001;
		int id2 = 1002;
		int id3 = 2000;
		int shareRelationID = 4444;
		// Create FileInfo
		FileInfoEncrypt textFileInfo1 = new FileInfoEncrypt(id1, textFile.getAbsolutePath(), shareRelationID);
		FileInfoEncrypt textFileInfo2 = new FileInfoEncrypt(id2, textFile.getAbsolutePath(), shareRelationID);
		FileInfoEncrypt textFileInfoBigger = new FileInfoEncrypt(id3, textFileBigger.getAbsolutePath(), shareRelationID);
		

		// create FileInfoList
		List<FileInfoEncrypt> fileInfoList = new ArrayList<FileInfoEncrypt>();
		fileInfoList.add(textFileInfo1);
		fileInfoList.add(textFileInfo2);
		fileInfoList.add(textFileInfoBigger);

		// set Max Container Size
		containerManager.setMaxContainerSize(textFile.length() * 2 + 1);

		// get the gerated File Infos
		containerManager.assignContainerID(fileInfoList);

		assertFalse(fileInfoList.isEmpty()); // not Empty

		// check if both textfiles are in the same container
		assertTrue(fileInfoList.get(fileInfoList.indexOf(textFileInfo1)).getContainerInfo().getContainerID() == fileInfoList
				.get(fileInfoList.indexOf(textFileInfo2)).getContainerInfo().getContainerID());
		// Check Size of these container
		assertTrue(fileInfoList.get(fileInfoList.indexOf(textFileInfo1)).getContainerInfo().getEstimatedContainerSize() == (textFile
				.length() * 2));

		// check if the Bigger File is in another container
		assertFalse(fileInfoList.get(fileInfoList.indexOf(textFileInfo1)).getContainerInfo().getContainerID() == fileInfoList
				.get(fileInfoList.indexOf(textFileInfoBigger)).getContainerInfo().getContainerID());
		// Check Size of this container
		assertTrue(fileInfoList.get(fileInfoList.indexOf(textFileInfoBigger)).getContainerInfo().getEstimatedContainerSize() == textFileBigger
				.length());
	}
	
	@Before
	public void befor() {
		
			// create DIR
			TestHelper.setupTestDir();
			// create a new textfile
			textFile = TestHelper.createTextFile(filenameTextFile, contentTextFile);	
			// create Bigger textfile
			textFileBigger = TestHelper.createTextFile(filenameTextFileBigger, contentTextFile, 100);			
	}

	@After
	public void after() {
		TestHelper.cleanTestDir();
	}

}
