package ch.droptilllate.filesystem.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import ch.droptilllate.filesystem.commons.Constants;
import ch.droptilllate.filesystem.info.FileInfoEncrypt;
import ch.droptilllate.filesystem.security.KeyManager;
import de.schlichtherle.truezip.file.TArchiveDetector;
import de.schlichtherle.truezip.file.TConfig;
import de.schlichtherle.truezip.file.TFile;

public class ContainerManagerTest
{
	private ContainerManager containerManager;
	private static String workingDir = System.getProperty("user.dir");

	private static File testDir;

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
		TArchiveDetector tad = KeyManager.getArchiveDetector(Constants.CONTAINER_EXTENTION, Constants.PASSWORD.toCharArray());
		config.setArchiveDetector(tad);
	}

	@Test
	public void testNewContainerAssignment() {
		System.out.println(Constants.TESTCASE_LIMITER);
		System.out.println(this.getClass().getSimpleName()+": " + name.getMethodName());
		int id = 2222;
		// Create FileInfo
		FileInfoEncrypt fie = new FileInfoEncrypt(id, textFile.getAbsolutePath(), testDir.getAbsolutePath());
		
		List<FileInfoEncrypt> fileInfoList = new ArrayList<FileInfoEncrypt>();
		fileInfoList.add(fie);
		// get the gerated File Infos
		containerManager.assignContainerID(fileInfoList);
		assertFalse(fileInfoList.isEmpty()); // not Empty
		// check Container ID
		assertTrue(fileInfoList.get(0).getContainerInfo().getContainerID() >= Constants.MIN_RND);
		// Check Path
		assertEquals(fie.getContainerInfo().getParentContainerPath(), fileInfoList.get(0).getContainerInfo()
				.getParentContainerPath());
		// Check Size
		assertTrue(fileInfoList.get(0).getContainerInfo().getEstimatedContainerSize() == textFile.length());
	}

	@Test
	public void testExistingContainerID() {
		System.out.println(Constants.TESTCASE_LIMITER);
		System.out.println(this.getClass().getSimpleName()+": " + name.getMethodName());
		int id = 2222;
		int contId = 9999;
		// Create FileInfo
		FileInfoEncrypt fie = new FileInfoEncrypt(id, textFile.getAbsolutePath(), testDir.getAbsolutePath());
		fie.getContainerInfo().setContainerID(contId);
		
		List<FileInfoEncrypt> fileInfoList = new ArrayList<FileInfoEncrypt>();
		fileInfoList.add(fie);
		// get the gerated File Infos
		containerManager.assignContainerID(fileInfoList);
		assertFalse(fileInfoList.isEmpty()); // not Empty
		// check Container ID
		assertTrue(fileInfoList.get(0).getContainerInfo().getContainerID() == contId);
		// Check Path
		assertEquals(fie.getContainerInfo().getParentContainerPath(), fileInfoList.get(0).getContainerInfo()
				.getParentContainerPath());
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
		// Create FileInfo
		FileInfoEncrypt textFileInfo1 = new FileInfoEncrypt(id1, textFile.getAbsolutePath(), testDir.getAbsolutePath());
		FileInfoEncrypt textFileInfo2 = new FileInfoEncrypt(id2, textFile.getAbsolutePath(), testDir.getAbsolutePath());
		FileInfoEncrypt textFileInfoBigger = new FileInfoEncrypt(id3, textFileBigger.getAbsolutePath(), testDir.getAbsolutePath());
		

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
		try
		{
			// create DIR
			testDir = new File(workingDir, "tmpTest");
			testDir.mkdir();
			// create textfile
			textFile = new File(testDir.getAbsolutePath(), filenameTextFile);
			// write content
			BufferedWriter writer1 = new BufferedWriter(new FileWriter(textFile));
			writer1.write(contentTextFile);
			// close writer
			writer1.close();

			// create Bigger textfile
			textFileBigger = new File(testDir.getAbsolutePath(), filenameTextFileBigger);
			// write content
			PrintWriter writer2 = new PrintWriter(new FileWriter(textFileBigger));
			for (int i = 0; i < 100; i++)
			{
				writer2.println(contentTextFile);
				writer2.flush();
			}
			// close writer
			writer2.close();
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@After
	public void after() {
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
