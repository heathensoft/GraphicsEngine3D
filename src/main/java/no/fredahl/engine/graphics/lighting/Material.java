package no.fredahl.engine.graphics.lighting;

import org.joml.Vector3f;

/**
 * @author Frederik Dahl
 * 04/12/2021
 */


public class Material {
    
    private final static Vector3f DEFAULT_EMISSION = new Vector3f(0.0f,0.0f,0.0f);
    private final static Vector3f DEFAULT_AMBIENCE = new Vector3f(0.2f,0.2f,0.2f);
    private final static Vector3f DEFAULT_DIFFUSION = new Vector3f(1.0f,1.0f,1.0f);
    private final static Vector3f DEFAULT_SPECULAR = new Vector3f(0.2f,0.2f,0.2f);
    
    private final static float REFLECTIVITY_FACTOR = 128.0f;
    private final static float DEFAULT_SHINE = 0.2f;
    
    private final Vector3f ambient;
    private final Vector3f diffuse;
    private final Vector3f specular;
    private final Vector3f emissivity;
    
    private float shine;
    
    public Material(Material material) {
        
        if (material == null) {
            this.ambient = new Vector3f(DEFAULT_AMBIENCE);
            this.diffuse = new Vector3f(DEFAULT_DIFFUSION);
            this.specular = new Vector3f(DEFAULT_SPECULAR);
            this.emissivity = new Vector3f(DEFAULT_EMISSION);
            this.shine = DEFAULT_SHINE * REFLECTIVITY_FACTOR;
        }
        else {
            this.ambient = new Vector3f(material.ambient);
            this.diffuse = new Vector3f(material.diffuse);
            this.specular = new Vector3f(material.specular);
            this.emissivity = new Vector3f(material.emissivity);
            this.shine = material.shine;
        }
    }
    
    public Material(Vector3f ambient, Vector3f diffuse, Vector3f specular, Vector3f emissivity, float shine) {
        this.ambient = ambient;
        this.diffuse = diffuse;
        this.specular = specular;
        this.emissivity = emissivity;
        this.shine = shine * REFLECTIVITY_FACTOR;
    }
    
    public Material(Vector3f ambient, Vector3f diffuse, Vector3f specular, float shine) {
        this(ambient,diffuse,specular,new Vector3f(DEFAULT_EMISSION),shine);
    }
    
    public Material(Vector3f color, float shine) {
        this(new Vector3f(DEFAULT_AMBIENCE),color,new Vector3f(DEFAULT_SPECULAR),shine);
    }
    
    public Material(Vector3f color) {
        this(color,DEFAULT_SHINE);
    }
    
    public Material() {
        this(DEFAULT_DIFFUSION);
    }
    
    
    public void set(Material material) {
        setAmbient(material.ambient);
        setDiffuse(material.diffuse);
        setSpecular(material.specular);
        setEmission(material.emissivity);
        setShine(material.shine);
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
    
    public Vector3f emissivity() {
        return emissivity;
    }
    
    public void setEmission(Vector3f emissivity) {
        this.emissivity.set(emissivity);
    }
    
    public float shine() {
        return shine;
    }
    
    public void setShine(float shine) {
        this.shine = shine;
    }
}
