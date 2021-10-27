package no.fredahl.engine;

import no.fredahl.engine.window.events.CharInput;
import no.fredahl.engine.window.events.KeyInput;

/**
 * @author Frederik Dahl
 * 26/10/2021
 */


public class Keyboard {
 
    private final KeyInput keyInput;
    private final CharInput charInput;
    
    
 
    public Keyboard(KeyInput keyInput, CharInput charInput) {
        this.keyInput = keyInput;
        this.charInput = charInput;
    }
    
    public boolean pressed(int keyCode) {
        return false;
    }
    
    public boolean pressed(int keyCode1, int keyCode2) {
        return false;
    }
    
    public boolean justPressed(int keyCode) {
        return false;
    }
    
    public boolean justPressed(int keyCode, int mod) {
        return false;
    }
    
    public boolean justReleased(int keyCode) {
        return false;
    }
}
