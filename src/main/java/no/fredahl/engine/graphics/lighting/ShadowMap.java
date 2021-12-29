package no.fredahl.engine.graphics.lighting;

import no.fredahl.engine.graphics.BufferObject;
import no.fredahl.engine.graphics.GLBindings;
import no.fredahl.engine.graphics.ShaderProgram;
import no.fredahl.engine.graphics.Texture;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL14.GL_DEPTH_COMPONENT16;
import static org.lwjgl.opengl.GL30.*;

/**
 * @author Frederik Dahl
 * 22/12/2021
 */


public class ShadowMap {
    
    private final static GLBindings bindings = GLBindings.get();
    
    private final int fbo;
    private final int width;
    private final int height;
    private final Texture depthMap;
    
    
    private BufferObject vbo, ebo;
    private int vao;
    
    public void renderDepthTexture() {
        depthMap.bind();
        bindings.bindAttributeArray(vao);
        final int elementArrLength = 6;
        glDrawElements(GL_TRIANGLES, elementArrLength, GL_UNSIGNED_SHORT,0);
        bindings.bindAttributeArray(0);
    }
    
    private void generateBuffers() {
        
        vao = glGenVertexArrays();
        bindings.bindAttributeArray(vao);
    
        final float[] vertexArray = {
            
                // position       // texCoord
                1.0f, 0.0f,     1.0f, 0.0f, // Bottom right 0
                0.0f,  1.0f,     0.0f, 1.0f, // Top left     1
                1.0f,  1.0f ,    1.0f, 1.0f, // Top right    2
                0.0f, 0.0f,     0.0f, 0.0f, // Bottom left  3
        };
        
        vbo = new BufferObject(GL_ARRAY_BUFFER,GL_STATIC_DRAW);
        vbo.bind();
        vbo.bufferData(vertexArray);
    
        final short[] elementArray = {
                2, 1, 0, // Top right triangle
                0, 1, 3  // bottom left triangle
        };
        
        ebo = new BufferObject(GL_ELEMENT_ARRAY_BUFFER,GL_STATIC_DRAW);
        ebo.bind();
        ebo.bufferData(elementArray);
    
        final int fboPosSize = 2;
        final int fboTexCoordSize = 2;
        final int fboVertexSizeBytes = (fboPosSize + fboTexCoordSize) * Float.BYTES;
    
        glVertexAttribPointer(0, fboPosSize, GL_FLOAT, false, fboVertexSizeBytes, 0);
        glVertexAttribPointer(1, fboTexCoordSize, GL_FLOAT, false, fboVertexSizeBytes, fboPosSize * Float.BYTES);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        bindings.bindAttributeArray(0);
    }
    
    public ShadowMap(int width, int height) throws Exception {
    
        this.width = width;
        this.height = height;
        
        depthMap = new Texture(GL_TEXTURE_2D);
        depthMap.bind();
        depthMap.filter(GL_NEAREST);
        depthMap.wrapST(GL_CLAMP_TO_BORDER);
        glTexParameterfv(GL_TEXTURE_2D, GL_TEXTURE_BORDER_COLOR,new float[] {1,1,1,1});
        depthMap.tex2D(0,GL_DEPTH_COMPONENT16,width,height,GL_DEPTH_COMPONENT,GL_FLOAT);
        
        fbo = glGenFramebuffers();
        bindings.bindFrameBuffer(GL_FRAMEBUFFER,fbo);
        glFramebufferTexture2D(GL_FRAMEBUFFER,GL_DEPTH_ATTACHMENT,GL_TEXTURE_2D,depthMap.id(),0);
        
        glDrawBuffer(GL_NONE);
        glReadBuffer(GL_NONE);
    
        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
            throw new Exception("Unable to create FrameBuffer");
        }
    
        bindings.bindFrameBuffer(GL_FRAMEBUFFER,0);
        
        //generateBuffers();
    }
    
    
    public Texture texture() {
        return depthMap;
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
    
    public void bind() {
        bindings.bindFrameBuffer(GL_FRAMEBUFFER,fbo);
    }
    
    public void unbind() {
        bindings.bindFrameBuffer(GL_FRAMEBUFFER,0);
    }
    
    public void dispose() {
        unbind();
        glDeleteFramebuffers(fbo);
        //bindings.bindAttributeArray(vao);
        //glDisableVertexAttribArray(0);
        //glDisableVertexAttribArray(1);
        //bindings.bindBufferObject(GL_ARRAY_BUFFER, 0);
        //vbo.free();
        //ebo.free();
        //bindings.bindAttributeArray(0);
        //glDeleteVertexArrays(vao);
        depthMap.unbind();
        depthMap.delete();
    }
}
