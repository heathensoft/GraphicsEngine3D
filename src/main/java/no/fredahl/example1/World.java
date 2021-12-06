package no.fredahl.example1;

import no.fredahl.engine.graphics.Texture;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Frederik Dahl
 * 04/11/2021
 */


public class World {
    
    private final List<Unit> units;
    private final Mesh unitMesh;
    
    public World() throws Exception{
        units = new ArrayList<>();
        Assets assets = Assets.get();
        Texture texture;
        try {
            texture = assets.texture(assets.unitTexturePath, Texture.Config.LINEAR_REPEAT_2D);
        } catch (RuntimeException e) {
            throw new Exception(e);
        }
        unitMesh = new Mesh(
                Cube.vertexPositions(),
                Cube.textureCoordinates(),
                Cube.indices(),texture);
    }
    
    public void update(float dt) {
    
    }
    
    public void createUnit(float x, float z) {
        Unit unit = new Unit(unitMesh);
        unit.setPosition(x,z);
        units.add(unit);
    }
    
    public List<Unit> getUnits() {
        return units;
    }
    
    public void dispose() {
        units.clear();
        Assets.get().dispose();
    }
}
