package no.fredahl.engine.graphics.test;

import no.fredahl.engine.graphics.BufferObject;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL31.glDrawElementsInstanced;

/**
 * @author Frederik Dahl
 * 25/10/2021
 */


public class IndexArray extends BufferObject {
    
    private final int mode;
    private int type;
    
    
    public IndexArray(byte[] indices, int mode) {
        super(GL_ELEMENT_ARRAY_BUFFER,GL_STATIC_DRAW);
        this.type = GL_UNSIGNED_BYTE;
        this.mode = mode;
        ByteBuffer buffer = null;
        try {
            buffer = MemoryUtil.memAlloc(indices.length);
            buffer.put(indices).flip();
            bind();
            bufferData(buffer);
        } finally {
            if (buffer != null)
                MemoryUtil.memFree(buffer);
        }
    }
    
    public IndexArray(short[] indices, int mode) {
        super(GL_ELEMENT_ARRAY_BUFFER,GL_STATIC_DRAW);
        this.type = GL_UNSIGNED_SHORT;
        this.mode = mode;
        ShortBuffer buffer = null;
        try {
            buffer = MemoryUtil.memAllocShort(indices.length);
            buffer.put(indices).flip();
            bind();
            bufferData(buffer);
        } finally {
            if (buffer != null)
                MemoryUtil.memFree(buffer);
        }
        
    }
    
    public IndexArray(int[] indices, int mode) {
        super(GL_ELEMENT_ARRAY_BUFFER,GL_STATIC_DRAW);
        this.type = GL_UNSIGNED_INT;
        this.mode = mode;
        IntBuffer buffer = null;
        try {
            buffer = MemoryUtil.memAllocInt(indices.length);
            buffer.put(indices).flip();
            bind();
            bufferData(buffer);
        } finally {
            if (buffer != null)
                MemoryUtil.memFree(buffer);
        }
    }
    
    public void drawElements(int vertexCount) {
        glDrawElements(mode, vertexCount, type, 0);
    }
    
    public void drawElementsInstanced(int vertexCount, int instances) {
        glDrawElementsInstanced(mode,vertexCount,type,0,instances);
    }
    
    protected int mode() {
        return mode;
    }
    
    protected int type() {
        return type;
    }
    
}
