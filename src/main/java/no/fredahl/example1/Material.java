package no.fredahl.example1;

import no.fredahl.engine.graphics.Color;
import org.joml.Vector4f;

/**
 * todo: I should probably use vec3's instead. Then use a single float for alpha. Yep
 *
 * @author Frederik Dahl
 * 26/11/2021
 */


public class Material {
    
    
    public static final Material BRONZE = new Material(
            new Vector4f(0.2125f,0.1275f,0.054f,1),
            new Vector4f(0.714f,0.4284f,0.18144f,1),
            new Vector4f(0.393548f,0.271906f,0.166721f,1),0.2f);
    
    private static final Vector4f DEFAULT_COLOR = Color.WHITE_RGBA;
    
    private Texture texture;
    private Vector4f ambient;
    private Vector4f diffuse;
    private Vector4f specular
            ;
    private float reflectance;
    
    
    public Material(Vector4f ambient, Vector4f diffuse, Vector4f specular, float reflectance) {
        this.ambient = ambient;
        this.diffuse = diffuse;
        this.specular = specular;
        this.reflectance = reflectance;
        this.texture = null;
    }
    
    public Material() {
        this(DEFAULT_COLOR, DEFAULT_COLOR,DEFAULT_COLOR,0);
    }
    
    public Material(Texture texture, float reflectance) {
        this();
        this.texture = texture;
        this.reflectance = reflectance;
    }
    
    public Material(Texture texture) {
        this(texture,0);
    }
    
    public Vector4f ambientColor() {
        return ambient;
    }
    
    public Vector4f diffuseColor() {
        return diffuse;
    }
    
    public Vector4f specularColor() {
        return specular;
    }
    
    public float reflectance() {
        return reflectance;
    }
    
    public Texture texture() {
        return texture;
    }
    
    public boolean isTextured() {
        return this.texture != null;
    }
    
    
    public void setAmbient(Vector4f ambient) {
        this.ambient = ambient;
    }
    
    public void setDiffuse(Vector4f diffuse) {
        this.diffuse = diffuse;
    }
    
    public void setSpecular(Vector4f specular) {
        this.specular = specular;
    }
    
    public void setReflectance(float reflectance) {
        this.reflectance = reflectance;
    }
    
    public void setTexture(Texture texture) {
        this.texture = texture;
    }
}
