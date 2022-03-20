package no.fredahl.engine.graphics.lighting;

import no.fredahl.engine.graphics.Texture;
import org.lwjgl.opengl.GL32;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP;
import static org.lwjgl.opengl.GL30.*;

/**
 * @author Frederik Dahl
 * 07/01/2022
 */


public class ShadowMapCube extends DepthMap {
    
    
    public ShadowMapCube(int width, int height) throws Exception {
        super(width, height);
    }
    
    @Override
    protected void create(int width, int height) throws Exception {
        this.width = width;
        this.height = height;
        depthTexture = new Texture(GL_TEXTURE_CUBE_MAP);
        depthTexture.bind();
        depthTexture.filter(GL_NEAREST);
        depthTexture.wrapSTR(GL_CLAMP_TO_EDGE);
        depthTexture.depthCubeMap(width, height);
        fbo = glGenFramebuffers();
        bindFramebuffer();
        GL32.glFramebufferTexture(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, depthTexture.id(),0);
        glDrawBuffer(GL_NONE);
        glReadBuffer(GL_NONE);
        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE)
            throw new Exception("Unable to create FrameBuffer");
        unbindFramebuffer();
    }
    
    @Override
    public void dispose() {
        glDeleteFramebuffers(fbo);
        depthTexture.dispose();
    }
}
