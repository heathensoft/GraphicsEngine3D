package no.fredahl.engine.graphics.lighting;

import org.joml.Vector3f;

import java.nio.FloatBuffer;

/**
 * @author Frederik Dahl
 * 04/12/2021
 */


public class Material {
    
    public static final Material BRASS = new Material(
            new Vector3f(0.329412f, 0.223529f, 0.027451f),
            new Vector3f(0.780392f, 0.568627f, 0.113725f),
            new Vector3f(0.992157f, 0.941176f, 0.807843f),27.8974f / 128);
    
    public static final Material BRONZE = new Material(
            new Vector3f(0.2125f, 0.1275f, 0.054f),
            new Vector3f(0.714f, 0.4284f, 0.18144f),
            new Vector3f(0.393548f, 0.271906f, 0.166721f),25.6f / 128);
    
    public static final Material POLISHED_BRONZE = new Material(
            new Vector3f(0.25f, 0.148f, 0.06475f),
            new Vector3f(0.4f, 0.2368f, 0.1036f),
            new Vector3f(0.774597f, 0.458561f, 0.200621f),76.8f / 128);
    
    public static final Material CHROME = new Material(
            new Vector3f(0.25f, 0.25f, 0.25f),
            new Vector3f(0.4f, 0.4f, 0.4f),
            new Vector3f(0.774597f, 0.774597f, 0.774597f),76.8f / 128);
    
    public static final Material COPPER = new Material(
            new Vector3f(0.19125f, 0.0735f, 0.0225f),
            new Vector3f(0.7038f, 0.27048f, 0.0828f),
            new Vector3f(0.256777f, 0.137622f, 0.086014f),12.8f / 128);
    
    public static final Material POLISHED_COPPER = new Material(
            new Vector3f(0.2295f, 0.08825f, 0.0275f),
            new Vector3f(0.5508f, 0.2118f, 0.066f),
            new Vector3f(0.580594f, 0.223257f, 0.0695701f),51.2f / 128);
    
    public static final Material GOLD = new Material(
            new Vector3f(0.24725f, 0.1995f, 0.0745f),
            new Vector3f(0.75164f, 0.60648f, 0.22648f),
            new Vector3f(0.628281f, 0.555802f, 0.366065f),51.2f / 128);
    
    public static final Material POLISHED_GOLD = new Material(
            new Vector3f(0.24725f, 0.2245f, 0.0645f),
            new Vector3f(0.34615f, 0.3143f, 0.0903f),
            new Vector3f(0.797357f, 0.723991f, 0.208006f),83.2f / 128);
    
    public static final Material TIN = new Material(
            new Vector3f(0.105882f, 0.058824f, 0.113725f),
            new Vector3f(0.427451f, 0.470588f, 0.541176f),
            new Vector3f(0.333333f, 0.333333f, 0.521569f),9.84615f / 128);
    
    public static final Material SILVER = new Material(
            new Vector3f(0.19225f, 0.19225f, 0.19225f),
            new Vector3f(0.50754f, 0.50754f, 0.50754f),
            new Vector3f(0.508273f, 0.508273f, 0.508273f),51.2f / 128);
    
    public static final Material POLISHED_SILVER = new Material(
            new Vector3f(0.23125f, 0.23125f, 0.23125f),
            new Vector3f(0.2775f, 0.2775f, 0.2775f),
            new Vector3f(0.773911f, 0.773911f, 0.773911f),89.6f / 128);
    
    public static final Material EMERALD = new Material(
            new Vector3f(0.0215f, 0.1745f, 0.0215f),
            new Vector3f(0.07568f, 0.61424f, 0.07568f),
            new Vector3f(0.633f, 0.727811f, 0.633f),76.8f / 128);
    
    public static final Material JADE = new Material(
            new Vector3f(0.135f, 0.2225f, 0.1575f),
            new Vector3f(0.54f, 0.89f, 0.63f),
            new Vector3f(0.316228f, 0.316228f, 0.316228f),12.8f / 128);
    
    public static final Material OBSIDIAN = new Material(
            new Vector3f(0.05375f, 0.05f, 0.06625f),
            new Vector3f(0.18275f, 0.17f, 0.22525f),
            new Vector3f(0.332741f, 0.328634f, 0.346435f),38.4f / 128);
    
    public static final Material PERL = new Material(
            new Vector3f(0.25f, 0.20725f, 0.20725f),
            new Vector3f(1.0f, 0.829f, 0.829f),
            new Vector3f(0.296648f, 0.296648f, 0.296648f),11.264f / 128);
    
    public static final Material RUBY = new Material(
            new Vector3f(0.1745f, 0.01175f, 0.01175f),
            new Vector3f(0.61424f, 0.04136f, 0.04136f),
            new Vector3f(0.727811f, 0.626959f, 0.626959f),76.8f / 128);
    
    public static final Material TURQUOISE = new Material(
            new Vector3f(0.1f, 0.18725f, 0.1745f),
            new Vector3f(0.396f, 0.74151f, 0.69102f),
            new Vector3f(0.297254f, 0.30829f, 0.306678f),12.8f / 128);
    
    public static final Material BLACK_PLASTIC = new Material(
            new Vector3f(0.0f, 0.0f, 0.0f),
            new Vector3f(0.01f, 0.01f, 0.01f),
            new Vector3f(0.50f, 0.50f, 0.50f),32.0f / 128);
    
    public static final Material GREEN_PLASTIC = new Material(
            new Vector3f(0.0f, 0.0f, 0.0f),
            new Vector3f(0.1f,0.35f,0.1f),
            new Vector3f(0.45f,0.55f,0.45f),32.0f / 128);
    
    public static final Material RED_PLASTIC = new Material(
            new Vector3f(0.0f, 0.0f, 0.0f),
            new Vector3f(0.5f,0.0f,0.0f),
            new Vector3f(0.7f,0.6f,0.6f),32.0f / 128);
    
    public static final Material WHITE_PLASTIC = new Material(
            new Vector3f(0.0f, 0.0f, 0.0f),
            new Vector3f(0.55f,0.55f,0.55f),
            new Vector3f(0.70f,0.70f,0.70f),32.0f / 128);
    
    public static final Material YELLOW_PLASTIC = new Material(
            new Vector3f(0.0f, 0.0f, 0.0f),
            new Vector3f(0.5f,0.5f,0.0f),
            new Vector3f(0.60f,0.60f,0.50f),32.0f / 128);
    
    public static final Material BLACK_RUBBER = new Material(
            new Vector3f(0.02f, 0.02f, 0.02f),
            new Vector3f(0.01f, 0.01f, 0.01f),
            new Vector3f(0.4f, 0.4f, 0.4f),10.0f / 128);
    
    public static final Material GREEN_RUBBER = new Material(
            new Vector3f(0.0f,0.05f,0.0f),
            new Vector3f(0.4f,0.5f,0.4f),
            new Vector3f(0.04f,0.7f,0.04f),10.0f / 128);
    
    public static final Material RED_RUBBER = new Material(
            new Vector3f(0.05f,0.0f,0.0f),
            new Vector3f(0.5f,0.4f,0.4f),
            new Vector3f(0.7f,0.04f,0.04f),10.0f / 128);
    
    public static final Material WHITE_RUBBER = new Material(
            new Vector3f(0.05f,0.05f,0.05f),
            new Vector3f(0.5f,0.5f,0.5f),
            new Vector3f(0.7f,0.7f,0.7f),10.0f / 128);
    
    
    private final static Vector3f DEFAULT_AMBIENCE = new Vector3f(0.2f,0.2f,0.2f);
    private final static Vector3f DEFAULT_DIFFUSION = new Vector3f(1.0f,1.0f,1.0f);
    private final static Vector3f DEFAULT_SPECULAR = new Vector3f(0.2f,0.2f,0.2f);
    
    private final static float REFLECTIVITY_FACTOR = 128.0f;
    private final static float DEFAULT_EMISSION = 0.0f;
    private final static float DEFAULT_SHINE = 0.2f;
    private final static float DEFAULT_ALPHA = 1.0f;
    
    private final Vector3f ambient;
    private final Vector3f diffuse;
    private final Vector3f specular;
    private float emission;
    private float shine;
    private float alpha;
    
    
    public Material(Vector3f ambient, Vector3f diffuse, Vector3f specular, float shine, float emission, float alpha) {
        this.ambient = ambient;
        this.diffuse = diffuse;
        this.specular = specular;
        this.emission = emission;
        this.alpha = alpha;
        this.shine = shine * REFLECTIVITY_FACTOR;
    }
    
    public Material(Vector3f ambient, Vector3f diffuse, Vector3f specular, float shine, float emission) {
        this(ambient,diffuse,specular,shine,emission,DEFAULT_ALPHA);
    }
    
    public Material(Vector3f ambient, Vector3f diffuse, Vector3f specular, float shine) {
        this(ambient,diffuse,specular,shine,DEFAULT_EMISSION);
    }
    
    public Material(Vector3f ambient, Vector3f diffuse, Vector3f specular) {
        this(ambient,diffuse,specular,DEFAULT_SHINE);
    }
    
    public Material() {
        this(new Vector3f(DEFAULT_AMBIENCE),new Vector3f(DEFAULT_DIFFUSION),new Vector3f(DEFAULT_SPECULAR));
    }
    
    private Material(float[] ambient, float[] diffuse, float[] specular, float shine) {
        this(new Vector3f(ambient),new Vector3f(diffuse),new Vector3f(specular),shine);
    }
    
    public void set(Material material) {
        setAmbient(material.ambient);
        setDiffuse(material.diffuse);
        setSpecular(material.specular);
        setEmission(material.emission);
        setShine(material.shine);
        setAlpha(material.alpha);
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
    
    public float emissivity() {
        return emission;
    }
    
    public void setEmission(float emission) {
        this.emission = emission;
    }
    
    public float shine() {
        return shine;
    }
    
    public void setShine(float shine) {
        this.shine = shine;
    }
    
    public float alpha() {
        return alpha;
    }
    
    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }
    
    public void getSTD140(FloatBuffer buffer) {
        buffer.put(ambient.x).put(ambient.y).put(ambient.z).put(emission);
        buffer.put(diffuse.x).put(diffuse.y).put(diffuse.z).put(alpha);
        buffer.put(specular.x).put(specular.y).put(specular.z).put(shine);
    }
    
    public static int sizeSTD140(int count) {
        return count * 48;
    }
}
