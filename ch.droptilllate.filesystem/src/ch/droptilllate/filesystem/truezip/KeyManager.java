package ch.droptilllate.filesystem.truezip;

	import ch.droptilllate.filesystem.commons.Constants;
import de.schlichtherle.truezip.file.TArchiveDetector;

	public class KeyManager {
		
		/**
		 * Returns a new archive detector which uses the given password for all
		 * RAES encrypted ZIP files with the given list of suffixes.
		 * 
		 * A protective copy of the given password char array is made.
		 * It's recommended to overwrite the parameter array with any non-password
		 * data after calling this method.
		 * 
		 * @param suffixes suffixes A list of file name suffixes which shall identify
		 *         prospective archive files.
		 * @param  password the password char array to be copied for internal use.
		 * @return A new archive detector which uses the given password for all
		 *         RAES encrypted ZIP files with the given list of suffixes.
		 */
		public static TArchiveDetector getArchiveDetector(String suffixes, char[] password) 
		{
			// TODO Overwrite password parameter array?
		    return new TArchiveDetector(suffixes, new CustomZipRaesDriver(password));
		}
		

		/**
		 * Returns a new archive detector which uses the given password for all
		 * RAES encrypted ZIP files with the given list of suffixes.
		 * 
		 * A protective copy of the given password char array is made.
		 * It's recommended to overwrite the parameter array with any non-password
		 * data after calling this method.
		 * 
		 * @param suffixes suffixes A list of file name suffixes which shall identify
		 *         prospective archive files.
		 * @return A new archive detector which uses the given password for all
		 *         RAES encrypted ZIP files with the given list of suffixes.
		 */
		public static TArchiveDetector getArchiveDetector(String suffixes) 
		{
		    return getArchiveDetector(Constants.CONTAINER_EXTENTION, "".toCharArray());
		}
		
		/**
		 * Returns a new archive detector which uses the given password for all
		 * RAES encrypted ZIP files with the given list of suffixes.
		 * 
		 * A protective copy of the given password char array is made.
		 * It's recommended to overwrite the parameter array with any non-password
		 * data after calling this method.
		 * 
		 * @param  password the password char array to be copied for internal use.
		 * @return A new archive detector which uses the given password for all
		 *         RAES encrypted ZIP files with the given list of suffixes.
		 */
		public static TArchiveDetector getArchiveDetector(char[] password) 
		{
		    return getArchiveDetector(Constants.CONTAINER_EXTENTION, password);
		}
}
