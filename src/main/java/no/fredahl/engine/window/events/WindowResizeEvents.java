package no.fredahl.engine.window.events;

import org.lwjgl.glfw.GLFWWindowSizeCallback;

/**
 * @author Frederik Dahl
 * 21/10/2021
 */


public class WindowResizeEvents extends GLFWWindowSizeCallback {
    
    private int w;
    private int h;
    
    
    
    @Override
    public void invoke(long window, int w, int h) {
        this.w = w;
        this.h = h;
    }
    
    public int height() {
        return h;
    }
    
    public int width() {
        return w;
    }
}
