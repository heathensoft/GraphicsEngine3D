package no.fredahl.engine.graphics.light;

import org.joml.Vector3f;

/**
 * @author Frederik Dahl
 * 03/12/2021
 */


public class Components {
    
    private Vector3f ambient;
    private Vector3f diffuse;
    private Vector3f specular;
    
    public Components(Vector3f color) {
        this(new Vector3f(color),new Vector3f(color),new Vector3f(color));
    }
    
    public Components(Vector3f ambient, Vector3f diffuse, Vector3f specular) {
        this.ambient = ambient;
        this.diffuse = diffuse;
        this.specular = specular;
    }
    
    public Vector3f getAmbient() {
        return ambient;
    }
    
    public void setAmbient(Vector3f ambient) {
        this.ambient = ambient;
    }
    
    public Vector3f getDiffuse() {
        return diffuse;
    }
    
    public void setDiffuse(Vector3f diffuse) {
        this.diffuse = diffuse;
    }
    
    public Vector3f getSpecular() {
        return specular;
    }
    
    public void setSpecular(Vector3f specular) {
        this.specular = specular;
    }
}
