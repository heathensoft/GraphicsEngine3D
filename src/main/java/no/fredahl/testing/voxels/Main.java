package no.fredahl.testing.voxels;

import no.fredahl.engine.Application;
import no.fredahl.engine.Engine;
import no.fredahl.engine.graphics.BufferObject;
import no.fredahl.engine.graphics.Color;
import no.fredahl.engine.graphics.GLBindings;
import no.fredahl.engine.graphics.ShaderProgram;
import no.fredahl.engine.math.MathLib;
import no.fredahl.engine.utility.FileUtility;
import no.fredahl.engine.window.Options;
import no.fredahl.engine.window.Window;
import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

/**
 * @author Frederik Dahl
 * 17/01/2022
 */


public class Main implements Application {
    
    private final GLBindings bindings = GLBindings.get();
    
    private int vao;
    private BufferObject vbo;
    private BufferObject ebo;
    private Matrix4f projectionView;
    private Vector3f cameraPosition;
    private Vector3f cameraLookAt;
    private Vector3f cameraUp;
    private ShaderProgram shader;
    
    float red = Color.packed(1,0,0,1);
    float green = Color.packed(0,1,0,1);
    float blue = Color.packed(0,0,1,1);
    float bitMask = 6;
    
    @Override
    public void start(Window window) throws Exception {
        
        
        shader = new ShaderProgram();
        String vs_source = FileUtility.resource.toString("testing/voxels/vertex.glsl");
        String fs_source = FileUtility.resource.toString("testing/voxels/fragment.glsl");
        shader.attach(vs_source,GL_VERTEX_SHADER);
        shader.attach(fs_source,GL_FRAGMENT_SHADER);
        shader.compile();
        shader.link();
        shader.bind();
        shader.createUniform("uProjectionView");
    
        
        projectionView = new Matrix4f();
        cameraUp = new Vector3f(0,1,0);
        cameraPosition = new Vector3f(1,-2,-2);
        cameraLookAt = new Vector3f(0.5f,0.5f,0.5f);
        Matrix4f view = MathLib.mat4();
        Matrix4f projection = MathLib.mat4();
        view.identity().lookAt(cameraPosition,cameraLookAt,cameraUp);
        projection.identity().perspective(Math.toRadians(70),window.aspectRatio(),0.01f,50f);
        projectionView.set(projection).mul(view);
        
        
        vao = glGenVertexArrays();
        bindings.bindAttributeArray(vao);
    
        ebo = new BufferObject(GL_ELEMENT_ARRAY_BUFFER,GL_STATIC_DRAW);
        ebo.bind();
        
        
        final int len = 6 * 6; // 6 faces, 6 elements
        final short[] indices = new short[len];
        short j = 0;
        for (int i = 0; i < len; i += 6, j += 4) {
            indices[i] = j;
            indices[i + 1] = (short)(j + 1);
            indices[i + 2] = (short)(j + 2);
            indices[i + 3] = (short)(j + 2);
            indices[i + 4] = (short)(j + 3);
            indices[i + 5] = j;
        }
        
        ebo.bufferData(indices);
    
        vbo = new BufferObject(GL_ARRAY_BUFFER,GL_STATIC_DRAW);
        vbo.bind();
        
        FloatBuffer buffer = null;
        
        int bufferSize = 6 * 4 * 5; // 6 faces 4 vertices 4 floats
        try {
            buffer = MemoryUtil.memAllocFloat(bufferSize);
            
            Face face = Face.TOP;
            buffer.put(face.x1).put(face.y1).put(face.z1).put(red).put(bitMask);
            buffer.put(face.x2).put(face.y2).put(face.z2).put(red).put(bitMask);
            buffer.put(face.x3).put(face.y3).put(face.z3).put(red).put(bitMask);
            buffer.put(face.x4).put(face.y4).put(face.z4).put(red).put(bitMask);
            
            face = Face.LEFT;
            buffer.put(face.x1).put(face.y1).put(face.z1).put(red).put(bitMask);
            buffer.put(face.x2).put(face.y2).put(face.z2).put(red).put(bitMask);
            buffer.put(face.x3).put(face.y3).put(face.z3).put(red).put(bitMask);
            buffer.put(face.x4).put(face.y4).put(face.z4).put(red).put(bitMask);
            
            face = Face.RIGHT;
            buffer.put(face.x1).put(face.y1).put(face.z1).put(red).put(bitMask);
            buffer.put(face.x2).put(face.y2).put(face.z2).put(red).put(bitMask);
            buffer.put(face.x3).put(face.y3).put(face.z3).put(red).put(bitMask);
            buffer.put(face.x4).put(face.y4).put(face.z4).put(red).put(bitMask);
            
            face = Face.BOTTOM;
            buffer.put(face.x1).put(face.y1).put(face.z1).put(red).put(bitMask);
            buffer.put(face.x2).put(face.y2).put(face.z2).put(red).put(bitMask);
            buffer.put(face.x3).put(face.y3).put(face.z3).put(red).put(bitMask);
            buffer.put(face.x4).put(face.y4).put(face.z4).put(red).put(bitMask);
            
            face = Face.FRONT;
            buffer.put(face.x1).put(face.y1).put(face.z1).put(blue).put(bitMask);
            buffer.put(face.x2).put(face.y2).put(face.z2).put(blue).put(bitMask);
            buffer.put(face.x3).put(face.y3).put(face.z3).put(blue).put(bitMask);
            buffer.put(face.x4).put(face.y4).put(face.z4).put(blue).put(bitMask);
            
            face = Face.REAR;
            buffer.put(face.x1).put(face.y1).put(face.z1).put(red).put(bitMask);
            buffer.put(face.x2).put(face.y2).put(face.z2).put(red).put(bitMask);
            buffer.put(face.x3).put(face.y3).put(face.z3).put(red).put(bitMask);
            buffer.put(face.x4).put(face.y4).put(face.z4).put(red).put(bitMask);
            
            buffer.flip();
            vbo.bufferData(buffer);
            
        } finally {
            if (buffer != null)
                MemoryUtil.memFree(buffer);
        }
    
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 5*Float.BYTES, 0);
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 4, GL_UNSIGNED_BYTE, true, 5*Float.BYTES, 3*Float.BYTES);
        glEnableVertexAttribArray(2);
        glVertexAttribPointer(2, 1, GL_FLOAT, false, 5*Float.BYTES, 4*Float.BYTES);
    
        bindings.bindAttributeArray(0);
    
        glEnable(GL_DEPTH_TEST);
    }
    
    @Override
    public void input(float delta) {
    
    }
    
    @Override
    public void update(float delta) {
    
    }
    
    @Override
    public void resize(Window window) {
    
        Matrix4f view = MathLib.mat4();
        Matrix4f projection = MathLib.mat4();
        view.identity().lookAt(cameraPosition,cameraLookAt,cameraUp);
        projection.identity().perspective(Math.toRadians(70),window.aspectRatio(),0.01f,50f);
        projectionView.set(projection).mul(view);
        
        
    }
    
    @Override
    public void render(float alpha) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        shader.bind();
        shader.setUniform("uProjectionView", projectionView);
        bindings.bindAttributeArray(vao);
        glDrawElements(GL_TRIANGLES,36,GL_UNSIGNED_SHORT,0);
        bindings.bindAttributeArray(0);
        shader.unBind();
    
    }
    
    @Override
    public void exit() {
        if (shader != null) {
            shader.unBind();
            shader.dispose();
        }
        bindings.bindBufferObject(GL_ARRAY_BUFFER, 0);
        vbo.free();
        bindings.bindBufferObject(GL_ELEMENT_ARRAY_BUFFER, 0);
        ebo.free();
        bindings.bindAttributeArray(0);
        glDeleteVertexArrays(vao);
    }
    
    public static void main(String[] args) {
    
        int size = 8191;
        int len = size * 6;
        
        final short[] indices = new short[len];
        short j = 0;
        for (int i = 0; i < len; i += 6, j += 4) {
            indices[i] = j;
            indices[i + 1] = (short)(j + 1);
            indices[i + 2] = (short)(j + 2);
            indices[i + 3] = (short)(j + 2);
            indices[i + 4] = (short)(j + 3);
            indices[i + 5] = j;
        }
        
        System.out.println(indices[indices.length - 1]);
        
        Engine.get().start(new Main(), new Options() {
            @Override
            public String title() {
                return "Example 3";
            }
    
            @Override
            public boolean showTriangles() {
                return true;
            }
    
            @Override
            public boolean cullFace() {
                return false;
            }
        });
    }
}
