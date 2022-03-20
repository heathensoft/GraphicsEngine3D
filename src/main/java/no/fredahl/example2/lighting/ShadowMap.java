package no.fredahl.example2.lighting;

import no.fredahl.engine.graphics.BufferObject;
import no.fredahl.engine.graphics.GLBindings;
import no.fredahl.engine.graphics.ShaderProgram;
import no.fredahl.engine.graphics.Texture;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL14.GL_DEPTH_COMPONENT16;
import static org.lwjgl.opengl.GL30.*;

/**
 * @author Frederik Dahl
 * 22/12/2021
 */


public class ShadowMap {
    
    private final static GLBindings bindings = GLBindings.get();
    
    private final int fbo;
    private final int width;
    private final int height;
    private final Texture depthMap;
    
    // Debug purposes
    private boolean debug;
    private boolean perspective;
    private ShaderProgram fboProgram;
    private BufferObject vertices;
    private BufferObject elements;
    private int vao;
    
    public void renderDepthTextureOrtho(int textureUnit) {
        if (debug && !perspective) {
            fboProgram.bind();
            fboProgram.setUniform1i("u_depthTexture",textureUnit);
            depthMap.bindAndSetActive(textureUnit);
            bindings.bindAttributeArray(vao);
            glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_SHORT,0);
            bindings.bindAttributeArray(0);
            fboProgram.unBind();
        }
    }
    
    public void renderDepthTexturePerspective(int textureUnit, float near, float far) {
        if (debug && perspective) {
            fboProgram.bind();
            fboProgram.setUniform1i("u_depthTexture",textureUnit);
            fboProgram.setUniform1f("u_nearPlane",near);
            fboProgram.setUniform1f("u_farPlane",far);
            depthMap.bindAndSetActive(textureUnit);
            bindings.bindAttributeArray(vao);
            glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_SHORT,0);
            bindings.bindAttributeArray(0);
            fboProgram.unBind();
        }
    }
    
    public void debugSetup(boolean perspectiveProjection) throws Exception {
        if (!debug) {
            if (perspectiveProjection) {
                generateProgramPerspective();
            }  else generateProgramOrtho();
            generateBuffers();
            debug = true;
            perspective = perspectiveProjection;
        }
        
    }
    
    public ShadowMap(int width, int height) throws Exception {
    
        this.width = width;
        this.height = height;
        
        depthMap = new Texture(GL_TEXTURE_2D);
        depthMap.bind();
        depthMap.filter(GL_NEAREST);
        depthMap.wrapST(GL_CLAMP_TO_BORDER);
        glTexParameterfv(GL_TEXTURE_2D, GL_TEXTURE_BORDER_COLOR,new float[] {1,1,1,1});
        depthMap.tex2D(0,GL_DEPTH_COMPONENT16,width,height,GL_DEPTH_COMPONENT,GL_FLOAT);
        
        fbo = glGenFramebuffers();
        bindings.bindFrameBuffer(GL_FRAMEBUFFER,fbo);
        glFramebufferTexture2D(GL_FRAMEBUFFER,GL_DEPTH_ATTACHMENT,GL_TEXTURE_2D,depthMap.id(),0);
        
        glDrawBuffer(GL_NONE);
        glReadBuffer(GL_NONE);
    
        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
            throw new Exception("Unable to create FrameBuffer");
        }
    
        bindings.bindFrameBuffer(GL_FRAMEBUFFER,0);
        
    }
    
    
    public Texture texture() {
        return depthMap;
    }
    
    public int fbo() {
        return fbo;
    }
    
    public int width() {
        return width;
    }
    
    public int height() {
        return height;
    }
    
    public void bind() {
        bindings.bindFrameBuffer(GL_FRAMEBUFFER,fbo);
    }
    
    public void unbind() {
        bindings.bindFrameBuffer(GL_FRAMEBUFFER,0);
    }
    
    public void dispose() {
        unbind();
        glDeleteFramebuffers(fbo);
        depthMap.unbind();
        depthMap.dispose();
        if (debug) {
            if (fboProgram != null) fboProgram.dispose();
            bindings.bindBufferObject(GL_ARRAY_BUFFER, 0);
            vertices.free();
            elements.free();
            bindings.bindAttributeArray(0);
            glDeleteVertexArrays(vao);
        }
    }
    
    private void generateProgramOrtho() throws Exception {
        fboProgram = new ShaderProgram();
        fboProgram.attach(fboVert(),GL_VERTEX_SHADER);
        fboProgram.attach(fboFragOrtho(),GL_FRAGMENT_SHADER);
        fboProgram.compile();
        fboProgram.link();
        fboProgram.bind();
        fboProgram.createUniform("u_depthTexture");
    }
    
    private void generateProgramPerspective() throws Exception {
        fboProgram = new ShaderProgram();
        fboProgram.attach(fboVert(),GL_VERTEX_SHADER);
        fboProgram.attach(fboFragPerspective(),GL_FRAGMENT_SHADER);
        fboProgram.compile();
        fboProgram.link();
        fboProgram.bind();
        fboProgram.createUniform("u_depthTexture");
        fboProgram.createUniform("u_nearPlane");
        fboProgram.createUniform("u_farPlane");
    }
    
    private void generateBuffers() {
        
        vao = glGenVertexArrays();
        bindings.bindAttributeArray(vao);
        
        final float[] vertexArray = {
                // position     // texCoord
                1.0f, 0.5f,     1.0f, 0.0f, // Bottom right 0
                0.5f, 1.0f,     0.0f, 1.0f, // Top left     1
                1.0f, 1.0f ,    1.0f, 1.0f, // Top right    2
                0.5f, 0.5f,     0.0f, 0.0f, // Bottom left  3
        };
        
        vertices = new BufferObject(GL_ARRAY_BUFFER,GL_STATIC_DRAW);
        vertices.bind();
        vertices.bufferData(vertexArray);
        
        final short[] elementArray = {
                2, 1, 0, // Top right triangle
                0, 1, 3  // bottom left triangle
        };
        
        elements = new BufferObject(GL_ELEMENT_ARRAY_BUFFER,GL_STATIC_DRAW);
        elements.bind();
        elements.bufferData(elementArray);
        
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 16, 0);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 16, 8);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        bindings.bindAttributeArray(0);
    }
    
    private String fboVert() {
        return "#version 430\n" +
                       "\n" +
                       "layout (location = 0) in vec2 a_pos;\n" +
                       "layout (location = 1) in vec2 a_tex;\n" +
                       "\n" +
                       "out VS_OUT {\n" +
                       "    vec2 texCoords;\n" +
                       "} _out;\n" +
                       "\n" +
                       "void main()\n" +
                       "{\n" +
                       "    gl_Position = vec4(a_pos.x, a_pos.y, 0.0, 1.0);\n" +
                       "    _out.texCoords = a_tex;\n" +
                       "}";
    }
    
    private String fboFragOrtho() {
        return "#version 430\n" +
                       "\n" +
                       "out vec4 color;\n" +
                       "\n" +
                       "in VS_OUT {\n" +
                       "    vec2 texCoords;\n" +
                       "} _in;\n" +
                       "\n" +
                       "uniform sampler2D u_depthTexture;\n" +
                       "\n" +
                       "void main() {\n" +
                       "    float depth = texture(u_depthTexture, _in.texCoords).r;\n" +
                       "    color = vec4(vec3(depth),1.0);\n" +
                       "}";
    }
    
    private String fboFragPerspective() {
        return "#version 430\n" +
                       "\n" +
                       "out vec4 color;\n" +
                       "\n" +
                       "in VS_OUT {\n" +
                       "    vec2 texCoords;\n" +
                       "} _in;\n" +
                       "\n" +
                       "uniform sampler2D u_depthTexture;\n" +
                       "uniform float u_nearPlane;\n" +
                       "uniform float u_farPlane;\n" +
                       "\n" +
                       "float linearizeDepth(float depth)\n" +
                       "{\n" +
                       "    float z = depth * 2.0 - 1.0; // Back to NDC\n" +
                       "    return (2.0 * u_nearPlane * u_farPlane) / (u_farPlane + u_nearPlane - z * (u_farPlane - u_nearPlane));\n" +
                       "}\n" +
                       "\n" +
                       "void main()\n" +
                       "{\n" +
                       "    float depth = texture(u_depthTexture, _in.texCoords).r;\n" +
                       "    color = vec4(vec3(linearizeDepth(depth) / u_farPlane),1.0);\n" +
                       "}";
    }
}
