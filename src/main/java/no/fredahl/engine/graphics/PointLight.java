package no.fredahl.engine.graphics;

import org.joml.Vector3f;

/**
 * @author Frederik Dahl
 * 26/11/2021
 */


public class PointLight {
    
    private Vector3f color;
    private Vector3f position;
    private Attenuation attenuation;
    protected float intensity;
    
    public PointLight(Vector3f color, Vector3f position, float intensity) {
        this.attenuation = new Attenuation(1, 0, 0);
        this.color = color;
        this.position = position;
        this.intensity = intensity;
    }
    
    public PointLight(Vector3f color, Vector3f position, float intensity, Attenuation attenuation) {
        this(color, position, intensity);
        this.attenuation = attenuation;
    }
    
    public Vector3f color() {
        return color;
    }
    
    public Vector3f position() {
        return position;
    }
    
    public float intensity() {
        return intensity;
    }
    
    public Attenuation attenuation() {
        return attenuation;
    }
    
    public void setColor(Vector3f color) {
        this.color = color;
    }
    
    public void setPosition(Vector3f position) {
        this.position = position;
    }
    
    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }
    
    public void setAttenuation(Attenuation attenuation) {
        this.attenuation = attenuation;
    }
    
    public static class Attenuation {
        
        private float constant;
        private float linear;
        private float exponent;
        
        public Attenuation(float constant, float linear, float exponent) {
            this.constant = constant;
            this.linear = linear;
            this.exponent = exponent;
        }
        
        public float constant() {
            return constant;
        }
    
        public float linear() {
            return linear;
        }
    
        public float exponent() {
            return exponent;
        }
        
        public void setConstant(float constant) {
            this.constant = constant;
        }
        
        public void setLinear(float linear) {
            this.linear = linear;
        }
        
        public void setExponent(float exponent) {
            this.exponent = exponent;
        }
    }
}
