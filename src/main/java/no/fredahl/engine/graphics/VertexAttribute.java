package no.fredahl.engine.graphics;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL41.GL_FIXED;

/**
 * @author Frederik Dahl
 * 23/10/2021
 */


public class VertexAttribute {
    
    
    public static final class Usage {
        public static final int Position = 1;
        public static final int ColorUnpacked = 2;
        public static final int ColorPacked = 4;
        public static final int Normal = 8;
        public static final int TextureCoordinates = 16;
        public static final int Undefined_0 = 32;
        public static final int Undefined_1 = 64;
        public static final int Undefined_3 = 128;
    }
    
    public int usage;
    public final int index;
    public final int type;
    public final int components;
    public final boolean normalized;
    
    
    public static VertexAttribute position(int index) {
        return new VertexAttribute(index,Usage.Position,GL_FLOAT,3,false);
    }
    
    public static VertexAttribute colorUnpacked(int index) {
        return new VertexAttribute(index,Usage.ColorUnpacked,GL_FLOAT,3,false);
    }
    
    public static VertexAttribute colorPacked(int index) {
        return new VertexAttribute(index,Usage.ColorPacked,GL_UNSIGNED_BYTE,4,true);
    }
    
    public static VertexAttribute normal(int index) {
        return new VertexAttribute(index,Usage.Normal,GL_FLOAT,3,false);
    }
    
    public static VertexAttribute textureCoordinates(int index) {
        return new VertexAttribute(index,Usage.TextureCoordinates,GL_FLOAT,2,false);
    }
    
    public VertexAttribute(int index, int type, int components, boolean normalized) {
        this.index = index;
        this.type = type;
        this.components = components;
        this.normalized = normalized;
    }
    
    private VertexAttribute(int index, int usage, int type, int components, boolean normalized) {
        this.index = index;
        this.usage = usage;
        this.type = type;
        this.components = components;
        this.normalized = normalized;
    }
    
    public int size() {
        switch (type) {
            case GL_FLOAT:
            case GL_FIXED:
                return 4 * components;
            case GL_UNSIGNED_BYTE:
            case GL_BYTE:
                return components;
            case GL_UNSIGNED_SHORT:
            case GL_SHORT:
                return 2 * components;
            default: return 0;
        }
    }
    
    public int sizeBytes() {
        return size() * Float.BYTES;
    }
    

}
