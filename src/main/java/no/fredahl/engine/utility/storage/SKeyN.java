package no.fredahl.engine.utility.storage;

import no.fredahl.engine.utility.storage.exceptions.KeyStateConflictException;

/**
 * @author Frederik Dahl
 * 29/12/2021
 */


public class SKeyN implements StorageKey {
    
    private byte sC;
    private int[] keys = {NONE,NONE};
    private short[] sID = {NONE,NONE};
    
    @Override
    public int sKey(short storageID) {
        for (byte i = 0; i < sC; i++) {
            if (sID[i] == storageID)
                return keys[i];
        }return NONE;
    }
    
    @Override
    public void sOnInsert(int assignedKey, short storageID) {
        if (sID.length == sC) {
            if (sC == Byte.MAX_VALUE)
                throw new IllegalStateException("Item can max inhabit 127 Arrays");
            final int newSize = Math.min(Byte.MAX_VALUE, sC * 2);
            int[] newKeys = new int[newSize];
            short[] newArID = new short[newSize];
            for (byte i = 0; i < sC; i++) {
                newKeys[i] = keys[i];
                newArID[i] = sID[i];}
            for (byte i = sC; i < newSize; i++) {
                newKeys[i] = NONE;
                newArID[i] = NONE;}
            keys = newKeys;
            sID = newArID;}
        keys[sC] = assignedKey;
        sID[sC] = storageID;
        sC++;
    }
    
    @Override
    public void sOnReplace(int assignedKey, short storageID) {
        for (byte i = 0; i < sC; i++) {
            if (sID[i] == storageID) {
                keys[i] = assignedKey;
                return;
            }
        }throw new KeyStateConflictException("Item, not registered to inhabit storage");
    }
    
    @Override
    public void sOnRemove(short storageID) {
        for (int i = 0; i < sC; i++) {
            if (sID[i] == storageID) {
                final int lastIndex = sC - 1;
                if (i != lastIndex) {
                    keys[i] = keys[lastIndex];
                    sID[i] = sID[lastIndex];
                    keys[lastIndex] = NONE;
                    sID[lastIndex] = NONE;
                } else keys[i] = sID[i] = NONE;
                sC--;
                return;
            }
        }throw new KeyStateConflictException("Item, not registered to inhabit storage");
    }
}
