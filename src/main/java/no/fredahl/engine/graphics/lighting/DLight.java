package no.fredahl.engine.graphics.lighting;

import no.fredahl.engine.math.MathLib;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.nio.FloatBuffer;

/**
 * @author Frederik Dahl
 * 03/01/2022
 */


public class DLight extends Light<DLight> implements DirectionalLight {
    
    
    private final Vector3f direction;
    
    public DLight(float r, float g, float b, float dx, float dy, float dz, float ambient, float diffuse) {
        super(r, g, b, ambient, diffuse);
        this.direction = new Vector3f(dx,dy,dz).normalize();
    }
    
    public DLight(Vector3f color, float dx, float dy, float dz, float ambient, float diffuse) {
        super(color, ambient, diffuse);
        this.direction = new Vector3f(dx,dy,dz).normalize();
    }
    
    public DLight(Vector3f color, Vector3f direction, float ambient, float diffuse) {
        this(color,direction.x,direction.y,direction.z,ambient,diffuse);
    }
    
    public DLight(Vector3f color, Vector3f direction) {
        this(color,direction,DEFAULT_AMBIENT,DEFAULT_DIFFUSE);
    }
    
    public DLight(Vector3f color) {
        this(color,DEFAULT_DIR);
    }
    
    public DLight() {
        this(DEFAULT_COL);
    }
    
    public DLight(DLight dl) {
        this(dl.color,dl.direction,dl.ambient,dl.diffuse);
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
    
    @Override
    public void set(DLight light) {
        if (light != null) {
            setComponents(light);
            setDirection(light.direction,false);
        }
    }
    
    @Override
    public void setComponents(DLight light) {
        if (light != null) {
            setColor(light.color);
            setAmbient(light.ambient);
            setDiffuse(light.diffuse);
        }
    }
    
    @Override
    public void get(Matrix4f viewSpace, FloatBuffer buffer) {
        /*struct DL {
        vec3 color;
        float ambient;
        vec3 direction;
        float diffuse;};*/
        Vector4f v4 = MathLib.vec4();
        v4.set(direction,0.0f).mul(viewSpace);
        buffer.put(color.x).put(color.y).put(color.z).put(ambient);
        buffer.put(v4.x).put(v4.y).put(v4.z).put(diffuse);
    }
    
    @Override
    public DLight copy() {
        return new DLight(this);
    }
    
    
    public static int structSize(int count) {
        /*struct DL {
        vec3 color;
        float ambient;
        vec3 direction;
        float diffuse;};*/
        return structSizeFloat(count) * Float.BYTES;
    }
    
    public static int structSizeFloat(int count) {
        return count * 8;
    }
}
