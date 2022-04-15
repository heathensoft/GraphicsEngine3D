package no.fredahl.engine.graphics.surface;

import no.fredahl.engine.graphics.Texture;
import org.joml.Math;
import org.lwjgl.system.MemoryUtil;

import java.nio.ShortBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.GL_R16;

/**
 * 16-bit depth map
 *
 * @author Frederik Dahl
 * 10/04/2022
 */


public class DepthMap16 {
    
    private final int cols;
    private final int rows;
    private final short[] map;
    
    public DepthMap16(HeightMap hm) {
        this.cols = hm.cols();
        this.rows = hm.cols();
        this.map = new short[cols * rows];
        float[][] m = hm.map;
        float amp = hm.amplitude();
        float bsl = hm.baseline();
        int idx = 0;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                float n = ((m[r][c] - bsl) / amp + 1) / 2f;
                map[idx++] = (short)(Math.round(n * 0xffff) & 0xffff);
            }
        }
    }
    
    public Texture toTexture(int GL_WRAP, int GL_FILTER) {
        ShortBuffer buffer = MemoryUtil.memAllocShort(cols*rows);
        buffer.put(map).flip();
        Texture texture = new Texture(GL_TEXTURE_2D);
        texture.bind();
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_WRAP);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_WRAP);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_FILTER);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_FILTER);
        glPixelStorei(GL_UNPACK_ALIGNMENT,1);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_R16,cols,rows,0,GL_RED,GL_UNSIGNED_SHORT,buffer);
        MemoryUtil.memFree(buffer);
        return texture;
    }
    
    public short[] data() {
        return map;
    }
    
    public int cols() {
        return cols;
    }
    
    public int rows() {
        return rows;
    }
    
    public int size() {
        return map.length;
    }
}
