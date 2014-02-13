package ch.droptilllate.filesystem.commons;

import static org.junit.Assert.assertEquals;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import ch.droptilllate.filesystem.info.InfoHelper;

public class InfoHelperTest
{
	
	private String testFileName = "test.tmp";
	 @Rule public TestName name = new TestName();


	@Test
	public void checkPath() {
		System.out.println(Constants.TESTCASE_LIMITER);
		System.out.println(this.getClass().getSimpleName()+": " + name.getMethodName());
		// Check path
		String path = InfoHelper.checkPath("Test" + InfoHelper.getDirLimiter());
		assertEquals("Test", path);

	}

	@Test
	public void checkFileExt() {
		System.out.println(Constants.TESTCASE_LIMITER);
		System.out.println(this.getClass().getSimpleName()+": " + name.getMethodName());
		// Check Extension
		assertEquals("tmp", InfoHelper.checkFileExt(".tmp"));
	}
	
	@Test
	public void createFullPath() {
		System.out.println(Constants.TESTCASE_LIMITER);
		System.out.println(this.getClass().getSimpleName()+": " + name.getMethodName());
		// Check Extension
		assertEquals("Test"+InfoHelper.getDirLimiter()+testFileName, InfoHelper.createFullPath("Test", "test", "tmp"));
	}

}
