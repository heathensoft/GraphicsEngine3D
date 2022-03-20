package no.fredahl.engine.graphics.lighting;

import no.fredahl.engine.graphics.ShaderProgram;
import no.fredahl.engine.math.Cullable;
import no.fredahl.engine.math.FrustumBox;
import no.fredahl.engine.math.MathLib;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.nio.FloatBuffer;

/**
 * @author Frederik Dahl
 * 13/01/2022
 */


public class PLCaster extends ShadowCaster<PLight,ShadowMapCube>{
    
    private final FrustumBox frustumBox;
    private final Vector3f prevPos;
    private float prevRange;
    
    
    public PLCaster(PLight light, int resolutionWidth, int resolutionHeight, int range) throws Exception {
        super(light, resolutionWidth, resolutionHeight);
        this.light.farPlane = prevRange = range;
        this.frustumBox = new FrustumBox(light().position,range);
        this.prevPos = new Vector3f(light.position);
        this.frustumBox.updateFrustum();
    }
    
    public void updateFrustum() {
        if (prevRange != range()) {
            frustumBox.setFar(range());
            prevRange = range();
        }
        if (!prevPos.equals(light.position)) {
            frustumBox.setCenter(light.position);
            prevPos.set(light.position);
        }
        frustumBox.updateFrustum();
    }
    
    public void setRange(float range) {
        light.farPlane = range;
    }
    
    public float range() {
        return light.farPlane;
    }
    
    @Override
    public void get(Matrix4f viewSpace, FloatBuffer buffer) {
        light.get(viewSpace,buffer);
        Vector4f v4 = MathLib.vec4();
        v4.set(light.position,1.0f).get(buffer);
    }
    
    @Override
    public boolean insideFrustum(Cullable cullable) {
        return frustumBox.insideFrustum(cullable);
    }
    
    @Override
    protected ShadowMapCube createDepthMap(int width, int height) throws Exception {
        return new ShadowMapCube(width, height);
    }
    
    @Override
    protected void uploadUniforms(ShaderProgram shader) {
        // upload mat4[6]
    }
    
    public static int structSize(int count) {
        /*struct PLC {
        PL light;
        vec4 positionWorld;};*/
        return structSizeFloat(count) * Float.BYTES;
    }
    
    public static int structSizeFloat(int count) {
        return count * 16;
    }
}
