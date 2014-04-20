
package ch.droptilllate.filesystem.api;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import ch.droptilllate.filesystem.commons.OsHelper;
import ch.droptilllate.filesystem.commons.Timer;
import ch.droptilllate.filesystem.error.FileError;
import ch.droptilllate.filesystem.helper.TestHelper;
import ch.droptilllate.filesystem.info.FileInfo;
import ch.droptilllate.filesystem.info.FileInfoDecrypt;
import ch.droptilllate.filesystem.info.FileInfoEncrypt;
import ch.droptilllate.filesystem.info.FileInfoMove;
import ch.droptilllate.filesystem.io.IFile;
import ch.droptilllate.filesystem.preferences.Constants;
import ch.droptilllate.filesystem.truezip.FileHandler;
import ch.droptilllate.security.commons.KeyRelation;

public class FileSystemHandlerTest
{

	@Rule
	public TestName name = new TestName();

	private FileSystemHandler fsh = null;
	private KeyRelation krS = null;
	private int shareRelationID = 4444;
	private int shareRelationID2 = 5555;

	private IFile iFile = new FileHandler();

	File shareDir1;
	File shareDir2;

	// TODO Add testcase for different src and dest keys for moving files

	public FileSystemHandlerTest()
	{
		
	}

	@Test
	public void testEncryptFiles()
	{
		int id = 1;
		int size = 1;
		int count = 4;

		System.out.println(Constants.TESTCASE_LIMITER);
		System.out.println(this.getClass().getSimpleName() + ": " + name.getMethodName());
		System.out.println(count + " Files a " + size + " MB");

		// create Files
		ArrayList<File> fileList = TestHelper.createFiles(count, size, "txt");

		// create FileInfos Encrypt
		ArrayList<FileInfoEncrypt> fieList = new ArrayList<FileInfoEncrypt>();
		for (File file : fileList)
		{
			fieList.add(new FileInfoEncrypt(id, file.getAbsolutePath(), shareRelationID));
			id++;
		}

		// ******************************************************************
		// encrypt all files
		Timer.start();
		FileHandlingSummary fhs = fsh.encryptFiles(fieList, krS);
		Timer.stop(true);
		// list the files
		iFile.listFileAssignment(fieList, Constants.TEST_PASSWORD_1);
		// test if the Summary
		for (FileInfoEncrypt fie : fieList)
		{
			assertTrue(fhs.getFileInfoSuccessList().contains(fie));
		}
	}

	@Test
	public void testDecryptFiles()
	{
		int id = 1;
		int size = 1;
		int count = 4;
		String ext = "txt";

		System.out.println(Constants.TESTCASE_LIMITER);
		System.out.println(this.getClass().getSimpleName() + ": " + name.getMethodName());
		System.out.println(count + " Files a " + size + " MB");

		// create Files
		ArrayList<File> fileList = TestHelper.createFiles(count, size, ext);

		// create FileInfos Encrypt
		ArrayList<FileInfoEncrypt> fieList = new ArrayList<FileInfoEncrypt>();
		for (File file : fileList)
		{
			fieList.add(new FileInfoEncrypt(id, file.getAbsolutePath(), shareRelationID));
			id++;
		}
		// encrypt all files
		fsh.encryptFiles(fieList, krS);

		// create FileInfos Decrypt
		ArrayList<FileInfoDecrypt> fidList = new ArrayList<FileInfoDecrypt>();
		for (FileInfoEncrypt fie : fieList)
		{
			fidList.add(new FileInfoDecrypt(fie.getFileID(), ext, fie.getContainerInfo()
					.getShareRelationID(), fie.getContainerInfo().getContainerID()));
		}

		// ******************************************************************
		// decrypt all Files
		Timer.start();
		FileHandlingSummary fhs = fsh.decryptFiles(fidList, krS);
		Timer.stop(true);
		// test if the Summary
		for (FileInfoDecrypt fid : fidList)
		{
			assertTrue(fhs.getFileInfoSuccessList().contains(fid));
		}
		// test if all files are in the extracted directory
		File extractDir = new File(TestHelper.getExtractDir());
		List<String> extractFileList = Arrays.asList(extractDir.list());
		for (FileInfoDecrypt fid : fidList)
		{
			assertTrue(extractFileList.contains(fid.getPlainFileName()));
		}
	}
	
	@Test
	public void testDecryptFilesToAbsPath()
	{
		int id = 1;
		int size = 1;
		int count = 4;
		String ext = "txt";
		
		String extractPath = TestHelper.getTestDir() + OsHelper.getDirLimiter() + "myExtractDir" + OsHelper.getDirLimiter(); 

		System.out.println(Constants.TESTCASE_LIMITER);
		System.out.println(this.getClass().getSimpleName() + ": " + name.getMethodName());
		System.out.println(count + " Files a " + size + " MB");

		// create Files
		ArrayList<File> fileList = TestHelper.createFiles(count, size, ext);

		// create FileInfos Encrypt and decrypt
		ArrayList<FileInfoEncrypt> fieList = new ArrayList<FileInfoEncrypt>();
		ArrayList<FileInfoDecrypt> fidList = new ArrayList<FileInfoDecrypt>();
		for (File file : fileList)
		{
			fieList.add(new FileInfoEncrypt(id, file.getAbsolutePath(), shareRelationID));
			fidList.add(new FileInfoDecrypt(id, shareRelationID, 0, extractPath + file.getName()));
			id++;
		}
		// encrypt all files
		fsh.encryptFiles(fieList, krS);

		// Update FileInfos Decrypt		
		for (FileInfoEncrypt fie : fieList)
		{
			for (FileInfoDecrypt fid : fidList) {
				if (fie.getFileID() == fid.getFileID()) {
					fid.setContainerInfo(fie.getContainerInfo());
				}
			}
		}

		// ******************************************************************
		// decrypt all Files
		Timer.start();
		FileHandlingSummary fhs = fsh.decryptFiles(fidList, krS);
		Timer.stop(true);
		// test if the Summary
		for (FileInfoDecrypt fid : fidList)
		{
			assertTrue(fhs.getFileInfoSuccessList().contains(fid));
		}
		// test if all files are in the extracted directory
		File extractDir = new File(extractPath);
		List<String> extractFileList = Arrays.asList(extractDir.list());
		for (FileInfoDecrypt fid : fidList)
		{
			assertTrue(extractFileList.contains(fid.getPlainFileName()));
		}
	}

	@Test
	public void testDeleteFiles()
	{
		int id = 1;
		int size = 1;
		int count = 4;
		String ext = "txt";

		System.out.println(Constants.TESTCASE_LIMITER);
		System.out.println(this.getClass().getSimpleName() + ": " + name.getMethodName());
		System.out.println(count + " Files a " + size + " MB");

		// create Files
		ArrayList<File> fileList = TestHelper.createFiles(count, size, ext);

		// create FileInfos Encrypt
		ArrayList<FileInfoEncrypt> fieList = new ArrayList<FileInfoEncrypt>();
		for (File file : fileList)
		{
			fieList.add(new FileInfoEncrypt(id, file.getAbsolutePath(), shareRelationID));
			id++;
		}
		// encrypt all files
		fsh.encryptFiles(fieList, krS);

		// create FileInfos Delete
		ArrayList<FileInfo> fiList = new ArrayList<FileInfo>();
		for (FileInfoEncrypt fie : fieList)
		{
			fiList.add(new FileInfo(fie.getFileID(), fie.getContainerInfo()));
		}

		// ******************************************************************
		// delete all Files
		Timer.start();
		FileHandlingSummary fhs = fsh.deleteFiles(fiList, krS);
		Timer.stop(true);
		// test if the Summary is correct and the empty container got deleted
		for (FileInfo fi : fiList)
		{
			assertTrue(fhs.getFileInfoSuccessList().contains(fi));
			File cont = new File(fi.getContainerInfo().getContainerPath());
			assertFalse(cont.exists());
		}
	}

	@Test
	public void testMoveFiles()
	{
		int id = 1;
		int size = 1;
		int count = 4;
		int shareRelationID2 = 5555;
		String ext = "txt";

		System.out.println(Constants.TESTCASE_LIMITER);
		System.out.println(this.getClass().getSimpleName() + ": " + name.getMethodName());
		System.out.println(count + " Files a " + size + " MB");

		// create Files
		ArrayList<File> fileList = TestHelper.createFiles(count, size, ext);

		// create FileInfos Encrypt
		ArrayList<FileInfoEncrypt> fieList = new ArrayList<FileInfoEncrypt>();
		for (File file : fileList)
		{
			fieList.add(new FileInfoEncrypt(id, file.getAbsolutePath(), shareRelationID));
			id++;
		}
		// encrypt all files
		fsh.encryptFiles(fieList, krS);

		// create FileInfos Move
		ArrayList<FileInfoMove> fimList = new ArrayList<FileInfoMove>();
		for (FileInfoEncrypt fie : fieList)
		{
			fimList.add(new FileInfoMove(fie.getFileID(), fie.getSize(), fie.getContainerInfo().getShareRelationID(), fie
					.getContainerInfo().getContainerID(), shareRelationID2));
		}

		// ******************************************************************
		// move Files

		Timer.start();
		FileHandlingSummary fhs = fsh.moveFiles(fimList, krS);
		Timer.stop(true);

		// get all files in the share directory
		HashMap<Integer, List<FileInfo>> resultMap = fsh.getFilesPerRelation(krS);

		List<FileInfo> fiShareList = resultMap.get(shareRelationID2);

		// test if the Summary is ok and all files are in the shared directory
		for (FileInfoMove fim : fimList)
		{
			assertTrue(fhs.getFileInfoSuccessList().contains(fim));
			assertTrue(fiShareList.contains(fim));
		}

	}

	@Test
	public void testListFilesPerShareRelation()
	{
		int id = 1;
		int size = 1;
		int count = 4;
		int shareRelationID2 = 5555;
		String ext = "txt";

		System.out.println(Constants.TESTCASE_LIMITER);
		System.out.println(this.getClass().getSimpleName() + ": " + name.getMethodName());
		System.out.println(count + " Files a " + size + " MB");

		// create Files
		ArrayList<File> fileList = TestHelper.createFiles(count, size, ext);

		// create FileInfos Encrypt
		ArrayList<FileInfoEncrypt> fieList = new ArrayList<FileInfoEncrypt>();
		for (File file : fileList)
		{
			if (id <= 2)
			{
				fieList.add(new FileInfoEncrypt(id, file.getAbsolutePath(), shareRelationID));
			} else
			{
				fieList.add(new FileInfoEncrypt(id, file.getAbsolutePath(), shareRelationID2));
			}
			id++;
		}
		// encrypt all files
		fsh.encryptFiles(fieList, krS);

		// get all files in the share directory
		HashMap<Integer, List<FileInfo>> resultMap = fsh.getFilesPerRelation(krS);

		// check dirs
		List<FileInfo> resultList1 = resultMap.get(shareRelationID);
		List<FileInfo> resultList2 = resultMap.get(shareRelationID2);
		assertTrue(resultList1.size() == 2);
		assertTrue(resultList2.size() == 2);
		id = 1;
		for (FileInfo fi: fieList) {
			if (fi.getFileID() <= 2)
			{
				assertTrue(resultList1.contains(fi));
			} else
			{
				assertTrue(resultList2.contains(fi));
			}
			id++;			
		}
	}
	
	@Test
	public void testStoreAndLoadFileStructure() 
	{
		int id = 1;
		int contID = Constants.STRUCT_CONT_ID;
		String fileName = "test.xml";
		String content = "test entry";
		
		System.out.println(Constants.TESTCASE_LIMITER);
		System.out.println(this.getClass().getSimpleName() + ": " + name.getMethodName());
		// create Files
		File file = TestHelper.createTextFile(fileName, content);
		
		// Store XML
		FileInfoEncrypt fie = new FileInfoEncrypt(id, file.getAbsolutePath(), shareRelationID);
		fie.getContainerInfo().setContainerID(contID);
		
		fie = fsh.storeFileStructure(fie, Constants.TEST_PASSWORD_1);
		assertTrue(fie.getError() == FileError.NONE);
		
		// Load XML
		FileInfoDecrypt fid = new FileInfoDecrypt(id, "xml", fie.getContainerInfo()
				.getShareRelationID(), fie.getContainerInfo().getContainerID());
		fid = fsh.loadFileStructure(fid, Constants.TEST_PASSWORD_1);
		
		assertTrue(fid.getError() == FileError.NONE);
		
		assertTrue(TestHelper.getTextFileContent(file).equals(TestHelper.getTextFileContent(new File(fid.getFullTmpFilePath()))));

	}

	@Before
	public void befor()
	{
		TestHelper.setupTestDir();
		// create key relation
		// pass for the share relation
		krS = new KeyRelation();
		krS.addKeyOfShareRelation(shareRelationID, Constants.TEST_PASSWORD_1);
		krS.addKeyOfShareRelation(shareRelationID2, Constants.TEST_PASSWORD_1);
		
		fsh = new FileSystemHandler(TestHelper.getTestDir(), TestHelper.getExtractDir());
	}

	@After
	public void after()
	{
		TestHelper.cleanTestDir();
	}

}
