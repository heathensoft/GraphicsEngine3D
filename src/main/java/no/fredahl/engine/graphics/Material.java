package no.fredahl.engine.graphics;

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
    
    private static final Vector4f DEFAULT_COLOR = Color.WHITE;
    
    private Texture texture;
    private Vector4f ambientColor;
    private Vector4f diffuseColor;
    private Vector4f specularColor;
    private float reflectance;
    
    
    public Material(Vector4f ambientColor, Vector4f diffuseColor, Vector4f specularColor, float reflectance) {
        this.ambientColor = ambientColor;
        this.diffuseColor = diffuseColor;
        this.specularColor = specularColor;
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
        return ambientColor;
    }
    
    public Vector4f diffuseColor() {
        return diffuseColor;
    }
    
    public Vector4f specularColor() {
        return specularColor;
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
    
    
    public void setAmbientColor(Vector4f ambientColor) {
        this.ambientColor = ambientColor;
    }
    
    public void setDiffuseColor(Vector4f diffuseColor) {
        this.diffuseColor = diffuseColor;
    }
    
    public void setSpecularColor(Vector4f specularColor) {
        this.specularColor = specularColor;
    }
    
    public void setReflectance(float reflectance) {
        this.reflectance = reflectance;
    }
    
    public void setTexture(Texture texture) {
        this.texture = texture;
    }
}
