package no.fredahl.engine.window.events;

import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import no.fredahl.engine.window.Viewport;

/**
 * @author Frederik Dahl
 * 21/10/2021
 */


public class FrameBufferSize extends GLFWFramebufferSizeCallback {
    
    private final Viewport v;
    private int w;
    private int h;
    
    public FrameBufferSize(Viewport viewport) {
        this.w = viewport.width();
        this.h = viewport.height();
        this.v = viewport;
    }
    
    @Override
    public void invoke(long window, int w, int h) {
        v.update(this.w, this.h);
        this.h = h;
        this.w = w;
    }
    
    public int height() {
        return h;
    }
    
    public int width() {
        return w;
    }
}
