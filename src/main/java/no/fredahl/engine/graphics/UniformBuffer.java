package no.fredahl.engine.graphics;


import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL31.GL_UNIFORM_BUFFER;

/**
 * @author Frederik Dahl
 * 12/12/2021
 */


public class UniformBuffer extends BufferObject {
    
    private final int bindingPoint;
    
    public UniformBuffer(int bindingPoint, int usage) {
        super(GL_UNIFORM_BUFFER, usage);
        this.bindingPoint = bindingPoint;
    }
    
    public UniformBuffer(int bindingPoint) {
        this(bindingPoint,GL_DYNAMIC_DRAW);
    }
    
    public void bindBufferBase() {
        bindBufferBase(bindingPoint);
    }
    
    public int bindingPoint() {
        return bindingPoint;
    }
}
