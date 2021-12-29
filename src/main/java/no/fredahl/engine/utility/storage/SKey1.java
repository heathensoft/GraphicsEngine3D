package no.fredahl.engine.utility.storage;

/**
 * @author Frederik Dahl
 * 29/12/2021
 */


public class SKey1 implements StorageKey {
    
    private int key = NONE;
    
    @Override
    public int sKey(short storageID) {
        return key;
    }
    
    @Override
    public void sOnInsert(int assignedKey, short storageID) {
        key = assignedKey;
    }
    
    @Override
    public void sOnReplace(int assignedKey, short storageID) {
        key = assignedKey;
    }
    
    @Override
    public void sOnRemove(short storageID) {
        key = NONE;
    }
}
