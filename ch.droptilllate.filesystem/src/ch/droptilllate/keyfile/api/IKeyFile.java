package ch.droptilllate.keyfile.api;


import ch.droptilllate.keyfile.error.KeyFileError;
import ch.droptilllate.security.commons.KeyRelation;

public interface IKeyFile
{	
	/**
	 * Stores and encrypted the passed key relations in the keyfile and encrypts it with the passed key.
	 * @param keyFilePath path to the keyfile, Example: "C:\\DropTillLateApplication\\keyfile"
	 * @param key Key used for encrypting the keyfile.
	 * @param keyRealtion containing all keys per share relation in the specified keyfile.
	 * @return KeyFileError, if no error occurred than getError() == KeyFileError.NONE
	 */
	 KeyFileError storeKeyFile(String keyFilePath, String key, KeyRelation keyRealtion);
	
	 /**
	  * Loads and decrypted the keyfile specified by the path, using the passed key.
	  * @param keyFilePath path to the keyfile, Example: "C:\\DropTillLateApplication\\keyfile"
	  * @param key Key used for decrypting the keyfile.
	  * @return KeyRelation containing all keys per share relation in the specified keyfile.
	  */
	 KeyRelation loadKeyFile(String keyFilePath, String key);	

}
