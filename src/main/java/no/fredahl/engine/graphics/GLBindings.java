package no.fredahl.engine.graphics;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL30.*;

/**
 * @author Frederik Dahl
 * 26/11/2021
 */


public class GLBindings {
    
    private int boundAttributeArray = 0;
    private int boundRenderBuffer = 0;
    private int boundFrameBuffer = 0;
    private int boundBufferObject = 0;
    private int boundProgram = 0;
    private int boundTexture = 0;
    
    private static GLBindings instance;
    
    private GLBindings() {
    }
    
    public static GLBindings get() {
        return instance == null ? new GLBindings() : instance;
    }
    
    
    
    
    public void bindAttributeArray(int id) {
        if (id == boundAttributeArray) return;
        glBindVertexArray(boundAttributeArray = id);
    }
    
    public void unbindAttributeArray() {
        bindAttributeArray(0);
    }
    
    public void bindBufferObject(int target, int id) {
        if (id == boundBufferObject) return;
        glBindBuffer(target, boundBufferObject = id);
    }
    
    public void unbindBufferObject(int target) {
        bindBufferObject(target,0);
    }
    
    public void useShader(int id) {
        if (id == boundProgram) return;
        glUseProgram(boundProgram = id);
    }
    
    public void bindTexture(int target, int id) {
        if (id == boundTexture) return;
        glBindTexture(target, boundTexture = id);
    }
    
    public void unbindTexture(int target) {
        bindTexture(target,0);
    }
    
    public void bindFrameBuffer(int target, int id) {
        if (id == boundFrameBuffer) return;
        glBindFramebuffer(target, boundFrameBuffer = id);
    }
    
    public void unbindFramebuffer(int target) {
        bindFrameBuffer(target,0);
    }
    
    public void bindRenderBuffer(int target, int id) {
        if (id == boundRenderBuffer) return;
        glBindRenderbuffer(target, boundRenderBuffer = id);
    }
    
    public void unbindRenderBuffer(int target) {
        bindRenderBuffer(target,0);
    }
    
}
