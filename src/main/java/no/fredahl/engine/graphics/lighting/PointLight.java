package no.fredahl.engine.graphics.lighting;

import org.joml.Vector3f;

/**
 * @author Frederik Dahl
 * 26/11/2021
 */


public class PointLight {
    
    private final static Vector3f DEFAULT_AMBIENCE = new Vector3f(0.0f,0.0f,0.0f);
    private final static Vector3f DEFAULT_DIFFUSION = new Vector3f(1.0f,1.0f,1.0f);
    private final static Vector3f DEFAULT_SPECULAR = new Vector3f(0.5f,0.5f,0.5f);
    private final static Vector3f DEFAULT_POSITION = new Vector3f(0.0f,0.0f,0.0f);
    
    private final static Attenuation DEFAULT_ATTENUATION = Attenuation.ATT_65;
    
    private final Vector3f ambient;
    private final Vector3f diffuse;
    private final Vector3f specular;
    private final Vector3f position;
    private final Attenuation attenuation;
    
    public PointLight(Vector3f position, Vector3f ambient, Vector3f diffuse, Vector3f specular, Attenuation attenuation) {
        this.position = position;
        this.ambient = ambient;
        this.diffuse = diffuse;
        this.specular = specular;
        this.attenuation = attenuation;
    }
    
    public PointLight(Vector3f position, Vector3f ambient, Vector3f diffuse, Vector3f specular) {
        this(position,ambient,diffuse,specular,new Attenuation(DEFAULT_ATTENUATION));
    }
    
    public PointLight(Vector3f ambient, Vector3f diffuse, Vector3f specular, Attenuation attenuation) {
        this(new Vector3f(DEFAULT_POSITION),ambient,diffuse,specular,attenuation);
    }
    
    public PointLight(Vector3f ambient, Vector3f diffuse, Vector3f specular) {
        this(ambient,diffuse,specular,new Attenuation(DEFAULT_ATTENUATION));
    }
    
    public PointLight(Vector3f position, Vector3f color, Attenuation attenuation) {
        this(position,new Vector3f(DEFAULT_AMBIENCE),color,new Vector3f(DEFAULT_SPECULAR),attenuation);
    }
    
    public PointLight(Vector3f position, Vector3f color) {
        this(position,color,new Attenuation(DEFAULT_ATTENUATION));
    }
    
    public PointLight(Vector3f color, Attenuation attenuation) {
        this(new Vector3f(DEFAULT_POSITION),color,attenuation);
    }
    
    public PointLight(Vector3f color) {
        this(color,new Attenuation(DEFAULT_ATTENUATION));
    }
    
    public PointLight() {
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
    
    public Vector3f position() {
        return position;
    }
    
    public void setPosition(Vector3f position) {
        this.position.set(position);
    }
    
    public Attenuation attenuation() {
        return attenuation;
    }
    
    public void setAttenuation(Attenuation attenuation) {
        this.attenuation.set(attenuation);
    }
    
    public void setComponents(PointLight pointLight) {
        if (pointLight != null) {
            setAmbient(pointLight.ambient);
            setDiffuse(pointLight.diffuse);
            setSpecular(pointLight.specular);
            setAttenuation(pointLight.attenuation);
        }
    }
}
