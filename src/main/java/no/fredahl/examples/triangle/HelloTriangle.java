package no.fredahl.examples.triangle;

import no.fredahl.engine.Application;
import no.fredahl.engine.Engine;
import no.fredahl.engine.graphics.*;
import no.fredahl.engine.utils.IO;
import no.fredahl.engine.window.Options;
import no.fredahl.engine.window.Window;

import static org.lwjgl.opengl.GL20.*;

/**
 * @author Frederik Dahl
 * 24/10/2021
 */


public class HelloTriangle implements Application {
    
    ShaderProgram program;
    VAO vao;
    
    @Override
    public void start(Window window) throws Exception{
        
        String vertexShaderPath = IO.projectPath("vertex.glsl","res","triangle");
        String fragmentShaderPath = IO.projectPath("fragment.glsl","res","triangle");
    
        ShaderSource fragmentShader = new ShaderSource(GL_FRAGMENT_SHADER,fragmentShaderPath);
        ShaderSource vertexShader = new ShaderSource(GL_VERTEX_SHADER,vertexShaderPath);
        
        program = new ShaderProgram();
        program.attach(fragmentShader,vertexShader);
        program.compile();
        program.link();
        program.bind();
    
        float[] vertices = new float[]{
                0.0f,  0.5f, 0.0f,
                -0.5f, -0.5f, 0.0f,
                0.5f, -0.5f, 0.0f
        };
    
        vao = GLObject.get().VAO();
        vao.storeData(VertexAttribute.position(0),vertices);
    }
    
    @Override
    public void update(float delta) {
    
    }
    
    @Override
    public void render(float alpha) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glDrawArrays(GL_TRIANGLES, 0, 3);
    }
    
    @Override
    public void exit() {
        if (program != null)
            program.delete();
        GLObject.get().freeAll();
    }
    
    public static void main(String[] args) {
        Engine.get().start(new HelloTriangle(), new Options() {
            @Override
            public String windowTitle() {
                return "Triangle";
            }
        });
    }
}
