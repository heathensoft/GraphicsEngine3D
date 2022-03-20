package no.fredahl.engine.graphics.lighting;

import no.fredahl.engine.graphics.GLBindings;
import no.fredahl.engine.graphics.Texture;
import no.fredahl.engine.utility.Disposable;

import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;

/**
 * @author Frederik Dahl
 * 07/01/2022
 */


public abstract class DepthMap implements Disposable {
    
    protected final static GLBindings bindings = GLBindings.get();
    
    protected int fbo;
    protected int width;
    protected int height;
    protected Texture depthTexture;
    
    public DepthMap(int width, int height) throws Exception {
        create(width, height);
    }
    
    protected abstract void create(int width, int height) throws Exception;
    
    public Texture texture() {
        return depthTexture;
    }
    
    public int fbo() {
        return fbo;
    }
    
    public int width() {
        return width;
    }
    
    public int height() {
        return height;
    }
    
    public void bindFramebuffer() {
        bindings.bindFrameBuffer(GL_FRAMEBUFFER,fbo);
    }
    
    public void unbindFramebuffer() {
        bindings.bindFrameBuffer(GL_FRAMEBUFFER,0);
    }
    
    
}
