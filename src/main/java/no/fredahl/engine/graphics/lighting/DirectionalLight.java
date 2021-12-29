package no.fredahl.engine.graphics.lighting;

import no.fredahl.engine.graphics.Color;
import org.joml.Vector3f;

import java.nio.FloatBuffer;

/**
 * @author Frederik Dahl
 * 17/12/2021
 */


public class DirectionalLight {
    
    private final static float DEFAULT_AMBIENCE = 0.5f;
    private final static float DEFAULT_DIFFUSE = 0.5f;
    private final static Vector3f DEFAULT_COLOR = new Vector3f(Color.WHITE_RGB);
    private final static Vector3f DEFAULT_DIRECTION = new Vector3f(0f,-1.0f,0.000001f).normalize();
    
    private final Vector3f color;
    private final Vector3f direction;
    
    private float ambient;
    private float diffuse;
    
    
    public DirectionalLight(Vector3f color, Vector3f direction, float ambient, float diffuse) {
        this.color = color;
        this.direction = direction;
        this.ambient = ambient;
        this.diffuse = diffuse;
    }
    
    public DirectionalLight(Vector3f color, Vector3f direction) {
        this(color,direction,DEFAULT_AMBIENCE,DEFAULT_DIFFUSE);
    }
    
    public DirectionalLight(Vector3f color) {
        this(color,new Vector3f(DEFAULT_DIRECTION));
    }
    
    public DirectionalLight() {
        this(new Vector3f(DEFAULT_COLOR));
    }
    
    public float ambient() {
        return ambient;
    }
    
    public DirectionalLight setAmbient(float ambient) {
        this.ambient = ambient;
        return this;
    }
    
    public float diffuse() {
        return diffuse;
    }
    
    public DirectionalLight setDiffuse(float diffuse) {
        this.diffuse = diffuse;
        return this;
    }
    
    public Vector3f color() {
        return color;
    }
    
    public DirectionalLight setColor(Vector3f color) {
        this.color.set(color);
        return this;
    }
    
    public DirectionalLight setColor(float r, float g, float b) {
        this.color.set(r, g, b);
        return this;
    }
    
    public Vector3f direction() {
        return direction;
    }
    
    public DirectionalLight setDirection(Vector3f direction) {
        this.direction.set(direction);
        return this;
    }
    
    public DirectionalLight setDirection(float x, float y, float z) {
        this.direction.set(x,y,z);
        return this;
    }
    
    public DirectionalLight setComponents(DirectionalLight light) {
        if (light != null) {
            this.color.set(light.color);
            this.ambient = light.ambient;
            this.diffuse = light.diffuse;
        } return this;
    }
    
    public DirectionalLight set(DirectionalLight light) {
        if (light != null) {
            this.direction.set(light.direction);
            this.color.set(light.color);
            this.ambient = light.ambient;
            this.diffuse = light.diffuse;
        } return this;
    }
    
    public void getSTD140(FloatBuffer buffer) {
        buffer.put(color.x).put(color.y).put(color.z).put(ambient);
        buffer.put(direction.x).put(direction.y).put(direction.z).put(diffuse);
    }
    
    public static int sizeSTD140(int count) {
        return count * 32;
    }
    
    public static int sizeInFloats(int count) {
        return count * 8;
    }
    
}
