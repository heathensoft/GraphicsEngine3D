package no.fredahl.examples.cube;

import no.fredahl.engine.Application;
import no.fredahl.engine.Camera;
import no.fredahl.engine.Engine;
import no.fredahl.engine.window.Options;
import no.fredahl.engine.window.Window;
import no.fredahl.engine.window.processors.Keyboard;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Frederik Dahl
 * 28/10/2021
 */


public class HelloCube implements Application {
    
    private Camera camera;
    private Keyboard keyboard;
    private Renderer renderer;
    private List<Entity> entities;
    
    @Override
    public void start(Window window) throws Exception {
        
        keyboard = new Keyboard(window.keyPressEvents(),window.charPressEvents());
        camera = new Camera(window);
        renderer = new Renderer();
        entities = new ArrayList<>();
        
        float[] positions = new float[] {
                // VO
                -0.5f,  0.5f,  0.5f,
                // V1
                -0.5f, -0.5f,  0.5f,
                // V2
                0.5f, -0.5f,  0.5f,
                // V3
                0.5f,  0.5f,  0.5f,
                // V4
                -0.5f,  0.5f, -0.5f,
                // V5
                0.5f,  0.5f, -0.5f,
                // V6
                -0.5f, -0.5f, -0.5f,
                // V7
                0.5f, -0.5f, -0.5f,
        };
    
        float[] colours = new float[]{
                0.5f, 0.0f, 0.0f,
                0.0f, 0.5f, 0.0f,
                0.0f, 0.0f, 0.5f,
                0.0f, 0.5f, 0.5f,
                0.5f, 0.0f, 0.0f,
                0.0f, 0.5f, 0.0f,
                0.0f, 0.0f, 0.5f,
                0.0f, 0.5f, 0.5f,
        };
    
        short[] indices = new short[] {
                // Front face
                0, 1, 3, 3, 1, 2,
                // Top Face
                4, 0, 3, 5, 4, 3,
                // Right face
                3, 2, 7, 5, 3, 7,
                // Left face
                6, 1, 0, 6, 0, 4,
                // Bottom face
                2, 1, 6, 2, 6, 7,
                // Back face
                7, 6, 4, 7, 4, 5,
        };
    
        Mesh mesh = new Mesh(positions,colours,indices);
        
        Entity cube1 = new Entity(mesh);
        cube1.transform.setPosition(0,0,-2);
        //cube1.transform.rotate(45f,35.264f,0);
        entities.add(cube1);
    
        Entity cube2 = new Entity(mesh);
        cube2.transform.setPosition(2,0,-4);
        entities.add(cube2);
    
        Entity cube3 = new Entity(mesh);
        cube3.transform.setPosition(-2,0,-4);
        entities.add(cube3);
        
    }
    
    @Override
    public void input() {
        keyboard.collect();
        
    }
    
    @Override
    public void update(float delta) {
        float rot = delta * 10;
        for (Entity cube : entities) {
            cube.transform.rotate(rot,rot,rot);
        }
        
    }
    
    @Override
    public void render(float alpha) {
        renderer.render(entities,camera);
    }
    
    @Override
    public void exit() {
        renderer.free();
        for (Entity entity : entities)
            entity.mesh.free();
    }
    
    public static void main(String[] args) {
    
        Engine.get().start(new HelloCube(), new Options() {
            @Override
            public String windowTitle() {
                return "Hello Cube";
            }
            
        });
    }
}
