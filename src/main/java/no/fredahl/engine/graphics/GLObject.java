package no.fredahl.engine.graphics;

import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;

/**
 * @author Frederik Dahl
 * 23/10/2021
 */


public class GLObject {
    
    
    private VAO boundVAO = null;
    private static GLObject instance;
    private final Set<VAO> vaoSet = new HashSet<>();
    
    
    private GLObject() {}
    
    
    public static GLObject get() {
        return instance == null ? instance = new GLObject() : instance;
    }
    
    public VAO createDynamic(int vertices, VertexAttribute... attributes) {
        
        return null;
    }
    
    
    public VAO VAO() {
        VAO vao = new VAO(this, glGenVertexArrays());
        vaoSet.add(vao);
        return vao;
    }
    
    
    protected void storeDataInAttributeList(VAO vao, VertexAttribute attribute, float[] data) {
        bind(vao);
        FloatBuffer buffer = null;
        final int dataLength = data.length;
        final int attributeSize = attribute.components;
        if (dataLength % attributeSize != 0) {
            freeAll();
            throw new IllegalStateException("Attribute not matching data");
        }
        final int vertexCount = dataLength / attributeSize;
        final int vaoVertices = vao.vertexCount();
        if (vaoVertices == 0) {
            vao.setVertexCount(vertexCount);
            vao.setCapacity(dataLength);
        }
        else if (vaoVertices != vertexCount) {
            freeAll();
            throw new IllegalStateException("Vertex count not matching");
        }
        try {
            VBO vbo = new VBO(glGenBuffers(),new VertexAttribute[] {attribute});
            buffer = MemoryUtil.memAllocFloat(dataLength);
            buffer.put(data).flip();
            glBindBuffer(GL_ARRAY_BUFFER,vbo.id());
            glEnableVertexAttribArray(attribute.index);
            glBufferData(GL_ARRAY_BUFFER,buffer,GL_STATIC_DRAW);
            glVertexAttribPointer(
                    attribute.index,
                    attribute.components,
                    attribute.type,
                    attribute.normalized,
                    0,
                    0
            );
            try {
                vao.addVBO(vbo);
            }catch (Exception e) {
                freeAll();
                throw new IllegalStateException(e);
            }
        } finally {
            MemoryUtil.memFree(buffer);
            glBindBuffer(GL_ARRAY_BUFFER,0);
        }
    }
    
    public void freeAll() {
        for (VAO vao : vaoSet) {
            free(vao);
        }
    }
    
    protected void free(VAO vao) {
        bind(vao);
        List<VBO> vboList = vao.bufferObjects();
        for (VBO vbo : vboList) {
            vbo.free();
        }
        glDeleteVertexArrays(vao.id());
        boundVAO = null;
    }
    
    protected void bind(VAO vao) {
        if (vao != boundVAO) {
            glBindVertexArray(vao.id());
            boundVAO = vao;
        }
    }
    
    protected void unbind() {
        glBindVertexArray(0);
        boundVAO = null;
    }
}
