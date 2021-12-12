package no.fredahl.example1;

import no.fredahl.engine.graphics.GLBindings;

import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

/**
 * @author Frederik Dahl
 * 29/06/2021
 */

public class VAO {

    private final static GLBindings bindings = GLBindings.get();
    private final int id;

    public VAO() {
        id = glGenVertexArrays();
    }

    public void bind() {
        bindings.bindAttributeArray(id);
    }

    public void unbind() {
        bindings.bindAttributeArray(0);
    }

    public void delete() { glDeleteVertexArrays(id);}

    public int id() { return id; }

}
