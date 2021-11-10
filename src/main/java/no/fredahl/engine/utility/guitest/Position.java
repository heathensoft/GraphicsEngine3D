package no.fredahl.engine.utility.guitest;

/**
 * @author Frederik Dahl
 * 07/11/2021
 */


public class Position {
 
    public int x;
    public int y;
    
    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    void set(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
