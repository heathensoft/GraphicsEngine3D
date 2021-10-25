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


public class App implements Application {
    
    ShaderProgram program;
    VAO vao;
    
    @Override
    public void start(Window window){
        
        try {
            program = new ShaderProgram();
            ShaderSource fragmentShader = new ShaderSource(GL_FRAGMENT_SHADER,IO.projectPath("fragment.glsl","res","triangle"));
            ShaderSource vertexShader = new ShaderSource(GL_VERTEX_SHADER,IO.projectPath("vertex.glsl","res","triangle"));
            program.attach(fragmentShader,vertexShader);
            program.compile();
            program.link();
            
            float[] vertices = new float[]{
                    0.0f,  0.5f, 0.0f,
                    -0.5f, -0.5f, 0.0f,
                    0.5f, -0.5f, 0.0f
            };
    
            vao = GLObject.get().VAO();
            vao.storeData(VertexAttribute.position(0),vertices);
            program.bind();
            
        } catch (Exception e) {
            e.printStackTrace();
            Engine.get().exit();
            
        }
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
        Engine.get().start(new App(), new Options() {
            @Override
            public String windowTitle() {
                return "Triangle";
            }
        });
    }
}
