package no.fredahl.example1;

import no.fredahl.engine.Application;
import no.fredahl.engine.Engine;
import no.fredahl.engine.utility.HeightMap;
import no.fredahl.engine.utility.noise.FastNoiseLite;
import no.fredahl.engine.utility.noise.INoise;
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
    
    
    private INoise noise = new INoise() {
        FastNoiseLite noiseLite = new FastNoiseLite();
        @Override
        public float query(float x, float y) {
            return noiseLite.GetNoise(x,y);
        }
    };
    
    
    
    
    @Override
    public void start(Window window) throws Exception {
        
        //Image image = new Image(FileUtility.resource.toBuffer("example2/png/heightmap.png",1000),false);
        //float[][] heightmap = HeightMap.create(image);
        float[][] heightmap2 = HeightMap.create(noise,10,40,1f,4, 4);
        
        //float[] normals = HeightMap.calculateNormals(heightmap2);
        
        short[] indices = HeightMap.indices(heightmap2);
        
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
