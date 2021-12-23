package no.fredahl.engine.graphics.shadow;

import no.fredahl.engine.graphics.GLBindings;
import no.fredahl.engine.graphics.Texture;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL14.GL_DEPTH_COMPONENT16;
import static org.lwjgl.opengl.GL30.*;

/**
 * @author Frederik Dahl
 * 22/12/2021
 */


public class ShadowMap {
    
    private final static GLBindings bindings = GLBindings.get();
    
    public static final int WIDTH = 1024;
    
    public static final int HEIGHT = 1024;
    
    private final int fbo;
    
    private final Texture depthMap;
    
    
    public ShadowMap() throws Exception {
    
        depthMap = new Texture(GL_TEXTURE_2D);
        depthMap.bind();
        depthMap.filter(GL_NEAREST);
        depthMap.wrapST(GL_CLAMP_TO_EDGE);
        depthMap.tex2D(0,GL_DEPTH_COMPONENT16,WIDTH,HEIGHT,GL_DEPTH_COMPONENT,GL_FLOAT);
        
        fbo = glGenFramebuffers();
        bindings.bindFrameBuffer(GL_FRAMEBUFFER,fbo);
        glFramebufferTexture2D(GL_FRAMEBUFFER,GL_DEPTH_ATTACHMENT,GL_TEXTURE_2D,depthMap.id(),0);
        
        glDrawBuffer(GL_NONE);
        glReadBuffer(GL_NONE);
    
        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
            throw new Exception("Unable to create FrameBuffer");
        }
    
        bindings.bindFrameBuffer(GL_FRAMEBUFFER,0);
    }
    
    
    public Texture texture() {
        return depthMap;
    }
    
    public int fbo() {
        return fbo;
    }
    
    public void bind() {
        bindings.bindFrameBuffer(GL_FRAMEBUFFER,fbo);
    }
    
    public void unbind() {
        bindings.bindFrameBuffer(GL_FRAMEBUFFER,0);
    }
    
    public void dispose() {
        glDeleteFramebuffers(fbo);
        depthMap.unbind();
        depthMap.delete();
    }
}
