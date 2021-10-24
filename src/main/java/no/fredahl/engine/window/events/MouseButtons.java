package no.fredahl.engine.window.events;

import org.lwjgl.glfw.GLFWMouseButtonCallback;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;

/**
 * @author Frederik Dahl
 * 21/10/2021
 */


public class MouseButtons extends GLFWMouseButtonCallback {
    
    private final static int NUM_BUTTONS = 3;
    private final boolean[] pressed = new boolean[NUM_BUTTONS];
    
    @Override
    public void invoke(long window, int button, int action, int mods) {
        pressed[button] = action == GLFW_PRESS && button < NUM_BUTTONS;
    }
    
    public boolean isPressed(int button) {
        return button < NUM_BUTTONS && pressed[button];
        
    }
    
}
