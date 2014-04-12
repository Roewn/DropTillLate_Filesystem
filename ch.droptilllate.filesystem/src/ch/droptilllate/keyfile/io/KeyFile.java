/*
 * Copyright (C) 2005-2013 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package ch.droptilllate.keyfile.io;

import de.schlichtherle.truezip.crypto.raes.RaesKeyException;
import de.schlichtherle.truezip.crypto.raes.RaesOutputStream;
import de.schlichtherle.truezip.crypto.raes.RaesParameters;
import de.schlichtherle.truezip.crypto.raes.RaesReadOnlyFile;
import de.schlichtherle.truezip.crypto.raes.param.KeyManagerRaesParameters;
import de.schlichtherle.truezip.file.*;
import de.schlichtherle.truezip.key.sl.KeyManagerLocator;
import de.schlichtherle.truezip.rof.DefaultReadOnlyFile;
import de.schlichtherle.truezip.rof.ReadOnlyFile;
import de.schlichtherle.truezip.rof.ReadOnlyFileInputStream;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Set;

import ch.droptilllate.filesystem.commons.OsHelper;
import ch.droptilllate.filesystem.commons.OsUtils;
import ch.droptilllate.filesystem.error.FileError;
import ch.droptilllate.filesystem.error.FileException;
import ch.droptilllate.filesystem.preferences.Constants;
import ch.droptilllate.keyfile.error.KeyFileError;
import ch.droptilllate.keyfile.error.KeyFileException;
import ch.droptilllate.security.commons.KeyRelation;
import ch.droptilllate.security.truezip.CustomRaesParameters;
import ch.droptilllate.security.truezip.KeyManager1;

/**
 * Saves and restores the contents of arbitrary files to and from the RAES file format for encryption and decryption. This class cannot get
 * instantiated outside its package.
 * <p>
 * Note that this class is not intended to access RAES encrypted ZIP files - use the {@link TFile} class for this task instead.
 * 
 * @author Christian Schlichtherle
 */
public class KeyFile
{
	private final static String ENTRY_DIVIDER = "\t";

	private KeyFile()
	{
	}

	/**
	 * Stores and encrypted the passed key relations in the keyfile and encrypts it with the passed key.
	 * 
	 * @param keyFilePath path to the keyfile, Example: "C:\\DropTillLateApplication\\keyfile"
	 * @param key Key used for encrypting the keyfile.
	 * @param keyRealtion containing all keys per share relation in the specified keyfile.
	 * @return KeyFileError, if no error occurred than getError() == KeyFileError.NONE
	 */
/**
 * Stores and encrypted the passed key relations in the keyfile and encrypts it with the passed key.
 * @param keyFilePath path to the keyfile, Example: "C:\\DropTillLateApplication\\keyfile"
 * @param key Key used for encrypting the keyfile
 * @param keyRelation containing all keys per share relation in the specified keyfile.
 * @throws KeyFileException Thrown if an error occurred.
 */
	public static synchronized void store(String keyFilePath, String key, KeyRelation keyRelation) throws KeyFileException
	{
		// Check if key relation contains entries
		if (keyRelation == null || keyRelation.getKeyShareMap() == null || keyRelation.getKeyShareMap().isEmpty())
		{
			throw new KeyFileException(KeyFileError.EMPTY_KEYRELATION,
					"Please provide a valid key relation containing shareRelationID's including the related keys");
		}
		// checks if the passed keyfile path is correct, if not an exception is thrown
		checkKeyFilePath(keyFilePath);

		final TFile keyFile = new TFile(keyFilePath);

		final RaesParameters params = new CustomRaesParameters(key.toCharArray());

		RaesOutputStream out = null;

		// open the encrypted output stream
		try
		{
			out = RaesOutputStream.getInstance(new TFileOutputStream(keyFile, false), params);
		} catch (FileNotFoundException e)
		{
			throw new KeyFileException(KeyFileError.FILE_NOT_FOUND, keyFile.getAbsolutePath());
		} catch (IOException e)
		{
			throw new KeyFileException(KeyFileError.IO_EXCEPTION, e.getMessage());
		} 

		// Console
		if (keyFile.exists())
		{
			System.out.println("Override existing keyfile");
		} else
		{
			System.out.println("Create new keyfile");
		}

		// Write key realtion to keyfile
		PrintWriter pw = new PrintWriter(out);
		try
		{
			Set<Integer> shareRelationIdSet = keyRelation.getKeyShareMap().keySet();
			for (int shareRelationID : shareRelationIdSet)
			{
				pw.println(shareRelationID + ENTRY_DIVIDER + keyRelation.getKeyOfShareRelation(shareRelationID));
			}
			pw.flush();
			pw.close();
		} catch (Exception e)
		{
			throw new KeyFileException(KeyFileError.FILE_WRITE_EXCEPTION, e.getMessage());
		} finally
		{
			if (pw != null)
			{
				pw.flush();
				pw.close();
			}
		}

	}

	/**
	 * Loads and decrypted the keyfile specified by the path, using the passed key.
	 * @param keyFilePath path to the keyfile, Example: "C:\\DropTillLateApplication\\keyfile"
	 * @param key Key used for decrypting the keyfile
	 * @param errorList A list of error which occur while reading the entry lines from the keyfile, this list gets updated by using the reference!
	 * @return KeyRelation which contains  all loaded keys per share relation in the specified keyfile.
	 * @throws KeyFileException Thrown if an error occurred.
	 */
	public static KeyRelation load(final String keyFilePath, String key, List<KeyFileError> errorList) throws KeyFileException
	{
		// checks if the passed keyfile path is correct, if not an exception is thrown
		checkKeyFilePath(keyFilePath);

		final TFile keyFile = new TFile(keyFilePath);
		final RaesParameters params = new CustomRaesParameters(key.toCharArray());
		KeyRelation keyRelation = new KeyRelation();

		ReadOnlyFile rof;
		// preparing input stream
		try
		{
			rof = new DefaultReadOnlyFile(keyFile);
		} catch (FileNotFoundException e)
		{
			throw new KeyFileException(KeyFileError.FILE_NOT_FOUND, keyFile.getAbsolutePath());
		}

		// read keyfile entries
		try
		{
			RaesReadOnlyFile rrof;

			rrof = RaesReadOnlyFile.getInstance(rof, params);

			// check if the key matches
			rrof.authenticate();

			final InputStream in = new ReadOnlyFileInputStream(rrof);

			BufferedReader br = new BufferedReader(new InputStreamReader(in));

			// fille the entries of the file in a key relation
			String line = null;
			// reading lines until the end of the file
			while ((line = br.readLine()) != null)
			{
				
				//TODO terminate if an error occurs or read the rest of the lines?
				try
				{
					updateKeyRelation(line, keyRelation);
				} catch (KeyFileException e)
				{
					System.err.println(e.getError());
					errorList.add(e.getError());
				}
			}

		} catch (RaesKeyException e1)
		{
			throw new KeyFileException(KeyFileError.INVALID_KEY, e1.getMessage());
		} catch (IOException e)
		{
			throw new KeyFileException(KeyFileError.IO_EXCEPTION, e.getMessage());
		} finally
		{
			try
			{
				rof.close();
			} catch (IOException e)
			{
				throw new KeyFileException(KeyFileError.IO_EXCEPTION, e.getMessage());
			}
		}
		return keyRelation;
	}

	/**
	 * Checks the passed keyfile path and throws an exception if it is incorrect
	 * 
	 * @param path keyFilePath path to the keyfile, Example: "C:\\DropTillLateApplication\\keyfile"
	 * @throws KeyFileException thrown if the path argument is invalid
	 */
	private synchronized static void checkKeyFilePath(String path) throws KeyFileException
	{
		if (path == null || path.length() < 4)
		{
			throw new KeyFileException(KeyFileError.FILE_INVALID_PATH,
					"Path is null or to short (has to be bigger than 4 chars), passed path: " + path);
		}
		if (OsHelper.pathEndsWithDirLimiter(path))
		{
			throw new KeyFileException(KeyFileError.FILE_INVALID_PATH, "Path ends with a directory limiter, passed path: " + path);
		}
	}

	/**
	 * Updates the passed key relation with the entries contained in the passed line
	 * 
	 * @param line read line from keyfile (contains shareRelationID and key)
	 * @param keyRelation New entries get added to this keyRelation, containing all keys per share relation in the specified keyfile.
	 * @throws KeyFileException KeyFileException thrown if an error occurred
	 */
	private synchronized static void updateKeyRelation(String line, KeyRelation keyRelation) throws KeyFileException
	{

		// split the line to access the shareRelationID and the related key
		String[] elements = null;
		try
		{
			elements = line.split(ENTRY_DIVIDER);
		} catch (Exception e)
		{
			throw new KeyFileException(KeyFileError.LINE_SPLIT_ERROR, e.getMessage());
		}
		// check entry count
		if (elements.length != 2)
		{
			throw new KeyFileException(KeyFileError.LINE_FETCH_ERROR, "Entry count does not match, line should contain 2 entries but has: "
					+ elements.length);
		}
		// fetch share relation
		String shareRelation = elements[0];
		int shareRelationID;
		// convert sharerelation to int
		try
		{
			shareRelationID = Integer.parseInt(shareRelation);
		} catch (NumberFormatException e)
		{
			throw new KeyFileException(KeyFileError.SHARE_ID_NOT_PARSABLE, e.getMessage());
		}
		// fetch key
		String key = elements[1];
		if (shareRelationID > 0 && key.length() > 0)
		{
			keyRelation.addKeyOfShareRelation(shareRelationID, key);
		} else
		{
			throw new KeyFileException(KeyFileError.LINE_FETCH_ERROR, "Fetched ShareRelationID or Key is invalid: ShareRelationID="
					+ shareRelationID + " and key=" + key);
		}
	}

}