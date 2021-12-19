package no.fredahl.example2;

import no.fredahl.engine.Application;
import no.fredahl.engine.Engine;
import no.fredahl.engine.window.Options;
import no.fredahl.engine.window.Window;
import no.fredahl.engine.window.processors.Keyboard;
import no.fredahl.engine.window.processors.Mouse;

/**
 * @author Frederik Dahl
 * 19/12/2021
 */


public class Main implements Application {
    
    private OrbitalCamera camera;
    private Renderer renderer;
    private Controls controls;
    private World world;
    
    @Override
    public void start(Window window) throws Exception {
        world = new World();
        renderer = new Renderer(world);
        camera = new OrbitalCamera();
        Mouse mouse = new Mouse(window);
        Keyboard keyboard = new Keyboard(window);
        controls = new Controls(mouse,keyboard,camera,world);
    }
    
    @Override
    public void input(float delta) {
        controls.processInput(delta);
    }
    
    @Override
    public void update(float delta) {
        world.update(delta);
    }
    
    @Override
    public void resize(Window window) {
        camera.aspectRatio = window.aspectRatio();
        camera.updateProjection();
    }
    
    @Override
    public void render(float alpha) {
        renderer.render(camera,world);
    }
    
    @Override
    public void exit() {
        world.dispose();
        renderer.dispose();
    }
    
    public static void main(String[] args) {
        Engine.get().start(new Main(), new Options() {
    
            @Override
            public String title() {
                return "Example 2";
            }
    
            @Override
            public boolean cullFace() {
                return true;
            }
        });
    }
}
