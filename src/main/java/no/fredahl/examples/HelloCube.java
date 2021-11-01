package no.fredahl.examples;

import no.fredahl.engine.*;
import no.fredahl.engine.graphics.Texture;
import no.fredahl.engine.math.PerspectiveCamera;
import no.fredahl.engine.utility.IO;
import no.fredahl.engine.window.Options;
import no.fredahl.engine.window.Window;
import no.fredahl.engine.window.processors.Keyboard;
import no.fredahl.engine.window.processors.Mouse;
import no.fredahl.examples.cube.Cube;
import no.fredahl.examples.cube.Mesh;
import no.fredahl.examples.cube.Renderer;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

/**
 * @author Frederik Dahl
 * 28/10/2021
 */


public class HelloCube implements Application {
    
    
    private Mouse mouse;
    private Keyboard keyboard;
    private PerspectiveCamera camera;
    private Renderer renderer;
    private List<Cube> cubes;
    
    @Override
    public void start(Window window) throws Exception {
        mouse = new Mouse(window);
        keyboard = new Keyboard(window);
        camera = new PerspectiveCamera(window);
        camera.update();
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
    public void input(float delta) {
        keyboard.collect();
        mouse.collect(delta);
        if (keyboard.justPressed(GLFW_KEY_ESCAPE)) Engine.get().exit();
        if (keyboard.pressed(GLFW_KEY_D))
            camera.strafeLeft(0.03f);
        if (keyboard.pressed(GLFW_KEY_W))
            camera.forward(0.03f);
        if (keyboard.pressed(GLFW_KEY_S))
            camera.backward(0.03f);
        if (keyboard.pressed(GLFW_KEY_A))
            camera.strafeRight(0.03f);
    
        for (int buttons = 0; buttons < Mouse.NUM_BUTTONS; buttons++) {
            
                //System.out.println(buttons);
            //if (mouse.isDragging(buttons)) System.out.println(mouse.dragOriginScreenLeft);
        }
        
    }
    
    @Override
    public void update(float delta) {
    
        /*
        float r = delta * 10;
        for (Cube cube : cubes)
            cube.transform.rotate(0,r,0);
         */
        
        camera.update();
    
    
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
