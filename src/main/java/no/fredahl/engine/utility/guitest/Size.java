package no.fredahl.engine.utility.guitest;

/**
 * @author Frederik Dahl
 * 07/11/2021
 */


public class Size {

    public int width;
    public int height;
    
    
    public Size(int width, int height) {
        this.width = width;
        this.height = height;
    }
    
    public Size set(int width, int height) {
        this.width = width;
        this.height = height;
        return this;
    }
}
