package ch.droptilllate.security.truezip;

import ch.droptilllate.filesystem.preferences.Constants;

import de.schlichtherle.truezip.crypto.raes.Type0RaesParameters.KeyStrength;
import de.schlichtherle.truezip.crypto.raes.param.AesCipherParameters;
import de.schlichtherle.truezip.file.TArchiveDetector;
import de.schlichtherle.truezip.fs.archive.zip.raes.PromptingKeyManagerService;
import de.schlichtherle.truezip.fs.archive.zip.raes.SafeZipRaesDriver;


import de.schlichtherle.truezip.key.PromptingKeyProvider.Controller;
import de.schlichtherle.truezip.key.PromptingKeyProvider.View;
import de.schlichtherle.truezip.key.UnknownKeyException;

import de.schlichtherle.truezip.key.sl.KeyManagerLocator;
import de.schlichtherle.truezip.socket.sl.IOPoolLocator;

public class KeyManager2
{
	// TODO See Truezip samples for better implementations of the Key manager

	/**
	 * Returns a new archive detector which uses the given password for all RAES encrypted ZIP files with the given list of suffixes.
	 * 
	 * A protective copy of the given password char array is made. It's recommended to overwrite the parameter array with any non-password
	 * data after calling this method.
	 * 
	 * @param suffixes suffixes A list of file name suffixes which shall identify prospective archive files.
	 * @param password the password char array to be copied for internal use.
	 * @return A new archive detector which uses the given password for all RAES encrypted ZIP files with the given list of suffixes.
	 */
	public static TArchiveDetector getArchiveDetector(String suffixes, char[] password)
	{
		// TODO Overwrite password parameter array?
		return new TArchiveDetector(suffixes, new CustomZipRaesDriver(password));
	}

	/**
	 * Returns a new archive detector which uses the given password for all RAES encrypted ZIP files with the given list of suffixes.
	 * 
	 * A protective copy of the given password char array is made. It's recommended to overwrite the parameter array with any non-password
	 * data after calling this method.
	 * 
	 * @param suffixes suffixes A list of file name suffixes which shall identify prospective archive files.
	 * @return A new archive detector which uses the given password for all RAES encrypted ZIP files with the given list of suffixes.
	 */
	public static TArchiveDetector getArchiveDetector(String suffixes)
	{
		return getArchiveDetector(Constants.CONTAINER_EXTENTION, "".toCharArray());
	}

	/**
	 * Returns a new archive detector which uses the given password for all RAES encrypted ZIP files with the given list of suffixes.
	 * 
	 * A protective copy of the given password char array is made. It's recommended to overwrite the parameter array with any non-password
	 * data after calling this method.
	 * 
	 * @param password the password char array to be copied for internal use.
	 * @return A new archive detector which uses the given password for all RAES encrypted ZIP files with the given list of suffixes.
	 */
	public static TArchiveDetector getArchiveDetector(char[] password)
	{
		return getArchiveDetector(Constants.CONTAINER_EXTENTION, password);
	}

	private static final class CustomZipRaesDriver2 extends SafeZipRaesDriver
	{

		final PromptingKeyManagerService service;

		CustomZipRaesDriver2(char[] password)
		{
			super(IOPoolLocator.SINGLETON, KeyManagerLocator.SINGLETON);
			this.service = new PromptingKeyManagerService(new CustomView(password));
		}
		
	} // CustomZipRaesDriver2

	private static final class CustomView implements View<AesCipherParameters>
	{

		final char[] password;

		CustomView(char[] password)
		{
			this.password = password.clone();
		}

		/**
		 * You need to create a new key because the key manager may eventually reset it when the archive file gets moved or deleted.
		 */
		private AesCipherParameters newKey()
		{
			AesCipherParameters param = new AesCipherParameters();
			param.setPassword(password);
			param.setKeyStrength(KeyStrength.BITS_256);
			return param;
		}

		@Override
		public void promptReadKey(Controller<AesCipherParameters> arg0, boolean arg1) throws UnknownKeyException
		{
			// if (arg1) throw new UnknownKeyException();
			// TODO check if arg1 can be checked somehow

			// You might as well call controller.getResource() here in order to
			// programmatically set the parameters for individual resource URIs.
			// Note that this would typically return the hierarchical URI of
			// the archive file unless ZipDriver.mountPointUri(FsModel) would
			// have been overridden.
			arg0.setKey(newKey());
		}

		@Override
		public void promptWriteKey(Controller<AesCipherParameters> arg0) throws UnknownKeyException
		{
			// You might as well call controller.getResource() here in order to
			// programmatically set the parameters for individual resource URIs.
			// Note that this would typically return the hierarchical URI of
			// the archive file unless ZipDriver.mountPointUri(FsModel) would
			// have been overridden.
			arg0.setKey(newKey());

		}
	} // CustomView

}
