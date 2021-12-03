package no.fredahl.engine.graphics.light;

import org.joml.Vector3f;

/**
 * @author Frederik Dahl
 * 03/12/2021
 */


public class PointLight {
 
    private Components components;
    private Attenuation attenuation;
    private Vector3f position;
    
    public PointLight(Components components, Attenuation attenuation, Vector3f position) {
        this.components = components;
        this.attenuation = attenuation;
        this.position = position;
    }
    
    public PointLight(Vector3f ambient, Vector3f diffuse, Vector3f specular, Attenuation attenuation, Vector3f position) {
        this(new Components(ambient, diffuse, specular),attenuation,position);
    }
    
    public PointLight(Vector3f color, Attenuation attenuation, Vector3f position) {
        this(new Components(color),attenuation,position);
    }
    
    public Components getComponents() {
        return components;
    }
    
    public void setComponents(Components components) {
        this.components = components;
    }
    
    public Attenuation getAttenuation() {
        return attenuation;
    }
    
    public void setAttenuation(Attenuation attenuation) {
        this.attenuation = attenuation;
    }
    
    public Vector3f getPosition() {
        return position;
    }
    
    public void setPosition(Vector3f position) {
        this.position = position;
    }
}
