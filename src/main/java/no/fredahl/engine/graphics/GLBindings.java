package no.fredahl.engine.graphics;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

/**
 * @author Frederik Dahl
 * 26/11/2021
 */


public class GLBindings {
    
    private int boundAttributeArray = 0;
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
    
    public void bindBufferObject(int id, int target) {
        if (id == boundBufferObject) return;
        glBindBuffer(target, boundBufferObject = id);
    }
    
    public void bindShaderProgram(int id) {
        if (id == boundProgram) return;
        glUseProgram(boundProgram = id);
    }
    
    public void bindTexture2D(int id) {
        if (id == boundTexture) return;
        glBindTexture(GL_TEXTURE_2D, boundTexture = id);
    }
    
}