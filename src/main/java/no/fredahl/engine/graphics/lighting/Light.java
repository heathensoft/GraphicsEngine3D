package no.fredahl.engine.graphics.lighting;

import no.fredahl.engine.graphics.Color;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.nio.FloatBuffer;

/**
 * @author Frederik Dahl
 * 31/12/2021
 */


public abstract class Light<T extends Light<T>> {
    
    protected final static float DEFAULT_AMBIENT = 0.5f;
    protected final static float DEFAULT_DIFFUSE = 0.5f;
    protected final static float DEFAULT_CUTOFF = 45f;
    protected final static Vector3f DEFAULT_COL = new Vector3f(Color.WHITE_RGB);
    protected final static Vector3f DEFAULT_POS = new Vector3f(0f,0f,0f);
    protected final static Vector3f DEFAULT_DIR = new Vector3f(0f,-1.0f,Float.MIN_VALUE).normalize();
    
    protected final Vector3f color;
    protected float ambient;
    protected float diffuse;
    
    public Light(float r, float g, float b, float ambient, float diffuse) {
        this.color = new Vector3f(r,g,b);
        this.ambient = ambient;
        this.diffuse = diffuse;
    }
    
    public Light(Vector3f color, float ambient, float diffuse) {
        this(color.x,color.y,color.z,ambient,diffuse);
    }
    
    public Light(Vector3f color) {
        this(color,DEFAULT_AMBIENT,DEFAULT_DIFFUSE);
    }
    
    public Light() {
        this(DEFAULT_COL);
    }
    
    public abstract void set(T light);
    
    public abstract void setComponents(T light);
    
    public abstract void get(Matrix4f viewSpace, FloatBuffer buffer);
    
    public abstract T copy();
    
    //public abstract T viewSpace(Matrix4f worldToView);
    
    public Vector3f color() {
        return color;
    }
    
    public void setColor(Vector3f color) {
        setColor(color.x,color.y,color.z);
    }
    
    public void setColor(float r, float g, float b) {
        color.set(r, g, b);
    }
    
    public float ambient() {
        return ambient;
    }
    
    public void setAmbient(float ambient) {
        this.ambient = ambient;
    }
    
    public float diffuse() {
        return diffuse;
    }
    
    public void setDiffuse(float diffuse) {
        this.diffuse = diffuse;
    }
    
}
