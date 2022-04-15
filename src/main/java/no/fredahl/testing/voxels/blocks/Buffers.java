package no.fredahl.testing.voxels.blocks;

import no.fredahl.engine.graphics.BufferObject;
import no.fredahl.engine.graphics.GLBindings;
import no.fredahl.engine.utility.Disposable;
import org.joml.Math;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_SHORT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

/**
 * @author Frederik Dahl
 * 19/02/2022
 */


public class Buffers implements Disposable {
    
    private static final GLBindings bindings = GLBindings.get();
    
    private final List<Chunk> chunks;
    private final FloatBuffers floatBuffers;
    private final BufferPool pool;
    
    public Buffers(int bufferCount) {
        chunks = new ArrayList<>();
        pool = new BufferPool(bufferCount);
        floatBuffers = new FloatBuffers();
    }
    
    public void removeChunk(Chunk chunk) {
        if (chunk == null) return;
        if (chunks.contains(chunk)) {
            if (chunk.bufferAssigned()) {
                pool.free(chunk.retrieveBlockBuffer());
            } chunks.remove(chunk);
        }
    }
    
    public void removeAllChunks() {
        for (Chunk chunk : chunks) {
            if (chunk.bufferAssigned())
                pool.free(chunk.retrieveBlockBuffer());
        } chunks.clear();
    }
    
    public void distributeBlockBuffers(List<Chunk> centerChunks, Vector3f cameraPosition) {
        boolean chunkAdded = false;
        for (Chunk chunk : centerChunks) {
            if (chunks.contains(chunk)) continue;
            chunks.add(chunk);
            chunkAdded = true;
        }
        if (!chunkAdded) return;
        if (chunks.size() > pool.capacity()) {
            chunks.sort(chunkComparator(
                    cameraPosition.x,
                    cameraPosition.y));
            while (chunks.size() > pool.capacity()) {
                Chunk chunkForRemoval = chunks.remove(0);
                if (chunkForRemoval.bufferAssigned()) {
                    pool.free(chunkForRemoval.retrieveBlockBuffer());
                }
            }
            for (Chunk chunk : chunks) {
                if (!chunk.bufferAssigned()) {
                    // should always be available here.
                    if (pool.available() > 0) {
                        chunk.assignBlockBuffer(pool.obtain());
                    }
                }
            }
        }
        else {
            for (Chunk chunk : chunks) {
                if (chunk.bufferAssigned()) continue;
                chunk.assignBlockBuffer(pool.obtain());
            }
        }
    }
    
    public FloatBuffers floatBuffers() {
        return floatBuffers;
    }
    
    @Override
    public void dispose() {
        removeAllChunks();
        pool.dispose();
        floatBuffers.dispose();
    }
    
    private static Comparator<Chunk> chunkComparator(final float x, final float y) {
        return (c1, c2) -> {
            float ds1 = c1.centerPosition().distanceSquared(x,y);
            float ds2 = c2.centerPosition().distanceSquared(x,y);
            return Float.compare(ds2,ds1);
        };
    }
    
    protected static final class BlockBuffer implements Disposable {
        
        
        private static final int FACE_STRIDE = 3 + 2; // pos | uv
        private final BufferObject ebo;
        private final BufferObject vbo;
        private int vao;
        private final int limit;
        private boolean initialized;
        
        
        public BlockBuffer() {
            limit = ((Map.CHUNK_SIZE / 2) * (Map.CHUNK_SIZE / 2)) * 5;
            ebo = new BufferObject(GL_ELEMENT_ARRAY_BUFFER,GL_STATIC_DRAW);
            vbo = new BufferObject(GL_ARRAY_BUFFER,GL_DYNAMIC_DRAW);
        }
        
        public void initialize() {
            if (!initialized) {
                vao = glGenVertexArrays();
                bindings.bindAttributeArray(vao);
                ebo.bind();
                ebo.bufferData(generateIndices(limit));
                vbo.bind();
                int bytes = (limit * 4 * FACE_STRIDE) * Float.BYTES;
                vbo.bufferData(bytes);
                glEnableVertexAttribArray(0);
                glVertexAttribPointer(0, 3, GL_FLOAT,false,FACE_STRIDE * Float.BYTES, 0);
                glEnableVertexAttribArray(1);
                glVertexAttribPointer(1, 2, GL_FLOAT,false,FACE_STRIDE * Float.BYTES, 3 * Float.BYTES);
                bindings.unbindAttributeArray();
                initialized = true;
            }
        }
        
        public void render(int faceCount) {
            bindings.bindAttributeArray(vao);
            faceCount = Math.min(faceCount,limit);
            glDrawElements(GL_TRIANGLES, faceCount * 6,GL_UNSIGNED_SHORT,0);
        }
        
        public void upload(FloatBuffer vertices) {
            vbo.bufferSubData(vertices,0);
        }
        
        private short[] generateIndices(int faces) {
            int len = faces * 6;
            final short[] indices = new short[len];
            short j = 0;
            for (int i = 0; i < len; i += 6, j += 4) {
                indices[i] = j;
                indices[i + 1] = (short)(j + 1);
                indices[i + 2] = (short)(j + 2);
                indices[i + 3] = (short)(j + 2);
                indices[i + 4] = (short)(j + 3);
                indices[i + 5] = j;
            } return indices;
        }
        
        @Override
        public void dispose() {
            if (initialized) {
                bindings.bindBufferObject(GL_ARRAY_BUFFER, 0);
                vbo.free();
                ebo.free();
                bindings.bindAttributeArray(0);
                glDeleteVertexArrays(vao);
                initialized = false;
            }
        }
    }
    
    protected static final class BufferPool implements Disposable {
        
        private BlockBuffer[] pool;
        private final List<BlockBuffer> all;
        private int available;
        
        
        public BufferPool(int capacity) {
            this.all = new ArrayList<>(capacity);
            this.pool = new BlockBuffer[capacity];
            for (int i = 0; i < pool.length; i++) {
                BlockBuffer buffer = new BlockBuffer();
                pool[i] = buffer;
                all.add(buffer);
            } available = capacity();
        }
        
        public BlockBuffer obtain() {
            BlockBuffer buffer;
            if (available > 0)
                buffer = pool[--available];
            else {
                buffer = new BlockBuffer();
                all.add(buffer);
            }
            buffer.initialize();
            return buffer;
        }
        
        public void free(BlockBuffer buffer) {
            if (available == capacity()) resize();
            pool[available++] = buffer;
        }
        
        private void resize() {
            BlockBuffer[] newArray = new BlockBuffer[capacity() * 2];
            if (available >= 0) System.arraycopy(pool, 0, newArray, 0, available);
            pool = newArray;
        }
        
        public int available() {
            return available;
        }
        
        public int capacity() {
            return pool.length;
        }
        
        @Override
        public void dispose() {
            for(BlockBuffer buffer : all) {
                buffer.dispose();
            }
        }
    }
    
    
    protected static final class FloatBuffers implements Disposable {
        
        private static final int FACE_STRIDE = 3 + 2; // pos | uv
        private final List<FloatBuffer> buffers;
        private final List<FloatBuffer> all;
        private final int bufferSize;
        private int count;
        
        public FloatBuffers() {
            bufferSize = ((Map.CHUNK_SIZE / 2) * (Map.CHUNK_SIZE / 2)) * 5 * 4 * FACE_STRIDE;
            buffers = new ArrayList<>();
            all = new ArrayList<>();
        }
        
        private void createNew() {
            FloatBuffer buffer = MemoryUtil.memAllocFloat(bufferSize);
            buffers.add(buffer);
            all.add(buffer);
            count++;
        }
        
        public synchronized FloatBuffer get() {
            if (buffers.isEmpty()) createNew();
            return buffers.get(0);
        }
        
        
        public synchronized void put(FloatBuffer buffer) {
            if (buffer.capacity() == bufferSize) {
                buffer.clear();
                buffers.add(buffer);
            }
        }
        
        public int numCreated() {
            return count;
        }
        
        @Override
        public void dispose() {
            for (FloatBuffer buffer : all)
                MemoryUtil.memFree(buffer);
        }
    }
}
