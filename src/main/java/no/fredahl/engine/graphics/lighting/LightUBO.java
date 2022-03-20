package no.fredahl.engine.graphics.lighting;

import no.fredahl.engine.graphics.BufferObject;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 * @author Frederik Dahl
 * 04/01/2022
 */


public abstract class LightUBO {
    
    protected BufferObject uniforms;
    protected FloatBuffer dlBuffer;
    protected FloatBuffer plBuffer;
    protected FloatBuffer slBuffer;
    protected IntBuffer intBuffer;
    
    protected int bindingPoint;
    
    
    public abstract void free();
    
}
