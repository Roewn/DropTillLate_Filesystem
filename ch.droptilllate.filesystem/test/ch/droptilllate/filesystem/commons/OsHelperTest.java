package ch.droptilllate.filesystem.commons;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import ch.droptilllate.filesystem.error.FileException;
import ch.droptilllate.filesystem.preferences.Constants;

public class OsHelperTest
{

	private String testFileName = "test.tmp";
	private String containerPath;
	private int containerID = 111111;
	private int shareRelationID = 999999;

	@Rule
	public TestName name = new TestName();
	
	public OsHelperTest() {
		this.containerPath = "bla" + OsHelper.getDirLimiter() + "blub" + OsHelper.getDirLimiter() + 
				shareRelationID + OsHelper.getDirLimiter() +  containerID + Constants.EXT_LIMITER + Constants.CONTAINER_EXTENTION;
	}
	

	@Test
	public void checkPath()
	{
		System.out.println(Constants.TESTCASE_LIMITER);
		System.out.println(this.getClass().getSimpleName() + ": " + name.getMethodName());
		// Check path
		String path = OsHelper.checkPath("Test" + OsHelper.getDirLimiter());
		assertEquals("Test", path);

	}

	@Test
	public void checkFileExt()
	{
		System.out.println(Constants.TESTCASE_LIMITER);
		System.out.println(this.getClass().getSimpleName() + ": " + name.getMethodName());
		// Check Extension
		assertEquals("tmp", OsHelper.checkFileExt(".tmp"));
	}

	@Test
	public void createFullPath()
	{
		System.out.println(Constants.TESTCASE_LIMITER);
		System.out.println(this.getClass().getSimpleName() + ": " + name.getMethodName());
		// Check Extension
		assertEquals("Test" + OsHelper.getDirLimiter() + testFileName, OsHelper.createFullPath("Test", "test", "tmp"));
	}

	@Test
	public void extractContainerID()
	{
		System.out.println(Constants.TESTCASE_LIMITER);
		System.out.println(this.getClass().getSimpleName() + ": " + name.getMethodName());
		// Check for path
		try
		{
			assertTrue(OsHelper.extractContainerID(containerPath) == containerID);
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
			assertTrue(OsHelper.extractShareRelationID(containerPath) == shareRelationID);
		} catch (FileException e)
		{
			System.err.println(e.getError());
		}
	}

}
