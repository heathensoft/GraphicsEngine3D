package no.fredahl.engine.window.events;

import no.fredahl.engine.utility.storage.Interator;
import no.fredahl.engine.utility.storage.IntQueue;
import org.lwjgl.glfw.GLFWKeyCallback;


import static org.lwjgl.glfw.GLFW.*;

/**
 *
 * Enqueues key state-changes only.
 * On release the enqueued key-value is negative
 *
 * @author Frederik Dahl
 * 21/10/2021
 */


public class KeyPressEvents extends GLFWKeyCallback {
    
    private final IntQueue queue = new IntQueue();
    private int lastKey = 0;
    private boolean ignore;
    
    @Override
    public void invoke(long window, int key, int scancode, int action, int mods) {
        if (ignore) return;
        if (key != GLFW_KEY_UNKNOWN && key < GLFW_KEY_LAST && action != GLFW_REPEAT) {
            key = action == GLFW_PRESS ? key : -key;
            if (key == lastKey) return;
            synchronized (this) {
                if (queue.size() == 16)
                    queue.dequeue();
                queue.enqueue(lastKey = key);
            }
        }
    }
    
    public synchronized boolean collect(Interator collector) {
        if (queue.isEmpty()) return false;
        while (!queue.isEmpty())
            collector.next(queue.dequeue());
        return true;
    }
    
    public synchronized void ignore(boolean ignore) {
        this.ignore = ignore;
    }
    
    public synchronized void clear() {
        queue.clear();
    }
}
