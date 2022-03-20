package no.fredahl.example3.blocks;

import no.fredahl.engine.Engine;
import no.fredahl.example3.blocks.Buffers.BlockBuffer;
import no.fredahl.engine.math.MathLib;
import org.joml.Vector2f;

import java.nio.FloatBuffer;

/**
 * @author Frederik Dahl
 * 18/02/2022
 */


public class Chunk {
 
    private final static int UPDATE_MASK = 1;
    private final static int UPDATING_MASK = 2;
    private final static int PRE_BUFFER_VERTICES = 4;
    private final static int PRE_BUFFERING_VERTICES = 8;
    private final static int RE_UPLOAD_VERTICES = 16;
    private final static int RE_UPLOADING_VERTICES = 32;
    private final static int VERTEX_DATA_UPLOADED = 64;
    
    
    private final Map map;
    private BlockBuffer blockBuffer;
    private FloatBuffer floatBuffer;
    
    private short blockCount;
    private short faceCount;
    private short facesToRender;
    
    private final int x0;
    private final int y0;
    private int state;
    
    // Used on deserialization
    protected Chunk(Map map, int x, int y, short blockCount) {
        if (blockCount > 0) set(UPDATE_MASK);
        this.blockCount = blockCount;
        this.map = map;
        this.x0 = x;
        this.y0 = y;
    }
    
    public Chunk(Map map, int x, int y) {
        this(map,x,y,(short)0);
    }
    
    
    public void update(boolean concurrent) {
        
        if (concurrent) {
            if (check(UPDATE_MASK)) {
                if (!check(UPDATING_MASK)) {
                    updateMask(true);
                }
            }
            else if (check(PRE_BUFFER_VERTICES)) {
                if (!check(PRE_BUFFERING_VERTICES)) {
                    preBuffer(true);
                }
            }
            else if (check(RE_UPLOAD_VERTICES)) {
                if (!check(RE_UPLOADING_VERTICES)) {
                    reUpload();
                }
            }
        } else {
            
            if (runningConcurrently()) return;
            if (check(UPDATE_MASK)) updateMask(false);
            if (check(PRE_BUFFER_VERTICES)) preBuffer(false);
            if (check(RE_UPLOAD_VERTICES)) reUpload();
            
        }
    }
    
    protected void assignBlockBuffer(BlockBuffer blockBuffer) {
        if (bufferAssigned()) return;
        this.blockBuffer = blockBuffer;
        if (blockCount > 0) set(RE_UPLOAD_VERTICES);
    }
    
    protected BlockBuffer retrieveBlockBuffer() {
        BlockBuffer blockBuffer = this.blockBuffer;
        this.blockBuffer = null;
        remove(VERTEX_DATA_UPLOADED);
        return blockBuffer;
    }
    
    protected void blockAltered() {
        set(UPDATE_MASK);
    }
    
    protected void blockInserted() {
        blockAltered();
        blockCount++;
    }
    
    protected void blockRemoved() {
        blockAltered();
        blockCount--;
    }
    
    protected int x0() {
        return x0;
    }
    
    protected int y0() {
        return y0;
    }
    
    protected short blockCount() {
        return blockCount;
    }
    
    protected boolean noBlocks() {
        return blockCount == 0;
    }
    
    protected Vector2f centerPosition() {
        // Used in sorting chunks by distance to camera xy
        Vector2f center = MathLib.vec2();
        return center.set(
                x0 + (Map.CHUNK_SIZE / 2f),
                y0 + (Map.CHUNK_SIZE / 2f));
    }
    
    private void updateMask(boolean concurrent) {
        
        if (concurrent) {
            remove(UPDATE_MASK);
            set(UPDATING_MASK);
            Engine.get().execute(() -> updateMask(false));
        }
        else {
            if (!check(UPDATING_MASK)) {
                remove(UPDATE_MASK);
                set(UPDATING_MASK);
            }
            
            // Update mask
            
            
            remove(UPDATING_MASK);
            set(PRE_BUFFER_VERTICES);
        }
        
    }
    
    private void preBuffer(boolean concurrent) {
        
        if (concurrent) {
            remove(PRE_BUFFER_VERTICES);
            set(PRE_BUFFERING_VERTICES);
            Engine.get().execute(() -> preBuffer(false));
        }
        else {
            if (!check(PRE_BUFFERING_VERTICES)) {
                remove(PRE_BUFFER_VERTICES);
                set(PRE_BUFFERING_VERTICES);
            }
    
            if (floatBufferAssigned())
                floatBuffer.clear();
            else fetchFloatBuffer();
                
                /*
                FACE VERTICES
        
                V1 - TOP_LEFT  - U, V
                V2 - BOT_LEFT  - U, V2
                V3 - BOT_RIGHT - U2,V2
                V4 - TOP_RIGHT - U2, V
                */
    
            floatBuffer.flip();
            remove(PRE_BUFFERING_VERTICES);
            set(RE_UPLOAD_VERTICES);
        }
    
    }
    
    private void reUpload() {
        remove(RE_UPLOAD_VERTICES);
        set(RE_UPLOADING_VERTICES);
        blockBuffer.upload(floatBuffer);
        // Returns the float-buffer to pool
        returnFloatBuffer();
        set(VERTEX_DATA_UPLOADED);
    }
    
    private void fetchFloatBuffer() {
        Buffers.FloatBuffers pool = map.blockManager().buffers().floatBuffers();
        floatBuffer = pool.get();
    }
    
    private void returnFloatBuffer() {
        Buffers.FloatBuffers pool = map.blockManager().buffers().floatBuffers();
        pool.put(floatBuffer); // clears the buffer
        floatBuffer = null;
    }
    
    private boolean floatBufferAssigned() {
        return floatBuffer != null;
    }
    
    protected boolean runningConcurrently() {
        return check(UPDATING_MASK) || check(PRE_BUFFERING_VERTICES);
    }
    
    protected boolean bufferAssigned() {
        return blockBuffer != null;
    }
    
    private void set(int flag) {
        state |= flag;
    }
    
    private void remove(int flag) {
        state &= ~flag;
    }
    
    private boolean check(int flag) {
        return (state & flag) == flag;
    }
}
