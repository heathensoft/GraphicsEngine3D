package no.fredahl.engine.utility.storage;

/**
 * @author Frederik Dahl
 * 29/12/2021
 */


public interface Array<E> {
    
    int DEFAULT_CAPACITY = 16;
    
    void iterate(Iterator<E> itr);
    
    void ensureCapacity(int n);
    
    void setTargetCapacity(int cap);
    
    boolean fit(boolean absolute);
    
    E[] toArray();
    
    void clear();
    
    float loadFactor();
    
    int count();
    
    int capacity();
    
    int targetCapacity();
    
    boolean isEmpty();
    
    boolean notEmpty();
}
