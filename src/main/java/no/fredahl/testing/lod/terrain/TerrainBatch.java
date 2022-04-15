package no.fredahl.testing.lod.terrain;

import no.fredahl.engine.graphics.BufferObject;
import no.fredahl.engine.graphics.VAO;
import no.fredahl.engine.utility.Disposable;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL31.glDrawElementsInstanced;
import static org.lwjgl.opengl.GL33.glVertexAttribDivisor;

/**
 * @author Frederik Dahl
 * 04/04/2022
 */


public class TerrainBatch implements Disposable {
    
    public static final int LO_RES = 64;
    public static final int HI_RES = 128;
    public static final int BATCH_SIZE = 32;
    public static final int RESOLUTION_BREAKPOINT = 16;
    // size 128x128 vertex terrain grid
    private final VAO vao_hi_res;
    private final BufferObject idx_hi_res;
    private final BufferObject vtx_hi_res;
    private final BufferObject inst_hi_res;
    private final int idxCount_hi_res;
    // size 64x64 vertex terrain grid
    private final VAO vao_lo_res;
    private final BufferObject idx_lo_res;
    private final BufferObject vtx_lo_res;
    private final BufferObject inst_lo_res;
    private final int idxCount_lo_res;
    private final FloatBuffer instance_buffer;
    private int instance_count;
    private int drawCalls_frame;
    private int drawCalls_runtime;
    private boolean render_state;
    
    
    public TerrainBatch() {
        instance_buffer = MemoryUtil.memAllocFloat(4 * BATCH_SIZE);
        vao_hi_res = new VAO();
        vao_lo_res = new VAO();
        idx_hi_res = new BufferObject(GL_ELEMENT_ARRAY_BUFFER,GL_STATIC_DRAW);
        vtx_hi_res = new BufferObject(GL_ARRAY_BUFFER,GL_STATIC_DRAW);
        inst_hi_res = new BufferObject(GL_ARRAY_BUFFER,GL_DYNAMIC_DRAW);
        idx_lo_res = new BufferObject(GL_ELEMENT_ARRAY_BUFFER,GL_STATIC_DRAW);
        vtx_lo_res = new BufferObject(GL_ARRAY_BUFFER,GL_STATIC_DRAW);
        inst_lo_res = new BufferObject(GL_ARRAY_BUFFER,GL_DYNAMIC_DRAW);
        vao_hi_res.bind();
        inst_hi_res.bind();
        inst_hi_res.bufferData((long) 4 * BATCH_SIZE * Float.BYTES);
        idx_hi_res.bind();
        idxCount_hi_res = generateIndices(idx_hi_res,HI_RES);
        vtx_hi_res.bind();
        generateVertices(vtx_hi_res,HI_RES);
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 2*Float.BYTES, 0);
        glEnableVertexAttribArray(0);
        inst_hi_res.bind();
        glVertexAttribPointer(1, 4, GL_FLOAT, false, 4*Float.BYTES, 0);
        glVertexAttribDivisor(1, 1);
        glEnableVertexAttribArray(1);
        vao_lo_res.bind();
        inst_lo_res.bind();
        inst_lo_res.bufferData((long) 4 * BATCH_SIZE * Float.BYTES);
        idx_lo_res.bind();
        idxCount_lo_res = generateIndices(idx_lo_res,LO_RES);
        vtx_lo_res.bind();
        generateVertices(vtx_lo_res,LO_RES);
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 2*Float.BYTES, 0);
        glEnableVertexAttribArray(0);
        inst_lo_res.bind();
        glVertexAttribPointer(1, 4, GL_FLOAT, false, 4*Float.BYTES, 0);
        glVertexAttribDivisor(1, 1);
        glEnableVertexAttribArray(1);
        VAO.unbind();
    }
    
    public void begin() {
        if (render_state) return;
        drawCalls_frame = 0;
        render_state = true;
    }
    
    
    public void draw(float x, float y, float size, float tex) {
        if (render_state) {
            if (instance_count >= BATCH_SIZE) flush();
            instance_buffer.put(x).put(y).put(size).put(tex);
            instance_count++;
        }
    }
    
    public void end() {
        if (!render_state) return;
        if (instance_count > 0) flush();
        render_state = false;
    }
    
    private void flush() {
        // render low_res if more than N instances are drawn in total this frame
        boolean low_res = instance_count > RESOLUTION_BREAKPOINT || drawCalls_frame > 1;
        instance_buffer.flip();
        render(low_res);
        instance_buffer.clear();
        drawCalls_runtime++;
        drawCalls_frame++;
        instance_count = 0;
    }
    
    private void render(boolean low_res) {
        int indices = instance_count;
        if (low_res) {
            vao_lo_res.bind();
            inst_lo_res.bind();
            inst_lo_res.bufferSubData(instance_buffer,0);
            indices *= idxCount_lo_res;
        } else {
            vao_hi_res.bind();
            inst_hi_res.bind();
            inst_hi_res.bufferSubData(instance_buffer,0);
            indices *= idxCount_hi_res;
        }
        glDrawElementsInstanced(GL_TRIANGLE_STRIP,indices,GL_UNSIGNED_SHORT,0, instance_count);
    }
    
    private void generateVertices(BufferObject buffer, int res) {
        FloatBuffer positions = null;
        final float delta = 1f/(res-1);
        try { positions = MemoryUtil.memAllocFloat(res * res * 2);
            for (int r = 0; r < res; r++) {
                for (int c = 0; c < res; c++) {
                    positions.put(-0.5f + c * delta);
                    positions.put(-0.5f + r * delta);
                }
            } buffer.bufferData(positions.flip());
        }finally {
            if (positions != null)
                MemoryUtil.memFree(positions);
        }
    }
    
    public int generateIndices(BufferObject buffer, int res) {
        int stripsReq = res - 1;
        int degensReq = 2 * (stripsReq - 1);
        int verticesPerStrip = 2 * res;
        int numIndices =  (verticesPerStrip * stripsReq) + degensReq;
        ShortBuffer indices = null;
        try { indices = MemoryUtil.memAllocShort(numIndices);
            for (int r = 0; r < res - 1; r++) {
                if (r > 0) indices.put((short) (r * res));
                for (int c = 0; c < res; c++) {
                    indices.put((short) ((r * res) + c));
                    indices.put((short) (((r + 1) * res) + c));
                }if (r < res - 2) indices.put((short) (((r + 1) * res) + (res - 1)));
            } buffer.bufferData(indices.flip());
        }finally {
            if (indices != null)
                MemoryUtil.memFree(indices);
        } return numIndices;
    }
    
    public int drawCalls_total() {
        return drawCalls_runtime;
    }
    
    public int drawCalls_frame() {
        return drawCalls_frame;
    }
    
    @Override
    public void dispose() {
        if (instance_buffer != null)
            MemoryUtil.memFree(instance_buffer);
        VAO.unbind();
        vtx_hi_res.free();
        vtx_lo_res.free();
        idx_hi_res.free();
        idx_lo_res.free();
        inst_hi_res.free();
        inst_lo_res.free();
        vao_hi_res.delete();
        vao_lo_res.delete();
    }
}
