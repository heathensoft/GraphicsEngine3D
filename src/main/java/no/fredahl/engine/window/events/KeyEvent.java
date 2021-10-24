package no.fredahl.engine.window.events;

/**
 * @author Frederik Dahl
 * 21/10/2021
 */


public class KeyEvent {
    
    public int key;
    public int scancode;
    public int action;
    public int mods;
    
    public KeyEvent(int key, int scancode, int action, int mods) {
        this.key = key;
        this.scancode = scancode;
        this.action = action;
        this.mods = mods;
    }
}
