package no.fredahl.engine.graphics.lighting;

import no.fredahl.engine.graphics.ShaderProgram;
import no.fredahl.engine.math.Cullable;
import no.fredahl.engine.math.MathLib;
import org.joml.FrustumIntersection;
import org.joml.Matrix4f;

import java.nio.FloatBuffer;

/**
 * @author Frederik Dahl
 * 06/01/2022
 */



public class DLCaster extends ShadowCaster<DLight,ShadowMap> {
    
    private final FrustumIntersection frustumIntersection;
    private final Matrix4f frustum;
    
    
    public DLCaster(DLight light,int resolutionWidth, int resolutionHeight) throws Exception {
        super(light, resolutionWidth, resolutionHeight);
        this.frustumIntersection = new FrustumIntersection();
        this.frustum = new Matrix4f();
    }
    
    public DLCaster(DLight light) throws Exception {
        this(light,1024,1024);
    }
    
    public void updateFrustum(Matrix4f cameraCombinedINV) {
        MathLib.lightSpace.combinedOrtho(cameraCombinedINV,light.direction(), frustum,false);
        frustumIntersection.set(frustum);
    }
    
    @Override
    public void get(Matrix4f viewSpace, FloatBuffer buffer) {
        light.get(viewSpace,buffer);
        frustum.get(buffer);
    }
    
    @Override
    public boolean insideFrustum(Cullable cullable) {
        return cullable.insideFrustum(frustumIntersection);
    }
    
    @Override
    protected ShadowMap createDepthMap(int width, int height) throws Exception {
        return new ShadowMap(width,height);
    }
    
    @Override
    protected void uploadUniforms(ShaderProgram shader) {
        shader.setUniform("u_lightSpace", frustum);
    }
    
    public static int structSize(int count) {
        /*struct DLC { // see DLight
        DL light;
        mat4 lightSpace;};*/
        return structSizeFloat(count) * Float.BYTES;
    }
    
    public static int structSizeFloat(int count) {
        return count * 24;
    }
    
}

