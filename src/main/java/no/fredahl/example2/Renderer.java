package no.fredahl.example2;

import no.fredahl.engine.Engine;
import no.fredahl.engine.graphics.ShaderProgram;
import no.fredahl.engine.graphics.lighting.DirectionalLight;
import no.fredahl.engine.graphics.lighting.Lights;
import no.fredahl.engine.graphics.lighting.Material;
import no.fredahl.engine.graphics.lighting.MaterialBlock;
import no.fredahl.engine.graphics.shadow.ShadowMap;
import no.fredahl.engine.math.ICamera;
import no.fredahl.engine.utility.FileUtility;
import no.fredahl.engine.window.Window;
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
    private static final Vector3f tmpV3f = new Vector3f();
    
    private final ShaderProgram materialProgram;
    private final ShaderProgram depthMapProgram;
    private final MaterialBlock materialBlock;
    private final ShadowMap shadowMap;
    
    public Renderer(World world) throws Exception {
        
        Lights lights = world.lights();
        
        shadowMap = new ShadowMap();
        
        materialBlock = new MaterialBlock(1,
                Material.BLACK_PLASTIC,
                Material.BLACK_RUBBER,
                Material.BRASS,
                Material.BRONZE,
                Material.CHROME,
                Material.COPPER,
                Material.EMERALD,
                Material.GOLD,
                Material.GREEN_PLASTIC,
                Material.GREEN_RUBBER,
                Material.JADE,
                Material.OBSIDIAN,
                Material.PERL,
                Material.POLISHED_BRONZE,
                Material.POLISHED_COPPER,
                Material.POLISHED_GOLD,
                Material.POLISHED_SILVER,
                Material.RED_PLASTIC,
                Material.RED_RUBBER,
                Material.RUBY,
                Material.SILVER,
                Material.TIN,
                Material.WHITE_PLASTIC,
                Material.WHITE_RUBBER,
                Material.YELLOW_PLASTIC,
                Material.TURQUOISE);
        
        String vs_source = FileUtility.resource.toString(Assets.MATERIAL_VS);
        String fs_source = FileUtility.resource.toString(Assets.MATERIAL_FS);
        String lighting_source = FileUtility.resource.toString(Assets.LIGHTING_SHADER);
        fs_source = ShaderProgram.insert(lighting_source,"#LIGHTING",fs_source);
        fs_source = ShaderProgram.insert(lights.glslUniformBlock(),"#LIGHT_BLOCK",fs_source);
        //fs_source = ShaderProgram.insert(materialBlock.glslMaterial(),"#MATERIALS",fs_source);
        fs_source = ShaderProgram.insert(materialBlock.glslUniformBlock(),"#MATERIAL_BLOCK",fs_source);
        materialProgram = new ShaderProgram();
        materialProgram.attach(fs_source,GL_FRAGMENT_SHADER);
        materialProgram.attach(vs_source,GL_VERTEX_SHADER);
        materialProgram.compile();
        materialProgram.link();
        materialProgram.bind();
        //materialProgram.createUniformBlockIndex("Lights");
        //materialProgram.bindBlock("Lights",0);
        //materialProgram.createUniformBlockIndex("Materials");
        //materialProgram.bindBlock("Materials",1);
        materialProgram.createUniform("u_shadowMap");
        materialProgram.createUniform("u_modelView");
        materialProgram.createUniform("u_projection");
        materialProgram.createUniform("u_material_index");
        materialProgram.createUniform("u_light_modelView");
        materialProgram.createUniform("u_light_projection");
    
        
        String depthMap_source = FileUtility.resource.toString(Assets.DEPTH_MAP_VS);
        depthMapProgram = new ShaderProgram();
        depthMapProgram.attach(depthMap_source,GL_VERTEX_SHADER);
        depthMapProgram.compile();
        depthMapProgram.link();
        depthMapProgram.bind();
        depthMapProgram.createUniform("u_shadowMV");
        depthMapProgram.createUniform("u_shadowP");
        
        glEnable(GL_DEPTH_TEST);
    }
    
    
    public void render(ICamera camera, World world) {
        renderDepthMap(camera, world);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        world.lights().uploadUniforms(camera.view());
        materialProgram.bind();
        materialProgram.setUniform("u_projection",camera.projection());
        materialProgram.setUniform1i("u_shadowMap",1);
        glActiveTexture(GL_TEXTURE1);
        shadowMap.texture().bind();
        
        DirectionalLight light = world.lights().directionalLights().get(0);
        Vector3f lightDirection = new Vector3f(light.direction());
        tmpV3f.set(0,0,0).add(lightDirection.mul(5));
        lightView.identity().lookAt(tmpV3f.x,tmpV3f.y,tmpV3f.z,0,0,0,0,1,0);
    
    
        materialProgram.setUniform("u_light_projection",lightProjection);
        
        List<GameObject> objects = world.gameObjects();
        for (GameObject object : objects) {
            Matrix4f modelToWorld = object.transform.modelToWorldMatrix();
            tmpM4f.set(camera.view()).mul(modelToWorld);
            materialProgram.setUniform("u_modelView", tmpM4f);
            materialProgram.setUniform1i("u_material_index", object.material);
            tmpM4f.set(lightView).mul(modelToWorld);
            materialProgram.setUniform("u_light_modelView", tmpM4f);
            object.mesh.render();
        }
        materialProgram.unBind();
        shadowMap.texture().unbind();
    }
    
    // Temporary until I figure out where to put them
    
    private static final Matrix4f lightProjection = new Matrix4f().ortho(-20.0f, 20.0f, -20.0f, 20.0f, -1.0f, 20.0f);
    
    private static final Matrix4f lightView = new Matrix4f();
    
    private void renderDepthMap(ICamera camera, World world) {
        
        DirectionalLight light = world.lights().directionalLights().get(0);
        if (light == null) return;
        Vector3f lightDirection = new Vector3f(light.direction());
        tmpV3f.set(0,0,0).add(lightDirection.mul(5));
        lightView.identity().lookAt(tmpV3f.x,tmpV3f.y,tmpV3f.z,0,0,0,0,1,0);
        
        
        shadowMap.bind(); // binds the framebuffer
        glViewport(0, 0, ShadowMap.WIDTH, ShadowMap.HEIGHT);
        glClear(GL_DEPTH_BUFFER_BIT);
        depthMapProgram.bind();
        
        depthMapProgram.setUniform("u_shadowP",lightProjection);
        
        List<GameObject> objects = world.gameObjects();
        for (GameObject object : objects) {
            Matrix4f modelToWorld = object.transform.modelToWorldMatrix();
            tmpM4f.set(lightView).mul(modelToWorld);
            depthMapProgram.setUniform("u_shadowMV", tmpM4f);
            object.mesh.render();
        }
        
        Window window = Engine.get().window;
        glViewport(
                window.viewportX(),
                window.viewportY(),
                window.viewportW(),
                window.viewportH());
        shadowMap.unbind();
        depthMapProgram.unBind();
    }
    
    public void dispose() {
        if (shadowMap != null) shadowMap.dispose();
        if (materialBlock != null) materialBlock.free();
        if (materialProgram != null) materialProgram.delete();
        if (depthMapProgram != null) depthMapProgram.delete();
    }
}
