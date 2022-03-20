package no.fredahl.example3.blocks;

import no.fredahl.engine.graphics.TextureRegion;
import org.joml.Vector4f;

/**
 *
 * BlockType will also contain physics parameters like mass and friction.
 * Toughness / health maybe etc.
 *
 * @author Frederik Dahl
 * 24/01/2022
 */


public class BlockType {
 
    private static final int REGION_ROWS = 6;
    private static final int REGION_COLS = 8;
    
    private final Vector4f[] uvCoordinates;
    private String name;
    private int id;
    
    
    public BlockType(String name, TextureRegion textureRegion) {
        textureRegion.subDivide(REGION_COLS,REGION_ROWS);
        uvCoordinates = new Vector4f[48];
        for (int i = 0; i < uvCoordinates.length; i++) {
            uvCoordinates[i] = new Vector4f();
            textureRegion.subRegionUVs(i,uvCoordinates[i]);
        }
        this.name = name;
    }
    
    public BlockType(TextureRegion textureRegion) {
        this("NO_NAME",textureRegion);
    }
    
    public BlockType() {
        uvCoordinates = new Vector4f[48];
        for (int i = 0; i < uvCoordinates.length; i++) {
            uvCoordinates[i] = new Vector4f(0,0,1,1);
        }
        this.name = "NO_NAME";
    }
    
    public String name() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public int id() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public Vector4f UVCoordinatesFront(int index) {
        return uvCoordinates[index];
    }
    
    public Vector4f UVCoordinatesSides() {
        return uvCoordinates[0];
    }
    
    public void setTextureRegion(TextureRegion textureRegion) {
        textureRegion.subDivide(REGION_COLS,REGION_ROWS);
        for (int i = 0; i < uvCoordinates.length; i++) {
            textureRegion.subRegionUVs(i,uvCoordinates[i]);
        }
    }
}
