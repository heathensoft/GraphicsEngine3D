package no.fredahl.engine.graphics;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL41.GL_FIXED;

/**
 * @author Frederik Dahl
 * 26/10/2021
 */


public class VertexAttribute implements Comparable<VertexAttribute>{
    
    
    public enum Type {
        
        FLOAT_1(GL_FLOAT,1,false),
        FLOAT_2(GL_FLOAT,2,false),
        FLOAT_3(GL_FLOAT,3,false),
        FLOAT_4(GL_FLOAT,4,false),
        POSITION_2D(GL_FLOAT,2,false),
        POSITION_3D(GL_FLOAT,3,false),
        COLOR_RGB(GL_FLOAT,3,false),
        COLOR_RGBA(GL_FLOAT,4,false),
        COLOR_PACKED(GL_UNSIGNED_BYTE,4,true),
        TEX_COORDINATE(GL_FLOAT,2,false),
        NORMAL(GL_FLOAT,2,false);
        
        private final int type;
        private final int comp;
        private final boolean norm;
        
        Type(int type, int comp, boolean norm) {
            this.type = type;
            this.comp = comp;
            this.norm = norm;
        }
    }
    
    private final int index;
    private final int type;
    private final int comp;
    private final boolean norm;
    
    public VertexAttribute(int index, Type type) {
        this(index,type.type,type.comp,type.norm);
    }
    
    private VertexAttribute(int index, int type, int comp, boolean norm) {
        this.index = index;
        this.type = type;
        this.comp = comp;
        this.norm = norm;
    }
    
    public int size() {
        return comp * primitiveSize();
    }
    
    public void enable() {
        glEnableVertexAttribArray(index);
    }
    
    public void disable() {
        glDisableVertexAttribArray(index);
    }
    
    public void attributePointer(int stride, int pointer) {
        glVertexAttribPointer(index,comp,type,norm,stride,pointer);
    }
    
    private int primitiveSize() {
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
    
    public int index() {
        return index;
    }
    
    public int type() {
        return type;
    }
    
    public int components() {
        return comp;
    }
    
    public boolean isNormalized() {
        return norm;
    }
    
    @Override
    public int compareTo(VertexAttribute o) {
        return Integer.compare(this.index,o.index);
    }
    
}
