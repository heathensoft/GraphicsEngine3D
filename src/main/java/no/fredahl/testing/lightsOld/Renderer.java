package no.fredahl.testing.lightsOld;

import no.fredahl.engine.Engine;
import no.fredahl.engine.graphics.ShaderProgram;
import no.fredahl.engine.graphics.lighting.MaterialBlock;
import no.fredahl.testing.lightsOld.lighting.*;
import no.fredahl.engine.utility.FileUtility;
import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;

/**
 * @author Frederik Dahl
 * 18/12/2021
 */


public class Renderer {
    
    private static final Matrix4f tmpM4f = new Matrix4f();
    
    private final ShaderProgram materialProgram;
    private final ShaderProgram depthMapProgram;
    private final MaterialBlock materialBlock;
    private final ShadowMap shadowMap;
    private final Matrix4f lightMVP;
    private final Matrix4f proj;
    
    public Renderer(World world) throws Exception {
        
        Lights lights = world.lights();
        
        shadowMap = new ShadowMap(2048,2048);
        lightMVP = new Matrix4f();
        //proj = new Matrix4f().setPerspective(Math.toRadians(60f),16/9f,0.01f,30f);
        proj = new Matrix4f().setPerspective(Math.toRadians(90f),1f,0.01f,20f);
        
        materialBlock = new MaterialBlock(1);
        
        String vs_source = FileUtility.resource.toString(Assets.MATERIAL_VS);
        String fs_source = FileUtility.resource.toString(Assets.MATERIAL_FS);
        String lighting_source = FileUtility.resource.toString(Assets.LIGHTING_SHADER);
        fs_source = ShaderProgram.insert(lighting_source,"#LIGHTING",fs_source);
        fs_source = ShaderProgram.insert(lights.glslUniformBlock(),"#LIGHT_BLOCK",fs_source);
        fs_source = ShaderProgram.insert(materialBlock.glslUniformBlock(),"#MATERIAL_BLOCK",fs_source);
        
        materialProgram = new ShaderProgram();
        materialProgram.attach(fs_source,GL_FRAGMENT_SHADER);
        materialProgram.attach(vs_source,GL_VERTEX_SHADER);
        materialProgram.compile();
        materialProgram.link();
        materialProgram.bind();
        materialProgram.createUniform("u_shadowMap");
        materialProgram.createUniform("u_modelView");
        materialProgram.createUniform("u_projection");
        materialProgram.createUniform("u_material_index");
        materialProgram.createUniform("u_light_mvp");
    
        
        String depthMap_source = FileUtility.resource.toString(Assets.DEPTH_MAP_VS);
        depthMapProgram = new ShaderProgram();
        depthMapProgram.attach(depthMap_source,GL_VERTEX_SHADER);
        depthMapProgram.compile();
        depthMapProgram.link();
        depthMapProgram.bind();
        depthMapProgram.createUniform("u_light_mvp");
        
        glEnable(GL_DEPTH_TEST);
    }
    
    
    public void render(ICamera camera, World world) {
        
        renderDepthMap(camera, world);
        
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        world.lights().uploadUniforms(camera.view());
        
        materialProgram.bind();
        materialProgram.setUniform("u_projection",camera.projection());
        materialProgram.setUniform1i("u_shadowMap",0);
        
        shadowMap.texture().bindAndSetActive(GL_TEXTURE0);
        
        List<GameObject> objects = world.gameObjects();
        for (GameObject object : objects) {
            Matrix4f modelToWorld = object.transform.modelToWorldMatrix();
            tmpM4f.set(camera.view()).mul(modelToWorld);
            materialProgram.setUniform("u_modelView", tmpM4f);
            materialProgram.setUniform1i("u_material_index", object.material);
            tmpM4f.set(lightMVP).mul(modelToWorld);
            materialProgram.setUniform("u_light_mvp", tmpM4f);
            object.mesh.render();
        }
        materialProgram.unBind();
        
        shadowMap.texture().unbind();
    }
    
    private void renderDepthMap(ICamera camera, World world) {
        
        //DirectionalLight light = world.lights().directionalLights().get(0);
        //ShadowCast.calcLightProjViewOrtho(proj,camera.view(),light.direction(),lightMVP.identity(),false);
        
        ShadowCast.lightCombinedPerspective(proj,new Vector3f(0,7,0),new Vector3f(0,-1,-0.0001f).normalize(),lightMVP.identity());
        shadowMap.bind(); // binds the framebuffer
    
        
        Engine.get().window.borrowViewport(0, 0, shadowMap.width(), shadowMap.height());
        //glViewport(0, 0, shadowMap.width(), shadowMap.height());
        
        
        
        
        glClear(GL_DEPTH_BUFFER_BIT);
        depthMapProgram.bind();
        
        
        List<GameObject> objects = world.gameObjects();
        for (GameObject object : objects) {
            Matrix4f modelToWorld = object.transform.modelToWorldMatrix();
            tmpM4f.set(lightMVP).mul(modelToWorld);
            depthMapProgram.setUniform("u_light_mvp", tmpM4f);
            object.mesh.render();
        }
        
        Engine.get().window.returnViewport();
        shadowMap.unbind();
        depthMapProgram.unBind();
    }
    
    public void dispose() {
        if (shadowMap != null) shadowMap.dispose();
        if (materialBlock != null) materialBlock.free();
        if (materialProgram != null) materialProgram.dispose();
        if (depthMapProgram != null) depthMapProgram.dispose();
    }
}
