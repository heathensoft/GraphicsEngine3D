package no.fredahl.engine.window.events;

import org.lwjgl.glfw.GLFWKeyCallback;

import java.util.Collection;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static org.lwjgl.glfw.GLFW.*;

/**
 * @author Frederik Dahl
 * 21/10/2021
 */


public class KeyInput extends GLFWKeyCallback {
    
    private volatile boolean containsEvents;
    private final BlockingQueue<KeyEvent> eventQueue = new ArrayBlockingQueue<>(16);
    
    @Override
    public void invoke(long window, int key, int scancode, int action, int mods) {
    
        if (key == GLFW_KEY_E && action == GLFW_REPEAT) {
        
        }
        
        if (eventQueue.offer(new KeyEvent(key, scancode, action, mods))) containsEvents = true;
    }
    
    public void collect(Collection<KeyEvent> collection) {
        eventQueue.drainTo(collection);
        containsEvents = false;
    }
    
    public boolean containsEvents() {
        return containsEvents;
    }
}
