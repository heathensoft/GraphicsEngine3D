package no.fredahl.example;

import no.fredahl.engine.Application;
import no.fredahl.engine.Engine;
import no.fredahl.engine.window.Options;
import no.fredahl.engine.window.Window;

/**
 * Testing out "RTS" -like camera control and unit-selection.
 * I really have XCOM type games in mind, but the idea is a "2D world" in the 3D space.
 *
 * @author Frederik Dahl
 * 05/11/2021
 */


public class RTS implements Application {
    
    private RTSControl control;
    private Renderer renderer;
    private World world;
    
    
    @Override
    public void start(Window window) throws Exception {
        renderer = new Renderer();
        world = new World();
        control = new RTSControl(world);
        for (int i = -20; i < 20; i++) {
            for (int j = -20; j < 20; j++) {
                if (j % 2 == 0) continue;
                if (i % 2 == 0) continue;
                world.createUnit(i,j);
            }
        }
    }
    
    @Override
    public void input(float delta) {
        control.processInput(delta);
    }
    
    @Override
    public void update(float delta) {
        world.update(delta);
    }
    
    @Override
    public void resize(Window window) {
        control.onWindowResize(window.aspectRatio());
    }
    
    @Override
    public void render(float alpha) {
        renderer.render(
                control.getCamera(),
                world.getUnits()
        );
    }
    
    @Override
    public void exit() {
        world.dispose();
        renderer.dispose();
    }
    
    public static void main(String[] args) {
        Engine.get().start(new RTS(), new Options() {
            final String title = "RTS Test";
            @Override
            public String title() {
                return title;
            }
        });
    }
}
