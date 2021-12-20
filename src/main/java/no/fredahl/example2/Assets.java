package no.fredahl.example2;

import no.fredahl.engine.graphics.Heightmap;
import no.fredahl.engine.graphics.Image;
import no.fredahl.engine.graphics.OBJFormatter;
import no.fredahl.engine.utility.FileUtility;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Frederik Dahl
 * 18/12/2021
 */


public class Assets {
    
    
    private static Assets instance;
    private static final FileUtility.ResourceUtility resource = FileUtility.resource;
    
    public static final String CUBE_OBJ = "example2/obj/cube.obj";
    public static final String MATERIAL_VS = "example2/shaders/main/material_vert.glsl";
    public static final String MATERIAL_FS = "example2/shaders/main/material_frag.glsl";
    //public static final String MATERIAL_VS = "example2/shaders/main/mat_vert.glsl";
    //public static final String MATERIAL_FS = "example2/shaders/main/mat_frag.glsl";
    public static final String HEIGHTMAP_PNG = "example2/png/heightmap.png";
    
    private final Map<String,Mesh> GEOMETRY = new HashMap<>();
    
    private Assets() {}
    
    public static Assets get() {
        return instance == null ? new Assets() : instance;
    }
    
    public Mesh heightmapMesh(String path, float amplitude, int smoothen) throws IOException {
        Mesh mesh = GEOMETRY.get(path);
        if (mesh != null) return mesh;
        Image img = new Image(resource.toBuffer(path,1024),false);
        Heightmap heightmap = new Heightmap(img,amplitude);
        for (int i = 0; i < smoothen; i++) {
            heightmap.smoothen(true);
        }
        mesh = new Mesh(heightmap);
        GEOMETRY.put(path,mesh);
        return mesh;
    }
    
    public Mesh objectMesh(String path, int drawMode) throws IOException {
        Mesh mesh = GEOMETRY.get(path);
        if (mesh != null) return mesh;
        mesh = new Mesh(OBJFormatter.process(resource.asLines(path)),drawMode);
        GEOMETRY.put(path,mesh);
        return mesh;
    }
    
    public void disposeMesh(String path) {
        Mesh mesh = GEOMETRY.get(path);
        if (mesh == null) {
            System.out.println("Assets: unable to dispose: " + path);
            return;
        } mesh.free();
    }
    
    public void disposeAll() {
        for (Map.Entry<String,Mesh> entry : GEOMETRY.entrySet()){
            entry.getValue().free();
        }
    }
}
