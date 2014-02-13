package ch.droptilllate.filesystem.commons;

public final class Constants
{
	// *****************************************************************************
	// TrueZip Parameters
	// *****************************************************************************
	public static final String CONTAINER_EXTENTION = "tilllate";
	public static final String DIR_LIMITER_WIN = "\\";
	public static final String DIR_LIMITER_MAC = "/";
	public static final String EXT_LIMITER = ".";
	// Size in Byte
	public static final long MAX_CONT_SIZE = 70000 * 1024;
	public static final long MIN_CONT_SIZE = 40000 * 1024;
	// Limits for the ConatinerID Generator
	// 100000 and 900000, means that the ID will have 6 digits
	public static final int MIN_RND = 100000;
	public static final int MAX_RND = 900000;

	// *****************************************************************************
	// Strings for Test Cases
	// *****************************************************************************
	public static final String PASSWORD = "12341234";

	// *****************************************************************************
	// Console
	// *****************************************************************************
	public static final String TIMER_MESSAGE = "Time elapsed: ";
	public static final String CONSOLE_LIMITER = "----------------------------------";
	public static final String TESTCASE_LIMITER = "**********************************";
	
	// *****************************************************************************
	// Exceptions
	// *****************************************************************************
	public static final String EXC_ISDIR = "Target is not a file, it is a directory";
}
