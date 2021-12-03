package no.fredahl.engine.graphics.light;

import org.joml.Vector3f;

/**
 * @author Frederik Dahl
 * 03/12/2021
 */


public class DirectionalLight {
    
    private Phong components;
    private Vector3f direction;
    
    public DirectionalLight(Vector3f color, Vector3f direction) {
        this(new Phong(color),direction);
    }
    
    public DirectionalLight(Vector3f ambient, Vector3f diffuse, Vector3f specular, Vector3f direction) {
        this(new Phong(ambient, diffuse, specular),direction);
    }
    
    public DirectionalLight(Phong components, Vector3f direction) {
        this.components = components;
        this.direction = direction;
    }
    
    public Phong getComponents() {
        return components;
    }
    
    public void setComponents(Phong components) {
        this.components = components;
    }
    
    public Vector3f getDirection() {
        return direction;
    }
    
    public void setDirection(Vector3f direction) {
        this.direction = direction;
    }
}
