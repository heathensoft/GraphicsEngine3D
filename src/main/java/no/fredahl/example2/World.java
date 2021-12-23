package no.fredahl.example2;

import no.fredahl.engine.graphics.lighting.*;
import no.fredahl.engine.math.Transform;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;

/**
 * @author Frederik Dahl
 * 19/12/2021
 */


public class World {
    
    private final Lights lights;
    private final List<GameObject> gameObjects;
    
    public World() throws Exception {
        
        gameObjects = new ArrayList<>();
        Mesh heightmapMesh = Assets.get().heightmapMesh(Assets.HEIGHTMAP_PNG,0.18f,1);
        
        float scale = 20f;
        
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                Transform heightmapTransform = new Transform();
                heightmapTransform.setScale(scale);
                heightmapTransform.setPosition(scale * i,-2,scale * j);
                GameObject heightmap = new GameObject(heightmapMesh,heightmapTransform,14);
                gameObjects.add(heightmap);
            }
        }
    
        Random rnd = new Random();
    
        
        for (int i = -10; i < 10; i++) {
            for (int j = -10; j < 10; j++) {
                if (j % 2 == 0) continue;
                if (i % 2 == 0) continue;
                int m = Math.abs(rnd.nextInt()) % 26;
                createCube(i,2,j,m);
            }
        }
        
        lights = new Lights(1,5,1,0);
        DirectionalLight directionalLight = new DirectionalLight(new Vector3f(1,1,1),new Vector3f(2f,1f,1f).normalize());
        directionalLight.setAmbient(0.7f).setDiffuse(0.8f);
        PointLight pointLight1 = new PointLight(new Vector3f(1f,0.4f,0.5f),new Vector3f(0,30,0));
        pointLight1.setAttenuation(Attenuation.ATT_3250).setDiffuse(0.9f).setAmbient(0.5f);
        PointLight pointLight2 = new PointLight(new Vector3f(0.4f,0.7f,0.7f),new Vector3f(20f,2f,20f));
        pointLight2.setAttenuation(new Attenuation(0.4f,0.0014f,0.0007f)).setDiffuse(0.8f).setAmbient(0.2f);
        PointLight pointLight3 = new PointLight().set(pointLight2).setPosition(20,2,-20);
        PointLight pointLight4 = new PointLight().set(pointLight2).setPosition(-20,1,-20);
        PointLight pointLight5 = new PointLight().set(pointLight2).setPosition(-20,2,20);
        SpotLight spotLight = new SpotLight(pointLight1,new Vector3f(0,1,0).normalize(),20f,30f);
        lights.addDirectionalLight(directionalLight);
        lights.addPointLight(pointLight2);
        //lights.addPointLight(pointLight3);
        //lights.addPointLight(pointLight4);
        //lights.addPointLight(pointLight5);
        //lights.addSpotLight(spotLight);
        
        
    }
    
    private static float timer = 0f;
    
    public void update(float dt) {
        
        /*
        timer += dt;
        if (timer >= 5f) {
            timer = 0f;
            for (GameObject object : gameObjects) {
                object.material = (object.material + 1) % 26;
            }
            System.out.println(gameObjects.get(0).material);
        }
        
         */
        
        
        
        
        
        
        
    }
    
    public void createCube(float x, float y, float z, int material) throws IOException {
        Mesh cubeMesh = Assets.get().objectMesh(Assets.CUBE_OBJ,GL_TRIANGLES);
        Transform cubeTransform = new Transform();
        cubeTransform.setScale(0.25f,0.25f,0.25f);
        cubeTransform.setPosition(x, y, z);
        GameObject cube = new GameObject(cubeMesh,cubeTransform,material);
        gameObjects.add(cube);
    }
    
    public List<GameObject> gameObjects() {
        return gameObjects;
    }
    
    public Lights lights() {
        return lights;
    }
    
    public void dispose() {
        lights.free();
        Assets.get().disposeAll();
    }
}