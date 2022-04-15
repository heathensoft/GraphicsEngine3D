package no.fredahl.testing.voxels.blocks;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Frederik Dahl
 * 19/02/2022
 */


public class Test {
    
    public static void main(String[] args) throws Exception{
        
        Map map = new Map("FREDs MAP",4,4);
        
        map.placeBlock(4,0,0);
        map.placeBlock(4,32,32);
        map.placeBlock(4,40,40);
        map.placeBlock(4,3,4);
        
        BlockSerializer.serialize(map,"C:\\dev\\Engine\\screenshots");
    
        Path path = Paths.get("C:\\dev\\Engine\\screenshots\\FREDs MAP.bin");
        
        Map map2 = BlockSerializer.deserialize(path);
        
    }
}
