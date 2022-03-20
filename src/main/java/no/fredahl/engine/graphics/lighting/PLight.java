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


public class PLight extends PointLight<PLight>{
    
    
    protected float farPlane = 0;
    
    public PLight(Vector3f color, float x, float y, float z, Attenuation attenuation, float ambient, float diffuse) {
        super(color, x, y, z, attenuation, ambient, diffuse);
    }
    
    public PLight(Vector3f color, Vector3f position, Attenuation attenuation, float ambient, float diffuse) {
        super(color, position, attenuation, ambient, diffuse);
    }
    
    public PLight(Vector3f color, Vector3f position, Attenuation attenuation) {
        super(color, position, attenuation);
    }
    
    public PLight(Vector3f color, Attenuation attenuation) {
        super(color, attenuation);
    }
    
    public PLight(Vector3f color) {
        super(color);
    }
    
    public PLight() {
        super();
    }
    
    public PLight(PLight pl) {
        super(pl);
        this.farPlane = pl.farPlane;
    }
    
    @Override
    public void set(PLight light) {
        if (light != null) {
            setComponents(light);
            setPosition(light.position);
        }
    }
    
    @Override
    public void get(Matrix4f viewSpace, FloatBuffer buffer) {
        /* struct PL {
        vec3 color;
        float farPlane;
        vec3 position;
        float ambient;
        float diffuse;
        float constant;
        float linear;
        float quadratic;
        }; */
        Vector4f v4 = MathLib.vec4();
        v4.set(position,1.0f).mul(viewSpace);
        buffer.put(color.x).put(color.y).put(color.z).put(farPlane());
        buffer.put(v4.x).put(v4.y).put(v4.z).put(ambient).put(diffuse);
        attenuation.get(buffer);
    }
    
    @Override
    public PLight copy() {
        return new PLight(this);
    }
    
    // used in PLCaster, else it's 0
    protected float farPlane() {
        return farPlane;
    }
    
    protected void setFarPlane(float farPlane) {
        this.farPlane = farPlane;
    }
    
    public static int structSize(int count) {
        /*struct PL {
        vec3 color;
        float farPlane;
        vec3 position;
        float ambient;
        float diffuse;
        float constant;
        float linear;
        float quadratic;};*/
        return structSizeFloat(count) * Float.BYTES;
    }
    
    public static int structSizeFloat(int count) {
        return count * 12;
    }
    
}
