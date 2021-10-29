package no.fredahl.examples;

import no.fredahl.engine.Application;
import no.fredahl.engine.Camera;
import no.fredahl.engine.Engine;
import no.fredahl.engine.graphics.Texture;
import no.fredahl.engine.utility.IO;
import no.fredahl.engine.window.Options;
import no.fredahl.engine.window.Window;
import no.fredahl.engine.window.processors.Keyboard;
import no.fredahl.examples.cube.Cube;
import no.fredahl.examples.cube.Mesh;
import no.fredahl.examples.cube.Renderer;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

/**
 * @author Frederik Dahl
 * 28/10/2021
 */


public class HelloCube implements Application {
    
    private Camera camera;
    private Camera camera_1;
    private Camera camera_2;
    private Renderer renderer;
    private Keyboard keyboard;
    private List<Cube> cubes;
    
    @Override
    public void start(Window window) throws Exception {
        
        keyboard = new Keyboard(window.keyPressEvents(),window.charPressEvents());
        camera_1 = new Camera(window);
        camera_2 = new Camera(
                window,
                new Vector3f(0,1.7f,1),
                new Vector3f(25,0,0),
                0.01f,
                1000f,
                (float)Math.toRadians(60.0f));
        camera = camera_2;
        renderer = new Renderer();
        cubes = new ArrayList<>();
    
        String texturePath = IO.projectPath("cubeTest.png","res","cube","png");
        Texture texture = new Texture(texturePath, Texture.Config.NEAREST_REPEAT);
        
        Mesh mesh = new Mesh(
                Cube.vertexPositions(),
                Cube.textureCoordinates(),
                Cube.indices(),texture);
        
        Cube cube1 = new Cube(mesh);
        cube1.transform.setPosition(0,0,-2);
        cube1.transform.rotate(45f,35.264f,0);
        cubes.add(cube1);
    
        Cube cube2 = new Cube(mesh);
        cube2.transform.setPosition(2,0,-4);
        cubes.add(cube2);
    
        Cube cube3 = new Cube(mesh);
        cube3.transform.setPosition(-2,0,-4);
        cubes.add(cube3);
    }
    
    @Override
    public void input() {
        
        keyboard.collect();
        if (keyboard.pressed(GLFW_KEY_ESCAPE)) Engine.get().exit();
        if (keyboard.justPressed(GLFW_KEY_C,GLFW_KEY_LEFT_CONTROL)) {
            if (camera == camera_1)
                camera = camera_2;
            else camera = camera_1;
        }
    }
    
    @Override
    public void update(float delta) {
        
        float r = delta * 20;
        for (Cube cube : cubes)
            cube.transform.rotate(0,r,0);
    }
    
    @Override
    public void render(float alpha) {
        renderer.render(cubes, camera);
    }
    
    @Override
    public void exit() {
        renderer.free();
        for (Cube cube : cubes)
            cube.mesh.free();
    }
    
    public static void main(String[] args) {
        
        Engine.get().start(new HelloCube(), new Options() {
    
            @Override
            public boolean antialiasing() {
                return false;
            }
    
            @Override
            public String windowTitle() {
                return "Hello Cube";
            }
    
            @Override
            public boolean showTriangles() {
                return false;
            }
        });
    }
}
