package no.fredahl.engine.utility.storage;

/**
 * @author Frederik Dahl
 * 29/12/2021
 */


public interface StorageKey {
    
    int NONE = -1;
    
    int sKey(short storageID);
    
    void sOnInsert(int assignedKey, short storageID);
    
    void sOnReplace(int assignedKey, short storageID);
    
    void sOnRemove(short storageID);
}
