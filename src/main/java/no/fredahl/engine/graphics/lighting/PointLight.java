package no.fredahl.engine.graphics.lighting;

import no.fredahl.engine.graphics.Color;
import org.joml.Vector3f;

import java.nio.FloatBuffer;

/**
 * @author Frederik Dahl
 * 26/11/2021
 */


public class PointLight {
    
    private final static float DEFAULT_AMBIENCE = 0.01f;
    private final static float DEFAULT_DIFFUSE = 1.0f;
    private final static Attenuation DEFAULT_ATTENUATION = Attenuation.ATT_65;
    private final static Vector3f DEFAULT_COLOR = new Vector3f(Color.WHITE_RGB);
    
    protected float ambient;
    protected float diffuse;
    protected final Vector3f color;
    protected final Vector3f position;
    protected final Attenuation attenuation;
    
    
    public PointLight(Vector3f color, Vector3f position, float ambient, float diffuse, Attenuation attenuation) {
        this.color = color;
        this.position = position;
        this.ambient = ambient;
        this.diffuse = diffuse;
        this.attenuation = attenuation;
    }
    
    public PointLight(Vector3f color, Vector3f position, float ambient, float diffuse) {
        this(color,position,ambient,diffuse,new Attenuation(DEFAULT_ATTENUATION));
    }
    
    public PointLight(Vector3f color, Vector3f position) {
        this(color,position,DEFAULT_AMBIENCE,DEFAULT_DIFFUSE);
    }
    
    public PointLight(Vector3f color) {
        this(color,new Vector3f());
    }
    
    public PointLight() {
        this(DEFAULT_COLOR);
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
    
    public Vector3f color() {
        return color;
    }
    
    public void setColor(Vector3f color) {
        this.color.set(color);
    }
    
    public void setColor(float r, float g, float b) {
        this.color.set(r, g, b);
    }
    
    public Vector3f position() {
        return position;
    }
    
    public void setPosition(Vector3f position) {
        this.position.set(position);
    }
    
    public void setPosition(float x, float y, float z) {
        this.position.set(x,y,z);
    }
    
    public void translate(Vector3f translation) {
        this.position.add(translation);
    }
    
    public void translate(float x, float y, float z) {
        this.position.add(x,y,z);
    }
    
    public Attenuation attenuation() {
        return attenuation;
    }
    
    public void setAttenuation(Attenuation attenuation) {
        this.attenuation.set(attenuation);
    }
    
    public void setAttenuation(float c, float l, float q) {
        this.attenuation.set(c, l, q);
    }
    
    public void setComponents(PointLight light) {
        if (light != null) {
            setColor(light.color);
            setAmbient(light.ambient);
            setDiffuse(light.diffuse);
            setAttenuation(light.attenuation);
        }
    }
    
    public void set(PointLight light) {
        if (light != null) {
            setColor(light.color);
            setAmbient(light.ambient);
            setDiffuse(light.diffuse);
            setPosition(light.position);
            setAttenuation(light.attenuation);
        }
    }
    
    public void getSTD140(FloatBuffer buffer) {
        buffer.put(color.x).put(color.y).put(color.z).put(0.0f);
        buffer.put(position.x).put(position.y).put(position.z).put(ambient);
        buffer.put(diffuse).put(attenuation.constant()).put(attenuation.linear()).put(attenuation.quadratic());
    }
    
    public static int sizeSTD140(int count) {
        return count * 48;
    }
    
    public static int sizeInFloats(int count) {
        return count * 12;
    }
}
