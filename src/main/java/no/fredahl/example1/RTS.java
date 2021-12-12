package no.fredahl.example1;

import no.fredahl.engine.Application;
import no.fredahl.engine.Engine;
import no.fredahl.engine.graphics.Image;
import no.fredahl.engine.graphics.Texture;
import no.fredahl.engine.graphics.TextureAtlas;
import no.fredahl.engine.graphics.TextureRegion;
import no.fredahl.engine.utility.FileUtility;
import no.fredahl.engine.utility.noise.FastNoiseLite;
import no.fredahl.engine.utility.noise.NoiseGenerator;
import no.fredahl.engine.window.Options;
import no.fredahl.engine.window.Window;
import org.joml.Vector4f;

import java.util.List;

import static org.lwjgl.opengl.GL11.*;

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
    
    
    private NoiseGenerator noise = new NoiseGenerator() {
        FastNoiseLite noiseLite = new FastNoiseLite();
        @Override
        public float query(float x, float y) {
            return noiseLite.GetNoise(x,y);
        }
    };
    
    
    
    
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
    
    
    public static void main(String[] args) throws Exception{
    
        
        
        
        Engine.get().start(new RTS(), new Options() {
            final String title = "RTS Test";
            @Override
            public String title() {
                return title;
            }
        });
        
    }
}
