package no.fredahl.engine.graphics.lighting;

import org.joml.Vector3f;

import java.nio.FloatBuffer;

/**
 * @author Frederik Dahl
 * 12/12/2021
 */


public class SpotLight {
    
    private final PointLight light;
    private final Vector3f direction;
    private float cutoffInner;
    private float cutoffOuter;
    
    
    public SpotLight(PointLight light, Vector3f direction, float cutoffInner, float cutoffOuter) {
        this.light = light;
        this.direction = direction;
        this.cutoffInner = cutoffInner;
        this.cutoffOuter = cutoffOuter;
    }
    
    public SpotLight(PointLight light, Vector3f direction, float cutoff) {
        this(light,direction,cutoff,cutoff);
    }
    
    public SpotLight(PointLight light, Vector3f direction) {
        this(light,direction,1f);
    }
    
    public SpotLight(PointLight light) {
        this(light,new Vector3f(0,-1,0),1f);
    }
    
    public SpotLight() {
        this(new PointLight(),new Vector3f(0,-1,0),1f);
    }
    
    public void set(SpotLight light) {
        if (light != null) {
            setDirection(light.direction);
            this.light.set(light.light);
            this.cutoffInner = light.cutoffInner;
            this.cutoffOuter = light.cutoffOuter;
        }
    }
    
    public PointLight light() {
        return light;
    }
    
    public Vector3f direction() {
        return direction;
    }
    
    public void setDirection(Vector3f direction) {
        this.direction.set(direction);
    }
    
    public void setDirection(float x, float y, float z) {
        this.direction.set(x, y, z);
    }
    
    public float cutoffInner() {
        return cutoffInner;
    }
    
    public void setCutoffInner(float cutoff) {
        this.cutoffInner = cutoff;
    }
    
    public float cutoffOuter() {
        return cutoffOuter;
    }
    
    public void setCutoffOuter(float cutoff) {
        this.cutoffOuter = cutoff;
    }
    
    public void getSTD140(FloatBuffer buffer) {
        Attenuation att = light.attenuation;
        buffer.put(light.color.x).put(light.color.y).put(light.color.z).put(light.ambient);
        buffer.put(light.position.x).put(light.position.y).put(light.position.z).put(light.diffuse);
        buffer.put(direction.x).put(direction.y).put(direction.z).put(att.constant());
        buffer.put(att.linear()).put(att.quadratic()).put(cutoffInner).put(cutoffOuter);
    }
    
    public static int sizeSTD140(int count) {
        return count * 64;
    }
    
    public static int sizeInFloats(int count) {
        return count * 16;
    }
}
