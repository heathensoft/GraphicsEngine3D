package no.fredahl.example2;

import no.fredahl.engine.graphics.BufferObject;
import no.fredahl.engine.graphics.GLBindings;
import no.fredahl.engine.graphics.Heightmap;
import no.fredahl.engine.graphics.OBJFormatter;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

/**
 * @author Frederik Dahl
 * 18/12/2021
 */


public class Mesh {
    
    private static final GLBindings bindings = GLBindings.get();
    
    private final BufferObject positionBufferObject;
    private final BufferObject normalBufferObject;
    private final BufferObject elementBufferObject;
    private final int drawMode;
    private final int indices;
    private final int vao;
    
    public Mesh(Heightmap heightmap) {
    
        vao = glGenVertexArrays();
        bindings.bindAttributeArray(vao);
        positionBufferObject = new BufferObject(GL_ARRAY_BUFFER,GL_STATIC_DRAW);
        positionBufferObject.bind();
        heightmap.bufferPositionData(positionBufferObject);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        normalBufferObject = new BufferObject(GL_ARRAY_BUFFER,GL_STATIC_DRAW);
        normalBufferObject.bind();
        heightmap.bufferNormalsData(normalBufferObject);
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0);
        elementBufferObject = new BufferObject(GL_ELEMENT_ARRAY_BUFFER,GL_STATIC_DRAW);
        elementBufferObject.bind();
        heightmap.bufferIndexDataShort(elementBufferObject);
        
        
        
        bindings.bindAttributeArray(0);
        
        this.indices = heightmap.indices();
        this.drawMode = GL_TRIANGLE_STRIP;
    }
    
    public Mesh(float[] positionsArray, float[] normalsArray, short[] indicesArray, int drawMode) {
        
        vao = glGenVertexArrays();
        bindings.bindAttributeArray(vao);
        positionBufferObject = new BufferObject(GL_ARRAY_BUFFER,GL_STATIC_DRAW);
        positionBufferObject.bind();
        positionBufferObject.bufferData(positionsArray);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        normalBufferObject = new BufferObject(GL_ARRAY_BUFFER,GL_STATIC_DRAW);
        normalBufferObject.bind();
        normalBufferObject.bufferData(normalsArray);
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0);
        elementBufferObject = new BufferObject(GL_ELEMENT_ARRAY_BUFFER,GL_STATIC_DRAW);
        elementBufferObject.bind();
        elementBufferObject.bufferData(indicesArray);
        
        
        
        bindings.bindAttributeArray(0);
        
        this.drawMode = drawMode;
        this.indices = indicesArray.length;
    }
    
    public Mesh(OBJFormatter.Geometry geometry, int drawMode) {
        this(geometry.positions,geometry.normals,geometry.indices,drawMode);
    }
    
    public void render() {
        bindings.bindAttributeArray(vao);
        glDrawElements(drawMode,indices,GL_UNSIGNED_SHORT,0);
    }
    
    public void free() {
        bindings.bindBufferObject(GL_ARRAY_BUFFER, 0);
        positionBufferObject.free();
        normalBufferObject.free();
        elementBufferObject.free();
        bindings.bindAttributeArray(0);
        glDeleteVertexArrays(vao);
    }
}
