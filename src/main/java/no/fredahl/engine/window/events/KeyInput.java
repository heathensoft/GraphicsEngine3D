package no.fredahl.engine.window.events;

import org.lwjgl.glfw.GLFWKeyCallback;

import java.util.Collection;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static org.lwjgl.glfw.GLFW.*;

/**
 *
 * Enqueues key state-changes only.
 * On release the enqueued key-value is negative
 *
 * @author Frederik Dahl
 * 21/10/2021
 */


public class KeyInput extends GLFWKeyCallback {
    
    private boolean active;
    private boolean containsEvents;
    private final BlockingQueue<Integer> eventQueue = new ArrayBlockingQueue<>(16);
    private int lastKey = 0;
    
    @Override
    public void invoke(long window, int key, int scancode, int action, int mods) {
        
        if (key != GLFW_KEY_UNKNOWN && action != GLFW_REPEAT) {
            
            key = action == GLFW_PRESS ? key : -key;
            
            if (key == lastKey) return;
            
            if (eventQueue.offer(lastKey = key)) containsEvents = true;
        }
    }
    
    public void collect(Collection<Integer> collection) {
        eventQueue.drainTo(collection);
        containsEvents = false;
    }
    
    public boolean containsEvents() {
        return containsEvents;
    }
}
