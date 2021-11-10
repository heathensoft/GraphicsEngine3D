package no.fredahl.example;

import no.fredahl.engine.graphics.Texture;
import no.fredahl.engine.utility.IO;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Frederik Dahl
 * 05/11/2021
 */


public class Assets {
    
    
    private static Assets instance;
    
    private final HashMap<String, Texture> TEXTURES = new HashMap<>();
    
    public final String unitVertexShader = IO.projectPath("vertex.glsl","res","rts","shaders");
    public final String unitFragmentShader = IO.projectPath("fragment.glsl","res","rts","shaders");
    public final String unitTexture = IO.projectPath("cubeTest.png","res","rts","png");
    
    private Assets() {}
    
    public static Assets get() {
        return instance == null ? new Assets() : instance;
    }
    
    public Texture texture(String key, Texture.Config config){
        if (TEXTURES.containsKey(key))
            return TEXTURES.get(key);
        Texture texture = new Texture(key,config);
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
