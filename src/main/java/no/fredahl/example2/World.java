package no.fredahl.example2;

import no.fredahl.example2.lighting.*;
import no.fredahl.engine.math.Transform;
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
        Mesh heightmapMesh = Assets.get().heightmapMesh(Assets.HEIGHTMAP_PNG,0.028f,1);
        
        float scale = 20f;
        
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                Transform heightmapTransform = new Transform();
                heightmapTransform.setScale(scale);
                heightmapTransform.setPosition(scale * i,-4,scale * j);
                GameObject heightmap = new GameObject(heightmapMesh,heightmapTransform,14);
                gameObjects.add(heightmap);
            }
        }
    
        Random rnd = new Random();
    
        
        for (int i = -10; i < 10; i++) {
            for (int j = -10; j < 10; j++) {
                for (int k = 0; k < 3; k++) {
                    if (j % 2 == 0) continue;
                    if (i % 2 == 0) continue;
                    if (k % 2 == 0) continue;
                    int m = (Math.abs(rnd.nextInt()) % 25) + 1;
                    createCube(i,k,j,m);
                }
                
            }
        }
        
        lights = new Lights(1,5,1,0);
        DirectionalLight directionalLight = new DirectionalLight(new Vector3f(1,1,1),new Vector3f(-3f,-7f,-1f).normalize());
        directionalLight.setAmbient(0.3f).setDiffuse(0.3f);
        PointLight pointLight1 = new PointLight(new Vector3f(0.6f,0.3f,0.5f),new Vector3f(0,7,0));
        pointLight1.setAttenuation(Attenuation.ATT_160).setDiffuse(0.8f).setAmbient(0.5f);
        PointLight pointLight2 = new PointLight(new Vector3f(0.7f,0.0f,0.0f),new Vector3f(20f,2f,20f));
        pointLight2.setAttenuation(new Attenuation(0.3f,0.0014f,0.0007f)).setDiffuse(0.8f).setAmbient(0.2f);
        //PointLight pointLight3 = new PointLight();
        //pointLight3.setComponents(pointLight2).setPosition(20,2,-20);
        //PointLight pointLight4 = new PointLight().set(pointLight2).setPosition(-20,2,-20);
        //PointLight pointLight5 = new PointLight().set(pointLight2).setPosition(-20,2,20);
        SpotLight spotLight = new SpotLight(pointLight1,new Vector3f(0,-1,0.0001f).normalize(),90f,90f);
        lights.addDirectionalLight(directionalLight);
        //lights.addPointLight(pointLight2);
        lights.addPointLight(pointLight2);
        //lights.addPointLight(pointLight4);
        //lights.addPointLight(pointLight5);
        lights.addSpotLight(spotLight);
        createCube(0,7,0,0);
        
        
    }
    
    public void update(float dt) {
        
    }
    
    public void createCube(float x, float y, float z, int material) throws IOException {
        Mesh cubeMesh = Assets.get().objectMesh(Assets.CUBE_OBJ,GL_TRIANGLES);
        Transform cubeTransform = new Transform();
        cubeTransform.setScale(0.5f,0.5f,0.5f);
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
