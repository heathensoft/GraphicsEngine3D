package no.fredahl.engine.graphics.lighting;

import no.fredahl.engine.graphics.ShaderProgram;
import no.fredahl.engine.math.Cullable;
import no.fredahl.engine.math.MathLib;
import org.joml.FrustumIntersection;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.nio.FloatBuffer;

/**
 * @author Frederik Dahl
 * 12/01/2022
 */


public class SLCaster extends ShadowCaster<SLight,ShadowMap>{
    
    private final FrustumIntersection frustumIntersection;
    private final Matrix4f frustum;
    private final Matrix4f projection;
    private final Vector3f prevPos;
    private final Vector3f prevDir;
    private float prevCutoff;
    private float prevRange;
    private float range;
    
    public SLCaster(SLight light, int resolutionWidth, int resolutionHeight, float range) throws Exception {
        super(light, resolutionWidth, resolutionHeight);
        this.frustumIntersection = new FrustumIntersection();
        this.projection = new Matrix4f();
        this.frustum = new Matrix4f();
        this.prevPos = new Vector3f();
        this.prevDir = new Vector3f();
        this.prevCutoff = light.cutoffOuter;
        this.prevRange = range;
        this.range = range;
        updateProjection();
    }
    
    public SLCaster(SLight light, float range) throws Exception {
        this(light,1024,1024,range);
    }
    
    public SLCaster(SLight light) throws Exception {
        this(light,25);
    }
    
    private void updateProjection() {
        projection.setPerspective(light.cutoffOuter,1,0,range);
    }
    
    public void updateFrustum() {
        if (prevRange != range || prevCutoff != light.cutoffOuter) {
            prevCutoff = light.cutoffOuter;
            prevRange = range;
            updateProjection();
        }
        if (prevPos.equals(light.position) && prevDir.equals(light.direction)) return;
        prevPos.set(light.position);
        prevDir.set(light.direction);
        MathLib.lightSpace.combinedPerspective(projection,light.position,light.direction,frustum,false);
        frustumIntersection.set(frustum);
    }
    
    public void setRange(float range) {
        this.range = range;
    }
    
    public float range() {
        return range;
    }
    
    @Override
    public void get(Matrix4f viewSpace, FloatBuffer buffer) {
        light.get(viewSpace, buffer);
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
        /*struct SLC { // see SLight
        SL light;
        mat4 lightSpace;};*/
        return structSizeFloat(count) * Float.BYTES;
    }
    
    public static int structSizeFloat(int count) {
        return count * 32;
    }
}
