package no.fredahl.examples.cube;

import no.fredahl.engine.graphics.VertexAttribute;
import no.fredahl.examples.shared.EBO;
import no.fredahl.examples.shared.VAO;
import no.fredahl.examples.shared.VBO;


/**
 * @author Frederik Dahl
 * 28/10/2021
 */


public class Mesh {
    
    private final VAO vao;
    private final EBO ebo;
    private final VBO vboPos;
    private final VBO vboColor;
    
    public Mesh(float[] position, float[] color, short[] indices) {
        vboPos = new VBO(new VertexAttribute(0, VertexAttribute.Type.POSITION_3D));
        vboColor = new VBO(new VertexAttribute(1,VertexAttribute.Type.COLOR_RGB));
        ebo = new EBO(indices.length);
        vao = new VAO();
        vao.bind();
        vboPos.bind();
        vboPos.bufferData(position);
        vboPos.enableAttribute();
        vboColor.bind();
        vboColor.bufferData(color);
        vboColor.enableAttribute();
        ebo.bind();
        ebo.bufferData(indices);
        vao.unbind();
    }
    
    public void render() {
        vao.bind();
        ebo.drawElements();
        vao.unbind();
    }
    
    public void free() {
        vboColor.free();
        vboPos.free();
        ebo.free();
        vao.delete();
    }
}
