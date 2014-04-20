package ch.droptilllate.filesystem.helper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.ArrayList;

import ch.droptilllate.filesystem.commons.OsHelper;
import ch.droptilllate.filesystem.preferences.Constants;
import de.schlichtherle.truezip.file.TFile;

public class TestHelper
{
	private static String workingDir = System.getProperty("user.dir");
	private static File testDir;
	private static String extractDir = "tmp";

	public static void setupTestDir()
	{
		// create DIR
		testDir = new File(workingDir, "tmpTest");
		testDir.mkdir();
	}

	public static void cleanTestDir()
	{
		try
		{
			TFile.rm_r(new TFile(testDir.getAbsolutePath()));
		} catch (IOException e)
		{
			System.err.println("Could not clean test directory: " + testDir.getAbsolutePath());
			System.err.println(e.getMessage());
		}
	}

	/**
	 * Creates test files in the test directory
	 * 
	 * @param count Number of files
	 * @param fileSize soze of the files in MB
	 * @param fileExtentions file extention without leading point Example: "txt"
	 * @return List of created files
	 */
	public static ArrayList<File> createFiles(int count, int fileSize, String fileExtentions)
	{
		ArrayList<File> fileList = new ArrayList<File>();
		for (int i = 0; i < count; i++)
		{
			File file = new File(testDir.getAbsolutePath(), (i + 1) + Constants.EXT_LIMITER + fileExtentions);
			RandomAccessFile raf = null;
			try
			{
				raf = new RandomAccessFile(file, "rw");
				raf.setLength(fileSize * 1024 * 1024);
				raf.close();
			} catch (FileNotFoundException e)
			{
				System.err.println(e.getMessage());
			} catch (IOException e)
			{
				System.err.println(e.getMessage());
			}
			fileList.add(file);
		}
		return fileList;
	}

	/**
	 * Create a new Textfile and writes the given content into it
	 * @param fileName Name of the text file (Example : "test.txt")
	 * @param content text to write
	 * @return created text File
	 */
	public static File createTextFile(String fileName, String content)
	{
		// create a new textfile
		File textFile = null;
		try
		{
			// create Test File
			textFile = new File(testDir.getAbsolutePath(), fileName);
			BufferedWriter writer = new BufferedWriter(new FileWriter(textFile));
			writer.write(content);

			// close writer
			writer.close();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return textFile;
	}
	
	/**
	 * Create a new Textfile and writes the given content into it
	 * @param fileName Name of the text file (Example : "test.txt")
	 * @param content text to write
	 * @return created text File
	 */
	public static File createTextFile(String fileName, String content, int repeatContent)
	{
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < repeatContent; i++) {
			sb.append(content);
		}
		return createTextFile(fileName, sb.toString());
	}
	
	/**
	 * Gets the content of the text file
	 * @param file text file
	 * @return content of text file
	 */
	public static String getTextFileContent(File file) {
		String thisLine;
		StringBuilder sb = null;
		try
		{
			FileInputStream fin = new FileInputStream(file);
			BufferedReader myInput = new BufferedReader(new InputStreamReader(fin));
			sb = new StringBuilder();
			while ((thisLine = myInput.readLine()) != null) // $codepro.audit.disable assignmentInCondition
			{
				sb.append(thisLine);
			}
			myInput.close();
		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		return sb.toString();
	}

	public static String getTestDir()
	{
		return testDir.getAbsolutePath();
	}

	public static String getExtractDir()
	{
		return testDir.getAbsolutePath() + OsHelper.getDirLimiter() + extractDir;
	}

}
