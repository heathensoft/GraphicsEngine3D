package no.fredahl.example;

import no.fredahl.engine.graphics.ShaderProgram;
import no.fredahl.engine.graphics.ShaderSource;
import no.fredahl.engine.math.ICamera;
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
        ShaderSource fragmentShader = new ShaderSource(GL_FRAGMENT_SHADER, assets.unitFragmentShader);
        ShaderSource vertexShader = new ShaderSource(GL_VERTEX_SHADER, assets.unitVertexShader);
        program = new ShaderProgram();
        program.attach(fragmentShader,vertexShader);
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
        program.setUniform("texture_sampler", 0);
        program.setUniform("projectionMatrix", transform.getProjectionMatrix());
        for (Unit unit : units) {
            Matrix4f modelToWorld = unit.getModelToWorld();
            tmpM4f.set(transform.getWorldToViewMatrix());
            tmpM4f.mul(modelToWorld);
            program.setUniform("modelViewMatrix", tmpM4f);
            unit.draw();
        }
        program.unBind();
    }
    
    public void dispose() {
        if (program != null) program.delete();
    }
}
