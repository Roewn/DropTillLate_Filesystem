package ch.droptilllate.filesystem.api;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import ch.droptilllate.filesystem.commons.Constants;
import ch.droptilllate.filesystem.commons.Timer;
import ch.droptilllate.filesystem.helper.TestHelper;
import ch.droptilllate.filesystem.info.FileInfo;
import ch.droptilllate.filesystem.info.FileInfoDecrypt;
import ch.droptilllate.filesystem.info.FileInfoEncrypt;
import ch.droptilllate.filesystem.info.FileInfoMove;
import ch.droptilllate.filesystem.io.DirectoryOperator;
import ch.droptilllate.filesystem.io.FileOperator;

public class FileSystemHandlerTest
{

	@Rule
	public TestName name = new TestName();

	FileSystemHandler fsh = null;

	public FileSystemHandlerTest()
	{
		fsh = new FileSystemHandler();
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
			fieList.add(new FileInfoEncrypt(id, file.getAbsolutePath(), TestHelper.getTestDir()));
			id++;
		}

		// ******************************************************************
		// encrypt all files
		Timer.start();
		FileHandlingSummary fhs = fsh.encryptFiles(fieList);
		Timer.stop(true);
		// list the files
		FileOperator.listFileAssignment(fieList);
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
			fieList.add(new FileInfoEncrypt(id, file.getAbsolutePath(), TestHelper.getTestDir()));
			id++;
		}
		// encrypt all files
		fsh.encryptFiles(fieList);

		// create FileInfos Decrypt
		ArrayList<FileInfoDecrypt> fidList = new ArrayList<FileInfoDecrypt>();
		for (FileInfoEncrypt fie : fieList)
		{
			fidList.add(new FileInfoDecrypt(fie.getFileID(), ext, TestHelper.getExtractDir(), fie.getContainerInfo()
					.getParentContainerPath(), fie.getContainerInfo().getContainerID()));
		}

		// ******************************************************************
		// decrypt all Files
		Timer.start();
		FileHandlingSummary fhs = fsh.decryptFiles(fidList);
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
			assertTrue(extractFileList.contains(fid.getFullFileName()));
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
			fieList.add(new FileInfoEncrypt(id, file.getAbsolutePath(), TestHelper.getTestDir()));
			id++;
		}
		// encrypt all files
		fsh.encryptFiles(fieList);

		// create FileInfos Delete
		ArrayList<FileInfo> fiList = new ArrayList<FileInfo>();
		for (FileInfoEncrypt fie : fieList)
		{
			fiList.add(new FileInfo(fie.getFileID(), fie.getContainerInfo()));
		}

		// ******************************************************************
		// delete all Files
		Timer.start();
		FileHandlingSummary fhs = fsh.deleteFiles(fiList);
		Timer.stop(true);
		// test if the Summary is correct and the empty container got deleted
		for (FileInfo fi : fiList)
		{
			assertTrue(fhs.getFileInfoSuccessList().contains(fi));
			File cont = new File(fi.getContainerInfo().getFullContainerPath());
			assertFalse(cont.exists());
		}
	}

	@Test
	public void testMoveFiles()
	{
		int id = 1;
		int size = 1;
		int count = 4;
		String ext = "txt";

		System.out.println(Constants.TESTCASE_LIMITER);
		System.out.println(this.getClass().getSimpleName() + ": " + name.getMethodName());
		System.out.println(count + " Files a " + size + " MB");
		
		// Create share directory
		File shareDir = new File(TestHelper.getTestDir(), "share");
		shareDir.mkdir();

		// create Files
		ArrayList<File> fileList = TestHelper.createFiles(count, size, ext);

		// create FileInfos Encrypt
		ArrayList<FileInfoEncrypt> fieList = new ArrayList<FileInfoEncrypt>();
		for (File file : fileList)
		{
			fieList.add(new FileInfoEncrypt(id, file.getAbsolutePath(), TestHelper.getTestDir()));
			id++;
		}
		// encrypt all files
		fsh.encryptFiles(fieList);

		// create FileInfos Move
		ArrayList<FileInfoMove> fimList = new ArrayList<FileInfoMove>();
		for (FileInfoEncrypt fie : fieList)
		{
			fimList.add(new FileInfoMove(fie.getFileID(), fie.getSize(), fie.getContainerInfo().getParentContainerPath(), fie.getContainerInfo().getContainerID(), shareDir.getAbsolutePath()));
		}

		// ******************************************************************
		// move Files
		Timer.start();
		FileHandlingSummary fhs = fsh.moveFiles(fimList);
		Timer.stop(true);
		
		// get all files in the share directory
		List<FileInfo> fiShareList = DirectoryOperator.getAllEncryptedFilesInDir(shareDir.getAbsolutePath());
		// test if the Summary is ok and  all files are in the shared directory
		for (FileInfoMove fim : fimList)
		{
			assertTrue(fhs.getFileInfoSuccessList().contains(fim));
			assertTrue(fiShareList.contains(fim));
		}
	}

	@Before
	public void befor()
	{
		TestHelper.setupTestDir();
	}

	@After
	public void after()
	{
		TestHelper.cleanTestDir();
	}

}
