package no.fredahl.engine.window.events;

import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import no.fredahl.engine.window.Viewport;

/**
 * @author Frederik Dahl
 * 21/10/2021
 */


public class FrameBufferEvents extends GLFWFramebufferSizeCallback {
    
    private final Viewport viewport;
    private boolean viewportEvent = true;
    private int w;
    private int h;
    
    public FrameBufferEvents(Viewport viewport) {
        this.w = viewport.width();
        this.h = viewport.height();
        this.viewport = viewport;
    }
    
    @Override
    public void invoke(long window, int w, int h) {
        if (w > 0 && h > 0) {
            this.viewport.update(w, h);
            this.h = h;
            this.w = w;
            this.viewportEvent = true;
        }
        
    }
    
    public int viewportHeight() {
        return h;
    }
    
    public int viewportWidth() {
        return w;
    }
    
    public boolean viewportEvent() {
        return viewportEvent;
    }
    
    public void reset() {
        viewportEvent = false;
    }
}
