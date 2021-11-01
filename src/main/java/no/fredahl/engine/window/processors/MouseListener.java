package no.fredahl.engine.window.processors;

/**
 * @author Frederik Dahl
 * 01/11/2021
 */


public interface MouseListener {
    
    void hover(double x, double y, double dX, double dY, double nX, double nY);
    
    void click(int button, double x, double y, double nX, double nY);
    
    void scroll(int value, double x, double y);
    
    void dragging(int button, double vX, double vY, double dX, double dY);
    
    void dragStart(int button, double pX, double pY);
    
    void dragRelease(int button, double pX, double pY);
    
    
}
