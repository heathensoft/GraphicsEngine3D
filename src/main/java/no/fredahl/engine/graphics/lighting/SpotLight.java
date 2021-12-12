package no.fredahl.engine.graphics.lighting;

import org.joml.Vector3f;

/**
 * @author Frederik Dahl
 * 12/12/2021
 */


public class SpotLight {
    
    private PointLight light;
    private final Vector3f direction;
    private float cutoffInner;
    private float cutoffOuter;
    
    public SpotLight(PointLight light, Vector3f direction, float cutoffInner, float cutoffOuter) {
        this.light = light;
        this.direction = direction;
        this.cutoffInner = cutoffInner;
        this.cutoffOuter = cutoffOuter;
    }
    
    
    public PointLight pointLight() {
        return light;
    }
    
    public void setLight(PointLight light) {
        this.light = light;
    }
    
    public Vector3f direction() {
        return direction;
    }
    
    public void setDirection(Vector3f direction) {
        this.direction.set(direction);
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
}
