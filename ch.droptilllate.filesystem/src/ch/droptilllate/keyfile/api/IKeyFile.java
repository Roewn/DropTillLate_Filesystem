package ch.droptilllate.keyfile.api;

import ch.droptilllate.keyfile.error.KeyFileError;
import ch.droptilllate.security.commons.KeyRelation;

public interface IKeyFile
{
	/**
	 * Stores and encrypted the passed key relations in the keyfile and encrypts it with the passed key.
	 * 
	 * @param keyFilePath path to the keyfile, Example: "C:\\DropTillLateApplication\\keyfile"
	 * @param key Key used for encrypting the keyfile.
	 * @param keyRelation containing all keys per share relation in the specified keyfile.
	 * @return KeyFileError, if no error occurred than KeyFileError == KeyFileError.NONE
	 */
	KeyFileError storeKeyFile(String keyFilePath, String key, KeyRelation keyRelation);

	/**
	 * Loads and decrypted the keyfile specified by the path, using the passed key.
	 * 
	 * @param keyFilePath path to the keyfile, Example: "C:\\DropTillLateApplication\\keyfile"
	 * @param key Key used for decrypting the keyfile.
	 * @return KeyFileHandlingSummary containing a KeyRelation with all loaded keys per share relation in the specified keyfile and a list
	 *         of errors. If no error occurred, getKeyFileErrorCount() == 0;
	 */
	KeyFileHandlingSummary loadKeyFile(String keyFilePath, String key);

}
