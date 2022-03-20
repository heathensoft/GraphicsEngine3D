package no.fredahl.example3;

import no.fredahl.engine.Application;
import no.fredahl.engine.Engine;
import no.fredahl.engine.graphics.Color;
import no.fredahl.engine.window.Options;
import no.fredahl.engine.window.Window;
import no.fredahl.engine.window.processors.Keyboard;
import no.fredahl.engine.window.processors.Mouse;
import org.joml.Math;
import org.joml.Vector3f;

/**
 * @author Frederik Dahl
 * 18/01/2022
 */


public class Main2 implements Application {
    
    private OrbitalCam camera;
    private Controls controls;
    private Voxels voxels;
    private Vector3f lightDirection;
    
    @Override
    public void start(Window window) throws Exception {
        lightDirection = new Vector3f(1,-4,2).normalize();
        voxels = new Voxels(768);
        camera = new OrbitalCam();
        controls = new Controls(
                new Mouse(window),
                new Keyboard(window),
                camera
        );
    
        float c;
    
        for (int i = 0; i < 100; i++) {
            for (int j = 0; j < 100; j++) {
                float f = (float) (Math.abs(Math.random()));
                c = Color.packed(0.8f,0.5f,f,1);
                voxels.addVoxel(new Voxel(i,0,j,c ));
            }
        }
        /*
        int i = 0;
        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 16; y++) {
                for (int z = 0; z < 16; z++) {
                    if (x == 0 && y == 0 && z == 0) continue;
                    render = i++ % 2 == 0;
                    float f = (float) (Math.abs(Math.random()));
                    c = Color.packed(0.8f,0.5f,f,1);
                    voxels.addVoxel(new Voxel(x,y,z,c ));
                }
            }
        }
        
         */
    }
    
    @Override
    public void input(float delta) {
        controls.processInput(delta);
    }
    
    @Override
    public void update(float delta) {
        System.out.println(voxels.getBatch().renderCalls());
    }
    
    @Override
    public void resize(Window window) {
        camera.setAspectRatio(window.aspectRatio());
        camera.updateProjection();
    }
    
    @Override
    public void render(float alpha) {
        voxels.render(camera,lightDirection);
    }
    
    @Override
    public void exit() {
        voxels.dispose();
    }
    
    public static void main(String[] args) {
        Engine.get().start(new Main2(), new Options() {
            @Override
            public String title() {
                return "voxel test";
            }
    
            @Override
            public boolean showTriangles() {
                return false;
            }
    
            @Override
            public boolean cullFace() {
                return true;
            }
        });
    }
}
