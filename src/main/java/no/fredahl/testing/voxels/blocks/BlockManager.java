package no.fredahl.testing.voxels.blocks;

import no.fredahl.engine.utility.Disposable;

/**
 * @author Frederik Dahl
 * 11/02/2022
 */


public class BlockManager implements Disposable {
 
    private MapLoader loader;
    private Buffers buffers;
    
    
    public Buffers buffers() {
        return buffers;
    }
    
    @Override
    public void dispose() {
        buffers.dispose();
    }
}
