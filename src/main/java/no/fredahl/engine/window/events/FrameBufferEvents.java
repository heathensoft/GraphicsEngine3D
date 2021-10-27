package no.fredahl.engine.window.events;

import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import no.fredahl.engine.window.Viewport;

/**
 * @author Frederik Dahl
 * 21/10/2021
 */


public class FrameBufferEvents extends GLFWFramebufferSizeCallback {
    
    private final Viewport v;
    private int w;
    private int h;
    
    public FrameBufferEvents(Viewport viewport) {
        this.w = viewport.width();
        this.h = viewport.height();
        this.v = viewport;
    }
    
    @Override
    public void invoke(long window, int w, int h) {
        if (w > 0 && h > 0) {
            v.update(w, h);
            this.h = h;
            this.w = w;
        }
        
    }
    
    public int height() {
        return h;
    }
    
    public int width() {
        return w;
    }
}
