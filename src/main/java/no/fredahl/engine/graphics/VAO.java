package no.fredahl.engine.graphics;

import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

/**
 * @author Frederik Dahl
 * 04/04/2022
 */


public class VAO {
    
    protected final static GLBindings bindings = GLBindings.get();
    
    private final int handle;
    
    public VAO() {
        handle = glGenVertexArrays();
    }
    
    public void bind() {
        bindings.bindAttributeArray(handle);
    }
    
    public static void unbind() {
        bindings.unbindAttributeArray();
    }
    
    public void delete() {
        glDeleteVertexArrays(handle);
    }
    
}
