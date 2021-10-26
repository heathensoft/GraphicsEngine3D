package no.fredahl.engine.graphics.test;

import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;

/**
 * @author Frederik Dahl
 * 23/10/2021
 */


public class VBO {
    
    private final int id;
    private final VertexAttribute[] attributes;
    
    protected VBO(int id, VertexAttribute[] attributes) {
        this.attributes = attributes;
        this.id = id;
    }
    
    protected int strideBytes() {
        int stride = 0;
        for (VertexAttribute attribute : attributes) {
            stride += attribute.bytes();
        }
        return stride;
    }
    
    protected void enableAttributes() {
        for (VertexAttribute attribute : attributes) {
            glEnableVertexAttribArray(attribute.index);
        }
    }
    
    protected void disableAttributes() {
        for (VertexAttribute attribute : attributes) {
            glDisableVertexAttribArray(attribute.index);
        }
    }
    
    protected VertexAttribute[] attributes() {
        return attributes;
    }
    
    protected int id() {
        return id;
    }
    
    protected void free() {
        disableAttributes();
        glDeleteBuffers(id);
    }
    
}
