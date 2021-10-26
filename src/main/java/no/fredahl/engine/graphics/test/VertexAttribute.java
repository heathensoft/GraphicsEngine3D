package no.fredahl.engine.graphics.test;


import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL41.GL_FIXED;

/**
 * @author Frederik Dahl
 * 23/10/2021
 */


public class VertexAttribute {
    
    
    public final int index;
    public final int type;
    public final int components;
    public final boolean normalized;
    
    
    public VertexAttribute(int index, int type, int components, boolean normalized) {
        this.index = index;
        this.type = type;
        this.components = components;
        this.normalized = normalized;
    }
    
    public int bytes() {
        return componentSizeBytes() * components;
    }
    
    public int componentSizeBytes() {
        switch (type) {
            case GL_FLOAT:
            case GL_FIXED:
                return Float.BYTES;
            case GL_UNSIGNED_BYTE:
            case GL_BYTE:
                return Byte.BYTES;
            case GL_UNSIGNED_SHORT:
            case GL_SHORT:
                return Short.BYTES;
            default: return 0;
        }
    }
    
    
    public static VertexAttribute position(int index) {
        return new VertexAttribute(index,GL_FLOAT,3,false);
    }
    
    public static VertexAttribute colorUnpacked(int index) {
        return new VertexAttribute(index,GL_FLOAT,3,false);
    }
    
    public static VertexAttribute colorPacked(int index) {
        return new VertexAttribute(index,GL_UNSIGNED_BYTE,4,true);
    }
    
    public static VertexAttribute normal(int index) {
        return new VertexAttribute(index,GL_FLOAT,3,false);
    }
    
    public static VertexAttribute textureCoordinates(int index) {
        return new VertexAttribute(index,GL_FLOAT,2,false);
    }

}
