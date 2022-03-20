package no.fredahl.engine.graphics.lighting;

import no.fredahl.engine.math.MathLib;
import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.nio.FloatBuffer;

/**
 * @author Frederik Dahl
 * 03/01/2022
 */


public class SLight extends PointLight<SLight> implements DirectionalLight {
    
    
    protected final Vector3f direction;
    protected float cosineOfCutoffInner;
    protected float cosineOfCutoffOuter;
    protected float cutoffInner;
    protected float cutoffOuter;
    
    public SLight(PLight pLight, float dx, float dy, float dz, float cutoffInner, float cutoffOuter) {
        super(pLight);
        this.direction = new Vector3f(dx,dy,dz).normalize();
        this.cutoffInner = Math.toRadians(cutoffInner);
        this.cutoffOuter = Math.toRadians(cutoffOuter);
        this.cosineOfCutoffInner = Math.cos(cutoffInner);
        this.cosineOfCutoffOuter = Math.cos(cutoffOuter);
    }
    
    public SLight(PLight pLight, Vector3f direction, float cutoffInner, float cutoffOuter) {
        this(pLight,direction.x,direction.y,direction.z, cutoffInner, cutoffOuter);
    }
    
    public SLight(PLight pLight, Vector3f direction, float cutOff) {
        this(pLight,direction,cutOff,cutOff);
    }
    
    public SLight(PLight pLight, Vector3f direction) {
        this(pLight,direction,DEFAULT_CUTOFF);
    }
    
    public SLight(PLight pLight) {
        this(pLight,DEFAULT_DIR);
    }
    
    public SLight() {
        this(new PLight());
    }
    
    public SLight(SLight sl) {
        super(sl.color,sl.position,sl.attenuation,sl.ambient,sl.diffuse);
        this.direction = new Vector3f(sl.direction).normalize();
        this.cosineOfCutoffInner = sl.cosineOfCutoffInner;
        this.cosineOfCutoffOuter = sl.cosineOfCutoffOuter;
        this.cutoffInner = sl.cutoffInner;
        this.cutoffOuter = sl.cutoffOuter;
    }
    
    @Override
    public Vector3f direction() {
        return direction;
    }
    
    @Override
    public void setDirection(Vector3f direction, boolean normalize) {
        setDirection(direction.x,direction.y,direction.z,normalize);
    }
    
    @Override
    public void setDirection(float x, float y, float z, boolean normalize) {
        direction.set(x,y,z);
        if (normalize) direction.normalize();
    }
    
    @Override
    public void rotateAxis(float radians, Vector3f axis) {
        rotateAxis(radians,axis.x,axis.y,axis.z);
    }
    
    @Override
    public void rotateAxis(float radians, float x, float y, float z) {
        direction.rotateAxis(radians, x, y, z);
    }
    
    @Override
    public void rotateX(float radians) {
        direction.rotateX(radians);
    }
    
    @Override
    public void rotateY(float radians) {
        direction.rotateY(radians);
    }
    
    @Override
    public void rotateZ(float radians) {
        direction.rotateZ(radians);
    }
    
    public float cutoffInner() {
        return cutoffInner;
    }
    
    public void setCutoffInner(float deg) {
        cutoffInner = Math.toRadians(deg);
        cosineOfCutoffInner = Math.cos(cutoffInner);
    }
    
    public float cutoffOuter() {
        return cutoffOuter;
    }
    
    public void setCutoffOuter(float deg) {
        cutoffOuter = Math.toRadians(deg);
        cosineOfCutoffOuter = Math.cos(cutoffOuter);
    }
    
    @Override
    public void set(SLight light) {
        if (light != null) {
            setComponents(light);
            setPosition(light.position);
            setDirection(light.direction,false);
            cosineOfCutoffInner = light.cosineOfCutoffInner;
            cosineOfCutoffOuter = light.cosineOfCutoffOuter;
        }
    }
    
    @Override
    public void get(Matrix4f viewSpace, FloatBuffer buffer) {
        /*struct SL {
        vec3 color;
        float ambient;
        vec3 position;
        float diffuse;
        vec3 coneDir;
        float constant;
        float linear;
        float quadratic;
        float innerCutoff;
        float outerCutoff;};*/
        Vector4f v4 = MathLib.vec4();
        buffer.put(color.x).put(color.y).put(color.z).put(ambient);
        v4.set(position,1.0f).mul(viewSpace);
        buffer.put(v4.x).put(v4.y).put(v4.z).put(diffuse);
        v4.set(direction,0.0f).mul(viewSpace);
        buffer.put(v4.x).put(v4.y).put(v4.z);
        attenuation.get(buffer);
        buffer.put(cosineOfCutoffInner).put(cosineOfCutoffOuter);
    }
    
    @Override
    public SLight copy() {
        return new SLight(this);
    }
    
    
    public static int structSize(int count) {
        /*struct SL {
        vec3 color;
        float ambient;
        vec3 position;
        float diffuse;
        vec3 coneDir;
        float constant;
        float linear;
        float quadratic;
        float innerCutoff;
        float outerCutoff;};*/
        return structSizeFloat(count) * Float.BYTES;
    }
    
    public static int structSizeFloat(int count) {
        return count * 16;
    }
}
