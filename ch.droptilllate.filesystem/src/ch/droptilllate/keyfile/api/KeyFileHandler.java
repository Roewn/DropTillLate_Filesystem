/**
 * 
 */
package ch.droptilllate.keyfile.api;

import ch.droptilllate.filesystem.security.KeyRelation;
import ch.droptilllate.keyfile.error.KeyFileError;

/**
 * @author Roewn
 *
 */
public class KeyFileHandler implements IKeyFile
{

	@Override
	public KeyFileError storeKeyFile(String keyFilePath, String key, KeyRelation keyRealtion)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public KeyRelation loadKeyFile(String keyFilePath, String key)
	{
		// TODO Auto-generated method stub
		return null;
	}

}
