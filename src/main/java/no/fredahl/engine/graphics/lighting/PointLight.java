package no.fredahl.engine.graphics.lighting;

import org.joml.Vector3f;

/**
 * @author Frederik Dahl
 * 03/01/2022
 */


public abstract class PointLight<T extends PointLight<T>> extends Light<T>{
    
    protected final Vector3f position;
    protected final Attenuation attenuation;
    
    public PointLight(Vector3f color, float x, float y, float z, Attenuation attenuation, float ambient, float diffuse) {
        super(color, ambient, diffuse);
        this.attenuation = new Attenuation(attenuation);
        this.position = new Vector3f(x,y,z);
    }
    
    public PointLight(Vector3f color, Vector3f position, Attenuation attenuation, float ambient, float diffuse) {
        this(color,position.x,position.y,position.z,attenuation,ambient,diffuse);
    }
    
    public PointLight(Vector3f color, Vector3f position, Attenuation attenuation) {
        this(color,position,attenuation,DEFAULT_AMBIENT,DEFAULT_DIFFUSE);
    }
    
    public PointLight(Vector3f color, Attenuation attenuation) {
        this(color,DEFAULT_POS,attenuation);
    }
    
    public PointLight(Vector3f color) {
        this(color,Attenuation.ATT_100);
    }
    
    public PointLight() {
        this(DEFAULT_POS);
    }
    
    public PointLight(PLight pl) {
        this(pl.color,pl.position,pl.attenuation,pl.ambient,pl.diffuse);
    }
    
    public Vector3f position() {
        return position;
    }
    
    public void setPosition(Vector3f position) {
        setPosition(position.x,position.y,position.z);
    }
    
    public void setPosition(float x, float y, float z) {
        position.set(x, y, z);
    }
    
    public void translate(Vector3f translation) {
        position.add(translation);
    }
    
    public void translate(float x, float y, float z) {
        position.add(x, y, z);
    }
    
    public void translateX(float x) {
        position.x += x;
    }
    
    public void translateY(float y) {
        position.y += y;
    }
    
    public void translateZ(float z) {
        position.z += z;
    }
    
    public Attenuation attenuation() {
        return attenuation;
    }
    
    public void setAttenuation(Attenuation attenuation) {
        this.attenuation.set(attenuation);
    }
    
    @Override
    public void setComponents(T light) {
        if (light != null) {
            setColor(light.color);
            setAmbient(light.ambient);
            setDiffuse(light.diffuse);
            setAttenuation(light.attenuation);
        }
    }
}
