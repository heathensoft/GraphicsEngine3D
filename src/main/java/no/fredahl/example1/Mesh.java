package no.fredahl.example1;

import no.fredahl.engine.graphics.BufferObject;
import no.fredahl.engine.graphics.Texture;
import no.fredahl.engine.graphics.VertexAttribute;
import org.lwjgl.opengl.GL30;

import static org.lwjgl.opengl.GL11.GL_INT;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;


/**
 * @author Frederik Dahl
 * 28/10/2021
 */


public class Mesh {
    
    private final VAO vao;
    private final EBO ebo;
    private final VBO vboPos;
    private final VBO vboTex;
    private final BufferObject intBO;
    private final Texture texture;
    
    public Mesh(float[] position, float[] texCoord, short[] indices, Texture texture) {
        vboPos = new VBO(new VertexAttribute(0, VertexAttribute.Type.POSITION_3D));
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
        intBO = new BufferObject(GL_ARRAY_BUFFER, GL_STATIC_DRAW);
        /*
        intBO.bind();
        int[] intArray = new int[position.length/3];
        intBO.bufferData(intArray);
        glEnableVertexAttribArray(2);
        GL30.glVertexAttribIPointer(2,1,GL_INT,0,0);
        
         */
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
        //intBO.free();
        vboTex.free();
        vboPos.free();
        ebo.free();
        vao.delete();
    }
}
