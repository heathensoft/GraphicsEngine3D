package no.fredahl.example2;

import no.fredahl.engine.graphics.ShaderProgram;
import no.fredahl.engine.graphics.lighting.Lights;
import no.fredahl.engine.graphics.lighting.Material;
import no.fredahl.engine.graphics.lighting.MaterialBlock;
import no.fredahl.engine.math.ICamera;
import no.fredahl.engine.utility.FileUtility;
import org.joml.Matrix4f;

import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;

/**
 * @author Frederik Dahl
 * 18/12/2021
 */


public class Renderer {
    
    private static final Matrix4f tmpM4f = new Matrix4f();
    
    private final ShaderProgram materialProgram;
    private final MaterialBlock materialBlock;
    
    public Renderer(World world) throws Exception {
        
        Lights lights = world.lights();
        
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
        fs_source = ShaderProgram.insert(lights.glslLighting(),"#LIGHTS",fs_source);
        fs_source = ShaderProgram.insert(lights.glslUniformBlock(),"#LIGHT_BLOCK",fs_source);
        fs_source = ShaderProgram.insert(materialBlock.glslMaterial(),"#MATERIALS",fs_source);
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
        materialProgram.createUniform("u_modelView");
        materialProgram.createUniform("u_projection");
        materialProgram.createUniform("u_material_index");
        
        glEnable(GL_DEPTH_TEST);
    }
    
    
    public void render(ICamera camera, World world) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        world.lights().uploadUniforms(camera.getWorldToViewMatrix());
        materialProgram.bind();
        materialProgram.setUniform("u_projection",camera.getProjectionMatrix());
        List<GameObject> objects = world.gameObjects();
        for (GameObject object : objects) {
            Matrix4f modelToWorld = object.transform.modelToWorldMatrix();
            tmpM4f.set(camera.getWorldToViewMatrix()).mul(modelToWorld);
            materialProgram.setUniform("u_modelView",tmpM4f);
            materialProgram.setUniform1i("u_material_index", object.material);
            object.mesh.render();
        } materialProgram.unBind();
    }
    
    public void dispose() {
        materialBlock.free();
        if (materialProgram != null) materialProgram.delete();
    }
}
