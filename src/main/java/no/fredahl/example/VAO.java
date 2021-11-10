package no.fredahl.example;

import static org.lwjgl.opengl.GL30.*;

/**
 * @author Frederik Dahl
 * 29/06/2021
 */

public class VAO {

    private final int id;

    public VAO() {
        id = glGenVertexArrays();
    }

    public void bind() {
        glBindVertexArray(id);
    }

    public void unbind() {
        glBindVertexArray(0);
    }

    public void delete() { glDeleteVertexArrays(id);}

    public int id() { return id; }

}
