package no.fredahl.testing.lod;

import no.fredahl.engine.Application;
import no.fredahl.engine.Engine;
import no.fredahl.engine.window.Options;
import no.fredahl.engine.window.Window;
import no.fredahl.testing.lod.terrain.Terrain;

/**
 * @author Frederik Dahl
 * 28/03/2022
 */


public class Main implements Application {
    
    private Controls controls;
    private Terrain terrain;
    
    
    @Override
    public void start(Window window) throws Exception {
        terrain = new Terrain();
        controls = new Controls(window);
        controls.getCamera().setFarPlane(1500);
        controls.getCamera().setNearPlane(5);
        controls.getCamera().updateProjection();
    }
    
    @Override
    public void input(float delta) {
        controls.processInput(delta);
    }
    
    @Override
    public void update(float delta) {
        terrain.update(delta);
    }
    
    @Override
    public void resize(Window window) {
        controls.resize(window);
    }
    
    @Override
    public void render(float alpha) {
        terrain.render(controls.getCamera());
    }
    
    @Override
    public void exit() {
        terrain.dispose();
    }
    
    public static void main(String[] args) throws Exception{
        
        Engine.get().start(new Main(), new Options() {
            
            final String winTitle = "Level of Detail";
    
            @Override
            public boolean verticalSynchronization() {
                return true;
            }
    
            @Override
            public boolean windowedMode() {
                return true;
            }
    
            @Override
            public String title() {
                return winTitle;
            }
    
            @Override
            public boolean cullFace() {
                return true;
            }
    
            @Override
            public boolean showTriangles() {
                return false;
            }
        });
    }
}
