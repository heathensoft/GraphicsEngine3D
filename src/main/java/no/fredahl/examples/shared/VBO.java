package no.fredahl.examples.shared;

import no.fredahl.engine.graphics.BufferObject;
import no.fredahl.engine.graphics.VertexAttribute;

import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;

/**
 * @author Frederik Dahl
 * 28/10/2021
 */


public class VBO extends BufferObject {
    
    public VertexAttribute attribute;
    
    
    public VBO(VertexAttribute attribute) {
        super(GL_ARRAY_BUFFER, GL_STATIC_DRAW);
        this.attribute = attribute;
    }
    
    public void enableAttribute() {
        attribute.enable();
        attribute.attributePointer(0,0);
    }
    
    @Override
    public void free() {
        attribute.disable();
        super.free();
    }
}
