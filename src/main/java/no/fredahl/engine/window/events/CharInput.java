package no.fredahl.engine.window.events;

import org.lwjgl.glfw.GLFWCharCallback;

import java.util.Collection;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * @author Frederik Dahl
 * 21/10/2021
 */


public class CharInput extends GLFWCharCallback {
    
    private boolean containsEvents;
    private final BlockingQueue<Integer> eventQueue = new ArrayBlockingQueue<>(16);
    
    @Override
    public void invoke(long window, int codepoint) {
        if (eventQueue.offer(codepoint)) containsEvents = true;
    }
    
    public void collect(Collection<Integer> collection) {
        eventQueue.drainTo(collection);
        containsEvents = false;
    }
    
    public boolean containsEvents() {
        return containsEvents;
    }
    
    
}
