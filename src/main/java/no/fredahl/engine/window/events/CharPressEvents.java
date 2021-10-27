package no.fredahl.engine.window.events;

import no.fredahl.engine.window.processors.IntQueue;
import no.fredahl.engine.window.processors.Collector;
import org.lwjgl.glfw.GLFWCharCallback;

/**
 * Only ASCII valid characters are queued.
 *
 * @author Frederik Dahl
 * 21/10/2021
 */


public class CharPressEvents extends GLFWCharCallback {
    
    private final IntQueue queue = new IntQueue();
    private boolean ignore;
    
    @Override
    public void invoke(long window, int codepoint) {
        if (ignore || ((codepoint & 0x7F) != codepoint)) return;
        synchronized (this) {
            if (queue.size() == 16)
                queue.dequeue();
            queue.enqueue(codepoint);
        }
    }
    
    public synchronized void collect(Collector collector) {
        while (!queue.isEmpty()) collector.next(queue.dequeue());
    }
    
    public synchronized void ignore(boolean ignore) {
        this.ignore = ignore;
    }
    
    public synchronized void clear() {
        queue.clear();
    }
    
}
