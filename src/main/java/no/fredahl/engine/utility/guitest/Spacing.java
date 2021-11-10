package no.fredahl.engine.utility.guitest;

/**
 * @author Frederik Dahl
 * 07/11/2021
 */


public class Spacing {

    public int top;
    public int right;
    public int bottom;
    public int left;
    
    public Spacing(int spacing) {
        this(spacing,spacing);
    }
    
    public Spacing(int horizontal, int vertical) {
        this(vertical,horizontal,vertical,horizontal);
    }
    
    public Spacing(int top, int right, int bottom, int left) {
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        this.left = left;
    }
    
    public int vertical() {
        return top + bottom;
    }
    
    public int horizontal() {
        return left + right;
    }
}
