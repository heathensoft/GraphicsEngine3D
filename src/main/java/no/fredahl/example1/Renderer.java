package no.fredahl.example1;

import no.fredahl.engine.graphics.ShaderProgram;
import no.fredahl.engine.math.ICamera;
import no.fredahl.engine.utility.FileUtility;
import org.joml.Matrix4f;

import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;

/**
 * @author Frederik Dahl
 * 05/11/2021
 */


public class Renderer {
 
    private final ShaderProgram program;
    private final Matrix4f tmpM4f = new Matrix4f();
    
    
    public Renderer() throws Exception {
        Assets assets = Assets.get();
        String fragmentShader = FileUtility.resource.toString(assets.unitFragmentShaderPath);
        String vertexShader = FileUtility.resource.toString(assets.unitVertexShaderPath);
        program = new ShaderProgram();
        program.attach(fragmentShader,GL_FRAGMENT_SHADER);
        program.attach(vertexShader,GL_VERTEX_SHADER);
        program.compile();
        program.link();
        program.bind();
        program.createUniform("projectionMatrix");
        program.createUniform("modelViewMatrix");
        program.createUniform("texture_sampler");
        glEnable(GL_DEPTH_TEST);
    }
    
    public void render(ICamera transform, List<Unit> units) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        program.bind();
        program.setUniform1i("texture_sampler", 0);
        program.setUniform("projectionMatrix", transform.projection());
        for (Unit unit : units) {
            Matrix4f modelToWorld = unit.getModelToWorld();
            tmpM4f.set(transform.view()).mul(modelToWorld);
            program.setUniform("modelViewMatrix", tmpM4f);
            unit.draw();
        }
        program.unBind();
    }
    
    public void dispose() {
        if (program != null) program.delete();
    }
}
