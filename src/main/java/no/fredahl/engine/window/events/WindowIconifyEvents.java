package no.fredahl.engine.window.events;

import org.lwjgl.glfw.GLFWWindowIconifyCallback;

/**
 * @author Frederik Dahl
 * 21/10/2021
 */


public class WindowIconifyEvents extends GLFWWindowIconifyCallback {
    
    private boolean minimized;
    
    @Override
    public void invoke(long window, boolean iconified) {
        minimized = iconified;
    }
    
    public boolean isMinimized() {
        return minimized;
    }
}
