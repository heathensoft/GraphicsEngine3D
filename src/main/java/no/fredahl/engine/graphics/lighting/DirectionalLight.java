package no.fredahl.engine.graphics.lighting;

import org.joml.Vector3f;

public class DirectionalLight {
    
    private final static Vector3f DEFAULT_AMBIENCE = new Vector3f(0.4f,0.4f,0.4f);
    private final static Vector3f DEFAULT_DIFFUSION = new Vector3f(1.0f,1.0f,1.0f);
    private final static Vector3f DEFAULT_SPECULAR = new Vector3f(0.5f,0.5f,0.5f);
    private final static Vector3f DEFAULT_DIRECTION = new Vector3f(0.0f,1.0f,0.0f).normalize();
    
    private final Vector3f ambient;
    private final Vector3f diffuse;
    private final Vector3f specular;
    private final Vector3f direction;
    
    
    public DirectionalLight(Vector3f ambient, Vector3f diffuse, Vector3f specular, Vector3f direction) {
        this.ambient = ambient;
        this.diffuse = diffuse;
        this.specular = specular;
        this.direction = direction;
    }
    
    public DirectionalLight(Vector3f ambient, Vector3f diffuse, Vector3f specular) {
        this(ambient,diffuse,specular,new Vector3f(DEFAULT_DIRECTION));
    }
    
    public DirectionalLight(Vector3f color, Vector3f direction) {
        this(new Vector3f(DEFAULT_AMBIENCE),color,new Vector3f(DEFAULT_SPECULAR),direction);
    }
    
    public DirectionalLight(Vector3f color) {
        this(new Vector3f(DEFAULT_AMBIENCE),color,new Vector3f(DEFAULT_SPECULAR));
    }
    
    public DirectionalLight() {
        this(new Vector3f(DEFAULT_DIFFUSION));
    }
    
    public Vector3f ambient() {
        return ambient;
    }
    
    public void setAmbient(Vector3f ambient) {
        this.ambient.set(ambient);
    }
    
    public Vector3f diffuse() {
        return diffuse;
    }
    
    public void setDiffuse(Vector3f diffuse) {
        this.diffuse.set(diffuse);
    }
    
    public Vector3f specular() {
        return specular;
    }
    
    public void setSpecular(Vector3f specular) {
        this.specular.set(specular);
    }
    
    public Vector3f direction() {
        return direction;
    }
    
    public void setDirection(Vector3f direction) {
        this.direction.set(direction);
    }
    
    public void setComponents(DirectionalLight directionalLight) {
        if (directionalLight != null) {
            this.setAmbient(directionalLight.ambient);
            this.setDiffuse(directionalLight.diffuse);
            this.setSpecular(directionalLight.specular);
        }
    }
}