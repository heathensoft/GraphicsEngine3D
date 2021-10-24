package no.fredahl.engine.graphics;

import java.io.*;
import java.util.Set;

import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL32.GL_GEOMETRY_SHADER;

/**
 * @author Frederik Dahl
 * 17/10/2021
 */


public class ShaderSource {
    
    private int type;
    private String source;
    
    static public final Set<Integer> SUPPORTED = Set.of(
            GL_VERTEX_SHADER,
            GL_FRAGMENT_SHADER,
            GL_GEOMETRY_SHADER
    );
    
    public ShaderSource(int type, String path) throws Exception {
        this(type,new File(path));
    }
    
    public ShaderSource(int type, File file) throws Exception {
        validateType(type);
        validateFile(file);
        loadResource(file);
    }
    
    private void validateType(int type) throws Exception {
        if (SUPPORTED.contains(type)) this.type = type;
        else throw new Exception("Unsupported GL Shader");
    }
    
    private void validateFile(File file) throws Exception {
        if (file != null && file.exists() && file.isFile()) return;
        throw new Exception("Could not locate file");
    }
    
    private void loadResource(File file) throws Exception {
        StringBuilder source = new StringBuilder();
        InputStream in = new FileInputStream(file);
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line;
        while ((line = reader.readLine()) != null)
            source.append(line).append("\n");
        this.source = source.toString();
    }
    
    public String get() {
        return source;
    }
    
    public int type() {
        return type;
    }
}
