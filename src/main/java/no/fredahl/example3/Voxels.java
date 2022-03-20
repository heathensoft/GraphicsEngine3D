package no.fredahl.example3;

import no.fredahl.engine.graphics.ShaderProgram;
import no.fredahl.engine.math.Camera;
import no.fredahl.engine.math.MathLib;
import no.fredahl.engine.utility.Disposable;
import no.fredahl.engine.utility.FileUtility;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;

/**
 * @author Frederik Dahl
 * 18/01/2022
 */


public class Voxels implements Disposable {
 
    private List<Voxel> voxels;
    private ShaderProgram shader;
    private VoxelBatch batch;
    
    
    public Voxels(int batchSizeQuads) throws Exception {
        batch = new VoxelBatch(batchSizeQuads);
        voxels = new ArrayList<>();
        shader = new ShaderProgram();
        String vs_source = FileUtility.resource.toString("example3/voxel/vertex.glsl");
        String fs_source = FileUtility.resource.toString("example3/voxel/fragment.glsl");
        shader.attach(vs_source,GL_VERTEX_SHADER);
        shader.attach(fs_source,GL_FRAGMENT_SHADER);
        shader.compile();
        shader.link();
        shader.bind();
        shader.createUniform("uView");
        //shader.createUniform("uProjection");
        shader.createUniform("uProjectionView");
        shader.createUniform("uLightDirection");
        glEnable(GL_DEPTH_TEST);
    }
    
    public void render(Camera camera, Vector3f lightDirection) {
        shader.bind();
        Matrix4f view = camera.view();
        Matrix4f projection = camera.projection();
        Matrix4f projectionView = camera.combined();
        Vector3f direction = MathLib.vec3();
        Vector4f v4 = MathLib.vec4();
        v4.set(lightDirection,0.0f).mul(view);
        direction.set(v4.x,v4.y,v4.z); // Normalize it here instead of in shader
        shader.setUniform("uView", view);
        //shader.setUniform("uProjection", projection);
        shader.setUniform("uProjectionView", projectionView);
        shader.setUniform("uLightDirection",direction);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        batch.begin();
        for (Voxel v : voxels) batch.drawVoxel(v.x,v.y,v.z,v.color);
        batch.end();
        shader.unBind();
    }
    
    public void addVoxel(Voxel voxel) {
        voxels.add(voxel);
    }
    
    public void removeVoxel(Voxel voxel) {
        voxels.remove(voxel);
    }
    
    public VoxelBatch getBatch() {
        return batch;
    }
    
    @Override
    public void dispose() {
        if (shader != null) {
            shader.unBind();
            shader.dispose();
        } batch.dispose();
    }
}
