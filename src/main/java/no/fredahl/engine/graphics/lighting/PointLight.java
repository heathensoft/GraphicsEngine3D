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
    
    public PointLight setAmbient(float ambient) {
        this.ambient = ambient;
        return this;
    }
    
    public float diffuse() {
        return diffuse;
    }
    
    public PointLight setDiffuse(float diffuse) {
        this.diffuse = diffuse;
        return this;
    }
    
    public Vector3f color() {
        return color;
    }
    
    public PointLight setColor(Vector3f color) {
        this.color.set(color);
        return this;
    }
    
    public PointLight setColor(float r, float g, float b) {
        this.color.set(r, g, b);
        return this;
    }
    
    public Vector3f position() {
        return position;
    }
    
    public PointLight setPosition(Vector3f position) {
        this.position.set(position);
        return this;
    }
    
    public PointLight setPosition(float x, float y, float z) {
        this.position.set(x,y,z);
        return this;
    }
    
    public PointLight translate(Vector3f translation) {
        this.position.add(translation);
        return this;
    }
    
    public PointLight translate(float x, float y, float z) {
        this.position.add(x,y,z);
        return this;
    }
    
    public Attenuation attenuation() {
        return attenuation;
    }
    
    public PointLight setAttenuation(Attenuation attenuation) {
        this.attenuation.set(attenuation);
        return this;
    }
    
    public PointLight setAttenuation(float c, float l, float q) {
        this.attenuation.set(c, l, q);
        return this;
    }
    
    public PointLight setComponents(PointLight light) {
        if (light != null) {
            setColor(light.color);
            setAmbient(light.ambient);
            setDiffuse(light.diffuse);
            setAttenuation(light.attenuation);
        } return this;
    }
    
    public PointLight set(PointLight light) {
        if (light != null) {
            setColor(light.color);
            setAmbient(light.ambient);
            setDiffuse(light.diffuse);
            setPosition(light.position);
            setAttenuation(light.attenuation);
        } return this;
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
