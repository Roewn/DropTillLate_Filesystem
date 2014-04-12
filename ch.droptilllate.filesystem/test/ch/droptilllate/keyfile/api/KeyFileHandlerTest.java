package ch.droptilllate.keyfile.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import ch.droptilllate.filesystem.commons.OsHelper;
import ch.droptilllate.filesystem.helper.TestHelper;
import ch.droptilllate.filesystem.preferences.Constants;
import ch.droptilllate.keyfile.error.KeyFileError;
import ch.droptilllate.security.commons.KeyRelation;

public class KeyFileHandlerTest
{
	@Rule
	public TestName name = new TestName();

	private int shareRelationID1 = 1111;
	private int shareRelationID2 = 2222;
	private String keyFilePass1 = "a109729e7af32853003c6af0fead5ede";
	private String keyFilePass2 = "a7b324a28a46559dd7baee6f29e116a8";
	
	private KeyFileHandler kfh;
	
	public KeyFileHandlerTest(){
		kfh = new KeyFileHandler();
	}

	@Test
	public void testStoreAndLoad()
	{
		System.out.println(Constants.TESTCASE_LIMITER);
		System.out.println(this.getClass().getSimpleName() + ": " + name.getMethodName());
		
		KeyRelation kr1 = new KeyRelation();
		kr1.addKeyOfShareRelation(shareRelationID1, keyFilePass1);
		kr1.addKeyOfShareRelation(shareRelationID2, keyFilePass2);

		String path = TestHelper.getTestDir() + OsHelper.getDirLimiter() + "keyfile";
		
		KeyFileHandlingSummary kfhs = new KeyFileHandlingSummary();
		KeyFileError error = KeyFileError.NONE;
		
		// Store key relation
		kfh.storeKeyFile(path, Constants.TEST_PASSWORD_1, kr1);		
		assertEquals(error, KeyFileError.NONE);
		
		// Load key relation
		kfhs = kfh.loadKeyFile(path, Constants.TEST_PASSWORD_1);
		System.out.println(kfhs.getKeyRelation());
		assertEquals(kfhs.getKeyRelation(), kr1);
	}
	
	@Test
	public void testWrongKey()
	{
		System.out.println(Constants.TESTCASE_LIMITER);
		System.out.println(this.getClass().getSimpleName() + ": " + name.getMethodName());
		
		KeyRelation kr1 = new KeyRelation();
		kr1.addKeyOfShareRelation(shareRelationID1, keyFilePass1);
		kr1.addKeyOfShareRelation(shareRelationID2, keyFilePass2);

		String path = TestHelper.getTestDir() + OsHelper.getDirLimiter() + "keyfile";
		
		KeyFileHandlingSummary kfhs = new KeyFileHandlingSummary();
		KeyFileError error = KeyFileError.NONE;
		
		// Store key relation
		kfh.storeKeyFile(path, Constants.TEST_PASSWORD_1, kr1);		
		assertEquals(error, KeyFileError.NONE);
		
		// Load key relation
		kfhs = kfh.loadKeyFile(path, Constants.TEST_PASSWORD_2);
		assertTrue(kfhs.wrongKey());
	}
	
	@Test
	public void testEmptyShareKey()
	{
		System.out.println(Constants.TESTCASE_LIMITER);
		System.out.println(this.getClass().getSimpleName() + ": " + name.getMethodName());
		
		KeyRelation kr1 = new KeyRelation();
		kr1.addKeyOfShareRelation(shareRelationID1, keyFilePass1);
		kr1.addKeyOfShareRelation(shareRelationID2, null);

		String path = TestHelper.getTestDir() + OsHelper.getDirLimiter() + "keyfile";
		
		KeyFileHandlingSummary kfhs = new KeyFileHandlingSummary();
		KeyFileError error = KeyFileError.NONE;
		
		// Store key relation
		error = kfh.storeKeyFile(path, Constants.TEST_PASSWORD_1, kr1);		
		assertEquals(error, KeyFileError.LINE_WRITE_ERROR);
			
	}
	
	@Test
	public void testCorruptFileEntry()
	{
		System.out.println(Constants.TESTCASE_LIMITER);
		System.out.println(this.getClass().getSimpleName() + ": " + name.getMethodName());
		
		KeyRelation kr1 = new KeyRelation();
		kr1.addKeyOfShareRelation(shareRelationID1, keyFilePass1);
		kr1.addKeyOfShareRelation(shareRelationID2, "1");

		String path = TestHelper.getTestDir() + OsHelper.getDirLimiter() + "keyfile";
		
		KeyFileHandlingSummary kfhs = new KeyFileHandlingSummary();
		KeyFileError error = KeyFileError.NONE;
		
		// Store key relation
		kfh.storeKeyFile(path, Constants.TEST_PASSWORD_1, kr1);		
		assertEquals(error, KeyFileError.NONE);
		
		// Load key relation
		kfhs = kfh.loadKeyFile(path, Constants.TEST_PASSWORD_1);
		assertTrue(kfhs.getKeyFileErrorCount() == 1);
		assertTrue(kfhs.getKeyFileErrorList().contains(KeyFileError.LINE_FETCH_ERROR));		
	}

	@Before
	public void befor()
	{
		// create DIR
		TestHelper.setupTestDir();

	}

	@After
	public void after()
	{
		TestHelper.cleanTestDir();
	}

}
