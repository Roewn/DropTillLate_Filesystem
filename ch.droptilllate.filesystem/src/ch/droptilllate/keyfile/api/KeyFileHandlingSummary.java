/**
 * 
 */
package ch.droptilllate.keyfile.api;

import java.util.ArrayList;
import java.util.List;

import ch.droptilllate.filesystem.info.FileInfo;
import ch.droptilllate.keyfile.error.KeyFileError;
import ch.droptilllate.security.commons.KeyRelation;

/**
 * @author Roewn
 *
 */
public class KeyFileHandlingSummary
{
	private List<KeyFileError> keyFileErrorList;
	private KeyRelation keyRelation;
	
	
	public KeyFileHandlingSummary()
	{
		this.keyFileErrorList =  new ArrayList<KeyFileError>();;
		this.keyRelation = new KeyRelation();
	}
	
	/**
	 * Checks if a wrong key for the keyfile has been provided.
	 * @return true if the passed key for the keyfile was invalid
	 */
	public boolean wrongKey() {
		return keyFileErrorList.contains(KeyFileError.INVALID_KEY);
	}
	
	/**
	 * @return the KeyFileError count
	 */
	public int getKeyFileErrorCount() {
		return keyFileErrorList.size();
	}

	/**
	 * Adds a new entry to the FileInfoError List.
	 * 
	 * @param fileInfo FileInfo which throw an Exception during an Operation
	 */
	public void addKeyFileError(KeyFileError error) {
		keyFileErrorList.add(error);
	}


	/**
	 * @return the keyFileErrorList
	 */
	public List<KeyFileError> getKeyFileErrorList()
	{
		return keyFileErrorList;
	}


	/**
	 * @param keyFileErrorList the keyFileErrorList to set
	 */
	public void setKeyFileErrorList(List<KeyFileError> keyFileErrorList)
	{
		this.keyFileErrorList = keyFileErrorList;
	}


	/**
	 * @return the keyRelation
	 */
	public KeyRelation getKeyRelation()
	{
		return keyRelation;
	}


	/**
	 * @param keyRelation the keyRelation to set
	 */
	public void setKeyRelation(KeyRelation keyRelation)
	{
		this.keyRelation = keyRelation;
	}
	
	
	
	
	

}
