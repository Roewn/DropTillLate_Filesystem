/**
 * 
 */
package ch.droptilllate.keyfile.api;

import java.util.ArrayList;
import java.util.List;

import ch.droptilllate.keyfile.error.KeyFileError;
import ch.droptilllate.keyfile.error.KeyFileException;
import ch.droptilllate.keyfile.io.KeyFile;
import ch.droptilllate.security.commons.KeyRelation;

/**
 * @author Roewn
 *
 */
public class KeyFileHandler implements IKeyFile
{

	@Override
	public KeyFileError storeKeyFile(String keyFilePath, String key, KeyRelation keyRelation)
	{
		KeyFileError error = KeyFileError.NONE;
		
		try
		{
			KeyFile.store(keyFilePath, key, keyRelation);
		} catch (KeyFileException e)
		{
			System.err.println(e.getError());
			error = e.getError();
		}
		return error;
		
	}

	@Override
	public KeyFileHandlingSummary loadKeyFile(String keyFilePath, String key)
	{
		KeyFileHandlingSummary kfhs = new KeyFileHandlingSummary();
		List<KeyFileError> errorList = new ArrayList<KeyFileError>();
		KeyFileError error = KeyFileError.NONE;
		try
		{
			// load all key per sharerelation into the summary
			kfhs.setKeyRelation(KeyFile.load(keyFilePath, key, errorList));
			// add the line read errors to the summary
			kfhs.setKeyFileErrorList(errorList);
		} catch (KeyFileException e)
		{
			System.err.println(e.getError());
			kfhs.addKeyFileError(e.getError());
		}
		
		return kfhs;
	}

}
