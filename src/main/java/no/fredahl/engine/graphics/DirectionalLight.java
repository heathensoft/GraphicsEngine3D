package no.fredahl.engine.graphics;

import org.joml.Vector3f;

public class DirectionalLight {
    
    protected Vector3f color;
    protected Vector3f direction;
    protected float intensity;
    
    public DirectionalLight(Vector3f color, Vector3f direction, float intensity) {
        this.color = color;
        this.direction = direction;
        this.intensity = intensity;
    }
    
    public Vector3f color() {
        return color;
    }
    
    public Vector3f direction() {
        return direction;
    }
    
    public float intensity() {
        return intensity;
    }

    public void setColor(Vector3f color) {
        this.color = color;
    }
    
    public void setDirection(Vector3f direction) {
        this.direction = direction;
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }
}