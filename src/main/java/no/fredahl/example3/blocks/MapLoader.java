package no.fredahl.example3.blocks;


import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Frederik Dahl
 * 11/02/2022
 */


public class MapLoader {
    
    private String workingDirectory;
    private Set<String> binaries;
    
    
    public MapLoader(String workingDirectory) {
        this.workingDirectory = workingDirectory;
        revalidateDir();
    }
    
    private void revalidateDir() {
        binaries = new HashSet<>();
        File[] files = new File(workingDirectory).listFiles((dir, name) -> name.endsWith(".bin"));
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    binaries.add(file.getName().replace(".bin",""));
                }
            }
        }
    }
    
    public Map load(String name) {
        if (binaries().contains(name)) {
            Map map;
            Path path = Paths.get(workingDirectory,name + ".bin");
            try {
                map = BlockSerializer.deserialize(path);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return map;
        }
        return null;
    }
    
    
    public boolean overwrite(Map map) {
        if (map != null) {
            try {
                BlockSerializer.serialize(map,workingDirectory);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            binaries.add(map.id());
        }
        return true;
    }
    
    public boolean saveAsNew(Map map) {
        if (map != null) {
            String name = map.id();
            int i = 1;
            while (binaries.contains(name))
                name = name + "(" + (i++) + ")";
            map.setID(name);
            try {
                BlockSerializer.serialize(map,workingDirectory);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            binaries.add(map.id());
        }
        return true;
    }
    
    public Set<String> binaries() {
        return binaries;
    }
    
    public void setWorkingDirectory(String path) {
        this.workingDirectory = path;
        revalidateDir();
    }
    
    public String workingDirectory() {
        return workingDirectory;
    }
}
