package ch.droptilllate.filesystem.info;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import ch.droptilllate.filesystem.error.FileError;
import ch.droptilllate.filesystem.info.ContainerInfo;
import ch.droptilllate.filesystem.info.FileInfo;
import ch.droptilllate.filesystem.preferences.Constants;

public class FileInfoTest
{

	@Rule public TestName name = new TestName();
	 
	@Test
	public void testSimpleFileInfo()
	{
		System.out.println(Constants.TESTCASE_LIMITER);
		System.out.println(this.getClass().getSimpleName()+": " + name.getMethodName());
		int id = 123;
		FileInfo fi = new FileInfo(id);
		assertTrue(id == fi.getFileID());
	}
	
	@Test
	public void testContainerInfo()
	{
		System.out.println(Constants.TESTCASE_LIMITER);
		System.out.println(this.getClass().getSimpleName()+": " + name.getMethodName());
		int id = 123;
		int containerID = 9999;
		int shareIDRelation = 4444;
		FileInfo fi = new FileInfo(id, new ContainerInfo(containerID, shareIDRelation));
		assertTrue(containerID == fi.getContainerInfo().getContainerID());
		assertEquals(shareIDRelation, fi.getContainerInfo().getShareRelationID());		
	}
	
	@Test
	public void testError()
	{
		System.out.println(Constants.TESTCASE_LIMITER);
		System.out.println(this.getClass().getSimpleName()+": " + name.getMethodName());
		int id = 123;
		String testMsg = "Test";
		FileInfo fi = new FileInfo(id);
		fi.setError(FileError.DEST_FILE_NOT_FOUND);
		assertEquals(FileError.DEST_FILE_NOT_FOUND, fi.getError());
		fi.setError(FileError.IO_EXCEPTION, testMsg);
		assertEquals(FileError.IO_EXCEPTION, fi.getError());
		assertEquals(testMsg, fi.getErrorMessage());
	}

}
