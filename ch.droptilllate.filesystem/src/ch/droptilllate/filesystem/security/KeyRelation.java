/**
 * 
 */
package ch.droptilllate.filesystem.security;

import java.util.HashMap;

import ch.droptilllate.filesystem.info.InfoHelper;

/**
 * This class hold the key for every share relation.
 * It is implemented with a hashMap so no duplicate entries occur.
 * @author Roewn
 *
 */
public class KeyRelation
{
	HashMap<String, String>  keyShareMap = null;
	

	public KeyRelation() {
		keyShareMap = new HashMap<String, String>();
	}
	
	/**
	 * Adds the share relation and its key to the map.
	 * @param shareRelationPath identifier of the share relation
	 * @param key related key for this share relation
	 */
	public void addKeyOfShareRelation(String shareRelationPath, String key){
		keyShareMap.put(InfoHelper.checkPath(shareRelationPath), key);
	}
	
	/**
	 * Returns the key of the passed share relation.
	 * @param shareRelationPath identifier of the share relation
	 * @return related key for this share relation
	 */
	public String getKeyOfShareRelation(String shareRelationPath) {
		return keyShareMap.get(shareRelationPath);
	}
	
	/**
	 * Checks if the passed share relation already exists in the map
	 * @param shareRelationPath identifier of the share relation
	 * @return True if the share relation already exists
	 */
	public boolean containsShareRelation(String shareRelationPath){
		return keyShareMap.containsKey(shareRelationPath);
	}
	
	/**
	 * @return the keyShareMap
	 */
	public HashMap<String, String> getKeyShareMap()
	{
		return keyShareMap;
	}

	/**
	 * @param keyShareMap the keyShareMap to set
	 */
	public void setKeyShareMap(HashMap<String, String> keyShareMap)
	{
		this.keyShareMap = keyShareMap;
	}

}
