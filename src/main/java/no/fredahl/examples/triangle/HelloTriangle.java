package no.fredahl.examples.triangle;

import no.fredahl.engine.Application;
import no.fredahl.engine.Engine;
import no.fredahl.engine.graphics.*;
import no.fredahl.engine.graphics.VertexAttribute;
import no.fredahl.engine.graphics.BufferObject;
import no.fredahl.engine.graphics.VertexAttributeArray;
import no.fredahl.engine.utility.IO;
import no.fredahl.engine.window.Options;
import no.fredahl.engine.window.Window;
import no.fredahl.engine.window.processors.Keyboard;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL20.*;

/**
 * @author Frederik Dahl
 * 24/10/2021
 */


public class HelloTriangle implements Application {
    
    ShaderProgram program;
    VertexAttributeArray vao;
    VertexAttribute positions;
    BufferObject vbo;
    Keyboard keyboard;
    
    @Override
    public void start(Window window) throws Exception{
        keyboard = new Keyboard(window.keyPressEvents(),window.charPressEvents());
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
        
        FloatBuffer verticesBuffer = null;
        
        try {
            verticesBuffer = MemoryUtil.memAllocFloat(vertices.length);
            verticesBuffer.put(vertices).flip();
            vao = new VertexAttributeArray();
            vao.bind();
            vbo = new BufferObject(GL_ARRAY_BUFFER,GL_STATIC_DRAW);
            vbo.bind();
            vbo.bufferData(verticesBuffer);
            positions = new VertexAttribute(0, VertexAttribute.Type.POSITION_3D);
            positions.enable();
            positions.attributePointer(0,0);
            vbo.unbind();
            vao.unbind();
            
        } finally {
            if (verticesBuffer != null) {
                MemoryUtil.memFree(verticesBuffer);
            }
        }
    }
    
    @Override
    public void input() {
        keyboard.collect();
    }
    
    @Override
    public void update(float delta) {
        
        if (keyboard.justPressed(GLFW_KEY_E))
            System.out.println("E");
    
        if (keyboard.justReleased(GLFW_KEY_F))
            System.out.println("F");
    
        if (keyboard.justPressed(GLFW_KEY_F,GLFW_KEY_LEFT_CONTROL))
            System.out.println("ctrl-F");
        
    }
    
    @Override
    public void render(float alpha) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        vao.bind();
        glDrawArrays(GL_TRIANGLES, 0, 3);
        vao.unbind();
    }
    
    @Override
    public void exit() {
        if (program != null)
            program.delete();
        vbo.unbind();
        vbo.free();
        vao.unbind();
        vao.free();
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
