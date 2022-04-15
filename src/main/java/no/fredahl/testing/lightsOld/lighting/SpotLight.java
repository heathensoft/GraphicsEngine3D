package no.fredahl.testing.lightsOld.lighting;

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
    
    
    /**
     *
     * @param light base point-light
     * @param direction the cone direction
     * @param cutoffInner the cone outer cutoff angle in degrees
     * @param cutoffOuter the cone outer cutoff angle in degrees
     */
    public SpotLight(PointLight light, Vector3f direction, float cutoffInner, float cutoffOuter) {
        this.light = light;
        this.direction = direction;
        this.cutoffInner = (float) Math.cos(Math.toRadians(cutoffInner/2));
        this.cutoffOuter = (float) Math.cos(Math.toRadians(cutoffOuter/2));
    }
    
    public SpotLight(PointLight light, Vector3f direction, float cutoff) {
        this(light,direction,cutoff,cutoff);
    }
    
    public SpotLight(PointLight light, Vector3f direction) {
        this(light,direction,12.5f);
    }
    
    public SpotLight(PointLight light) {
        this(light,new Vector3f(0,-1,0));
    }
    
    public SpotLight() {
        this(new PointLight());
    }
    
    public SpotLight set(SpotLight light) {
        if (light != null) {
            setDirection(light.direction);
            this.light.set(light.light);
            this.cutoffInner = light.cutoffInner;
            this.cutoffOuter = light.cutoffOuter;
        } return this;
    }
    
    public PointLight light() {
        return light;
    }
    
    public Vector3f direction() {
        return direction;
    }
    
    public SpotLight setDirection(Vector3f direction) {
        this.direction.set(direction);
        return this;
    }
    
    public SpotLight setDirection(float x, float y, float z) {
        this.direction.set(x, y, z);
        return this;
    }
    
    public float cutoffInner() {
        return cutoffInner;
    }
    
    public SpotLight setCutoffInner(float cutoff) {
        this.cutoffInner = cutoff;
        return this;
    }
    
    public float cutoffOuter() {
        return cutoffOuter;
    }
    
    public SpotLight setCutoffOuter(float cutoff) {
        this.cutoffOuter = cutoff;
        return this;
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
