/**
 * 
 */
package ch.droptilllate.filesystem.security;

import java.util.HashMap;



/**
 * This class hold the key for every share relation.
 * It is implemented with a hashMap so no duplicate entries occur.
 * @author Roewn
 *
 */
public class KeyRelation
{
	HashMap<Integer, String>  keyShareMap = null;
	

	public KeyRelation() {
		keyShareMap = new HashMap<Integer, String>();
	}
	
	/**
	 * Adds the share relation and its key to the map.
	 * @param shareRelationID identifier of the share relation
	 * @param key related key for this share relation
	 */
	public void addKeyOfShareRelation(int shareRelationID, String key){
		keyShareMap.put(shareRelationID, key);
	}
	
	/**
	 * Returns the key of the passed share relation.
	 * @param shareRelationID identifier of the share relation
	 * @return related key for this share relation
	 */
	public String getKeyOfShareRelation(int shareRelationID) {
		return keyShareMap.get(shareRelationID);
	}
	
	/**
	 * Checks if the passed share relation already exists in the map
	 * @param shareRelationID identifier of the share relation
	 * @return True if the share relation already exists
	 */
	public boolean containsShareRelation(int shareRelationID){
		return keyShareMap.containsKey(shareRelationID);
	}
	
	/**
	 * @return the keyShareMap
	 */
	public HashMap<Integer, String> getKeyShareMap()
	{
		return keyShareMap;
	}

	/**
	 * @param keyShareMap the keyShareMap to set
	 */
	public void setKeyShareMap(HashMap<Integer, String> keyShareMap)
	{
		this.keyShareMap = keyShareMap;
	}

}
