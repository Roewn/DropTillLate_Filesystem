package ch.droptilllate.filesystem.info;

import static org.junit.Assert.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import ch.droptilllate.filesystem.error.FileException;
import ch.droptilllate.filesystem.preferences.Constants;

public class InfoHelperTest
{

	private String testFileName = "test.tmp";
	private String containerPath;
	private int containerID = 111111;
	private int shareRelationID = 999999;

	@Rule
	public TestName name = new TestName();
	
	public InfoHelperTest() {
		this.containerPath = "bla" + InfoHelper.getDirLimiter() + "blub" + InfoHelper.getDirLimiter() + 
				shareRelationID + InfoHelper.getDirLimiter() +  containerID + Constants.EXT_LIMITER + Constants.CONTAINER_EXTENTION;
	}
	

	@Test
	public void checkPath()
	{
		System.out.println(Constants.TESTCASE_LIMITER);
		System.out.println(this.getClass().getSimpleName() + ": " + name.getMethodName());
		// Check path
		String path = InfoHelper.checkPath("Test" + InfoHelper.getDirLimiter());
		assertEquals("Test", path);

	}

	@Test
	public void checkFileExt()
	{
		System.out.println(Constants.TESTCASE_LIMITER);
		System.out.println(this.getClass().getSimpleName() + ": " + name.getMethodName());
		// Check Extension
		assertEquals("tmp", InfoHelper.checkFileExt(".tmp"));
	}

	@Test
	public void createFullPath()
	{
		System.out.println(Constants.TESTCASE_LIMITER);
		System.out.println(this.getClass().getSimpleName() + ": " + name.getMethodName());
		// Check Extension
		assertEquals("Test" + InfoHelper.getDirLimiter() + testFileName, InfoHelper.createFullPath("Test", "test", "tmp"));
	}

	@Test
	public void extractContainerID()
	{
		System.out.println(Constants.TESTCASE_LIMITER);
		System.out.println(this.getClass().getSimpleName() + ": " + name.getMethodName());
		// Check for path
		try
		{
			assertTrue(InfoHelper.extractContainerID(containerPath) == containerID);
		} catch (FileException e)
		{
			System.err.println(e.getError());
		}
	}
	
	@Test
	public void extractShareRelationID()
	{
		System.out.println(Constants.TESTCASE_LIMITER);
		System.out.println(this.getClass().getSimpleName() + ": " + name.getMethodName());
		// Check for path
		try
		{
			assertTrue(InfoHelper.extractShareRelationID(containerPath) == shareRelationID);
		} catch (FileException e)
		{
			System.err.println(e.getError());
		}
	}

}
