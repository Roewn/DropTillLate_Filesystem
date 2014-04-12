/*
 * Copyright (C) 2005-2013 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package ch.droptilllate.keyfile.io;

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
import java.util.Set;

import ch.droptilllate.filesystem.error.FileError;
import ch.droptilllate.filesystem.error.FileException;
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
	private KeyFile()
	{
	}

	/**
	 * Encrypts the given plain file to the given RAES file, using the provided TArchiveDetector to detect any archive files in its parent
	 * directory path except the files themselves, which are not recognized as archive files.
	 */

	public static synchronized void store(String keyFilePath, String key, KeyRelation keyRelation) throws KeyFileException
	{
		// Check if key relation contains entries
		if (keyRelation == null || keyRelation.getKeyShareMap() == null || keyRelation.getKeyShareMap().isEmpty())
		{
			throw new KeyFileException(KeyFileError.EMPTY_KEYRELATION,
					"Please provide a valid key relation containing shareRelationID's including the related keys");
		}
		
		

		final TFile keyFile = new TFile(keyFilePath);

		final RaesParameters params = new CustomRaesParameters(key.toCharArray());

		RaesOutputStream out = null;

		try
		{
			out = RaesOutputStream.getInstance(new TFileOutputStream(keyFile, false), params);
		} catch (FileNotFoundException e)
		{
			throw new KeyFileException(KeyFileError.FILE_NOT_FOUND, keyFile.getAbsolutePath());
		} catch (IOException e)
		{
			throw new KeyFileException(KeyFileError.FILE_NOT_FOUND, e.getMessage());
		}

		if (keyFile.exists())
		{
			System.out.println("Override existing keyfile");
		} else
		{
			System.out.println("Create new keyfile");
		}

		PrintWriter pw = new PrintWriter(out);
		Set<Integer> shareRelationIdSet = keyRelation.getKeyShareMap().keySet();
		for (int shareRelationID : shareRelationIdSet)
		{
			pw.println(shareRelationID + "\t" + keyRelation.getKeyOfShareRelation(shareRelationID));
		}
		pw.flush();
		pw.close();
	}

	/**
	 * Decrypts the given RAES file to the given plain file, using the provided TArchiveDetector to detect any archvie files in its parent
	 * directory path except the files themselves, which are not recognized as archive files.
	 * 
	 * @param authenticate If this is {@code true}, the entire contents of the encrypted file get authenticated, which can be a time
	 *            consuming operation. Otherwise, only the key/password and the file length get authenticated.
	 */
	public static KeyRelation load(final String keyFilePath, String key) throws IOException
	{
		final TFile keyFile = new TFile(keyFilePath);
		final RaesParameters params = new CustomRaesParameters(key.toCharArray());

		final ReadOnlyFile rof = new DefaultReadOnlyFile(keyFile);
		try
		{
			final RaesReadOnlyFile rrof = RaesReadOnlyFile.getInstance(rof, params);

			rrof.authenticate();

			final InputStream in = new ReadOnlyFileInputStream(rrof);

			BufferedReader br = new BufferedReader(new InputStreamReader(in));

			String line = null;
			while ((line = br.readLine()) != null)
			{
				// reading lines until the end of the file
				System.out.println(line);

			}

		} finally
		{
			rof.close();
		}
		return null;
	}

	/**
	 * Creates a file object which uses the provided TArchiveDetector, but does not recognize its own pathname as an archive file. Please
	 * note that this method just creates a file object, and does not actually operate on the file system.
	 */
	private static TFile newNonArchiveFile(String path, TArchiveDetector detector)
	{
		TFile file = new TFile(path, detector);
		return new TFile(file.getParentFile(), file.getName(), TArchiveDetector.NULL);
	}
}