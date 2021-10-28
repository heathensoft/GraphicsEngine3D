package no.fredahl.examples.shared;

import no.fredahl.engine.graphics.BufferObject;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;

/**
 * @author Frederik Dahl
 * 28/10/2021
 */


public class EBO extends BufferObject {
    
    private final int vertexCount;
    
    public EBO(int vertexCount) {
        super(GL_ELEMENT_ARRAY_BUFFER, GL_STATIC_DRAW);
        this.vertexCount = vertexCount;
    }
    
    public void drawElements() {
        glDrawElements(GL_TRIANGLES, vertexCount, GL_UNSIGNED_SHORT, 0);
    }
}
