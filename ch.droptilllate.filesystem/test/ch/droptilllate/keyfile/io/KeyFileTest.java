package ch.droptilllate.keyfile.io;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import ch.droptilllate.filesystem.commons.OsHelper;
import ch.droptilllate.filesystem.helper.TestHelper;
import ch.droptilllate.filesystem.preferences.Constants;
import ch.droptilllate.filesystem.preferences.Options;
import ch.droptilllate.keyfile.error.KeyFileError;
import ch.droptilllate.keyfile.error.KeyFileException;
import ch.droptilllate.security.commons.KeyRelation;

public class KeyFileTest
{
	@Rule
	public TestName name = new TestName();

	private int shareRelationID1 = 1111;
	private int shareRelationID2 = 2222;
	private String keyFilePass1 = "a109729e7af32853003c6af0fead5ede";
	private String keyFilePass2 = "a7b324a28a46559dd7baee6f29e116a8";

	@Test
	public void testStoreAndLoad()
	{
		System.out.println(Constants.TESTCASE_LIMITER);
		System.out.println(this.getClass().getSimpleName() + ": " + name.getMethodName());

		KeyRelation kr1 = new KeyRelation();
		kr1.addKeyOfShareRelation(shareRelationID1, keyFilePass1);
		kr1.addKeyOfShareRelation(shareRelationID2, keyFilePass2);
		List<KeyFileError> errorList = new ArrayList<KeyFileError>();
		
		String path = TestHelper.getTestDir() + OsHelper.getDirLimiter() + "keyfile";
		// STORE keyfile
		try
		{
			KeyFile.store(path, Constants.TEST_PASSWORD_1, kr1);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		File keyFile = new File(path);
		assertTrue(keyFile.exists());

		// LOAD keyfile
		KeyRelation kr2 = new KeyRelation();
		try
		{
			kr2 = KeyFile.load(path, Constants.TEST_PASSWORD_1, errorList);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		System.out.println(kr2.getKeyShareMap());
		assertEquals(kr1, kr2);
	}

	@Test
	public void testWrongPassword()
	{
		System.out.println(Constants.TESTCASE_LIMITER);
		System.out.println(this.getClass().getSimpleName() + ": " + name.getMethodName());

		KeyRelation kr1 = new KeyRelation();
		kr1.addKeyOfShareRelation(shareRelationID1, keyFilePass1);
		kr1.addKeyOfShareRelation(shareRelationID2, keyFilePass2);
		List<KeyFileError> errorList = new ArrayList<KeyFileError>();
		
		String path = TestHelper.getTestDir() + OsHelper.getDirLimiter() + "keyfile";
		// STORE keyfile
		try
		{
			KeyFile.store(path, Constants.TEST_PASSWORD_1, kr1);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		File keyFile = new File(path);
		assertTrue(keyFile.exists());

		// LOAD keyfile with wrong password
		KeyRelation kr2 = new KeyRelation();
		KeyFileError error = KeyFileError.NONE;
		try
		{
			kr2 = KeyFile.load(path, Constants.TEST_PASSWORD_2, errorList);
		} catch (KeyFileException e)
		{
			error = e.getError();
		}
		assertEquals(error, KeyFileError.INVALID_KEY);
	}

	@Test
	public void testWrongPath()
	{
		System.out.println(Constants.TESTCASE_LIMITER);
		System.out.println(this.getClass().getSimpleName() + ": " + name.getMethodName());

		KeyRelation kr1 = new KeyRelation();
		kr1.addKeyOfShareRelation(shareRelationID1, keyFilePass1);
		kr1.addKeyOfShareRelation(shareRelationID2, keyFilePass2);
		KeyFileError error = KeyFileError.NONE;

		String path = TestHelper.getTestDir() + OsHelper.getDirLimiter() + "keyfile";
		// STORE keyfile
		try
		{
			KeyFile.store(path, Constants.TEST_PASSWORD_1, kr1);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		// LOAD wrong path
		path = null;
		try
		{
			KeyFile.store(path, Constants.TEST_PASSWORD_1, kr1);
		} catch (KeyFileException e)
		{
			System.err.println(e.getError());
			error = e.getError();
		}
		assertEquals(error, KeyFileError.FILE_INVALID_PATH);
		path = TestHelper.getTestDir() + OsHelper.getDirLimiter();
		try
		{
			KeyFile.store(path, Constants.TEST_PASSWORD_1, kr1);
		} catch (KeyFileException e)
		{
			System.err.println(e.getError());
			error = e.getError();
		}
		assertEquals(error, KeyFileError.FILE_INVALID_PATH);
	}

	@Test
	public void testFileNotFound()
	{
		System.out.println(Constants.TESTCASE_LIMITER);
		System.out.println(this.getClass().getSimpleName() + ": " + name.getMethodName());

		KeyRelation kr1 = new KeyRelation();
		kr1.addKeyOfShareRelation(shareRelationID1, keyFilePass1);
		kr1.addKeyOfShareRelation(shareRelationID2, keyFilePass2);
		List<KeyFileError> errorList = new ArrayList<KeyFileError>();
		KeyFileError error = KeyFileError.NONE;

		String path = TestHelper.getTestDir() + OsHelper.getDirLimiter() + "keyfile";
		// STORE keyfile
		try
		{
			KeyFile.store(path, Constants.TEST_PASSWORD_1, kr1);
		} catch (KeyFileException e)
		{
			System.err.println(e.getError());
		}
		// LOAD keyfile with wrong file name
		path = TestHelper.getTestDir() + OsHelper.getDirLimiter() + "wrongfilename";
		try
		{
			KeyFile.load(path, Constants.TEST_PASSWORD_1, errorList);
		} catch (KeyFileException e)
		{
			System.err.println(e.getError());
			error = e.getError();
		}
		assertEquals(error, KeyFileError.FILE_NOT_FOUND);
	}

	@Test
	public void testCorruptFileEmptyKey()
	{
		System.out.println(Constants.TESTCASE_LIMITER);
		System.out.println(this.getClass().getSimpleName() + ": " + name.getMethodName());

		KeyRelation kr1 = new KeyRelation();
		kr1.addKeyOfShareRelation(shareRelationID1, keyFilePass1);
		kr1.addKeyOfShareRelation(shareRelationID2, "");
		List<KeyFileError> errorList = new ArrayList<KeyFileError>();
		
		String path = TestHelper.getTestDir() + OsHelper.getDirLimiter() + "keyfile";
		// STORE keyfile with an empty key
		try
		{
			KeyFile.store(path, Constants.TEST_PASSWORD_1, kr1);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		File keyFile = new File(path);
		assertTrue(keyFile.exists());

		// LOAD keyfile
		KeyRelation kr2 = new KeyRelation();
		try
		{
			kr2 = KeyFile.load(path, Constants.TEST_PASSWORD_1, errorList);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		System.out.println(kr2.getKeyShareMap());
		assertTrue(kr2.getKeyShareMap().size() == 1);
		assertTrue(errorList.contains(KeyFileError.LINE_FETCH_ERROR));
	}

	@Test
	public void testEmptyKeyFile()
	{
		System.out.println(Constants.TESTCASE_LIMITER);
		System.out.println(this.getClass().getSimpleName() + ": " + name.getMethodName());
		KeyFileError error = KeyFileError.NONE;

		KeyRelation kr1 = new KeyRelation();
		String path = TestHelper.getTestDir() + OsHelper.getDirLimiter() + "keyfile";
		// STORE keyfile with an empty key
		try
		{
			KeyFile.store(path, Constants.TEST_PASSWORD_1, kr1);
		} catch (KeyFileException e)
		{
			System.err.println(e.getError());
			error = e.getError();
		}
		assertEquals(error, KeyFileError.EMPTY_KEYRELATION);

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
