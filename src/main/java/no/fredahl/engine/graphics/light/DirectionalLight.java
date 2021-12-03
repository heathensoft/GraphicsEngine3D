package no.fredahl.engine.graphics.light;

import org.joml.Vector3f;

/**
 * @author Frederik Dahl
 * 03/12/2021
 */


public class DirectionalLight {
    
    private Components components;
    private Vector3f direction;
    
    public DirectionalLight(Vector3f color, Vector3f direction) {
        this(new Components(color),direction);
    }
    
    public DirectionalLight(Vector3f ambient, Vector3f diffuse, Vector3f specular, Vector3f direction) {
        this(new Components(ambient, diffuse, specular),direction);
    }
    
    public DirectionalLight(Components components, Vector3f direction) {
        this.components = components;
        this.direction = direction;
    }
    
    public Components getComponents() {
        return components;
    }
    
    public void setComponents(Components components) {
        this.components = components;
    }
    
    public Vector3f getDirection() {
        return direction;
    }
    
    public void setDirection(Vector3f direction) {
        this.direction = direction;
    }
}
