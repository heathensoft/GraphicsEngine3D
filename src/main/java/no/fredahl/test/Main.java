package no.fredahl.test;

import no.fredahl.engine.Application;
import no.fredahl.engine.Engine;
import no.fredahl.engine.graphics.*;
import no.fredahl.engine.utility.FileUtility;
import no.fredahl.engine.window.Options;
import no.fredahl.engine.window.Window;
import org.joml.Vector4f;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

/**
 * @author Frederik Dahl
 * 24/01/2022
 */


public class Main implements Application {
    
    GLBindings bindings = GLBindings.get();
    TextureAtlas atlas;
    ShaderProgram shader;
    TextureRegion region;
    BufferObject vbo;
    BufferObject ebo;
    int vao;
    
    
    @Override
    public void start(Window window) throws Exception {
    
        
        Texture texture = new Texture(GL_TEXTURE_2D);
        Image image = new Image(FileUtility.resource.toBuffer("test/biome_tundra.png",1024),false);
        texture.bind();
        texture.wrapST(GL_REPEAT);
        texture.filter(GL_NEAREST);
        texture.tex2D(image);
        image.free();
        //region = new TextureRegion(texture);
        atlas = new TextureAtlas(texture,FileUtility.resource.asLines("test/biome_tundra.atlas"));
        region = atlas.get("knight_yellow_walk");
        
    
        // Top Left
        float v1x = -0.25f;
        float v1y = 0.5f;
        // Bottom left
        float v2x = -0.25f;
        float v2y = -0.5f;
        // Bottom Right
        float v3x = 0.25f;
        float v3y = -0.5f;
        // Top Right
        float v4x = 0.25f;
        float v4y = 0.5f;
    
        region.subDivide(8,8);
        
        Vector4f uvs = region.subRegionUVs(1);
        
        float u = uvs.x;
        float v = uvs.y;
        float u2 = uvs.z;
        float v2 = uvs.w;
        
        float[] vertices = {
                v1x,v1y,0,u,v,
                v2x,v2y,0,u,v2,
                v3x,v3y,0,u2,v2,
                v4x,v4y,0,u2,v};
        
        short[] indices = {0,1,2,2,3,0};
        
        
        
        vao = glGenVertexArrays();
        bindings.bindAttributeArray(vao);
        
        ebo = new BufferObject(GL_ELEMENT_ARRAY_BUFFER,GL_STATIC_DRAW);
        ebo.bind();
        ebo.bufferData(indices);
        
        vbo = new BufferObject(GL_ARRAY_BUFFER,GL_STATIC_DRAW);
        vbo.bind();
        vbo.bufferData(vertices);
    
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 5 * Float.BYTES, 0);
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 5 * Float.BYTES, 3 * Float.BYTES);
    
        bindings.unbindAttributeArray();
        
        shader = new ShaderProgram();
        String vs_source = FileUtility.resource.toString("test/vertex.glsl");
        String fs_source = FileUtility.resource.toString("test/fragment.glsl");
        shader.attach(vs_source,GL_VERTEX_SHADER);
        shader.attach(fs_source,GL_FRAGMENT_SHADER);
        shader.compile();
        shader.link();
        shader.bind();
    
        shader.createUniform("uTexture");
    
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
    
    }
    
    @Override
    public void render(float alpha) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        shader.bind();
        shader.setUniform1i("uTexture",0);
        glActiveTexture(GL_TEXTURE0);
        region.texture().bind();
        
        bindings.bindAttributeArray(vao);
        glDrawElements(GL_TRIANGLES,6,GL_UNSIGNED_SHORT,0);
        
        shader.unBind();
        
    }
    
    @Override
    public void exit() {
        region.texture().dispose();
        if (shader != null) shader.dispose();
        bindings.bindBufferObject(GL_ARRAY_BUFFER, 0);
        vbo.free();
        ebo.free();
        bindings.bindAttributeArray(0);
        glDeleteVertexArrays(vao);
    }
    
    public static void main(String[] args) {
        Engine.get().start(new Main(), new Options() {
            @Override
            public String title() {
                return "test";
            }
        });
    }
}
