package no.fredahl.examples.cube;

import no.fredahl.engine.graphics.Texture;
import no.fredahl.engine.graphics.VertexAttribute;
import no.fredahl.examples.shared.EBO;
import no.fredahl.examples.shared.VAO;
import no.fredahl.examples.shared.VBO;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;


/**
 * @author Frederik Dahl
 * 28/10/2021
 */


public class Mesh {
    
    private final VAO vao;
    private final EBO ebo;
    private final VBO vboPos;
    private final VBO vboTex;
    private final Texture texture;
    
    public Mesh(float[] position, float[] texCoord, short[] indices, Texture texture) {
        vboPos = new VBO(new VertexAttribute(0, VertexAttribute.Type.POSITION_3D));
        //vboColor = new VBO(new VertexAttribute(1,VertexAttribute.Type.COLOR_PACKED));
        vboTex = new VBO(new VertexAttribute(1, VertexAttribute.Type.TEX_COORDINATE));
        ebo = new EBO(indices.length);
        vao = new VAO();
        vao.bind();
        vboPos.bind();
        vboPos.bufferData(position);
        vboPos.enableAttribute();
        vboTex.bind();
        vboTex.bufferData(texCoord);
        vboTex.enableAttribute();
        ebo.bind();
        ebo.bufferData(indices);
        vao.unbind();
        this.texture = texture;
    }
    
    public void render() {
        glActiveTexture(GL_TEXTURE0);
        texture.bind();
        vao.bind();
        ebo.drawElements();
        vao.unbind();
    }
    
    public void free() {
        texture.unbind();
        texture.free();
        vboTex.free();
        vboPos.free();
        ebo.free();
        vao.delete();
    }
}
