package no.fredahl.examples.cube;

import no.fredahl.engine.Application;
import no.fredahl.engine.Engine;
import no.fredahl.engine.window.Options;
import no.fredahl.engine.window.Window;

/**
 * @author Frederik Dahl
 * 26/10/2021
 */


public class HelloCube implements Application {
    
    
    
    @Override
    public void start(Window window) throws Exception {
    
    }
    
    @Override
    public void update(float delta) {
    
    }
    
    @Override
    public void render(float alpha) {
    
    }
    
    @Override
    public void exit() {
    
    }
    
    public static void main(String[] args) {
        Engine.get().start(new HelloCube(), new Options() {
            @Override
            public String windowTitle() {
                return "Cube";
            }
        });
    }
}
