package no.fredahl.example2;

import no.fredahl.engine.graphics.lighting.*;
import no.fredahl.engine.math.Transform;
import org.joml.Vector3f;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
        Mesh heightmapMesh = Assets.get().heightmapMesh(Assets.HEIGHTMAP_PNG,0.05f);
        Transform heightmapTransform = new Transform();
        heightmapTransform.setScale(20f);
        heightmapTransform.setPosition(0,0,0);
        GameObject heightmap = new GameObject(heightmapMesh,heightmapTransform,16);
        gameObjects.add(heightmap);
        
        
        lights = new Lights(1,5,1,0);
        DirectionalLight directionalLight = new DirectionalLight(new Vector3f(1,1,1),new Vector3f(1f,1f,0).normalize());
        directionalLight.setAmbient(0.5f);
        directionalLight.setDiffuse(0.6f);
        PointLight pointLight = new PointLight(new Vector3f(1,0,0),new Vector3f(0,6,0));
        pointLight.setDiffuse(0.4f);
        pointLight.setAmbient(0.0f);
        lights.addPointLight(pointLight);
        lights.addDirectionalLight(directionalLight);
        
        
        for (int i = -10; i < 10; i++) {
            for (int j = -10; j < 10; j++) {
                if (j % 2 == 0) continue;
                if (i % 2 == 0) continue;
                int m = (20 + i + j) % 26;
                createCube(i,2,j,m);
            }
        }
    }
    
    public void update(float dt) {
    
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
