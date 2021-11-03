package no.fredahl.examples.cube;

import no.fredahl.engine.*;
import no.fredahl.engine.graphics.Texture;
import no.fredahl.engine.math.Camera;
import no.fredahl.engine.utility.IO;
import no.fredahl.engine.window.Options;
import no.fredahl.engine.window.Window;
import no.fredahl.engine.window.processors.Keyboard;
import no.fredahl.engine.window.processors.Mouse;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Frederik Dahl
 * 28/10/2021
 */


public class HelloCube implements Application {
    
    private FPSControl FPSControl;
    private Mouse mouse;
    private Camera camera;
    private Renderer renderer;
    private List<Cube> cubes;
    
    @Override
    public void start(Window window) throws Exception {
        
        Keyboard keyboard = new Keyboard(window);
        camera = new Camera(window.aspectRatio());
        camera.updateProjection();
        camera.updateWorldToView();
        FPSControl = new FPSControl(camera,keyboard);
        mouse = new Mouse(window);
        mouse.setListener(FPSControl);
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
        FPSControl.update(delta);
        mouse.collect(delta);
    }
    
    @Override
    public void update(float delta) {
        float r = delta * 10;
        for (Cube cube : cubes)
            cube.transform.rotate(0,r,0);
        
        
    }
    
    @Override
    public void resize(int viewportX, int viewportY, int viewportW, int viewportH) {
        camera.aspectRatio = (float) viewportW / viewportH;
        camera.updateProjection();
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
                return true;
            }
    
            @Override
            public String windowTitle() {
                return "Hello Cube";
            }
    
            @Override
            public boolean showTriangles() {
                return Options.super.showTriangles();
            }
            
    
            @Override
            public boolean windowedMode() {
                return true;
            }
    
            @Override
            public boolean verticalSynchronization() {
                return true;
            }
        });
    }
}
