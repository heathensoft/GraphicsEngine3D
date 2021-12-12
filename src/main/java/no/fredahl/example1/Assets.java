package no.fredahl.example1;

import no.fredahl.engine.graphics.Image;
import no.fredahl.engine.utility.FileUtility;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Frederik Dahl
 * 05/11/2021
 */


public class Assets {
    
    
    private static Assets instance;
    
    private final HashMap<String, Texture> TEXTURES = new HashMap<>();
    
    public final String unitVertexShaderPath = "example1/shaders/vertex.glsl";
    public final String unitFragmentShaderPath = "example1/shaders/fragment.glsl";
    public final String unitTexturePath = "example1/png/cubeTest.png";
    
    private Assets() {}
    
    public static Assets get() {
        return instance == null ? new Assets() : instance;
    }
    
    public Texture texture(String key, Texture.Config config) throws IOException {
        if (TEXTURES.containsKey(key))
            return TEXTURES.get(key);
        Image image = FileUtility.resource.image(key);
        Texture texture = new Texture(image,config);
        TEXTURES.put(key,texture);
        return texture;
    }
    
    public void disposeTexture(String key) {
        if (TEXTURES.containsKey(key)) {
            Texture entry = TEXTURES.remove(key);
            entry.unbind();
            entry.free();
        }
    }
    
    public void dispose() {
        for (Map.Entry<String,Texture> entry : TEXTURES.entrySet()) {
            entry.getValue().unbind();
            entry.getValue().free();
        }
    }
    
}
