package no.fredahl.engine.graphics;

import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Creates TextureRegions from a .atlas file.
 * And stores them in a map with the region-name as key.
 * I am using gdx-texture-packer (I like it a lot)
 * https://github.com/crashinvaders/gdx-texture-packer-gui/releases
 * But you could also write a .atlas manually.
 * I am not using all the values in the .atlas.
 * The important values are the filename,
 * the x and y start of the region in pixels (y0 at top),
 * the regions width and height,
 * and the region name.
 * we skip the rest.
 *
 * the format:
 *
 * [file-name]
 * [region-name]
 * xy:[x],[y]
 * size:[w],[h]
 * [region-name]
 * xy:[x],[y]
 * size:[w],[h]
 * ...
 *
 *
 * @author Frederik Dahl
 * 11/12/2021
 */


public class TextureAtlas {
 
    private final Texture texture;
    private final String filename;
    private final List<String> regionNames;
    private final Map<String,TextureRegion> regions;
    
    public TextureAtlas(Texture texture, List<String> layout) throws Exception {
        this.regionNames = new ArrayList<>();
        this.regions = new HashMap<>();
        this.texture = texture;
        int p = 0;
        while (layout.get(p).isBlank()) p++;
        filename = layout.get(p++);
    
        List<String> keys = new ArrayList<>();
        List<Vector2i> xyList = new ArrayList<>();
        List<Vector2i> whList = new ArrayList<>();
    
        String[] tokens;
        String[] sub;
    
        for (int i = p; i < layout.size() ; i++, p++) {
            String line = layout.get(i);
            if (line.isBlank()) continue;
            tokens = line.split(":");
            if (tokens.length == 1) break;
        }
        
        try {
            for (int i = p; i < layout.size(); i++) {
                String line = layout.get(i).replace(" ", "");
                tokens = line.split(":");
                if (tokens[0].isBlank()) continue;
                if (tokens.length == 1)
                    keys.add(tokens[0]);
                switch (tokens[0]){
                    case "xy":
                        sub = tokens[1].split(",");
                        int x = Integer.parseInt(sub[0]);
                        int y = Integer.parseInt(sub[1]);
                        xyList.add(new Vector2i(x,y));
                        break;
                    case "size":
                        sub = tokens[1].split(",");
                        int w = Integer.parseInt(sub[0]);
                        int h = Integer.parseInt(sub[1]);
                        whList.add(new Vector2i(w,h));
                        break;
                }
            }
        } catch (IndexOutOfBoundsException e) {
            throw new Exception(e);
        }
        
        if (keys.size() != xyList.size() && keys.size() != whList.size())
            throw new Exception("Atlas formatting Error");
    
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            Vector2i xy = xyList.get(i);
            Vector2i wh = whList.get(i);
            TextureRegion region = new TextureRegion(
                    texture, xy.x, xy.y, wh.x, wh.y
            );
            regions.put(key,region);
            regionNames.add(key);
        }
    }
    
    
    public Map<String, TextureRegion> regions() {
        return regions;
    }
    
    public TextureRegion get(String region) {
        return regions.get(region);
    }
    
    public List<String> regionNames() {
        return regionNames;
    }
    
    public Texture texture() {
        return texture;
    }
    
    public String filename() {
        return filename;
    }
}
