package no.fredahl.engine.window.events;

import org.lwjgl.glfw.GLFWScrollCallback;

/**
 * @author Frederik Dahl
 * 21/10/2021
 */


public class MouseScroll extends GLFWScrollCallback {
    
    private int scroll = 0;
    
    @Override
    public void invoke(long window, double xoffset, double yoffset) {
        synchronized (this) { scroll += (int) yoffset; }
    }
    
    /**
     * @return sum of events (0 for no change)
     */
    public synchronized int value() {
        int value = scroll;
        scroll = 0;
        return value;
    }
}
