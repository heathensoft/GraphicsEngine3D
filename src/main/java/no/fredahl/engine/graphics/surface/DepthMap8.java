package no.fredahl.engine.graphics.surface;

import no.fredahl.engine.graphics.Image;
import no.fredahl.engine.graphics.Texture;
import org.joml.Math;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.GL_R8;
import static org.lwjgl.stb.STBImageWrite.stbi_write_png;

/**
 * 8-bit depth map
 *
 * @author Frederik Dahl
 * 31/03/2022
 */


public class DepthMap8 {
    
    private final int cols;
    private final int rows;
    private final byte[] map;
    
    public DepthMap8(HeightMap hm) {
        this.cols = hm.cols();
        this.rows = hm.cols();
        this.map = new byte[cols * rows];
        float[][] m = hm.map;
        float amp = hm.amplitude();
        float bsl = hm.baseline();
        int idx = 0;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                float n = ((m[r][c] - bsl) / amp + 1) / 2f;
                map[idx++] = (byte)(Math.round(n * 0xff) & 0xff);
            }
        }
    }
    
    public DepthMap8(Image img) {
        this.cols = img.width();
        this.rows = img.height();
        this.map = new byte[cols * rows];
        int c = img.channels();
        int avg = 0;
        int length = size();
        ByteBuffer data = img.get();
        switch (c) {
            case 1: case 2: case 3:
                for (int i = 0; i < length; i++) {
                    for (int j = 0; j < c; j++)
                        avg += (data.get(i*c+j) & 0xff);
                    avg = Math.round((float) avg/c);
                    map[i] = (byte) (avg & 0xff);
                    avg = 0;
                }
                break;
            case 4:
                for (int i = 0; i < length; i++) {
                    avg += (data.get(i*c) & 0xff);
                    avg += (data.get(i*c+1) & 0xff);
                    avg += (data.get(i*c+2) & 0xff);
                    avg = Math.round((float) avg/3);
                    map[i] = (byte) (avg & 0xff);
                    avg = 0;
                }
            break;
        }
    }
    
    public Texture toTexture(int GL_WRAP, int GL_FILTER) {
        ByteBuffer buffer = MemoryUtil.memAlloc(size());
        buffer.put(map).flip();
        Texture texture = new Texture(GL_TEXTURE_2D);
        texture.bind();
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_WRAP);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_WRAP);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_FILTER);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_FILTER);
        glPixelStorei(GL_UNPACK_ALIGNMENT,1);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_R8,cols,rows,0,GL_RED,GL_UNSIGNED_BYTE,buffer);
        MemoryUtil.memFree(buffer);
        return texture;
    }
    
    public void toPNG(String path) {
        ByteBuffer buffer = BufferUtils.createByteBuffer(size());
        buffer.put(map).flip();
        stbi_write_png(path, cols, rows,1,buffer, cols);
    }
    
    public byte[] data() {
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
