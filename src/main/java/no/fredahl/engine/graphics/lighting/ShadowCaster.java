package no.fredahl.engine.graphics.lighting;

import no.fredahl.engine.Engine;
import no.fredahl.engine.graphics.ShaderProgram;
import no.fredahl.engine.math.Cullable;
import no.fredahl.engine.utility.Disposable;
import org.joml.Matrix4f;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author Frederik Dahl
 * 06/01/2022
 */


public abstract class ShadowCaster<T extends Light<T>, U extends DepthMap> implements Disposable {
    
    protected final T light;
    protected final U depthMap;
    
    public ShadowCaster(T light, int resolutionWidth, int resolutionHeight) throws Exception {
        this.depthMap = createDepthMap(resolutionWidth, resolutionHeight);
        this.light = light;
    }
    
    public T light() {
        return light;
    }
    
    public U DepthMap() {
        return depthMap;
    }
    
    public abstract void get(Matrix4f viewSpace, FloatBuffer buffer);
    
    public abstract boolean insideFrustum(Cullable cullable);
    
    protected abstract U createDepthMap(int width, int height) throws Exception;
    
    protected abstract void uploadUniforms(ShaderProgram shader);
    
    /**
     * ! this method fits the glViewport to the depthMap resolution.
     * Once finished with rendering to shadowCasters' framebuffers,
     * it's necessary to restore the glViewport to the default framebuffer
     * resolution. "Engine.get().window.returnViewport()"
     * @param shader the depthMap shader program
     */
    public void prepareDepthMap(ShaderProgram shader) {
        depthMap.bindFramebuffer();
        Engine.get().window.borrowViewport(0, 0, depthMap.width(), depthMap.height());
        glClear(GL_DEPTH_BUFFER_BIT);
        shader.bind();
        uploadUniforms(shader);
    }
    
    @Override
    public void dispose() {
        if (depthMap != null) depthMap.dispose();
    }
    
}
