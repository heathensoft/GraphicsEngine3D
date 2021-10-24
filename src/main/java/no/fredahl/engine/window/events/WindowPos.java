package no.fredahl.engine.window.events;

import org.lwjgl.glfw.GLFWWindowPosCallback;

/**
 * @author Frederik Dahl
 * 21/10/2021
 */


public class WindowPos extends GLFWWindowPosCallback {
    
    private int x, y;
    
    public WindowPos() {
        x = 0;
        y = 0;
    }
    @Override
    public void invoke(long window, int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public int x() {
        return x;
    }
    
    public int y() {
        return y;
    }
}
